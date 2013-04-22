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
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.expression.Expression;
import org.neo4j.cypherdsl.grammar.Execute;
import org.neo4j.cypherdsl.grammar.ExecuteWithParameters;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.jdbc.embedded.EmbeddedQueryExecutor;
import org.neo4j.jdbc.rest.RestQueryExecutor;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * Implementation of Connection that delegates to the Neo4j REST API and sends queries as Cypher requests
 */
public class Neo4jConnection extends AbstractConnection {
    protected final static Log log = LogFactory.getLog(Neo4jConnection.class);
    private String url;

    private QueryExecutor queryExecutor;
    private boolean closed = false;
    private final Properties properties = new Properties();
    private boolean debug;
    private Driver driver;
    private Version version;
    
    private SQLWarning sqlWarnings;
    private boolean readonly = false;

    public Neo4jConnection(Driver driver, String jdbcUrl, Properties properties) throws SQLException
    {
        this.driver = driver;
        this.url = jdbcUrl;
        this.properties.putAll(properties);
        this.debug = hasDebug();
        final String connectionUrl = jdbcUrl.substring("jdbc:neo4j".length());
        this.queryExecutor = createExecutor(connectionUrl,getUser(),getPassword());
        this.version = this.queryExecutor.getVersion();
        validateVersion();
    }

    private QueryExecutor createExecutor(String connectionUrl, String user, String password) throws SQLException {
        if (connectionUrl.contains("://"))
            return new RestQueryExecutor(connectionUrl,user,password);

        return getDriver().createExecutor(connectionUrl,properties);
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
        if (version.getMajorVersion() != 1 || version.getMinorVersion() < 5)
            throw new SQLException("Unsupported Neo4j version:"+ version);
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

    public void close() throws SQLException {
        try {
            queryExecutor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closed = true;

        }

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
            final ExecutionResult result = queryExecutor.executeQuery(query, parameters);
            return debug(toResultSet(result));
        }
        catch (SQLException e)
        {
        	throw e;
        }
        catch (Exception e)
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
        return new IteratorResultSet(this,result.columns(), result.getResult());
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

    public Version getVersion()
    {
        return version;
    }
}
