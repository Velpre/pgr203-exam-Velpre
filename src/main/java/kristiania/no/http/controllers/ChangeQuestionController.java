package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.QuestionDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ChangeQuestionController implements HttpController {
    private final QuestionDao questionDao;
    private final OptionDao optionDao;
    private final AnswerDao answerDao;

    public ChangeQuestionController(QuestionDao questionDao, OptionDao optionDao, AnswerDao answerDao) {
        this.optionDao = optionDao;
        this.questionDao = questionDao;
        this.answerDao = answerDao;
    }

    @Override
    public String getPath() {
        return "/api/changeQuestion";
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
        List<Option> allOptions = new ArrayList<>(optionDao.retrieveFromQuestionId(Long.parseLong(queryMap.get("question"))));

        while (allOptions.size() < 5) {
            Option option = new Option(queryMap.get("title"), Integer.parseInt(queryMap.get("question")));
            optionDao.save(option);
            allOptions.add(option);
        }

        questionDao.update(queryMap.get("title"), Long.parseLong(queryMap.get("question")));

        for (int i = 1; i < 6; i++) {
            if (!Objects.equals(queryMap.get("option" + i), "") && allOptions.size() >= i) {
                optionDao.update(queryMap.get("option" + i), (int) allOptions.get(i - 1).getId());
            } else {
                optionDao.delete(allOptions.get(i - 1).getId());

            }
            answerDao.delete(Long.parseLong(queryMap.get("question")));
        }

        String responseText = "Question has been changed";
        return new HttpMessage("HTTP/1.1 303", responseText, "../editSurvey.html");
    }


}
