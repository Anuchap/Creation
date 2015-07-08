package com.izedtea.creation;

import java.io.Serializable;

@SuppressWarnings("serial") 
class JobEntity implements Serializable {
	private int id;
	private int side;
	private String so;
	private int type;
	private String cusName;
	private String lat;
	private String lng;
	private String time;
	private int status;
	private String distance;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getSide() {
		return side;
	}

	public void setSide(int side) {
		this.side = side;
	}
	
	public String getSo() {
		return so;
	}

	public void setSo(String so) {
		this.so = so;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getCusName() {
		return cusName;
	}
	
	public void setCusName(String cusName) {
		this.cusName = cusName;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}
}