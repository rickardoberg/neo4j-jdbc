package org.neo4j.jdbc.embedded;

import org.junit.Test;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.test.ImpermanentGraphDatabase;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * @author mh
 * @since 15.06.12
 */
public class CypherCreateTest {
    @Test
    public void testCreateNodeWithParam() throws Exception {
        final ImpermanentGraphDatabase gdb = new ImpermanentGraphDatabase();
        final ExecutionEngine engine = new ExecutionEngine(gdb);
        engine.execute("create n={name:{1}}", Collections.<String,Object>singletonMap("1", "test"));
        final Node node = gdb.getNodeById(1);
        assertEquals("test",node.getProperty("name"));
    }
}
