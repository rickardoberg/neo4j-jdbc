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
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Properties;

/**
 * TODO
 */
public class DriverTest
{
    Driver driver;

    @Before
    public void before()
    {
        driver = new Driver();
    }

    @Test
    public void testAcceptsURL() throws SQLException
    {
        Assert.assertTrue(driver.acceptsURL("jdbc:neo4j://localhost:7474/db/data"));
        Assert.assertTrue(!driver.acceptsURL("jdbc:derby://localhost:7474/"));
    }

    @Test
    public void testURLProperties() throws SQLException
    {
        Neo4jConnection conn = (Neo4jConnection) driver.connect("jdbc:neo4j://localhost:7474/?debug=false", new Properties());

        Assert.assertThat(conn.getProperties().getProperty("debug"), CoreMatchers.equalTo("false"));
    }
}
