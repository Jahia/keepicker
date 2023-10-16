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
    private static final String PREFIX = "kpk:";

    private static final String FORM_TYPE_IMAGE = "PICTURE";
    private static final String FORM_TYPE_VIDEO = "VIDEO";
//    private static final String FORM_TYPE_SOUND = "SOUND";
//    private static final String FORMAT_DOCUMENT = "DOCUMENT";

    private static final String CONTENT_TYPE_IMAGE = "kpknt:image";
    private static final String CONTENT_TYPE_VIDEO = "kpknt:video";
//    private static final String CONTENT_TYPE_PDF = "kpknt:pdf";
//    private static final String CONTENT_TYPE_DOC = "kpknt:document";
    private static final String CONTENT_TYPE_OTHER = "kpknt:other";
//    private class Urls {
//        private String baseUrl;
//        private String endUrl;
//
//        public Urls(String url){
//            String regex = "(?<baseUrl>.*upload)/(?<endUrl>.*)";
//            Pattern urlPattern = Pattern.compile(regex);
//            Matcher matcher = urlPattern.matcher(url);
//
//            if(matcher.find()){
//                baseUrl=matcher.group("baseUrl");
//                endUrl=matcher.group("endUrl");
//            }
//        }
//        public String getBaseUrl(){return baseUrl;}
//        public String getEndUrl(){return endUrl;}
//    }


    public KeepeekAssetDeserializer() {
        this(null);
    }

    public KeepeekAssetDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public KeepeekAsset deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException, JsonProcessingException {
//        JsonNode response = jsonParser.getCodec().readTree(jsonParser);
//        JsonNode keepeekNode = response.get("resources").get(0);
        JsonNode keepeekNode = jsonParser.getCodec().readTree(jsonParser);
        KeepeekAsset keepeekAsset = new KeepeekAsset();

//        List<String> mixinTypes = new ArrayList<String>();
//        "cloudymix:cloudyAsset"
//        keepeekAsset.addProperty("jcr:mixinTypes","cloudymix:cloudyAsset");

        String formType = keepeekNode.get("formType").textValue();
        String id = Integer.toString(keepeekNode.get("id").intValue());

        keepeekAsset.setId(id);
        keepeekAsset.addProperty(PREFIX+"assetId",id);
        keepeekAsset.addProperty(PREFIX+"originalFileName",keepeekNode.get("originalFileName").textValue());
        keepeekAsset.addProperty(PREFIX+"status",keepeekNode.get("status").textValue());
        keepeekAsset.addProperty("jcr:title",keepeekNode.get("title").textValue());
        keepeekAsset.addProperty(PREFIX+"statusUpdateDate",keepeekNode.get("statusUpdateDate").textValue());
        keepeekAsset.addProperty(PREFIX+"creationDate",keepeekNode.get("creationDate").textValue());
        keepeekAsset.addProperty(PREFIX+"updateDate",keepeekNode.get("updateDate").textValue());
        keepeekAsset.addProperty(PREFIX+"importDate",keepeekNode.get("importDate").textValue());
        keepeekAsset.addProperty(PREFIX+"fileSize",keepeekNode.get("fileSize").longValue());
        keepeekAsset.addProperty(PREFIX+"fileSizeString",keepeekNode.get("fileSizeString").textValue());
        keepeekAsset.addProperty(PREFIX+"width",keepeekNode.get("width").longValue());
        keepeekAsset.addProperty(PREFIX+"height",keepeekNode.get("height").longValue());
        keepeekAsset.addProperty(PREFIX+"resolution",keepeekNode.get("resolution").longValue());
        keepeekAsset.addProperty(PREFIX+"mediaType",keepeekNode.get("mediaType").textValue());
        keepeekAsset.addProperty(PREFIX+"formType",formType);
        keepeekAsset.addProperty(PREFIX+"thumbnailGenerationStatus",keepeekNode.get("thumbnailGenerationStatus").textValue());
//        keepeekAsset.addProperty(PREFIX+"cover",keepeekNode.at("/_links/kpk:cover/href").textValue());

        switch (formType){
            case FORM_TYPE_IMAGE :
                keepeekAsset.addProperty(PREFIX+"xlarge",keepeekNode.at("/_links/kpk:xlarge/href").textValue());
                keepeekAsset.addProperty(PREFIX+"large",keepeekNode.at("/_links/kpk:large/href").textValue());
                keepeekAsset.addProperty(PREFIX+"medium",keepeekNode.at("/_links/kpk:medium/href").textValue());
                keepeekAsset.addProperty(PREFIX+"small",keepeekNode.at("/_links/kpk:small/href").textValue());
                keepeekAsset.addProperty(PREFIX+"poster",keepeekNode.at("/_links/kpk:small/href").textValue());
                keepeekAsset.setJahiaNodeType(CONTENT_TYPE_IMAGE);
                break;

            case FORM_TYPE_VIDEO:
                keepeekAsset.setJahiaNodeType(CONTENT_TYPE_VIDEO);
                keepeekAsset.addProperty(PREFIX+"xlarge",keepeekNode.at("/_links/kpk:xlarge/href").textValue());
                keepeekAsset.addProperty(PREFIX+"large",keepeekNode.at("/_links/kpk:large/href").textValue());
                keepeekAsset.addProperty(PREFIX+"medium",keepeekNode.at("/_links/kpk:medium/href").textValue());
                keepeekAsset.addProperty(PREFIX+"small",keepeekNode.at("/_links/kpk:small/href").textValue());
                keepeekAsset.addProperty(PREFIX+"poster",keepeekNode.at("/_links/kpk:small/href").textValue());
                keepeekAsset.addProperty(PREFIX+"duration",keepeekNode.get("duration").textValue());
                keepeekAsset.addProperty(PREFIX+"durationInSeconds",keepeekNode.get("durationInSeconds").longValue());
                break;

            default:
                keepeekAsset.setJahiaNodeType(CONTENT_TYPE_OTHER);
                break;
        }
        return keepeekAsset;
    }

//    private void addPoster(String url, KeepeekAsset keepeekAsset){
//        url = url.substring(0, url.lastIndexOf('.')).concat(".jpg") ;
//        keepeekAsset.addProperty(PREFIX+"poster",url);
//    }

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
