
// package com.northeastern.INFO7255.INFO7255AbhinavChoudhary.util;

// import io.jsonwebtoken.ExpiredJwtException;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.MalformedJwtException;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.SignatureException;
// import io.jsonwebtoken.UnsupportedJwtException;
// import java.security.KeyFactory;
// import java.security.NoSuchAlgorithmException;
// import java.security.PrivateKey;
// import java.security.PublicKey;
// import java.security.spec.InvalidKeySpecException;
// import java.security.spec.PKCS8EncodedKeySpec;
// import java.security.spec.X509EncodedKeySpec;
// import java.util.Date;
// import java.util.Map;
// import org.apache.commons.codec.binary.Base64;
// import org.springframework.stereotype.Service;

// @Service
// public class JwtUtils {
//     int accessExpirationMs=9600000;
    
//     public String generateAccessToken(Map<String, Object> claims, String jwtPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
//         return Jwts.builder()
//                 .setClaims(claims)
//                 .setIssuedAt(new Date())
//                 .setExpiration(new Date((new Date()).getTime() + accessExpirationMs))
//                 .signWith(SignatureAlgorithm.RS256, generateJwtKeyEncryption(jwtPrivateKey))
//                 .compact();
//     }
    
//     public PublicKey generateJwtKeyDecryption(String jwtPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
//         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//         byte[] keyBytes = Base64.decodeBase64(jwtPublicKey);
//         X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec(keyBytes);
//         return keyFactory.generatePublic(x509EncodedKeySpec);
//     }

//     public PrivateKey generateJwtKeyEncryption(String jwtPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
//         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//         byte[] keyBytes = Base64.decodeBase64(jwtPrivateKey);
//         PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec(keyBytes);
//         return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
//     }

//     public String validateJwtToken(String authToken,String jwtPublicKey) {
//         try {
//             if(authToken == "" || jwtPublicKey == "") return "Authorization Key Issue"; 
//             Jwts.parser().setSigningKey(generateJwtKeyDecryption(jwtPublicKey)).parseClaimsJws(authToken);
//             return "tokenValid";
//         } catch (SignatureException e) {
//             return("Invalid JWT signature: "+ e.getMessage());
//         } catch (MalformedJwtException e) {
//             return("Invalid JWT token: "+ e.getMessage());
//         } catch (ExpiredJwtException e) {
//             return("JWT token is expired: "+ e.getMessage());
//         } catch (UnsupportedJwtException e) {
//             return("JWT token is unsupported: "+ e.getMessage());
//         } catch (IllegalArgumentException e) {
//             return("JWT claims string is empty: "+ e.getMessage());
//         } catch (NoSuchAlgorithmException e) {
//             return("no such algorithm exception");
//         } catch (InvalidKeySpecException e) {
//             return("invalid key exception");
//         }
//     }
// }
