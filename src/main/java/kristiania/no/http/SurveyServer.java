package kristiania.no.http;

import kristiania.no.http.controllers.*;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.QuestionDao;
import kristiania.no.jdbc.survey.SurveyDao;
import kristiania.no.jdbc.user.UserDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;


public class SurveyServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private static DataSource createDataSource() throws IOException {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("pgr203.properties")) {
            properties.load(reader);
        }
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    public static void main(String[] args) throws IOException {
        DataSource dataSource = createDataSource();
        QuestionDao questionDao = new QuestionDao(dataSource);
        SurveyDao surveyDao = new SurveyDao(dataSource);
        AnswerDao answerDao = new AnswerDao(dataSource);
        UserDao userDao = new UserDao(dataSource);
        OptionDao optionDao = new OptionDao(dataSource);

        HttpServer httpServer = new HttpServer(7000);
        httpServer.addController("/api/listQuestions", new ListQuestionsController(questionDao, optionDao));
        httpServer.addController("/api/addAndListSurvey", new AddAndListSurveyController(surveyDao));
        httpServer.addController("/api/answerQuestions", new AnswerQuestionsController(answerDao, userDao));
        httpServer.addController("/api/addAndListAllQuestions", new AddAndListAllQuestionsController(questionDao, optionDao));
        httpServer.addController("/api/deleteSurvey", new DeleteSurveyController(surveyDao));
        httpServer.addController("/api/listUsers", new ListUsersController(userDao));
        httpServer.addController("/api/listAnswers", new ListAnswersController(questionDao, answerDao));
        httpServer.addController("/api/changeQuestion", new ChangeQuestionController(questionDao, optionDao));

        httpServer.setRoot(Paths.get("src/main/resources/webfiles"));

        logger.info("Server running at http://localhost:{}/", httpServer.getPort());
    }


}
