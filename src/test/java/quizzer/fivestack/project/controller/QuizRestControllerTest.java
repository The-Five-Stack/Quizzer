package quizzer.fivestack.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.repository.QuizRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class QuizRestControllerTest {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception{
        quizRepository.deleteAll();
    }

    @Test
    public void getAllQuizzesReturnsEmptyListWhenNoQuizzesExist() throws Exception {
        this.mockMvc.perform(get("/api/quizzes/publishedquizz"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllQuizzesReturnsListOfQuizzesWhenPublishedQuizzesExist() throws Exception {
        Quiz quiz1 = new Quiz("English Test", "Test for beginners", "ENG001AS2AE", true);
        Quiz quiz2 = new Quiz("Math Test", "Basic Algebra", "ANA001AS2AE", true);
        quizRepository.saveAll(List.of(quiz1, quiz2));

        this.mockMvc.perform(get("/api/quizzes/publishedquizz"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name").value("English Test"))
            .andExpect(jsonPath("$[1].name").value("Math Test"));
    }

    @Test
    public void getAllQuizzesDoesNotReturnUnpublishedQuizzes() throws Exception {
        Quiz quiz1 = new Quiz("English Test", "Test for beginners", "ENG001AS2AE", true);
        Quiz quiz2 = new Quiz("Math Test", "Basic Algebra", "ANA001AS2AE", true);
        Quiz quiz3 = new Quiz("German Test", "German for the beginner", "GER001AS2AE", false);
        Quiz quiz4 = new Quiz("Python Test", "Introduction to Python", "PYT001AS2AE", false);
        quizRepository.saveAll(List.of(quiz1, quiz2, quiz3, quiz4));

        this.mockMvc.perform(get("/api/quizzes/publishedquizz"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name").value("English Test"))
            .andExpect(jsonPath("$[1].name").value("Math Test"));
    }

    @Test
    public void getAllQuizzesReturnsEmptyListWhenNoPublishedQuizzesExist() throws Exception {
        Quiz quiz3 = new Quiz("German Test", "German for the beginner", "GER001AS2AE", false);
        Quiz quiz4 = new Quiz("Python Test", "Introduction to Python", "PYT001AS2AE", false);
        quizRepository.saveAll(List.of(quiz3, quiz4));

        this.mockMvc.perform(get("/api/quizzes/publishedquizz"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }
}
