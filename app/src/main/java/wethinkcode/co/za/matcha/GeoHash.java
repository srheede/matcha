package wethinkcode.co.za.matcha;

public class GeoHash {

    /**
     * Powers of 2 from 32 down to 1.
     */
    private static final int[] BITS = new int[] { 16, 8, 4, 2, 1 };

    /**
     * The characters used in base 32 representations.
     */
    private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";

    /**
     * Private constructor.
     */
    private GeoHash() {
        // prevent instantiation
    }

    /**  ENCODE HASH */


    /**
     * Converts an angle in degrees to range -180< x <= 180.
     *
     * @param d angle in degrees
     * @return converted angle in degrees
     */
    private static double to180(double d) {
        if (d < 0)
            return -to180(Math.abs(d));
        else {
            if (d > 180) {
                long n = Math.round(Math.floor((d + 180) / 360.0));
                return d - n * 360;
            } else
                return d;
        }
    }

    /**
     * Returns a geohash of given length for the given WGS84 point.
     *
     * @param p
     *            point
     * @param length
     *            length of hash
     * @return hash at point of given length
     */
    public static String encodeHash(LatLong p, int length) {
        return encodeHash(p.getLat(), p.getLon(), length);
    }

    /**
     * Returns a geohash of given length for the given WGS84 point
     * (latitude,longitude). If latitude is not between -90 and 90 throws an
     * {@link IllegalArgumentException}.
     *
     * @param latitude
     *            in decimal degrees (WGS84)
     * @param longitude
     *            in decimal degrees (WGS84)
     * @param length
     *            length of desired hash
     * @return geohash of given length for the given point
     */

    public static String encodeHash(double latitude, double longitude, int length) {
        longitude = to180(longitude);

        return fromLongToString(encodeHashToLong(latitude, longitude, length));
    }

    /**
     * Takes a hash represented as a long and returns it as a string.
     *
     * @param hash
     *            the hash, with the length encoded in the 4 lea
     *            st significant
     *            bits
     * @return the string encoded geohash
     */
    static String fromLongToString(long hash) {
        int length = (int) (hash & 0xf);
        if (length > 12 || length < 1)
            throw new IllegalArgumentException("invalid long geohash " + hash);
        char[] geohash = new char[length];
        for (int pos = 0; pos < length; pos++) {
            geohash[pos] = BASE32.charAt(((int) (hash >>> 59)));
            hash <<= 5;
        }
        return new String(geohash);
    }

    static long encodeHashToLong(double latitude, double longitude, int length) {
        boolean isEven = true;
        double minLat = -90.0, maxLat = 90;
        double minLon = -180.0, maxLon = 180.0;
        long bit = 0x8000000000000000L;
        long g = 0;

        long target = 0x8000000000000000L >>> (5 * length);
        while (bit != target) {
            if (isEven) {
                double mid = (minLon + maxLon) / 2;
                if (longitude >= mid) {
                    g |= bit;
                    minLon = mid;
                } else
                    maxLon = mid;
            } else {
                double mid = (minLat + maxLat) / 2;
                if (latitude >= mid) {
                    g |= bit;
                    minLat = mid;
                } else
                    maxLat = mid;
            }

            isEven = !isEven;
            bit >>>= 1;
        }
        return g |= length;
    }



    /**  DECODE HASH */


    /**
     * Returns a latitude,longitude pair as the centre of the given geohash.
     * Latitude will be between -90 and 90 and longitude between -180 and 180.
     *
     * @param geohash
     *            hash to decode
     * @return lat long point
     */
    // Translated to java from:
    // geohash.js
    // Geohash library for Javascript
    // (c) 2008 David Troy
    // Distributed under the MIT License
    public static LatLong decodeHash(String geohash) {
        boolean isEven = true;
        double[] lat = new double[2];
        double[] lon = new double[2];
        lat[0] = -90.0;
        lat[1] = 90.0;
        lon[0] = -180.0;
        lon[1] = 180.0;

        for (int i = 0; i < geohash.length(); i++) {
            char c = geohash.charAt(i);
            int cd = BASE32.indexOf(c);
            for (int j = 0; j < 5; j++) {
                int mask = BITS[j];
                if (isEven) {
                    refineInterval(lon, cd, mask);
                } else {
                    refineInterval(lat, cd, mask);
                }
                isEven = !isEven;
            }
        }
        double resultLat = (lat[0] + lat[1]) / 2;
        double resultLon = (lon[0] + lon[1]) / 2;

        return new LatLong(resultLat, resultLon);
    }

    /**
     * Refines interval by a factor or 2 in either the 0 or 1 ordinate.
     *
     * @param interval
     *            two entry array of double values
     * @param cd
     *            used with mask
     * @param mask
     *            used with cd
     */
    private static void refineInterval(double[] interval, int cd, int mask) {
        if ((cd & mask) != 0)
            interval[0] = (interval[0] + interval[1]) / 2;
        else
            interval[1] = (interval[0] + interval[1]) / 2;
    }

}
