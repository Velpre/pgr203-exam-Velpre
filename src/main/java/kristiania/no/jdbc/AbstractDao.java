package kristiania.no.jdbc;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T> {
    protected final DataSource dataSource;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected T retrieveById(long id, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return mapFromResultSet(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }
    public List<T> listAll(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<T> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(mapFromResultSet(rs));
                    }
                    return result;
                }
            }
        }
    }
    public void delete(int id, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        }
    }
    public T retrieve(long id, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();

                    return mapFromResultSet(rs);
                }
            }
        }
    }

    public List<T> retrieveFromParentId(long id, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<T> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(mapFromResultSet(rs));
                    }
                    return result;
                }
            }
        }
    }

    protected abstract T mapFromResultSet(ResultSet rs) throws SQLException;
}