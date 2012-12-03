package org.neo4j.jdbc.rest;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.*;
import org.restlet.resource.ClientResource;
import org.restlet.util.Series;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author mh
 * @since 12.06.12
 */
public class Resources {
    private ObjectMapper mapper = new ObjectMapper();
    private final Reference ref;
    private String user;
    private String password;
    private final Client client;

    Resources(String url, Client client) {
        this.client = client;
        ref = new Reference(new Reference(url), "/");
    }

    private Context createContext() {
        Context context = new Context();
        context.setClientDispatcher(client);
        return context;
    }


    public void setAuth(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public DiscoveryClientResource getDiscoveryResource() throws IOException {
        DiscoveryClientResource discovery = withAuth(new DiscoveryClientResource(createContext(), ref, mapper));
        discovery.readInformation();
        return discovery;

    }

    <T extends ClientResource> T withAuth(T resource) {
        if (hasAuth()) {
            resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, user, password);
        }
        return resource;
    }

    private boolean hasAuth() {
        return user != null && password != null;
    }

    public ClientResource getCypherResource(String cypherPath) {
        return withAuth(new CypherClientResource(new Context(), cypherPath, mapper));
    }

    public JsonNode readJsonFrom(String uri) throws IOException {
        ClientResource resource = withAuth(new ClientResource(createContext(), uri));
        resource.getClientInfo().setAcceptedMediaTypes(streamingJson());
        return mapper.readTree(resource.get().getReader());
    }

    private String textField(JsonNode node, String field) {
        final JsonNode fieldNode = node.get(field);
        if (fieldNode == null) return null;
        return fieldNode.getTextValue();
    }

    public class DiscoveryClientResource extends ClientResource {
        private String version;
        private final ObjectMapper mapper;
        private String cypherPath;

        public DiscoveryClientResource(Context context, Reference ref, ObjectMapper mapper) {
            super(context, ref);
            this.mapper = mapper;
            getClientInfo().setAcceptedMediaTypes(streamingJson());
        }

        public String getVersion() {
            return version;
        }

        public void readInformation() throws IOException {
            // Get service root
            JsonNode discoveryInfo = mapper.readTree(get().getReader());

            final String dataUri = textField(discoveryInfo, "data");

            JsonNode serverData = readJsonFrom(dataUri);

            version = textField(serverData, "neo4j_version");

            cypherPath = obtainCypherPath(serverData);
        }

        private String obtainCypherPath(JsonNode serverData) {
            String cypherPath = textField(serverData, "cypher");
            if (cypherPath == null) {
                final JsonNode extensions = serverData.get("extensions");
                if (extensions != null) {
                    final JsonNode plugin = extensions.get("CypherPlugin");
                    if (plugin != null) cypherPath = textField(plugin, "execute_query");
                }
            }
            return cypherPath;
        }

        public String getCypherPath() {
            return cypherPath;
        }
    }


    private static class CypherClientResource extends ClientResource {
        private final ObjectMapper mapper;

        public CypherClientResource(final Context context, String cypherPath, ObjectMapper mapper) {
            super(context, cypherPath);
            this.mapper = mapper;
            getClientInfo().setAcceptedMediaTypes(streamingJson());
        }

        @Override
        public void doError(Status errorStatus) {
            try {
                JsonNode node = mapper.readTree(getResponse().getEntity().getReader());
                JsonNode message = node.get("message");
                if (message != null)
                    super.doError(new Status(errorStatus.getCode(), message.toString(), message.toString(), errorStatus.getUri()));
            } catch (IOException e) {
                // Ignore
            }

            super.doError(errorStatus);
        }
    }

    private static List<Preference<MediaType>> streamingJson() {
        final MediaType mediaType = streamingJsonType();
        return Collections.singletonList(new Preference<MediaType>(mediaType));
    }

    private static MediaType streamingJsonType() {
        final Series<Parameter> parameters = new Series<Parameter>(Parameter.class);
        parameters.add("stream", "true");
        return new MediaType(MediaType.APPLICATION_JSON.getName(), parameters);
    }
}
