package quizzer.fivestack.project.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quizzer.fivestack.project.dto.StudentAnswerRequest;
import quizzer.fivestack.project.service.StudentAnswerService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/student-answers")
@CrossOrigin(origins = { "http://localhost:5173", "https://quizzer-ui.onrender.com" })
public class StudentAnswerRestController {
    private final StudentAnswerService service;

    public StudentAnswerRestController(StudentAnswerService studentAnswerService) {
        this.service = studentAnswerService;
    }

    @PostMapping
    public ResponseEntity<?> submitAnswer(@Valid @RequestBody StudentAnswerRequest request, Principal principal) {
        try {
            return ResponseEntity.ok(service.submitAnswer(request, principal));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
