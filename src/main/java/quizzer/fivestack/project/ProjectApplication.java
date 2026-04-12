package quizzer.fivestack.project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import quizzer.fivestack.project.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import quizzer.fivestack.project.repository.UserRepository;

@SpringBootApplication
public class ProjectApplication {

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

				User teacher2 = new User(
						"Teacher2",
						"User2",
						"teacher2",
						"teacher2@example.com",
						passwordEncoder.encode("teacher123")
				);
				userRepository.save(teacher2);
			}
		};
	}
}
