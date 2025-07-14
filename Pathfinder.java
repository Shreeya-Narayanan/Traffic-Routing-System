import java.util.*;

// PathNode is a helper class for Pathfinder and can be defined in the same file
// or in its own PathNode.java file. For this example, it's included here.
class PathNode implements Comparable<PathNode> {
    String nodeId;
    double cost;

    public PathNode(String nodeId, double cost) {
        this.nodeId = nodeId;
        this.cost = cost;
    }

    @Override
    public int compareTo(PathNode other) {
        return Double.compare(this.cost, other.cost);
    }
}

public class Pathfinder {
    private Graph graph;

    public Pathfinder(Graph graph) {
        this.graph = graph;
    }

    /**
     * Finds the shortest path using Dijkstra's algorithm, considering time-dependent traffic,
     * and generates human-readable turn instructions.
     * @param startNodeId The ID of the starting node.
     * @param endNodeId The ID of the ending node.
     * @param hourOfDay The hour of the day (0-23) for which to calculate traffic.
     * @return A PathResult object containing the path, total travel time, and instructions.
     */
    public PathResult findPath(String startNodeId, String endNodeId, int hourOfDay) {
        // Validate start and end nodes
        if (graph.getNode(startNodeId) == null || graph.getNode(endNodeId) == null) {
            System.err.println("Error: Start or end node does not exist in the graph.");
            return new PathResult(Collections.emptyList(), 0.0, Collections.emptyList());
        }

        // Validate hourOfDay
        if (hourOfDay < 0 || hourOfDay > 23) {
            System.err.println("Error: Hour of day must be between 0 and 23.");
            return new PathResult(Collections.emptyList(), 0.0, Collections.emptyList());
        }

        // Dijkstra's Algorithm Initialization
        Map<String, Double> distances = new HashMap<>(); // Stores the shortest distance from startNode to each node
        Map<String, String> previousNodes = new HashMap<>(); // Stores the previous node in the shortest path
        PriorityQueue<PathNode> pq = new PriorityQueue<>(); // Priority queue for nodes to visit

        // Initialize distances: infinity for all nodes, 0 for the start node
        for (String nodeId : graph.getNodeIds()) {
            distances.put(nodeId, Double.POSITIVE_INFINITY);
            previousNodes.put(nodeId, null);
        }
        distances.put(startNodeId, 0.0);
        pq.add(new PathNode(startNodeId, 0.0));

        // Dijkstra's Algorithm Core Loop
        while (!pq.isEmpty()) {
            PathNode current = pq.poll();
            String currentNodeId = current.nodeId;
            double currentCost = current.cost;

            // If we've already found a shorter path to this node, skip
            if (currentCost > distances.get(currentNodeId)) {
                continue;
            }

            // If we reached the end node, we can stop
            if (currentNodeId.equals(endNodeId)) {
                break;
            }

            // Explore neighbors
            for (Map.Entry<Node, Edge> neighborEntry : graph.getNeighbors(currentNodeId)) {
                Node neighborNode = neighborEntry.getKey();
                Edge edge = neighborEntry.getValue();

                // Get travel time based on the specified hour of day
                double travelTime = edge.getCurrentTravelTime(hourOfDay);
                double newDistance = currentCost + travelTime;

                // If a shorter path to the neighbor is found
                if (newDistance < distances.get(neighborNode.getId())) {
                    distances.put(neighborNode.getId(), newDistance);
                    previousNodes.put(neighborNode.getId(), currentNodeId);
                    pq.add(new PathNode(neighborNode.getId(), newDistance));
                }
            }
        }

        // --- Path Reconstruction ---
        List<String> shortestPath = new ArrayList<>();
        String currentNode = endNodeId;
        while (currentNode != null && previousNodes.containsKey(currentNode)) {
            shortestPath.add(0, currentNode); // Add to the beginning of the list to build path in correct order
            currentNode = previousNodes.get(currentNode);
            if (currentNode == null) break; // Break if we reached the start node
        }

        // Check if path was found and starts with the start node
        if (shortestPath.isEmpty() || !shortestPath.get(0).equals(startNodeId) || distances.get(endNodeId) == Double.POSITIVE_INFINITY) {
            return new PathResult(Collections.emptyList(), 0.0, Collections.emptyList()); // Path not found
        }

        // --- Generate Turn Instructions ---
        List<String> instructions = new ArrayList<>();
        instructions.add("1. Start at " + startNodeId + ".");

        String currentRoadName = null;
        String previousNodeInPath = null; // Keep track of the node before the current segment

        for (int i = 0; i < shortestPath.size() - 1; i++) {
            String u = shortestPath.get(i); // Current node in path
            String v = shortestPath.get(i + 1); // Next node in path

            // Get the specific directed edge from u to v
            Edge edge = graph.getDirectedEdge(u, v);

            if (edge != null) {
                String nextRoadName = edge.getName();

                if (currentRoadName == null) {
                    // First segment of the journey
                    instructions.add((i + 2) + ". Head onto " + nextRoadName + ".");
                } else if (!nextRoadName.equals(currentRoadName)) {
                    // Road name changed, implies a turn
                    // For simplicity, we just say "Turn onto".
                    // A more advanced system would calculate left/right turns based on coordinates
                    // and the angle between the previous and current edge.
                    instructions.add((i + 2) + ". Turn onto " + nextRoadName + " at " + u + ".");
                } else {
                    // Continue on the same road
                    // Only add "Continue" if it's not the very next instruction after a turn
                    // This avoids redundant "Continue" messages for short segments on the same road
                    if (instructions.size() > 0 && !instructions.get(instructions.size() - 1).contains("Head onto") &&
                        !instructions.get(instructions.size() - 1).contains("Turn onto")) {
                        // Avoid consecutive "Continue" if the road is very long and has intermediate nodes
                        if (!instructions.get(instructions.size() - 1).contains("Continue on " + nextRoadName)) {
                             instructions.add((i + 2) + ". Continue on " + nextRoadName + ".");
                        }
                    }
                }
                currentRoadName = nextRoadName;
            } else {
                // This case should ideally not happen if the path is valid and graph is consistent,
                // but included for robustness.
                instructions.add((i + 2) + ". Error: Could not find edge between " + u + " and " + v + ".");
            }
        }
        instructions.add((shortestPath.size() + 1) + ". Arrive at " + endNodeId + ".");


        return new PathResult(shortestPath, distances.get(endNodeId), instructions);
    }
}
