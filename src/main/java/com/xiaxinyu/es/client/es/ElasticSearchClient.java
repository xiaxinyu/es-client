package com.xiaxinyu.es.client.es;

import com.alibaba.fastjson.JSONObject;
import com.xiaxinyu.es.client.dto.TaskLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
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
    @Autowired
    RestHighLevelClient restHighLevelClient;

    public boolean createIndex(String index) {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(Settings.builder().put("index.number_of_shards", 10).put("index.number_of_replicas", 1));

        //设置别名
        request.alias(new Alias(index + "alias"));
        //设置创建索引超时2分钟
        request.setTimeout(TimeValue.timeValueMinutes(2));

        try {
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            // 处理响应
            boolean acknowledged = createIndexResponse.isAcknowledged();
            boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();
            System.out.println(acknowledged + "," + shardsAcknowledged);
            log.info("创建索引响应：acknowledged={}, shardsAcknowledged={}", acknowledged, shardsAcknowledged);
        } catch (IOException e) {
            log.error("索引创建异常：index={}", index, e);
            return false;
        }
        return true;
    }

    public boolean addIndex(String index, TaskLogDTO taskLogDto) {
        try {
            IndexRequest indexRequest = new IndexRequest(index);
            indexRequest.source(JSONObject.toJSONString(taskLogDto), XContentType.JSON);
            log.debug("写入到es的日志数据是:{}", taskLogDto);
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("部署任务日志写入es报错", e);
            return false;
        }
        return true;
    }

    public List<String> queryIndex(String index, String taskExecuteId) {
        log.info("根据任务ID查询任务: index={}, taskExecuteId={}", index, taskExecuteId);
        List<String> result = new ArrayList<>();
        try {
            SearchHit[] searchHitArray = commonQuery(index, taskExecuteId);
            if (Objects.nonNull(searchHitArray) && searchHitArray.length > 0) {
                result = new ArrayList<>();
                for (SearchHit searchHit : searchHitArray) {
                    result.add(searchHit.getSourceAsString());
                }
            }
        } catch (Exception e) {
            log.error("查询出现错误", e);
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    public boolean deleteIndex(String index, String taskExecuteId) {
        try {
            SearchHit[] searchHitArray = commonQuery(index, taskExecuteId);
            if (Objects.nonNull(searchHitArray) && searchHitArray.length > 0) {
                DeleteRequest deleteRequest = new DeleteRequest();
                for (SearchHit hit : searchHitArray) {
                    deleteRequest = new DeleteRequest(index, hit.getId());
                    DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
                    log.info("Delete Done【" + deleteResponse.getId() + "】,Status:【" + deleteResponse.status() + "】");
                }
            }
        } catch (Exception e) {
            log.error("删除索引出现错误：index={}, taskExecuteId={}", index, taskExecuteId, e);
            return false;
        }
        return true;
    }

    public SearchHit[] commonQuery(String index, String taskExecuteId) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);

        //查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(new MatchQueryBuilder("taskExecuteId", taskExecuteId));
        //sourceBuilder.sort(new FieldSortBuilder("endTime.keyword").order(SortOrder.ASC));
        //sourceBuilder.from(QUERY_FROM_INDEX);
        // sourceBuilder.size(QUERY_SIZE);
        searchRequest.source(sourceBuilder);

        //查询加解析
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        if (Objects.isNull(response) || Objects.isNull(response.getHits())) {
            return null;
        }
        SearchHits searchHits = response.getHits();
        SearchHit[] searchHitArray = searchHits.getHits();
        return searchHitArray;
    }
}
