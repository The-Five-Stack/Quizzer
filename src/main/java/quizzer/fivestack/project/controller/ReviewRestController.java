package quizzer.fivestack.project.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import quizzer.fivestack.project.domain.Review;
import quizzer.fivestack.project.repository.ReviewRepository;


@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Operations for managing reviews within quizzes")
@CrossOrigin(origins = { "http://localhost:5173", "https://quizzer-ui.onrender.com" })
public class ReviewRestController {

        private final ReviewRepository reviewRepository;

        public ReviewRestController(ReviewRepository reviewRepository) {
                this.reviewRepository = reviewRepository;
        }

        @Operation(summary = "Delete a review by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
        })
        @DeleteMapping("/{reviewId}")
        public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
                Optional<Review> reviewOpt = reviewRepository.findById(reviewId);

                if (reviewOpt.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }

                reviewRepository.delete(reviewOpt.get());
                return ResponseEntity.noContent().build();
        }
}