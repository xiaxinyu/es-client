package com.xiaxinyu.es.client.core;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置
 * @author XIAXINYU3
 * @date 2020.6.12
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "es")
public class ESClientProperties {
    private String host;
    private Integer port;
    private Integer workThread;
    private Integer connectionRequestTimeout;
    private Integer connectTimeout;
    private Integer socketTimeout;
}
