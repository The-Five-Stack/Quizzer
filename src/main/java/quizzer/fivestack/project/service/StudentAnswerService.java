package quizzer.fivestack.project.service;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import quizzer.fivestack.project.domain.*;
import quizzer.fivestack.project.dto.StudentAnswerRequest;
import quizzer.fivestack.project.dto.StudentAnswerResponse;
import quizzer.fivestack.project.repository.AnswerRepository;
import quizzer.fivestack.project.repository.StudentAnswerRepository;
import quizzer.fivestack.project.repository.UserRepository;

import java.security.Principal;

@Service
public class StudentAnswerService {
    private final StudentAnswerRepository studentAnswerRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public StudentAnswerService(StudentAnswerRepository studentAnswerRepository,
                                AnswerRepository answerRepository,
                                UserRepository userRepository) {
        this.studentAnswerRepository = studentAnswerRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public StudentAnswerResponse submitAnswer(StudentAnswerRequest request, Principal principal) {
        String username = principal.getName();
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Answer selectedAnswer = answerRepository.findById(request.getAnswerOptionId())
                .orElseThrow(() -> new RuntimeException("Answer option not found"));

        Question question = selectedAnswer.getQuestion();
        Quiz quiz = question.getQuiz();

        if (quiz.getIsPublished() == null || !quiz.getIsPublished()) {
            throw new RuntimeException("Quiz is not published");
        }

        // Allow redo via delete existing answered based on User and Question id
        studentAnswerRepository.deleteByUserAndQuestion(student, question);

        boolean isCorrect = selectedAnswer.getIsCorrect();
        studentAnswerRepository.save(new StudentAnswer(
                student,
                quiz,
                question,
                selectedAnswer,
                isCorrect
        ));
        String feedback = isCorrect
                ? "Correct! Well done."
                : "Incorrect. Please review the concept and try again.";

        return new StudentAnswerResponse(isCorrect, feedback);
    }
}
