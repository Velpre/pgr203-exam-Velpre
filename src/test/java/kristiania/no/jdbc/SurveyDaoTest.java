package kristiania.no.jdbc;

import kristiania.no.jdbc.survey.Survey;
import kristiania.no.jdbc.survey.SurveyDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class SurveyDaoTest {
    private final SurveyDao dao = new SurveyDao(TestData.testDataSource());

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
        Survey survey = new Survey("Survey");
        dao.save(survey);
        assertThat(dao.listAll())
                .extracting(Survey::getName)
                .contains("Website Questionnaire", "Customer Satisfaction Questionnaire", "Survey");
    }

    @Test
    void shouldAddAndDeleteSurvey() throws SQLException {
        Survey survey = new Survey("Survey");
        dao.save(survey);
        dao.delete((int) survey.getId());
        assertThat(dao.listAll()).doesNotContain(survey);
    }
}
