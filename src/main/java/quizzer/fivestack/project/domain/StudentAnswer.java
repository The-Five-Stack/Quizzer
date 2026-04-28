package quizzer.fivestack.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_answers")
@Getter
@Setter
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_answer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Answer selectedAnswer;

    @Column(nullable = false)
    private Boolean isCorrect;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    public StudentAnswer() {
    }

    public StudentAnswer(User user, Quiz quiz, Question question, Answer selectedAnswer, Boolean isCorrect) {
        this.user = user;
        this.quiz = quiz;
        this.question = question;
        this.selectedAnswer = selectedAnswer;
        this.isCorrect = isCorrect;
    }
}
