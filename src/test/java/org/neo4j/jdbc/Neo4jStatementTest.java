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

import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.tooling.GlobalGraphOperations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * TODO
 */
public class Neo4jStatementTest extends Neo4jJdbcTest {

    public Neo4jStatementTest(Mode mode) throws SQLException {
        super(mode);
    }

    @Test
    public void testExecuteStatement() throws Exception {
        final ResultSet rs = conn.createStatement().executeQuery(REFERENCE_NODE_ID_QUERY);
        assertTrue(rs.next());
        assertEquals(0, ((Number)rs.getObject("id")).intValue());
        assertEquals(0L, rs.getLong("id"));
        assertEquals(0, ((Number)rs.getObject(1)).intValue());
        assertEquals(0L, rs.getLong(1));
        assertFalse(rs.next());
    }

    @Test(expected = SQLException.class)
    public void testPreparedStatementMissingParameter() throws Exception {
        final PreparedStatement ps = conn.prepareStatement("start n=node({1}) return ID(n) as id");
        final ResultSet rs = ps.executeQuery();
        rs.next();

    }
    @Test
    public void testExecutePreparedStatement() throws Exception {
        final PreparedStatement ps = conn.prepareStatement("start n=node({1}) return ID(n) as id");
        ps.setLong(1,0L);
        final ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(0, ((Number)rs.getObject("id")).intValue());
        assertEquals(0L, rs.getLong("id"));
        assertEquals(0, ((Number)rs.getObject(1)).intValue());
        assertEquals(0L, rs.getLong(1));
        assertFalse(rs.next());
    }

    @Test
    public void testCreateNodeStatement() throws Exception {
        final PreparedStatement ps = conn.prepareStatement("create n={name:{1}}");
        ps.setString(1, "test");
        // TODO int count = ps.executeUpdate();
        int count = 0;
        ps.executeUpdate();
        for (Node node : GlobalGraphOperations.at(gdb).getAllNodes()) {
            if (node.equals(gdb.getReferenceNode())) continue;
            assertEquals("test", node.getProperty("name"));
            count ++;
        }
        assertEquals(1, count);
    }

    @Test(expected = SQLException.class)
    public void testCreateOnReadonlyConnection() throws Exception {
        conn.setReadOnly(true);
        conn.createStatement().executeUpdate("create n={name:{1}}");
    }

    @Test(expected = SQLDataException.class)
    public void testColumnZero() throws Exception {
        final ResultSet rs = conn.createStatement().executeQuery(REFERENCE_NODE_ID_QUERY);
        assertTrue(rs.next());
        assertEquals(0, rs.getObject(0));
        assertFalse(rs.next());
    }
    @Test(expected = SQLDataException.class)
    public void testColumnLargerThan() throws Exception {
        final ResultSet rs = conn.createStatement().executeQuery(REFERENCE_NODE_ID_QUERY);
        rs.next();
        rs.getObject(2);
    }
    @Test(expected = SQLException.class)
    public void testInvalidColumnName() throws Exception {
        final ResultSet rs = conn.createStatement().executeQuery(REFERENCE_NODE_ID_QUERY);
        rs.next();
        rs.getObject("foo");
    }
}
