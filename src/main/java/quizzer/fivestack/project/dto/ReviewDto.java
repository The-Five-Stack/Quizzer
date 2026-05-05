package quizzer.fivestack.project.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ReviewDto {
    @Min(value = 1, message = "Minimum of rating is 1")
    @Max(value = 5, message = "Maximum of rating is 5")
    private int rating;

    @NotBlank(message = "Review text cannot be empty")
    private String review;

    @NotBlank(message = "Nickname is required")
    private String nickname;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
