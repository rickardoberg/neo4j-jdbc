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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ResultSet implementation that is backed by an Iterator.
 */
public class IteratorResultSet extends AbstractResultSet
{
    private Iterator<List<Object>> data;
    private List<Object> currentRow;
    private int row = -1;

    public IteratorResultSet(List<Neo4jColumnMetaData> columns, Iterator<List<Object>> data, Neo4jConnection conn)
    {
        super(columns,conn);
        this.data = data;
    }

    @Override
    protected List<Object> currentRow() {
        return currentRow;
    }

    @Override
    public boolean next() throws SQLException
    {
        if (hasNext())
        {
            currentRow = data.next();
            row ++;
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public void close() throws SQLException
    {
        closed = true;
    }

    @Override
    public boolean wasNull() throws SQLException
    {
        return isBeforeFirst() && !hasNext();
    }

    private boolean hasNext() {
        return data.hasNext();
    }

    @Override
    public boolean isBeforeFirst() throws SQLException
    {
        return row == -1;
    }

    @Override
    public boolean isAfterLast() throws SQLException
    {
        return !hasNext();
    }

    @Override
    public boolean isFirst() throws SQLException
    {
        return row == 0;
    }

    @Override
    public boolean isLast() throws SQLException
    {
        return !hasNext();
    }

    @Override
    public void beforeFirst() throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public void afterLast() throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean first() throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean last() throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public int getRow() throws SQLException
    {
        return row;
    }

    @Override
    public boolean absolute(int i) throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean relative(int i) throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean previous() throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public void setFetchDirection(int i) throws SQLException
    {
        if (i != ResultSet.FETCH_FORWARD)
            throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public int getFetchDirection() throws SQLException
    {
        return ResultSet.FETCH_FORWARD;
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
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        return closed;
    }

    @Override
    public String toString()
    {
        return "Columns:"+columns +" current row "+row+": "+currentRow;
    }

}
