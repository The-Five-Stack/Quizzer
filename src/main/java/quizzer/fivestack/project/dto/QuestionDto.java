package quizzer.fivestack.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import quizzer.fivestack.project.enums.Difficulty;

public class QuestionDto {

    @NotBlank(message = "Question is required")
    @Size(max = 1000)
    private String questionContent;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    public QuestionDto(){

    }

    public QuestionDto(String questionContent, Difficulty difficulty){
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
