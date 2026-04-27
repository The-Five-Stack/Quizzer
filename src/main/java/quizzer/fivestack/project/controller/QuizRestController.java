package quizzer.fivestack.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import quizzer.fivestack.project.repository.QuizRepository;
import quizzer.fivestack.project.repository.UserRepository;
import quizzer.fivestack.project.repository.CategoryRepository;
import quizzer.fivestack.project.repository.StudentAnswerRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;

import quizzer.fivestack.project.domain.Answer;
import quizzer.fivestack.project.domain.Question;
import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.domain.User;
import quizzer.fivestack.project.domain.Category;
import quizzer.fivestack.project.dto.AnswerDto;
import quizzer.fivestack.project.dto.QuestionDto;
import quizzer.fivestack.project.dto.QuestionResultDto;
import quizzer.fivestack.project.dto.QuizDto;
import java.util.stream.Collectors;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = { "http://localhost:5173", "https://quizzer-ui.onrender.com" })
public class QuizRestController {
    private final QuizRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    public QuizRestController(QuizRepository repository, UserRepository userRepository,
            CategoryRepository categoryRepository, StudentAnswerRepository studentAnswerRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.studentAnswerRepository = studentAnswerRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizDto dto, Principal principal) {
        // Check current username
        String currentUsername = principal.getName();
        Optional<User> userOpt = userRepository.findByUsername(currentUsername);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found: " + currentUsername));
        }
        User currentUser = userOpt.get();

        // Check user in database
        Quiz newQuiz = new Quiz();

        newQuiz.setQuizName(dto.getName());
        newQuiz.setQuizDescription(dto.getDescription());
        newQuiz.setCourseCode(dto.getCourseCode());
        newQuiz.setIsPublished(dto.getPublished());

        newQuiz.setOwner(currentUser);

        Optional<Category> categoryOpt = categoryRepository.findById(dto.getCategoryId());
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Category not found with id: " + dto.getCategoryId()));
        }
        newQuiz.setCategory(categoryOpt.get());

        Quiz saveQuiz = repository.save(newQuiz);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Quiz created successfully!",
                "quizId", saveQuiz.getQuizId()));
    }

    // Get quiz by ID endpoint
    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable Long id, Principal principal) {
        Optional<Quiz> quizOpt = repository.findWithQuestionsAndAnswersById(id);

        Quiz quiz = quizOpt.orElse(null);

        boolean doesExistAndIsOwner = quiz != null && quiz.getOwner() != null
                && quiz.getOwner().getUsername().equals(principal.getName());

        if (!doesExistAndIsOwner) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "Quiz not found with id: " + id));
        }

        QuizDto dto = QuizDto.from(quiz);

        List<QuestionDto> questionDtos = new ArrayList<>();
        if (quiz.getQuestions() != null) {
            for (Question q : quiz.getQuestions()) {
                questionDtos.add(toQuestionDto(q));
            }
        }
        dto.setQuestions(questionDtos);

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<QuizDto>> getAllQuizzes() {
        List<QuizDto> quizzes = ((List<Quiz>) repository.findAll())
                .stream()
                .map(QuizDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(quizzes);
    }

    // Endpoint to delete a quiz by quizId
    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId, Principal principal) {

        Optional<Quiz> quizOpt = repository.findById(quizId);

        Quiz quiz = quizOpt.orElse(null);
        String currentUsername = principal.getName();

        if (quiz == null
                || quiz.getOwner() == null
                || !quiz.getOwner().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Quiz not found with id: " + quizId));
        }

        repository.delete(quiz);

        return ResponseEntity.ok(
                Map.of("message", "Quiz deleted successfully",
                        "quizId", quizId));
    }

    // Helper method to convert question entity to DTO
    private static QuestionDto toQuestionDto(Question q) {
        QuestionDto dto = new QuestionDto();
        dto.setId(q.getQuestionId());
        dto.setQuestionContent(q.getQuestionContent());
        dto.setDifficulty(q.getDifficulty());

        List<AnswerDto> answerDtos = new ArrayList<>();
        if (q.getAnswers() != null) {
            for (Answer a : q.getAnswers()) {
                answerDtos.add(toAnswerDto(a));
            }
        }
        dto.setAnswers(answerDtos);
        return dto;
    }

    // Helper method to convert answer entity to DTO
    private static AnswerDto toAnswerDto(Answer a) {
        AnswerDto dto = new AnswerDto();
        dto.setId(a.getAnswerId());
        dto.setContent(a.getAnswerContent());
        dto.setCorrect(a.getIsCorrect());
        return dto;
    }

    // filter quizz by published
    @GetMapping("/publishedquizz")
    public ResponseEntity<List<QuizDto>> getAllPublishedQuizzes() {
        List<QuizDto> quizzes = repository.findByIsPublishedTrue()
                .stream()
                .map(QuizDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(quizzes);
    }

    // Edit, update quiz endpoint
    @PutMapping("/{id}")
    public ResponseEntity<?> editQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizDto dto,
            Principal principal) {
        Optional<Quiz> quizOpt = repository.findById(id);

        Quiz quiz = quizOpt.orElse(null);
        if (quiz == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Quiz not found with id: " + id));
        }

        if (quiz.getOwner() == null || !quiz.getOwner().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Not your quiz"));
        }

        if (dto.getCategoryId() != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(dto.getCategoryId());
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Category not found with id: " + dto.getCategoryId()));
            }
            quiz.setCategory(categoryOpt.get());
        } else {
            quiz.setCategory(null);
        }

        quiz.setQuizName(dto.getName());
        quiz.setQuizDescription(dto.getDescription());
        quiz.setCourseCode(dto.getCourseCode());
        quiz.setIsPublished(dto.getPublished());

        Quiz saved = repository.save(quiz);

        return ResponseEntity.ok(
                Map.of("message", "Quiz updated successfully", "quizId", saved.getQuizId()));
    }

    // Get quiz results endpoint
    @GetMapping("/{quizId}/results")
    public ResponseEntity<?> getQuizResults(@PathVariable Long quizId) { // <?> is ok here because we can return either
                                                                         // List<QuestionResultDto> or error message
        if (!repository.existsById(quizId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Quiz not found with id: " + quizId));
        }

        List<Object[]> results = studentAnswerRepository.findQuizResultsByQuizId(quizId);

        List<QuestionResultDto> resultDtos = results.stream()
                .map(row -> new QuestionResultDto(
                        (Long) row[0],
                        (String) row[1],
                        row[2].toString(),
                        ((Number) row[3]).intValue(),
                        ((Number) row[4]).intValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultDtos);
    }
}
