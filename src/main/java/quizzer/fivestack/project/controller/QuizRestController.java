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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.validation.Valid;
import quizzer.fivestack.project.domain.Answer;
import quizzer.fivestack.project.domain.Question;
import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.domain.User;
import quizzer.fivestack.project.dto.AnswerDto;
import quizzer.fivestack.project.dto.QuestionDto;
import quizzer.fivestack.project.dto.QuizDto;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // Port React/Vite
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

        // 1. Tạo danh sách câu hỏi trống
        List<Question> questions = new ArrayList<>();

        // 2. Lặp qua danh sách câu hỏi từ DTO
        if (dto.getQuestions() != null) {
            for (QuestionDto qDto : dto.getQuestions()) {
                Question q = new Question();
                q.setQuestionContent(qDto.getQuestionContent());
                q.setDifficulty(qDto.getDifficulty());
                q.setQuiz(newQuiz); // Thiết lập mối quan hệ cha - con

                // 3. Xử lý Answers bên trong mỗi Question
                List<Answer> answers = new ArrayList<>();
                if (qDto.getAnswers() != null) {
                    for (AnswerDto aDto : qDto.getAnswers()) {
                        Answer a = new Answer();
                        a.setAnswerContent(aDto.getContent());
                        a.setIsCorrect(aDto.getCorrect());
                        a.setQuestion(q); // Thiết lập mối quan hệ cha - con
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable Long id) {
        return repository.findById(id)
                .map(quiz -> {
                    QuizDto dto = new QuizDto();
                    dto.setId(quiz.getQuizId());
                    dto.setName(quiz.getQuizName());
                    dto.setDescription(quiz.getQuizDescription());
                    dto.setCourseCode(quiz.getCourseCode());
                    dto.setPublished(quiz.getIsPublished());

                    // Map Questions from Domain to DTO
                    // If Quiz (Domain) has List<Question> questions
                    List<QuestionDto> questionDtos = quiz.getQuestions().stream().map(q -> {
                        QuestionDto qDto = new QuestionDto();
                        qDto.setId(q.getQuestionId());
                        qDto.setQuestionContent(q.getQuestionContent());
                        qDto.setDifficulty(q.getDifficulty()); // Difficulty Enum

                        // Map Answers same as QuestionDto
                        // qDto.setAnswers(...)

                        return qDto;
                    }).collect(Collectors.toList());

                    dto.setQuestions(questionDtos);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
