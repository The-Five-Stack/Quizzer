package quizzer.fivestack.project.dto;

import java.time.LocalDateTime;

public record ReviewResponseDto(Long id,
        int rating,
        String review,
        String nickname,
        LocalDateTime createdAt,
        Long quizId) {

}
