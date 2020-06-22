package com.xiaxinyu.es.client.es.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SearchHitDTO {
    private String startTime;
    private String endTime;
    private String status;
    private String host;
}
