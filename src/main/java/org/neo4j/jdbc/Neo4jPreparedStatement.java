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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.neo4j.jdbc.DriverQueries.QUERIES;

/**
 * TODO
 */
public class Neo4jPreparedStatement
    extends Neo4jStatement
    implements PreparedStatement
{
    private String query;
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public Neo4jPreparedStatement(Neo4jConnection connection, String query)
    {
        super(connection);
        this.query = query;
    }

    @Override
    public ResultSet executeQuery() throws SQLException
    {
        resultSet = connection.executeQuery(query, parameters);
        return resultSet;
    }

    @Override
    public int executeUpdate() throws SQLException
    {
        resultSet = connection.executeQuery(query, parameters);
        return 0;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException
    {
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException
    {
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException
    {
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException
    {
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException
    {
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException
    {
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException
    {
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException
    {
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException
    {
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException
    {
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException
    {
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException
    {
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException
    {
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException
    {
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
    }

    @Override
    public void clearParameters() throws SQLException
    {
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException
    {
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException
    {
    }

    @Override
    public boolean execute() throws SQLException
    {
        resultSet = connection.executeQuery(query, parameters);

        return true;
    }

    @Override
    public void addBatch() throws SQLException
    {
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException
    {
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException
    {
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException
    {
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException
    {
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException
    {
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException
    {
        if (resultSet == null)
            throw new SQLException("Prepared statement has been closed");

        return resultSet.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException
    {
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException
    {
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException
    {
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException
    {
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException
    {
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException
    {
        return null;
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException
    {
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException
    {
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException
    {
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException
    {
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException
    {
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException
    {
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException
    {
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
    {
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException
    {
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException
    {
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException
    {
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException
    {
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException
    {
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException
    {
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException
    {
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException
    {
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException
    {
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException
    {
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException
    {
    }
}
