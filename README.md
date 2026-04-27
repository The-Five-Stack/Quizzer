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
https://quizzer-git-quizzer-project.2.rahtiapp.fi

#### REST API
https://quizzer-git-quizzer-project.2.rahtiapp.fi/swagger-ui/index.html

### Frontend
#### Teacher Dashboard
https://quizzer-ui.onrender.com

#### Student Dashboard
https://quizzer-ui.onrender.com/student

## Retrospectives
https://edu.flinga.fi/s/EKJFXSK
