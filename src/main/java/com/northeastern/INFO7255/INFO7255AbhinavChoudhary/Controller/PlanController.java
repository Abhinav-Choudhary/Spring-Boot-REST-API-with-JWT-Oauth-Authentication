package com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Service.IndexingMessage;
import com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Service.IndexingService;
import com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Service.PlanService;
import com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Validator.JSONValidator;
import com.northeastern.INFO7255.INFO7255AbhinavChoudhary.configuration.MessagingConfig;
import com.northeastern.INFO7255.INFO7255AbhinavChoudhary.util.JwtUtils;
// import java.security.KeyPair;
// import java.security.KeyPairGenerator;
// import java.security.NoSuchAlgorithmException;
// import java.security.spec.InvalidKeySpecException;
// import java.util.logging.Level;
// import java.util.logging.Logger;
import javax.validation.Valid;
// import org.apache.commons.codec.binary.Base64;

import com.northeastern.INFO7255.INFO7255AbhinavChoudhary.Service.AuthService;

@RestController
@RequestMapping("/v1")
public class PlanController {
    @Autowired
    JSONValidator jsonValidator;

    @Autowired
    PlanService planService;
    
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IndexingService indexingService;

    @Autowired
    RabbitTemplate template;

    @Autowired
    AuthService auth;

    Map<String, Object> map = new HashMap<String, Object>();
    String publicKey = "";
    String privateKey = "";
    
    //  @GetMapping("/token")
    //  public ResponseEntity<Object> generateToken() {
    //      Map<String, Object> claims = new HashMap<>();
    //     try {
    //         KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    //         kpg.initialize(2048);
    //         KeyPair kp = kpg.generateKeyPair();
            
    //         publicKey = Base64.encodeBase64String(kp.getPublic().getEncoded());
    //         privateKey = Base64.encodeBase64String(kp.getPrivate().getEncoded());
            
    //         String jwtToken = jwtUtils.generateAccessToken(claims, privateKey);
    //         return ResponseEntity.status(HttpStatus.CREATED).body(new JSONObject().put("JWTToken", jwtToken).toString());
    //     } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
    //         Logger.getLogger(PlanController.class.getName()).log(Level.SEVERE, null, ex);
    //     }
        
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JSONObject().put("JWTToken", "Error creating JWT Token").toString());
    //  }

    //  public String validateToken(@RequestHeader HttpHeaders headers) {
    //     String jwtToken = (headers.getFirst("Authorization") != null) ? headers.getFirst("Authorization").split(" ")[1] : "";
    //     return jwtUtils.validateJwtToken(jwtToken,publicKey);
    //  }

    @PostMapping(path = "/plan/", produces = "application/json")
    public ResponseEntity<Object> createPlan(@RequestBody(required = false) String medicalPlan, @RequestHeader HttpHeaders headers) throws JSONException, Exception {
        
        // String validateToken = validateToken(headers);
        // if(!validateToken.equals("tokenValid")) {
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JSONObject().put("Authentication Error", validateToken).toString());
        // }

        String authToken = (headers.getFirst("Authorization") != null) ? headers.getFirst("Authorization").split(" ")[1] : "";
        System.out.println("Token:"+authToken);
        if(!auth.verify(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JSONObject().put("Authentication Error", authToken).toString());
        }
        
        map.clear();

        if(medicalPlan == null || medicalPlan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JSONObject().put("Error", " The request body is empty. Please provide a valid JSON payload in the request body.").toString());
        }

        JSONObject json = new JSONObject(medicalPlan);
        try {
            jsonValidator.validateJson(json);
        } catch(ValidationException ve){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JSONObject().put("Error",ve.getErrorMessage()).toString());

        }

        String key = json.get("objectType").toString() + ":" + json.get("objectId").toString() + ":";
        if(planService.checkIfKeyExists(key)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new JSONObject().put("Message", "Plan already exist.").toString());
        }

        String newEtag = planService.savePlanToRedis(json, key);

        indexingService.receiveMessage(new IndexingMessage("CREATE", new JSONObject(medicalPlan).toString()));

        template.convertAndSend(MessagingConfig.MESSAGE_EXCHANGE_NAME, MessagingConfig.ROUTING_KEY, new IndexingMessage("CREATE", new JSONObject(medicalPlan).toString()));

        return ResponseEntity.ok().eTag(newEtag).body(" {\"message\": \"Created data with key: " + json.get("objectId") + "\" }");
    }

    @GetMapping(path = "/{type}/{objectId}", produces = "application/json")
    public ResponseEntity<Object> getPlan(@RequestHeader HttpHeaders headers, @PathVariable String objectId,@PathVariable String type) throws JSONException, Exception {

        // String validateToken = validateToken(headers);
        // if(!validateToken.equals("tokenValid")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        // .body(new JSONObject().put("Authentication Error", validateToken).toString());

        String authToken = (headers.getFirst("Authorization") != null) ? headers.getFirst("Authorization").split(" ")[1] : "";
        if(!auth.verify(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JSONObject().put("Authentication Error", authToken).toString());
        }
        
        if (!planService.checkIfKeyExists(type + ":" + objectId + ":")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JSONObject().put("Message", "ObjectId does not exist").toString());
        }

        String actualEtag = null;
        if (type.equals("plan")) {
            actualEtag = planService.getEtag(type + ":" + objectId + ":", "eTag");
            String eTag = headers.getFirst("If-None-Match");
            if (eTag != null && eTag.equals(actualEtag)) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(actualEtag).body(new JSONObject().put("Message", "Not Modified").toString());
            }
        }

        String key = type + ":" + objectId + ":";
        Map<String, Object> plan = planService.getPlan(key);

        if (type.equals("plan")) {
            return ResponseEntity.ok().eTag(actualEtag).body(new JSONObject(plan).toString());
        }

        return ResponseEntity.ok().body(new JSONObject(plan).toString());
    }

    @DeleteMapping(path = "/plan/{objectId}", produces = "application/json")
    public ResponseEntity<Object> getPlan(@RequestHeader HttpHeaders headers, @PathVariable String objectId){

        // String validateToken = validateToken(headers);
        // if(!validateToken.equals("tokenValid")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        // .body(new JSONObject().put("Authentication Error", validateToken).toString());

        String authToken = (headers.getFirst("Authorization") != null) ? headers.getFirst("Authorization").split(" ")[1] : "";
        if(!auth.verify(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JSONObject().put("Authentication Error", authToken).toString());
        }
        
        if (!planService.checkIfKeyExists("plan"+ ":" + objectId + ":")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JSONObject().put("Message", "ObjectId does not exist").toString());
        }
        String key = "plan:" + objectId + ":";
        String actualEtag = planService.getEtag(key, "eTag");
        String eTag = headers.getFirst("If-Match");
        if (eTag != null && !eTag.equals(actualEtag)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).eTag(actualEtag).body(new JSONObject().put("Message", "Precondition Failed").toString());
        }
        if(eTag == null) return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).eTag(actualEtag).body(new JSONObject().put("Message", "Precondition Failed").toString());

        Map<String, Object> plan = planService.getPlan(key);
        template.convertAndSend(MessagingConfig.MESSAGE_EXCHANGE_NAME, MessagingConfig.ROUTING_KEY, new IndexingMessage("DELETE", new JSONObject(plan).toString()));
        
        planService.deletePlan("plan" + ":" + objectId + ":");

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new JSONObject().put("Message", "Deleted Data").toString());


    }

    @PutMapping(path = "/plan/{objectId}", produces = "application/json")
    public ResponseEntity<Object> updatePlan(@RequestHeader HttpHeaders headers, @Valid @RequestBody String medicalPlan,
                                             @PathVariable String objectId) throws IOException {

        // String validateToken = validateToken(headers);
        // if(!validateToken.equals("tokenValid")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        // .body(new JSONObject().put("Authentication Error", validateToken).toString());

        String authToken = (headers.getFirst("Authorization") != null) ? headers.getFirst("Authorization").split(" ")[1] : "";
        if(!auth.verify(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JSONObject().put("Authentication Error", authToken).toString());
        }

        JSONObject planObject = new JSONObject(medicalPlan);
        try {
            jsonValidator.validateJson(planObject);
        } catch (ValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new JSONObject().put("Validation Error", ex.getMessage()).toString());
        }

        String key = "plan:" + objectId + ":";
        if (!planService.checkIfKeyExists(key)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JSONObject().put("Message", "ObjectId does not exist").toString());
        }

        // Get eTag value
        String actualEtag = planService.getEtag(key, "eTag");
        String eTag = headers.getFirst("If-Match");
        if (eTag != null && !eTag.equals(actualEtag)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).eTag(actualEtag).body(new JSONObject().put("Message", "Precondition Failed").toString());
        }

        Map<String, Object> oldPlan = planService.getPlan(key);

        template.convertAndSend(MessagingConfig.MESSAGE_EXCHANGE_NAME, MessagingConfig.ROUTING_KEY, new IndexingMessage("DELETE", new JSONObject(oldPlan).toString()));
        
        planService.deletePlan(key);

        String newEtag = planService.savePlanToRedis(planObject, key);

        template.convertAndSend(MessagingConfig.MESSAGE_EXCHANGE_NAME, MessagingConfig.ROUTING_KEY, new IndexingMessage("CREATE", new JSONObject(medicalPlan).toString()));

        return ResponseEntity.ok().eTag(newEtag).body(" {\"message\": \"Created data with key: " + planObject.get("objectId") + "\" }");

        // return ResponseEntity.ok().eTag(newEtag)
        //         .body(new JSONObject().put("Message: ", "Resource updated successfully").toString());
    }

    @PatchMapping(path = "/plan/{objectId}", produces = "application/json")
    public ResponseEntity<Object> patchPlan(@RequestHeader HttpHeaders headers, @Valid @RequestBody String medicalPlan,
                                            @PathVariable String objectId) throws IOException {

        // String validateToken = validateToken(headers);
        // if(!validateToken.equals("tokenValid")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        // .body(new JSONObject().put("Authentication Error", validateToken).toString());

        String authToken = (headers.getFirst("Authorization") != null) ? headers.getFirst("Authorization").split(" ")[1] : "";
        if(!auth.verify(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JSONObject().put("Authentication Error", authToken).toString());
        }

        JSONObject planObject = new JSONObject(medicalPlan);

        String key = "plan:" + objectId + ":";
        if (!planService.checkIfKeyExists(key)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JSONObject().put("Message", "ObjectId does not exist").toString());
        }

        try {
            jsonValidator.validateJson(planObject);
        } catch(ValidationException ve){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JSONObject().put("Error",ve.getErrorMessage()).toString());

        }

        String actualEtag = planService.getEtag(key, "eTag");
        String eTag = headers.getFirst("If-Match");
        if (eTag != null && !eTag.equals(actualEtag)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).eTag(actualEtag).body(new JSONObject().put("Message", "Precondition Failed").toString());
        }
        if(eTag == null) return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).eTag(actualEtag).body(new JSONObject().put("Message", "Precondition Failed").toString());

        template.convertAndSend(MessagingConfig.MESSAGE_EXCHANGE_NAME, MessagingConfig.ROUTING_KEY, new IndexingMessage("CREATE", new JSONObject(medicalPlan).toString()));
        
        String newEtag = planService.savePlanToRedis(planObject, key);

        return ResponseEntity.ok().eTag(newEtag)
                .body(new JSONObject().put("Message: ", "Resource updated successfully").toString());
    }
}
