package org.neo4j.jdbc;

import java.util.Map;

/**
* @author mh
* @since 15.06.12
*/
public interface QueryExecutor {
    ExecutionResult doExecuteQuery(String query, Map<String, Object> parameters) throws Exception;

    void stop() throws Exception;

    Version getVersion();
}
