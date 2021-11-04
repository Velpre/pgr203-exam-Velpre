package kristiania.no.http;

import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.question.QuestionDao;
import kristiania.no.jdbc.user.User;
import kristiania.no.jdbc.user.UserDao;

import java.sql.SQLException;
import java.util.Map;

import static kristiania.no.http.HttpServer.parseRequestParameters;

public class AnswerQuestionsController implements HttpController{
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;
    private final UserDao userDao;
    ;


    public AnswerQuestionsController(QuestionDao questionDao, AnswerDao answerDao, UserDao userDao) {
        this.questionDao = questionDao;
        this.answerDao = answerDao;
        this.userDao = userDao;
    }


    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "You have added: ";

        Map<String, String> queryMap = parseRequestParameters(request.messageBody);
        User user = new User(queryMap.get("userName"));
        userDao.save(user);
        queryMap.remove("userName");

        Object[] keySet = queryMap.keySet().toArray();

        for (int i = 0; i < keySet.length; i++) {
            Answer a = new Answer(queryMap.get(keySet[i]), Integer.parseInt((String) keySet[i]), (int) user.getId());
            answerDao.save(a);
            responseText += " " + a.getAnswer();
        }

        responseText += " with user" + user.getUserName();
        return new HttpMessage("HTTP/1.1 200", responseText);
    }
}
