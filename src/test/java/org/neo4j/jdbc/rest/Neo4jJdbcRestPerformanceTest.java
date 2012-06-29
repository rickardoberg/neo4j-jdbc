package org.neo4j.jdbc.rest;

import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.jdbc.Neo4jJdbcPerformanceTestRunner;
import org.neo4j.jdbc.Neo4jJdbcTest;

import java.sql.SQLException;

/**
 * @author mh
 * @since 14.06.12
 */
@Ignore("Perf-Test")
public class Neo4jJdbcRestPerformanceTest extends Neo4jJdbcTest {

    private Neo4jJdbcPerformanceTestRunner runner;

    public Neo4jJdbcRestPerformanceTest(Neo4jJdbcTest.Mode mode) throws SQLException {
        super(mode);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        runner = new Neo4jJdbcPerformanceTestRunner(gdb);
    }

    @Test
    public void testExecuteStatement() throws Exception {
        runner.executeMultiple(conn);
    }
}
