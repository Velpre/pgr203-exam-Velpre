package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.survey.Survey;
import kristiania.no.jdbc.survey.SurveyDao;
import java.sql.SQLException;
import java.util.Map;


public class NewSurveyController implements HttpController {
    private final SurveyDao surveyDao;

    public NewSurveyController(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
        Survey s = new Survey(queryMap.get("title"));
        surveyDao.save(s);
        String responseText = "You have added: Title: " + s.getName() + ".";
        return new HttpMessage("HTTP/1.1 200", responseText);
    }
}
