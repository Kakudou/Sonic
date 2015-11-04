package sonic.fournisseuridentite.sonic.requester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class IdPivotSonicRequester {

	private String keyId;
	private String keySecret;
	private String name;
	private String scopesSonic;

	public IdPivotSonicRequester(String keyId, String keySecret, String name,
			String scopesSonic) {
		this.keyId = keyId;
		this.keySecret = keySecret;
		this.name = name;
		this.scopesSonic = scopesSonic;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getKeySecret() {
		return keySecret;
	}

	public void setKeySecret(String keySecret) {
		this.keySecret = keySecret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getScopesSonic() {
		List<String> list = new ArrayList<>();
		if (scopesSonic != null){
		 list = Arrays.asList(scopesSonic.split(","));
		}
		return list;
	}

	public void setScopesSonic(List<String> scopesSonic) {
	  
		this.scopesSonic = StringUtils.join(scopesSonic, ",");
		;
	}

}
