package org.neo4j.jdbc;

/**
 * @author mh
 * @since 15.06.12
 */
public class Version {
    private final String version;

    public Version(String version) {
        this.version = version;
    }

    public int getMajorVersion() {
        String[] versionParts = version.split("\\.");
        return Integer.parseInt(versionParts[0]);
    }

    public int getMinorVersion() {
        String[] versionParts = version.split("\\.");
        return Integer.parseInt(versionParts[1]);
    }

    public String getVersion() {
        return version;
    }
}
