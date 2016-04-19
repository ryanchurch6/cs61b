import java.util.HashSet;

/**
 * Created by delto on 4/18/2016.
 */
public class Node {
    private String id;
    private double lat;
    private double lon;
    private HashSet<Node> connections;
    private double distance;
    private Node previous;


    public Node getPrevious() { return previous; }
    public double getLat() {
        return lat;
    }
    public double getLon() {
        return lon;
    }
    public HashSet<Node> getConnections() {
        return connections;
    }
    public String getId() {
        return id;
    }
    public double getDistance() { return distance; }

    public void setDistance(double n) {
        distance = n;
    }
    public void setPrevious(Node n) {
        previous = n;
    }

    public Node(String id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        connections = new HashSet<Node>();
        distance = 0;
    }

    public Long getLongID() {
        return Long.parseLong(this.getId());
    }

    public void connect(Node m) {
        connections.add(m);
    }

    public boolean isConnected(Node m) {
        return connections.contains(m.getId());
    }
}