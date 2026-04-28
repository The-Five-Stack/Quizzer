package quizzer.fivestack.project.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quizzer.fivestack.project.dto.StudentAnswerRequest;
import quizzer.fivestack.project.service.StudentAnswerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/student-answers")
@Tag(name = "Student Answers", description = "Operations for submitting and managing student answers")
@CrossOrigin(origins = { "http://localhost:5173", "https://quizzer-ui.onrender.com" })
public class StudentAnswerRestController {
    private final StudentAnswerService service;

    public StudentAnswerRestController(StudentAnswerService studentAnswerService) {
        this.service = studentAnswerService;
    }

    @Operation(summary = "Submit an answer", description = "Submits a student's answer for a question. Allows redo by replacing the previous answer for the same question.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Answer submitted successfully with correctness feedback"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Quiz is not published"),
            @ApiResponse(responseCode = "404", description = "Answer option or User not found")
    })
    // Submit or redo an answer for a question
    @PostMapping
    public ResponseEntity<?> submitAnswer(@Valid @RequestBody StudentAnswerRequest request, Principal principal) {
        try {
            return ResponseEntity.ok(service.submitAnswer(request, principal));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
