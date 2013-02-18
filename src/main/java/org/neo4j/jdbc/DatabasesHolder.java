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

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is a simple singleton holder for the a database instance
 */
public class DatabasesHolder {
	private final static Log log = LogFactory.getLog(Driver.class);
	
	private static DatabasesHolder holder=null;
	
	public static final DatabasesHolder INSTANCE(){
		if (holder!=null)return holder;
		synchronized(log){
			if (holder!=null)return holder;
			holder=new DatabasesHolder();
		}
		return holder;
	}
	
	/** Singleton No instance */
	private DatabasesHolder(){}
	
	private final Databases databases = createDatabases();

    private Databases createDatabases() {
        try {
            //return (Databases)Class.forName("org.neo4j.jdbc.embedded.EmbeddedDatabases").newInstance();
        	return new org.neo4j.jdbc.embedded.EmbeddedDatabases();
        } catch (Throwable e) {
            if (log.isInfoEnabled()) log.info("Embedded Neo4j support not enabled "+e.getMessage());
            return null;
        }
    }

    public QueryExecutor createExecutor(String connectionUrl, Properties properties) throws SQLException {
        if (databases == null)
            throw new SQLFeatureNotSupportedException("Embedded Neo4j not available please add neo4j-kernel, -index and -cypher to the classpath");
        return databases.createExecutor(connectionUrl,properties);
    }
}
