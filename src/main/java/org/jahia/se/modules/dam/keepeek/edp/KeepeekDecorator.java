package org.jahia.se.modules.dam.keepeek.edp;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jahia.osgi.BundleUtils;
import org.jahia.se.modules.dam.keepeek.model.KeepeekAsset;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRNodeDecorator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.camel.model.rest.RestParamType.query;
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

    @Override
    public String getUrl(List<String> params) {
        KeepeekCacheManager keepeekCacheManager = BundleUtils.getOsgiService(KeepeekCacheManager.class, null);
        try {
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

                //Note : is poiX & poiY should be part of the key ? if yes uncomment ;
    //            if(this.getPropertyAsString("kpk:poiX") != null)
    //                keepeekProps.put("poiX", this.getPropertyAsString("kpk:poiX"));
    //            if(this.getPropertyAsString("kpk:poiY") != null)
    //                keepeekProps.put("poiY", this.getPropertyAsString("kpk:poiY"));

                //build the key
                StringBuilder sb = new StringBuilder();
                for(String key : keepeekProps.keySet()){
                    sb.append(key + keepeekProps.get(key));
                }

                String resizeKey = sb.toString();
                try{
                    KeepeekAsset keepeekAsset = keepeekCacheManager.getKeepeekAsset(this.getPropertyAsString("kpk:assetId"));

                    String[] urls = keepeekAsset.getProperty("kpk:cachedDerivedUrls");
                    String derivedUrl = null;
                    JSONObject jsonUrls;

                    if(urls == null || urls.length == 0){
                        derivedUrl = getResizedUrl(keepeekProps);
                        jsonUrls = new JSONObject();
                    }else{
                        jsonUrls = new JSONObject(urls[0]);
                        derivedUrl = (String) jsonUrls.opt(resizeKey);
                        if(derivedUrl == null || derivedUrl.trim().isEmpty()){
                            derivedUrl = getResizedUrl(keepeekProps);
                        }
                    }
                    jsonUrls.put(resizeKey,derivedUrl);
                    //store the url into the content in cache
                    keepeekAsset.addProperty("kpk:cachedDerivedUrls",jsonUrls.toString());
                    keepeekCacheManager.cacheKeepeekAsset(keepeekAsset);

                    return derivedUrl;
                } catch (JSONException e) {
                    //else build the url
                    return this.getUrl();
                } catch (UnsupportedEncodingException e) {
                    return this.getUrl();
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
        }catch (RepositoryException e) {
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

    private String getResizedUrl(Map<String,String> params) throws JSONException, UnsupportedEncodingException, RepositoryException {
        String src = this.getPropertyAsString("kpk:derivedSrcService");
        if(src == null || src.isEmpty())
            return null;
        String t = "fill-down";
        String f = "webp";
        String extension = ".webp";
        Map<String, Object> properties = getConfigProperties();
        String servicePath = (String) properties.get("keepeek_provider.back.apiSignatureServicePath");
        String baseUrl = (String) properties.get("keepeek_provider.back.apiSignatureBaseUrl");

        JSONObject payload = new JSONObject();
        payload.put("baseUrl",baseUrl);

        StringBuilder path = new StringBuilder();
        path.append("/resize?t=").append(t).append("&src=").append(src).append("&f=").append(f);
        if(params.get("w") != null)
            path.append("&w=").append(params.get("w"));
        if(params.get("h") != null)
            path.append("&h=").append(params.get("h"));

        if(this.getPropertyAsString("kpk:poiX") != null)
            path.append("&poiX=").append(this.getPropertyAsString("kpk:poiX"));
        if(this.getPropertyAsString("kpk:poiY") != null)
            path.append("&poiY=").append(this.getPropertyAsString("kpk:poiY"));

        path.append("&extension=").append(extension);

        payload.append("resourcePaths",path.toString());
        final StringEntity jsonEntity = new StringEntity(payload.toString());


        JSONObject signatureResp  = new JSONObject(queryKeepeekSignature(servicePath,jsonEntity));
        String signedUrl = signatureResp.getJSONArray("signedUrls").optString(0);
        //TODO check return format -> string or json
        return signedUrl;
    }


    //http://thumbnail.services.keepeek.com/uwvyoZc4h-83FrubxPZTPSzrs1ARIGPt_TyL5UKbatQ/resize?src=kpk://tst/1/0/100-476kd2t22l.jpg&w=250&h=250&ex=true&f=webp
    private String queryKeepeekSignature(String path,StringEntity jsonEntity) throws RepositoryException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {

            Map<String, Object> properties = getConfigProperties();
            String schema = (String) properties.get("keepeek_provider.back.apiSchema");
            String endpoint = (String) properties.get("keepeek_provider.back.apiEndPoint");
            String apiAccount = (String) properties.get("keepeek_provider.back.apiAccount");
            String apiSecret = (String) properties.get("keepeek_provider.back.apiSecret");

            URIBuilder builder = new URIBuilder().setScheme(schema).setHost(endpoint).setPath(path);

            URI uri = builder.build();

            long l = System.currentTimeMillis();
            final HttpPost postMethod = new HttpPost(uri);
            postMethod.setEntity(jsonEntity);

            String encoding = Base64.getEncoder().encodeToString((apiAccount+":"+apiSecret).getBytes("UTF-8"));
            postMethod.setHeader(HttpHeaders.AUTHORIZATION,"Basic " + encoding);
            postMethod.setHeader("Content-Type","application/json");
            CloseableHttpResponse resp = null;
            try {
                resp = httpClient.execute(postMethod);
                //TODO
//                JSONObject response = new JSONObject(EntityUtils.toString(resp.getEntity()));
                String response = EntityUtils.toString(resp.getEntity());
                return response;

            } finally {
                if (resp != null) {
                    resp.close();
                }
                logger.debug("Request {} executed in {} ms",uri, (System.currentTimeMillis() - l));
            }
        } catch (Exception e) {
            logger.error("Error while querying Keepeek resize service", e);
            throw new RepositoryException(e);
        }
    }
    private Map<String, Object> getConfigProperties(){
        Map<String, Object> properties = null;
        ConfigurationAdmin configAdmin = BundleUtils.getOsgiService(ConfigurationAdmin.class, null);
        try {
            Configuration config = configAdmin.getConfiguration("org.jahia.se.modules.keepicker_credentials");
            Dictionary<String, ?> dict = config.getProperties();
            List<String> keys = Collections.list(dict.keys());
            properties = keys.stream()
                    .collect(Collectors.toMap(Function.identity(), dict::get));
        }catch (IOException e){
            logger.error("Error reading cloudinary config file e: "+e);
        }
        return properties;
    }
}
