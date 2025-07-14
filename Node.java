import java.util.Objects; // Required for Objects.equals and Objects.hash

// Node class representing an intersection or point on the map
public class Node { // Made public
    private String id; // Unique identifier for the node
    private double x;  // X-coordinate (for potential future visualization)
    private double y;  // Y-coordinate (for potential future visualization)

    public Node(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id); // Use Objects.equals for null-safe comparison
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Use Objects.hash for consistent hashing
    }

    @Override
    public String toString() {
        return "Node{" +
               "id='" + id + '\'' +
               '}';
    }
}
