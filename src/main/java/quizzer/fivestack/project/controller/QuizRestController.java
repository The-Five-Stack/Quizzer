package quizzer.fivestack.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import quizzer.fivestack.project.repository.QuizRepository;
import quizzer.fivestack.project.repository.UserRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;

import quizzer.fivestack.project.domain.Answer;
import quizzer.fivestack.project.domain.Question;
import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.domain.User;
import quizzer.fivestack.project.dto.AnswerDto;
import quizzer.fivestack.project.dto.QuestionDto;
import quizzer.fivestack.project.dto.QuizDto;
import java.util.stream.Collectors;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "http://localhost:5173")
public class QuizRestController {
    private final QuizRepository repository;

    private final UserRepository userRepository;

    public QuizRestController(QuizRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizDto dto, Principal principal) {
        // Check current username
        String currentUsername = principal.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create new Quiz
        Quiz newQuiz = new Quiz();
        newQuiz.setQuizName(dto.getName());
        newQuiz.setQuizDescription(dto.getDescription());
        newQuiz.setCourseCode(dto.getCourseCode());
        newQuiz.setIsPublished(dto.getPublished());

        // Create list of questions
        List<Question> questions = new ArrayList<>();

        // Process questions from DTO
        if (dto.getQuestions() != null) {
            for (QuestionDto qDto : dto.getQuestions()) {
                Question q = new Question();
                q.setQuestionContent(qDto.getQuestionContent());
                q.setDifficulty(qDto.getDifficulty());
                q.setQuiz(newQuiz); // Set parent-child relationship

                // Process Answers inside each Question
                List<Answer> answers = new ArrayList<>();
                if (qDto.getAnswers() != null) {
                    for (AnswerDto aDto : qDto.getAnswers()) {
                        Answer a = new Answer();
                        a.setAnswerContent(aDto.getContent());
                        a.setIsCorrect(aDto.getCorrect());
                        a.setQuestion(q); // Set parent-child relationship
                        answers.add(a);
                    }
                }
                q.setAnswers(answers);
                questions.add(q);
            }
        }

        if (questions.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "At least one question is required"));
        }

        newQuiz.setQuestions(questions);
        newQuiz.setOwner(currentUser);

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

        QuizDto dto = new QuizDto();
        dto.setId(quiz.getQuizId());
        dto.setName(quiz.getQuizName());
        dto.setDescription(quiz.getQuizDescription());
        dto.setCourseCode(quiz.getCourseCode());
        dto.setPublished(quiz.getIsPublished());

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
