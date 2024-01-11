package org.jahia.se.modules.dam.keepeek.service;

public interface KeepeekProviderConfig {
    /**
     * The http schema used to execute the request; default is 'https'
     *
     * @return the Keepeek api schema
     */
    String getApiSchema();

    /**
     * The API endpoint; default is 'api.keepeek.com'
     *
     * @return the Keepeek api endpoint
     */
    String getApiEndPoint();

    /**
     * The API account
     *
     * @return the Keepeek api key
     */
    String getApiAccount();

    /**
     * The Keepeek API secret;
     *
     * @return the Keepeek api secret
     */
    String getApiSecret();
}
