package quizzer.fivestack.project.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import quizzer.fivestack.project.domain.Answer;
import quizzer.fivestack.project.domain.Category;
import quizzer.fivestack.project.domain.Question;
import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.domain.User;
import quizzer.fivestack.project.enums.Difficulty;
import quizzer.fivestack.project.repository.CategoryRepository;
import quizzer.fivestack.project.repository.QuizRepository;
import quizzer.fivestack.project.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class QuestionRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User teacher;
    private Category agileCategory;

    @BeforeEach
    void setUp() {
        // Fetch the seeded data from CommandLineRunner
        teacher = userRepository.findByUsername("teacher2").orElseThrow();
        agileCategory = categoryRepository.findAll().iterator().next();
    }

    @Test
    public void getQuestionsByQuizIdReturnsEmptyListWhenQuizDoesNotHaveQuestions() throws Exception {
        // Arrange: Save a NEW quiz without any questions
        Quiz emptyQuiz = new Quiz("Empty Java Quiz", "No questions yet", "JAVA001AS1AE", true);
        emptyQuiz.setOwner(teacher);
        emptyQuiz.setCategory(agileCategory);
        Quiz savedQuiz = quizRepository.save(emptyQuiz);

        // Act & Assert
        this.mockMvc.perform(get("/api/quizzes/" + savedQuiz.getQuizId())
                .with(httpBasic("teacher2", "teacher123")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questions", hasSize(0)));
    }

    @Test
    public void getQuestionsByQuizIdReturnsListOfQuestionsWhenQuizHasQuestionsWithDefaultQuiz() throws Exception {
        // Arrange: Use the seeded "The Scrum Framework" quiz (which has 2 questions)
        Quiz seededQuiz = ((List<Quiz>) quizRepository.findAll()).stream()
                .filter(q -> q.getQuizName().equals("The Scrum Framework"))
                .findFirst()
                .orElseThrow();

        // Act & Assert
        this.mockMvc.perform(get("/api/quizzes/" + seededQuiz.getQuizId())
                .with(httpBasic("teacher", "teacher123")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questions", hasSize(2)))
                // Check question content
                .andExpect(jsonPath("$.questions[0].questionContent").exists())
                // Check that answer options are present for the questions
                .andExpect(jsonPath("$.questions[0].answers", hasSize(2)))
                .andExpect(jsonPath("$.questions[0].answers[0].content").exists());
    }

    @Test
    public void getQuestionsByQuizIdReturnsErrorWhenQuizDoesNotExist() throws Exception {
        // Act & Assert: Request an ID that was never saved
        this.mockMvc.perform(get("/api/quizzes/9999")
                .with(httpBasic("teacher2", "teacher123")))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void getQuestionsByQuizIdReturnsListOfQuestionsWhenQuizHasQuestions() throws Exception {
        // 1. Arrange: Create the Quiz
        Quiz newQuiz = new Quiz("Manual Test Quiz", "Testing full graph", "TES101AS1AE", true);
        newQuiz.setOwner(teacher);
        newQuiz.setCategory(agileCategory);
        newQuiz.setQuestions(new ArrayList<>()); // Initialize the list

        // 2. Arrange: Create a Question
        Question question = new Question();
        question.setQuestionContent("What is the capital of Finland?");
        question.setDifficulty(Difficulty.EASY);
        question.setQuiz(newQuiz);
        newQuiz.getQuestions().add(question);

        // 3. Arrange: Create an Answer
        Answer answer = new Answer();
        answer.setAnswerContent("Helsinki");
        answer.setIsCorrect(true);
        answer.setQuestion(question);

        question.setAnswers(new ArrayList<>());
        question.getAnswers().add(answer);

        // 4. Save: Saving the Quiz will cascade and save questions/answers
        // (Assuming CascadeType.ALL is set in your entities)
        Quiz savedQuiz = quizRepository.save(newQuiz);

        // 5. Act & Assert
        this.mockMvc.perform(get("/api/quizzes/" + savedQuiz.getQuizId())
                .with(httpBasic("teacher2", "teacher123")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Manual Test Quiz"))
                .andExpect(jsonPath("$.questions", hasSize(1)))
                .andExpect(jsonPath("$.questions[0].questionContent").value("What is the capital of Finland?"))
                // Verify the nested answer exists
                .andExpect(jsonPath("$.questions[0].answers", hasSize(1)))
                .andExpect(jsonPath("$.questions[0].answers[0].content").value("Helsinki"));
    }
}