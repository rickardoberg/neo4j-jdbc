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
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * TODO
 */
public class DriverTest extends Neo4jJdbcTest
{
    Driver driver;

    public DriverTest(Mode mode) throws SQLException {
        super(mode);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        driver = new Driver();
    }

    @Test
    public void testAcceptsURL() throws SQLException
    {
        Assert.assertTrue(driver.acceptsURL(jdbcUrl()));
        Assert.assertTrue(!driver.acceptsURL("jdbc:derby://localhost:7474/"));
    }
    
    @Test
    public void testConnect() throws SQLException
    {
    	final Properties properties = new Properties();
    	properties.put("db",gdb);
        Assert.assertNotNull(driver.connect("jdbc:neo4j:instance:db", properties));
        Assert.assertNull(driver.connect("jdbc:derby://localhost:7474/", properties));
    }

    @Test
    public void testURLProperties() throws SQLException
    {
        final Properties properties = new Properties();
        driver.parseUrlProperties(jdbcUrl()+"?debug=false", properties);
        Assert.assertThat(properties.getProperty("debug"), CoreMatchers.equalTo("false"));
    }
    
    @Test
    public void testDriverRegistration()
    {
        try
        {
            java.sql.Driver driver = DriverManager.getDriver(jdbcUrl());
            Assert.assertNotNull(driver);
            Assert.assertEquals(this.driver.getClass(), driver.getClass());
        } catch (SQLException e)
        {
            Assert.fail(e.getLocalizedMessage());
        }

    }

    @Test
    public void testDriverService()
    {
        ServiceLoader<java.sql.Driver> serviceLoader = ServiceLoader.load(java.sql.Driver.class);
        for (java.sql.Driver driver : serviceLoader)
        {
            if (Driver.class.isInstance(driver))
                return;
        }
        Assert.fail(Driver.class.getName() + " not registered as a Service");
    }
}
