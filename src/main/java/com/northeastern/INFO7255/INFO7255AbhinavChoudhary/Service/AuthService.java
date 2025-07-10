package com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Service;

import java.util.Collections;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@SuppressWarnings("deprecation")
@Service
public class AuthService {

    private static final com.google.api.client.json.jackson2.JacksonFactory jf = new JacksonFactory();

    private String google_client_ID = "client-id.apps.googleusercontent.com";

    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), jf)
                                            .setAudience(Collections.singletonList(google_client_ID)).build();

    public  boolean verify(String id_token){
        try{
            GoogleIdToken idToken = verifier.verify(id_token);
            if (null != idToken ) {
                System.out.println("Authorize successful");
                return true; } 
            else {
                System.out.println("Failed to authorize");
                return false;
            }
        }catch (Exception e){
            System.out.println("Invalid token"+e);        
            return false;
        }
    }
    
}
