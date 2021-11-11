package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.user.User;
import kristiania.no.jdbc.user.UserDao;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Map;


public class AnswerQuestionsController implements HttpController {
    private final AnswerDao answerDao;
    private final UserDao userDao;
    private HttpMessage httpMessage;

    public AnswerQuestionsController(AnswerDao answerDao, UserDao userDao) {
        this.answerDao = answerDao;
        this.userDao = userDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException {
        String responseText;
        if (request.startLine.startsWith("POST")) {
            Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
            //Finner ut av om bruker lager ny user eller velger eksisterende
            User newUser;
            User existingUser;
            if (queryMap.get("newUser").equals("")) {
                existingUser = userDao.retrieve(Long.parseLong(queryMap.get("existingUsers")));
                queryMap.remove("newUser");
                queryMap.remove("existingUsers");
                //Methode som lagrer answers
                saveAnswers(queryMap, existingUser);
            } else {
                newUser = new User(queryMap.get("newUser"));
                userDao.save(newUser);
                queryMap.remove("newUser");
                queryMap.remove("existingUsers");
                //Methode som lagrer answers
                saveAnswers(queryMap, newUser);
            }

            responseText = "Completed";
            httpMessage = new HttpMessage("HTTP/1.1 303", responseText, "../takeSurvey.html");
        } else if (request.startLine.startsWith("GET")) {
            responseText = "Questions answered";
            httpMessage = new HttpMessage("HTTP/1.1 200", responseText);
        }
        return httpMessage;
    }


    public void saveAnswers(Map<String, String> queryMap, User user) throws SQLException {
        Object[] keySet = queryMap.keySet().toArray();

        for (Object o : keySet) {
            Answer a = new Answer(queryMap.get(o), Integer.parseInt((String) o), (int) user.getId());
            answerDao.save(a);
        }
    }
}
