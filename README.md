# Real-time Traffic Routing System

This project implements a real-time traffic routing system in Java, capable of finding optimal paths between two points on a simulated road network. It demonstrates core Data Structures and Algorithms (DSA) concepts, including graph representation, shortest path algorithms, and dynamic edge weights to simulate real-time traffic conditions.

## ✨ Features

* **Graph Representation:** Utilizes an **Adjacency List** to efficiently model road networks, supporting dynamic connections.
* **Directed Edges:** Accurately represents one-way streets and specific travel directions.
* **Time-Dependent Traffic Simulation:** Edge weights (travel times) dynamically adjust based on the hour of the day, simulating varying traffic congestion.
* **Dijkstra's Algorithm:** Implements Dijkstra's shortest path algorithm, leveraging a **Priority Queue** for efficient route calculation.
* **Human-Readable Instructions:** Generates step-by-step, turn-by-turn navigation instructions for the calculated optimal route.
* **Modular Design:** Built with an Object-Oriented (OO) approach, separating concerns into distinct classes (`Node`, `Edge`, `Graph`, `Pathfinder`, `PathResult`) for maintainability and scalability.
* **Command-Line Interface (CLI):** Provides an interactive console application for users to input start/end points and query routes at specific times.

## 🚀 Technologies Used

* **Java:** The primary programming language for all core logic and the CLI.
* **Core Java Collections:** `Map`, `List`, `Set`, `HashMap`, `ArrayList`, `PriorityQueue` for efficient data management.

## 📂 Project Structure

The project is organized into several modular `.java` files:

* `Node.java`: Represents an intersection or point on the map with an ID and coordinates.
* `Edge.java`: Represents a road segment, storing base travel time, road name, and time-dependent traffic factors.
* `Graph.java`: Manages the collection of nodes and directed edges using an adjacency list.
* `Pathfinder.java`: Implements Dijkstra's algorithm to find the shortest path and generate instructions. (Includes `PathNode` helper class).
* `PathResult.java`: Stores and formats the result of a pathfinding query (path, total time, and instructions).
* `TrafficRoutingSystem.java`: The main class containing the `main` method to run the command-line interface and initialize the graph with sample data and traffic patterns.

##  How to Run

1.  **Save the Files:**
    Ensure all `.java` files (`Node.java`, `Edge.java`, `Graph.java`, `Pathfinder.java`, `PathResult.java`, `TrafficRoutingSystem.java`) are saved in the same directory.

2.  **Compile the Java Files:**
    Open your terminal or command prompt, navigate to the directory where you saved the files, and compile them using the Java compiler:
    ```bash
    javac *.java
    ```

3.  **Run the Application:**
    After successful compilation, execute the main class:
    ```bash
    java TrafficRoutingSystem
    ```
    The application will then prompt you to enter the start node, end node, and hour of the day. Type `exit` at any prompt to quit.

## 💡 Future Enhancements

* **A\* Search Algorithm:** Implement the A\* algorithm in `Pathfinder` for more efficient goal-directed search, especially on larger graphs.
* **Graphical User Interface (GUI):** Develop a UI (e.g., using JavaFX or Swing) to visually display the map, traffic conditions, and highlighted routes.
* **Traffic Prediction:** Use Machine learning concepts to predict the traffic factor according to the time of day.
* **External Data Integration:** Explore integrating with real-world map data (e.g., OpenStreetMap) or traffic APIs (though this would be a significant leap).

