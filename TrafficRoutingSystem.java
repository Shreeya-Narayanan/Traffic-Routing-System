import java.util.Scanner; // Required for reading user input

// Main class to demonstrate the usage via command-line
public class TrafficRoutingSystem {
    public static void main(String[] args) {
        Graph graph = new Graph();

        // Define some nodes
        graph.addNode(new Node("A", 100, 100));
        graph.addNode(new Node("B", 300, 100));
        graph.addNode(new Node("C", 100, 300));
        graph.addNode(new Node("D", 300, 300));
        graph.addNode(new Node("E", 500, 200));
        graph.addNode(new Node("F", 500, 400));
        graph.addNode(new Node("G", 700, 200));
        graph.addNode(new Node("H", 700, 400));

        // Define directed edges with base weights and names
        // For two-way roads, add two edges (e.g., A->B and B->A)
        graph.addEdge("A", "B", 5, "Main St");
        graph.addEdge("B", "A", 5, "Main St");

        graph.addEdge("A", "C", 8, "Elm Ave");
        graph.addEdge("C", "A", 8, "Elm Ave");

        graph.addEdge("B", "D", 6, "Oak Ln"); // One-way from B to D

        graph.addEdge("B", "E", 10, "Maple Blvd");
        graph.addEdge("E", "B", 10, "Maple Blvd");

        graph.addEdge("C", "D", 4, "Pine Rd");
        graph.addEdge("D", "C", 4, "Pine Rd");

        graph.addEdge("D", "F", 7, "Cedar Ct");
        graph.addEdge("F", "D", 7, "Cedar Ct");

        graph.addEdge("E", "G", 3, "Willow Way");
        graph.addEdge("G", "E", 3, "Willow Way");

        graph.addEdge("E", "F", 5, "Birch St");
        graph.addEdge("F", "E", 5, "Birch St");

        graph.addEdge("F", "H", 2, "Spruce Ave");
        graph.addEdge("H", "F", 2, "Spruce Ave");

        graph.addEdge("G", "H", 9, "Cypress Dr");
        graph.addEdge("H", "G", 9, "Cypress Dr");

        Pathfinder pathfinder = new Pathfinder(graph);

        // --- Simulate Time-Dependent Traffic ---
        // Example: Heavy morning rush hour traffic on Main St (A->B) and Oak Ln (B->D)
        // And evening rush hour on Pine Rd (C->D)
        Edge mainStAB = graph.getDirectedEdge("A", "B");
        if (mainStAB != null) {
            mainStAB.setTrafficFactor(8, 2.0);  // 8 AM: double travel time
            mainStAB.setTrafficFactor(9, 1.8);  // 9 AM: 80% increase
            mainStAB.setTrafficFactor(17, 1.5); // 5 PM: 50% increase
        }

        Edge oakLnBD = graph.getDirectedEdge("B", "D");
        if (oakLnBD != null) {
            oakLnBD.setTrafficFactor(8, 3.0);   // 8 AM: triple travel time (heavy congestion)
            oakLnBD.setTrafficFactor(18, 2.0);  // 6 PM: double travel time
        }

        Edge pineRdCD = graph.getDirectedEdge("C", "D");
        if (pineRdCD != null) {
            pineRdCD.setTrafficFactor(17, 2.5); // 5 PM: 150% increase
            pineRdCD.setTrafficFactor(18, 2.2); // 6 PM: 120% increase
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Real-time Traffic Routing System (Command-Line) ---");
        System.out.println("Available Nodes: A, B, C, D, E, F, G, H");
        System.out.println("Enter 'exit' at any prompt to quit.");

        while (true) {
            System.out.print("\nEnter Start Node ID: ");
            String startNodeId = scanner.nextLine().trim().toUpperCase();
            if (startNodeId.equals("EXIT")) break;

            System.out.print("Enter End Node ID: ");
            String endNodeId = scanner.nextLine().trim().toUpperCase();
            if (endNodeId.equals("EXIT")) break;

            int hourOfDay;
            while (true) {
                System.out.print("Enter Hour of Day (0-23): ");
                String hourInput = scanner.nextLine().trim();
                if (hourInput.equals("exit")) { // Allow exit from hour prompt
                    scanner.close();
                    return;
                }
                try {
                    hourOfDay = Integer.parseInt(hourInput);
                    if (hourOfDay >= 0 && hourOfDay <= 23) {
                        break;
                    } else {
                        System.out.println("Invalid hour. Please enter a number between 0 and 23.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }

            // Find and display path
            PathResult result = pathfinder.findPath(startNodeId, endNodeId, hourOfDay);
            System.out.println(result);
        }

        scanner.close();
        System.out.println("Exiting Traffic Routing System. Goodbye!");
    }
}
