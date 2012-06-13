package org.neo4j.jdbc.ext;

import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.jdbc.Connections;
import org.neo4j.jdbc.Neo4jJdbcTest;

import java.sql.SQLException;

/**
 * @author mh
 * @since 12.06.12
 */
@Ignore
public class DbVisualizerConnectionTest extends Neo4jJdbcTest{
    @Override
    public void setUp() throws Exception {
        System.setProperty(Connections.DB_VIS,"true");
        super.setUp();
    }

    @Override
    public void tearDown() {
        super.tearDown();
        System.setProperty(Connections.DB_VIS, null);
        System.out.println(System.getProperties());
    }

    @Test
    public void testExecuteQuery() throws Exception {
        conn.createStatement().executeQuery("$columns$");
    }
}
