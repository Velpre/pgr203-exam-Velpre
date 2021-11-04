package kristiania.no.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDaoTest {
    private UserDao dao = new UserDao(TestData.testDataSource());

    @Test
    void shouldRetrieveSavedUser() throws SQLException {
        User user = new User("testUser");
        dao.save(user);
        assertThat(dao.retrieve(user.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    void shouldListSavedUser() throws SQLException {
        User user = new User("testUser");
        dao.save(user);

        assertThat(dao.listAll())
                .extracting(User::getUserName)
                .contains("testUser");
    }
    @Test
    void shouldAddAndDeleteUser() throws SQLException {
        User user = new User("testUser");
        dao.save(user);
        dao.delete((int) user.id);
        assertThat(dao.listAll()).doesNotContain(user);
    }
}
