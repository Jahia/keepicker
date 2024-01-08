package org.jahia.se.modules.dam.keepeek.edp;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.jahia.osgi.BundleUtils;
import org.jahia.se.modules.dam.keepeek.model.KeepeekAsset;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRNodeDecorator;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jahia.se.modules.dam.keepeek.ContentTypesConstants.*;

public class KeepeekDecorator extends JCRNodeDecorator {
    private static final Logger logger = LoggerFactory.getLogger(KeepeekDecorator.class);
    private final String VIDEO_TYPE_480 = "480p";
    private final String VIDEO_TYPE_1080 = "1080p";
    private final String VIDEO_TYPE_PREVIEW = "preview";

    public KeepeekDecorator(JCRNodeWrapper node) {
        super(node);
    }


    @Override
    public String getDisplayableName() {
        try {
            return node.getProperty("jcr:title").getString();
        } catch (RepositoryException e) {
            return super.getDisplayableName();
        }
    }

    @Override
    public String getUrl() {
        try {
            return node.getProperty("kpk:url").getString();
        } catch (RepositoryException e) {
            return super.getUrl();
        }
    }
    //TODO review this with the signature call -> cache ?
    public String getUrl(List<String> params) throws RepositoryException {
        KeepeekCacheManager keepeekCacheManager = BundleUtils.getOsgiService(KeepeekCacheManager.class, null);

        if(this.isNodeType(CONTENT_TYPE_IMAGE)){
            HashMap<String, String> keepeekProps = new HashMap<String, String>();
            for (String param : params) {
                if (param.startsWith("width:")) {
                    String width = StringUtils.substringAfter(param, "width:");
                    if(!width.trim().isEmpty()){
                        keepeekProps.put("w", width);
                    }
                }
                if (param.startsWith("height:")) {
                    String height = StringUtils.substringAfter(param, "height:");
                    if(!height.trim().isEmpty()){
                        keepeekProps.put("h", height);
                    }
                }
            }
            //build the key
            StringBuilder sb = new StringBuilder();
            for(String key : keepeekProps.keySet()){
                sb.append(key + keepeekProps.get(key));
            }

            String resizeKey = sb.toString();
            try{
                //get urls as json
                JSONObject urls =new JSONObject(this.getPropertyAsString("kpk:urls"));
                //check if key exist and get value
                String url = (String) urls.opt(resizeKey);
                //else build the url
                if(url == null || url.trim().isEmpty()){
                    url = getResizedUrl(keepeekProps);
                    urls.put(resizeKey,url);
                    //store the url into the content in cache
                    KeepeekAsset keepeekAsset = keepeekCacheManager.getKeepeekAsset(this.getPropertyAsString("kpk:assetId"));
                    keepeekAsset.addProperty("kpk:urls",urls.toString());
                }
                //return the signed url
                return url;
            } catch (JSONException e) {
                //else build the url
                return this.getUrl();
//                throw new RuntimeException(e);
            }
        }else if(this.isNodeType(CONTENT_TYPE_VIDEO)){
            String keepeekProps = VIDEO_TYPE_PREVIEW;
            for (String param : params) {
                if (param.startsWith("quality:")) {
                    String quality = StringUtils.substringAfter(param, "quality:");
                    if( VIDEO_TYPE_PREVIEW.equals(quality) ||
                            VIDEO_TYPE_480.equals(quality) ||
                            VIDEO_TYPE_1080.equals(quality)
                    ){
                        keepeekProps = quality;
                    }
                    return node.getProperty(keepeekProps).getString();
                }
            }
            return node.getProperty(keepeekProps).getString();
        }else{
            return this.getUrl();
        }
    }

    @Override
    public String getThumbnailUrl(String name) {
        try {
            return node.getProperty("kpk:poster").getString();
        } catch (RepositoryException e) {
            return super.getUrl();
        }
    }

    private String getResizedUrl(Map<String,String> params){
        String poiX = this.getPropertyAsString("kpk:poiX");
        String poiY = this.getPropertyAsString("kpk:poiY");
        String kpkp = this.getPropertyAsString("kpk:derivedSrcService");
        String w = params.get("w");
        String h = params.get("w");

    }

    private String queryKeepeekSignature(String path) throws RepositoryException {
        try {
            String schema = keepeekProviderConfig.getApiSchema();
            String endpoint = keepeekProviderConfig.getApiEndPoint();
            String apiAccount = keepeekProviderConfig.getApiAccount();
            String apiSecret = keepeekProviderConfig.getApiSecret();

            URIBuilder builder = new URIBuilder().setScheme(schema).setHost(endpoint).setPath(path);

            URI uri = builder.build();

            long l = System.currentTimeMillis();
            HttpGet getMethod = new HttpGet(uri);

            String encoding = Base64.getEncoder().encodeToString((apiAccount+":"+apiSecret).getBytes("UTF-8"));
            getMethod.setHeader(HttpHeaders.AUTHORIZATION,"Basic " + encoding);
            getMethod.setHeader("Content-Type","application/json");
            CloseableHttpResponse resp = null;
            try {
                resp = httpClient.execute(getMethod);
                //TODO
                KeepeekAsset keepeekAsset = mapper.readValue(EntityUtils.toString(resp.getEntity()),KeepeekAsset.class);
                return keepeekAsset;

            } finally {
                if (resp != null) {
                    resp.close();
                }
                LOGGER.debug("Request {} executed in {} ms",uri, (System.currentTimeMillis() - l));
            }
        } catch (Exception e) {
            LOGGER.error("Error while querying Keepeek", e);
            throw new RepositoryException(e);
        }
    }
}
