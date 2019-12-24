import java.util.*;

//Edges are undirected.
//-1 means no edge.
//no self-looping edges allowed
public class Graph
{
	// Just going to use the adjacency matrix, so... IDK what exactly we need to do
    private int[][] adjDist;
    private int[][] adjPrice;
    private HashMap<String, Integer> names;
    private String[] num_to_name;
    private int n;
    private ArrayDeque<Integer> BFSQueue = new ArrayDeque<>();
    private ArrayList<Integer> BFSvisited = new ArrayList<>();
    private ArrayList<String> DFSPaths;
    private int global_debt;

    public HashMap<String, Integer> getNames() {
        return names;
    }

    public String[] getNum_to_name() {
        return num_to_name;
    }

    public void setNames(HashMap<String, Integer> hm){
        names = hm;
    }

    public void setNum_to_name(String[] nm){
        num_to_name = nm;
    }

    public Graph(int n){
        this.adjDist = new int[n][n];
        this.adjPrice = new int[n][n];

        for(int i = 0; i < n; i++){
            Arrays.fill(adjDist[i], -1);
            Arrays.fill(adjPrice[i], -1);
        }

        this.num_to_name = new String[n];
        this.names = new HashMap<String, Integer>(n);
        this.n = n;
    }

    public String get_name(int num){
        if(num < 0 || num > n) return null;
        return num_to_name[num];
    }

    public Integer[] minHopsarray(int from, int to){
        BFSvisited = new ArrayList<>(n);
        BFSQueue = new ArrayDeque<>(n);
        int[] fromwhere = new int[n];
        Arrays.fill(fromwhere, -1);
        if(from == to) return new Integer[]{to};

        BFSvisited.add(from);
        BFSQueue.add(from);
        boolean found = false;

        while(!BFSQueue.isEmpty()){
            int front = BFSQueue.removeFirst();
            int[] edgearray = get_edges(front, false);

            for(int i = 0; i < n; i++){
                int got = edgearray[i];
                if(got != -1 && !BFSvisited.contains(i)){
                    BFSvisited.add(i);
                    BFSQueue.add(i);
                    fromwhere[i] = front;

                    if(i == to){
                        BFSQueue.clear();
                        found = true;
                        break;
                    }
                }
            }
        }

        if(!found) return null;
        int going = to;
        ArrayList<Integer> hops = new ArrayList<Integer>();
        while(going != from){
            hops.add(going);
            going = fromwhere[going];
        }
        hops.add(going);

        return hops.toArray(new Integer[hops.size()]);
    }


    public void add_city(String city, int num){
        names.put(city, num);
        num_to_name[num] = city;
    }

    public int get_num(String city){
        return names.get(city);
    }

    public boolean is_connected(int v, boolean pricearray){
        int[] conns = get_edges(v, pricearray);
        for(int i: conns) if(i != -1) return true;
        return false;
    }

    // get_edges returns the edges connected to the node
    // asks whether you want the price array or the distance array
    public int[] get_edges(int v, boolean pricearray){
        if(pricearray) return adjPrice[v];
        else return adjDist[v];
    }

    // Get weight returns the weight of the two nodes. Not a directed graph, so either way works
    public int get_weight(int v1, int v2, boolean pricearray){
        if(pricearray) return adjPrice[v1][v2];
        else return adjDist[v1][v2];
    }

    // For when you want to ADD a connection. God knows why.
    public void add_edge(int v1, int v2, int w, boolean pricearray){
        if(pricearray) {
            adjPrice[v1][v2] = w;
            adjPrice[v2][v1] = w;
        }
        else{
            adjDist[v1][v2] = w;
            adjDist[v2][v1] = w;
        }
    }

    // For when you want to REMOVE a connection. Much more manageable
    public void remove_edge(int v1, int v2) {
        adjPrice[v1][v2] = -1;
        adjPrice[v2][v1] = -1;
        adjDist[v1][v2] = -1;
        adjDist[v2][v1] = -1;
    }

    // Just gives you back the array. For those "oh, screw it" moments, when you really just want the underlying.
    public int[][] screw_it(boolean pricearray){
        if(pricearray) return adjPrice;
        else return adjDist;
    }

    public int get_n(){
        return n;
    }

    public ArrayList<Path> paths_from(int v, boolean pricearray){
        int[] underlying = get_edges(v, pricearray);
        ArrayList<Path> paths = new ArrayList<>();
        for(int i = 0; i < underlying.length; i++){
            if(underlying[i] != -1){
                paths.add(new Path(underlying[i], new int[]{v, i}));
            }
        }
        return paths;
    }

    public String toString(boolean pricearray)
    {
        // Just copied this from PrimMST, so it makes a few weird function calls
        // But I mean, screw it, it works.
        StringBuilder returner = new StringBuilder();
        returner.append("Here's your graph:\n");
        Integer n = get_n();
        int[][] mydata = screw_it(pricearray);

        returner.append(n.toString());
        returner.append('\n');
        for(int i = 0; i < n; i++){
            StringBuilder line = new StringBuilder();
            for(int j = 0; j < n; j++){
                Integer num = mydata[i][j];
                line.append(num);
                line.append(",");
            }
            line.deleteCharAt(line.length() - 1);
            line.append('\n');
            returner.append(line);
        }

        return returner.toString();
    }

    @Override
    public String toString(){
        StringBuilder returner = new StringBuilder();
        returner.append("Here's the price array.\n");
        returner.append(toString(true));
        returner.append("\nAnd here's the distance array.\n");
        returner.append(toString(false));
        return returner.toString();
    }

    public String niceToString(){
        StringBuilder returner = new StringBuilder();
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(adjPrice[i][j] != -1 && adjDist[i][j] != -1){
                    returner.append("From " + num_to_name[i] + " to " + num_to_name[j] + " the cost is: ");
                    returner.append("" + adjPrice[i][j] + ".00  and the distance is " + adjDist[i][j] + " miles.\n");
                }
                else if(adjDist[i][j] != -1){
                    returner.append("From " + num_to_name[i] + " to " + num_to_name[j] + " the distance is " + adjDist[i][j] + " miles.\n");
                }

            }
        }

        return returner.toString();
    }

    public String toSaveString(){
        StringBuilder sb = new StringBuilder();
        sb.append((n - 1) + "\n");
        for(int i = 1; i < n; i++){
            sb.append(get_name(i) + "\n");
        }
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(adjDist[i][j] != -1 && j >= i){
                    sb.append(i + " " + j + " " + adjDist[i][j] + " " + adjPrice[i][j] + ".00\n");
                }
            }
        }

        String s = sb.toString();
        return s.substring(0, s.length() - 1);
    }

    //returns the length of the path from the given String representing a path (comma-separated)
    //returns -1 if it is not a valid path
    public int findPathLength(final String path, boolean pricearray)
    {
        String[] csv = path.split(",");
        if(path.length() < 2) return -1;
        int len = 0;
        int i = 1;

        while(i < csv.length){
            String froms = csv[i - 1];
            String tos = csv[i];
            int from = Integer.parseInt(froms);
            int to = Integer.parseInt(tos);
            int dist = get_weight(from, to, pricearray);
            // If the path doesn't exist, -1
            if(dist == -1) return -1;
            // Otherwise, add that in
            len += dist;

            i++;
        }
        return len;
    }

    private String ArrayListtoString(int max, ArrayList<Integer> ar){
        StringBuilder sb = new StringBuilder();
        sb.append("Cost: " + (global_debt - max) + " Path: ");
        for(int i = 0; i < ar.size(); i++){
            sb.append(get_name(ar.get(i)) + ",");
        }
        String returner = sb.toString().substring(0, sb.length() - 1);
        return returner;
    }

    private void recursive_paths(int v, int max, ArrayList<Integer> prev){
        if(max <= 0) return;
        prev.add(v);
        if(prev.size() > 1)
            DFSPaths.add(ArrayListtoString(max, prev));

        int[] edges = get_edges(v, true);
        for(int i = 0; i < n; i++){
            int edge = edges[i];
            if(edge != -1 && !prev.contains(i)){
                recursive_paths(i, max - edge, prev);
            }
        }

        prev.remove((Object)v);
    }

    public String below_paths(int max){
        DFSPaths = new ArrayList<String>();
        global_debt = max;
        for(int i = 0; i < n; i++)
            recursive_paths(i, max, new ArrayList<Integer>());

        StringBuilder sb = new StringBuilder();
        for(String s: DFSPaths)
            sb.append(s + "\n");

        return sb.toString();
    }
}