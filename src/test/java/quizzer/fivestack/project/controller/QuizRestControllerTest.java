package quizzer.fivestack.project.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import quizzer.fivestack.project.domain.Answer;
import quizzer.fivestack.project.domain.Question;
import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.enums.Difficulty;
import quizzer.fivestack.project.repository.QuestionRepository;
import quizzer.fivestack.project.repository.QuizRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class QuizRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @BeforeEach
    void setUp() {
        questionRepository.deleteAll();
        quizRepository.deleteAll();
    }

    @Test
    public void getQuestionsByQuizIdReturnsEmptyListWhenQuizHasNoQuestions() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setQuizName("Empty Quiz");
        quiz.setIsPublished(true);
        Quiz savedQuiz = quizRepository.save(quiz);

        this.mockMvc.perform(get("/api/quizzes/" + savedQuiz.getQuizId() + "/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getQuestionsByQuizIdReturnsListOfQuestionsWhenQuestionsExist() throws Exception {
        // 1. Arrange
        Quiz quiz = new Quiz();
        quiz.setQuizName("Java Basics");
        quiz.setIsPublished(true);
        Quiz savedQuiz = quizRepository.save(quiz);

        Question question1 = new Question();
        question1.setQuestionContent("What is Spring?");
        question1.setDifficulty(Difficulty.EASY); // Required based on your Entity
        question1.setQuiz(savedQuiz);
        
        Answer opt1 = new Answer();
        opt1.setAnswerContent("A Framework");
        opt1.setIsCorrect(true);
        opt1.setQuestion(question1);
        
        // Use setAnswers (plural) as defined in your Question entity
        question1.setAnswers(List.of(opt1));

        questionRepository.save(question1);

        // 2. Act & Assert
        this.mockMvc.perform(get("/api/quizzes/" + savedQuiz.getQuizId() + "/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                // Match the actual field names in your Question/Answer entities!
                .andExpect(jsonPath("$[0].questionContent").value("What is Spring?"))
                .andExpect(jsonPath("$[0].answers", hasSize(1)))
                .andExpect(jsonPath("$[0].answers[0].answerContent").value("A Framework"));
    }

    @Test
    public void getQuestionsByQuizIdReturnsNotFoundWhenQuizDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/api/quizzes/999/questions"))
                .andExpect(status().isNotFound());
    }
}