package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;

import java.sql.SQLException;
import java.util.Map;


public class AddAndListAllQuestionsController implements HttpController {

    private final QuestionDao questionDao;
    private final OptionDao optionDao;
    HttpMessage httpMessage;


    public AddAndListAllQuestionsController(QuestionDao questionDao, OptionDao optionDao) {
        this.questionDao = questionDao;
        this.optionDao = optionDao;
    }


    @Override
    public String getPath() {
        return "/api/addAndListAllQuestions";
    }


    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "";
        if (request.startLine.startsWith("POST")) {
            Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
            Question q = new Question(queryMap.get("title"), Integer.parseInt(queryMap.get("survey")));
            questionDao.save(q);

            responseText = "You have added: Question: " + q.getTitle() + "\r\n Survey: " + q.getSurveyId() + "\r\nWith options:";
            for (int i = 1; i < 6; i++) {
                Option option = new Option(queryMap.get("option" + i), (int) q.getId());
                if (option.getOptionName() != "") {
                    optionDao.save(option);
                }
                responseText += " " + option.getOptionName();
            }
            httpMessage = new HttpMessage("HTTP/1.1 303", responseText, "../editSurvey.html");
        } else if (request.startLine.startsWith("GET")) {
            for (Question question : questionDao.listAll()) {
                responseText += "<option value=" + question.getId() + ">" + question.getTitle() + "</option>";
            }
            httpMessage = new HttpMessage("HTTP/1.1 200", responseText);
        }
        return httpMessage;
    }

}