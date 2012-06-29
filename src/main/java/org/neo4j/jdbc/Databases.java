package org.neo4j.jdbc;

import org.neo4j.graphdb.GraphDatabaseService;

import java.util.Properties;

/**
 * @author mh
 * @since 15.06.12
 */
public interface Databases {
    QueryExecutor createExecutor(String connectionUrl, Properties properties);
}
