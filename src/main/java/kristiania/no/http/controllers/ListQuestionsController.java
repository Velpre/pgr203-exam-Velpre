package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.http.HttpServer;
import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ListQuestionsController implements HttpController {
    private final QuestionDao questionDao;
    private final OptionDao optionDao;
    private int surveyId;

    public ListQuestionsController(QuestionDao questionDao, OptionDao optionDao) {
        this.questionDao = questionDao;
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        String responseText = "";
        Map<String, String> queryMap = HttpServer.parseRequestParameters(request.messageBody);
        if (queryMap.size() != 0) {
            surveyId = Integer.parseInt(queryMap.get("survey"));
        }
        responseText += "<p>Write username:</p>";
        responseText += "<input required type=\"text\" id=\"userName\" name=\"userName\" label =\"Username:\"> </input><br>";
        String optionsString = "";

        for (Question question : questionDao.retrieveFromSurveyId(surveyId)) {
            responseText += "<h3>" + question.getTitle() + "</h3>\r\n";

            for (Option option : optionDao.retrieveFromQuestionId(question.getId())) {
                responseText += "<label required class =\"radioLabel\"><input type=\"radio\" id=\"myRange\" name=\"" +
                        question.getId() + "\" value=\"" +
                        option.getOptionName() + "\">" +
                        option.getOptionName() + "</label>";
            }
            if (optionDao.retrieveFromQuestionId(question.getId()).size() == 0) {
                responseText += "<input name = \"" + question.getId() + "\" type=\"range\" min=\"1\" max=\"5\" value=\"3\" class=\"slider\" id=\"myRange\">";
            }
        }
        responseText += optionsString;

        responseText += "<br><button>Answer</button>";

        return new HttpMessage("HTTP/1.1 200", responseText);
    }
}