package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
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
    private HttpMessage httpMessage;

    public ListQuestionsController(QuestionDao questionDao, OptionDao optionDao) {
        this.questionDao = questionDao;
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        String responseText = "";
        Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
        if (request.startLine.startsWith("POST")) {
            surveyId = Integer.parseInt(queryMap.get("survey"));
            responseText = "Selected survey with id: " + surveyId;
            httpMessage = new HttpMessage("HTTP/1.1 303", responseText, "../takeSurvey.html");
        } else {
            responseText += "<p>Create New user:</p>";
            responseText += "<input type=\"text\" id=\"userName\" name=\"newUser\" label =\"Username:\"> </input><br>";

            responseText += "<p>Chose one of existing users<p>";
            responseText += "<p><label>Select user <select name=\"existingUsers\" id=\"existingUsers\"></select></label></p>";

            String optionsString = "";

            for (Question question : questionDao.retrieveFromSurveyId(surveyId)) {
                responseText += "<h3>" + question.getTitle() + "</h3>\r\n";

                for (Option option : optionDao.retrieveFromQuestionId(question.getId())) {
                    responseText += "<label class =\"radioLabel\"><input required type=\"radio\" id=\"myRange\" name=\"" +
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
            httpMessage = new HttpMessage("HTTP/1.1 200", responseText);
        }

        return httpMessage;
    }
}