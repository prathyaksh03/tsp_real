package com.real.vrp.drone.util;

import com.graphhopper.jsprit.core.util.Coordinate;

public class GeoTimeCalculator {

	/**
	 * Calculate distance between two points in latitude and longitude taking into
	 * account height difference. If you are not interested in height difference
	 * pass 0.0. Uses Haversine method as its base.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters el2
	 * End altitude in meters
	 * 
	 * @returns Distance in Meters
	 */
	public static double distance(Coordinate coord1, Coordinate coord2) {
		double lat1 = coord1.getX();
		double lon1 = coord1.getY();

		double lat2 = coord2.getX();
		double lon2 = coord2.getY();

		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		double height = 0.00;

		distance = Math.pow(distance, 2) + Math.pow(height, 2);

		return Math.sqrt(distance);
	}

	public static long getTime(double distance, double velocity) {
		return (long) (distance / velocity);
	}

	public static String getFormattedTime(long totalSecs) {
		long hours = totalSecs / 3600;
		long minutes = (totalSecs % 3600) / 60;
		long seconds = totalSecs % 60;

		if (hours > 0)
			return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
		return String.format("%02dm %02ds", minutes, seconds);
	}
}
