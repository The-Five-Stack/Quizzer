package quizzer.fivestack.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import quizzer.fivestack.project.domain.Quiz;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizDto {
    private Long id;

    @NotBlank(message = "Quiz name is required")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Quiz description is required")
    @Size(max = 1000)
    private String description;

    @NotBlank(message = "Course code is required")
    @Size(max = 20)
    @Pattern(regexp = "^[A-Z]{3}\\d{3}[A-Z]{2}\\d[A-Z]{2}$", message = "Must follow Haaga-Helia format (e.g., SOF005AS3AE)")
    private String courseCode;

    @NotNull(message = "Published status is required")
    private Boolean published;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Helsinki")
    private LocalDateTime createdAt;

    private List<QuestionDto> questions;

    private CategoryDto category;

    public QuizDto(){

    }
    
    public QuizDto(Long id, String name, String description, String courseCode, Boolean published) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.courseCode = courseCode;
        this.published = published;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "QuizDto [name=" + name + ", description=" + description + ", courseCode=" + courseCode + ", published="
                + published + ", createdAt=" + createdAt +"]";
    }

    public static QuizDto from(Quiz quiz) {
        if (quiz == null) {
            return null;
        }

        QuizDto dto = new QuizDto();
        dto.setId(quiz.getQuizId());
        dto.setName(quiz.getQuizName());
        dto.setDescription(quiz.getQuizDescription());
        dto.setCourseCode(quiz.getCourseCode());
        dto.setPublished(quiz.getIsPublished());
        dto.setCreatedAt(quiz.getCreatedAt());

        if (quiz.getCategory() != null) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(quiz.getCategory().getId());
            categoryDto.setName(quiz.getCategory().getName());
            categoryDto.setDescription(quiz.getCategory().getDescription());
            dto.setCategory(categoryDto);
        }
        return dto;
    }

    
    
}
