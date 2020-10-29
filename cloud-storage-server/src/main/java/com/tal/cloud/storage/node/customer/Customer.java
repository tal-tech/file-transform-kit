package com.tal.cloud.storage.node.customer;

import java.util.ArrayList;
import java.util.List;

public class Customer {

	private String clientId = "";
	private String clientName = "";
	private List<String> domains = new ArrayList<String>();

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public List<String> getDomains() {
		return domains;
	}

	public void setDomains(List<String> domains) {
		this.domains = domains;
	}

	public void addDomains(String domain) {
		this.domains.add(domain);
	}

}
