package quizzer.fivestack.project.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import quizzer.fivestack.project.domain.Review;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    List<Review> findByQuizQuizId(Long quizId);
}
