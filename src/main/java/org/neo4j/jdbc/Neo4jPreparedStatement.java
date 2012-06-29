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

/**
 * Implementation of PreparedStatement. Parameters in Cypher queries have to be done as {nr}, as calls to methods
 * here will be saved in a parameter map with "nr"->value, since JDBC does not support named parameters.
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
        parameters.remove(Integer.toString(parameterIndex));
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException
    {
        parameters.put(Integer.toString(parameterIndex), Boolean.valueOf(x));
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException
    {
        parameters.put(Integer.toString(parameterIndex), Byte.valueOf(x));
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException
    {
        parameters.put(Integer.toString(parameterIndex), Short.valueOf(x));
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException
    {
        parameters.put(Integer.toString(parameterIndex), Integer.valueOf(x));
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException
    {
        parameters.put(Integer.toString(parameterIndex), Long.valueOf(x));
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException
    {
        parameters.put(Integer.toString(parameterIndex), Float.valueOf(x));
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException
    {
        parameters.put(Integer.toString(parameterIndex), Double.valueOf(x));
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException
    {
        parameters.put(Integer.toString(parameterIndex), x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException
    {
        parameters.put(Integer.toString(parameterIndex), x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException
    {
        throw new SQLException("Bytes not supported as parameters");
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException
    {
        throw new SQLException("Dates not supported as parameters");
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException
    {
        throw new SQLException("Times not supported as parameters");
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException
    {
        throw new SQLException("Timestamps not supported as parameters");
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void clearParameters() throws SQLException
    {
        parameters.clear();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException
    {
        parameters.put(Integer.toString(parameterIndex), x);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException
    {
        parameters.put(Integer.toString(parameterIndex), x);
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
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException
    {
        throw new SQLException("Refs not supported as parameters");
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException
    {
        throw new SQLException("Blobs not supported as parameters");
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException
    {
        throw new SQLException("Clobs not supported as parameters");
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException
    {
        throw new SQLException("Arrays not supported as parameters");
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException
    {
        if (resultSet == null)
        {
            execute();
        }

        return resultSet.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException
    {
        throw new SQLException("Dates not supported as parameters");
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException
    {
        throw new SQLException("Times not supported as parameters");
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException
    {
        throw new SQLException("Timestamps not supported as parameters");
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException
    {
        parameters.remove(Integer.toString(parameterIndex));
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException
    {
        return null;
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException
    {
        throw new SQLException("Timestamps not supported as parameters");
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException
    {
        throw new SQLException("Timestamps not supported as parameters");
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException
    {
        throw new SQLException("Clobs not supported as parameters");
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException
    {
        throw new SQLException("Clobs not supported as parameters");
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException
    {
        throw new SQLException("Blobs not supported as parameters");
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException
    {
        throw new SQLException("Clobs not supported as parameters");
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
    {
        throw new SQLException("SQLXML not supported as parameters");
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException
    {
        throw new SQLException("Object not supported as parameters");
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException
    {
        throw new SQLException("Streams not supported as parameters");
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException
    {
        throw new SQLException("Clobs not supported as parameters");
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException
    {
        throw new SQLException("Blobs not supported as parameters");
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException
    {
        throw new SQLException("Clobs not supported as parameters");
    }
}
