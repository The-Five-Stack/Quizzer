package quizzer.fivestack.project.repository;

import org.springframework.data.repository.CrudRepository;
import quizzer.fivestack.project.domain.Question;
import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    List<Question> findByQuestionContent(String questionContent);
}
