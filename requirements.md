# Final Project Assignment

## Project Expectations: Goal

Develop a well-architected, secure, and maintainable Java SE application showcasing object-oriented design, use of patterns, and core Java technologies.

---

# Project Expectations: Minimum Requirements

## 4 Required Design Patterns

- Factory
- Composite
- Iterator
- Exception Shielding

## Core Technologies

- Collections
- Generics
- Java I/O
- Logging
- JUnit Testing

## Secure Programming

- Input Sanitization
- No Hardcoded Secrets
- Controlled Exception Propagation

---

# Project Expectations: Aiming for the Maximum

## Beyond Minimum

- Additional patterns and technologies from a defined list.
- Correct integration and critical-path usage of optional components.
- Additional points do not raise the grade beyond 50, but allow varied strategy.

## Security Penalties

- Crashes, stack traces, insecure data handling will subtract points.

## Documentation and Justification

README must include:

- Project overview
- Design decisions
- Diagrams
- Technology and pattern justifications
- Limitations

Expect interview questions on alternatives and rationale.

---

# Project Expectations: Freedom of Design

Students are free to select any project scenario or application domain of their choice.

Examples include:

- Utility applications
- Simulations
- Personal productivity tools
- Data processing tools
- Any other application that fits the criteria

Requirements:

- Minimum required features must be implemented as specified.
- Students may integrate any number of advanced technologies or design patterns from the optional list to improve their score.
- Projects will be evaluated not just on what is included, but how well technologies are integrated, justified, and secured.

---

# Project Expectations: Deliverables

1. Complete Java project source code

2. `README.md` containing:

   - Application overview and functionality
   - Technologies and patterns used, with justification
   - Setup and execution instructions
   - UML diagrams (class + architectural)
   - Known limitations and future work

3. Test suite

---

# Project Expectations: Interview

During the evaluation, students will:

1. Demo the application live
2. Walk through random parts of the code
3. Justify design decisions

Expect questions on why specific technologies and patterns were used over alternatives. Understanding and rationale are part of the grade.

---

# Evaluation Breakdown

## Required Features

### Design Patterns (16 pts total)

| Pattern | Points |
|---|---|
| Factory | 3 pts |
| Composite | 4 pts |
| Iterator | 4 pts |
| Exception Shielding | 5 pts |

### Technologies (14 pts total)

| Technology | Points |
|---|---|
| Collections Framework | 3 pts |
| Generics | 3 pts |
| Java I/O | 3 pts |
| Logging | 2 pts |
| JUnit Testing | 3 pts |

### Constraints

- All required items must be included in the project.
- Each item coding and usage will be evaluated from 0 to N points.
- Minimum required score from required features: 25 points.

Constraint formula:

```text
25 ≤ α ≤ 30
```

---

# Evaluation Breakdown — Optional Advanced Features

## Total Formula

```text
Total = α + β
```

## Optional Advanced Features

### Optional Design Patterns

| Pattern | Points |
|---|---|
| Abstract Factory | 3 pts |
| Builder | 3 pts |
| Strategy | 4 pts |
| Observer | 5 pts |
| Chain of Responsibility | 4 pts |
| Adapter | 2 pts |
| Bridge | 2 pts |
| Proxy | 2 pts |
| Decorator | 2 pts |
| Singleton | 2 pts |
| Memento | 3 pts |
| Template Method | 3 pts |

### Optional Technologies

| Technology | Points |
|---|---|
| Multithreading | 8 pts |
| Stream API & Lambdas | 5 pts |
| Reflection | 4 pts |
| Inversion of Control | 5 pts |
| Custom Annotations | 2 pts |
| Mockito | 3 pts |

### Constraints

```text
25 ≤ α ≤ 30
0 ≤ β ≤ 25
```

---

# Evaluation Breakdown — Security Penalties

## Total Formula

```text
Total = α + β - γ
```

## Security Penalties

| Issue | Penalty |
|---|---|
| Stack traces visible to users | -5 pts |
| Application crashes on invalid input | -5 pts |
| Hardcoded credentials | -3 pts |
| No input sanitization | -3 pts |
| Exception propagation leaks | -3 pts |
| Poor logging practices | -2 pts |
| Minor issues | -1 pt each |

### Constraints

```text
25 ≤ α ≤ 30
0 ≤ β ≤ 25
α + β - γ ≤ 50
```

---

# Final Grade Rules

- Minimum grade to approve the project: 25 points
- Maximum grade obtained with at least 50 points