package quizzer.fivestack.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import quizzer.fivestack.project.domain.Quiz;

@Repository
public interface QuizRepository extends CrudRepository<Quiz, Long> {
    List<Quiz> findByQuizName(String quizName);

    // Fetches questions together with the quiz
    @EntityGraph(attributePaths = { "questions" })
    @Query("SELECT q FROM Quiz q WHERE q.quizId = :id")
    Optional<Quiz> findWithQuestionsAndAnswersById(@Param("id") Long id);
}
