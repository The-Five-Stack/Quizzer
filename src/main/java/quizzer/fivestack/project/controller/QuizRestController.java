package quizzer.fivestack.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import quizzer.fivestack.project.repository.QuizRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.dto.QuizDto;

@RestController
@RequestMapping("/api/quizzes")
public class QuizRestController {
    private final QuizRepository repository;

    public QuizRestController(QuizRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/create")
    public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody QuizDto dto){
        Quiz newQuiz = new Quiz();

        newQuiz.setQuizName(dto.getName());
        newQuiz.setQuizDescription(dto.getDescription());
        newQuiz.setCourseCode(dto.getCourseCode());
        newQuiz.setIsPublished(dto.getPublished());

        Quiz saveQuiz = repository.save(newQuiz);

        return new ResponseEntity<>(saveQuiz, HttpStatus.CREATED);
    }

    
    
}


