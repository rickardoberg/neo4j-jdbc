package org.neo4j.jdbc.embedded;

import org.junit.Test;
import org.neo4j.jdbc.ExecutionResult;
import org.neo4j.test.ImpermanentGraphDatabase;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author mh
 * @since 15.06.12
 */
public class EmbeddedQueryExecutorTest {
    @Test
    public void testDoExecuteQuery() throws Exception {
        final EmbeddedQueryExecutor executor = new EmbeddedQueryExecutor(new ImpermanentGraphDatabase());
        final ExecutionResult result = executor.executeQuery("start n=node(0) return ID(n) as n", null);
        assertEquals(asList("n"),result.columns());
        final Object[] row = result.iterator().next();
        assertEquals(1, row.length);
        assertEquals(0L, row[0]);
    }
}
