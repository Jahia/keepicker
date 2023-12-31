package org.jahia.se.modules.dam.keepeek;

import org.apache.commons.lang3.StringUtils;
import org.jahia.se.modules.dam.keepeek.service.KeepeekMountPointService;
import org.jahia.se.modules.dam.keepeek.service.KeepeekProviderConfig;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;

@Component(service = { KeepeekProviderConfig.class,
        ManagedService.class }, property = "service.pid=org.jahia.se.modules.keepicker_credentials", immediate = true)
public class KeepeekProviderConfigImpl implements ManagedService, KeepeekProviderConfig{

    public static final Logger logger = LoggerFactory.getLogger(KeepeekProviderConfigImpl.class);

    private Dictionary<String, ?> properties = new Hashtable<>();
    private BundleContext bundleContext;
    private KeepeekMountPointService keepeekMountPointService;

    @Reference
    public void setKeepeekMountPointService(KeepeekMountPointService keepeekMountPointService) {
        this.keepeekMountPointService = keepeekMountPointService;
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        if (properties != null) {
            this.properties = properties;
            logger.info("Keepeek provider configuration reloaded");
            if (isConfigurationReady() && bundleContext.getBundle().getState() == Bundle.ACTIVE) {
                logger.info("Keepeek provider configuration is ready");
                startServices();
            } else {
                logger.warn("Keepeek provider configuration is incomplete, please check your configuration");
                stopServices();
            }
        } else {
            this.properties = new Hashtable<>();
            logger.info("Keepeek provider configuration removed");
            stopServices();
        }
    }

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Deactivate
    public void deactivate() throws ConfigurationException {
        stopServices();
    }

    private void startServices() throws ConfigurationException {
        try {
            keepeekMountPointService.start(this);
        } catch (Exception e) {
            throw new ConfigurationException("Global config", "Error starting Keepeek Provider services", e);
        }
    }

    private void stopServices() throws ConfigurationException {
        try {
            keepeekMountPointService.stop();
        } catch (Exception e) {
            throw new ConfigurationException("Global config", "Error stopping Keepeek Provider services", e);
        }
    }

    private boolean isConfigurationReady(){
        return StringUtils.isNotEmpty(getApiSchema()) &&
                StringUtils.isNotEmpty(getApiEndPoint()) &&
                StringUtils.isNotEmpty(getApiAccount()) &&
                StringUtils.isNotEmpty(getApiSecret());
    }

    @Override
    public String getApiSchema() { return (String) properties.get("keepeek_provider.back.apiSchema"); }

    @Override
    public String getApiEndPoint() { return (String) properties.get("keepeek_provider.back.apiEndPoint"); }

    @Override
    public String getApiAccount() { return (String) properties.get("keepeek_provider.back.apiAccount"); }

    @Override
    public String getApiSecret() { return (String) properties.get("keepeek_provider.back.apiSecret"); }

}
