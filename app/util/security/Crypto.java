package util.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import play.libs.Json;
import sonic.oidc.idtoken.model.IdToken;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Crypto
{
  
  public static String toSha256(String value)
  {
    StringBuffer hexString = null;
    try {
      byte[] hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes("UTF-8"));
      hexString = new StringBuffer();
      for (byte b : hash) {
        hexString.append(Integer.toHexString(0xFF & b));
      }
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e1) {
    }
    
    return hexString.toString();
  }
  
  public static String sign(IdToken idToken, String keySecret)
  {
    byte[] secret = keySecret.getBytes();
    
    String algo = ("HmacSHA256");
    
    ObjectNode header = JsonNodeFactory.instance.objectNode();
    header.put("typ", "JWT");
    header.put("alg", algo);
    String encodedHeader = "";
    try {
      encodedHeader = new String(Base64.encodeBase64URLSafe(header.toString().getBytes("UTF-8")));
    } catch (UnsupportedEncodingException e) {
    }
    
    HashMap<String, String> maps = new HashMap<>();
    maps.put("AccessToken", idToken.getAccessToken());
    maps.put("Email", idToken.getEmail());
    maps.put("Identifiant", idToken.getSub());
    maps.put("Iss", idToken.getIss());
    maps.put("Nom", idToken.getNom());
    maps.put("Prenom", idToken.getPrenom());
    maps.putAll(idToken.getScopeData());
    
    String payload = Json.stringify(Json.toJson(maps));
    String encodedPayload = "";
    try {
      encodedPayload = new String(Base64.encodeBase64URLSafe(payload.toString().getBytes("UTF-8")));
    } catch (UnsupportedEncodingException e) {
    }
    
    String assemble = encodedHeader + "." + encodedPayload;
    String encodedSignature = "";
    try {
      Mac mac = Mac.getInstance(algo);
      mac.init(new SecretKeySpec(secret, algo));
      byte[] signature = mac.doFinal(assemble.getBytes());
      encodedSignature = new String(Base64.encodeBase64URLSafe(signature));
    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
    }
    
    String finalJwt = assemble + "." + encodedSignature;
    
    return finalJwt;
  }
  
  public static boolean verify(String secret, String jwt)
  {
    
    String[] splitJwt = jwt.split("\\.");
    String encodedHeader = splitJwt[0];
    String encodedPayload = splitJwt[1];
    
    String encodedSignatureJwt = splitJwt[2];
    
    String assemble = encodedHeader + "." + encodedPayload;
    String encodedSignature = "";
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
      byte[] signature = mac.doFinal(assemble.getBytes());
      encodedSignature = new String(Base64.encodeBase64URLSafe(signature));
    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
    }
    
    return encodedSignature.equals(encodedSignatureJwt);
  }
  
  public static JsonNode decode(String base64string)
  {
    Base64 decoder = new Base64(true);
    String decoded = "";
    try {
      decoded = new String(decoder.decode(base64string), "UTF-8");
    } catch (UnsupportedEncodingException e) {
    }
    return Json.parse(decoded);
  }
  
}
