package kristiania.no.jdbc.survey;

import kristiania.no.jdbc.AbstractDao;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class SurveyDao extends AbstractDao {
    public SurveyDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Survey mapFromResultSet(ResultSet rs) throws SQLException {
        Survey survey = new Survey();
        survey.setId(rs.getLong("id"));
        survey.setName(rs.getString("survey_name"));
        return survey;
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
        return (Survey) retrieve(id, "select * from survey where id = ?");
    }

    public List<Survey> listAll() throws SQLException {
        return listAll("select * from survey");
    }

    public void delete(int id) throws SQLException {
        delete(id, "delete from survey where id = ?");
    }

}

