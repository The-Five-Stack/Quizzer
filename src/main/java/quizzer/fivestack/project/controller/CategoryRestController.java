package quizzer.fivestack.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import quizzer.fivestack.project.repository.CategoryRepository;
import quizzer.fivestack.project.repository.QuizRepository;
import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.dto.QuizDto;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import quizzer.fivestack.project.domain.Category;
import quizzer.fivestack.project.dto.CategoryDto;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = { "http://localhost:5173", "https://quizzer-ui.onrender.com" })
public class CategoryRestController {

    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;

    public CategoryRestController(CategoryRepository categoryRepository, QuizRepository quizRepository) {
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
    }

    // Create a new category
    @PostMapping
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
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Category not found with id: " + id));
        }

        Category category = categoryOpt.get();
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());

        return ResponseEntity.ok(dto);
    }

    // Get all categories
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = ((List<Category>) categoryRepository.findAll())
                .stream()
                .map(c -> {
                    CategoryDto dto = new CategoryDto();
                    dto.setId(c.getId());
                    dto.setName(c.getName());
                    dto.setDescription(c.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    // Delete category by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Category not found with id: " + id));
        }

        categoryRepository.delete(categoryOpt.get());

        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // Get published quizzes by category
    @GetMapping("/{id}/published-quizzes")
    public ResponseEntity<List<QuizDto>> getPublishedQuizzesByCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Quiz> quizzes = quizRepository.findByCategoryIdAndIsPublishedTrue(id);
        List<QuizDto> quizDtos = quizzes.stream()
                .map(QuizDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(quizDtos);
    }
}