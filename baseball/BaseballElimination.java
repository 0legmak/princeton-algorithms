import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;

public class BaseballElimination {

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

    private int next_node_id;
    private HashMap<String, Integer> node_name_to_id;

    private int getId(String name) {
        Integer node_id = node_name_to_id.get(name);
        if (node_id != null) {
            return node_id;
        }
        node_name_to_id.put(name, next_node_id);
        return next_node_id++;
    }

    private int nodeCount() {
        final int n = teams.length;
        return 1 + (n - 1) * (n - 2) / 2 + (n - 1) + 1;
    }

    private FlowNetwork buildFlowNetwork(int team_idx) {
        FlowNetwork fn = new FlowNetwork(nodeCount());
        next_node_id = 0;
        node_name_to_id = new HashMap<>();
        int source_node_id = getId("s");
        for (int idx1 = 0; idx1 < teams.length; ++idx1) {
            if (idx1 == team_idx) {
                continue;
            }
            for (int idx2 = idx1 + 1; idx2 < teams.length; ++idx2) {
                if (idx2 == team_idx) {
                    continue;
                }
                int game_node_id = getId(Integer.toString(idx1) + "-" + Integer.toString(idx2));
                fn.addEdge(new FlowEdge(source_node_id, game_node_id, teams[idx1].games[idx2]));
                fn.addEdge(new FlowEdge(game_node_id, getId(Integer.toString(idx1)), Double.POSITIVE_INFINITY));
                fn.addEdge(new FlowEdge(game_node_id, getId(Integer.toString(idx2)), Double.POSITIVE_INFINITY));
            }
        }
        int target_node_id = getId("t");
        for (int idx = 0; idx < teams.length; ++idx) {
            if (idx != team_idx) {
                double win_cap = teams[team_idx].wins + teams[team_idx].remaining - teams[idx].wins;
                fn.addEdge(new FlowEdge(getId(Integer.toString(idx)), target_node_id, win_cap));
            }
        }
        return fn;
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

    public BaseballElimination(String filename) {      // create a baseball division from given filename in format specified below
        elimination = new HashMap<>();
        loadData(filename);
        findTriviallyEliminated();
        for (int team_id = 0; team_id < teams.length; ++team_id) {
            if (!isEliminated(teams[team_id].name)) {
                FlowNetwork flowNetwork = buildFlowNetwork(team_id);
                FordFulkerson ff = new FordFulkerson(flowNetwork, getId("s"), getId("t"));
                boolean is_eliminated = false;
                for (FlowEdge edge : flowNetwork.adj(getId("s"))) {
                    if (edge.flow() < edge.capacity()) {
                        is_eliminated = true;
                        break;
                    }
                }
                if (is_eliminated) {
                    HashSet<String> cert = new HashSet<>();
                    for (int team_idx = 0; team_idx < teams.length; ++team_idx) {
                        if (team_idx != team_id) {
                            if (ff.inCut(getId(Integer.toString(team_idx)))) {
                                cert.add(teams[team_idx].name);
                            }
                        }
                    }
                    elimination.put(teams[team_id].name, cert);

                    printCertificateOfElimination(team_id);
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
        BaseballElimination division = new BaseballElimination(args[0]);
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
