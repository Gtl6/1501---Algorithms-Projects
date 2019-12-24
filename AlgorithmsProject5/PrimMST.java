import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class PrimMST
{
	Graph mygraph;
	Graph minSpanTree;

	public PrimMST(Graph myg)
	{
		mygraph = myg;
		minSpanTree = findMST(false);
		minSpanTree.setNames(mygraph.getNames());
		minSpanTree.setNum_to_name(mygraph.getNum_to_name());
	}

	public final Graph findMST(boolean pricearray)
	{
	    int n = mygraph.get_n();

	    // Created the new Graph
        int[] visited = new int[n];
	    int[][] returnerarr = new int[n][n];
		for(int i = 0; i < n; i++){
		    for(int j = 0; j < n; j++){
		        returnerarr[i][j] = -1;
            }
            visited[i] = -1;
        }
        Graph returner = new Graph(n);

		// algorithm
        // Just going to get it from the 0 node always. Since it's arbitrary.
        int visitednum = 1;
		visited[1] = 1;
		int oldy = 1;
		int[] connections = mygraph.get_edges(1, pricearray);
        ArrayList<DirectedEdge> myEdges = new ArrayList<DirectedEdge>(n * n);

        // Add the zero edges
        for(int i = 1; i < n; i++) {
            if(connections[i] != -1)
                myEdges.add(new DirectedEdge(1, i, connections[i]));
        }
        Collections.sort(myEdges);

        // Assuming here that you can get to every node
		while(visitednum < n && !myEdges.isEmpty()){
            // First pick the node we're traveling to, mark it as visited, and add it to the graph
            DirectedEdge lowest = myEdges.get(0);
            int myv = lowest.getTo();
            visited[myv] = 1;
            oldy = lowest.getFrom();
            returner.add_edge(oldy, myv, lowest.getWeight(), pricearray);

            // Add all of its edges to our ArrayList
            connections = mygraph.get_edges(myv, pricearray);
            for(int i = 1; i < n; i++) {
                if (connections[i] != -1 && visited[i] != 1) {
                    myEdges.add(new DirectedEdge(myv, i, connections[i]));
                }
            }
            ArrayList<DirectedEdge> toRemove = new ArrayList<>();

            // Remove all the edges that contain it as a to
            for(DirectedEdge e: myEdges){
                if(e.getTo() == myv) toRemove.add(e);
            }
            for(DirectedEdge e: toRemove){
                myEdges.remove(e);
            }

            // Sort our list and continue
            Collections.sort(myEdges);

		    visitednum ++;
        }

		//return tree
		return returner;
	}

	@Override
	public String toString()
	{
		return minSpanTree.niceToString();
	}
}