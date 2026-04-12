package quizzer.fivestack.project.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import quizzer.fivestack.project.enums.Difficulty;

public class QuestionDto {

    private Long id;

    @NotBlank(message = "Question is required")
    @Size(max = 1000)
    private String questionContent;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    private List<AnswerDto> answers;

    public QuestionDto(){

    }

    public QuestionDto(String questionContent, Difficulty difficulty){
        this.questionContent = questionContent;
        this.difficulty = difficulty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<AnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDto> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "QuestionDto [questionContent=" + questionContent + ", difficulty=" + difficulty + "]";
    }

    
}
