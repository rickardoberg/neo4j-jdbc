/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.Execute;
import org.neo4j.cypherdsl.ExecuteWithParameters;
import org.neo4j.cypherdsl.query.Expression;
import org.restlet.Client;
import org.restlet.data.*;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * Implementation of Connection that delegates to the Neo4j REST API and sends queries as Cypher requests
 */
public class Neo4jConnection extends AbstractConnection {
    protected final static Log log = LogFactory.getLog(Neo4jConnection.class);

    private boolean closed = false;
    private String url;
    private Properties properties;
    private ClientResource cypherResource;
    private ObjectMapper mapper = new ObjectMapper();
    private boolean debug;
    private Driver driver;
    private String databaseProductVersion;
    
    private SQLWarning sqlWarnings;
    private boolean readonly = false;
    private final Resources resources;

    public Neo4jConnection(Driver driver, String jdbcUrl, Client client, Properties properties) throws SQLException
    {
        this.driver = driver;
        this.url = jdbcUrl;
        this.properties = properties;
        this.debug = hasDebug();

        try
        {
            String url = "http" + jdbcUrl.substring("jdbc:neo4j".length());
            if (log.isInfoEnabled()) log.info("Connecting to URL "+url);
            resources = new Resources(url,client);

            if (hasAuth()) {
                resources.setAuth(getUser(), getPassword());
            }

            Resources.DiscoveryClientResource discovery = resources.getDiscoveryResource();

            databaseProductVersion = discovery.getVersion();

            validateVersion();

            String cypherPath = discovery.getCypherPath();

            cypherResource = resources.getCypherResource(cypherPath);
        } catch (IOException e)
        {
            throw new SQLNonTransientConnectionException(e);
        }
    }

    private String getPassword() {
        return properties.getProperty("password");
    }

    private String getUser() {
        return properties.getProperty("user");
    }

    private boolean hasAuth() {
        return properties.contains("user") && properties.contains("password");
    }

    private boolean hasDebug() {
        return Connections.hasDebug(properties);
    }

    private void validateVersion() throws SQLException {
        if (getMetaData().getDatabaseMajorVersion() != 1 || getMetaData().getDatabaseMinorVersion() < 5)
            throw new SQLException("Unsupported Neo4j version:"+databaseProductVersion);
    }

    public Statement createStatement() throws SQLException
    {
        return debug(new Neo4jStatement(this));
    }

    public PreparedStatement prepareStatement(String statement) throws SQLException
    {
        return debug(new Neo4jPreparedStatement(this, statement));
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException
    {
    }

    @Override
    public boolean getAutoCommit() throws SQLException
    {
        return true;
    }

    @Override
    public void commit() throws SQLException
    {
    }

    @Override
    public void rollback() throws SQLException
    {
    }

    public void close() throws SQLException
    {
        try
        {
            ((Client) cypherResource.getNext()).stop();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        closed = true;
    }

    public boolean isClosed() throws SQLException
    {
        return closed;
    }

    public DatabaseMetaData getMetaData() throws SQLException
    {
        return debug(new Neo4jDatabaseMetaData(this));
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException
    {
        this.readonly=readOnly;
    }

    @Override
    public boolean isReadOnly() throws SQLException
    {
        return readonly;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        return sqlWarnings;
    }

    @Override
    public void clearWarnings() throws SQLException
    {
        sqlWarnings = null;
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
    {
        return debug(new Neo4jStatement(this));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        return debug(new Neo4jPreparedStatement(this, nativeSQL(sql)));
    }


    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        return debug(new Neo4jStatement(this));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        return debug(new Neo4jPreparedStatement(this, nativeSQL(sql)));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
    {
        return debug(new Neo4jPreparedStatement(this, nativeSQL(sql)));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
    {
        return debug(new Neo4jPreparedStatement(this, nativeSQL(sql)));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
    {
        return debug(new Neo4jPreparedStatement(this, nativeSQL(sql)));
    }

    @Override
    public boolean isValid(int timeout) throws SQLException
    {
        return true;
    }

    // Connection helpers
    public ResultSet executeQuery(Execute execute) throws SQLException
    {
        if (execute instanceof ExecuteWithParameters)
            return executeQuery(execute.toString(), ((ExecuteWithParameters) execute).getParameters());
        else
            return executeQuery(execute.toString(), Collections.<String, Object>emptyMap());
    }

    public ResultSet executeQuery(final String query, Map<String, Object> parameters) throws SQLException
    {
        if (log.isInfoEnabled()) log.info("Executing query: "+query+"\n with params"+parameters);
        checkReadOnly(query);
        try
        {
            ObjectNode queryNode = queryParameter(query, parameters);

            final ClientResource resource = new ClientResource(cypherResource);
            Representation rep = resource.post(queryNode.toString(), MediaType.APPLICATION_JSON);

            JsonNode node = mapper.readTree(rep.getReader());
            final ResultParser parser = new ResultParser(node);
            return debug(toResultSet(new ExecutionResult(parser.getColumns(), parser.parseData())));

        } catch (ResourceException e)
        {
            throw new SQLException(e.getStatus().getReasonPhrase(),e);
        } catch (Throwable e)
        {
            throw new SQLException(e);
        }
    }

    private void checkReadOnly(String query) throws SQLException {
        if (readonly && isMutating(query)) throw new SQLException("Mutating Query in readonly mode: "+query);
    }

    private boolean isMutating(String query) {
        return query.matches("(?is).*\\b(create|relate|delete|set)\\b.*");
    }

    private ObjectNode queryParameter(String query, Map<String, Object> parameters) {
        ObjectNode queryNode = mapper.createObjectNode();
        queryNode.put("query", escapeQuery(query));
        queryNode.put("params", parametersNode(parameters));
        return queryNode;
    }

    private String escapeQuery(String query) {
        query = query.replace('\"', '\'');
        query = query.replace('\n', ' ');
        return query;
    }

    private ObjectNode parametersNode(Map<String, Object> parameters) {
        ObjectNode params = mapper.createObjectNode();
        for (Map.Entry<String, Object> entry : parameters.entrySet())
        {
            final String name = entry.getKey();
            final Object value = entry.getValue();
            if (value==null) {
                params.putNull(name);
            } else if (value instanceof String)
                params.put(name, value.toString());
            else if (value instanceof Integer)
                params.put(name, (Integer) value);
            else if (value instanceof Long)
                params.put(name, (Long) value);
            else if (value instanceof Boolean)
                params.put(name, (Boolean) value);
            else if (value instanceof BigDecimal)
                params.put(name, (BigDecimal) value);
            else if (value instanceof Double)
                params.put(name, (Double) value);
            else if (value instanceof byte[])
                params.put(name, (byte[]) value);
            else if (value instanceof Float)
                params.put(name, (Float) value);
            else if (value instanceof Number) {
                final Number number = (Number) value;
                if (number.longValue()==number.doubleValue()) {
                    params.put(name, number.longValue());
                } else {
                    params.put(name, number.doubleValue());
                }
            }
        }
        return params;
    }

    public String tableColumns(String tableName, String columnPrefix) throws SQLException
    {
        ResultSet columns = executeQuery(driver.getQueries().getColumns(tableName));
        StringBuilder columnsBuilder = new StringBuilder();
        while (columns.next())
        {
            if (columnsBuilder.length() > 0)
                columnsBuilder.append(',');
            columnsBuilder.append(columnPrefix).append(columns.getString("property.name"));
        }
        return columnsBuilder.toString();
    }

    public Iterable<Expression> returnProperties(String tableName, String columnPrefix) throws SQLException
    {
        ResultSet columns = executeQuery(driver.getQueries().getColumns(tableName));
        List<Expression> properties = new ArrayList<Expression>();
        while (columns.next())
        {
            properties.add(CypherQuery.identifier(columnPrefix).property(columns.getString("property.name")));
        }
        return properties;
    }

    String getURL()
    {
        return url;
    }

    protected ResultSet toResultSet(ExecutionResult result) throws SQLException
    {
        ResultSetBuilder rs = new ResultSetBuilder();
        for (String column : result.columns())
        {
            rs.column(column);
        }

        for (Map<String, Object> row : result)
        {
            rs.rowData(row.values());
        }

        return rs.newResultSet(this);
    }

    public <T> T debug(T obj)
    {
        return Connections.debug(obj,debug);
    }

    public Properties getProperties()
    {
        return properties;
    }

    public Driver getDriver()
    {
        return driver;
    }

    public String getDatabaseProductVersion()
    {
        return databaseProductVersion;
    }
}
