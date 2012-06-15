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

package org.neo4j.jdbc.ext;

import org.neo4j.jdbc.Driver;
import org.neo4j.jdbc.Neo4jConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * DbVisualizer specific Neo4j connection. Contains workarounds to get it to work with DbVisualizer.
 */
public class DbVisualizerConnection
    extends Neo4jConnection
    implements Connection
{
    public DbVisualizerConnection(Driver driver, String url, Properties properties) throws SQLException
    {
        super(driver, url, properties);
    }

    @Override
    public ResultSet executeQuery(String query, Map<String, Object> parameters) throws SQLException
    {
        {
            if (query.contains("$columns$"))
            {
                int idx = query.indexOf("\"");
                int idx2 = query.indexOf("\"", idx+1);
                final String type = query.substring(idx+1, idx2);

                query = query.replace("$columns$", super.tableColumns(type, "instance."));
            }
        }

        return super.executeQuery(query, parameters);
    }
}
