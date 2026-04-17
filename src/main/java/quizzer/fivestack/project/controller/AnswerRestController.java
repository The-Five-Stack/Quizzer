package quizzer.fivestack.project.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import quizzer.fivestack.project.dto.AnswerDto;
import quizzer.fivestack.project.repository.AnswerRepository;
import quizzer.fivestack.project.repository.QuestionRepository;
import quizzer.fivestack.project.domain.Answer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class AnswerRestController {
    private final AnswerRepository answerRepository;

    private final QuestionRepository questionRepository;

    public AnswerRestController(AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    @PostMapping("{questionId}/answers")
    public ResponseEntity<?> addAnswertoQuestion(@PathVariable Long questionId, @Valid @RequestBody AnswerDto dto,
            Principal principal) {

        // check Question by questionId
        quizzer.fivestack.project.domain.Question question = questionRepository.findById(questionId).orElse(null);
        

        if (question == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Question not found with id: " + questionId));
        }

        // Check quiz of question
        quizzer.fivestack.project.domain.Quiz quiz = question.getQuiz();

        // Check owner of Quiz
        String currentUsername = principal.getName();
        if (!quiz.getOwner().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not the owner of the quiz that contains this question!"));
        }

        Answer newAnswer = new Answer();
        newAnswer.setAnswerContent(dto.getContent());
        newAnswer.setIsCorrect(dto.getCorrect());
        newAnswer.setQuestion(question);

        Answer saveAnswer = answerRepository.save(newAnswer);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Answer added successfully!",
                "answerId", saveAnswer.getAnswerId()));
    }

    @DeleteMapping("{questionId}/answers/{answerId}")
    public ResponseEntity<?> deleteAnswerFromQuestion(@PathVariable Long questionId, @PathVariable Long answerId,
            Principal principal) {

        Optional<quizzer.fivestack.project.domain.Question> questionOpt = questionRepository.findById(questionId);
        // Return 404 when the parent question does not exist.
        if (questionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Question not found with id: " + questionId));
        }

        quizzer.fivestack.project.domain.Question question = questionOpt.get();
        quizzer.fivestack.project.domain.Quiz quiz = question.getQuiz();
        String currentUsername = principal.getName();

        // Return 403 when question exists but current user does not own its quiz.
        if (quiz == null || quiz.getOwner() == null || !quiz.getOwner().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not the owner of the quiz that contains this question!"));
        }

        Optional<Answer> answerOpt = answerRepository.findById(answerId);
        // Return 404 when answer does not exist or is not part of this question.
        if (answerOpt.isEmpty() || answerOpt.get().getQuestion() == null
                || !answerOpt.get().getQuestion().getQuestionId().equals(questionId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Answer not found with id: " + answerId + " for question: " + questionId));
        }

        answerRepository.delete(answerOpt.get());

        return ResponseEntity.ok(Map.of(
                "message", "Answer deleted successfully",
                "answerId", answerId,
                "questionId", questionId));
    }

}
