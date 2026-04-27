package quizzer.fivestack.project.repository;

import org.springframework.data.repository.CrudRepository;
import quizzer.fivestack.project.domain.Question;
import quizzer.fivestack.project.domain.StudentAnswer;
import quizzer.fivestack.project.domain.User;

public interface StudentAnswerRepository extends CrudRepository<StudentAnswer, Long> {
    void deleteByUserAndQuestion(User user, Question question);
}
