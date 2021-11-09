package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.user.User;
import kristiania.no.jdbc.user.UserDao;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Map;

import static kristiania.no.http.HttpServer.parseRequestParameters;

public class AnswerQuestionsController implements HttpController {
    private final AnswerDao answerDao;
    private final UserDao userDao;
    String responseText;

    public AnswerQuestionsController(AnswerDao answerDao, UserDao userDao) {
        this.answerDao = answerDao;
        this.userDao = userDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException {
        if (request.messageBody != null) {
            responseText = "You have answered: ";

            Map<String, String> queryMap = parseRequestParameters(request.messageBody);
            //Finner ut av om bruker lager ny user eller velger eksisterende
            User newUser;
            User existingUser;
            if (queryMap.get("newUser") == "") {
                existingUser = userDao.retrieve(Long.parseLong(queryMap.get("existingUsers")));
                queryMap.remove("newUser");
                queryMap.remove("existingUsers");
                Object[] keySet = queryMap.keySet().toArray();

                for (int i = 0; i < keySet.length; i++) {
                    Answer a = new Answer(queryMap.get(keySet[i]), Integer.parseInt((String) keySet[i]), (int) existingUser.getId());
                    answerDao.save(a);

                    responseText += " " + a.getAnswer();
                }
                responseText += " with user " + existingUser.getUserName();
            } else {
                newUser = new User(queryMap.get("newUser"));
                userDao.save(newUser);
                queryMap.remove("newUser");
                queryMap.remove("existingUsers");

                Object[] keySet = queryMap.keySet().toArray();

                for (int i = 0; i < keySet.length; i++) {
                    Answer a = new Answer(queryMap.get(keySet[i]), Integer.parseInt((String) keySet[i]), (int) newUser.getId());
                    answerDao.save(a);

                    responseText += " " + a.getAnswer();
                }

                responseText += " with user " + newUser.getUserName();
            }
        }
        return new HttpMessage("HTTP/1.1 200", responseText.toString());
    }
}
