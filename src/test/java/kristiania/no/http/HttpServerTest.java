package kristiania.no.http;

import kristiania.no.http.controllers.*;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.answer.AnswerDao;
import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;
import kristiania.no.jdbc.survey.Survey;
import kristiania.no.jdbc.survey.SurveyDao;
import kristiania.no.jdbc.user.UserDao;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {
    HttpServer server = new HttpServer(0);
    DataSource dataSource = TestData.testDataSource();
    QuestionDao questionDao = new QuestionDao(dataSource);
    SurveyDao surveyDao = new SurveyDao(dataSource);
    AnswerDao answerDao = new AnswerDao(dataSource);
    UserDao userDao = new UserDao(dataSource);
    OptionDao optionDao = new OptionDao(dataSource);

    AddAndListAllQuestionsController addAndListAllQuestionsController = new AddAndListAllQuestionsController(questionDao, optionDao);

    public HttpServerTest() throws IOException {
        server.addController(new ListQuestionsController(questionDao, optionDao));
        server.addController(new AddAndListSurveyController(surveyDao));
        server.addController(new AnswerQuestionsController(answerDao, userDao));
        server.addController(new AddAndListAllQuestionsController(questionDao, optionDao));
        server.addController(new DeleteSurveyController(surveyDao));
        server.addController(new ListUsersController(userDao));
        server.addController(new ListAnswersController(questionDao, answerDao));
        server.addController(new ChangeQuestionController(questionDao, optionDao,answerDao));
    }

    @Test
    void shouldReturn404ForUnknownRequestTarget() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldRespondWithRequestTargetIn404() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals("File not found: /non-existing", client.getMessageBody());
    }

    @Test
    void shouldServeFiles() throws IOException {

        String fileContent = "Det funker";
        Files.write(Paths.get("target/test-classes/test.txt"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/test.txt");
        assertEquals(fileContent, client.getMessageBody());
        assertEquals("text/plain", client.getHeader("Content-Type"));
    }

    @Test
    void shouldUseFileExtensionForContentTypeHTML() throws IOException {
        String fileContent = "<p>Hello</p>";
        Files.write(Paths.get("target/test-classes/example-file.html"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/example-file.html");
        assertEquals("text/html", client.getHeader("Content-Type"));
    }

    @Test
    void shouldUseFileExtensionForContentTypeCSS() throws IOException {
        String fileContent = "";
        Files.write(Paths.get("target/test-classes/example-file.css"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/example-file.css");
        assertEquals("text/css", client.getHeader("Content-Type"));
    }

    @Test
    void shouldHandelMoreThanOneRequest() throws IOException {
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/api/addAndListAllQuestions").getStatusCode());
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/api/addAndListAllQuestions").getStatusCode());
    }

    @Test
    void shouldRespondWith200ForKnownRequestTarget() throws IOException {
        new HttpPostClient("localhost", server.getPort(), "/api/answerQuestions", "newUser=test");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/answerQuestions");
        assertAll(
                () -> assertEquals(200, client.getStatusCode()),
                () -> assertEquals("text/html; charset=utf-8", client.getHeader("Content-Type")),
                () -> assertEquals("Questions answered", client.getMessageBody())
        );
    }

    @Test
    void shouldReturnSurveyFromServer() throws IOException, SQLException {
        Survey survey1 = new Survey("test1");
        Survey survey2 = new Survey("test2");
        surveyDao.save(survey1);
        surveyDao.save(survey2);
        server.addController(new AddAndListSurveyController(surveyDao));
        //Andre survey objekter blir også lagt til i DB gjennom V006 migrering
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/addAndListSurvey");
        assertThat(client.getMessageBody()).contains("<option value=" + survey1.getId() + ">test1</option><option value=" + survey2.getId() + ">test2</option>");
    }

    @Test
    void shouldAddQuestions() throws IOException, SQLException {
        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(),
                "/api/addAndListAllQuestions",
                "title=title1&survey" +
                        "=1&option1=o1&option2=o2&option3=o3&option4=o4&option5=o5");
        assertEquals(303, postClient.getStatusCode());
        List<Question> questionList = questionDao.listAll();

        assertThat(questionList)
                .extracting(Question::getId)
                .contains(1L);
    }

    @Test
    void shouldReturnQuestionsFromServer() throws IOException, SQLException {
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        OptionDao optionDao = new OptionDao(TestData.testDataSource());
        server.addController(new ListQuestionsController(questionDao, optionDao));


        //question1 & question2 objekt blir lagt til i DB gjennom V006 migrering
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/listQuestions");
        assertEquals(
                "<p>Create New user:</p><input type=\"text\" id=\"userName\" name=\"newUser\" label =\"Username:\"> </input><br><p>Chose one of existing users<p><p><label>Select user <select name=\"existingUsers\" id=\"existingUsers\"></select></label></p><br><button>Answer</button>"
                ,
                client.getMessageBody()
        );
    }

    @Test
    void shouldGetWeirdCharTest() throws SQLException {
        Question question1 = new Question("øåæ ", 1);
        questionDao.save(question1);


        HttpMessage httpMessage = new HttpMessage("GET HTTP/1.1 200", "");
        HttpMessage response = addAndListAllQuestionsController.handle(httpMessage);
        assertThat(response.messageBody)
                .contains("øåæ ");
    }

    @Test
    void shouldPostWeirdCharTest() throws SQLException {
        HttpMessage httpMessage = new HttpMessage("POST HTTP/1.1 200", "title=æåø &survey=1&option1=øåææ&option2=øøø" +
                "&option3=ø æ å &option4=æ(% å æø &option5=øæ æåø æø");
        addAndListAllQuestionsController.handle(httpMessage);

        assertThat(optionDao.listAll())
                .extracting(Option::getOptionName)
                .contains("øæ æåø æø", "æ(% å æø ", "ø æ å ", "øåææ", "øøø");
    }

}
