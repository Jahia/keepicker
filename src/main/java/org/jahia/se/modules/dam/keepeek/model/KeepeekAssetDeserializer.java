package org.jahia.se.modules.dam.keepeek.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeepeekAssetDeserializer extends StdDeserializer<KeepeekAsset> {
    private static final String PREFIX = "cloudy:";

    private static final String RESOURCE_TYPE_IMAGE = "image";
    private static final String RESOURCE_TYPE_VIDEO = "video";

    private static final String FORMAT_PDF = "pdf";

    private static final String CONTENT_TYPE_IMAGE = "cloudynt:image";
    private static final String CONTENT_TYPE_VIDEO = "cloudynt:video";
    private static final String CONTENT_TYPE_PDF = "cloudynt:pdf";
    private static final String CONTENT_TYPE_DOC = "cloudynt:document";

    private class Urls {
        private String baseUrl;
        private String endUrl;

        public Urls(String url){
            String regex = "(?<baseUrl>.*upload)/(?<endUrl>.*)";
            Pattern urlPattern = Pattern.compile(regex);
            Matcher matcher = urlPattern.matcher(url);

            if(matcher.find()){
                baseUrl=matcher.group("baseUrl");
                endUrl=matcher.group("endUrl");
            }
        }
        public String getBaseUrl(){return baseUrl;}
        public String getEndUrl(){return endUrl;}
    }


    public KeepeekAssetDeserializer() {
        this(null);
    }

    public KeepeekAssetDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public KeepeekAsset deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException, JsonProcessingException {
        JsonNode response = jsonParser.getCodec().readTree(jsonParser);
        JsonNode keepeekNode = response.get("resources").get(0);
        KeepeekAsset keepeekAsset = new KeepeekAsset();

//        List<String> mixinTypes = new ArrayList<String>();
//        "cloudymix:cloudyAsset"
//        keepeekAsset.addProperty("jcr:mixinTypes","cloudymix:cloudyAsset");

        String resourceType = keepeekNode.get("resource_type").textValue();
        String format = keepeekNode.get("format").textValue();
        String url = keepeekNode.get("secure_url").textValue();
        Urls urls = new Urls(url);

        keepeekAsset.setId(keepeekNode.get("asset_id").textValue());
        keepeekAsset.addProperty(PREFIX+"assetId",keepeekNode.get("asset_id").textValue());
        keepeekAsset.addProperty(PREFIX+"publicId",keepeekNode.get("public_id").textValue());
        keepeekAsset.addProperty(PREFIX+"folder",keepeekNode.get("folder").textValue());
        keepeekAsset.addProperty("jcr:title",keepeekNode.get("filename").textValue());
        keepeekAsset.addProperty(PREFIX+"format",format);
        keepeekAsset.addProperty(PREFIX+"version",keepeekNode.get("version").longValue());
        keepeekAsset.addProperty(PREFIX+"resourceType",resourceType);
        keepeekAsset.addProperty(PREFIX+"type",keepeekNode.get("type").textValue());
        keepeekAsset.addProperty(PREFIX+"createdAt",keepeekNode.get("created_at").textValue());
        keepeekAsset.addProperty(PREFIX+"uploadedAt",keepeekNode.get("uploaded_at").textValue());
        keepeekAsset.addProperty(PREFIX+"bytes",keepeekNode.get("bytes").longValue());
        keepeekAsset.addProperty(PREFIX+"width",keepeekNode.get("width").longValue());
        keepeekAsset.addProperty(PREFIX+"height",keepeekNode.get("height").longValue());
        keepeekAsset.addProperty(PREFIX+"aspectRatio",keepeekNode.get("aspect_ratio").doubleValue());
        keepeekAsset.addProperty(PREFIX+"url",url);
        keepeekAsset.addProperty(PREFIX+"status",keepeekNode.get("status").textValue());
        keepeekAsset.addProperty(PREFIX+"accessMode",keepeekNode.get("access_mode").textValue());
        keepeekAsset.addProperty(PREFIX+"accessControl",keepeekNode.get("access_control").textValue());

        keepeekAsset.addProperty(PREFIX+"baseUrl",urls.getBaseUrl());
        keepeekAsset.addProperty(PREFIX+"endUrl",urls.getEndUrl());
//        splitURL(keepeekNode.get("secure_url").textValue(),keepeekAsset);

        switch (resourceType){
            case RESOURCE_TYPE_IMAGE :
                if( FORMAT_PDF.equals(format) ){
                    keepeekAsset.setJahiaNodeType(CONTENT_TYPE_PDF);
                    addPoster(urls.getEndUrl(),keepeekAsset);
                }else{
                    keepeekAsset.setJahiaNodeType(CONTENT_TYPE_IMAGE);
                }
                break;

            case RESOURCE_TYPE_VIDEO:
                keepeekAsset.setJahiaNodeType(CONTENT_TYPE_VIDEO);
                keepeekAsset.addProperty(PREFIX+"duration",keepeekNode.get("duration").doubleValue());
                addPoster(urls.getEndUrl(),keepeekAsset);
                break;

            default:
                keepeekAsset.setJahiaNodeType(CONTENT_TYPE_DOC);
                break;
        }
        return keepeekAsset;
    }

    private void addPoster(String url, KeepeekAsset keepeekAsset){
        url = url.substring(0, url.lastIndexOf('.')).concat(".jpg") ;
        keepeekAsset.addProperty(PREFIX+"poster",url);
    }

//    private void splitURL(String url, KeepeekAsset keepeekAsset){
//        String regex = "(?<baseUrl>.*upload)/(?<endUrl>.*)";
//        Pattern urlPattern = Pattern.compile(regex);
//        Matcher matcher = urlPattern.matcher(url);
//
//        if(matcher.find()){
//            keepeekAsset.addProperty(PREFIX+"baseUrl",matcher.group("baseUrl"));
//            keepeekAsset.addProperty(PREFIX+"endUrl",matcher.group("endUrl"));
//        }
//    }
}
