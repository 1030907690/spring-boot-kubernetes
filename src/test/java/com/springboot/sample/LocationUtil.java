package com.springboot.sample;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LocationUtil {
    private static final double EARTH_RADIUS = 6378.137D;

    public LocationUtil() {
    }

    private static double rad(double d) {
        return d * 3.141592653589793D / 180.0D;
    }

    public static double getMeter(double long1, double lat1, double long2, double lat2) {
        lat1 = rad(lat1);
        lat2 = rad(lat2);
        double a = lat1 - lat2;
        double b = rad(long1 - long2);
        double sa2 = Math.sin(a / 2.0D);
        double sb2 = Math.sin(b / 2.0D);
        double d = 12756.274D * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        d *= 1000.0D;
        BigDecimal bg = (new BigDecimal(d)).setScale(2, RoundingMode.UP);
        return bg.doubleValue();
    }



    public static boolean checkArea(double lng, double lat, String[] lnglatArray) throws Exception {
        boolean result = false;
        double lastLat = 0.0D;
        double lastLng = 0.0D;
        double firstLat = 0.0D;
        double firstLng = 0.0D;
        double angle = 0.0D;

        for(int j = 0; j < lnglatArray.length; ++j) {
            String[] lnglat = lnglatArray[j].split(",");
            if (lnglat.length > 1) {
                double nodeLng = (double)Float.parseFloat(lnglat[0]);
                double nodeLat = (double)Float.parseFloat(lnglat[1]);
                double x1;
                double y1;
                double x2;
                double y2;
                if (j == 0) {
                    firstLat = nodeLat;
                    firstLng = nodeLng;
                } else {
                    x1 = lastLng - lng;
                    y1 = lastLat - lat;
                    x2 = nodeLng - lng;
                    y2 = nodeLat - lat;
                    angle += angle2D(x1, y1, x2, y2);
                }

                if (j == lnglatArray.length - 1) {
                    x1 = nodeLng - lng;
                    y1 = nodeLat - lat;
                    x2 = firstLng - lng;
                    y2 = firstLat - lat;
                    angle += angle2D(x1, y1, x2, y2);
                }

                lastLat = nodeLat;
                lastLng = nodeLng;
            }
        }

        if (Math.abs(angle) >= 3.141592653589793D) {
            result = true;
        }

        return result;
    }

    public static boolean checkCircle(double pointLat, double pointLng, double centerLat, double cneterLng, double targetDistance) {
        double radLat1 = rad(centerLat);
        double radLat2 = rad(pointLat);
        double a = radLat1 - radLat2;
        double b = rad(cneterLng) - rad(pointLng);
        double s = 2.0D * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0D), 2.0D) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2.0D), 2.0D)));
        s = s * 6378.137D * 1000.0D;
        s = (double)(Math.round(s * 10000.0D) / 10000L);
        return targetDistance >= s;
    }

    private static double angle2D(double x1, double y1, double x2, double y2) {
        double theta1 = Math.atan2(y1, x1);
        double theta2 = Math.atan2(y2, x2);
        double dtheta = theta2 - theta1;
        if (dtheta > 3.141592653589793D) {
            dtheta -= 6.283185307179586D;
        }

        if (dtheta < -3.141592653589793D) {
            dtheta += 6.283185307179586D;
        }

        return dtheta;
    }


}