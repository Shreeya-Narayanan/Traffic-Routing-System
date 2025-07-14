import java.util.*;

// Graph class to manage nodes and directed edges using an adjacency list
public class Graph { // Made public
    private Map<String, Node> nodes; // Stores nodes by their ID
    private List<Edge> edges;        // Stores all edges (for easy iteration/lookup)
    // Adjacency list: Map<nodeId, List<Map.Entry<Node, Edge>>>
    // Each entry in the list contains the neighbor Node and the Edge connecting them
    private Map<String, List<Map.Entry<Node, Edge>>> adj;

    public Graph() {
        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>();
        this.adj = new HashMap<>();
    }

    // Add a node to the graph
    public void addNode(Node node) {
        if (!nodes.containsKey(node.getId())) {
            nodes.put(node.getId(), node);
            adj.put(node.getId(), new ArrayList<>()); // Initialize adjacency list for the new node
        }
    }

    
    public void addEdge(String startNodeId, String endNodeId, double baseWeight, String name) {
        if (!nodes.containsKey(startNodeId) || !nodes.containsKey(endNodeId)) {
            System.err.println("Error: One or both nodes for edge (" + startNodeId + ", " + endNodeId + ") do not exist.");
            return;
        }

        Edge edge = new Edge(startNodeId, endNodeId, baseWeight, name);
        edges.add(edge); // Add to the list of all edges

        // Add to adjacency list for the start node only (directed graph)
        adj.get(startNodeId).add(new AbstractMap.SimpleEntry<>(nodes.get(endNodeId), edge));
    }

    // Get neighbors of a given node ID
    public List<Map.Entry<Node, Edge>> getNeighbors(String nodeId) {
        return adj.getOrDefault(nodeId, Collections.emptyList());
    }

    // Get a node by its ID
    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    // Get all node IDs
    public Set<String> getNodeIds() {
        return nodes.keySet();
    }

    public Edge getDirectedEdge(String startNodeId, String endNodeId) {
        for (Edge edge : edges) {
            if (edge.getStartNodeId().equals(startNodeId) && edge.getEndNodeId().equals(endNodeId)) {
                return edge;
            }
        }
        return null;
        // For very large graphs, iterating through all edges might be slow.
        // A more optimized approach for getting a specific edge would be to
        // store edges in a Map<String, Map<String, Edge>> where keys are startNodeId and endNodeId.
    }

    public List<Edge> getAllEdges() {
        return edges;
    }

    // Added for UI to access nodes for drawing
    public Map<String, Node> getNodes() {
        return nodes;
    }
}
