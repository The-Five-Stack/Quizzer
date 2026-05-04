package quizzer.fivestack.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CrossOrigin;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Questions", description = "Operations for managing questions within quizzes")
@CrossOrigin(origins = { "http://localhost:5173", "https://quizzer-ui.onrender.com" })
public class QuestionRestController {
    private final QuestionRepository questionRepository;
    private final QuizRepository repository;

    public QuestionRestController(QuestionRepository questionRepository, QuizRepository repository) {
        this.questionRepository = questionRepository;
        this.repository = repository;
    }

    @Operation(summary = "Add a question to a quiz", description = "Creates a new question and links it to an existing quiz. Only the quiz owner can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Question created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not the owner of the quiz"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> addQuestionToQuiz(@PathVariable Long quizId, @Valid @RequestBody QuestionDto dto,
            Principal principal) {

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

    @Operation(
        summary = "Delete a question from a quiz",
        description = "Deletes a specific question by ID. Requires ownership of the parent quiz."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden: User is not the owner"),
        @ApiResponse(responseCode = "404", description = "Quiz or Question not found")
    })
    @DeleteMapping("{quizId}/questions/{questionId}")
    public ResponseEntity<?> deleteQuestionFromQuiz(@PathVariable Long quizId, @PathVariable Long questionId,
            Principal principal) {

        Optional<quizzer.fivestack.project.domain.Quiz> quizOpt = repository.findById(quizId);
        // Return 404 when the parent quiz does not exist.
        if (quizOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Quiz not found with id: " + quizId));
        }

        quizzer.fivestack.project.domain.Quiz quiz = quizOpt.get();
        String currentUsername = principal.getName();

        // Return 403 when quiz exists but belongs to another user.
        if (quiz.getOwner() == null || !quiz.getOwner().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not the owner of this quiz!"));
        }

        Optional<Question> questionOpt = questionRepository.findById(questionId);
        // Return 404 when question does not exist or is not part of this quiz.
        if (questionOpt.isEmpty() || questionOpt.get().getQuiz() == null
                || !questionOpt.get().getQuiz().getQuizId().equals(quizId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Question not found with id: " + questionId + " in quiz: " + quizId));
        }

        questionRepository.delete(questionOpt.get());

        return ResponseEntity.ok(Map.of(
                "message", "Question and related answers deleted successfully",
                "questionId", questionId,
                "quizId", quizId));
    }

}
