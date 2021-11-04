package kristiania.no.jdbc;

import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionDaoTest {
    private OptionDao dao = new OptionDao(TestData.testDataSource());

    @Test
    void shouldRetrieveSavedOptions() throws SQLException {
        Option option = new Option("o1", 1);
        dao.save(option);
        assertThat(dao.retrieve(option.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(option);
    }

    @Test
    void shouldListAllOptionsById() throws SQLException {
        Option option = new Option("o1", 1);
        dao.save(option);
        Option option1 = new Option("o2", 2);
        dao.save(option1);

        assertThat(dao.retrieveFromQuestionId(1))
                .extracting(Option::getId)
                .contains(option.getId())
                .doesNotContain(option1.getId());
    }

}
