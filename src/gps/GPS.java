package gps;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import eu.attech.gpstracker.R;
import eu.attech.gpstracker.R.id;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Okermüller Patrick
 * @web www.attech.eu
 * @email info@attech.eu
 * 
 */

public class GPS implements LocationListener {

	// Create the Local Variables
	public LocationManager lm;
	private Activity activity;

	private double speed = -1;
	private double latitude;
	private double longitude;

	private double lastLatitude;
	private double lastLongitude;
	private double km;
	private int numberofFixes = 0;

	private ArrayList<Double> lat;
	private ArrayList<Double> lon;

	/**
	 * Default Constructor
	 * 
	 * @param lm
	 *            : LocationManager from the Main Class
	 * @param activity
	 *            : Activity from the Main Class
	 *            
	 * Create new Objects for the ArrayList
	 * */
	public GPS(LocationManager lm, Activity activity) {
		this.lm = lm;
		this.activity = activity;
		lat = new ArrayList<Double>();
		lon = new ArrayList<Double>();
		checkIfGPSEnabled();
	}

	/* Start Searching for GPS */
	public void startSearchGPS() {
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 0.5f, this);
	}

	/* Check if GPS is enabled, if not -> display the Settings Screen to Enable it */
	public void checkIfGPSEnabled() {
		boolean enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			startGPSSettings();
		} else {
			startSearchGPS();
		}
	}

	/* Display the GPS Settings Screen, if GPS was not enabled */
	public void startGPSSettings() {
		Toast.makeText(activity, "GPS ist nicht aktiviert", Toast.LENGTH_LONG)
				.show(); // Create a Toast and display it
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		activity.startActivity(intent);

		startSearchGPS();
	}

	/*
	 * Is called if the location of the devices has changed get the longitude,
	 * latitude, speed
	 */
	@Override
	public void onLocationChanged(Location location) {
		longitude = location.getLongitude();
		latitude = location.getLatitude();
		speed = location.getSpeed();

		calculateKM(); // Calculate the Distance
		
		updateValues(); // Update the TextView's

		lat.add(latitude);
		lon.add(longitude);

		lastLatitude = latitude;
		lastLongitude = longitude;
		numberofFixes++;
	}

	/* Calculate the Distance between two Geo Points */
	public void calculateKM() {
		Location current = new Location("");
		current.setLatitude(latitude);
		current.setLongitude(longitude);

		Location last = new Location("");
		last.setLatitude(lastLatitude);
		last.setLongitude(lastLongitude);

		double m = current.distanceTo(last) / 1000;

		if (numberofFixes > 0) {
			km += roundDecimal(m, 2);
		} else {
			km = 0;
		}

	}

	// Round Method for Double
	private double roundDecimal(double value, final int decimalPlace) {
		BigDecimal bd = new BigDecimal(value);

		bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
		value = bd.doubleValue();

		return value;
	}

	// Updates the Values for the TextViews
	public void updateValues() {
		TextView speed = (TextView) activity.findViewById(R.id.textView1);
		TextView latitude = (TextView) activity.findViewById(R.id.textView2);
		TextView longitude = (TextView) activity.findViewById(R.id.textView3);
		TextView distance = (TextView) activity.findViewById(R.id.textView4);
		
		if (getSpeed() < 0) {
		} else {
			speed.setText("Speed: \n" + getSpeed() + " km/h");
			latitude.setText("Latitude: \n" + getLatitude());
			longitude.setText("Longitude: \n" + getLongitude());
			distance.setText("Distance: \n" + getKm() + " km");
		}

	}

	/* **************************************
	 * Getter and Setters for the Variables *
	 * ************************************ */

	public double getLastLatitude() {
		return lastLatitude;
	}

	public void setLastLatitude(double lastLatitude) {
		this.lastLatitude = lastLatitude;
	}

	public double getLastLongitude() {
		return lastLongitude;
	}

	public void setLastLongitude(double lastLongitude) {
		this.lastLongitude = lastLongitude;
	}

	public double getKm() {
		return km;
	}

	public void setKm(double km) {
		this.km = km;
	}

	public int getNumberofFixes() {
		return numberofFixes;
	}

	public void setNumberofFixes(int numberofFixes) {
		this.numberofFixes = numberofFixes;
	}

	public ArrayList<Double> getLat() {
		return lat;
	}

	public void setLat(ArrayList<Double> lat) {
		this.lat = lat;
	}

	public ArrayList<Double> getLon() {
		return lon;
	}

	public void setLon(ArrayList<Double> lon) {
		this.lon = lon;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getSpeed() {
		return roundDecimal(speed * 3.6, 2);
	}

	public LocationManager getLocationManager() {
		return lm;
	}

	/* *************************
	 *     Unused Methods      *
	 * *********************** */

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
