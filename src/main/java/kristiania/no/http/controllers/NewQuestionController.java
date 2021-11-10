package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;

import java.sql.SQLException;
import java.util.Map;


public class NewQuestionController implements HttpController {
    private final QuestionDao questionDao;
    private final OptionDao optionDao;
    String responseText;

    public NewQuestionController(QuestionDao questionDao, OptionDao optionDao) {
        this.questionDao = questionDao;
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        if (request.messageBody != null) {
            Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
            Question q = new Question(queryMap.get("title"), Integer.parseInt(queryMap.get("survey")));
            questionDao.save(q);
            Option o1 = new Option(queryMap.get("option1"), (int) q.getId());
            Option o2 = new Option(queryMap.get("option2"), (int) q.getId());
            Option o3 = new Option(queryMap.get("option3"), (int) q.getId());
            Option o4 = new Option(queryMap.get("option4"), (int) q.getId());
            Option o5 = new Option(queryMap.get("option5"), (int) q.getId());
            optionDao.save(o1);
            optionDao.save(o2);
            optionDao.save(o3);
            optionDao.save(o4);
            optionDao.save(o5);

            responseText = "You have added: Question: " + q.getTitle() + " Survey: " + q.getSurveyId() +
                    " Options:" + o1.getOptionName() + " " + o2.getOptionName() + " " +
                    o3.getOptionName() + " " + o4.getOptionName() + " " + o5.getOptionName() + ".";
        }
        return new HttpMessage("HTTP/1.1 200", responseText);
    }
}
