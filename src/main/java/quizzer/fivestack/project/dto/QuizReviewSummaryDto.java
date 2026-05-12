package quizzer.fivestack.project.dto;

import java.util.List;

public record QuizReviewSummaryDto(
        double averageRating,
        long totalReviews,
        List<ReviewResponseDto> reviews) {
}