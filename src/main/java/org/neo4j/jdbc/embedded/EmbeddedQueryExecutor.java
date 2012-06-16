package org.neo4j.jdbc.embedded;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.helpers.collection.IteratorWrapper;
import org.neo4j.jdbc.ExecutionResult;
import org.neo4j.jdbc.QueryExecutor;
import org.neo4j.jdbc.Version;
import org.neo4j.kernel.GraphDatabaseAPI;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author mh
 * @since 15.06.12
 */
public class EmbeddedQueryExecutor implements QueryExecutor {

    private static sun.misc.Unsafe unsafe = getUnsafe();

    private static Unsafe getUnsafe() {
        try {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Error accessing unsafe"+e);
        }
    }

    private final ExecutionEngine executionEngine;
    private final GraphDatabaseService gds;

    public EmbeddedQueryExecutor(GraphDatabaseService gds) {
        this.gds = gds;
        executionEngine = new ExecutionEngine(gds);
    }

    @Override
    public ExecutionResult executeQuery(final String query, Map<String, Object> parameters) throws Exception {
        final Map<String, Object> params = parameters == null ? Collections.<String, Object>emptyMap() : parameters;
        final org.neo4j.cypher.javacompat.ExecutionResult result = executionEngine.execute(query, params);
        final List<String> columns = result.columns();
        final int cols = columns.size();
        final Object[] resultRow = new Object[cols];
        return new ExecutionResult(columns,new IteratorWrapper<Object[],Map<String,Object>>(result.iterator()) {
            @Override
            public boolean hasNext() {
                try {
                    return super.hasNext();
                } catch(Exception e)  {
                    return handleException(e, query);
                }
            }

            protected Object[] underlyingObjectToObject(Map<String, Object> row) {
                for (int i = 0; i < cols; i++) {
                    resultRow[i]=row.get(columns.get(i));
                }
                return resultRow;
            }
        });
    }

    private boolean handleException(Exception cause, String query) {
        final SQLException sqlException = new SQLException("Error executing query: " + query, cause);
        if (unsafe!=null) {
            unsafe.throwException(sqlException);
            return false;
        }
        else {
            throw new RuntimeException(sqlException);
        }
    }

    @Override
    public void stop() throws Exception {
        // don't own the db, will be stopped when driver's stopped
    }

    @Override
    public Version getVersion() {
        return new Version(((GraphDatabaseAPI)gds).getKernelData().version().getVersion());
    }
}
