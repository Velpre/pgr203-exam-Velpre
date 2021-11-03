package kristiania.no.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SurveyDaoTest {
    private SurveyDao dao = new SurveyDao(TestData.testDataSource());


}
