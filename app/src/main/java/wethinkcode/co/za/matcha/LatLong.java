package wethinkcode.co.za.matcha;

public class LatLong {

    private final double lat;
    private final double lon;

    /**
     * Constructor.
     *
     * @param lat latitude in decimal degrees
     * @param lon longitude in decimal degrees
     */
    public LatLong(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Returns the latitude in decimal degrees.
     *
     * @return latitude in decimal degrees
     */
    public double getLat() {
        return lat;
    }

    /**
     * Returns the longitude in decimal degrees.
     *
     * @return longitude in decimal degrees
     */
    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LatLong [lat=");
        builder.append(lat);
        builder.append(", lon=");
        builder.append(lon);
        builder.append("]");
        return builder.toString();
    }
}
