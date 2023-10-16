package org.jahia.se.modules.dam.keepeek.service;

import org.jahia.exceptions.JahiaInitializationException;

/**
 * Service to handle keepeek mount point
 */
public interface KeepeekMountPointService {
    /**
     * Start and mount the keepeek EDP implementation
     * @param keepeekProviderConfig the config
     * @throws JahiaInitializationException
     */
    void start(KeepeekProviderConfig keepeekProviderConfig) throws JahiaInitializationException;

    /**
     * Stop and unmount the keepeek EDP implementation
     */
    void stop();
}
