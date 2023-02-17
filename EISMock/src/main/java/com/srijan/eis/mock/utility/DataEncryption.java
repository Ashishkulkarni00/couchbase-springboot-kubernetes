package com.srijan.eis.mock.utility;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import java.util.Base64;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.util.Base64URL;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

@Component
public class DataEncryption {
		
	
	//JWS
	public String generateJWS(String keyId, String privateKey, String data) throws Exception {
		JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(keyId).build();
		Payload payload = new Payload(data);
		JWSObject jwsObject = new JWSObject(jwsHeader, payload);
		
		//byte[] decodePrivateKey = Base64.getDecoder().decode(privateKey);
		
		//PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodePrivateKey);
		
		//RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);	
		
		RSAKey key1 = new RSAKey(new Base64URL("mzUZQ8_p7Tnw9NQBgYNc9JD2eTrDU4yENswYZSL9q4cHVKxgzZt61FLAjOHhoxTgT4B71OTOcMINRmfDCq69L0VvDMnCxxII7bJxwdDSqz7B-M2ys2XaiwSj8n6cQf5UDhFRPbnuME3JiZ0zCDwOi-OwK5Ium5p4oykIV9fKN9VLYAvXRBEYcStaeWVmoPH3aXp2L8hvo9WSuVOI4otDXhShkmDdSy00qWJk_9O-mgO5MiHGzpFat2vaPYdLTs_9IyeAJJe7ppSv0-TfBuySnfGadRrQ9nMK2m7rvqDAvM9QgYElkjr5dY5HG8WDuWx-xWMePMTGcN5Fx0sIy8YeCw") , new Base64URL("AQAB") , new Base64URL("WP3MG7a18UBcqXR53JG-lbgcqowH_Pq_wE-r2SmD-3qzuFgSNTHl7qH4J49IeeJ5bvxDhGEhAUQyqmDKU3C3psgQEWwKE2yC1A-R7EhY0rlsLMV8piJLeZRSkLMJ00kJVvuU8miOawHfAdmxrhIJYSpPE9yAftXGP_9B-mQPXY05RwJQ0QqSnHA8m9ECRcur0iiim2gGrRflJJxT1497dTW0YxLGbXUqfl3cYrcAEMS7VaGFs9NUF3UFF1l7ZS0_SZk0ILDU-LvBhW4X1UJesSw7ynQ9xeXG0HJNVBEZPKjYN31rvvymBnLbbXsH0UySeM9LCQR0lvIgsnYAYBLz2Q") , new Base64URL("yzZCrv6W7MghwVP175zhzNrsjvVlg1PT4FdN5F33nNV-vqzHH-e_5_4UrDGpKtlQBYqciwCPK_oHu_m5d_TUJ4zmE6BvkMuVGS7nCm7HMEFVemRnKFzqi6pPXjIoB2e3y7bx02GR-0f4O5PUNt4KJC9YyWTGj6WPnLgDSMI48HU") , new Base64URL("w4aAlUIYce7ZGTQqwik6CGK36ypfT9MSZKBZGElWm9p1it8s0nVal5rgNbR-Vy0NWx1FkR4Vo-DiZq5dL79TIribygOfVIuSPIZutvhr4dR_9_0GzIkhntvNTFXS_WgEVcZRaaL0KDYtfeEohZH9EkKWLuGhLssZFoIW7n4RBH8") , new Base64URL("NCqhw7qnk_FseOzM3c4wSR6KS8jDXotOgPDolg9pWWGVP-2q0I31veCD_hBhlRZkbIfA2A40st1kzuS0sA6xx9Vr2u38tfcNN4HK3erCR6j-AIV11e7EZ0Y5Sb0meYAutqUoP1N03kUDfIfempc1k0R0Tn8IWifuThUPAsaZR2E") , new Base64URL("Xx4tq27OCY8SWiqAqpf3vWCY7HRejay3A_DlpuT2lzc9e2N6oJ-qhJo5fKbj13D5UFv7Hc-u41xhh2ZHuxSuUvPQBgShHDYR3BPiBGzxvpjr8C5Ngm8rxeZUB1CxJcnWYgvqSyOU94LzuoswdVpB7QIroX7uNJ9lAwgiM79sc-0") , new Base64URL("SlzFv7mSQ2cYfMXfkro9dVmb0z3GYhglsELFR07YIUIjE1uhiubyP_380YQrUclxPJs8BjjCPZRYmViuDq9zDL49clSa8g0ADKuTPVENkJR06ScPZ7eoFSzQXmSek8yBUkWAFuXbfAtbERgkcMSVC-cwbguw8Jn1QpYVD-BrUs0") , null, null, null, null, JWSAlgorithm.RS256, "123", null, null, null, null, null);
		
		JWSSigner signer = new RSASSASigner(key1);
		jwsObject.sign(signer);
		return jwsObject.serialize();
	}
	
	public boolean verifyJWS(String key, String jswString) throws Exception {
		
		RSAKey keyy = new RSAKey.Builder(new Base64URL("mzUZQ8_p7Tnw9NQBgYNc9JD2eTrDU4yENswYZSL9q4cHVKxgzZt61FLAjOHhoxTgT4B71OTOcMINRmfDCq69L0VvDMnCxxII7bJxwdDSqz7B-M2ys2XaiwSj8n6cQf5UDhFRPbnuME3JiZ0zCDwOi-OwK5Ium5p4oykIV9fKN9VLYAvXRBEYcStaeWVmoPH3aXp2L8hvo9WSuVOI4otDXhShkmDdSy00qWJk_9O-mgO5MiHGzpFat2vaPYdLTs_9IyeAJJe7ppSv0-TfBuySnfGadRrQ9nMK2m7rvqDAvM9QgYElkjr5dY5HG8WDuWx-xWMePMTGcN5Fx0sIy8YeCw"), new Base64URL("AQAB")).keyID("123").algorithm(JWSAlgorithm.RS256).build();
		
		RSAKey rsaPublicKey = keyy.toPublicJWK();
		JWSObject jwsObject = JWSObject.parse(jswString);
		
		JWSVerifier verifier = new RSASSAVerifier(rsaPublicKey);
		return jwsObject.verify(verifier);
		
	}
	public  String getJWSPayload( String jswString) throws Exception {
		
		JWSObject jwsObject = JWSObject.parse(jswString);
		String data = jwsObject.getPayload().toString();
		return data;
	}
	
	
	// AES
    public String decryptByAES(String key, String json)throws Exception {
        byte[] secretKeyByte = key.getBytes();
        SecretKey secretKey = new SecretKeySpec(secretKeyByte, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(key.substring(0,16).getBytes("UTF-8")));
        byte[] byteStr = Base64.getDecoder().decode(json.getBytes());
        return new String(cipher.doFinal(byteStr), "UTF-8");
    }

    public String encryptByAES(String key, String json) throws Exception {
    	
        byte[] rawSecretKey = key.getBytes();
        SecretKey secretKey = new SecretKeySpec(rawSecretKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(key.substring(0, 16).getBytes()));
        byte[] encrypted = cipher.doFinal(json.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }
}
