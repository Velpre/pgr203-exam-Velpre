package kristiania.no.http;

import kristiania.no.jdbc.Question;
import kristiania.no.jdbc.QuestionDao;
import kristiania.no.jdbc.SurveyDao;
import kristiania.no.jdbc.TestData;
import org.junit.jupiter.api.Test;

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
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/hello").getStatusCode());
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/hello").getStatusCode());
    }


    @Test
    void shouldEchoMoreThanOneQueryParameter() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/hello?firstName=Veljko&lastName=Premovic");
        assertEquals("<p>Hello Veljko, Premovic</p>", client.getMessageBody());
    }


    //Denne testen ble forandret i forbildense med at vi printer ut input feil under spørsmål på api/questions
    //Må eventuelt finne bedre ting å teste på istedenfor å teste på messageBody.
    @Test
    void shouldReturnQuestionsFromServer() throws IOException, SQLException {
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.setQuestionDao(questionDao);

        Question q1 = new Question("title1", 1);
        Question q2 = new Question("title2", 2);
        questionDao.save(q1);
        questionDao.save(q2);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questions");
        assertEquals(
                "<p>title1</p>" +
                        "<label for=answer>Answer:</label>" +
                        "<input type=text name=answer>"+
                        "<p>title2</p>" +
                        "<label for=answer>Answer:</label>" +
                        "<input type=text name=answer>"
                ,
                client.getMessageBody()
        );
    }


    @Test
    void shouldReturnCategoriesFromServer() throws IOException, SQLException {
        SurveyDao surveyDao = new SurveyDao(TestData.testDataSource());
        server.setSurveyDao(surveyDao);
        surveyDao.save("Food survey");
        surveyDao.save("Sport survey");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/surveyOptions");
        assertEquals(
                "<option value=1>Food survey</option>" +
                        "<option value=2>Sport survey</option>"
                ,
                client.getMessageBody()
        );
    }

    @Test
    void shouldAddQuestions() throws IOException, SQLException {
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.setQuestionDao(questionDao);

        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(),"/api/newQuestion", "title=title1&questionText=text1&survey=1");
        assertEquals(200, postClient.getStatusCode());
        Question q = server.getQuestions().get(0);
        assertEquals("title1", q.getTitle());
    }


}
