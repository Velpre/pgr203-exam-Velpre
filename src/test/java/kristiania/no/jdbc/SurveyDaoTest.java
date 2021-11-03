package kristiania.no.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class SurveyDaoTest {
    private SurveyDao dao = new SurveyDao(TestData.testDataSource());


    @Test
    void shouldRetrieveSavedSurvey() throws SQLException {
        Survey survey = new Survey("s1");
        dao.save(survey);
        assertThat(dao.retrieve(survey.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(survey);
    }

    @Test
    void shouldListSavedSurvey() throws SQLException {
        // survey blir opprettet i V004 migration
        assertThat(dao.listAll())
                .extracting(Survey::getName)
                .contains("survey1", "survey2");
    }


}
