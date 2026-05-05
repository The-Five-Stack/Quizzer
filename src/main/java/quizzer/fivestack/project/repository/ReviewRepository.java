package quizzer.fivestack.project.repository;

import org.springframework.data.repository.CrudRepository;

import quizzer.fivestack.project.domain.Review;

public interface ReviewRepository extends CrudRepository<Review, Long> {
}
