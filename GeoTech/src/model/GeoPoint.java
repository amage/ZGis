package model;

public class GeoPoint {
	private double _lat;
	private double _lon;
	public GeoPoint(double lon, double lat) {
		this._lon = lon;
		this._lat = lat;
	}
	public double getLat() { return _lat; }
	public void setLat(double lat) { this._lat = lat; }
	public double getLon() { return _lon; }
	public void setLon(double lon) { this._lon = lon; }
}
