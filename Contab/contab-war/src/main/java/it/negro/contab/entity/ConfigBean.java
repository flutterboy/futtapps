package it.negro.contab.entity;

import org.springframework.data.annotation.Id;

public class ConfigBean {
	
	@Id
	private String id;
	private String label;
	private ConfigBeanValue[] value;
	
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setValue(ConfigBeanValue[] value) {
		this.value = value;
	}
	public ConfigBeanValue[] getValue() {
		return value;
	}
	
}
