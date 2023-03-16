package org.spt.compiler;

public enum JavaVersion {
    JAVA_8("1.8"),
    JAVA_7("1.7"),
    JAVA_11("11");

    private final String version;

    JavaVersion(String version){
        this.version = version;
    }

    public String getVersion(){
        return version;
    }
}
