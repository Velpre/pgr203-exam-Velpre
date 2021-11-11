package kristiania.no.jdbc;

import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.options.OptionDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionDaoTest {
    private final OptionDao dao = new OptionDao(TestData.testDataSource());

    @Test
    void shouldRetrieveAndSaveOptions() throws SQLException {
        Option option = new Option("TestName", 1);
        dao.save(option);

        assertThat(dao.retrieveFromQuestionId(option.getQuestionId()))
                .extracting(Option::getOptionName)
                .contains("TestName");
    }

    @Test
    void shouldListAllOptionsByQuestionId() throws SQLException {
        Option option = new Option("o1", 1);
        dao.save(option);
        Option option1 = new Option("o2", 2);
        dao.save(option1);

        assertThat(dao.retrieveFromQuestionId(1))
                .extracting(Option::getId)
                .contains(option.getId())
                .doesNotContain(option1.getId());
    }

    @Test
    void shouldDeleteOption() throws SQLException {
        Option option = new Option("o1", 1);
        dao.save(option);
        dao.delete(option.getId());
        assertThat(dao.listAll()).doesNotContain(option);
    }

    @Test
    void shouldListAll() throws SQLException {
        assertThat(dao.listAll()).
                extracting(Option::getOptionName)
                .contains("Less than a minute", "About 1 - 2 minutes", "Between 2 and 5 minutes", "More than 5 minutes",
                        "Finding enough time for important tasks", "Delegating work", "Having enough to do",
                        "Finding a faster way to work", "Problem-solving", "Staff development", "Less than a minute",
                        "About 1 - 2 minutes", "Between 2 and 5 minutes", "More than 5 minutes");
    }


    @Test
    void shouldUpdateOption() throws SQLException {
        Option option = new Option("TestNameUpdate", 1);
        dao.save(option);
        dao.update("NewTestName", option.getId());

        assertThat(dao.listAll()).
                extracting(Option::getOptionName)
                .contains("NewTestName")
                .doesNotContain("TestNameUpdate");
    }
}














