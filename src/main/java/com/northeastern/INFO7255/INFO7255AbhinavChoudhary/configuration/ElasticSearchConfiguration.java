// package com.northeastern.INFO7255.INFO7255AbhinavChoudhary.configuration;

// import org.apache.http.HttpHost;
// import org.elasticsearch.client.RestClient;
// import org.elasticsearch.client.RestHighLevelClient;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

// @Configuration
// public class ElasticSearchConfiguration extends AbstractElasticsearchConfiguration  {

//     // extends AbstractElasticsearchConfiguration

//     @Override
//     @Bean
//     public RestHighLevelClient elasticsearchClient() {
//         return new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
//     }

// }
