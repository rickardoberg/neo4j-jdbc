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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.server.NeoServerWithEmbeddedWebServer;
import org.neo4j.server.helpers.ServerBuilder;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;

public class Neo4jQueryNodeTest
{

    private static NeoServerWithEmbeddedWebServer server;

    private static Connection conn;

    @BeforeClass
    public static void setupDatabase() throws IOException
    {
        String testDatabase = new File(Neo4jQueryNodeTest.class.getResource("/graph.db/messages.log").getPath()).getParent();

        server = ServerBuilder.server().persistent().usingDatabaseDir(testDatabase).build();
        server.start();
    }

    @BeforeClass
    public static void before() throws SQLException
    {
        conn = new Driver().connect("jdbc:neo4j://localhost:7475/", new Properties());
    }

    @AfterClass
    public static void after()
    {
        try
        {
            conn.close();
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void teardownDatabase()
    {
 //       server.stop();
    }

    @Test
    public void testGetTables() throws SQLException
    {
        ResultSet rs = conn.getMetaData().getTables(null, null, "%", null);

        while (rs.next())
        {
            System.out.println(rs.getString("TABLE_NAME"));
        }
    }

    @Test
    public void testRetrieveNodes() throws SQLException
    {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("start n=node(*) match p=n-[r?]-m return n,r,m,p,ID(n),length(p),n.name? as name limit 5");
        int count = 0;
        ResultSetMetaData metaData = rs.getMetaData();
        int cols = metaData.getColumnCount();
        Assert.assertThat(cols, is(7));
        while (rs.next())
        {
            for (int i = 1; i <= cols; i++)
            {
                //String columnName = metaData.getColumnName(i);
                //System.out.println(columnName);
                System.out.print(rs.getObject(i) + "\t");
            }
            System.out.println();
            count++;
        }

        Assert.assertThat(count, is(5));
    }
}

