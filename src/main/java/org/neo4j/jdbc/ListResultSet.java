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

import org.codehaus.jackson.map.jsontype.impl.TypeNameIdResolver;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TODO
 */
public class ListResultSet
    implements ResultSet
{
    private boolean closed = false;
    private int current = -1;
    private List<ColumnMetaData> columns;
    private List<List<Object>> data;

    public ListResultSet(List<ColumnMetaData> columns, List<List<Object>> data)
    {
        this.columns = columns;
        this.data = data;
    }

    @Override
    public boolean next() throws SQLException
    {
        current++;

        return data.size()>current;
    }

    @Override
    public void close() throws SQLException
    {
        closed = true;
    }

    @Override
    public boolean wasNull() throws SQLException
    {
        return false;
    }

    @Override
    public String getString(int i) throws SQLException
    {
        Object value = data.get(current).get(i - 1);
        return value == null ? null : value.toString();
    }

    @Override
    public boolean getBoolean(int i) throws SQLException
    {
        return (Boolean) data.get(current).get(i-1);
    }

    @Override
    public byte getByte(int i) throws SQLException
    {
        return (Byte) data.get(current).get(i-1);
    }

    @Override
    public short getShort(int i) throws SQLException
    {
        return (Short) data.get(current).get(i-1);
    }

    @Override
    public int getInt(int i) throws SQLException
    {
        Object value = data.get(current).get(i - 1);
        if (value == null || !(value instanceof Integer))
            return 0;
        else
            return (Integer) value;
    }

    @Override
    public long getLong(int i) throws SQLException
    {
        return (Long) data.get(current).get(i-1);
    }

    @Override
    public float getFloat(int i) throws SQLException
    {
        return (Float) data.get(current).get(i-1);
    }

    @Override
    public double getDouble(int i) throws SQLException
    {
        return (Double) data.get(current).get(i-1);
    }

    @Override
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException
    {
        return (BigDecimal) data.get(current).get(i-1);
    }

    @Override
    public byte[] getBytes(int i) throws SQLException
    {
        return (byte[]) data.get(current).get(i-1);
    }

    @Override
    public Date getDate(int i) throws SQLException
    {
        return (Date) data.get(current).get(i-1);
    }

    @Override
    public Time getTime(int i) throws SQLException
    {
        return (Time) data.get(current).get(i-1);
    }

    @Override
    public Timestamp getTimestamp(int i) throws SQLException
    {
        return (Timestamp) data.get(current).get(i-1);
    }

    @Override
    public InputStream getAsciiStream(int i) throws SQLException
    {
        return (InputStream) data.get(current).get(i-1);
    }

    @Override
    public InputStream getUnicodeStream(int i) throws SQLException
    {
        return (InputStream) data.get(current).get(i-1);
    }

    @Override
    public InputStream getBinaryStream(int i) throws SQLException
    {
        return (InputStream) data.get(current).get(i-1);
    }

    @Override
    public String getString(String s) throws SQLException
    {
        return getString(getColumnIndex(s));
    }

    private int getColumnIndex(String s) throws SQLException
    {
        for (int i = 0; i < columns.size(); i++)
        {
            ColumnMetaData columnMetaData = columns.get(i);
            if (columnMetaData.getName().equals(s))
                return i+1;
        }
        throw new SQLException("No such column:"+s);
    }

    @Override
    public boolean getBoolean(String s) throws SQLException
    {
        return false;
    }

    @Override
    public byte getByte(String s) throws SQLException
    {
        return 0;
    }

    @Override
    public short getShort(String s) throws SQLException
    {
        return 0;
    }

    @Override
    public int getInt(String s) throws SQLException
    {
        return 0;
    }

    @Override
    public long getLong(String s) throws SQLException
    {
        return 0;
    }

    @Override
    public float getFloat(String s) throws SQLException
    {
        return 0;
    }

    @Override
    public double getDouble(String s) throws SQLException
    {
        return 0;
    }

    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException
    {
        return null;
    }

    @Override
    public byte[] getBytes(String s) throws SQLException
    {
        return new byte[0];
    }

    @Override
    public Date getDate(String s) throws SQLException
    {
        return null;
    }

    @Override
    public Time getTime(String s) throws SQLException
    {
        return null;
    }

    @Override
    public Timestamp getTimestamp(String s) throws SQLException
    {
        return null;
    }

    @Override
    public InputStream getAsciiStream(String s) throws SQLException
    {
        return null;
    }

    @Override
    public InputStream getUnicodeStream(String s) throws SQLException
    {
        return null;
    }

    @Override
    public InputStream getBinaryStream(String s) throws SQLException
    {
        return null;
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
    public String getCursorName() throws SQLException
    {
        return null;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException
    {
        return CallProxy.proxy(ResultSetMetaData.class, new ListMapResultSetMetaData());
    }

    @Override
    public Object getObject(int i) throws SQLException
    {
        return null;
    }

    @Override
    public Object getObject(String s) throws SQLException
    {
        return null;
    }

    @Override
    public int findColumn(String s) throws SQLException
    {
        return 0;
    }

    @Override
    public Reader getCharacterStream(int i) throws SQLException
    {
        return null;
    }

    @Override
    public Reader getCharacterStream(String s) throws SQLException
    {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException
    {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException
    {
        return null;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException
    {
        return false;
    }

    @Override
    public boolean isAfterLast() throws SQLException
    {
        return false;
    }

    @Override
    public boolean isFirst() throws SQLException
    {
        return false;
    }

    @Override
    public boolean isLast() throws SQLException
    {
        return false;
    }

    @Override
    public void beforeFirst() throws SQLException
    {
    }

    @Override
    public void afterLast() throws SQLException
    {
    }

    @Override
    public boolean first() throws SQLException
    {
        return false;
    }

    @Override
    public boolean last() throws SQLException
    {
        return false;
    }

    @Override
    public int getRow() throws SQLException
    {
        return 0;
    }

    @Override
    public boolean absolute(int i) throws SQLException
    {
        if (i > 0)
            current = i - 1;
        else
            current = data.size()-i;

        return false;
    }

    @Override
    public boolean relative(int i) throws SQLException
    {
        return false;
    }

    @Override
    public boolean previous() throws SQLException
    {
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
    public int getType() throws SQLException
    {
        return 0;
    }

    @Override
    public int getConcurrency() throws SQLException
    {
        return 0;
    }

    @Override
    public boolean rowUpdated() throws SQLException
    {
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException
    {
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException
    {
        return false;
    }

    @Override
    public void updateNull(int i) throws SQLException
    {
    }

    @Override
    public void updateBoolean(int i, boolean b) throws SQLException
    {
    }

    @Override
    public void updateByte(int i, byte b) throws SQLException
    {
    }

    @Override
    public void updateShort(int i, short i1) throws SQLException
    {
    }

    @Override
    public void updateInt(int i, int i1) throws SQLException
    {
    }

    @Override
    public void updateLong(int i, long l) throws SQLException
    {
    }

    @Override
    public void updateFloat(int i, float v) throws SQLException
    {
    }

    @Override
    public void updateDouble(int i, double v) throws SQLException
    {
    }

    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException
    {
    }

    @Override
    public void updateString(int i, String s) throws SQLException
    {
    }

    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException
    {
    }

    @Override
    public void updateDate(int i, Date date) throws SQLException
    {
    }

    @Override
    public void updateTime(int i, Time time) throws SQLException
    {
    }

    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException
    {
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i1) throws SQLException
    {
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i1) throws SQLException
    {
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, int i1) throws SQLException
    {
    }

    @Override
    public void updateObject(int i, Object o, int i1) throws SQLException
    {
    }

    @Override
    public void updateObject(int i, Object o) throws SQLException
    {
    }

    @Override
    public void updateNull(String s) throws SQLException
    {
    }

    @Override
    public void updateBoolean(String s, boolean b) throws SQLException
    {
    }

    @Override
    public void updateByte(String s, byte b) throws SQLException
    {
    }

    @Override
    public void updateShort(String s, short i) throws SQLException
    {
    }

    @Override
    public void updateInt(String s, int i) throws SQLException
    {
    }

    @Override
    public void updateLong(String s, long l) throws SQLException
    {
    }

    @Override
    public void updateFloat(String s, float v) throws SQLException
    {
    }

    @Override
    public void updateDouble(String s, double v) throws SQLException
    {
    }

    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException
    {
    }

    @Override
    public void updateString(String s, String s1) throws SQLException
    {
    }

    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException
    {
    }

    @Override
    public void updateDate(String s, Date date) throws SQLException
    {
    }

    @Override
    public void updateTime(String s, Time time) throws SQLException
    {
    }

    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException
    {
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException
    {
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException
    {
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException
    {
    }

    @Override
    public void updateObject(String s, Object o, int i) throws SQLException
    {
    }

    @Override
    public void updateObject(String s, Object o) throws SQLException
    {
    }

    @Override
    public void insertRow() throws SQLException
    {
    }

    @Override
    public void updateRow() throws SQLException
    {
    }

    @Override
    public void deleteRow() throws SQLException
    {
    }

    @Override
    public void refreshRow() throws SQLException
    {
    }

    @Override
    public void cancelRowUpdates() throws SQLException
    {
    }

    @Override
    public void moveToInsertRow() throws SQLException
    {
    }

    @Override
    public void moveToCurrentRow() throws SQLException
    {
    }

    @Override
    public Statement getStatement() throws SQLException
    {
        return null;
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> stringClassMap) throws SQLException
    {
        return null;
    }

    @Override
    public Ref getRef(int i) throws SQLException
    {
        return null;
    }

    @Override
    public Blob getBlob(int i) throws SQLException
    {
        return null;
    }

    @Override
    public Clob getClob(int i) throws SQLException
    {
        return null;
    }

    @Override
    public Array getArray(int i) throws SQLException
    {
        return null;
    }

    @Override
    public Object getObject(String s, Map<String, Class<?>> stringClassMap) throws SQLException
    {
        return null;
    }

    @Override
    public Ref getRef(String s) throws SQLException
    {
        return null;
    }

    @Override
    public Blob getBlob(String s) throws SQLException
    {
        return null;
    }

    @Override
    public Clob getClob(String s) throws SQLException
    {
        return null;
    }

    @Override
    public Array getArray(String s) throws SQLException
    {
        return null;
    }

    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException
    {
        return null;
    }

    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException
    {
        return null;
    }

    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException
    {
        return null;
    }

    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException
    {
        return null;
    }

    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException
    {
        return null;
    }

    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException
    {
        return null;
    }

    @Override
    public URL getURL(int i) throws SQLException
    {
        return null;
    }

    @Override
    public URL getURL(String s) throws SQLException
    {
        return null;
    }

    @Override
    public void updateRef(int i, Ref ref) throws SQLException
    {
    }

    @Override
    public void updateRef(String s, Ref ref) throws SQLException
    {
    }

    @Override
    public void updateBlob(int i, Blob blob) throws SQLException
    {
    }

    @Override
    public void updateBlob(String s, Blob blob) throws SQLException
    {
    }

    @Override
    public void updateClob(int i, Clob clob) throws SQLException
    {
    }

    @Override
    public void updateClob(String s, Clob clob) throws SQLException
    {
    }

    @Override
    public void updateArray(int i, Array array) throws SQLException
    {
    }

    @Override
    public void updateArray(String s, Array array) throws SQLException
    {
    }

    @Override
    public RowId getRowId(int i) throws SQLException
    {
        return null;
    }

    @Override
    public RowId getRowId(String s) throws SQLException
    {
        return null;
    }

    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException
    {
    }

    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException
    {
    }

    @Override
    public int getHoldability() throws SQLException
    {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        return closed;
    }

    @Override
    public void updateNString(int i, String s) throws SQLException
    {
    }

    @Override
    public void updateNString(String s, String s1) throws SQLException
    {
    }

    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException
    {
    }

    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException
    {
    }

    @Override
    public NClob getNClob(int i) throws SQLException
    {
        return null;
    }

    @Override
    public NClob getNClob(String s) throws SQLException
    {
        return null;
    }

    @Override
    public SQLXML getSQLXML(int i) throws SQLException
    {
        return null;
    }

    @Override
    public SQLXML getSQLXML(String s) throws SQLException
    {
        return null;
    }

    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException
    {
    }

    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException
    {
    }

    @Override
    public String getNString(int i) throws SQLException
    {
        return null;
    }

    @Override
    public String getNString(String s) throws SQLException
    {
        return null;
    }

    @Override
    public Reader getNCharacterStream(int i) throws SQLException
    {
        return null;
    }

    @Override
    public Reader getNCharacterStream(String s) throws SQLException
    {
        return null;
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException
    {
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException
    {
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException
    {
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException
    {
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException
    {
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException
    {
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException
    {
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException
    {
    }

    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException
    {
    }

    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException
    {
    }

    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException
    {
    }

    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException
    {
    }

    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException
    {
    }

    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException
    {
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException
    {
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException
    {
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException
    {
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException
    {
    }

    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException
    {
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException
    {
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException
    {
    }

    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException
    {
    }

    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException
    {
    }

    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException
    {
    }

    @Override
    public void updateClob(int i, Reader reader) throws SQLException
    {
    }

    @Override
    public void updateClob(String s, Reader reader) throws SQLException
    {
    }

    @Override
    public void updateNClob(int i, Reader reader) throws SQLException
    {
    }

    @Override
    public void updateNClob(String s, Reader reader) throws SQLException
    {
    }

    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException
    {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException
    {
        return false;
    }

    @Override
    public String toString()
    {
        String result = "Columns:"+columns;
        for (List<Object> row : data)
        {
            result+="\n"+row;
        }
        return result;
    }

    private class ListMapResultSetMetaData
        implements ResultSetMetaData
    {
        @Override
        public int getColumnCount() throws SQLException
        {
            return columns.size();
        }

        @Override
        public boolean isAutoIncrement(int i) throws SQLException
        {
            return false;
        }

        @Override
        public boolean isCaseSensitive(int i) throws SQLException
        {
            return false;
        }

        @Override
        public boolean isSearchable(int i) throws SQLException
        {
            return false;
        }

        @Override
        public boolean isCurrency(int i) throws SQLException
        {
            return false;
        }

        @Override
        public int isNullable(int i) throws SQLException
        {
            return 0;
        }

        @Override
        public boolean isSigned(int i) throws SQLException
        {
            return false;
        }

        @Override
        public int getColumnDisplaySize(int i) throws SQLException
        {
            return 20;
        }

        @Override
        public String getColumnLabel(int i) throws SQLException
        {
            return columns.get(i-1).getName();
        }

        @Override
        public String getColumnName(int i) throws SQLException
        {
            return columns.get(i-1).getName();
        }

        @Override
        public String getSchemaName(int i) throws SQLException
        {
            return null;
        }

        @Override
        public int getPrecision(int i) throws SQLException
        {
            return 0;
        }

        @Override
        public int getScale(int i) throws SQLException
        {
            return 0;
        }

        @Override
        public String getTableName(int i) throws SQLException
        {
            return null;
        }

        @Override
        public String getCatalogName(int i) throws SQLException
        {
            return null;
        }

        @Override
        public int getColumnType(int i) throws SQLException
        {
            return Types.VARCHAR;
        }

        @Override
        public String getColumnTypeName(int i) throws SQLException
        {
            return String.class.getName();
        }

        @Override
        public boolean isReadOnly(int i) throws SQLException
        {
            return false;
        }

        @Override
        public boolean isWritable(int i) throws SQLException
        {
            return false;
        }

        @Override
        public boolean isDefinitelyWritable(int i) throws SQLException
        {
            return false;
        }

        @Override
        public String getColumnClassName(int i) throws SQLException
        {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> tClass) throws SQLException
        {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> aClass) throws SQLException
        {
            return false;
        }
    }

    public static class ColumnMetaData
    {
        private String name;
        private String typeName;
        private int dataType;

        public ColumnMetaData(String name, String typeName, int dataType)
        {
            this.name = name;
            this.typeName = typeName;
            this.dataType = dataType;
        }

        public String getName()
        {
            return name;
        }

        public String getTypeName()
        {
            return typeName;
        }

        public int getDataType()
        {
            return dataType;
        }

        public String toString()
        {
            return name+":"+ typeName;
        }
    }
}
