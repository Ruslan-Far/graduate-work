package org.server.repository;

import org.server.entity.Word;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import java.sql.Types;
import java.util.ArrayList;

@Repository
public class WordsRepository implements IRestRepository<Word>
{
    protected final JdbcOperations jdbcOperations;

    private static String selectQuery = "SELECT * FROM \"words\" " +
                                        "ORDER BY \"id\"";

    private static String selectByUserIdQuery = "SELECT * FROM \"words\" " +
                                                 "WHERE \"userId\" = ?";

    private static String selectByIdQuery = "SELECT * FROM \"words\" " +
                                            "WHERE \"id\" = ?";

    private static String insertQuery = "INSERT INTO \"words\" (\"userId\", \"word\", \"count\") " +
            "VALUES (?, ?, ?) " +
            "RETURNING \"id\", \"userId\", \"word\", \"count\"";

    private static String updateQuery = "UPDATE \"words\" " +
            "SET \"count\" = ? " +
            "WHERE \"id\" = ? " +
            "RETURNING \"id\", \"userId\", \"word\", \"count\"";

    private static String deleteQuery = "DELETE FROM \"words\" " +
            "WHERE \"id\" = ? " +
            "RETURNING \"id\", \"userId\", \"word\", \"count\"";

    public WordsRepository(JdbcOperations jdbcOperations) { this.jdbcOperations = jdbcOperations; }


    public Word[] select()
    {
        ArrayList<Word> values = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(selectQuery);
        while (rowSet.next())
        {
            values.add(new Word(
                    rowSet.getInt(1),
                    rowSet.getInt(2),
                    rowSet.getString(3),
                    rowSet.getInt(4)
            ));
        }
        Word[] res = new Word[values.size()];
        res = values.toArray(res);
        return res;
    }


    public Word[] selectByUserId(Integer userId)
    {
        ArrayList<Word> values = new ArrayList<>();
        Object[] args = { userId };
        int[] types = { Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(selectByUserIdQuery, args, types);
        while (rowSet.next())
            values.add(new Word(
                    rowSet.getInt(1),
                    rowSet.getInt(2),
                    rowSet.getString(3),
                    rowSet.getInt(4)
            ));
        Word[] res = new Word[values.size()];
        res = values.toArray(res);
        return res;
    }


    public Word select(Integer id)
    {
        Object[] args = { id };
        int[] types = { Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(selectByIdQuery, args, types);
        if (!rowSet.next())
            return null;
        return new Word(
                rowSet.getInt(1),
                rowSet.getInt(2),
                rowSet.getString(3),
                rowSet.getInt(4)
        );
    }


    public Word insert(Word wordEntity)
    {
        Object[] args = { wordEntity.getUserId(), wordEntity.getWord(), wordEntity.getCount() };
        int[] types = { Types.INTEGER, Types.VARCHAR, Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(insertQuery, args, types);
        if (!rowSet.next())
            return null;
        return new Word(
                rowSet.getInt(1),
                rowSet.getInt(2),
                rowSet.getString(3),
                rowSet.getInt(4)
        );
    }


    public Word update(Integer id, Word wordEntity)
    {
        Object[] args = { wordEntity.getCount(), id };
        int[] types = { Types.INTEGER, Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(updateQuery, args, types);
        if (!rowSet.next())
            return null;
        return new Word(
                rowSet.getInt(1),
                rowSet.getInt(2),
                rowSet.getString(3),
                rowSet.getInt(4)
        );
    }


    public Word delete(Integer id)
    {
        Object[] args = { id };
        int[] types = { Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(deleteQuery, args, types);
        if (!rowSet.next())
            return null;
        return new Word(
                rowSet.getInt(1),
                rowSet.getInt(2),
                rowSet.getString(3),
                rowSet.getInt(4)
        );
    }
}
