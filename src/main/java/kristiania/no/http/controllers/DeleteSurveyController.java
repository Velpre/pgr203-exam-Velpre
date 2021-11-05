package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.survey.SurveyDao;

import java.sql.SQLException;
import java.util.Map;

import static kristiania.no.http.HttpServer.parseRequestParameters;

public class DeleteSurveyController implements HttpController {
    private final SurveyDao surveyDao;

    public DeleteSurveyController(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }
    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "";
        if (!(request.messageBody.equals(""))) {
            Map<String, String> queryMap = parseRequestParameters(request.messageBody);
            surveyDao.delete(Integer.parseInt(queryMap.get("survey")));
            responseText = "You have removed survey with id: " + queryMap.get("survey") + ".";
        }
        return new HttpMessage("HTTP/1.1 200", responseText);
    }
}
