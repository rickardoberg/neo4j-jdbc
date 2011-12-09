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

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
import org.restlet.resource.ClientResource;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

/**
 * TODO
 */
public class Driver
    implements java.sql.Driver
{

    private Client client;

    public Driver()
    {
        client = new Client("HTTP");
    }

    public Connection connect(String s, Properties properties) throws SQLException
    {
        s = "http"+s.substring("jdbc:neo4j".length());
        Reference ref = new Reference(new Reference(s), "/");
        Context context = new Context();
        context.setClientDispatcher(client);
        ClientResource resource = new ClientResource(context, ref);
        resource.getClientInfo().setAcceptedMediaTypes(Collections.singletonList(new Preference<MediaType>(MediaType.APPLICATION_JSON)));

        return new Neo4jConnection(resource);
    }

    public boolean acceptsURL(String s) throws SQLException
    {
        return s.startsWith("jdbc:neo4j");
    }

    public DriverPropertyInfo[] getPropertyInfo(String s, Properties properties) throws SQLException
    {
        return new DriverPropertyInfo[0];
    }

    public int getMajorVersion()
    {
        return 1;
    }

    public int getMinorVersion()
    {
        return 0;
    }

    public boolean jdbcCompliant()
    {
        return true;
    }
}
