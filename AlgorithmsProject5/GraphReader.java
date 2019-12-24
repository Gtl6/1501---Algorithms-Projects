import java.io.*;
import java.util.Scanner;

public class GraphReader
{
	private final File graphFile;

	public GraphReader(final String filename)
	{
		this(new File(filename));
	}

	public GraphReader(final File file)
	{
		if(file == null || !file.exists() || !file.isFile())
		{
			throw new IllegalArgumentException("Invalid file.");
		}
		this.graphFile = file;
	}

	public Graph readGraph() {
	    Graph returner;

	    try {
            BufferedReader br = new BufferedReader(new FileReader(graphFile));
            String ns = br.readLine();
            int n = Integer.parseInt(ns) + 1;
            returner = new Graph(n);
            // Read in the city names
            for(int i = 1; i < n; i++){
                ns = br.readLine();
                returner.add_city(ns, i);
            }
            // Read in the city path prices
            while((ns = br.readLine()) != null){
                Scanner strscn = new Scanner(ns);
                int from = strscn.nextInt();
                int to = strscn.nextInt();
                int dist = strscn.nextInt();
                double priced = strscn.nextDouble();
                int price = (int) priced;

                returner.add_edge(from, to, dist, false);
                returner.add_edge(from, to, price, true);
            }

            br.close();
        }
        catch(FileNotFoundException e){
	        System.out.println("Invalid file.");
	        returner = null;
	        System.exit(1);
        }
        catch(IOException e2){
	        System.out.println("Had an issue reading the file.");
	        returner = null;
	        System.exit(2);
        }

        return returner;
	}
}