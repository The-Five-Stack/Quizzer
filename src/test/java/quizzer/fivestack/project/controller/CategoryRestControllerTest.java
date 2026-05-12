package quizzer.fivestack.project.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import quizzer.fivestack.project.domain.Category;
import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.dto.CategoryDto;
import quizzer.fivestack.project.repository.CategoryRepository;
import quizzer.fivestack.project.repository.QuizRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuizRepository quizRepository;

    // ObjectMapper for converting Objects to JSON strings in tests
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createCategorySavesValidCategory() throws Exception {
        // Arrange: Prepare a valid Category DTO
        CategoryDto newCategory = new CategoryDto("Java", "Java Programming");

        // Act & Assert: Perform POST and verify successful creation (201 Created)
        mockMvc.perform(post("/api/categories")
                .with(httpBasic("teacher2", "teacher123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Category created successfully!"))
                .andExpect(jsonPath("$.categoryId").exists());
    }

    @Test
    public void createCategoryReturnsBadRequestWhenNameIsBlank() throws Exception {
        // Arrange: Create a DTO with an empty name to trigger @NotBlank validation
        CategoryDto invalidCategory = new CategoryDto("", "Description without name");

        // Act & Assert: Verify that the system returns 400 Bad Request
        mockMvc.perform(post("/api/categories")
                .with(httpBasic("teacher2", "teacher123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createCategoryReturnsConflictWhenNameExists() throws Exception {
        // Arrange: Seed the database with an existing category to simulate a name
        // collision
        // Use a unique value so this test doesn't depend on seed data and won't break
        // if
        // you later add a unique constraint on category name.
        String duplicateName = "Dup-" + UUID.randomUUID();

        Category existing = new Category();
        existing.setName(duplicateName);
        categoryRepository.save(existing);

        CategoryDto duplicateCategory = new CategoryDto(duplicateName, "Duplicate name test");

        // Act & Assert: Verify that the system prevents duplicates with 409 Conflict
        mockMvc.perform(post("/api/categories")
                .with(httpBasic("teacher2", "teacher123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateCategory)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Category name already exists!"));
    }

    @Test
    public void getPublishedQuizzesByCategoryReturnsListOfQuizzesWhenCategoryHasPublishedQuiz() throws Exception {
        Category category = createTestCategory();
        createPublishedQuiz(category, "Scrum Framework");
        createUnpublishedQuiz(category, "Draft Quiz");

        mockMvc.perform(get("/api/categories/" + category.getId() + "/published-quizzes")
                        .with(httpBasic("teacher2", "teacher123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Scrum Framework"))
                .andExpect(jsonPath("$[0].published").value(true));
    }

    @Test
    public void getPublishedQuizzesByCategoryReturnsOnlyPublishedQuizzes() throws Exception {
        Category category = createTestCategory();
        createPublishedQuiz(category, "Published One");
        createUnpublishedQuiz(category, "Unpublished One");

        mockMvc.perform(get("/api/categories/" + category.getId() + "/published-quizzes")
                        .with(httpBasic("teacher2", "teacher123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].published").value(true))
                .andExpect(jsonPath("$[0].name").value("Published One"));
    }

    @Test
    public void getPublishedQuizzesByCategoryReturnsEmptyListWhenCategoryHasNoPublishedQuizzes() throws Exception {
        Category category = createTestCategory();
        mockMvc.perform(get("/api/categories/" + category.getId() + "/published-quizzes")
                        .with(httpBasic("teacher2", "teacher123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void getPublishedQuizzesByCategoryReturnsNotFoundWhenCategoryDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/categories/999999/published-quizzes")
                        .with(httpBasic("teacher2", "teacher123")))
                .andExpect(status().isNotFound());
    }

    private Category createTestCategory() {
        Category category = new Category();
        category.setName("Test-Category-" + UUID.randomUUID());
        category.setDescription("Test category");
        return categoryRepository.save(category);
    }

    private void createPublishedQuiz(Category category, String name) {
        Quiz quiz = new Quiz(name, "Description", "SOF999AS3AE", true);
        quiz.setCategory(category);
        quizRepository.save(quiz);
    }

    private void createUnpublishedQuiz(Category category, String name) {
        Quiz quiz = new Quiz(name, "Description", "SOF888AS3AE", false);
        quiz.setCategory(category);
        quizRepository.save(quiz);
    }
}