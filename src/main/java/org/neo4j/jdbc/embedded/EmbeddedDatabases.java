package org.neo4j.jdbc.embedded;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.jdbc.Databases;
import org.neo4j.jdbc.QueryExecutor;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;
import org.neo4j.test.ImpermanentGraphDatabase;

import java.util.Properties;
import java.util.WeakHashMap;

/**
 * @author mh
 * @since 15.06.12
 */
public class EmbeddedDatabases implements Databases {
    enum Type {
        mem {
            @Override
            public GraphDatabaseService create(String name, Properties properties) {
                return new ImpermanentGraphDatabase();
            }
        }, instance {
            @Override
            public GraphDatabaseService create(String name, Properties properties) {
                return (GraphDatabaseService) properties.remove(name);
            }
        }, file {
            @Override
            public GraphDatabaseService create(String name, Properties properties) {
                if (isReadOnly(properties)) {
                    return new EmbeddedReadOnlyGraphDatabase(name);
                } else {
                    return new EmbeddedGraphDatabase(name);
                }
            }
        };

        public abstract GraphDatabaseService create(String name, Properties properties);

        protected boolean isReadOnly(Properties properties) {
            return properties != null && properties.getProperty("readonly", "false").equalsIgnoreCase("true");
        }

        public String getName(String url) {
            if (url.matches(":" + name() + ":.+")) return url.substring(name().length() + 2);
            return null;
        }
    }

    private final WeakHashMap<String, GraphDatabaseService> databases = new WeakHashMap<String, GraphDatabaseService>();

    public synchronized GraphDatabaseService createDatabase(String connectionUrl, Properties properties) {
        for (Type type : Type.values()) {
            String name = type.getName(connectionUrl);
            if (name != null) {
                GraphDatabaseService gds = databases.get(name);
                if (gds == null) {
                    gds = type.create(name, properties);
                    databases.put(name, gds);
                }
                return gds;
            }
        }
        return new ImpermanentGraphDatabase();
    }

    public QueryExecutor createExecutor(String connectionUrl, Properties properties) {
        GraphDatabaseService gds = createDatabase(connectionUrl, properties);
        return new EmbeddedQueryExecutor(gds);
    }
}
