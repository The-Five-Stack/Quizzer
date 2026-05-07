package quizzer.fivestack.project.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.domain.Review;
import quizzer.fivestack.project.dto.ReviewDto;
import quizzer.fivestack.project.dto.ReviewResponseDto;
import quizzer.fivestack.project.dto.QuizReviewSummaryDto;
import quizzer.fivestack.project.repository.QuizRepository;
import quizzer.fivestack.project.repository.ReviewRepository;

@RestController
@Tag(name = "Reviews", description = "Operations for managing reviews within quizzes")
@CrossOrigin(origins = { "http://localhost:5173", "https://quizzer-ui.onrender.com" })
public class ReviewRestController {

        private final QuizRepository quizRepository;
        private final ReviewRepository reviewRepository;

        public ReviewRestController(QuizRepository quizRepository, ReviewRepository reviewRepository) {
                this.quizRepository = quizRepository;
                this.reviewRepository = reviewRepository;
        }

        @Operation(summary = "Create a review for a quiz")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Review created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input / Validation error", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Quiz is not published", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Quiz not found", content = @Content)
        })
        @PostMapping("/api/quizzes/{quizId}/reviews")
        @ResponseStatus(HttpStatus.CREATED)
        public ReviewResponseDto createReview(@PathVariable Long quizId, @Valid @RequestBody ReviewDto dto) {

                Quiz quiz = quizRepository.findById(quizId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Quiz not found"));

                if (!quiz.getIsPublished()) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Cannot review an unpublished quiz");
                }

                Review review = new Review();
                review.setRating(dto.getRating());
                review.setReview(dto.getReview());
                review.setNickname(dto.getNickname());
                review.setCreatedAt(LocalDateTime.now());
                review.setQuiz(quiz);

                Review savedReview = reviewRepository.save(review);

                return new ReviewResponseDto(
                                savedReview.getId(),
                                savedReview.getRating(),
                                savedReview.getReview(),
                                savedReview.getNickname(),
                                savedReview.getCreatedAt(),
                                quiz.getQuizId());
        }

        @Operation(summary = "Get all reviews and statistics for a quiz")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews"),
                        @ApiResponse(responseCode = "404", description = "Quiz not found", content = @Content)
        })
        @GetMapping("/api/quizzes/{quizId}/reviews")
        public QuizReviewSummaryDto getQuizReviews(@PathVariable Long quizId) {

                // Check if quiz exists
                if (!quizRepository.existsById(quizId)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");
                }

                List<Review> reviews = reviewRepository.findByQuizQuizId(quizId);

                //Calculate the raw average using doubles
                double rawAverage = reviews.stream()
                                .mapToDouble(Review::getRating)
                                .average()
                                .orElse(0.0);

                // Round to 1 decimal place (e.g., 3.3333 -> 3.3)
                double average = BigDecimal.valueOf(rawAverage)
                                .setScale(1, RoundingMode.HALF_UP)
                                .doubleValue();
                long total = reviews.size();

                // Map entities to ReviewResponseDto (to keep Swagger clean)
                List<ReviewResponseDto> reviewDtos = reviews.stream()
                                .map(r -> new ReviewResponseDto(
                                                r.getId(),
                                                r.getRating(),
                                                r.getReview(),
                                                r.getNickname(),
                                                r.getCreatedAt(),
                                                quizId))
                                .toList();

                return new QuizReviewSummaryDto(average, total, reviewDtos);
        }

        @Operation(summary = "Delete a review by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
        })
        @DeleteMapping("/api/reviews/{reviewId}")
        public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
                Optional<Review> reviewOpt = reviewRepository.findById(reviewId);

                if (reviewOpt.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }

                reviewRepository.delete(reviewOpt.get());
                return ResponseEntity.noContent().build();
        }
}