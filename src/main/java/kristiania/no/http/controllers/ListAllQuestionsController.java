package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;

import java.sql.SQLException;

public class ListAllQuestionsController implements HttpController {
    private final QuestionDao questionDao;

    public ListAllQuestionsController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "";
        for (Question question : questionDao.listAll()) {
            responseText += "<option value=" + question.getId() + ">" + question.getTitle() + "</option>";
        }
        return new HttpMessage("HTTP/1.1 200", responseText);
    }
}
