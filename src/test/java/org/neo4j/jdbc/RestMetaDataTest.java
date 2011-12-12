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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TODO
 */
public class RestMetaDataTest
{
    private static Connection conn;

    @BeforeClass
    public static void before() throws SQLException
    {
        conn = new Driver().connect("jdbc:neo4j://localhost:7474/", null);
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

    @Test
    public void testGetTables() throws SQLException
    {
        ResultSet rs = conn.getMetaData().getTables(null, null, "%", null);

        System.out.println(rs);
    }

    @Test
    public void testGetColumns() throws SQLException
    {
        ResultSet rs = conn.getMetaData().getColumns(null, null, "%", null);

        System.out.println(rs);
    }
}
