package com.epicode.taskmanager.persistence;

import com.epicode.taskmanager.domain.Priority;
import com.epicode.taskmanager.domain.Task;
import com.epicode.taskmanager.domain.TaskBuilder;
import com.epicode.taskmanager.domain.TaskComponent;
import com.epicode.taskmanager.domain.TaskGroup;
import com.epicode.taskmanager.security.InputSanitizer;
import com.epicode.taskmanager.security.exception.PersistenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public final class XmlTaskRepository implements TaskRepository {
    private static final int MAX_ID_LENGTH = 80;
    private static final int MAX_TITLE_LENGTH = 80;
    private static final int MAX_DESCRIPTION_LENGTH = 300;
    private static final String GROUP_TAG = "taskGroup";
    private static final String TASK_TAG = "task";

    @Override
    public void save(TaskGroup root, Path filePath) {
        Objects.requireNonNull(root, "root cannot be null");
        Path safePath = normalize(filePath);

        try {
            Path parent = safePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Document document = newDocumentBuilder().newDocument();
            document.appendChild(toElement(document, root));

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            try (OutputStream outputStream = Files.newOutputStream(safePath)) {
                transformer.transform(new DOMSource(document), new StreamResult(outputStream));
            }
        } catch (IOException | ParserConfigurationException | TransformerException exception) {
            throw new PersistenceException("Unable to save tasks.", exception);
        }
    }

    @Override
    public TaskGroup load(Path filePath) {
        Path safePath = normalize(filePath);
        if (!Files.isRegularFile(safePath)) {
            throw new PersistenceException("Task file does not exist.");
        }

        try (InputStream inputStream = Files.newInputStream(safePath)) {
            Document document = newDocumentBuilder().parse(inputStream);
            Element root = document.getDocumentElement();
            if (root == null || !GROUP_TAG.equals(root.getTagName())) {
                throw new PersistenceException("Task file has an invalid root element.");
            }
            return readGroup(root);
        } catch (IOException | ParserConfigurationException | SAXException exception) {
            throw new PersistenceException("Unable to load tasks.", exception);
        }
    }

    private static Element toElement(Document document, TaskComponent component) {
        if (component instanceof Task task) {
            Element element = document.createElement(TASK_TAG);
            setCommonAttributes(element, task);
            element.setAttribute("priority", task.getPriority().name());
            element.setAttribute("dueDate", task.getDueDate().toString());
            element.setAttribute("completed", Boolean.toString(task.isCompleted()));
            return element;
        }

        if (component instanceof TaskGroup group) {
            Element element = document.createElement(GROUP_TAG);
            setCommonAttributes(element, group);
            for (TaskComponent child : group.getChildren()) {
                element.appendChild(toElement(document, child));
            }
            return element;
        }

        throw new PersistenceException("Unsupported task component type.");
    }

    private static void setCommonAttributes(Element element, TaskComponent component) {
        element.setAttribute("id", component.getId());
        element.setAttribute("title", component.getTitle());
        element.setAttribute("description", component.getDescription());
    }

    private static TaskGroup readGroup(Element element) {
        TaskGroup group = new TaskGroup(
                requiredAttribute(element, "id", MAX_ID_LENGTH),
                requiredAttribute(element, "title", MAX_TITLE_LENGTH),
                requiredAttribute(element, "description", MAX_DESCRIPTION_LENGTH)
        );

        NodeList children = element.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            Node node = children.item(index);
            if (node instanceof Element childElement) {
                group.add(readComponent(childElement));
            }
        }

        return group;
    }

    private static TaskComponent readComponent(Element element) {
        if (GROUP_TAG.equals(element.getTagName())) {
            return readGroup(element);
        }
        if (TASK_TAG.equals(element.getTagName())) {
            return new TaskBuilder()
                    .id(requiredAttribute(element, "id", MAX_ID_LENGTH))
                    .title(requiredAttribute(element, "title", MAX_TITLE_LENGTH))
                    .description(requiredAttribute(element, "description", MAX_DESCRIPTION_LENGTH))
                    .priority(readPriority(element))
                    .dueDate(readDueDate(element))
                    .completed(Boolean.parseBoolean(element.getAttribute("completed")))
                    .build();
        }
        throw new PersistenceException("Task file contains an unsupported element.");
    }

    private static Priority readPriority(Element element) {
        try {
            return Priority.valueOf(requiredAttribute(element, "priority", 20));
        } catch (IllegalArgumentException exception) {
            throw new PersistenceException("Task file contains an invalid priority.", exception);
        }
    }

    private static LocalDate readDueDate(Element element) {
        try {
            return LocalDate.parse(requiredAttribute(element, "dueDate", 20));
        } catch (DateTimeParseException exception) {
            throw new PersistenceException("Task file contains an invalid due date.", exception);
        }
    }

    private static String requiredAttribute(Element element, String name, int maxLength) {
        if (!element.hasAttribute(name)) {
            throw new PersistenceException("Task file is missing required data.");
        }
        return InputSanitizer.sanitizeText(element.getAttribute(name), name, maxLength);
    }

    private static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder();
    }

    private static Path normalize(Path filePath) {
        Objects.requireNonNull(filePath, "filePath cannot be null");
        Path normalized = filePath.toAbsolutePath().normalize();
        if (!normalized.getFileName().toString().endsWith(".xml")) {
            throw new PersistenceException("Task file must use the .xml extension.");
        }
        return normalized;
    }
}
