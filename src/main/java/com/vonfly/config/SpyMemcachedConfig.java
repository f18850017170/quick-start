package com.vonfly.config;

import com.vonfly.util.SpyMemcachedManager;
import net.spy.memcached.*;
import net.spy.memcached.spring.MemcachedClientFactoryBean;
import net.spy.memcached.transcoders.SerializingTranscoder;
import net.spy.memcached.transcoders.Transcoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.FieldRetrievingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpyMemcachedConfig {
    @Value("${memcached.servers}")
    private String servers;
    @Value("${memcached.protocol}")
    private String protocol;
    @Value("${memcached.opTimeout}")
    private Long opTimeout;
    @Value("${memcached.timeoutExceptionThreshold}")
    private Integer timeoutExceptionThreshold;
    @Value("${memcached.locatorType}")
    private String locatorType;
    @Value("${memcached.failureMode}")
    private String failureMode;
    @Value("${memcached.useNagleAlgorithm}")
    private Boolean useNagleAlgorithm;
    @Bean
    public FieldRetrievingFactoryBean ketamaHash(){
        FieldRetrievingFactoryBean factoryBean = new FieldRetrievingFactoryBean();
        factoryBean.setStaticField("net.spy.memcached.DefaultHashAlgorithm.KETAMA_HASH");
        return factoryBean;
    }
    @Bean
    public SerializingTranscoder transcoder(){
        SerializingTranscoder transcoder = new SerializingTranscoder();
        transcoder.setCompressionThreshold(1024);
        transcoder.setCharset("UTF-8");
        return transcoder;
    }
    @Bean
    public MemcachedClientFactoryBean memcachedClient(Transcoder transcoder,HashAlgorithm ketamaHash){
        MemcachedClientFactoryBean factoryBean = new MemcachedClientFactoryBean();
        factoryBean.setServers(this.servers);
        factoryBean.setProtocol(ConnectionFactoryBuilder.Protocol.valueOf(this.protocol));
        factoryBean.setTranscoder(transcoder);
        factoryBean.setOpTimeout(opTimeout);
        factoryBean.setTimeoutExceptionThreshold(timeoutExceptionThreshold);
        factoryBean.setHashAlg(DefaultHashAlgorithm.KETAMA_HASH);
        factoryBean.setLocatorType(ConnectionFactoryBuilder.Locator.valueOf(this.locatorType));
        factoryBean.setFailureMode(FailureMode.valueOf(failureMode));
        factoryBean.setUseNagleAlgorithm(useNagleAlgorithm);
        return factoryBean;
    }
    @Bean
    public SpyMemcachedManager memcachedManager(MemcachedClient memcachedClient){
        SpyMemcachedManager memcachedManager = new SpyMemcachedManager();
        memcachedManager.setMemcachedClient(memcachedClient);
        return memcachedManager;
    }
}
