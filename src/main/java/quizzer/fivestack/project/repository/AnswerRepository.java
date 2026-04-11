package quizzer.fivestack.project.repository;

import org.springframework.data.repository.CrudRepository;
import quizzer.fivestack.project.domain.Answer;
import java.util.List;


public interface AnswerRepository extends CrudRepository<Answer, Long>{
    List<Answer> findByAnswerContent(String answerContent);
}
