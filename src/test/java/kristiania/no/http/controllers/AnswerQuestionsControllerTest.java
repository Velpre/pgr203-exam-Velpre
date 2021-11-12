package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.user.UserDao;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


public class AnswerQuestionsControllerTest {
    AnswerDao answerDao = new AnswerDao(TestData.testDataSource());
    UserDao userDao = new UserDao(TestData.testDataSource());
    AnswerQuestionsController answerQuestionsController = new AnswerQuestionsController(answerDao, userDao);

    @Test
    void shouldAnswerQuestionsWithExistingUser() throws IOException, SQLException {
        HttpMessage httpPostMessage1 = new HttpMessage("POST HTTP/1.1 200", "newUser=&existingUsers=1&1=Answer1&2=Answer2");
        answerQuestionsController.handle(httpPostMessage1);
        assertThat(answerDao.retrieveFromQuestionId(1))
                .extracting(Answer::getAnswer)
                .contains("Answer1");
        assertThat(answerDao.retrieveFromQuestionId(2))
                .extracting(Answer::getAnswer)
                .contains("Answer2");
    }

    @Test
    void shouldAnswerQuestionsWithNewUser() throws IOException, SQLException {
        HttpMessage httpPostMessage1 = new HttpMessage("POST HTTP/1.1 200", "newUser=TestUser&existingUsers=1&1=Answer3&2=Answer4");
        answerQuestionsController.handle(httpPostMessage1);
        assertThat(answerDao.retrieveFromQuestionId(1))
                .extracting(Answer::getAnswer)
                .contains("Answer3");
    }
}
