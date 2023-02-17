package com.srijan.eis.mock.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.java.*;
import com.couchbase.client.java.kv.*;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.srijan.eis.mock.model.AuthData;
import com.srijan.eis.mock.utility.DataEncryption;
import com.couchbase.client.java.json.*;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


@Service
public class EISMockServiceImpl implements EISMockService{

	
	@Value("${storage.host}")
	private String connectionString;

//	@Value("${couchbase.port}")
//	private String couchbasePort;
	
	@Value("${storage.username}")
	private String username;
	
	@Value("${storage.password}")
	private String password;
	
	@Value("${storage.bucket}")
	private String bucketName;
	
	@Value("${storage.scope}")
	private String scopeName;
	
	@Value("${storage.collection.auth}")
	private String authCollectionName;
	
	@Value("${storage.collection.invoice}")
	private String invoiceCollectionName;
	
	
	@Autowired
	DataEncryption dataEncryption;
	
	// added for testing - to be removed
	private String createEncryptedData(String sessionKey) throws Exception {
		JsonObject obj = JsonObject.create().put("CompInvoiceId", "55510000028")
				.put("IssueDtm","20201110" )
				.put("EisUniqueId", "202011102000000100000001")
				.put("CorrYN", "N");
		String jsonStr1 = JSONObjectUtils.toJSONString(obj.toMap());
		
		JsonObject obj2 = JsonObject.create().put("CompInvoiceId", "55510000028")
				.put("IssueDtm","20201111" )
				.put("EisUniqueId", "202011102000000100000002")
				.put("CorrYN", "N");
		String jsonStr2 = JSONObjectUtils.toJSONString(obj2.toMap());
		
		JsonObject obj3 = JsonObject.create().put("CompInvoiceId", "55510000028")
				.put("IssueDtm","20201112" )
				.put("EisUniqueId", "202011102000000100000003")
				.put("CorrYN", "N");
		String jsonStr3 = JSONObjectUtils.toJSONString(obj3.toMap());
		
		
		String commaSepJWS = dataEncryption.generateJWS("","",jsonStr1)+","+dataEncryption.generateJWS("","",jsonStr2)+","+dataEncryption.generateJWS("","",jsonStr3);
		String encryptedCommaSepJWS = dataEncryption.encryptByAES(sessionKey, commaSepJWS);
		return encryptedCommaSepJWS;
	}
	
	@Override
	public ResponseEntity<?> authenticate(String accreditationId, String authToken, AuthData authData) {
		Cluster cluster = Cluster.connect(connectionString, username, password);
		//Cluster cluster = cluster.create("couchbase://" + "10.52.0.27");
		ResponseEntity<?> response=null;
		try {
			
			// Get a bucket, scope and collection reference
			Bucket bucket = cluster.bucket(bucketName);
			//Bucket bucket = cluster.openBucket("EIS-mock-bucket","");
			bucket.waitUntilReady(Duration.ofSeconds(60));
			
			Scope scope = bucket.scope(scopeName);
			Collection collection = scope.collection(authCollectionName);
			
			
			String id = accreditationId+"_"+authToken;
			
			collection.upsert(id,authData);
			
			JsonObject resObj = JsonObject.create().put("status", "1");
												   
			response = ResponseEntity.ok(resObj.toMap());

		} catch(Exception e) {
			
			System.out.println("Error Message:::"+e.getMessage());
			response = ResponseEntity.ok("Failure");
			
		}
		
		cluster.close();
		
		return response;
	}
	
	@Override
	public ResponseEntity<?> postInvoice(String accreditationId, String authToken, Object jsonBody) {
		
		Cluster cluster = Cluster.connect(connectionString, username, password);
		
		ResponseEntity<?> response=null;
		
		try {
			
			// Get a bucket, scope and collection reference
			Bucket bucket = cluster.bucket(bucketName);
			bucket.waitUntilReady(Duration.ofSeconds(60));
			
			Scope scope = bucket.scope(scopeName);
			Collection authCollection = scope.collection(authCollectionName);
			Collection invoiceCollection = scope.collection(invoiceCollectionName);
			
			
			//fetching userId and sessionKey from DB
			String id = accreditationId+"_"+authToken;
			GetResult getResult = authCollection.get(id);
			String userId  = getResult.contentAsObject().getString("userId");
			String sessionKey  = getResult.contentAsObject().getString("sessionKey");
			System.out.println("sessionKey=="+sessionKey);
			
			
			//added for testing - to be removed
			String encryptedCommaSepJWS = createEncryptedData( sessionKey);
			
			
			//fetching submitId and data from request
			ObjectMapper objectMapper = new ObjectMapper();
			
			JsonObject jsonData = JsonObject.fromJson(objectMapper.writeValueAsString(jsonBody));
			
			String submitId = jsonData.getString("submitId");
			String invoiceData = encryptedCommaSepJWS;//jsonData.getString("data");
			
			
			String dataJWSCommaSeperated = dataEncryption.decryptByAES(sessionKey, invoiceData);
			
			System.out.println("dataJWSCommaSeperated==="+dataJWSCommaSeperated.toString());


			String[] invoiceDataJWS = dataJWSCommaSeperated.split(",");
			String processStatusCode = "01";
			int totalCountQuantity = invoiceDataJWS.length;
			int successCountQuantity = 0;
			int failCountQuantity = 0;
			ArrayList<JsonObject> processedDocuments = new ArrayList<JsonObject>();
			
			for (int i=0;i<totalCountQuantity;i++) {
					
				String invoiceJWS = invoiceDataJWS[i];
				String invoiceJsonString = dataEncryption.getJWSPayload( invoiceJWS );
				
				JsonObject invoiceJson = JsonObject.fromJson(invoiceJsonString);
					
				JsonObject processedDoc = JsonObject.create().put("invoiceUid",invoiceJson.getString("EisUniqueId"))
							 .put("resultStatusCode","SUC001");
				successCountQuantity++;
				processedDocuments.add(processedDoc);
				
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
			
			//creating json document to be added to DB
			String ackId = "BIR-"+dateFormat.format(new Date())+"-"+UUID.randomUUID().toString().substring(0, 5).toUpperCase();
			JsonObject finalData = JsonObject.create().put("userId",userId)
													  .put("ackId", ackId)
													  .put("processStatusCode",processStatusCode )
													  .put("totalCountQuantity", totalCountQuantity)
													  .put("successCountQuantity", successCountQuantity)
													  .put("failCountQuantity", failCountQuantity)
													  .put("processedDocuments", processedDocuments);
			
			// adding document to DB
			System.out.println("finalData==="+finalData.toString());
			invoiceCollection.upsert(accreditationId+"_"+submitId,finalData);
			
			//Generate Response
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
			
			JsonObject dataObj = JsonObject.create().put("accreditationId",accreditationId)
												 .put("userId", userId)
												 .put("refSubmitId", submitId)
												 .put("ackId", ackId)
												 .put("responseDtm", dateFormat.format(new Date()))
												 .put("description","Transmission has been successfully processed. EIS will process the final registration within the next business day.");
			
			String data = dataEncryption.encryptByAES(sessionKey, objectMapper.writeValueAsString(dataObj));
			
			JsonObject resObj = JsonObject.create().put("status", "1").put("data",data );
			
			response = ResponseEntity.ok(resObj.toMap());

		} catch(Exception e) {
			
			System.out.println("Error Message:::"+e.getMessage());
			response = ResponseEntity.ok("Failure");
			
		}
		
		cluster.close();
		return response;
	}

	
	@Override
	public ResponseEntity<?> getInvoice(String accreditationId, String submitId) {
		
		Cluster cluster = Cluster.connect(connectionString, username, password);
		
		ResponseEntity<?> response=null;
		
		try {
			
			// Get a bucket, scope and collection reference
			Bucket bucket = cluster.bucket(bucketName);
			bucket.waitUntilReady(Duration.ofSeconds(60));
			
			Scope scope = bucket.scope(scopeName);
			Collection collection = scope.collection(invoiceCollectionName);
			
			
			// get data from db
			GetResult getResult = collection.get(accreditationId+"_"+submitId);
			JsonObject resultObject = getResult.contentAsObject();
			
			// Generate Response
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
			JsonObject dataObj = JsonObject.create().put("accreditationId",accreditationId)
					 .put("userId", resultObject.getString("userId"))
					 .put("refSubmitId", submitId)
					 .put("ackId", resultObject.getString("ackId"))
					 .put("responseDtm", dateFormat.format(new Date()))
					 .put("processStatusCode", resultObject.getString("processStatusCode"))
					 .put("totalCountQuantity", resultObject.getInt("totalCountQuantity"))
					 .put("successCountQuantity", resultObject.getInt("successCountQuantity"))
					 .put("failCountQuantity", resultObject.getInt("failCountQuantity"))
					 .put("processedDocuments", resultObject.getArray("processedDocuments"));
				
			response = ResponseEntity.ok(dataObj.toMap());
			  
		} catch(Exception e) {
			
			System.out.println("Error Message:::"+e.getMessage());
			response = ResponseEntity.ok("Failure");
			
		}
		cluster.close();
		return response;
	}
}
