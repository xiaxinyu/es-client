package com.xiaxinyu.es.client.es.dto;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskLogDTO {
    private Long delta;
    private String endTime;
    private String host;
    private String result;
    private String startTime;
    private String status;
    private Long stepIndex;
    private String taskExecuteId;
    private Long taskIndex;
    private String taskName;
    private String stageName;
}
