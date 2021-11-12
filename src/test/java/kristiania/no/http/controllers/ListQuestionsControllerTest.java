package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.QuestionDao;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ListQuestionsControllerTest {
    QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
    OptionDao optionDao = new OptionDao(TestData.testDataSource());
    ListQuestionsController listQuestionsController = new ListQuestionsController(questionDao, optionDao);

    @Test
    void shouldSaveAndListAnswersWithOneUser() throws SQLException, IOException {
        HttpMessage httpPostMessage = new HttpMessage("POST HTTP/1.1 200", "survey=1");
        listQuestionsController.handle(httpPostMessage);
        HttpMessage httpMessage = new HttpMessage("GET HTTP/1.1 200", "");
        HttpMessage response = listQuestionsController.handle(httpMessage);
        assertThat(response.messageBody)
                .contains("Create New user", "Having enough to do")
                .doesNotContain("Between 2 and 4 minutes");
    }
}
