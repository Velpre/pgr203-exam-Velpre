package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.question.QuestionDao;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class ListAnswersControllerTest {
    DataSource dataSource = TestData.testDataSource();
    QuestionDao questionDao = new QuestionDao(dataSource);
    AnswerDao answerDao = new AnswerDao(dataSource);
    ListAnswersController listAnswersController = new ListAnswersController(questionDao, answerDao);

    @Test
    void shouldSaveAndListAnswersWithOneUser() throws SQLException {
        Answer answer = new Answer("ThisIsTheAnswerToTest", 1, 1);
        answerDao.save(answer);
        Answer answer2 = new Answer("ShouldNotAppear", 1, 2);
        answerDao.save(answer2);
        HttpMessage httpPostMessage = new HttpMessage("POST HTTP/1.1 200", "survey=1&user=1");
        listAnswersController.handle(httpPostMessage);
        HttpMessage httpMessage = new HttpMessage("GET HTTP/1.1 200", "");
        HttpMessage response = listAnswersController.handle(httpMessage);
        assertThat(response.messageBody)
                .contains("ThisIsTheAnswerToTest")
                .doesNotContain("ShouldNotAppear");
    }

    @Test
    void shouldSaveAndListAnswers() throws SQLException {
        Answer answer = new Answer("ThisIsTheAnswerToTest", 1, 1);
        answerDao.save(answer);
        Answer answer2 = new Answer("ShouldAlsoAppear", 1, 2);
        answerDao.save(answer2);

        HttpMessage httpPostMessage = new HttpMessage("POST HTTP/1.1 200", "survey=1&allUsers=On&user=1");
        listAnswersController.handle(httpPostMessage);
        HttpMessage httpMessage = new HttpMessage("GET HTTP/1.1 200", "");
        HttpMessage response = listAnswersController.handle(httpMessage);
        assertThat(response.messageBody).
                contains("ThisIsTheAnswerToTest", "ShouldAlsoAppear");
    }
}
