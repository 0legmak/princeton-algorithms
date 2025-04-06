import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class MyBaseballElimination {
    
    private class TeamInfo {
        String name;
        int wins;
        int losses;
        int remaining;
        int[] games;
    };

    private HashMap<String, Integer> team_to_index;
    private TeamInfo[] teams;
    private HashMap<String, HashSet<String>> elimination;

    private void loadData(String filename) {
        In in = new In(filename);
        int cnt = in.readInt();
        team_to_index = new HashMap<>();
        teams = new TeamInfo[cnt];
        for (int idx = 0; idx < cnt; ++idx) {
            String name = in.readString();
            team_to_index.put(name, idx);
            TeamInfo ti = new TeamInfo();
            ti.name = name;
            ti.wins = in.readInt();
            ti.losses = in.readInt();
            ti.remaining = in.readInt();
            ti.games = new int[cnt];
            for (int j = 0; j < cnt; ++j) {
                ti.games[j] = in.readInt();
            }
            teams[idx] = ti;
        }
    }

    private void findTriviallyEliminated() {
        TreeMap<Integer, String> curr_wins = new TreeMap<>();
        for (int team_idx = 0; team_idx < teams.length; ++team_idx) {
            TeamInfo ti = teams[team_idx];
            curr_wins.put(ti.wins, ti.name);
        }
        for (int team_idx = 0; team_idx < teams.length; ++team_idx) {
            TeamInfo ti = teams[team_idx];
            int max_wins = ti.wins + ti.remaining;
            Map.Entry<Integer, String> e = curr_wins.ceilingEntry(max_wins + 1);
            if (e != null) {
                HashSet<String> cert = new HashSet<>();
                cert.add(e.getValue());
                elimination.put(ti.name, cert);
            }
        }
    }

    private class Edge {
        public Node src;
        public Node dst;
        public double flow;
        public double capacity;
        public boolean forward;
        public Edge(Node src, Node dst, double capacity, boolean forward) {
            this.src = src;
            this.dst = dst;
            flow = 0;
            this.capacity = capacity;
            this.forward = forward;
        }
    }

    private class Node {
        public String name;
        public int id;
        public HashMap<Integer, Edge> adj;
        Node(String name) {
            this.name = name;
            id = getId(name);
            adj = new HashMap<>();
        }
        public void addEdge(Node dst, double capacity, boolean forward) {
            adj.put(dst.id, new Edge(this, dst, capacity, forward));
        }
    };

    private int next_node_id;
    private HashMap<String, Integer> node_name_to_id;
    private HashMap<String, Node> nodes;

    private int getId(String name) {
        Integer node_id = node_name_to_id.get(name);
        if (node_id != null) {
            return node_id;
        }
        node_name_to_id.put(name, next_node_id);
        return next_node_id++;
    }

    private Node getNode(String name) {
        Node node = nodes.get(name);
        if (node == null) {
            node = new Node(name);
            nodes.put(name, node);
        }
        return node;
    }

    private int nodeCount() {
        return next_node_id;
    }

    private void linkNodes(Node node1, Node node2, double capacity) {
        node1.addEdge(node2, capacity, true);
        node2.addEdge(node1, capacity, false);
    }

    private void buildFlowNetwork(int team_idx) {
        next_node_id = 0;
        nodes = new HashMap<>();
        node_name_to_id = new HashMap<>();
        Node source_node = getNode("s");
        for (int idx1 = 0; idx1 < teams.length; ++idx1) {
            if (idx1 == team_idx) {
                continue;
            }
            for (int idx2 = idx1 + 1; idx2 < teams.length; ++idx2) {
                if (idx2 == team_idx) {
                    continue;
                }
                Node game_node = getNode(Integer.toString(idx1) + "-" + Integer.toString(idx2));
                linkNodes(source_node, game_node, teams[idx1].games[idx2]);
                linkNodes(game_node, getNode(Integer.toString(idx1)), Double.POSITIVE_INFINITY);
                linkNodes(game_node, getNode(Integer.toString(idx2)), Double.POSITIVE_INFINITY);
            }
        }
        Node target_node = getNode("t");
        for (int idx = 0; idx < teams.length; ++idx) {
            if (idx != team_idx) {
                double win_cap = teams[team_idx].wins + teams[team_idx].remaining - teams[idx].wins;
                linkNodes(getNode(Integer.toString(idx)), target_node, win_cap);
            }
        }
    }

    private Edge[] edge_to;

    private boolean findAugmentingPath() {
        boolean[] visited = new boolean[nodeCount()];
        Queue<Node> q = new Queue<>();
        Node source_node = getNode("s");
        q.enqueue(source_node);
        visited[source_node.id] = true;
        Node target_node = getNode("t");
        while (!q.isEmpty()) {
            Node node = q.dequeue();
            if (node == target_node) {
                return true;
            }
            for (Edge edge : node.adj.values()) {
                if (!visited[edge.dst.id]) {
                    if (edge.forward) {
                        if (edge.flow < edge.capacity) {
                            q.enqueue(edge.dst);
                            edge_to[edge.dst.id] = edge;
                            visited[edge.dst.id] = true;
                        }
                    } else {
                        if (edge.flow > 0) {
                            q.enqueue(edge.dst);
                            edge_to[edge.dst.id] = edge;
                            visited[edge.dst.id] = true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void fordFulkerson() {
        edge_to = new Edge[nodeCount()];
        Node source_node = getNode("s");
        Node target_node = getNode("t");
        double bottleneck = Double.POSITIVE_INFINITY;
        while (findAugmentingPath()) {
            Node node = target_node;
            while (node != source_node) {
                Edge edge = edge_to[node.id];
                if (edge.forward) {
                    bottleneck = Math.min(bottleneck, edge.capacity - edge.flow);
                } else {
                    bottleneck = Math.min(bottleneck, edge.flow);
                }
                node = edge.src;
            }
            node = target_node;
            while (node != source_node) {
                Edge edge = edge_to[node.id];
                Edge reverse_edge = node.adj.get(edge.src.id);
                if (edge.forward) {
                    edge.flow += bottleneck;
                    reverse_edge.flow += bottleneck;
                } else {
                    edge.flow -= bottleneck;
                    reverse_edge.flow -= bottleneck;
                }
                node = edge.src;
            }
        }
    }

    private boolean[] in_cut;

    private void computeMinCut() {
        in_cut = new boolean[nodeCount()];
        Queue<Node> q = new Queue<>();
        Node source_node = getNode("s");
        q.enqueue(source_node);
        in_cut[source_node.id] = true;
        while (!q.isEmpty()) {
            Node node = q.dequeue();
            for (Edge edge : node.adj.values()) {
                if (!in_cut[edge.dst.id]) {
                    if (edge.forward) {
                        if (edge.flow < edge.capacity) {
                            q.enqueue(edge.dst);
                            in_cut[edge.dst.id] = true;
                        }
                    } else {
                        if (edge.flow > 0) {
                            q.enqueue(edge.dst);
                            in_cut[edge.dst.id] = true;
                        }
                    }
                }
            }
        }
    }

    private void printFlowNetwork() {
        for (Node node : nodes.values()) {
            StdOut.println(node.name);
            for (Edge edge : node.adj.values()) {
                if (edge.forward) {
                    StdOut.println("->" + edge.dst.name + " " + edge.flow + "/" + edge.capacity);
                }
            }
        }
    }

    private void printCertificateOfElimination(int team_id) {
        HashSet<String> cert = elimination.get(teams[team_id].name);
        double wins = 0;
        double remaining = 0;
        for (String name1 : cert) {
            int idx1 = team_to_index.get(name1);
            wins += teams[idx1].wins;
            for (String name2 : cert) {
                int idx2 = team_to_index.get(name2);
                remaining += teams[idx1].games[idx2];
            }
        }
        double alpha = (wins + remaining / 2) / cert.size();
        double max_wins = teams[team_id].wins + teams[team_id].remaining;
        StdOut.print(teams[team_id].name + " alpha = " + alpha + " > max. wins = " + max_wins);
        StdOut.println(alpha > max_wins ? " is correct" : " is WRONG!");
    }

    public MyBaseballElimination(String filename) {      // create a baseball division from given filename in format specified below
        elimination = new HashMap<>();
        loadData(filename);
        findTriviallyEliminated();
        for (int team_id = 0; team_id < teams.length; ++team_id) {
            if (!isEliminated(teams[team_id].name)) {
                buildFlowNetwork(team_id);
                fordFulkerson();

                boolean is_eliminated = false;
                for (Edge edge : getNode("s").adj.values()) {
                    if (edge.flow < edge.capacity) {
                        is_eliminated = true;
                        break;
                    }
                }

                if (is_eliminated) {
                    computeMinCut();
                    HashSet<String> cert = new HashSet<>();
                    for (int team_idx = 0; team_idx < teams.length; ++team_idx) {
                        if (team_idx != team_id) {
                            if (in_cut[getNode(Integer.toString(team_idx)).id]) {
                                cert.add(teams[team_idx].name);
                            }
                        }
                    }
                    elimination.put(teams[team_id].name, cert);

                    // printFlowNetwork();
                    // printCertificateOfElimination(team_id);
                }
            }
        }
    }

    public int numberOfTeams() {                       // number of teams
        return teams.length;
    }

    public Iterable<String> teams() {                  // all teams
        return team_to_index.keySet();
    }

    private void checkTeamIsValid(String team) {
        if (!team_to_index.containsKey(team)) {
            throw new IllegalArgumentException();
        }
    }

    public int wins(String team) {                     // number of wins for given team
        checkTeamIsValid(team);
        return teams[team_to_index.get(team)].wins;
    }

    public int losses(String team) {                   // number of losses for given team
        checkTeamIsValid(team);
        return teams[team_to_index.get(team)].losses;
    }

    public int remaining(String team) {                // number of remaining games for given team
        checkTeamIsValid(team);
        return teams[team_to_index.get(team)].remaining;
    }

    public int against(String team1, String team2) {   // number of remaining games between team1 and team2
        checkTeamIsValid(team1);
        checkTeamIsValid(team2);
        return teams[team_to_index.get(team1)].games[team_to_index.get(team2)];
    }

    public boolean isEliminated(String team) {         // is given team eliminated?
        checkTeamIsValid(team);
        return elimination.containsKey(team);
    }

    public Iterable<String> certificateOfElimination(String team) { // subset R of teams that eliminates given team; null if not eliminated
        checkTeamIsValid(team);
        return elimination.get(team);
    }

    public static void main(String[] args) {
        Stopwatch timer = new Stopwatch();
        MyBaseballElimination division = new MyBaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
        StdOut.println(timer.elapsedTime());
    }    
}
