package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.user.User;
import kristiania.no.jdbc.user.UserDao;

import java.sql.SQLException;
import java.util.Map;

import static kristiania.no.http.HttpServer.parseRequestParameters;

public class AnswerQuestionsController implements HttpController {
    private final AnswerDao answerDao;
    private final UserDao userDao;

    public AnswerQuestionsController(AnswerDao answerDao, UserDao userDao) {
        this.answerDao = answerDao;
        this.userDao = userDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> queryMap = parseRequestParameters(request.messageBody);
        User user = new User(queryMap.get("userName"));

        StringBuilder responseText = new StringBuilder("You ("+ user.getUserName() +") have answered: ");

        userDao.save(user);
        queryMap.remove("userName");

        Object[] keySet = queryMap.keySet().toArray();

        for (Object o : keySet) {
            Answer a = new Answer(queryMap.get(o), Integer.parseInt((String) o), (int) user.getId());
            answerDao.save(a);
            responseText.append(" ").append(a.getAnswer());
        }

        responseText.append(" with user").append(user.getUserName());
        return new HttpMessage("HTTP/1.1 200", responseText.toString());
    }
}
