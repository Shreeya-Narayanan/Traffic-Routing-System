/*     UI for further enhancement
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

// Import the core logic classes from their respective files
// These classes are now expected to be in their own .java files in the same directory
// or properly configured in your project's build path.
// For this example, we assume they are in the same directory.
// import Node; // No explicit import needed if in same package/default package
// import Edge;
// import Graph;
// import Pathfinder;
// import PathResult;
// import PathNode; // PathNode is an inner class of Pathfinder, so not directly imported

public class TrafficRoutingSystem extends Application {

    private Graph graph;
    private Pathfinder pathfinder;
    private Canvas canvas;
    private GraphicsContext gc;

    private TextField startNodeField;
    private TextField endNodeField;
    private Slider hourSlider;
    private Label hourLabel;
    private Label resultLabel;
    private Label errorLabel;

    private List<String> currentPath = new ArrayList<>();
    private int currentHour = 0; // Default to 0 AM

    // Constants for drawing
    private static final double NODE_RADIUS = 15;
    private static final double NODE_BORDER_WIDTH = 2;
    private static final Color NODE_COLOR = Color.web("#4CAF50"); // Green
    private static final Color NODE_BORDER_COLOR = Color.web("#388E3C"); // Darker Green
    private static final Color EDGE_COLOR_CLEAR = Color.web("#8BC34A"); // Light Green
    private static final Color EDGE_COLOR_MODERATE = Color.web("#FFC107"); // Amber
    private static final Color EDGE_COLOR_HEAVY = Color.web("#F44336"); // Red
    private static final Color PATH_COLOR = Color.web("#2196F3"); // Blue
    private static final double EDGE_WIDTH = 3;
    private static final double PATH_WIDTH = 5;
    private static final double ARROW_SIZE = 8; // Size for arrowheads on directed edges

    @Override
    public void start(Stage primaryStage) {
        initializeGraph(); // Setup the graph data

        // --- UI Elements ---
        startNodeField = new TextField();
        startNodeField.setPromptText("Start Node ID (e.g., A)");
        startNodeField.setMaxWidth(150);

        endNodeField = new TextField();
        endNodeField.setPromptText("End Node ID (e.g., H)");
        endNodeField.setMaxWidth(150);

        hourSlider = new Slider(0, 23, 0); // Min, Max, Initial value
        hourSlider.setBlockIncrement(1);
        hourSlider.setMajorTickUnit(6);
        hourSlider.setMinorTickCount(5);
        hourSlider.setShowTickLabels(true);
        hourSlider.setShowTickMarks(true);
        hourSlider.setSnapToTicks(true);
        hourSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentHour = newVal.intValue();
            hourLabel.setText(String.format("Hour of Day: %02d:00", currentHour));
            drawGraph(); // Redraw graph to show potential traffic changes
        });
        hourLabel = new Label(String.format("Hour of Day: %02d:00", currentHour));
        hourLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button findPathButton = new Button("Find Path");
        findPathButton.setOnAction(e -> findPath());
        findPathButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 10 20;");
        findPathButton.setOnMouseEntered(e -> findPathButton.setStyle("-fx-background-color: #43A047; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 10 20;"));
        findPathButton.setOnMouseExited(e -> findPathButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 10 20;"));


        resultLabel = new Label("Path: \nTotal Travel Time: ");
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        resultLabel.setWrapText(true);

        errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        errorLabel.setWrapText(true);

        // --- Layout ---
        VBox controls = new VBox(10); // Spacing between elements
        controls.setPadding(new Insets(20));
        controls.setAlignment(Pos.TOP_LEFT);
        controls.getChildren().addAll(
                new Label("Start Node:"), startNodeField,
                new Label("End Node:"), endNodeField,
                hourLabel, hourSlider,
                findPathButton,
                new Separator(),
                new Label("Results:"), resultLabel,
                errorLabel
        );
        controls.setPrefWidth(250);
        controls.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        VBox.setVgrow(resultLabel, Priority.ALWAYS); // Allow result label to grow

        canvas = new Canvas(800, 600); // Canvas for drawing the graph
        gc = canvas.getGraphicsContext2D();

        // Wrap canvas in a StackPane to allow for potential overlays if needed
        StackPane canvasPane = new StackPane(canvas);
        canvasPane.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        HBox.setHgrow(canvasPane, Priority.ALWAYS); // Allow canvas to grow horizontally

        HBox root = new HBox(10); // Main layout, spacing between controls and canvas
        root.setPadding(new Insets(10));
        root.getChildren().addAll(controls, canvasPane);

        // --- Scene and Stage Setup ---
        Scene scene = new Scene(root, 1100, 620); // Width, Height
        primaryStage.setTitle("Real-time Traffic Routing System");
        primaryStage.setScene(scene);
        primaryStage.show();

        drawGraph(); // Initial drawing of the graph
    }

    private void initializeGraph() {
        graph = new Graph();

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

        pathfinder = new Pathfinder(graph);

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
    }

    private void drawGraph() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Clear canvas

        // Draw edges first
        for (Edge edge : graph.getAllEdges()) {
            Node startNode = graph.getNode(edge.getStartNodeId());
            Node endNode = graph.getNode(edge.getEndNodeId());

            if (startNode != null && endNode != null) {
                gc.setStroke(getEdgeColor(edge.getTrafficFactor(currentHour)));
                gc.setLineWidth(EDGE_WIDTH);
                gc.strokeLine(startNode.getX(), startNode.getY(), endNode.getX(), endNode.getY());

                // Draw arrow for directed edge
                drawArrowHead(gc, startNode.getX(), startNode.getY(), endNode.getX(), endNode.getY());

                // Draw edge weight/name
                double midX = (startNode.getX() + endNode.getX()) / 2;
                double midY = (startNode.getY() + endNode.getY()) / 2;
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Arial", 10));
                gc.fillText(String.format("%.1f (%s)", edge.getCurrentTravelTime(currentHour), edge.getName()), midX + 5, midY - 5);
            }
        }

        // Draw nodes
        for (Node node : graph.nodes.values()) {
            gc.setFill(NODE_COLOR);
            gc.setStroke(NODE_BORDER_COLOR);
            gc.setLineWidth(NODE_BORDER_WIDTH);
            gc.fillOval(node.getX() - NODE_RADIUS, node.getY() - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            gc.strokeOval(node.getX() - NODE_RADIUS, node.getY() - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.setTextBaseline(javafx.scene.text.VPos.CENTER);
            gc.fillText(node.getId(), node.getX(), node.getY());
        }

        // Highlight the path if available
        if (currentPath.size() > 1) {
            gc.setStroke(PATH_COLOR);
            gc.setLineWidth(PATH_WIDTH);
            gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND); // Smoother path lines

            for (int i = 0; i < currentPath.size() - 1; i++) {
                Node startPathNode = graph.getNode(currentPath.get(i));
                Node endPathNode = graph.getNode(currentPath.get(i + 1));
                if (startPathNode != null && endPathNode != null) {
                    gc.strokeLine(startPathNode.getX(), startPathNode.getY(), endPathNode.getX(), endPathNode.getY());
                    // Draw arrow for highlighted path
                    drawArrowHead(gc, startPathNode.getX(), startPathNode.getY(), endPathNode.getX(), endPathNode.getY(), PATH_COLOR);
                }
            }
        }
    }

    // Helper to draw arrowheads for directed edges
    private void drawArrowHead(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        drawArrowHead(gc, x1, y1, x2, y2, gc.getStroke()); // Use current stroke color
    }

    private void drawArrowHead(GraphicsContext gc, double x1, double y1, double x2, double y2, Color color) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double arrowX1 = x2 - ARROW_SIZE * Math.cos(angle - Math.PI / 6);
        double arrowY1 = y2 - ARROW_SIZE * Math.sin(angle - Math.PI / 6);
        double arrowX2 = x2 - ARROW_SIZE * Math.cos(angle + Math.PI / 6);
        double arrowY2 = y2 - ARROW_SIZE * Math.sin(angle + Math.PI / 6);

        gc.save(); // Save current state
        gc.setStroke(color);
        gc.setLineWidth(EDGE_WIDTH); // Match edge width
        gc.strokeLine(x2, y2, arrowX1, arrowY1);
        gc.strokeLine(x2, y2, arrowX2, arrowY2);
        gc.restore(); // Restore previous state
    }


    // Determine edge color based on traffic factor
    private Color getEdgeColor(double trafficFactor) {
        if (trafficFactor >= 2.5) { // Very heavy
            return EDGE_COLOR_HEAVY;
        } else if (trafficFactor >= 1.5) { // Moderate to heavy
            return EDGE_COLOR_MODERATE;
        } else { // Light or no traffic
            return EDGE_COLOR_CLEAR;
        }
    }

    private void findPath() {
        String startId = startNodeField.getText().trim().toUpperCase();
        String endId = endNodeField.getText().trim().toUpperCase();

        errorLabel.setText(""); // Clear previous errors
        resultLabel.setText("Path: \nTotal Travel Time: "); // Clear previous results
        currentPath.clear(); // Clear previous path highlighting

        if (startId.isEmpty() || endId.isEmpty()) {
            errorLabel.setText("Please enter both Start and End Node IDs.");
            drawGraph();
            return;
        }

        if (!graph.getNodeIds().contains(startId)) {
            errorLabel.setText("Start Node ID '" + startId + "' does not exist.");
            drawGraph();
            return;
        }
        if (!graph.getNodeIds().contains(endId)) {
            errorLabel.setText("End Node ID '" + endId + "' does not exist.");
            drawGraph();
            return;
        }
        if (startId.equals(endId)) {
            errorLabel.setText("Start and End Node IDs cannot be the same.");
            currentPath.add(startId); // Highlight single node
            drawGraph();
            return;
        }

        PathResult result = pathfinder.findPath(startId, endId, currentHour);

        if (result.getPath().isEmpty()) {
            resultLabel.setText("No path found between " + startId + " and " + endId + " at " + String.format("%02d:00", currentHour) + ".");
        } else {
            currentPath = result.getPath();
            resultLabel.setText(String.format("Path: %s\nTotal Travel Time: %.2f minutes",
                    String.join(" -> ", result.getPath()), result.getTotalTime()));
        }
        drawGraph(); // Redraw to show the new path
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/