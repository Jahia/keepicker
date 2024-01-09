package org.jahia.se.modules.dam.keepeek.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.jackrabbit.value.BinaryImpl;
import org.joda.time.format.ISODateTimeFormat;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jahia.se.modules.dam.keepeek.ContentTypesConstants.*;

public class KeepeekAssetDeserializer extends StdDeserializer<KeepeekAsset> {
    private static final String PREFIX = "kpk:";

    private static final String FORM_TYPE_IMAGE = "PICTURE";
    private static final String FORM_TYPE_VIDEO = "VIDEO";
//    private static final String FORM_TYPE_SOUND = "SOUND";
//    private static final String FORMAT_DOCUMENT = "DOCUMENT";

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

        String formType = keepeekNode.get("formType").textValue();
        String id = Integer.toString(keepeekNode.get("id").intValue());
        //TODO
        String derivedSrcService = getDerivedSrcService(keepeekAsset);

        keepeekAsset.setId(id);
        keepeekAsset.addProperty(PREFIX+"assetId",id);
        keepeekAsset.addProperty(PREFIX+"originalFileName",keepeekNode.get("originalFileName").textValue());
        keepeekAsset.addProperty(PREFIX+"status",keepeekNode.get("status").textValue());
        keepeekAsset.addProperty("jcr:title",keepeekNode.get("title").textValue());
        keepeekAsset.addProperty(PREFIX+"statusUpdateDate",keepeekNode.get("statusUpdateDate").textValue());
        keepeekAsset.addProperty(PREFIX+"creationDate",keepeekNode.get("creationDate").textValue());
        keepeekAsset.addProperty(PREFIX+"updateDate",keepeekNode.get("updateDate").textValue());
        keepeekAsset.addProperty("jcr:lastModified", ISODateTimeFormat.dateTime().parseDateTime(keepeekNode.get("updateDate").textValue()).toString());
        keepeekAsset.addProperty(PREFIX+"importDate",keepeekNode.get("importDate").textValue());
        keepeekAsset.addProperty(PREFIX+"fileSize",keepeekNode.get("fileSize").longValue());
        keepeekAsset.addProperty(PREFIX+"fileSizeString",keepeekNode.get("fileSizeString").textValue());
        keepeekAsset.addProperty("j:width",keepeekNode.get("width").longValue());
        keepeekAsset.addProperty("j:height",keepeekNode.get("height").longValue());
        if(keepeekNode.has("pointOfInterest")) {
            keepeekAsset.addProperty(PREFIX + "poiX", keepeekNode.at("/pointOfInterest/x").doubleValue());
            keepeekAsset.addProperty(PREFIX + "poiY", keepeekNode.at("/pointOfInterest/y").doubleValue());
        }
        keepeekAsset.addProperty(PREFIX+"resolution",keepeekNode.get("resolution").longValue());
        keepeekAsset.addProperty("jcr:mimeType",keepeekNode.get("mediaType").textValue());
        keepeekAsset.addProperty(PREFIX+"formType",formType);
        keepeekAsset.addProperty(PREFIX+"thumbnailGenerationStatus",keepeekNode.get("thumbnailGenerationStatus").textValue());
        keepeekAsset.addBinaryProperty("jcr:data", new BinaryImpl(new byte[0]) {
            @Override
            public long getSize() throws RepositoryException {
                return keepeekNode.get("fileSize").longValue();
            }
        });

        if(FORM_TYPE_IMAGE.equals(formType) || FORM_TYPE_VIDEO.equals(formType)){
//            keepeekAsset.addProperty(PREFIX+"xlarge",keepeekNode.at("/_links/kpk:xlarge/href").textValue());
//            keepeekAsset.addProperty(PREFIX+"large",keepeekNode.at("/_links/kpk:large/href").textValue());
//            keepeekAsset.addProperty(PREFIX+"medium",keepeekNode.at("/_links/kpk:medium/href").textValue());
//            keepeekAsset.addProperty(PREFIX+"small",keepeekNode.at("/_links/kpk:small/href").textValue());
        }
        switch (formType){
            case FORM_TYPE_IMAGE :
                keepeekAsset.setJahiaNodeType(CONTENT_TYPE_IMAGE);
                keepeekAsset.addProperty(PREFIX+"poster",keepeekNode.at("/_links/kpk:medium/href").textValue());
                keepeekAsset.addProperty(PREFIX+"url",keepeekNode.at("/_links/kpk:whr/href").textValue());
                keepeekAsset.addProperty(PREFIX+"derivedSrcService",derivedSrcService);
                break;

            case FORM_TYPE_VIDEO:
                keepeekAsset.setJahiaNodeType(CONTENT_TYPE_VIDEO);
                keepeekAsset.addProperty(PREFIX+"poster",keepeekNode.at("/_links/kpk:whr/href").textValue());
                keepeekAsset.addProperty(PREFIX+"url",keepeekNode.at("/_links/kpk:preview/href").textValue());
                keepeekAsset.addProperty(PREFIX+"preview",keepeekNode.at("/_links/kpk:preview/href").textValue());
                keepeekAsset.addProperty(PREFIX+"480p",keepeekNode.at("/_links/kpk:480p/href").textValue());
                keepeekAsset.addProperty(PREFIX+"1080p",keepeekNode.at("/_links/kpk:1080p/href").textValue());
                keepeekAsset.addProperty(PREFIX+"duration",keepeekNode.get("duration").textValue());
                keepeekAsset.addProperty(PREFIX+"durationInSeconds",keepeekNode.get("durationInSeconds").doubleValue());
                break;

            default:
                keepeekAsset.setJahiaNodeType(CONTENT_TYPE_OTHER);
                break;
        }
        return keepeekAsset;
    }

    private String getDerivedSrcService(JsonNode keepeekNode){
        String src = keepeekNode.at("/_links/kpk:whr/href").textValue();
        String regex = "pm_(?<domain>\\d+)_(?<media>\\d+)_(?<id>[\\w-]+)-[a-zA-Z]+(?<ext>\\.[a-zA-Z]+)";
        Pattern urlPattern = Pattern.compile(regex);
        Matcher matcher = urlPattern.matcher(src);

        if (matcher.find()) {
            baseUrl = matcher.group("baseUrl");
            endUrl = matcher.group("endUrl");
        }
    }
}
