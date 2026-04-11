package quizzer.fivestack.project.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import quizzer.fivestack.project.domain.Quiz;
import java.util.List;

@Repository
public interface QuizRepository extends CrudRepository<Quiz, Long>{
    List<Quiz> findByQuizName(String quizName);
} 
