package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.survey.SurveyDao;

import java.sql.SQLException;
import java.util.Map;

public class DeleteSurveyController implements HttpController {
    private final SurveyDao surveyDao;

    public DeleteSurveyController(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @Override
    public String getPath() {
        return "/api/deleteSurvey";
    }


    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "";
        if (request.startLine.startsWith("POST")) {
            Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
            surveyDao.delete(Long.parseLong(queryMap.get("survey")));
            responseText = "You have removed survey with id: " + queryMap.get("survey") + ".";
        }
        return new HttpMessage("HTTP/1.1 303", responseText, "../editSurvey.html");
    }


}
