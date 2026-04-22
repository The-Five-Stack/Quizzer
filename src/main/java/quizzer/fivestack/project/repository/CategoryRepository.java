package quizzer.fivestack.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quizzer.fivestack.project.domain.Category;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByName(String name);
}