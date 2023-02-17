package com.srijan.eis.mock.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.core.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.srijan.eis.mock.model.AuthData;
import com.srijan.eis.mock.service.EISMockService;

@RestController
@RequestMapping("/eis-mock/v1")
public class EISMockController {

	@Autowired
	EISMockService mockService;
	
	
	@PostMapping(value = "/api/authentication")
	public ResponseEntity<?> authenticate(@RequestHeader("accreditationId") String accreditationId, @RequestHeader("authToken") String authToken, @RequestBody AuthData authData) {
		return mockService.authenticate(accreditationId, authToken,authData);
	} 
	
	@PostMapping(value = "/api/invoices")
	public ResponseEntity<?> invoiceIssuance(@RequestHeader("accreditationId") String accreditationId, @RequestHeader("authToken") String authToken, @RequestBody Object jsonBody) throws JsonProcessingException {
		return mockService.postInvoice(accreditationId,authToken, jsonBody);
		 
	}
	
	@GetMapping(value = "/api/invoice_result/{submitId}")
	public ResponseEntity<?> getInvoice(@RequestHeader("accreditationId") String accreditationId, @PathVariable("submitId") String submitId) {
		return mockService.getInvoice(accreditationId,submitId);
	}
	
}

