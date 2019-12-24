import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DijkstraAPSP
{
	private final Graph map;
	int[] distances;
	int[] via;

	public DijkstraAPSP(Graph given)
	{
		map = given;
        distances = new int[map.get_n()];
        Arrays.fill(distances, -1);
        via = new int[map.get_n()];
        Arrays.fill(via, -1);
	}

	//returns all shortest paths from the start vertex
	//from the start vertex, each index represents the vertex number of the end of the path
	//each entry represents a string that represents the shortest path (of your design)
	//the array space is null if no path exists to the endpoint vertex
	public String[] findAPSP(final int startVertex, boolean pricearray)
	{
		//Check if start vertex even exists in the graph
		if(!map.is_connected(startVertex, pricearray)) return new String[0];

		int n = map.get_n();
        ArrayList<Path> all_the_paths = new ArrayList<Path>(n);
        ArrayList<Path> good_paths = new ArrayList<Path>(n);

        //algorithm and path storage
        int[] visited = new int[n];
        visited[startVertex] = 1;
        ArrayList<Path> starterPaths = map.paths_from(startVertex, pricearray);
        all_the_paths.addAll(starterPaths);
        Path leastPath;
        boolean needToSort = true;
        while(all_the_paths.size() > 0){
            if(needToSort) {
                Collections.sort(all_the_paths);
                needToSort = false;
            }
            leastPath = all_the_paths.get(0);
            all_the_paths.remove(leastPath);

            int thisNode = leastPath.getLast();
            int thisDist = leastPath.getDistance();

            // If we've already been here, forget it
            if(visited[thisNode] == 1) continue;

            // Compare this path to the one in place
            if(thisDist < distances[thisNode] || distances[thisNode] == -1){
                needToSort = true;
                // Update distance and where it came from
                distances[thisNode] = thisDist;
                via[thisNode] = leastPath.getFromtoLast();
                good_paths.add(leastPath);

                // Then add the new paths
                ArrayList<Path> possiblePaths = map.paths_from(thisNode, pricearray);
                ArrayList<Path> newpaths = new ArrayList<>();
                for(Path p: possiblePaths){
                    int to = p.getLast();
                    // If the node is already in the path, don't add it (loops are a no no)
                    if(!leastPath.nodeInPath(to)){
                        int newDist = p.getDistance();
                        int newNode = to;
                        Path toAdd = leastPath.newPathWithNodeOnEnd(newNode, newDist);
                        newpaths.add(toAdd);
                    }
                }
                all_the_paths.addAll(newpaths);
            } // Otherwise we just keep going
        }

        // Just building the return vehicle
        String[] returner = new String[good_paths.size()];
        int i = 0;
        for(Path p: good_paths) {
            returner[i++] = p.toString();
        }
		return returner;
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
            int dist = map.get_weight(from, to, pricearray);
            // If the path doesn't exist, -1
            if(dist == -1) return -1;
            // Otherwise, add that in
            len += dist;

            i++;
        }
        return len;
	}
}