package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.survey.Survey;
import kristiania.no.jdbc.survey.SurveyDao;

import java.sql.SQLException;

public class ListSurveyOptionsController implements HttpController {
    private final SurveyDao surveyDao;

    public ListSurveyOptionsController(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }


    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "";

        for (Survey survey : surveyDao.listAll()) {
            responseText += "<option value=" + survey.getId() + ">" + survey.getName() + "</option>";
        }

        return new HttpMessage("HTTP/1.1 200", responseText);
    }
}
