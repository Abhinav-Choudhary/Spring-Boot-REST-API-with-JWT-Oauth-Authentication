package com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.northeastern.INFO7255.INFO7255AbhinavChoudhary.configuration.MessagingConfig;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class IndexingService {
    public static final String INDEX_NAME = "plan_index";
    RestClient httpClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
    JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper();
    ElasticsearchTransport transport = new RestClientTransport(httpClient, jsonpMapper);
    ElasticsearchClient esClient = new ElasticsearchClient(transport);
    private CountDownLatch latch = new CountDownLatch(1);
    private LinkedHashMap<String, Map<String, Object>> documentMap = new LinkedHashMap<>();
    private ArrayList<String> documentKeysList = new ArrayList<>();

    boolean indexExists() throws IOException {
        ExistsRequest request = ExistsRequest.of(e -> e.index(INDEX_NAME));
        return esClient.indices().exists(request).value();
    }

    private void create(JSONObject plan) throws IOException {
        if(!indexExists()) {
            createPlanIndex();
        }
        documentMap = new LinkedHashMap<>();
        convertMapToDocumentIndex(plan, "", "plan");

        for (Map.Entry<String, Map<String, Object>> entry : documentMap.entrySet()) {
            String parentId = entry.getKey().split(":")[0];
            String objectId = entry.getKey().split(":")[1];
            JSONObject document = new JSONObject(entry.getValue());
            IndexResponse response = esClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(objectId)
                    .routing(parentId)
                    .withJson(new StringReader(document.toString()))
            );
            System.out.println("Index was completed with: " + response.id() + " and parent id: " + parentId);
        }
    }

    private void delete(JSONObject jsonObject) throws IOException {
        documentKeysList = new ArrayList<>();
        convertJsonToKeys(jsonObject);
        for(String key: documentKeysList) {
            DeleteRequest request = DeleteRequest.of(i -> i.index(INDEX_NAME).id(key));
            DeleteResponse deleteResponse = esClient.delete(request);
            System.out.println("Deletion of index completed: "+deleteResponse.toString());
        }
    }

    private Map<String, Map<String, Object>> convertJsonToKeys(JSONObject jsonObject){
        Map<String, Map<String, Object>> map = new HashMap<>();
        Map<String, Object> childMap = new HashMap<>();

        for (String key : jsonObject.keySet()) {
            String uniqueId = jsonObject.get("objectId").toString();
            Object jsonValue = jsonObject.get(key);

            if (jsonValue instanceof JSONObject) convertJsonToKeys((JSONObject) jsonValue);
            else if (jsonValue instanceof JSONArray) convertJsonToKeysList((JSONArray) jsonValue);
            else {
                childMap.put(key, jsonValue);
                map.put(uniqueId, childMap);
            }
        }
        documentKeysList.add(jsonObject.get("objectId").toString());
        return map;
    }

    public List<Object> convertJsonToKeysList(JSONArray jsonArray) {
        List<Object> jsonObjectList = new ArrayList<>();
        for (Object item : jsonArray) {
            if (item instanceof JSONArray) item = convertJsonToKeysList((JSONArray) item);
            else if (item instanceof JSONObject) item = convertJsonToKeys((JSONObject) item);
            jsonObjectList.add(item);
        }
        return jsonObjectList;
    }


    private Map<String, Map<String, Object>> convertMapToDocumentIndex(JSONObject jsonObject, String parentId, String objectName ) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        Map<String, Object> childMap = new HashMap<>();

        for(String key: jsonObject.keySet()) {
            String uniqueStoreKey = jsonObject.get("objectType") + ":" + parentId;
            Object jsonValue = jsonObject.get(key);
            if (jsonValue instanceof JSONObject) convertMapToDocumentIndex((JSONObject) jsonValue, jsonObject.get("objectId").toString(), key);
            else if (jsonValue instanceof JSONArray) convertToList((JSONArray) jsonValue, jsonObject.get("objectId").toString(), key);
            else {
                childMap.put(key, jsonValue);
                map.put(uniqueStoreKey, childMap);
            }
        }
        Map<String, Object> temp = new HashMap<>();
        if(objectName.equals("plan")) {
            childMap.put("plan_joins", objectName);
        } else {
            temp.put("name", objectName);
            temp.put("parent", parentId);
            childMap.put("plan_joins", temp);
        }
        String id = parentId + ":" + jsonObject.get("objectId").toString();
        documentMap.put(id, childMap);
        return map;
    }

    private List<Object> convertToList(JSONArray jsonArray, String parentId, String objectName) {
        List<Object> list = new ArrayList<>();
        for (Object item: jsonArray) {
            if (item instanceof JSONArray) item = convertToList((JSONArray) item, parentId, objectName);
            else if (item instanceof JSONObject) item = convertMapToDocumentIndex((JSONObject) item, parentId, objectName);
            list.add(item);
        }
        return list;
    }

    @RabbitListener(queues = MessagingConfig.MESSAGE_QUEUE_NAME)
    public void receiveMessage(IndexingMessage message) throws IOException {
        String action = message.action;
        switch(action) {
            case "CREATE":
                create(new JSONObject(message.body));
                break;
            case "DELETE":
                delete(new JSONObject(message.body));
                break;
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    void createPlanIndex() throws IOException {
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
        final String assetJsonSource = "src/main/resources/IndexingPropAndMapping.json";
        InputStream indexingJsonInput = new FileInputStream(assetJsonSource);
        CreateIndexResponse response = esClient.indices().create(c -> c
                .index(INDEX_NAME)
                .withJson(indexingJsonInput)
        );
        System.out.println("Index creation was completed successfully: "+response.acknowledged());
    }
}
