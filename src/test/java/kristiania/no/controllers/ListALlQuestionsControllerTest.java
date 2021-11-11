package kristiania.no.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.http.controllers.ListAllQuestionsController;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.question.QuestionDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class ListALlQuestionsControllerTest {

    @Test
    void shouldListAllQuestions() throws SQLException {
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        HttpMessage httpMessage = new HttpMessage("GET HTTP/1.1 200", "");
        ListAllQuestionsController listAllQuestionsController = new ListAllQuestionsController(questionDao);

        HttpMessage response = listAllQuestionsController.handle(httpMessage);
        assertThat(response.messageBody).contains("<option value=1>How much time do you spend using facebook? (per day)</option>");
    }
}
