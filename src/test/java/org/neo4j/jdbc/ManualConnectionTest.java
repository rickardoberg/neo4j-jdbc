package org.neo4j.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.neo4j.helpers.collection.MapUtil.map;

/**
 * @author mh
 * @since 03.12.12
 */
public class ManualConnectionTest {
    public static void main(String[] args) throws SQLException {
        final Driver driver = new Driver();
        final Properties props = new Properties();
        if (args.length>1) props.put("user",args[1]);
        if (args.length>2) props.put("password",args[2]);
        final String hostPort = args[0];
        Neo4jConnection conn = driver.connect("jdbc:neo4j://" + hostPort, props);
        final long id = 0L;
        final ResultSet rs = conn.executeQuery("start n=node({id}) return id(n) as id", map("id", id));
        while (rs.next()) {
            assertEquals(id,rs.getLong("id"));
        }
    }
}
