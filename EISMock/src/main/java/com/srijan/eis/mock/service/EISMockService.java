package com.srijan.eis.mock.service;

import org.springframework.http.ResponseEntity;

import com.srijan.eis.mock.model.AuthData;

public interface EISMockService {


	ResponseEntity<?> authenticate(String accreditationId, String authToken, AuthData authData);

	ResponseEntity<?> postInvoice(String accreditationId,  String authToken, Object jsonBody);

	ResponseEntity<?> getInvoice(String accreditationId, String submitId);

}
