package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.survey.Survey;
import kristiania.no.jdbc.survey.SurveyDao;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteSurveyControllerTest {
    SurveyDao surveyDao = new SurveyDao(TestData.testDataSource());
    DeleteSurveyController deleteSurveyController = new DeleteSurveyController(surveyDao);

    @Test
    void shouldDeleteSurveyTest() throws SQLException {
        Survey survey = new Survey("TestTitle");
        surveyDao.save(survey);

        HttpMessage httpMessage = new HttpMessage("POST HTTP/1.1 200", "");
        httpMessage.messageBody = "survey=" + survey.getId();
        deleteSurveyController.handle(httpMessage);

        assertThat(surveyDao.listAll())
                .extracting(Survey::getId)
                .doesNotContain(survey.getId());
    }
}
