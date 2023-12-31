package org.jahia.se.modules.dam.keepeek.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashMap;
import java.util.Map;

@JsonDeserialize(using = KeepeekAssetDeserializer.class)
public class KeepeekAsset {

    private String jahiaNodeType;
    private String id;
    private final Map<String, String[]> properties;

    public KeepeekAsset(){
        properties=new HashMap<>();
    }

    public String getId() { return id; }
    public String getJahiaNodeType() {
        return jahiaNodeType;
    }
    public Map<String, String[]> getProperties() {
        return properties;
    }

    public void setId(String id){
        this.id = id;
    }
    public void setJahiaNodeType(String jahiaNodeType) {
        this.jahiaNodeType = jahiaNodeType;
    }

    public void addProperty(String name,Object value){
        if(value == null)
            return;
        properties.put(name, new String[]{value.toString()});
    }
}
