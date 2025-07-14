import java.util.Collections;
import java.util.List;

public class PathResult {
    private List<String> path;
    private double totalTime;
    private List<String> instructions; // New field for turn instructions
  
    public PathResult(List<String> path, double totalTime, List<String> instructions) {
        this.path = path;
        this.totalTime = totalTime;
        this.instructions = instructions; // Initialize the new field
    }

    public List<String> getPath() {
        return path;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public List<String> getInstructions() { // New getter for instructions
        return instructions;
    }

    @Override
    public String toString() {
        if (path.isEmpty()) {
            return "No path found.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Path: ").append(String.join(" -> ", path)).append("\n");
        sb.append("Total Travel Time: ").append(String.format("%.2f", totalTime)).append(" minutes\n");
        sb.append("Instructions:\n");
        // Print each instruction with a bullet point
        for (String instruction : instructions) {
            sb.append(instruction).append("\n");
        }
        return sb.toString();
    }
}
