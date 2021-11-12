package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class AddAndListAllQuestionsControllerTest {
    QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
    OptionDao optionDao = new OptionDao(TestData.testDataSource());

    AddAndListAllQuestionsController addAndListAllQuestionsController = new AddAndListAllQuestionsController(questionDao, optionDao);

    @Test
    void shouldListAllQuestionsFromServer() throws SQLException {
        Question question1 = new Question("test1", 1);
        Question question2 = new Question("test2", 2);
        questionDao.save(question1);
        questionDao.save(question2);

        HttpMessage httpMessage = new HttpMessage("GET HTTP/1.1 200", "");
        HttpMessage response = addAndListAllQuestionsController.handle(httpMessage);
        assertThat(response.messageBody)
                .contains("<option value=" + question1.getId() + ">test1</option><option value=" + question2.getId() + ">test2</option>");
    }

    @Test
    void shouldListAllQuestions() throws SQLException {
        //Henter data som er lagt inn via migration.
        HttpMessage httpMessage = new HttpMessage("GET HTTP/1.1 200", "");
        HttpMessage response = addAndListAllQuestionsController.handle(httpMessage);
        assertThat(response.messageBody)
                .contains("<option value=1>How much time do you spend using facebook? (per day)</option>" +
                        "<option value=2>In the last month, what has been your biggest pain point?</option>"
                );
    }

    @Test
    void shouldPostQuestion() throws SQLException {
        HttpMessage httpMessage = new HttpMessage("POST HTTP/1.1 200", "title=testTitle&survey=1&option1=option1Test&option2=option2Test" +
                "&option3=option3Test&option4=option4Test&option5=option5Test");
        addAndListAllQuestionsController.handle(httpMessage);

        assertThat(questionDao.listAll())
                .extracting(Question::getTitle)
                .contains("testTitle");
    }
}
