package org.server.repository;

import org.server.entity.Collocation;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;

@Repository
public class CollocationsRepository implements IRestRepository<Collocation>
{
    protected final JdbcOperations jdbcOperations;

    private static String selectQuery = "SELECT * FROM \"collocations\" " +
                                        "ORDER BY \"id\"";

    private static String selectByUserIdQuery = "SELECT * FROM \"collocations\" " +
            "INNER JOIN \"words\" " +
            "ON \"collocations\".\"prevId\" = \"words\".\"id\" " +
            "WHERE \"userId\" = ? " +
            "ORDER BY \"collocations\".\"count\" DESC";

    private static String selectByIdQuery = "SELECT * FROM \"collocations\" " +
                                            "WHERE \"id\" = ?";

    private static String selectByPrevIdNextIdQuery = "SELECT * FROM \"collocations\" " +
                                                        "WHERE \"prevId\" = ? OR \"nextId\" = ? " +
                                                        "ORDER BY \"count\" DESC";

    private static String insertQuery = "INSERT INTO \"collocations\" (\"prevId\", \"nextId\", \"count\") " +
            "VALUES (?, ?, ?) " +
            "RETURNING \"id\", \"prevId\", \"nextId\", \"count\"";

    private static String updateQuery = "UPDATE \"collocations\" " +
            "SET \"count\" = ? " +
            "WHERE \"id\" = ? " +
            "RETURNING \"id\", \"prevId\", \"nextId\", \"count\"";

    private static String deleteQuery = "DELETE FROM \"collocations\" " +
            "WHERE \"id\" = ? " +
            "RETURNING \"id\", \"prevId\", \"nextId\", \"count\"";

    public CollocationsRepository(JdbcOperations jdbcOperations) { this.jdbcOperations = jdbcOperations; }


    public Collocation[] select()
    {
        ArrayList<Collocation> values = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(selectQuery);
        while (rowSet.next())
        {
            values.add(new Collocation(
                    rowSet.getInt(1),
                    rowSet.getInt(2),
                    rowSet.getInt(3),
                    rowSet.getInt(4)
            ));
        }
        Collocation[] res = new Collocation[values.size()];
        res = values.toArray(res);
        return res;
    }


    public Collocation[] selectByUserId(Integer userId)
    {
        ArrayList<Collocation> values = new ArrayList<>();
        Object[] args = { userId };
        int[] types = { Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(selectByUserIdQuery, args, types);
        while (rowSet.next())
            values.add(new Collocation(
                    rowSet.getInt(1),
                    rowSet.getInt(2),
                    rowSet.getInt(3),
                    rowSet.getInt(4)
            ));
        Collocation[] res = new Collocation[values.size()];
        res = values.toArray(res);
        return res;
    }


    public Collocation select(Integer id)
    {
        Object[] args = { id };
        int[] types = { Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(selectByIdQuery, args, types);
        if (!rowSet.next())
            return null;
        return new Collocation(
                rowSet.getInt(1),
                rowSet.getInt(2),
                rowSet.getInt(3),
                rowSet.getInt(4)
        );
    }


    public Collocation[] selectByPrevIdNextId(Integer prevId, Integer nextId)
    {
        ArrayList<Collocation> values = new ArrayList<>();
        Object[] args = { prevId, nextId };
        int[] types = { Types.INTEGER, Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(selectByPrevIdNextIdQuery, args, types);
        while (rowSet.next())
        {
            values.add(new Collocation(
                    rowSet.getInt(1),
                    rowSet.getInt(2),
                    rowSet.getInt(3),
                    rowSet.getInt(4)
            ));
        }
        Collocation[] res = new Collocation[values.size()];
        res = values.toArray(res);
        return res;
    }


    public Collocation insert(Collocation collocationEntity)
    {
        Object[] args = { collocationEntity.getPrevId(), collocationEntity.getNextId(), collocationEntity.getCount() };
        int[] types = { Types.INTEGER, Types.INTEGER, Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(insertQuery, args, types);
        if (!rowSet.next())
            return null;
        return new Collocation(
                rowSet.getInt(1),
                rowSet.getInt(2),
                rowSet.getInt(3),
                rowSet.getInt(4)
        );
    }


    public Collocation update(Integer id, Collocation collocationEntity)
    {
        Object[] args = { collocationEntity.getCount(), id };
        int[] types = { Types.INTEGER, Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(updateQuery, args, types);
        if (!rowSet.next())
            return null;
        return new Collocation(
                rowSet.getInt(1),
                rowSet.getInt(2),
                rowSet.getInt(3),
                rowSet.getInt(4)
        );
    }


    public Collocation delete(Integer id)
    {
        Object[] args = { id };
        int[] types = { Types.INTEGER };
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(deleteQuery, args, types);
        if (!rowSet.next())
            return null;
        return new Collocation(
                rowSet.getInt(1),
                rowSet.getInt(2),
                rowSet.getInt(3),
                rowSet.getInt(4)
        );
    }
}
