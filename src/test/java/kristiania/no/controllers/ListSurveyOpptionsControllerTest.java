package kristiania.no.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.http.controllers.ListSurveyOptionsController;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.survey.SurveyDao;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


import java.sql.SQLException;

public class ListSurveyOpptionsControllerTest {
    @Test
    void shouldListSurveys() throws SQLException {
        SurveyDao surveyDao = new SurveyDao(TestData.testDataSource());
        HttpMessage httpMessage = new HttpMessage("GET HTTP/1.1 200", "");
        ListSurveyOptionsController listSurveyOptionsController = new ListSurveyOptionsController(surveyDao);
        HttpMessage response = listSurveyOptionsController.handle(httpMessage);
        assertThat(response.messageBody).contains("<option value=1>Client Questionnaire</option>");
    }
}
