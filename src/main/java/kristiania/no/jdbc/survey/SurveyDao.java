package kristiania.no.jdbc.survey;

import kristiania.no.jdbc.survey.Survey;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SurveyDao {
    private final DataSource dataSource;
    public SurveyDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Survey survey) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into survey (survey_name) values (?)",
                    Statement.RETURN_GENERATED_KEYS

            )) {
                statement.setString(1, survey.getName());


                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    survey.setId(rs.getLong("id"));
                }
            }
        }
    }


    public Survey retrieve(long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from survey where id = ?")) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();

                    return readFromResultSet(rs);
                }
            }
        }
    }

    private Survey readFromResultSet(ResultSet rs) throws SQLException {
        Survey survey = new Survey();
        survey.setId(rs.getLong("id"));
        survey.setName(rs.getString("survey_name"));

        return survey;
    }

    public List<Survey> listAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from survey")) {
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Survey> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(readFromResultSet(rs));
                    }
                    return result;
                }
            }
        }
    }
    //Teste denne
    public void delete(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "delete from survey where id = ?"
            )) {
                statement.setLong(1, id);

                statement.executeUpdate();
            }
        }
    }

}

