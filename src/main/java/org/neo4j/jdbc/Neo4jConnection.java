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
import org.neo4j.cypherdsl.Execute;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * TODO
 */
public class Neo4jConnection
    implements Connection
{
    private boolean closed = false;
    private ClientResource resource;
    private ObjectMapper mapper = new ObjectMapper();

    public Neo4jConnection(ClientResource resource) throws SQLException
    {
        try
        {
            // Get service root
            {
                JsonNode node = mapper.readTree(resource.get().getReader());
                resource = new ClientResource(resource.getContext(),node.get("data").getTextValue());
                resource.getClientInfo().setAcceptedMediaTypes(Collections.singletonList(new Preference<MediaType>(MediaType.APPLICATION_JSON)));
            }

            // Get Cypher extension
            {
                JsonNode node = mapper.readTree(resource.get().getReader());
                resource = new ClientResource(resource.getContext(),node.get("extensions").get("CypherPlugin").get("execute_query").getTextValue());
                resource.getClientInfo().setAcceptedMediaTypes(Collections.singletonList(new Preference<MediaType>(MediaType.APPLICATION_JSON)));
            }

            // Store this resource
            this.resource = resource;
        } catch (IOException e)
        {
            throw new SQLNonTransientConnectionException(e);
        }
    }

    public Statement createStatement() throws SQLException
    {
        return new Neo4jStatement(this);
    }

    public PreparedStatement prepareStatement(String s) throws SQLException
    {
        return null;
    }

    public CallableStatement prepareCall(String s) throws SQLException
    {
        return null;
    }

    public String nativeSQL(String s) throws SQLException
    {
        return null;
    }

    public void setAutoCommit(boolean b) throws SQLException
    {
    }

    public boolean getAutoCommit() throws SQLException
    {
        return false;
    }

    public void commit() throws SQLException
    {

    }

    public void rollback() throws SQLException
    {

    }

    public void close() throws SQLException
    {
        closed = true;
    }

    public boolean isClosed() throws SQLException
    {
        return closed;
    }

    public DatabaseMetaData getMetaData() throws SQLException
    {
        Neo4jDatabaseMetaData metaData = new Neo4jDatabaseMetaData(this);

        return metaData;
    }

    public void setReadOnly(boolean b) throws SQLException
    {
    }

    public boolean isReadOnly() throws SQLException
    {
        return false;
    }

    public void setCatalog(String s) throws SQLException
    {
    }

    public String getCatalog() throws SQLException
    {
        return null;
    }

    public void setTransactionIsolation(int i) throws SQLException
    {
    }

    public int getTransactionIsolation() throws SQLException
    {
        return 0;
    }

    public SQLWarning getWarnings() throws SQLException
    {
        return null;
    }

    public void clearWarnings() throws SQLException
    {
    }

    public Statement createStatement(int i, int i1) throws SQLException
    {
        return null;
    }

    public PreparedStatement prepareStatement(String s, int i, int i1) throws SQLException
    {
        return null;
    }

    public CallableStatement prepareCall(String s, int i, int i1) throws SQLException
    {
        return null;
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException
    {
        return null;
    }

    public void setTypeMap(Map<String, Class<?>> stringClassMap) throws SQLException
    {
    }

    public void setHoldability(int i) throws SQLException
    {
    }

    public int getHoldability() throws SQLException
    {
        return 0;
    }

    public Savepoint setSavepoint() throws SQLException
    {
        return null;
    }

    public Savepoint setSavepoint(String s) throws SQLException
    {
        return null;
    }

    public void rollback(Savepoint savepoint) throws SQLException
    {
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException
    {
    }

    public Statement createStatement(int i, int i1, int i2) throws SQLException
    {
        return null;
    }

    public PreparedStatement prepareStatement(String s, int i, int i1, int i2) throws SQLException
    {
        return null;
    }

    public CallableStatement prepareCall(String s, int i, int i1, int i2) throws SQLException
    {
        return null;
    }

    public PreparedStatement prepareStatement(String s, int i) throws SQLException
    {
        return null;
    }

    public PreparedStatement prepareStatement(String s, int[] ints) throws SQLException
    {
        return null;
    }

    public PreparedStatement prepareStatement(String s, String[] strings) throws SQLException
    {
        return null;
    }

    public Clob createClob() throws SQLException
    {
        return null;
    }

    public Blob createBlob() throws SQLException
    {
        return null;
    }

    public NClob createNClob() throws SQLException
    {
        return null;
    }

    public SQLXML createSQLXML() throws SQLException
    {
        return null;
    }

    public boolean isValid(int i) throws SQLException
    {
        return false;
    }

    public void setClientInfo(String s, String s1) throws SQLClientInfoException
    {
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException
    {
    }

    public String getClientInfo(String s) throws SQLException
    {
        return null;
    }

    public Properties getClientInfo() throws SQLException
    {
        return null;
    }

    public Array createArrayOf(String s, Object[] objects) throws SQLException
    {
        return null;
    }

    public Struct createStruct(String s, Object[] objects) throws SQLException
    {
        return null;
    }

    public <T> T unwrap(Class<T> tClass) throws SQLException
    {
        return null;
    }

    public boolean isWrapperFor(Class<?> aClass) throws SQLException
    {
        return false;
    }

    ExecutionResult executeQuery(Execute execute) throws SQLException
    {
        return executeQuery(execute.toString());
    }

    ExecutionResult executeQuery(String query) throws SQLException
    {
        query = query.replace('\"', '\'');
        query = query.replace('\n',' ');
        System.out.println("Execute query:"+query);
        Representation req = new StringRepresentation("{\"query\": \""+query+"\",\"params\": {}}", MediaType.APPLICATION_JSON);
        try
        {
            Representation rep = resource.post(req);
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
            return new ExecutionResult(columns, data);
        } catch (Throwable e)
        {
            throw new SQLException(e);
        }
    }
}
