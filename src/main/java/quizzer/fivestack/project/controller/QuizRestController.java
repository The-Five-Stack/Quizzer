package quizzer.fivestack.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import quizzer.fivestack.project.repository.QuizRepository;
import quizzer.fivestack.project.repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.domain.User;
import quizzer.fivestack.project.dto.QuizDto;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<QuizDto>> getAllQuizzes() {
        List<QuizDto> quizzes = ((List<Quiz>) repository.findAll())
                .stream()
                .map(q -> {
                    QuizDto dto = new QuizDto();
                    dto.setId(q.getQuizId());
                    dto.setName(q.getQuizName());
                    dto.setDescription(q.getQuizDescription());
                    dto.setCourseCode(q.getCourseCode());
                    dto.setPublished(q.getIsPublished());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(quizzes);
    }
}
