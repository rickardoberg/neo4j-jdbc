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

import org.neo4j.cypherdsl.Execute;
import org.neo4j.cypherdsl.ExecuteWithParameters;
import org.neo4j.cypherdsl.query.Expression;

import static org.neo4j.cypherdsl.CypherQuery.start;
import static org.neo4j.cypherdsl.query.Expression.param;
import static org.neo4j.cypherdsl.query.MatchExpression.path;
import static org.neo4j.cypherdsl.query.ReturnExpression.properties;
import static org.neo4j.cypherdsl.query.StartExpression.node;
import static org.neo4j.cypherdsl.query.WhereExpression.prop;

/**
 * This class contains all the Cypher queries that the driver needs to issue.
 */
public class DriverQueries
{
    public static DriverQueries QUERIES = new DriverQueries();

    public Execute getTables()
    {
        return start(node("n", 0)).
                match(path().from("n").out("TYPE").to("type")).
                returns(properties("type.type"));
    }

    public Execute getColumns()
    {
        return start(node("n", 0)).
                match(path().from("n").out("TYPE").to("type").link().out("HAS_PROPERTY").to("property")).
                returns(properties("type.type", "property.name", "property.type"));
    }

    public ExecuteWithParameters getColumns(String typeName)
    {
        return start(node("n", 0)).
                match(path().from("n").out("TYPE").to("type").link().out("HAS_PROPERTY").to("property")).
                where(prop("type.type").eq(param("typename"))).
                returns(properties("type.type", "property.name", "property.type")).parameter("typename", typeName);
    }
}
