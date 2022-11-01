package org.server.repository;

import org.server.entity.User;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
public class UsersRepository
{
    protected final JdbcOperations jdbcOperations;

    private static String selectByIdQuery = "SELECT * FROM \"users\" WHERE \"id\" = ?";

    private static String selectByLoginQuery = "SELECT \"id\", \"login\" FROM \"users\" WHERE \"login\" = ?";

    private static String selectByLoginPasswordQuery =
            "SELECT \"id\", \"login\" FROM \"users\" WHERE \"login\" = ? AND \"password\" = ?";

    private static String insertQuery = "INSERT INTO \"users\" (\"login\", \"password\") " +
                                        "VALUES (?, ?) " +
                                        "RETURNING \"id\", \"login\"";

    public UsersRepository(JdbcOperations jdbcOperations) { this.jdbcOperations = jdbcOperations; }


    public User select(Integer id)
    {
        Object[] args = { id };
        int[] types = { Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(selectByIdQuery, args, types);
        if (!rowSet.next())
            return null;
        return new User(
                rowSet.getInt(1),
                rowSet.getString(2),
                rowSet.getString(3)
        );
    }


    public User select(User user)
    {
        Object[] args = { user.getLogin(), String.valueOf(user.getPassword().hashCode()) };
        int[] types = { Types.VARCHAR, Types.VARCHAR };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(selectByLoginPasswordQuery, args, types);
        if (!rowSet.next())
            return new User(
                    -1,
                    null,
                    null
            );
        return new User(
                rowSet.getInt(1),
                rowSet.getString(2),
                null
        );
    }


    private User select(String login)
    {
        Object[] args = { login };
        int[] types = { Types.VARCHAR };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(selectByLoginQuery, args, types);
        if (!rowSet.next())
            return null;
        return new User(
                rowSet.getInt(1),
                rowSet.getString(2),
                null
        );
    }


    public User insert(User user)
    {
        if (select(user.getLogin()) != null)
            return new User(
                    -1,
                    null,
                    null
            );
        Object[] args = { user.getLogin(), String.valueOf(user.getPassword().hashCode()) };
        int[] types = { Types.VARCHAR, Types.VARCHAR };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(insertQuery, args, types);
        if (!rowSet.next())
            return new User(
                    -1,
                    null,
                    null
            );
        return new User(
                rowSet.getInt(1),
                rowSet.getString(2),
                null
        );
    }
}
