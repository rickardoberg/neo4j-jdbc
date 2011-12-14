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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.neo4j.cypherdsl.Execute;
import org.neo4j.cypherdsl.ExecuteWithParameters;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.neo4j.jdbc.DriverQueries.QUERIES;

/**
 * TODO
 */
public class Neo4jConnection
    implements Connection
{
    private boolean closed = false;
    private String url;
    private Properties properties;
    private ClientResource cypherResource;
    private ObjectMapper mapper = new ObjectMapper();
    private boolean debug;

    public Neo4jConnection(String url, Client client, Properties properties) throws SQLException
    {
        this.url = url;
        this.properties = properties;
        this.debug = "true".equalsIgnoreCase(properties.getProperty("debug"));

        try
        {
            url  = "http"+url.substring("jdbc:neo4j".length());
            Reference ref = new Reference(new Reference(url), "/");
            Context context = new Context();
            context.setClientDispatcher(client);
            ClientResource resource = new ClientResource(context, ref);
            resource.getClientInfo().setAcceptedMediaTypes(Collections.singletonList(new Preference<MediaType>(MediaType.APPLICATION_JSON)));

            // Get service root
            JsonNode node = mapper.readTree(resource.get().getReader());
            ClientResource dataResource = new ClientResource(resource.getContext(),node.get("data").getTextValue());
            dataResource.getClientInfo().setAcceptedMediaTypes(Collections.singletonList(new Preference<MediaType>(MediaType.APPLICATION_JSON)));

            // Get Cypher extension
            node = mapper.readTree(dataResource.get().getReader());
            cypherResource = new ClientResource(dataResource.getContext(),node.get("extensions").get("CypherPlugin").get("execute_query").getTextValue());
            cypherResource.getClientInfo().setAcceptedMediaTypes(Collections.singletonList(new Preference<MediaType>(MediaType.APPLICATION_JSON)));
        } catch (IOException e)
        {
            throw new SQLNonTransientConnectionException(e);
        }
    }

    public Statement createStatement() throws SQLException
    {
        return debug(new Neo4jStatement(this));
    }

    public PreparedStatement prepareStatement(String s) throws SQLException
    {
        return debug(new Neo4jPreparedStatement(this, s));
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException
    {
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException
    {
        return null;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException
    {
    }

    @Override
    public boolean getAutoCommit() throws SQLException
    {
        return false;
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
            ((Client)cypherResource.getNext()).stop();
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
    }

    @Override
    public boolean isReadOnly() throws SQLException
    {
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException
    {
    }

    @Override
    public String getCatalog() throws SQLException
    {
        return "Default";
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException
    {
    }

    @Override
    public int getTransactionIsolation() throws SQLException
    {
        return 0;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException
    {
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
    {
        return debug(new Neo4jStatement(this));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        return debug(new Neo4jPreparedStatement(this, sql));
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException
    {
        return new HashMap<String, Class<?>>();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException
    {
    }

    @Override
    public void setHoldability(int holdability) throws SQLException
    {
    }

    @Override
    public int getHoldability() throws SQLException
    {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException
    {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException
    {
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException
    {
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException
    {
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        return debug(new Neo4jStatement(this));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        return debug(new Neo4jPreparedStatement(this, sql));
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
    {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
    {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
    {
        return null;
    }

    @Override
    public Clob createClob() throws SQLException
    {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException
    {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException
    {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException
    {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException
    {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException
    {
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException
    {
    }

    @Override
    public String getClientInfo(String name) throws SQLException
    {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException
    {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException
    {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException
    {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        return (T) this;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return false;
    }

    // Connection helpers
    public ResultSet executeQuery(Execute execute) throws SQLException
    {
        if (execute instanceof ExecuteWithParameters)
            return executeQuery(execute.toString(), ((ExecuteWithParameters)execute).getParameters());
        else
            return executeQuery(execute.toString(), Collections.<String, Object>emptyMap());
    }

    public ResultSet executeQuery(String query, Map<String, Object> parameters) throws SQLException
    {
        query = query.replace('\"', '\'');
        query = query.replace('\n',' ');

        ObjectNode queryNode = mapper.createObjectNode();
        queryNode.put("query", query);
        ObjectNode params = mapper.createObjectNode();
        for (Map.Entry<String, Object> stringObjectEntry : parameters.entrySet())
        {
            Object value = stringObjectEntry.getValue();
            if (value instanceof String)
                params.put(stringObjectEntry.getKey(), value.toString());
            else if (value instanceof Integer)
                params.put(stringObjectEntry.getKey(), (Integer)value);
            else if (value instanceof Long)
                params.put(stringObjectEntry.getKey(), (Long)value);
            else if (value instanceof Boolean)
                params.put(stringObjectEntry.getKey(), (Boolean)value);
        }
        queryNode.put("params", params);

        Representation req = new StringRepresentation(queryNode.toString(), MediaType.APPLICATION_JSON);
        try
        {
            Representation rep = cypherResource.post(req);
            JsonNode node = mapper.readTree(rep.getReader());

            List<String> columns = new ArrayList<String>();
            for (JsonNode column : node.get("columns"))
            {
                columns.add(column.getTextValue());
            }

            List<Map<String,Object>> data = new ArrayList<Map<String, Object>>();
            for (JsonNode row : node.get("data"))
            {
                int idx = 0;
                Map<String, Object> rowData = new LinkedHashMap<String, Object>();
                for (JsonNode cell : row)
                {
                    rowData.put(columns.get(idx++), cell.asText());
                }
                data.add(rowData);
            }
            return toResultSet(new ExecutionResult(columns, data));
        } catch (ResourceException e)
        {
            throw new SQLException(e.getStatus().getReasonPhrase());
        } catch (Throwable e)
        {
            throw new SQLException(e);
        }
    }

    public String tableColumns(String tableName, String columnPrefix) throws SQLException
    {
        ResultSet columns = executeQuery(QUERIES.getColumns(tableName));
        StringBuilder columnsBuilder = new StringBuilder();
        while (columns.next())
        {
            if (columnsBuilder.length() > 0)
                columnsBuilder.append(',');
            columnsBuilder.append(columnPrefix).append(columns.getString("property.name"));
        }
        return columnsBuilder.toString();
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

        for (Map<String,Object> row: result)
        {
            rs.rowData(row.values());
        }

        return rs.newResultSet(this);
    }

    public <T> T debug(T obj)
    {
        if (debug)
        {
            Class intf = obj.getClass().getInterfaces()[0];
            return (T) CallProxy.proxy(intf, obj);
        } else
            return obj;
    }

    public Properties getProperties()
    {
        return properties;
    }
}
