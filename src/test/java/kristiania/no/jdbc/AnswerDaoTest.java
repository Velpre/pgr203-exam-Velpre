package kristiania.no.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerDaoTest {
    private AnswerDao dao = new AnswerDao(TestData.testDataSource());
    @Test
    void shouldRetrieveSavedAnswer() throws SQLException {
        Answer answer = new Answer("a1", 1);
        dao.save(answer);
        assertThat(dao.retrieve(answer.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(answer);
    }

}
