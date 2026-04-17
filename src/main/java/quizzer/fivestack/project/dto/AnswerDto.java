package quizzer.fivestack.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public class AnswerDto {

    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
