package com.xiaxinyu.es.client.es;

import com.xiaxinyu.es.client.core.ESClientProperties;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * es 配置类
 *
 * @author XIAXINYU3
 * @date 2019.8.16
 */
@Configuration
public class ElasticSearchConfiguration {
    @Autowired
    ESClientProperties properties;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(getRestClientBuilder());
    }

    @Bean
    public RestClientBuilder restClientBuilder() {
        return getRestClientBuilder();
    }

    private RestClientBuilder getRestClientBuilder() {
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(properties.getHost(), properties.getPort()))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultIOReactorConfig(
                                IOReactorConfig.custom().setIoThreadCount(properties.getWorkThread()).build());
                    }
                }).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        return requestConfigBuilder.setConnectionRequestTimeout(properties.getConnectionRequestTimeout())
                                .setConnectTimeout(properties.getConnectTimeout())
                                .setSocketTimeout(properties.getSocketTimeout());
                    }
                });
        return restClientBuilder;
    }
}
