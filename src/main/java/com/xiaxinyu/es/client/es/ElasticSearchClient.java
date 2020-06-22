package com.xiaxinyu.es.client.es;

import com.alibaba.fastjson.JSONObject;
import com.xiaxinyu.es.client.es.dto.TaskLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ElasticSearch Rest Client
 *
 * @author XIAXINYU3
 * @date 2019.8.16
 */
@Component
@Slf4j
public class ElasticSearchClient {
    private static final String DEFAULT_INDICES = "es_task_result";
    private static final int QUERY_FROM_INDEX = 0;
    private static final int QUERY_SIZE = 2000;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    public List<String> queryByTaskExecuteId(String taskExecuteId) {
        log.info("根据任务ID查询任务， taskExecuteId={}", taskExecuteId);
        List<String> result = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices(DEFAULT_INDICES);

            //查询条件
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(new MatchQueryBuilder("taskExecuteId", taskExecuteId));
            sourceBuilder.sort(new FieldSortBuilder("endTime.keyword").order(SortOrder.ASC));
            sourceBuilder.from(QUERY_FROM_INDEX);
            sourceBuilder.size(QUERY_SIZE);
            searchRequest.source(sourceBuilder);

            //查询加解析
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            if (Objects.nonNull(response) && Objects.nonNull(response.getHits())) {
                SearchHits searchHits = response.getHits();
                SearchHit[] searchHitArray = searchHits.getHits();
                if (Objects.nonNull(searchHitArray) && searchHitArray.length > 0) {
                    result = new ArrayList<>();
                    for (SearchHit searchHit : searchHitArray) {
                        result.add(searchHit.getSourceAsString());
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询出现错误", e);
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    /**
     * 将处理日志写入es
     *
     * @param taskLogDTO
     */
    public void insertTaskLog(TaskLogDTO taskLogDTO) {
        try {
            if (StringUtils.isNotBlank(taskLogDTO.getResult())) {
                IndexRequest indexRequest = new IndexRequest(DEFAULT_INDICES);
                indexRequest.source(JSONObject.toJSONString(taskLogDTO), XContentType.JSON);
                log.debug("写入到es的日志数据是:{}", taskLogDTO);
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            log.error("部署任务日志写入es报错", e);
            throw new RuntimeException("insert.es.logs.error");
        }

    }
}
