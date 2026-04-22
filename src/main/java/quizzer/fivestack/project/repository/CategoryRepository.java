package quizzer.fivestack.project.repository;

import org.springframework.data.repository.CrudRepository;
import quizzer.fivestack.project.domain.Category;
import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findByCategoryName(String name);
}