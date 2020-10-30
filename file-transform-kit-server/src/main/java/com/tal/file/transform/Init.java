package com.tal.file.transform;

import com.tal.file.transform.common.storage.StorageConfig;
import com.tal.file.transform.common.storage.StorageFactory;
import com.tal.file.transform.common.storage.StorageFileNamer;
import com.tal.file.transform.common.storage.StorageZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description
 * <p>
 * </p>
 * DATE 2018/10/31.
 *
 * @author 刘江涛.
 */
@Configuration
@Slf4j
public class Init {


    @Autowired
    private Config config;

    @Bean
    public StorageFileNamer fileNamer() {
        return new StorageFileNamer();
    }

    @Bean
    public StorageZone storageZone() {
        StorageFactory.StorageProvider sp = null;
        String providerName = config.getStorageProvider();
        try {
            sp = StorageFactory.StorageProvider.valueOf(providerName);
            return StorageFactory.getZone(sp, getStorageSettings(config));
        } catch (Exception e) {
            log.error("Can't convert enum from \"" + providerName + "\"", e);
        }

       return null;
    }

    private StorageConfig getStorageSettings(Config conf) throws Exception{
        return new StorageConfig.Builder().setAccessKey(conf.getStorageAccessKey())
                .setBucket(conf.getStorageBucket()).setEndpoint(conf.getStorageEndpoint())
                .setLocalPath(conf.getStorageLocalPath()).setPrefixUrl(conf.getStoragePrefixUrl())
                .setSecretKey(conf.getStorageSecretKey()).build();
    }
}
