package com.izedtea.creation;

import android.location.Location;

public class FakeLocation extends Location{

	public FakeLocation() {
		super("network");
        this.setAccuracy(71.0f);
        this.setAltitude(0.0);
        this.setBearing(0.0f);
        this.setLatitude(13.8506577);
        this.setLongitude(100.6055876);
        this.setSpeed(0.0f);
        this.setTime(1338947257876l);
	}
}
