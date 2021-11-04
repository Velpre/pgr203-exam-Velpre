package kristiania.no.http;

import kristiania.no.jdbc.answer.Answer;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;
import kristiania.no.jdbc.user.User;
import kristiania.no.jdbc.user.UserDao;

import java.sql.SQLException;
import java.util.Map;

import static kristiania.no.http.HttpServer.parseRequestParameters;

public class NewQuestionController implements HttpController{
    private final QuestionDao questionDao;
    private final OptionDao optionDao;

    public NewQuestionController(QuestionDao questionDao, OptionDao optionDao) {
        this.questionDao = questionDao;
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> queryMap = parseRequestParameters(request.messageBody);
        Question q = new Question(queryMap.get("title"), Integer.parseInt(queryMap.get("survey")));
        questionDao.save(q);
        Option o = new Option(queryMap.get("option1"), (int) q.getId());
        Option o1 = new Option(queryMap.get("option2"), (int) q.getId());
        Option o2 = new Option(queryMap.get("option3"), (int) q.getId());
        optionDao.save(o);
        optionDao.save(o1);
        optionDao.save(o2);
        String responseText = "You have added: Question: " + q.getTitle()  + " Survey: " + q.getSurveyId() + " Options:" + o.getOptionName() + " " + o1.getOptionName() + " " + o2.getOptionName() + ".";


        return new HttpMessage("HTTP/1.1 200", responseText);
    }
}
