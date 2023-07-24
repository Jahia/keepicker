package org.jahia.se.modules.dam.keepeek.service;

public interface KeepeekProviderConfig {
    /**
     * The http schema used to execute the request; default is 'https'
     * @return the Keepeek api schema
     */
    String getApiSchema();

    /**
     * The API endpoint; default is 'api.keepeek.com'
     * @return the Keepeek api endpoint
     */
    String getApiEndPoint();

    /**
     * The Keepeek API version; default is 'v1_1'
     * @return the Keepeek api version
     */
    String getApiVersion();

    /**
     * The API key
     * @return the Keepeek api key
     */
    String getApiKey();

    /**
     * The Keepeek API secret;
     * @return the Keepeek api secret
     */
    String getApiSecret();

    /**
     * The name of the keepeek cloud you want to connect to
     * @return the Keepeek cloud name
     */
    String getCloudName();
}
