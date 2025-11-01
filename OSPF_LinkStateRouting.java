import java.util.*;

class NetworkGraph {
    Map<String, Map<String, Integer>> graph = new HashMap<>();

    void addRouter(String name) {
        graph.putIfAbsent(name, new HashMap<>());
    }

    void connectRouters(String a, String b, int cost) {
        graph.get(a).put(b, cost);
        graph.get(b).put(a, cost);
    }

    void dijkstra(String start) {
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();

        // Initialize all distances to infinity
        for (String node : graph.keySet())
            distance.put(node, Integer.MAX_VALUE);
        distance.put(start, 0);

        for (int i = 0; i < graph.size(); i++) {
            String current = null;
            int min = Integer.MAX_VALUE;

            for (String node : graph.keySet()) {
                if (!visited.contains(node) && distance.get(node) < min) {
                    min = distance.get(node);
                    current = node;
                }
            }

            if (current == null) break;
            visited.add(current);

            for (String neighbor : graph.get(current).keySet()) {
                int newDist = distance.get(current) + graph.get(current).get(neighbor);
                if (newDist < distance.get(neighbor)) {
                    distance.put(neighbor, newDist);
                    previous.put(neighbor, current);
                }
            }
        }

        // Display result
        System.out.println("\nShortest Paths from Router " + start + ":");
        System.out.println("----------------------------------------");
        System.out.println("Destination\tDistance\tPath");
        for (String node : graph.keySet()) {
            System.out.print(node + "\t\t" + distance.get(node) + "\t\t");
            printPath(previous, node);
            System.out.println();
        }
    }

    void printPath(Map<String, String> prev, String node) {
        List<String> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = prev.get(node);
        }
        Collections.reverse(path);
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i));
            if (i < path.size() - 1)
                System.out.print(" => ");
        }
    }
}

public class OSPF_LinkStateRouting {
    public static void main(String[] args) {
        NetworkGraph net = new NetworkGraph();

        // Add routers
        net.addRouter("R1");
        net.addRouter("R2");
        net.addRouter("R3");
        net.addRouter("R4");
        net.addRouter("R5");

        // Connect routers (Edges)
        net.connectRouters("R1", "R2", 4);
        net.connectRouters("R1", "R3", 1);
        net.connectRouters("R2", "R3", 2);
        net.connectRouters("R2", "R4", 5);
        net.connectRouters("R3", "R4", 8);
        net.connectRouters("R3", "R5", 10);
        net.connectRouters("R4", "R5", 2);

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter start router (R1/R2/R3/R4/R5): ");
        String start = sc.next().toUpperCase();

        net.dijkstra(start);
    }
}
