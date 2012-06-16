package org.neo4j.jdbc;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.server.web.WebServer;
import org.neo4j.test.ImpermanentGraphDatabase;

import java.sql.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Properties;

import static org.neo4j.jdbc.TestWebServer.startWebServer;

/**
 * @author mh
 * @since 12.06.12
 */
@RunWith(Parameterized.class)
@Ignore
public class Neo4jJdbcTest {
    public static final int PORT = 7475;
    protected static final String REFERENCE_NODE_ID_QUERY = "start n=node(0) return ID(n) as id";
    protected Neo4jConnection conn;
    protected static ImpermanentGraphDatabase gdb;
    private static WebServer webServer;
    protected final Mode mode;

    public enum Mode { embedded, server }
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.<Object[]>asList(new Object[]{Mode.embedded},new Object[]{Mode.server});
    }

    @BeforeClass
    public static void before() {
        gdb = new ImpermanentGraphDatabase();
    }
    public Neo4jJdbcTest(Mode mode) throws SQLException {
        this.mode = mode;
        System.out.println("Mode "+mode);
        final Driver driver = new Driver();
        final Properties props = new Properties();
        gdb.cleanContent(true);
        switch (mode) {
            case embedded:
                props.put("db",gdb);
                conn = driver.connect("jdbc:neo4j:instance:db", props);
                break;
            case server:
                if (webServer==null) {
                    webServer = startWebServer(gdb, PORT);
                }
                conn = driver.connect("jdbc:neo4j://localhost:"+PORT, props);
                break;
        }
    }


    @AfterClass
    public static void after() {
        try {
            if (webServer!=null) {
                webServer.stop();
                webServer = null;
            }
            gdb.shutdown();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws SQLException, Exception {
        gdb.cleanContent(true);
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
