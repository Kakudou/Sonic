package util.api;

import java.util.Map;
import java.util.Map.Entry;

import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;

import com.fasterxml.jackson.databind.JsonNode;

public class InvokeApi
{
  
  public static final long TIME_OUT = 10000;
  
  public static JsonNode invokeGet(String url, Map<String, String[]> queryParams, Map<String, String[]> headerParams)
  {
    WSRequestHolder req = WS.url(url);
    
    if (queryParams != null && !queryParams.isEmpty()) {
      Map<String, String[]> queryParam = queryParams;
      for (Entry<String, String[]> entry : queryParam.entrySet()) {
        req.setQueryParameter(entry.getKey(), entry.getValue()[0]);
      }
    }
    
    if (headerParams != null && !headerParams.isEmpty()) {
      Map<String, String[]> headerParam = headerParams;
      for (Entry<String, String[]> entry : headerParam.entrySet()) {
        req.setHeader(entry.getKey(), entry.getValue()[0]);
      }
    }
    
    JsonNode jsonResponse = req.get().get(TIME_OUT).asJson();
    return jsonResponse;
  }
  
  public static JsonNode invokePost(String url, JsonNode json)
  {
    WSRequestHolder req = WS.url(url);
    JsonNode jsonResponse = req.post(json).get(TIME_OUT).asJson();
    return jsonResponse;
  }
  
  public static JsonNode invokeId(String url, Long id)
  {
    WSRequestHolder req = WS.url(url);
    req.setQueryParameter("id", "eq,"+String.valueOf(id));
    JsonNode jsonResponse = req.get().get(TIME_OUT).asJson();
    return jsonResponse.get("results").get(0);
  }
}
