package quizzer.fivestack.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;

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

    @Override
    public String toString() {
        return "QuizDto [name=" + name + ", description=" + description + ", courseCode=" + courseCode + ", published="
                + published + "]";
    }

    
    
}
