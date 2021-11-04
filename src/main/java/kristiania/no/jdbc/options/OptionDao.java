package kristiania.no.jdbc.options;

import kristiania.no.jdbc.AbstractDao;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class OptionDao extends AbstractDao {
    public OptionDao(DataSource dataSource) {
        super(dataSource);
    }
    @Override
    protected Option mapFromResultSet(ResultSet rs) throws SQLException {
        Option options  = new Option();
        options.setId(rs.getLong("id"));
        options.setOptionName(rs.getString("option_name"));
        options.setQuestionId(rs.getInt("question_id"));
        return options;
    }

    public void save(Option option) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into options (option_name, question_id) values (?,?)",
                    Statement.RETURN_GENERATED_KEYS

            )) {
                statement.setString(1, option.getOptionName());
                statement.setInt(2, option.getQuestionId());
                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    option.setId(rs.getLong("id"));
                }
            }
        }
    }

    public Option retrieve(long id) throws SQLException {
        return (Option) retrieve(id,"select * from options where id = ?");
    }

    public List<Option> retrieveFromQuestionId(long id) throws SQLException {
        return retrieveFromParentId(id, "select * from options where question_id = ?");
    }
}
