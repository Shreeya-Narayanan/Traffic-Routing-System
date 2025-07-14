import java.util.Arrays;

// Edge class representing a road segment, now supporting time-dependent traffic and a name
public class Edge { // Made public
    private String startNodeId; // ID of the starting node
    private String endNodeId;   // ID of the ending node
    private double baseWeight;  // Base travel time/distance for the edge (e.g., in minutes)
    private String name;        // Name of the road/segment (e.g., "Main Street")

    // Array to store traffic factors for each hour of the day (0-23)
    // 1.0 means no traffic, >1.0 means congestion
    private double[] timeDependentTrafficFactors;

    public Edge(String startNodeId, String endNodeId, double baseWeight, String name) {
        this.startNodeId = startNodeId;
        this.endNodeId = endNodeId;
        this.baseWeight = baseWeight;
        this.name = name;
        this.timeDependentTrafficFactors = new double[24];
        // Initialize all hours with no traffic (factor 1.0)
        Arrays.fill(this.timeDependentTrafficFactors, 1.0);
    }

    public String getStartNodeId() {
        return startNodeId;
    }

    public String getEndNodeId() {
        return endNodeId;
    }

    public double getBaseWeight() {
        return baseWeight;
    }

    public String getName() {
        return name;
    }

   
    public void setTrafficFactor(int hourOfDay, double factor) {
        if (hourOfDay < 0 || hourOfDay > 23) {
            throw new IllegalArgumentException("Hour of day must be between 0 and 23.");
        }
        if (factor < 0) {
            throw new IllegalArgumentException("Traffic factor cannot be negative.");
        }
        this.timeDependentTrafficFactors[hourOfDay] = factor;
    }

  
    public double getTrafficFactor(int hourOfDay) {
        if (hourOfDay < 0 || hourOfDay > 23) {
            throw new IllegalArgumentException("Hour of day must be between 0 and 23.");
        }
        return this.timeDependentTrafficFactors[hourOfDay];
    }

    
    public double getCurrentTravelTime(int hourOfDay) {
        return baseWeight * getTrafficFactor(hourOfDay);
    }

    @Override
    public String toString() {
        return "Edge{" +
               "from=" + startNodeId +
               ", to=" + endNodeId +
               ", name='" + name + '\'' +
               ", baseWeight=" + baseWeight +
               '}';
    }
}