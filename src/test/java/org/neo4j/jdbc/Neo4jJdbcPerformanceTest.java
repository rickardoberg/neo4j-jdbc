package org.neo4j.jdbc;

import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author mh
 * @since 14.06.12
 */
public class Neo4jJdbcPerformanceTest extends Neo4jJdbcTest {

    private static final int RUNS = 10;
    private static final int COUNT = 5000;

    @Override
    public void setUp() throws SQLException, Exception {
        super.setUp();
        createData(gdb, COUNT);
    }

    private void createData(GraphDatabaseService gdb, int count) {
        final DynamicRelationshipType type = DynamicRelationshipType.withName("RELATED_TO");
        final Transaction tx = gdb.beginTx();
        final Node node = gdb.getReferenceNode();
        for (int i=0;i<count;i++) {
            final Node n = gdb.createNode();
            node.createRelationshipTo(n, type);
        }
        tx.success();tx.finish();
    }

    @Test
        public void testExecuteStatement() throws Exception {
            long delta = 0;
            execute();
            System.out.println("START");
            for (int i=0;i< RUNS;i++) {
                delta += execute();
            }
            System.out.println("Query took " + delta/RUNS + " ms.");
        }

    private long execute() throws SQLException {
        long time=System.currentTimeMillis();
        final ResultSet rs = conn.createStatement().executeQuery("start n=node(*) match p=n-[r]->m return n,ID(n) as id, r,m,p");
        int count=0;
        while (rs.next()) {
            rs.getInt("id");
            count++;
        }
        return System.currentTimeMillis()-time;
    }
}
