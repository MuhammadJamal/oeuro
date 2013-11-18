package com.example.goeuro.model;

public class Position implements Comparable<Position> {
	private String id;
	private String name;
	private Float distance;

	public Position(String id, String name, float dis) {
		this.id = id;
		this.name = name;
		distance = dis;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(Position another) {
		return distance.compareTo(another.distance);
	}
}
