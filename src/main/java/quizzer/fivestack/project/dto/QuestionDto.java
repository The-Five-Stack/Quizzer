package quizzer.fivestack.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import quizzer.fivestack.project.enums.Difficulty;

import java.util.List;

public class QuestionDto {
    private Long id;

    @NotBlank(message = "Question is required")
    @Size(max = 1000)
    private String questionContent;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    // List of answers for this question
    private List<AnswerDto> answers;

    public QuestionDto() {

    }

    // Getters and Setters for id and answers
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<AnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDto> answers) {
        this.answers = answers;
    }

    public QuestionDto(String questionContent, Difficulty difficulty) {
        this.questionContent = questionContent;
        this.difficulty = difficulty;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "QuestionDto [questionContent=" + questionContent + ", difficulty=" + difficulty + "]";
    }

}
