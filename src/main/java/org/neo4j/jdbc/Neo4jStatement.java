/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.jdbc;

import java.sql.*;
import java.util.Collections;

/**
 * TODO
 */
public class Neo4jStatement
    implements Statement
{
    protected Neo4jConnection connection;
    protected ResultSet resultSet;

    public Neo4jStatement(Neo4jConnection connection)
    {
        this.connection = connection;
    }

    @Override
    public ResultSet executeQuery(String s) throws SQLException
    {
        execute(s);
        return resultSet;
    }

    @Override
    public int executeUpdate(String s) throws SQLException
    {
        execute(s);
        return 0;
    }

    @Override
    public void close() throws SQLException
    {
        connection = null;
        resultSet = null;
    }

    @Override
    public int getMaxFieldSize() throws SQLException
    {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int i) throws SQLException
    {
    }

    @Override
    public int getMaxRows() throws SQLException
    {
        return 0;
    }

    @Override
    public void setMaxRows(int i) throws SQLException
    {
    }

    @Override
    public void setEscapeProcessing(boolean b) throws SQLException
    {
    }

    @Override
    public int getQueryTimeout() throws SQLException
    {
        return 0;
    }

    @Override
    public void setQueryTimeout(int i) throws SQLException
    {
    }

    @Override
    public void cancel() throws SQLException
    {
    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException
    {
    }

    @Override
    public void setCursorName(String s) throws SQLException
    {
    }

    @Override
    public boolean execute(String s) throws SQLException
    {
        try
        {
            resultSet = connection.executeQuery(s, Collections.<String, Object>emptyMap());
            return true;
        } catch (Throwable e)
        {
            throw new SQLException(e);
        }
    }

    @Override
    public ResultSet getResultSet() throws SQLException
    {
        return resultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException
    {
        return -1;
    }

    @Override
    public boolean getMoreResults() throws SQLException
    {
        resultSet = null;
        return false;
    }

    @Override
    public void setFetchDirection(int i) throws SQLException
    {
    }

    @Override
    public int getFetchDirection() throws SQLException
    {
        return 0;
    }

    @Override
    public void setFetchSize(int i) throws SQLException
    {
    }

    @Override
    public int getFetchSize() throws SQLException
    {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException
    {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException
    {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public void addBatch(String s) throws SQLException
    {
    }

    @Override
    public void clearBatch() throws SQLException
    {
    }

    @Override
    public int[] executeBatch() throws SQLException
    {
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return connection;
    }

    @Override
    public boolean getMoreResults(int i) throws SQLException
    {
        return getMoreResults();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException
    {
        return new ResultSetBuilder().newResultSet(connection);
    }

    @Override
    public int executeUpdate(String s, int i) throws SQLException
    {
        return executeUpdate(s);
    }

    @Override
    public int executeUpdate(String s, int[] ints) throws SQLException
    {
        return executeUpdate(s);
    }

    @Override
    public int executeUpdate(String s, String[] strings) throws SQLException
    {
        return executeUpdate(s);
    }

    @Override
    public boolean execute(String s, int i) throws SQLException
    {
        return execute(s);
    }

    @Override
    public boolean execute(String s, int[] ints) throws SQLException
    {
        return execute(s);
    }

    @Override
    public boolean execute(String s, String[] strings) throws SQLException
    {
        return execute(s);
    }

    @Override
    public int getResultSetHoldability() throws SQLException
    {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        return connection == null;
    }

    @Override
    public void setPoolable(boolean b) throws SQLException
    {
    }

    @Override
    public boolean isPoolable() throws SQLException
    {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException
    {
        return (T) this;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException
    {
        return false;
    }
}
