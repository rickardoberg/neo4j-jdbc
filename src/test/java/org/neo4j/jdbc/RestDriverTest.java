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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TODO
 */
public class RestDriverTest
{
    @Test
    public void testConnect() throws SQLException
    {
        Connection connect = new Driver().connect("jdbc:neo4j://localhost:7474/", null);
        connect.close();
    }

    @Test
    public void testAcceptsURL() throws SQLException
    {
        Assert.assertTrue(new Driver().acceptsURL("jdbc:neo4j://localhost:7474/"));
        Assert.assertFalse(new Driver().acceptsURL("jdbc:neo://localhost:7474/"));
    }
}
