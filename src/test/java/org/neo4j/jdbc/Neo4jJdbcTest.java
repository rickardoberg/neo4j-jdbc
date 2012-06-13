package org.neo4j.jdbc;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.server.web.WebServer;
import org.neo4j.test.ImpermanentGraphDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

import static org.neo4j.jdbc.TestWebServer.startWebServer;

/**
 * @author mh
 * @since 12.06.12
 */
public class Neo4jJdbcTest {
    protected static final String REFERENCE_NODE_ID_QUERY = "start n=node(0) return ID(n) as id";
    protected Neo4jConnection conn;
    public static final int PORT = 7475;
    protected static ImpermanentGraphDatabase gdb;
    private static WebServer webServer;

    @BeforeClass
    public static void before() {
        gdb = new ImpermanentGraphDatabase();
        webServer = startWebServer(gdb, PORT);
    }

    @AfterClass
    public static void after() {
        try {
            webServer.stop();
            gdb.shutdown();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws SQLException, Exception {
        gdb.cleanContent(true);
        conn = new Driver().connect(jdbcUrl(), new Properties());
    }

    protected String jdbcUrl() {
        return "jdbc:neo4j://localhost:" + PORT + "/";
    }

    @After
    public void tearDown() {
        try {
            if (conn != null) conn.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void createTableMetaData(GraphDatabaseService gdb, String typeName, String propName, String propType) {
        // CYPHER 1.7 START n=node(0)
        // MATCH (n)-[:TYPE]->(type)-[:HAS_PROPERTY]->(property)
        // WHERE type.type={typename}
        // RETURN type.type,property.name,property.type
        final Transaction tx = gdb.beginTx();
        final Node root = gdb.getReferenceNode();
        final Node type = gdb.createNode();
        type.setProperty("type",typeName);
        root.createRelationshipTo(type, DynamicRelationshipType.withName("TYPE"));
        final Node property = gdb.createNode();
        property.setProperty("name",propName);
        property.setProperty("type",propType);
        type.createRelationshipTo(property,DynamicRelationshipType.withName("HAS_PROPERTY"));
        tx.success();tx.finish();
    }

    protected void dumpColumns(ResultSet rs) throws SQLException {
        final ResultSetMetaData meta = rs.getMetaData();
        final int cols = meta.getColumnCount();
        for (int col=1;col<cols;col++) {
            System.out.println(meta.getColumnName(col));
        }
    }
}
