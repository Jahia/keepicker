package org.jahia.se.modules.dam.keepeek.edp;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.jahia.se.modules.dam.keepeek.model.KeepeekAsset;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.cache.CacheHelper;
import org.jahia.services.cache.ModuleClassLoaderAwareCacheEntry;
import org.jahia.services.cache.ehcache.EhCacheProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(service = KeepeekCacheManager.class, immediate = true)
public class KeepeekCacheManager {
    private static final String CACHE_NAME = "cacheKeepeek";
    private static final int TIME_TO_LIVE_SECONDS = 28800;
    private static final int TIME_TO_IDLE_SECONDS = 3600;

    private Ehcache cache;


    private static Ehcache initCache(EhCacheProvider cacheProvider, String cacheName) {
        CacheManager cacheManager = cacheProvider.getCacheManager();
        Ehcache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            cache = createKeepeekCache(cacheManager, cacheName);
        } else {
            cache.removeAll();
        }
        return cache;
    }

    private static Ehcache createKeepeekCache(CacheManager cacheManager, String cacheName) {
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName(cacheName);
        cacheConfiguration.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU);
        cacheConfiguration.setEternal(false);
        cacheConfiguration.timeToLiveSeconds(TIME_TO_LIVE_SECONDS);
        cacheConfiguration.setTimeToIdleSeconds(TIME_TO_IDLE_SECONDS);
        // Create a new cache with the configuration
        Ehcache cache = new Cache(cacheConfiguration);
        cache.setName(cacheName);
        // Cache name has been set now we can initialize it by putting it in the manager.
        // Only Cache manager is initializing caches.
        return cacheManager.addCacheIfAbsent(cache);
    }

    @Activate
    public void onActivate() {
        EhCacheProvider cacheProvider = (EhCacheProvider) SpringContextSingleton.getBean("ehCacheProvider");
        cache = initCache(cacheProvider, CACHE_NAME);
    }

    @Deactivate
    public void onDeactivate() {
        flush();
    }

    /**
     * This method flushes the keepeek cache
     */
    public void flush() {
        // flush
        if (cache != null) {
            cache.removeAll();
        }
    }

    public KeepeekAsset getKeepeekAsset(String cacheKey){
        return (KeepeekAsset) CacheHelper.getObjectValue(cache, cacheKey);
    };

    public void cacheKeepeekAsset (KeepeekAsset keepeekAsset){
        cache.put(new Element(keepeekAsset.getId(),new ModuleClassLoaderAwareCacheEntry(keepeekAsset, CACHE_NAME)));
    }
}
