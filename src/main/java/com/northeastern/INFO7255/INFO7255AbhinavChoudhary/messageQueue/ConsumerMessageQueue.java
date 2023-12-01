package com.northeastern.INFO7255.INFO7255AbhinavChoudhary.messageQueue;

// import java.io.IOException;
// import java.security.cert.X509Certificate;

// import javax.net.ssl.SSLContext;
// import javax.net.ssl.TrustManager;
// import javax.net.ssl.X509TrustManager;

// import org.apache.http.HttpHost;
// import org.apache.http.auth.AuthScope;
// import org.apache.http.auth.UsernamePasswordCredentials;
// import org.apache.http.client.CredentialsProvider;
// import org.apache.http.impl.client.BasicCredentialsProvider;
// import org.elasticsearch.action.delete.DeleteRequest;
// import org.elasticsearch.action.index.IndexRequest;
// import org.elasticsearch.action.index.IndexResponse;
// import org.elasticsearch.client.RequestOptions;
// import org.elasticsearch.client.RestClient;
// import org.elasticsearch.client.RestHighLevelClient;
// import org.elasticsearch.client.indices.CreateIndexRequest;
// import org.elasticsearch.client.indices.GetIndexRequest;
// import org.elasticsearch.common.settings.Settings;
// import org.elasticsearch.common.xcontent.XContentType;
// import org.json.JSONObject;

// import redis.clients.jedis.Jedis;

public class ConsumerMessageQueue {
    // private static Jedis jedis;
    // private static final String USERNAME = "elastic";
    // private static final String PASSWORD = "RP-Ywg*hkHCE7RN*6KwR";
    // private static final String IndexName="planindex";
    
	// private static RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));

    // private static RestHighLevelClient client = new RestHighLevelClient(
    //         RestClient.builder(new HttpHost("localhost", 9200, "http"))
    //                 .setHttpClientConfigCallback(httpAsyncClientBuilder ->
    //                         httpAsyncClientBuilder.setDefaultCredentialsProvider(createCredentialsProvider())
    //                 )
    // );

    // private static CredentialsProvider createCredentialsProvider() {
    //     CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    //     credentialsProvider.setCredentials(
    //             AuthScope.ANY,
    //             new UsernamePasswordCredentials(USERNAME, PASSWORD)
    //     );
    //     return credentialsProvider;
    // }

    // private static RestHighLevelClient client = createRestHighLevelClient();

    // private static RestHighLevelClient createRestHighLevelClient() {
    //     try {
    //         // Create a custom SSLContext that trusts all certificates
    //         SSLContext sslContext = SSLContext.getInstance("TLS");
    //         sslContext.init(null, new TrustManager[] {
    //             new X509TrustManager() {
    //                 public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {}
    //                 public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {}
    //                 public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    //             }
    //         }, null);

    //         // Use the custom SSLContext when creating the Elasticsearch client
    //         return new RestHighLevelClient(
    //             RestClient.builder(new HttpHost("localhost", 9200, "https"))
    //                 .setHttpClientConfigCallback(httpAsyncClientBuilder ->
    //                     httpAsyncClientBuilder.setSSLContext(sslContext).setDefaultCredentialsProvider(createCredentialsProvider())
    //                 )
    //         );
    //     } catch (Exception e) {
    //         throw new RuntimeException("Error creating RestHighLevelClient", e);
    //     }
    // }

    // public static void main(String args[]) throws IOException {
	// 	jedis = new Jedis();
	// 	System.out.println("Consumer MQ started");
	// 	while (true) {
	// 		String message = jedis.rpoplpush("messageQueue", "WorkingMQ");
	// 		if (message == null) {
	// 			continue;
	// 		}
	// 		JSONObject result = new JSONObject(message);
			
	// 		// Get action
	// 		Object obj = result.get("isDelete");
	// 		System.out.println("isDelete: " + obj.toString());
						
	// 		boolean isDelete = Boolean.parseBoolean(obj.toString());
	// 		if(!isDelete) {
	// 			JSONObject plan= new JSONObject(result.get("message").toString());
	// 			postDocument(plan);
	// 		}else {
	// 			deleteDocument(result.get("message").toString());
	// 		}
	// 	}
	// }
	
	// private static boolean indexExists() throws IOException {
	// 	GetIndexRequest request = new GetIndexRequest(IndexName); 
	// 	boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
	// 	return exists;
	// }
	
	// private static String postDocument(JSONObject plan) throws IOException {
	// 	if(!indexExists()) {
	// 		createElasticIndex();
	// 	}	
	// 	IndexRequest request = new IndexRequest(IndexName);
	// 	request.id(plan.get("objectId").toString());
	// 	request.source(plan.toString(), XContentType.JSON);
	// 	IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
	// 	System.out.println("response id: "+indexResponse.getId());
	// 	return indexResponse.getResult().name();
	// }
	
	// private static void createElasticIndex() throws IOException {
	// 	CreateIndexRequest request = new CreateIndexRequest(IndexName);
	// 	request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));
	// 	String mapping = getMapping();
	// 	request.mapping(mapping, XContentType.JSON);

	// 	client.indices().create(request, RequestOptions.DEFAULT); 
	// }
	
	// private static void deleteDocument(String documentId) throws IOException {
	// 	DeleteRequest request = new DeleteRequest(IndexName, documentId);
	// 	client.delete(
	// 	        request, RequestOptions.DEFAULT);
	// }
	
	// private static String getMapping() {
	// 	String mapping= "{\r\n" + 
	// 			"	\"properties\": {\r\n" + 
	// 			"		\"_org\": {\r\n" + 
	// 			"			\"type\": \"text\"\r\n" + 
	// 			"		},\r\n" + 
	// 			"		\"objectId\": {\r\n" + 
	// 			"			\"type\": \"keyword\"\r\n" + 
	// 			"		},\r\n" + 
	// 			"		\"objectType\": {\r\n" + 
	// 			"			\"type\": \"text\"\r\n" + 
	// 			"		},\r\n" + 
	// 			"		\"planType\": {\r\n" + 
	// 			"			\"type\": \"text\"\r\n" + 
	// 			"		},\r\n" + 
	// 			"		\"creationDate\": {\r\n" + 
	// 			"			\"type\": \"date\",\r\n" + 
	// 			"			\"format\" : \"MM-dd-yyyy\"\r\n" + 
	// 			"		},\r\n" + 
	// 			"		\"planCostShares\": {\r\n" +
    //             "			\"type\": \"nested\",\r\n" +
    //             "			\"properties\": {\r\n" +
	// 			"				\"copay\": {\r\n" + 
	// 			"					\"type\": \"long\"\r\n" + 
	// 			"				},\r\n" + 
	// 			"				\"deductible\": {\r\n" + 
	// 			"					\"type\": \"long\"\r\n" + 
	// 			"				},\r\n" + 
	// 			"				\"_org\": {\r\n" + 
	// 			"					\"type\": \"text\"\r\n" + 
	// 			"				},\r\n" + 
	// 			"				\"objectId\": {\r\n" + 
	// 			"					\"type\": \"keyword\"\r\n" + 
	// 			"				},\r\n" + 
	// 			"				\"objectType\": {\r\n" + 
	// 			"					\"type\": \"text\"\r\n" + 
	// 			"				}\r\n" + 
	// 			"			}\r\n" + 
	// 			"		},\r\n" + 
	// 			"		\"linkedPlanServices\": {\r\n" + 
	// 			"			\"type\": \"nested\",\r\n" + 
	// 			"			\"properties\": {\r\n" + 
	// 			"				\"_org\": {\r\n" + 
	// 			"					\"type\": \"text\"\r\n" + 
	// 			"				},\r\n" + 
	// 			"				\"objectId\": {\r\n" + 
	// 			"					\"type\": \"keyword\"\r\n" + 
	// 			"				},\r\n" + 
	// 			"				\"objectType\": {\r\n" + 
	// 			"					\"type\": \"text\"\r\n" + 
	// 			"				},\r\n" + 
	// 			"				\"linkedService\": {\r\n" +
    //             "                   \"type\": \"nested\",\r\n" +
    //             "					\"properties\": {\r\n" +
	// 			"						\"name\": {\r\n" + 
	// 			"							\"type\": \"text\"\r\n" + 
	// 			"						},\r\n" + 
	// 			"						\"_org\": {\r\n" + 
	// 			"							\"type\": \"text\"\r\n" + 
	// 			"						},\r\n" + 
	// 			"						\"objectId\": {\r\n" + 
	// 			"							\"type\": \"keyword\"\r\n" + 
	// 			"						},\r\n" + 
	// 			"						\"objectType\": {\r\n" + 
	// 			"							\"type\": \"text\"\r\n" + 
	// 			"						}\r\n" + 
	// 			"					}\r\n" + 
	// 			"				},\r\n" + 
	// 			"				\"planserviceCostShares\": {\r\n" +
    //             "                  \"type\": \"nested\",\r\n" +
	// 			"					\"properties\": {\r\n" + 
	// 			"						\"copay\": {\r\n" + 
	// 			"							\"type\": \"long\"\r\n" + 
	// 			"						},\r\n" + 
	// 			"						\"deductible\": {\r\n" + 
	// 			"							\"type\": \"long\"\r\n" + 
	// 			"						},\r\n" + 
	// 			"						\"_org\": {\r\n" + 
	// 			"							\"type\": \"text\"\r\n" + 
	// 			"						},\r\n" + 
	// 			"						\"objectId\": {\r\n" + 
	// 			"							\"type\": \"keyword\"\r\n" + 
	// 			"						},\r\n" + 
	// 			"						\"objectType\": {\r\n" + 
	// 			"							\"type\": \"text\"\r\n" + 
	// 			"						}\r\n" + 
	// 			"					}\r\n" + 
	// 			"				}\r\n" + 
	// 			"			}\r\n" + 
	// 			"		}\r\n" + 
	// 			"	}\r\n" + 
	// 			"}";
		
	// 	return mapping;
	// }
}
