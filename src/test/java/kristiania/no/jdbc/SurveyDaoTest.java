package kristiania.no.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SurveyDaoTest {
    private SurveyDao dao = new SurveyDao(TestData.testDataSource());

    @Test
    void shouldListSavedRoles() throws SQLException {
        String survey1 = "survey-" + UUID.randomUUID();
        String survey2 = "survey-" + UUID.randomUUID();

        dao.save(survey1);
        dao.save(survey2);

        assertThat(dao.listAll())
                .extracting(Survey::getName)
                .contains(survey1, survey2);
    }
}
