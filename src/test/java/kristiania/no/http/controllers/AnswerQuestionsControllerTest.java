package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.survey.Survey;
import kristiania.no.jdbc.user.UserDao;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;

public class AnswerQuestionsControllerTest {

    AnswerDao answerDao = new AnswerDao(TestData.testDataSource());
    UserDao userDao = new UserDao(TestData.testDataSource());

    AnswerQuestionsController answerQuestionsController = new AnswerQuestionsController(answerDao, userDao);

    /*
    @Test
    void shouldSaveAnswersController() throws SQLException, UnsupportedEncodingException {
        Answer answer = new Answer("answer1", 1, 1);
        HttpMessage httpMessage = new HttpMessage("POST HTTP/1.1 200", "");
        httpMessage.messageBody = "1=About 1 - 2 minutes";
        answerQuestionsController.handle(httpMessage);

        assertThat(answerDao.retrieveFromQuestionId(1)).extracting(Answer::getAnswer).contains("About 1 - 2 minutes");
    }

     */


}
