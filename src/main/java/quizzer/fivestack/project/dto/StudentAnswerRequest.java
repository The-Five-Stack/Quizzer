package quizzer.fivestack.project.dto;

import jakarta.validation.constraints.NotNull;

public class StudentAnswerRequest {
    @NotNull(message = "answerOptionId is required")
    private Long answerOptionId;

    public Long getAnswerOptionId() {
        return answerOptionId;
    }

    public void setAnswerOptionId(Long answerOptionId) {
        this.answerOptionId = answerOptionId;
    }
}
