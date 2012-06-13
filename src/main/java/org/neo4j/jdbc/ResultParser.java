package org.neo4j.jdbc;

import org.codehaus.jackson.JsonNode;

import java.util.*;

/**
 * @author mh
 * @since 12.06.12
 */
// TODO Implement support for streaming Cypher here implements Iterator<Map<String,Object>>
public class ResultParser {

    private final JsonNode node;
    private List<String> columns;

    public ResultParser(JsonNode node) {
        this.node = node;
        this.columns = parseColumns();
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<String> parseColumns() {
        List<String> columns = new ArrayList<String>();
        for (JsonNode column : node.get("columns")) {
            String textValue = column.getTextValue();
            columns.add(textValue);
        }
        return columns;
    }

    List<Map<String, Object>> parseData() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (JsonNode row : node.get("data")) {
            Map<String, Object> rowData = parseRow(row);
            data.add(rowData);
        }
        return data;
    }

    private Map<String, Object> parseRow(JsonNode row) {
        int idx = 0;
        Map<String, Object> rowData = new LinkedHashMap<String, Object>();
        for (JsonNode cell : row) {
            rowData.put(columns.get(idx++), toObject(cell));
        }
        return rowData;
    }

    private Object toObject(JsonNode cell) {
        if (cell.isObject()) {
            if (cell.has("length") && cell.has("nodes") && cell.has("relationships")) {
                return toPath(cell);
            }
            Map<String, Object> result = toPropertyContainer(cell);
            return addRelationshipInfo(cell, result);
        }
        if (cell.isArray()) {
            ArrayList<Object> result = new ArrayList<Object>(cell.size());
            for (JsonNode node : cell) {
                result.add(toObject(node));
            }
            return result;
        }
        if (cell.isTextual()) return cell.asText();
        if (cell.isBoolean()) return cell.asBoolean();
        if (cell.isNumber()) return cell.getNumberValue();
        return cell.asText();
    }

    private Object addRelationshipInfo(JsonNode cell, Map<String, Object> result) {
        if (cell.has("start")) result.put("_start", idOf(cell.get("start")));
        if (cell.has("end")) result.put("_end", idOf(cell.get("end")));
        if (cell.has("type")) result.put("_type", cell.get("type").asText());
        return result;
    }

    private Map<String, Object> toPropertyContainer(JsonNode cell) {
        Map<String, Object> result = new TreeMap<String, Object>();
        String idField = cell.has("type") ? "_rel_id" : "_node_id";
        result.put(idField, idOf(cell.get("self")));
        JsonNode data = cell.get("data");
        if (data != null && data.isObject() && data.size() > 0) {
            Iterator<String> fieldNames = data.getFieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                result.put(fieldName, toObject(data.get(fieldName)));
            }
        }
        return result;
    }

    private ArrayList<Object> toPath(JsonNode cell) {
        ArrayList<Object> path = new ArrayList<Object>(cell.get("length").asInt() + 1);
        Iterator<JsonNode> nodes = cell.get("nodes").iterator();
        Iterator<JsonNode> relationships = cell.get("relationships").iterator();
        while (nodes.hasNext()) {
            path.add(map("_node_id", idOf(nodes.next())));
            if (relationships.hasNext()) {
                path.add(map("_rel_id", idOf(relationships.next())));
            }
        }
        return path;
    }

    private Map<String, Object> map(String key, Object value) {
        TreeMap<String, Object> result = new TreeMap<String, Object>();
        result.put(key, value);
        return result;
    }

    private Long idOf(JsonNode node) {
        if (node == null) {
            return null;
        }
        String uri = node.asText();
        if (uri == null) return null;
        int idx = uri.lastIndexOf("/");
        return idx == -1 ? null : Long.valueOf(uri.substring(idx + 1));
    }
}
