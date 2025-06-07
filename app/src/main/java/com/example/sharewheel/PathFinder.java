package com.example.sharewheel;


import com.google.android.gms.maps.model.LatLng;
import java.util.*;

public class PathFinder {
    private static class Node implements Comparable<Node> {
        LatLng location;
        double distance;
        Node previous;

        Node(LatLng location) {
            this.location = location;
            this.distance = Double.MAX_VALUE;
            this.previous = null;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    public static List<LatLng> findShortestPath(LatLng start, LatLng end, List<LatLng> intermediatePoints) {
        // Create nodes for all points
        List<Node> nodes = new ArrayList<>();
        Node startNode = new Node(start);
        Node endNode = new Node(end);
        nodes.add(startNode);
        nodes.add(endNode);

        // Add intermediate points
        for (LatLng point : intermediatePoints) {
            nodes.add(new Node(point));
        }

        // Initialize distances
        startNode.distance = 0;

        // Create priority queue for Dijkstra's algorithm
        PriorityQueue<Node> queue = new PriorityQueue<>(nodes);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current == endNode) {
                break;
            }

            // Check all possible connections
            for (Node neighbor : nodes) {
                if (neighbor != current) {
                    double distance = calculateDistance(current.location, neighbor.location);
                    double newDistance = current.distance + distance;

                    if (newDistance < neighbor.distance) {
                        neighbor.distance = newDistance;
                        neighbor.previous = current;
                        // Update queue
                        queue.remove(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }

        // Reconstruct path
        List<LatLng> path = new ArrayList<>();
        Node current = endNode;
        while (current != null) {
            path.add(0, current.location);
            current = current.previous;
        }

        return path;
    }

    private static double calculateDistance(LatLng point1, LatLng point2) {
        // Haversine formula to calculate distance between two points on Earth
        double R = 6371; // Earth's radius in kilometers
        double lat1 = Math.toRadians(point1.latitude);
        double lat2 = Math.toRadians(point2.latitude);
        double dLat = Math.toRadians(point2.latitude - point1.latitude);
        double dLon = Math.toRadians(point2.longitude - point1.longitude);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }
}
