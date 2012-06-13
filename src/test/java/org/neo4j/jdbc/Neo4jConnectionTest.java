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

import org.hamcrest.CoreMatchers;
import org.junit.*;

import java.sql.*;

import org.neo4j.cypherdsl.query.CommonProperty;
import org.neo4j.cypherdsl.query.Expression;

import static org.junit.Assert.*;

public class Neo4jConnectionTest extends Neo4jJdbcTest {

    private String columName = "propName";
    private String tableName = "test";
    private String columnPrefix = "_";
    private final String columnType = "String";

    @Test
    public void testGetMetaData() throws SQLException
    {
        DatabaseMetaData metaData = conn.getMetaData();
        Assert.assertThat(metaData, CoreMatchers.<DatabaseMetaData>notNullValue());
        Assert.assertTrue(metaData.getDatabaseProductVersion().startsWith("1."));
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        createTableMetaData(gdb, tableName, columName, columnType);
    }

    @Test
    public void testGetTableMetaDataTables() throws Exception {
        final ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null);
        assertTrue(rs.next());
        assertEquals(tableName,rs.getString("TABLE_NAME"));
        assertFalse(rs.next());
    }

    @Test
    public void testGetTableMetaDataColumns() throws Exception {
        final ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, null);
        assertTrue(rs.next());
        dumpColumns(rs);
        assertEquals(tableName, rs.getString("TABLE_NAME"));
        assertEquals(columName, rs.getString("COLUMN_NAME"));
        assertEquals(Types.VARCHAR,rs.getInt("DATA_TYPE"));
        assertFalse(rs.next());
    }

    @Test
    public void testTableColumns() throws Exception {
        final String res = conn.tableColumns(tableName, columnPrefix);
        assertEquals(columnPrefix + columName,res);
    }
    @Test
    public void testProperties() throws Exception {
        final Iterable<Expression> res = conn.returnProperties(tableName, columnPrefix);
        boolean found=false;
        for (Expression expression : res) {
            assertTrue(expression instanceof CommonProperty);
            final CommonProperty property = (CommonProperty) expression;
            assertEquals(columName,property.name.name);
            found=true;
        }
        assertTrue(found);
    }
}
