package quizzer.fivestack.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import quizzer.fivestack.project.repository.QuizRepository;
import quizzer.fivestack.project.repository.UserRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.domain.User;
import quizzer.fivestack.project.dto.QuizDto;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
public class QuizRestController {
    private final QuizRepository repository;

    private final UserRepository userRepository;

    public QuizRestController(QuizRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizDto dto) {
        Quiz newQuiz = new Quiz();

        newQuiz.setQuizName(dto.getName());
        newQuiz.setQuizDescription(dto.getDescription());
        newQuiz.setCourseCode(dto.getCourseCode());
        newQuiz.setIsPublished(dto.getPublished());

        User owner = userRepository.findByUsername("teacher").orElse(null);
        newQuiz.setOwner(owner);

        Quiz saveQuiz = repository.save(newQuiz);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Quiz created successfully!",
                "quizId", saveQuiz.getQuizId()));
    }
    // Get quiz by ID endpoint
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable Long id) {
    Optional<Quiz> quiz = repository.findById(id);

    if (quiz.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "Quiz not found with id: " + id)
        );
    }

    return ResponseEntity.ok(quiz.get());
}
}
