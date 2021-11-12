package kristiania.no.http.controllers;

import kristiania.no.http.HttpMessage;
import kristiania.no.jdbc.TestData;
import kristiania.no.jdbc.user.UserDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class ListUserControllerTest {
    UserDao userDao = new UserDao(TestData.testDataSource());
    ListUsersController listUsersController = new ListUsersController(userDao);

    // Tester ListUsersController - DONE
    @Test
    void shouldListAllUsers() throws SQLException {
        HttpMessage httpMessage = new HttpMessage("GET HTTP/1.1 200", "");
        HttpMessage response = listUsersController.handle(httpMessage);
        assertThat(response.messageBody).contains("<option value=1>User1</option>");
    }
}
