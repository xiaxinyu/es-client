package com.xiaxinyu.es.client.es.dto;

import lombok.*;

/**
 * @author Caiguang
 * @Description: agent部署写入es日志实体类 兼容以前的es
 * @CreateDate: 2019/10/19
 */
@Data
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
