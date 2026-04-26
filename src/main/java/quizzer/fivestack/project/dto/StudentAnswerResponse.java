package quizzer.fivestack.project.dto;

public class StudentAnswerResponse {
    private boolean isCorrect;
    private String feedback;

    public StudentAnswerResponse(boolean isCorrect, String feedback) {
        this.isCorrect = isCorrect;
        this.feedback = feedback;
    }

    // Getters
    public boolean isCorrect() {
        return isCorrect;
    }

    public String getFeedback() {
        return feedback;
    }
}
