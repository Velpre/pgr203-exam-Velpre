package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.QuestionDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static kristiania.no.http.HttpServer.parseRequestParameters;

public class ChangeQuestionController implements HttpController {
    private final QuestionDao questionDao;
    private final OptionDao optionDao;

    public ChangeQuestionController(QuestionDao questionDao, OptionDao optionDao) {
        this.optionDao = optionDao;
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> queryMap = parseRequestParameters(request.messageBody);
        List<Option> allOptions = new ArrayList<>();

        for (Option option : optionDao.retrieveFromQuestionId(Long.parseLong(queryMap.get("question")))) {
            allOptions.add(option);
        }

        while (allOptions.size() < 5) {
            Option option = new Option("name", Integer.parseInt(queryMap.get("question")));
            optionDao.save(option);
            allOptions.add(option);
        }

        questionDao.updateQuestion(Long.parseLong(queryMap.get("question")), queryMap.get("title"));

        for (int i = 1; i < 6; i++) {
            if (queryMap.get("option" + i) != "" && allOptions.size() > i) {
                optionDao.updateOption(queryMap.get("option" + i), (int) allOptions.get(i - 1).getId());
            } else {
                optionDao.delete(allOptions.get(i - 1).getId());
            }
        }

        String responseText = "Question has been changed";
        return new HttpMessage("HTTP/1.1 200", responseText);
    }
}
