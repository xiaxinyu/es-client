package com.xiaxinyu.es.client;

import com.xiaxinyu.es.client.dto.TaskLogDTO;
import com.xiaxinyu.es.client.es.ElasticSearchClient;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ESClientApplication.class})
public class ESClientTest {
    private String index;

    @Autowired
    ElasticSearchClient elasticSearchClient;

    @Before
    public void before() throws Exception {
        index = "summer";
    }

    @Test
    public void testCreateIndex() {
        elasticSearchClient.createIndex(index);
    }

    @Test
    public void testAddIndex() {
        String uuid = UUID.randomUUID().toString();
        TaskLogDTO taskLogDto = TaskLogDTO.builder().taskName("test-" + uuid).taskExecuteId(uuid)
                .host("127.0.0.1").result("success").build();
        elasticSearchClient.addIndex(index, taskLogDto);
    }

    @Test
    public void testQueryIndex() {
        List<String> result = elasticSearchClient.queryIndex(index, "34137c79-3fd8-49ec-911e-e87c5453aba6");
        Assert.assertNotNull(result);
        Assert.assertTrue(!result.isEmpty());
        log.info("结果：{}", result.get(0));
    }

    @Test
    public void deleteIndex() {
        boolean result = elasticSearchClient.deleteIndex(index, "34137c79-3fd8-49ec-911e-e87c5453aba6");
        Assert.assertTrue(result);
    }
}
