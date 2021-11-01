package kristiania.no.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SurveyDao {
    private final DataSource dataSource;
    public SurveyDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(String surveyName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into survey (survey_name) values (?)"
            )) {
                statement.setString(1, surveyName);
                statement.executeUpdate();
            }
        }
    }

    public List<Survey> listAll() throws SQLException {

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from survey order by survey_name")) {
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Survey> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(new Survey(rs.getString("survey_name")));
                    }
                    return result;
                }
            }
        }
    }
}
