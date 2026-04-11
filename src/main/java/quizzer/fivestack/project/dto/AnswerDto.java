package quizzer.fivestack.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public class AnswerDto {

    @NotBlank
    @Size(max = 500)
    private String content;

    @NotNull
    private Boolean correct;

    public AnswerDto(){

    }

    public AnswerDto(String content, Boolean correct){
        this.content = content;
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    @Override
    public String toString() {
        return "AnswerDto [content=" + content + ", correct=" + correct + "]";
    }

    

}
