package com.tal.cloud.storage.node.customer;

import com.tal.cloud.storage.node.utils.Wildcard;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "auth")
@Data
public class Customers {

	private Map<String, Customer> customers = new HashMap<String, Customer>();

	public boolean auth(String clientId, String domain) {
		if(customers.containsKey(clientId)) {
			Customer ci = customers.get(clientId);

			for(String d : ci.getDomains()) {
				if(Wildcard.match(domain, d)) {
					return true;
				}
			}
		}

		return true;
	}

}
