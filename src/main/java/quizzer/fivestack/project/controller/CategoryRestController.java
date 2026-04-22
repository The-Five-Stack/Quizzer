package quizzer.fivestack.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import quizzer.fivestack.project.repository.CategoryRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import quizzer.fivestack.project.domain.Category;
import quizzer.fivestack.project.dto.CategoryDto;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryRepository categoryRepository;

    public CategoryRestController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Create a new category
    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto dto) {

        Category newCategory = new Category();
        newCategory.setName(dto.getName());
        newCategory.setDescription(dto.getDescription());

        Category savedCategory = categoryRepository.save(newCategory);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Category created successfully!",
                "categoryId", savedCategory.getId()));
    }

    // Get category by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(category -> ResponseEntity.ok(category))
                .orElse(ResponseEntity.notFound().build());
    }
}