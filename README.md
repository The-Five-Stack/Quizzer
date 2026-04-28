<h1>Quizzer</h1>


## Description
Quizzer is an quizz web application help both teachers and students in the learning process through interactive quizzes. The project is developed using a Scrum framework, where team collaborates in iterative sprints to deliver features aligned with the Product Owner’s vision. The goal is to provide an efficient and structured way to create, manage, and complete quizzes in an educational environment.

The platform includes two main user roles: teachers and students. Teachers use a dedicated dashboard to create and manage quizzes by defining details such as name, description, course code, and publication status. They can also add multiple choice questions with different difficulty levels, define answer options, and organize quizzes into categories. This enables structured content management and improves the organization of learning materials.

Students interact with the system through a separate dashboard where they can access published quizzes. While completing quizzes, students receive immediate feedback on their answers, supporting effective learning. The application also provides a results view, where students can see their overall performance, including correct answer percentages and detailed question-level statistics.

Members:
<ul>
<li>Oanh Pham</li>
<li>Tri Pham</li>
<li>Sadikshya Parajuli</li>
<li>Nghi Vo</li>
<li>Quy Tran</li>
</ul>

<h2> Our github links: </h2>
<ul>
<li><a href="https://github.com/lunapham10">Oanh Pham </a> </li>
<li><a href= "https://github.com/qynwphuu"> Quy Tran </a> </li>
<li> <a href= "https://github.com/tripham-fi"> Tri Pham </a> </li>
<li> <a href= "https://github.com/HaniNghi"> Nghi Vo </a> </li>
<li><a href= "https://github.com/sadikshyeah"> Sadikshya Parajuli</a></li>
</ul>

<h2> Backlog</h2>
<li><a href="https://github.com/orgs/The-Five-Stack/projects/2">Backlog for Quizzer</a></li>

## Developer Guide
### Backend
#### System requirements

To run this application, you must have the following installed on your system:
- Java 17: The application is built using Java 17 (as specified in the pom.xml under the java.version property).
- Git: To clone the repository.

#### How to start the backend application
Follow these steps to get the application up and running:
1. Clone the repository
Open your terminal (e.g., Git Bash, Command Prompt, or PowerShell) and run:

```
git clone https://github.com/The-Five-Stack/Quizzer.git
cd Quizzer
```

2. Configure the Environment (Important)
By default, the application is configured to run with an H2 in-memory database for local development. If you are running the application for the first time, ensure no other service is using port 8080.

3. Run the application
Use the Maven Wrapper (./mvnw) to start the Spring Boot application. This ensures you don't need to have Maven installed globally.

```
./mvnw spring-boot:run
```

4. Access the application
Once the terminal shows "Started ProjectApplication", open your web browser and visit: http://localhost:8080

#### URL of the backend application
https://teacher:teacher123@quizzer-git-quizzer-project.2.rahtiapp.fi/

#### REST API
https://teacher:teacher123@quizzer-git-quizzer-project.2.rahtiapp.fi/swagger-ui/index.html

### Frontend
#### Teacher Dashboard
https://quizzer-ui.onrender.com

#### Student Dashboard
https://quizzer-ui.onrender.com/student

## Retrospectives
https://edu.flinga.fi/s/EKJFXSK


## Data Model
### Entity Relationship Diagram
```mermaid
erDiagram
    USER ||--o{ QUIZ : "teacher_owns"
    CATEGORY ||--o{ QUIZ : "categorizes"
    QUIZ ||--o{ QUESTION : "contains"
    QUESTION ||--o{ ANSWER : "has"
    USER ||--o{ STUDENT_ANSWER : "student_submits"
    QUIZ ||--o{ STUDENT_ANSWER : "is_taken_in"
    QUESTION ||--o{ STUDENT_ANSWER : "is_answered_in"
    ANSWER ||--o{ STUDENT_ANSWER : "is_selected"

    USER {
        Long id PK
        String username UK
        String fullname
        String firstName
        String lastName
        String email UK
        String password
        Boolean enabled
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }

    CATEGORY {
        Long id PK
        String name
        String description
    }

    QUIZ {
        Long quizId PK
        String quizName
        String quizDescription
        String courseCode
        Boolean isPublished
        LocalDateTime createdAt
        Long category_id FK
        Long owner_id FK
    }

    QUESTION {
        Long questionId PK
        String questionContent
        Difficulty difficulty "EASY | NORMAL | HARD"
        Long quiz_id FK
    }

    ANSWER {
        Long answerId PK
        String answerContent
        Boolean isCorrect
        Long question_id FK
    }

    STUDENT_ANSWER {
        Long id PK
        Long user_id FK
        Long quiz_id FK
        Long question_id FK
        Long selected_answer_id FK
        Boolean isCorrect
        LocalDateTime submittedAt
    }
```


### Data Model Description

The **Quizzer** application uses **Basic Authentication** (Spring Security) and consists of the following main entities:

#### 1. **User**
- Represents both **teachers** and **students** in the system
- Stores authentication credentials (username and password) and profile information
- A user can own multiple quizzes (as a teacher) and submit answers to quizzes (as a student)

#### 2. **Category**
- Used to organize quizzes by topic ("Agile", "Databases", "Java", etc...)
- One category can be assigned to many quizzes.

#### 3. **Quiz**
- The central entity of the application.
- Contains metadata such as name, description, course code, published status, and creation date.
- Each quiz belongs to **one** teacher (`owner`) and **one** category

#### 4. **Question**
- Represents a single multiple-choice question inside a quiz
- Contains the question content and a difficulty level (`EASY`, `NORMAL`, `HARD`)
- One quiz can have many questions.

#### 5. **Answer**
- Represents one possible answer option for a question
- Contains the answer text and a boolean flag indicating whether it is the correct answer
- One question can have multiple answers.

#### 6. **StudentAnswer**
- Tracks individual student responses
- Records which student answered which question in which quiz, the selected answer, and whether it was correct
- Enables the calculation of quiz results and statistics


### Relationship Summary

| Relationship                    | Type          | Description |
|-------------------------------|---------------|-----------|
| User → Quiz                   | One-to-Many   | A teacher owns multiple quizzes |
| Quiz → User (owner)                | Many-to-One      | Many quizzes belong to one teacher |
| Category → Quiz               | One-to-Many   | A category contains many quizzes |
| Quiz → Category                    | Many-to-One      | Many quizzes belong to one category |
| Quiz → Question               | One-to-Many   | A quiz contains many questions |
| Question → Quiz                    | Many-to-One      | Many questions belong to one quiz |
| Question → Answer             | One-to-Many   | A question has multiple answer options |
| Answer → Question                  | Many-to-One      | Many answers belong to one question |
| User ↔ Answer (via StudentAnswer)   | Many-to-Many      | Students select answers to questions |
| User → StudentAnswer                | One-to-Many       | A user can have many answer submissions |
| Quiz → StudentAnswer          | One-to-Many     | One quiz can have many student submissions |
| Question → StudentAnswer      | One-to-Many     | One question can be answered by many students |
| Answer → StudentAnswer        | One-to-Many     | One answer option can be selected by many students |