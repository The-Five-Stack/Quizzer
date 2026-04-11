package quizzer.fivestack.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import quizzer.fivestack.project.domain.Question;
import quizzer.fivestack.project.dto.QuestionDto;
import quizzer.fivestack.project.repository.QuestionRepository;
import quizzer.fivestack.project.repository.QuizRepository;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
public class QuestionRestController {
    private final QuestionRepository questionRepository;
    private final QuizRepository repository;

    public QuestionRestController(QuestionRepository questionRepository, QuizRepository repository) {
        this.questionRepository = questionRepository;
        this.repository = repository;
    }

    @PostMapping("{quizId}/questions")
    public ResponseEntity<?> addQuestionToQuiz(@PathVariable Long quizId, @Valid @RequestBody QuestionDto dto, Principal principal) {
        
        // check Quiz by quizId
        quizzer.fivestack.project.domain.Quiz quiz = repository.findById(quizId)
                .orElse(null);

        if (quiz == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Quiz not found with id: " + quizId));
        }

        // Check owner of quiz
        String currentUsername = principal.getName(); 
        if (!quiz.getOwner().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "You are not the owner of this quiz!"));
        }

        Question newQuestion = new Question();
        
        newQuestion.setQuestionContent(dto.getQuestionContent());
        newQuestion.setDifficulty(dto.getDifficulty());
        newQuestion.setQuiz(quiz);

        Question saveQuestion = questionRepository.save(newQuestion);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Question created successfully and linked to quiz " + quizId,
                "questionId", saveQuestion.getQuestionId()));
    }
}
