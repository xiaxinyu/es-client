package com.xiaxinyu.es.client.biz;

import com.alibaba.fastjson.JSONObject;
import com.xiaxinyu.es.client.dto.TaskLogDTO;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class TaskService {
    private static final String DEFAULT_INDICES = "test";
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


}
