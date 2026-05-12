package quizzer.fivestack.project.dto;

import quizzer.fivestack.project.enums.Difficulty;

public class QuestionResultDto {
    private Long questionId;
    private String questionText;
    private Difficulty questionDifficulty;
    private int totalAnswers;
    private int correctAnswers;
    private int wrongAnswers;
    private double correctPercentage;

    public QuestionResultDto(Long questionId, String questionText, Difficulty questionDifficulty,
            int totalAnswers, int correctAnswers) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.questionDifficulty = questionDifficulty;
        this.totalAnswers = totalAnswers;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = totalAnswers - correctAnswers;
        this.correctPercentage = totalAnswers == 0 ? 0
                : Math.round((correctAnswers * 100.0 / totalAnswers) * 10.0) / 10.0;
    }

    // Getters
    public Long getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public Difficulty getQuestionDifficulty() {
        return questionDifficulty;
    }

    public int getTotalAnswers() {
        return totalAnswers;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public double getCorrectPercentage() {
        return correctPercentage;
    }

    @Override
    public String toString() {
        return "QuestionResultDto [questionId=" + questionId + ", questionText=" + questionText +
                ", questionDifficulty=" + questionDifficulty + ", totalAnswers=" + totalAnswers + ", correctAnswers="
                + correctAnswers + "]";
    }
}