package com.mutualfund.config;

import java.time.Duration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager ehCacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();

        createCache(
                cacheManager,
                "users",
                Long.class,
                com.mutualfund.model.response.UserResponse.class,
                10,
                1000);

        createCache(cacheManager, "allUsers", String.class, java.util.List.class, 5, 100);

        createCache(
                cacheManager,
                "mutualFunds",
                Long.class,
                com.mutualfund.model.entity.MutualFund.class,
                5,
                500);

        createCache(cacheManager, "allMutualFunds", String.class, java.util.List.class, 5, 100);

        createCache(cacheManager, "holdings", Long.class, java.util.List.class, 3, 500);

        createCache(cacheManager, "transactions", Long.class, java.util.List.class, 10, 500);

        return cacheManager;
    }

    private <K, V> void createCache(
            CacheManager cacheManager,
            String cacheName,
            Class<K> keyType,
            Class<V> valueType,
            long ttlMinutes,
            long heapEntries) {

        CacheConfigurationBuilder<K, V> configBuilder =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                keyType,
                                valueType,
                                ResourcePoolsBuilder.newResourcePoolsBuilder()
                                        .heap(heapEntries, EntryUnit.ENTRIES))
                        .withExpiry(
                                ExpiryPolicyBuilder.timeToLiveExpiration(
                                        Duration.ofMinutes(ttlMinutes)));

        javax.cache.configuration.Configuration<K, V> configuration =
                Eh107Configuration.fromEhcacheCacheConfiguration(configBuilder);

        cacheManager.createCache(cacheName, configuration);
    }
}
