package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.question.QuestionDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class ListAnswersControllerTest {
    QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
    AnswerDao answerDao = new AnswerDao(TestData.testDataSource());
    ListAnswersController listAnswersController = new ListAnswersController(questionDao, answerDao);

    @Test
    void shouldListAnswers() throws SQLException {
        HttpMessage httpMessage = new HttpMessage("GET HTTP/1.1 200", "");
        HttpMessage response = listAnswersController.handle(httpMessage);
        System.out.println(response.messageBody);
    }
}
