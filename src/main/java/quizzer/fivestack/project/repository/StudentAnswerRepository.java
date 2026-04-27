package quizzer.fivestack.project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import quizzer.fivestack.project.domain.Question;
import quizzer.fivestack.project.domain.StudentAnswer;
import quizzer.fivestack.project.domain.User;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends CrudRepository<StudentAnswer, Long> {
    interface QuizResultProjection {
        Long getQuestionId();
        String getQuestionContent();
        Object getDifficulty();
        Long getAnswerCount();
        Long getCorrectCount();
    }

    void deleteByUserAndQuestion(User user, Question question);

    @Query("SELECT sa.question.questionId AS questionId, " +
            "sa.question.questionContent AS questionContent, " +
            "sa.question.difficulty AS difficulty, " +
            "COUNT(sa.id) AS answerCount, " +
            "SUM(CASE WHEN sa.isCorrect = true THEN 1L ELSE 0L END) AS correctCount " +
            "FROM StudentAnswer sa WHERE sa.quiz.quizId = :quizId " +
            "GROUP BY sa.question.questionId, sa.question.questionContent, sa.question.difficulty " +
            "ORDER BY sa.question.questionId")
    List<QuizResultProjection> findQuizResultsByQuizId(@Param("quizId") Long quizId);
}
