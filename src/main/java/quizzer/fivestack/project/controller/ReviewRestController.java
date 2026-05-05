package quizzer.fivestack.project.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody; 

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.domain.Review;
import quizzer.fivestack.project.dto.ReviewDto;
import quizzer.fivestack.project.repository.QuizRepository;
import quizzer.fivestack.project.repository.ReviewRepository;

@RestController
@RequestMapping("/api/quizzes")
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
            @ApiResponse(responseCode = "200", description = "Review created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or quiz not published"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    @PostMapping("/{quizId}/reviews")
    public Review createReview(@PathVariable Long quizId, @Valid @RequestBody ReviewDto dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Quiz not found"));

        if (!quiz.getIsPublished()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot review an unpublished quiz");
        }

        Review review = new Review();
        review.setRating(dto.getRating());
        review.setReview(dto.getReview());
        review.setNickname(dto.getNickname());
        review.setCreatedAt(LocalDateTime.now());
        review.setQuiz(quiz);

        return reviewRepository.save(review);

    }
}
