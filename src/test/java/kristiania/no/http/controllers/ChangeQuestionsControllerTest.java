package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeQuestionsControllerTest {
    OptionDao optionDao = new OptionDao(TestData.testDataSource());
    QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
    ChangeQuestionController changeQuestionController = new ChangeQuestionController(questionDao, optionDao);

    @Test
    void shouldUpdateQuestion() throws SQLException {
        Question question = new Question("TestChangeQuestion", 1);
        questionDao.save(question);
        assertThat(questionDao.listAll())
                .extracting(Question::getTitle)
                .contains("TestChangeQuestion");
        HttpMessage httpMessage = new HttpMessage("POST HTTP/1.1 200",
                "question=" + question.getId() + "&title=EditedName" +
                        "&option1=TestOption1&option2=TestOption2&option3=" +
                        "&option4=TestOption4&option5=TestOption5");
        changeQuestionController.handle(httpMessage);
        assertThat(questionDao.listAll())
                .extracting(Question::getTitle)
                .contains("EditedName")
                .doesNotContain("TestChangeQuestion");
        assertThat(optionDao.retrieveFromQuestionId(question.getId()))
                .extracting(Option::getOptionName)
                .contains("TestOption1", "TestOption2",
                        "TestOption4", "TestOption5");
    }
}
