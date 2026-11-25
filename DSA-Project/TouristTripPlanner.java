
import java.util.*;

public class TouristTripPlanner {

    static class Edge {
        String destination;
        int time;

        Edge(String destination, int time) {
            this.destination = destination;
            this.time = time;
        }
    }

    static class Route {
        ArrayList<String> places;
        int time;
        int rating;

        Route(ArrayList<String> places, int time, int rating) {
            this.places = new ArrayList<>(places);
            this.time = time;
            this.rating = rating;
        }
    }

    static Map<String, ArrayList<Edge>> graph = new LinkedHashMap<>();
    static Map<String, Integer> ratings = new HashMap<>();

    static Route bestRoute = new Route(
        new ArrayList<>(Arrays.asList("Hotel")), 0, 0
    );

    static void addPlace(String name, int rating) {
        graph.put(name, new ArrayList<>());
        ratings.put(name, rating);
    }

    static void addRoad(String source, String destination, int time) {
        graph.get(source).add(new Edge(destination, time));
        graph.get(destination).add(new Edge(source, time));
    }

    static void createGraph() {
        addPlace("Hotel", 0);
        addPlace("Fort", 8);
        addPlace("Museum", 5);
        addPlace("Lake", 4);
        addPlace("Temple", 6);

        addRoad("Hotel", "Fort", 2);
        addRoad("Hotel", "Lake", 1);
        addRoad("Lake", "Museum", 1);
        addRoad("Museum", "Temple", 1);
        addRoad("Fort", "Temple", 2);
    }

    static void sortNeighbours(ArrayList<Edge> neighbours) {
        int n = neighbours.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {

                int r1 = ratings.get(neighbours.get(j).destination);
                int r2 = ratings.get(neighbours.get(j + 1).destination);

                if (r1 < r2) {
                    Edge temp = neighbours.get(j);
                    neighbours.set(j, neighbours.get(j + 1));
                    neighbours.set(j + 1, temp);
                }
            }
        }
    }

    static Route greedyPlan(int budget) {
        ArrayList<String> path = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        String current = "Hotel";
        int totalTime = 0;
        int totalRating = 0;

        path.add(current);
        visited.add(current);

        while (true) {
            ArrayList<Edge> neighbours =
                new ArrayList<>(graph.get(current));

            sortNeighbours(neighbours);

            Edge selected = null;

            for (Edge edge : neighbours) {
                if (!visited.contains(edge.destination)
                    && totalTime + edge.time <= budget) {
                    selected = edge;
                    break;
                }
            }

            if (selected == null) {
                break;
            }

            current = selected.destination;
            totalTime += selected.time;
            totalRating += ratings.get(current);

            path.add(current);
            visited.add(current);
        }

        return new Route(path, totalTime, totalRating);
    }

    static void backtrack(
        String current,
        int budget,
        int totalTime,
        int totalRating,
        ArrayList<String> path,
        Set<String> visited
    ) {

        if (totalRating > bestRoute.rating ||
            (totalRating == bestRoute.rating &&
             totalTime < bestRoute.time)) {

            bestRoute = new Route(path, totalTime, totalRating);
        }

        for (Edge edge : graph.get(current)) {

            String next = edge.destination;

            if (!visited.contains(next) &&
                totalTime + edge.time <= budget) {

                visited.add(next);
                path.add(next);

                backtrack(
                    next,
                    budget,
                    totalTime + edge.time,
                    totalRating + ratings.get(next),
                    path,
                    visited
                );

                path.remove(path.size() - 1);
                visited.remove(next);
            }
        }
    }

    static Route bestPlan(int budget) {
        bestRoute = new Route(
            new ArrayList<>(Arrays.asList("Hotel")), 0, 0
        );

        ArrayList<String> path = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        path.add("Hotel");
        visited.add("Hotel");

        backtrack("Hotel", budget, 0, 0, path, visited);

        return bestRoute;
    }

    static void displayRoute(String label, Route route) {
        System.out.println(label + ": "
            + String.join(" -> ", route.places)
            + " | " + route.time + " h | Rating "
            + route.rating);
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        createGraph();

        System.out.print("Enter travel budget in hours: ");

        if (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            sc.close();
            return;
        }

        int budget = sc.nextInt();

        if (budget <= 0) {
            System.out.println(
                "Invalid budget. Hours must be greater than 0."
            );
            sc.close();
            return;
        }

        Route greedy = greedyPlan(budget);
        Route best = bestPlan(budget);

        System.out.println("\nTravel Budget: " + budget + " hours");

        displayRoute("Greedy", greedy);
        displayRoute("Best (backtracking)", best);

        sc.close();
    }
}
