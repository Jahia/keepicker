package org.jahia.se.modules.dam.keepeek.edp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jahia.modules.external.ExternalData;
import org.jahia.modules.external.ExternalDataSource;
import org.jahia.se.modules.dam.keepeek.model.KeepeekAsset;
import org.jahia.se.modules.dam.keepeek.service.KeepeekProviderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.ItemNotFoundException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import java.net.URI;
import java.util.*;

//,ExternalDataSource.Searchable.class not used for now, needed if you want to use AugSearch with external asset
public class KeepeekDataSource implements ExternalDataSource{
    private static final Logger LOGGER = LoggerFactory.getLogger(KeepeekDataSource.class);

//    private static final String ASSET_ENTRY = "assets";
//    private static final String ASSET_ENTRY_EXPAND = "embeds,thumbnails,file_properties";

    private final ObjectMapper mapper = new ObjectMapper();
    private final KeepeekProviderConfig keepeekProviderConfig;
    private final KeepeekCacheManager keepeekCacheManager;
    private final CloseableHttpClient httpClient;

    public KeepeekDataSource(KeepeekProviderConfig keepeekProviderConfig, KeepeekCacheManager keepeekCacheManager) {
        this.keepeekProviderConfig = keepeekProviderConfig;
        this.keepeekCacheManager = keepeekCacheManager;
        // instantiate HttpClient
        this.httpClient = HttpClients.createDefault();
    }

    @Override
    public List<String> getChildren(String s) throws RepositoryException {
        List<String> child = new ArrayList<String>();
        return child;
    }

    @Override
    public ExternalData getItemByIdentifier(String identifier) throws ItemNotFoundException {
        try {
            if (identifier.equals("root")) {
                return new ExternalData(identifier, "/", "jnt:contentFolder", new HashMap<String, String[]>());
            }else{
                synchronized (this){
                    KeepeekAsset keepeekAsset = keepeekCacheManager.getKeepeekAsset(identifier);
                    if(keepeekAsset == null){
                        LOGGER.debug("no cacheEntry for : "+identifier);
                        final String path = "/dam/medias/"+identifier;
//                        Map<String, String> query = new LinkedHashMap<String, String>();
//                        query.put("expand",ASSET_ENTRY_EXPAND);
                        keepeekAsset = queryKeepeek(path);
                        keepeekCacheManager.cacheKeepeekAsset(keepeekAsset);
                    }
                    ExternalData data = new ExternalData(identifier, "/"+identifier, keepeekAsset.getJahiaNodeType(), keepeekAsset.getProperties());
//                    List<String> mixins = new ArrayList<String>();
//                    mixins.add("cloudymix:cloudyAsset");
//                    data.setMixin(mixins);

                    return data;
                }
            }
        } catch (Exception e) {
            LOGGER.error("",e);
            throw new ItemNotFoundException(e);
        }
    }

    @Override
    public ExternalData getItemByPath(String path) throws PathNotFoundException {
        String[] splitPath = path.split("/");
        try {
            if (path.endsWith("j:acl")) {
                throw new PathNotFoundException(path);
            }

            if (splitPath.length <= 1) {
                return getItemByIdentifier("root");

            } else if (splitPath.length == 2) {
                return getItemByIdentifier(splitPath[1]);

            }
        } catch (ItemNotFoundException e) {
            throw new PathNotFoundException(e);
        }
        throw new PathNotFoundException();
    }

    @Override
    public Set<String> getSupportedNodeTypes() {
        return Sets.newHashSet(
                "jnt:contentFolder",
                "kpknt:image",
//                "kpknt:video",
//                "kpknt:document",
                "kpknt:other"
        );
    }

    @Override
    public boolean isSupportsHierarchicalIdentifiers() {
        return false;
    }

    @Override
    public boolean isSupportsUuid() {
        return false;
    }

    @Override
    public boolean itemExists(String s) {
        return false;
    }

    private KeepeekAsset queryKeepeek(String path) throws RepositoryException {
        LOGGER.debug("Query Keepeek with path : {} ",path);
        try {
            String schema = keepeekProviderConfig.getApiSchema();
            String endpoint = keepeekProviderConfig.getApiEndPoint();
            String apiAccount = keepeekProviderConfig.getApiAccount();
            String apiSecret = keepeekProviderConfig.getApiSecret();

//            List<NameValuePair> parameters = new ArrayList<>(query.size());

//            for (Map.Entry<String, String> entry : query.entrySet()) {
//                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//            }

            URIBuilder builder = new URIBuilder()
                    .setScheme(schema)
                    .setHost(endpoint)
                    .setPath(path);

            URI uri = builder.build();

            long l = System.currentTimeMillis();
            HttpGet getMethod = new HttpGet(uri);

            //NOTE Keepeek return content in ISO-8859-1 even if Accept-Charset = UTF-8 is set.
            //Need to use appropriate charset later to read the inputstream response.
            String encoding = Base64.getEncoder().encodeToString((apiAccount+":"+apiSecret).getBytes("UTF-8"));
            getMethod.setHeader(HttpHeaders.AUTHORIZATION,"Basic " + encoding);
            getMethod.setHeader("Content-Type","application/json");
//            getMethod.setRequestHeader("Accept-Charset","ISO-8859-1");
//            getMethod.setRequestHeader("Accept-Charset","UTF-8");
            CloseableHttpResponse resp = null;
            try {
                resp = httpClient.execute(getMethod);
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
