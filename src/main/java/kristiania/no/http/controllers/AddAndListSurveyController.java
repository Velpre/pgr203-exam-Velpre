package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.survey.Survey;
import kristiania.no.jdbc.survey.SurveyDao;

import java.sql.SQLException;
import java.util.Map;


public class AddAndListSurveyController implements HttpController {
    public static final String PATH = "/api/addAndListSurvey";
    private final SurveyDao surveyDao;
    HttpMessage httpMessage;

    public AddAndListSurveyController(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "";
        if (request.startLine.startsWith("POST")) {
            Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
            Survey survey = new Survey(queryMap.get("title"));
            surveyDao.save(survey);
            responseText = "You have added: Title: " + survey.getName() + ".";
            httpMessage = new HttpMessage("HTTP/1.1 303", responseText, "../editSurvey.html");
        } else if (request.startLine.startsWith("GET")) {
            for (Survey survey : surveyDao.listAll()) {
                responseText += "<option value=" + survey.getId() + ">" + survey.getName() + "</option>";
            }
            httpMessage = new HttpMessage("HTTP/1.1 200", responseText);
        }
        return httpMessage;
    }
}
