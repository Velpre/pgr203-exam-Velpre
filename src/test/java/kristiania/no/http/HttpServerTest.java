package kristiania.no.http;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

public class HttpServerTest {
    @Test
    void shouldReturn404ForUnknowRequestTarget() throws IOException {
        HttpServer server = new HttpServer(10001);
        HttpClient client = new HttpClient("localhost", 10001, "/non-existing");
        assertEquals(404, client.getStatusCode());

    }
}
