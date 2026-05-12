package quizzer.fivestack.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quizzer.fivestack.project.domain.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
