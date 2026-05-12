package quizzer.fivestack.project.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import quizzer.fivestack.project.domain.Answer;
import quizzer.fivestack.project.domain.Category;
import quizzer.fivestack.project.domain.Question;
import quizzer.fivestack.project.domain.Quiz;
import quizzer.fivestack.project.domain.StudentAnswer;
import quizzer.fivestack.project.domain.User;
import quizzer.fivestack.project.enums.Difficulty;
import quizzer.fivestack.project.repository.AnswerRepository;
import quizzer.fivestack.project.repository.CategoryRepository;
import quizzer.fivestack.project.repository.QuizRepository;
import quizzer.fivestack.project.repository.StudentAnswerRepository;
import quizzer.fivestack.project.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudentAnswerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentAnswerRepository studentAnswerRepository;

    private User teacher;

    @BeforeEach
    void setUp() {
        teacher = userRepository.findByUsername("teacher").orElseThrow();
        studentAnswerRepository.deleteAll();
    }

    @Test
    void createAnswerSavesAnswerForPublishedQuiz() throws Exception {
        Answer publishedQuizAnswerOption = createAnswerOptionForQuiz(true);
        long countBefore = countStudentAnswers();
        String requestBody = "{\"answerOptionId\":" + publishedQuizAnswerOption.getAnswerId() + "}";

        mockMvc.perform(post("/api/student-answers")
                        .with(httpBasic("teacher", "teacher123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").exists())
                .andExpect(jsonPath("$.feedback").exists());

        long countAfter = countStudentAnswers();
        org.junit.jupiter.api.Assertions.assertEquals(countBefore + 1, countAfter);

        StudentAnswer savedStudentAnswer = studentAnswerRepository.findAll().iterator().next();
        org.junit.jupiter.api.Assertions.assertEquals(teacher.getId(), savedStudentAnswer.getUser().getId());
        org.junit.jupiter.api.Assertions.assertEquals(publishedQuizAnswerOption.getAnswerId(),
                savedStudentAnswer.getSelectedAnswer().getAnswerId());
        org.junit.jupiter.api.Assertions.assertEquals(publishedQuizAnswerOption.getQuestion().getQuestionId(),
                savedStudentAnswer.getQuestion().getQuestionId());
        org.junit.jupiter.api.Assertions.assertEquals(publishedQuizAnswerOption.getQuestion().getQuiz().getQuizId(),
                savedStudentAnswer.getQuiz().getQuizId());
        org.junit.jupiter.api.Assertions.assertEquals(publishedQuizAnswerOption.getIsCorrect(),
                savedStudentAnswer.getIsCorrect());
    }

    @Test
    void createAnswerDoesNotSaveAnswerWithoutAnswerOption() throws Exception {
        long countBefore = countStudentAnswers();

        mockMvc.perform(post("/api/student-answers")
                        .with(httpBasic("teacher", "teacher123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        long countAfter = countStudentAnswers();
        org.junit.jupiter.api.Assertions.assertEquals(countBefore, countAfter);
    }

    @Test
    void createAnswerDoesNotSaveAnswerForNonExistingAnswerOption() throws Exception {
        long countBefore = countStudentAnswers();
        String requestBody = "{\"answerOptionId\":999999}";

        mockMvc.perform(post("/api/student-answers")
                        .with(httpBasic("teacher", "teacher123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Answer option not found"));

        long countAfter = countStudentAnswers();
        org.junit.jupiter.api.Assertions.assertEquals(countBefore, countAfter);
    }

    @Test
    void createAnswerDoesNotSaveAnswerForNonPublishedQuiz() throws Exception {
        Answer unpublishedQuizAnswerOption = createAnswerOptionForQuiz(false);
        long countBefore = countStudentAnswers();
        Long unpublishedAnswerId = unpublishedQuizAnswerOption.getAnswerId();
        String requestBody = "{\"answerOptionId\":" + unpublishedAnswerId + "}";

        mockMvc.perform(post("/api/student-answers")
                        .with(httpBasic("teacher", "teacher123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Quiz is not published"));

        long countAfter = countStudentAnswers();
        org.junit.jupiter.api.Assertions.assertEquals(countBefore, countAfter);
    }

    private Answer createAnswerOptionForQuiz(boolean isPublished) {
        Category category = categoryRepository.findAll().iterator().next();
        Quiz quiz = new Quiz("Test Quiz " + isPublished, "desc", "TMP101AS1AE", isPublished);
        quiz.setOwner(teacher);
        quiz.setCategory(category);
        quiz.setQuestions(new ArrayList<>());

        Question question = new Question("Q?", Difficulty.EASY, quiz);
        question.setAnswers(new ArrayList<>());
        quiz.getQuestions().add(question);

        Answer answer = new Answer("Option A", true, question);
        question.getAnswers().add(answer);

        Quiz savedQuiz = quizRepository.save(quiz);
        return savedQuiz.getQuestions().get(0).getAnswers().get(0);
    }

    private long countStudentAnswers() {
        long count = 0;
        for (StudentAnswer ignored : studentAnswerRepository.findAll()) {
            count++;
        }
        return count;
    }
}
