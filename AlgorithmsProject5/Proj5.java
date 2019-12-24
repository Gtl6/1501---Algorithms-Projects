import java.io.*;
import java.util.Scanner;

public class Proj5 {

    static Graph airport;
    static File file;

    static void cleanup(){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(airport.toSaveString());
            bw.close();
        }
        catch(IOException er){
            System.out.println("Had an error on the write.");
        }
    }

    static int[] from_to(Scanner scan){
        // At this point, the scanner will be holding the words "from CITY to CITY"
        scan.next();
        String city1 = scan.next();
        scan.next();
        String city2 = scan.next();

        int c1 = airport.get_num(city1);
        int c2 = airport.get_num(city2);
        return new int[]{c1, c2};
    }

    static String path_to_names(String path){
        Scanner myscanner = new Scanner(path).useDelimiter(",");
        StringBuilder sb = new StringBuilder();
        while(myscanner.hasNext()){
            String nsInt = myscanner.next();
            int nInt = Integer.parseInt(nsInt);
            sb.append(airport.get_name(nInt) + ", ");
        }

        return sb.toString();
    }


    public static void main(String[] args)
    {
        if(args.length == 0)
        {
            System.out.println("Needs a input text file.");
            return;
        }

        file = new File(args[0]);
        GraphReader airportreader = new GraphReader(file);
        airport = airportreader.readGraph();
        String command = "";
        System.out.println("Welcome to Panther airlines. Type your commands into the terminal below.");
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        Scanner myscanner;
        boolean worked;
        boolean recalculate = true;
        int cmdlen;
        DijkstraAPSP myAPSP = new DijkstraAPSP(airport);
        PrimMST myPrim = new PrimMST(airport);

        try {
            while (true) {
                System.out.print("\ncommand > ");
                System.out.flush();
                worked = false;
                command = reader.readLine();
                cmdlen = command.length();
                for(int i = 0; i < cmdlen + 10; i++) System.out.print("-");
                System.out.println();

                if(recalculate){
                    myAPSP = new DijkstraAPSP(airport);
                    myPrim = new PrimMST(airport);
                    recalculate = false;
                }

                if(cmdlen > 18 && command.substring(0, 18).equals("SHORTEST COST PATH")){
                    // SHORTEST COST PATH FROM CITY1 TO CITY2
                    worked = true;
                    myscanner = new Scanner(command);
                    myscanner.next();
                    myscanner.next();
                    myscanner.next(); // Eat S.C.P.
                    int[] fromto = from_to(myscanner);
                    String[] shorts = myAPSP.findAPSP(fromto[0], true);
                    for(int i = 0; i < shorts.length; i++){
                        // Checking if the path contains the to
                        if(shorts[i].contains(((Integer)fromto[1]).toString())){
                            String yourpath = shorts[i];
                            System.out.println("Your cheapest path is below: $" + myAPSP.findPathLength(yourpath, true));
                            System.out.println(path_to_names(yourpath));
                        }
                    }
                }
                else if(cmdlen > 22 && command.substring(0, 22).equals("SHORTEST DISTANCE PATH")){
                    // SHORTEST DISTANCE PATH FROM CITY1 TO CITY2
                    worked = true;
                    myscanner = new Scanner(command);
                    myscanner.next();
                    myscanner.next();
                    myscanner.next(); // Eat S.C.P.
                    int[] fromto = from_to(myscanner);
                    String[] shorts = myAPSP.findAPSP(fromto[0], false);
                    for(int i = 0; i < shorts.length; i++){
                        // Checking if the path contains the to
                        if(shorts[i].contains(((Integer)fromto[1]).toString())){
                            String yourpath = shorts[i];
                            System.out.println("Your shortest path is below: " + myAPSP.findPathLength(yourpath, false) + " miles");
                            System.out.println(path_to_names(yourpath));
                        }
                    }
                }
                else if(cmdlen > 11 && command.substring(0, 11).equals("FEWEST HOPS")){
                    // FEWEST HOPS FROM CITY1 TO CITY2
                    worked = true;
                    myscanner = new Scanner(command);
                    myscanner.next();
                    myscanner.next(); // Get rid of FEWEST and HOPS
                    int[] fromto = from_to(myscanner);
                    Integer[] hops = airport.minHopsarray(fromto[0], fromto[1]);

                    if(hops == null){
                        System.out.println("Sorry, we couldn't find a path between your cities!");
                        continue;
                    }

                    System.out.println("Fewest hops is " + (hops.length - 1));
                    System.out.println("Path (in reverse order):");
                    for(int i = 0; i < hops.length; i++){
                        System.out.print(airport.get_name(hops[i]) + ", ");
                    }
                    System.out.println();
                }
                else if(cmdlen > 17 && command.substring(0, 17).equals("ALL PATHS OF COST")){
                    // ALL PATHS OF COST (COST) OR LESS
                    myscanner = new Scanner(command);
                    myscanner.next();
                    myscanner.next();
                    myscanner.next();
                    myscanner.next(); // Eat ALL PATHS OF COST
                    int max = myscanner.nextInt();
                    String paths = airport.below_paths(max);
                    System.out.println("Your list of paths:");
                    System.out.println(paths);

                }
                else if(cmdlen >= 21 && command.substring(0, 21).equals("MINIMUM SPANNING TREE")){
                    // MINIMUM SPANNING TREE
                    worked = true;
                    System.out.println("Here is your Minimum Spanning Tree:");
                    System.out.println(myPrim.toString());
                }
                else if(cmdlen >= 8 && command.substring(0, 8).equals("SHOW ALL")){
                    // SHOW ALL
                    System.out.println(airport.niceToString());
                    worked = true;
                }
                else if(cmdlen > 7 && command.substring(0, 7).equals("ADD NEW")){
                    // ADD NEW FROM CITY1 TO CITY2 PRICE (PRICE) DISTANCE (DISTANCE)
                    // e.g. ADD NEW FROM London TO Paris PRICE 250.00 DISTANCE 300
                    recalculate = true;
                    myscanner = new Scanner(command);
                    myscanner.next();
                    myscanner.next(); // Eat ADD NEW
                    int[] fromto = from_to(myscanner);
                    double dprice = myscanner.nextDouble();
                    int price = (int) dprice;
                    int distance = myscanner.nextInt();
                    airport.add_edge(fromto[0], fromto[1], price, true);
                    airport.add_edge(fromto[0], fromto[1], distance, false);
                }
                else if(cmdlen > 6 && command.substring(0, 6).equals("REMOVE")){
                    // REMOVE FROM CITY1 TO CITY2
                    recalculate = true;
                    myscanner = new Scanner(command);
                    myscanner.next(); // EAT REMOVE
                    int[] fromto = from_to(myscanner);
                    airport.remove_edge(fromto[0], fromto[1]);
                }
                else if(cmdlen >= 4 && command.substring(0, 4).equals("QUIT")) {
                    // QUIT
                    cleanup();
                    break;
                }


                if(!worked){
                    System.out.println("Sorry, I didn't understand that.");
                }
            }
        }
        catch(IOException e){
            System.out.println("Got a weird issue from the Buffered Reader.");
        }

        System.out.println("Thank you for flying Panther Airlines!");
    }
}
