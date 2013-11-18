package com.example.goeuro.model;
import android.location.Location;

public class UserLocation {
	private static Location location;
	

	

	public static Location getLocation() {
		return location;
	}

	public static void setLocation(Location _location) {
		location = _location;
	}
}
