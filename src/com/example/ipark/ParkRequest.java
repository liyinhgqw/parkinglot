package com.example.ipark;

public class ParkRequest {
	public String position;
	public double distance;
	
	public ParkRequest(String pos, double dist) {
		this.position = pos;
		this.distance = dist;
	}
}
