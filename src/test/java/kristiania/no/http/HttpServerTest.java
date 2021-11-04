package kristiania.no.http;

import kristiania.no.jdbc.*;
import kristiania.no.jdbc.options.OptionDao;
import kristiania.no.jdbc.question.Question;
import kristiania.no.jdbc.question.QuestionDao;
import kristiania.no.jdbc.survey.SurveyDao;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class HttpServerTest {
    HttpServer server = new HttpServer(0);

    public HttpServerTest() throws IOException {
    }

    @Test
    void shouldReturn404ForUnknowRequestTarget() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldRespondWithRequestTargetIn404() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals("File not found: /non-existing", client.getMessageBody());
    }

    @Test
    void shouldRespondWith200FoKnownRequestTrarget() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/hello");
        assertAll(
                () -> assertEquals(200, client.getStatusCode()),
                () -> assertEquals("text/html; charset=utf-8", client.getHeader("Content-Type")),
                () -> assertEquals("<p>Hello world</p>", client.getMessageBody())
        );
    }
    @Test
    void shouldServeFiles() throws IOException {
        server.setRoot(Paths.get("target/test-classes"));

        String fileContent= "Det funker";
        Files.write(Paths.get("target/test-classes/test.txt"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/test.txt");
        assertEquals(fileContent, client.getMessageBody());
    }
    //Sjekk om nødvendig
    @Test
    void shouldUseFileExtensionForContentType() throws IOException {
        server.setRoot(Paths.get("target/test-classes"));
        String fileContent = "<p>Hello</p>";
        Files.write(Paths.get("target/test-classes/example-file.html"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/example-file.html");
        assertEquals("text/html", client.getHeader("Content-Type"));
    }

    @Test
    void shouldHandelMoreThanOneRequest() throws IOException {
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.setQuestionDao(questionDao);
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/api/listQuestions").getStatusCode());
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/api/listQuestions").getStatusCode());
    }


    @Test
    void shouldEchoMoreThanOneQueryParameter() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/hello?firstName=Veljko&lastName=Premovic");
        assertEquals("<p>Hello Veljko, Premovic</p>", client.getMessageBody());
    }

    @Test
    void shouldReturnQuestionsFromServer() throws IOException, SQLException {
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.setQuestionDao(questionDao);


        //question1 & question2 objekt blir lagt til i DB gjennom V005 migrering
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/listQuestions");
        assertEquals(
                "<p>Write username:</p><input required type=\"text\" id=\"userName\" name=\"userName\" label =\"Username:\"> </input><br><br><button>Answer</button>"
                ,
                client.getMessageBody()
        );
    }


    @Test
    void shouldReturnCategoriesFromServer() throws IOException, SQLException {
        SurveyDao surveyDao = new SurveyDao(TestData.testDataSource());
        server.setSurveyDao(surveyDao);
        //survey1 & survey2 objekt blir lagt til i DB gjennom V004 migrering
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/listSurveyOptions");
        assertEquals(
                "<option value=1>survey1</option>" +
                        "<option value=2>survey2</option>"
                ,
                client.getMessageBody()
        );
    }


    @Test
    void shouldAddQuestions() throws IOException, SQLException {
        DataSource dataSource = TestData.testDataSource();
        QuestionDao questionDao = new QuestionDao(dataSource);
        OptionDao optionDao = new OptionDao(dataSource);
        server.setQuestionDao(questionDao);
        server.setOptionDao(optionDao);

        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(),"/api/newQuestion", "title=title1&questionText=text1&survey=1&option1=o1&option2=o2&option3=o3");
        assertEquals(200, postClient.getStatusCode());
        Question q = server.getQuestions().get(0);
        //question1 blir lagt til i DB gjennom V005 migrering
        assertEquals("question1", q.getTitle());
    }

}
