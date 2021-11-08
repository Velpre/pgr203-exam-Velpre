package kristiania.no.jdbc.options;

import kristiania.no.jdbc.options.Option;
import kristiania.no.jdbc.question.Question;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OptionDao {
    private final DataSource dataSource;

    public OptionDao(DataSource dataSource) {
        this.dataSource = dataSource;
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
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from options where id = ?")) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();

                    return readFromResultSet(rs);
                }
            }
        }
    }

    private Option readFromResultSet(ResultSet rs) throws SQLException {
        Option options  = new Option();
        options.setId(rs.getLong("id"));
        options.setOptionName(rs.getString("option_name"));
        options.setQuestionId(rs.getInt("question_id"));
        return options;
    }

    public List<Option> retrieveFromQuestionId(long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from options where question_id = ?")) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Option> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(readFromResultSet(rs));
                    }
                    return result;
                }
            }
        }
    }

    //teste denne
    public void updateOption(String name, int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("update options set option_name = ? where question_id = ? ")) {
                statement.setString(1, name);
                statement.setLong(2, id);
                statement.executeUpdate();
            }
        }
    }
}
