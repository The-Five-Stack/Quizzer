package quizzer.fivestack.project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import quizzer.fivestack.project.domain.*;
import quizzer.fivestack.project.enums.Difficulty;
import org.springframework.security.crypto.password.PasswordEncoder;
import quizzer.fivestack.project.repository.CategoryRepository;
import quizzer.fivestack.project.repository.QuizRepository;
import quizzer.fivestack.project.repository.UserRepository;

import java.util.ArrayList;

@SpringBootApplication
public class ProjectApplication {

	private final QuizRepository quizRepository;
	private final CategoryRepository categoryRepository;

    ProjectApplication(QuizRepository quizRepository, CategoryRepository categoryRepository) {
        this.quizRepository = quizRepository;
		this.categoryRepository = categoryRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(UserRepository userRepository,
								  PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.count() == 0) {
				// Teacher
				User teacher = new User(
						"Teacher",
						"User",
						"teacher",
						"teacher@example.com",
						passwordEncoder.encode("teacher123")
				);
				userRepository.save(teacher);
				
				// Teacher 2 – for testing authorization
				User teacher2 = new User(
						"Teacher",
						"User",
						"teacher2",
						"teacher2@example.com",
						passwordEncoder.encode("teacher123")
				);
				userRepository.save(teacher2);

				// seed category
				Category cate1 = new Category(
						"Agile",
						"Quizzes related to the Agile principles and project management frameworks"
				);

				Category cate2 = new Category(
						"Databases",
						"Quizzes related to different Databases management systems and query languages"
				);

				categoryRepository.save(cate1);
				categoryRepository.save(cate2);

				// seed quiz with category, questions and answers with teacher as owner
				Quiz quiz = new Quiz(
					"The Scrum Framework", 
					"Learn about Scrum roles, events, and artifacts", 
					"SOF005AS3AE", 
					true);
				quiz.setOwner(teacher);
				quiz.setCategory(cate1); // Agile

				Question question1 = new Question("Who is responsible for maximizing product value?", Difficulty.EASY, quiz);
				Question question2 = new Question("What is the purpose of the Retrospective event?", Difficulty.NORMAL, quiz);

				quiz.setQuestions(new ArrayList<>());
				quiz.getQuestions().add(question1);
				quiz.getQuestions().add(question2);

				Answer answer1 = new Answer("Product Owner", true, question1);
				Answer answer2 = new Answer("Scrum Master", false, question1);

				question1.setAnswers(new ArrayList<>());
				question1.getAnswers().add(answer1);
				question1.getAnswers().add(answer2);

				Answer answer3 = new Answer("Finding ways to improve the process", true, question2);
				Answer answer4 = new Answer("Planning the requirements for the upcoming Sprint", false, question2);

				question2.setAnswers(new ArrayList<>());
				question2.getAnswers().add(answer3);
				question2.getAnswers().add(answer4);

				quizRepository.save(quiz);
	
			}
			
		};
	}
}
