package it.negro.contab.entity;

import org.springframework.data.annotation.Id;

public class Counter {
	@Id
	private String id;
	private int val;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getVal() {
		return val;
	}
	public void setVal(int val) {
		this.val = val;
	}
	
}
