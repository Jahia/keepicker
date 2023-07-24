package org.jahia.se.modules.dam.keepeek.edp;
import org.jahia.exceptions.JahiaInitializationException;
import org.jahia.modules.external.ExternalContentStoreProvider;
import org.jahia.modules.external.ExternalProviderInitializerService;
import org.jahia.se.modules.dam.keepeek.service.KeepeekMountPointService;
import org.jahia.se.modules.dam.keepeek.service.KeepeekProviderConfig;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRStoreService;
import org.jahia.services.sites.JahiaSitesService;
import org.jahia.services.usermanager.JahiaGroupManagerService;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.util.Arrays;


/**
 * Factory for the EDP keepeek mount point
 */
@Component(service = {KeepeekMountPointService.class}, immediate = true)
public class KeepeekMountPointServiceImpl implements  KeepeekMountPointService{
    private static final Logger logger = LoggerFactory.getLogger(KeepeekDataSource.class);

//    public static final String WIDEN_NODETYPE = "kibanant:dashboard";
//    private static final List<String> EXTENDABLE_TYPES = Arrays.asList(DASHBOARD_NODETYPE);
//    private static final List<String> OVERRIDABLE_ITEMS = Collections.singletonList("*.*");

    private ExternalContentStoreProvider keepeekProvider;

    // Core deps
    private JahiaUserManagerService userManagerService;
    private JahiaGroupManagerService groupManagerService;
    private JahiaSitesService sitesService;
    private JCRStoreService jcrStoreService;
    private JCRSessionFactory sessionFactory;

    // EDP deps
    private ExternalProviderInitializerService externalProviderInitializerService;

    // internal deps
    private KeepeekCacheManager keepeekCacheManager;

    @Reference
    public void setUserManagerService(JahiaUserManagerService userManagerService) {
        this.userManagerService = userManagerService;
    }

    @Reference
    public void setGroupManagerService(JahiaGroupManagerService groupManagerService) {
        this.groupManagerService = groupManagerService;
    }

    @Reference
    public void setSitesService(JahiaSitesService sitesService) {
        this.sitesService = sitesService;
    }

    @Reference
    public void setJcrStoreService(JCRStoreService jcrStoreService) {
        this.jcrStoreService = jcrStoreService;
    }

    @Reference
    public void setSessionFactory(JCRSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Reference
    public void setExternalProviderInitializerService(ExternalProviderInitializerService externalProviderInitializerService) {
        this.externalProviderInitializerService = externalProviderInitializerService;
    }

    @Reference
    public void setKeepeekCacheManager(KeepeekCacheManager keepeekCacheManager) {
        this.keepeekCacheManager = keepeekCacheManager;
    }

    @Override
    public void start(KeepeekProviderConfig keepeekProviderConfig) throws JahiaInitializationException {
        logger.info("Starting Keepeek mount point service");
        keepeekProvider = new ExternalContentStoreProvider();
        keepeekProvider.setUserManagerService(userManagerService);
        keepeekProvider.setGroupManagerService(groupManagerService);
        keepeekProvider.setSitesService(sitesService);
        keepeekProvider.setService(jcrStoreService);
        keepeekProvider.setSessionFactory(sessionFactory);
        keepeekProvider.setExternalProviderInitializerService(externalProviderInitializerService);

        keepeekProvider.setDataSource(new KeepeekDataSource(keepeekProviderConfig, keepeekCacheManager));
//        keepeekProvider.setExtendableTypes(EXTENDABLE_TYPES);
//        keepeekProvider.setOverridableItems(OVERRIDABLE_ITEMS);
        keepeekProvider.setDynamicallyMounted(false);
        keepeekProvider.setMountPoint("/sites/systemsite/contents/dam-keepeek");
        keepeekProvider.setKey("keepeek");
        keepeekProvider.start();
        logger.info("Keepeek mount point service started");
    }

    @Override
    public void stop() {
        if (keepeekProvider != null) {
            logger.info("Stopping Keepeek mount point service");
            keepeekProvider.stop();
            keepeekProvider = null;
            logger.info("Keepeek mount point service stopped");
        }
    }
}
