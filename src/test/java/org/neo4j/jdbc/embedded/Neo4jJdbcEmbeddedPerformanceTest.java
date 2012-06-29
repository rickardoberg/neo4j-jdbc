package org.neo4j.jdbc.embedded;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.jdbc.Neo4jJdbcPerformanceTestRunner;
import org.neo4j.test.ImpermanentGraphDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author mh
 * @since 14.06.12
 */
public class Neo4jJdbcEmbeddedPerformanceTest {

    private ImpermanentGraphDatabase gdb;
    private Neo4jJdbcPerformanceTestRunner runner;

    @Before
    public void setUp() throws Exception {
        gdb = new ImpermanentGraphDatabase();
        runner = new Neo4jJdbcPerformanceTestRunner(gdb);
    }

    @Test
    public void testExecuteStatementEmbedded() throws Exception {
        final Properties props = new Properties();
        props.put("db", gdb);
        final Connection con = DriverManager.getConnection("jdbc:neo4j:instance:db", props);
        runner.executeMultiple(con);
    }
}
