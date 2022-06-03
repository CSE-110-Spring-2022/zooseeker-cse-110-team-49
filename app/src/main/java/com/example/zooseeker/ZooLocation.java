package com.example.zooseeker;
import java.lang.Math;

public class ZooLocation {
    public static final double LAT_IN_FT = 363843.57;
    public static final double LON_IN_FT = 307515.50;

    public double latitude;
    public double longitude;

    public static double dist(ZooLocation firstLocation, ZooLocation secondLocation) {
        double d_ft_lat = LAT_IN_FT * Math.abs(firstLocation.latitude - secondLocation.latitude);
        double d_ft_lon = LON_IN_FT * Math.abs(firstLocation.longitude - secondLocation.longitude);
        return Math.sqrt(Math.pow(d_ft_lat, 2) + Math.pow(d_ft_lon, 2));
    }

    public double dist(ZooLocation aLocation) {
        double d_ft_lat = LAT_IN_FT * Math.abs(latitude - aLocation.latitude);
        double d_ft_lon = LON_IN_FT * Math.abs(longitude - aLocation.longitude);
        return Math.sqrt(Math.pow(d_ft_lat, 2) + Math.pow(d_ft_lon, 2));
    }

    ZooLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
