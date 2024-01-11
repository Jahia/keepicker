package org.jahia.se.modules.dam.keepeek.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.jcr.Binary;
import java.util.HashMap;
import java.util.Map;

@JsonDeserialize(using = KeepeekAssetDeserializer.class)
public class KeepeekAsset {

    private String jahiaNodeType;
    private String id;
    private final Map<String, String[]> properties = new HashMap<>();
    private final Map<String, Binary[]> binaryProperties = new HashMap<>();

    public String getId() {
        return id;
    }

    public String getJahiaNodeType() {
        return jahiaNodeType;
    }

    public Map<String, String[]> getProperties() {
        return properties;
    }

    public Map<String, Binary[]> getBinaryProperties() {
        return binaryProperties;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setJahiaNodeType(String jahiaNodeType) {
        this.jahiaNodeType = jahiaNodeType;
    }

    public void addProperty(String name,Object value){
        if(value == null) return;
        properties.put(name, new String[]{value.toString()});
    }

    public String[] getProperty(String name){
        if(name == null) return null;
        return properties.get(name);
    }

    public void addBinaryProperty(String name, Binary value) {
        if (value == null) return;
        binaryProperties.put(name, new Binary[]{value});
    }
}
