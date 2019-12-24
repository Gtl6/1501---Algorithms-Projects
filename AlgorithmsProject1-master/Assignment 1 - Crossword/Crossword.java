import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Crossword {
    private static int solutionsfound = 0;
    private static MyDictionary mymydict = new MyDictionary();
    private static DLB mydlbdict = new DLB();
    private static DictInterface mydict;

    // The puzzle as a char array array
    // If we keep it a static variable, we can modify it with side effects, which will reduce the
    // overhead needed to pass it during the recursive calls (hopefully)
    private static char[][] puzzle;

    static public void main(String[] args){
        int N; // The grid will be N x N blocks

        if(args.length < 2){
            System.out.println("You need an argument referring to an input file, and choose DLB or MyDict!");
            System.exit(1);
        }

        try {
            // Read in the Dictionary
            File dictfile = new File(".\\InputData\\dict8.txt");
            BufferedReader br = new BufferedReader(new FileReader(dictfile));

            String ln;

            if(args[1].equals("DLB")){
                mydict = mydlbdict;
            }
            else if(args[1].equals("MyDict")){
                mydict = mymydict;
            }
            else{
                System.out.println("You didn't specify a dictionary correctly!");
                System.exit(1);
            }
            while((ln = br.readLine()) != null){
                mydict.add(ln);
            }

            // Read in the test file
            File testfile = new File(args[0]);
            br = new BufferedReader(new FileReader(testfile));

            N = Integer.parseInt(br.readLine());
            puzzle = new char[N][N];
            for(int i = 0; i < N; i++){
                ln = br.readLine();
                String[] linelist = ln.split("");
                // I convert the list to a char array to as to be more memory efficient
                String s = "";
                for(String n:linelist)
                    s += n;
                char[] charlist = s.toCharArray();
                puzzle[i] = charlist;
            }

            printpuzzle();
            System.out.println();

            // Now we'll execute the algorithm
            puzzleGenerator(0, 0, N);
            if(solutionsfound == 0){
                System.out.println("You have given me an unsolvable puzzle. Dang.");
            }
            else{
                System.out.println("We found : " + Integer.toString(solutionsfound) + " solutions. Nice job!");
            }
        }
        catch(java.io.FileNotFoundException e){
            System.out.println("One of the paths is mixed up. Check the dict path (in the code) and the " +
                    "test file path (your input argument).");
            System.out.println(e);
            System.exit(1);
        }
        catch(java.io.IOException e){
            System.out.println("Something went wrong reading the file.");
            System.exit(2);
        }

    }

    // Prints a single array of chars
    private static void printarray(char[] titular){
        StringBuilder buffer = new StringBuilder();
        for(char c : titular){
            buffer.append(c);
            buffer.append(" ");
        }
        System.out.println(buffer);
    }

    // Prints an entire puzzle, using the above method to do the individual array prints
    private static void printpuzzle(){
        for(char[] ar : puzzle){
            printarray(ar);
        }
    }

    // Gives the next x position (so we don't go above N, cause you can't go off the board)
    private static int newx(int x, int N){
        return (x + 1) % N;
    }

    // Gives the next y position (cause if we rolled over the x, then y has to increment)
    private static int newy(int x, int y, int N){
        if(((x + 1) % N) == 0)
            return y + 1;
        else return y;
    }

    // This was basically duplicated code in puzzleGenerator
    // So I abstracted it away
    // At this point, we know that the square we're at is valid, so
    private static void checkend(int x, int y, int N){
        if(x == N - 1 && y == N - 1){
            if(solutionsfound % 1000 == 0) {
                System.out.println("Solution #" + Integer.toString(solutionsfound + 1) + " found:");
                printpuzzle();
                System.out.println();
            }
            solutionsfound++;
        }
        else {
            puzzleGenerator(newx(x, N), newy(x, y, N), N);
        }
    }

    // Gets the word (or partial) to the left of the current position for checking against the dictionary
    private static StringBuilder getxword(int x, int y){
        // If we're at an impassible character, we're good
        if( x == -1 || puzzle[y][x] == '-'){
            return new StringBuilder();
        }
        else{
            // Otherwise, append this char onto the existing string
            char thisspot = puzzle[y][x];
            return getxword(x - 1, y).append(thisspot);
        }
    }

    // I don't like how much of the below code is duplicated
    // Checks whether the horizontal word is legal
    private static boolean hworks(int x, int y, int N){
        StringBuilder xword = getxword(x, y);
        int wordtype = mydict.searchPrefix(xword);

        // If to the right is an impassible character (End of Puzzle or -)
        if(x == N - 1 || puzzle[y][x + 1] == '-'){
            // Then we need the word to be a word or a partial (2 or a 3)
            return (wordtype == 2 || wordtype == 3);
        }
        // Otherwise, it needs to be a partial (1 or 3)
        return (wordtype == 1 || wordtype == 3);
    }

    // Lotta annoying reduntant code here
    // Basically performs the same function as getxword, but up instead of left
    private static StringBuilder getyword(int x, int y){

        // If we're at an impassible character, we're good
        if(y == -1 || puzzle[y][x] == '-'){
            return new StringBuilder();
        }
        else{
            // Otherwise... well, you know the rest
            char thisspot = puzzle[y][x];
            return getyword(x, y - 1).append(thisspot);
        }
    }

    // Checks whether the vertical word is legal
    private static boolean yworks(int x, int y, int N){
        StringBuilder yword = getyword(x, y);
        int wordtype = mydict.searchPrefix(yword);

        // If below is an impassible character (End of Puzzle or -)
        if(y == N - 1 || puzzle[y + 1][x] == '-'){
            // Then it needs to be a word (a 2 or a 3)
            return (wordtype == 2 || wordtype == 3);
        }
        // Otherwise it needs to be a partial (1 or 3)
        return (wordtype == 1 || wordtype == 3);
    }

    private static boolean checkworks(int x, int y, int N){
        boolean xcheck = hworks(x, y, N);
        if(xcheck){
            boolean ycheck = yworks(x, y, N);
            if(ycheck){
                // Then we got a good letter, so we're in the gold
                // If you're at the last spot on the board, solid
                return true;
            }
            else return false;
        }
        else return false;
    }

    // Uses the board that you put together in the main function
    // Looks for all possible solutions using a given dictionary
    private static void puzzleGenerator(int x, int y, int N){
        char current = puzzle[y][x];
        // If it's an empty spot
        if(current == '+'){
            // Gotta try all the letters
            // Tbh I think it's hilarious you can do this
            for(char letter = 'a'; letter <= 'z'; letter++){
                // Gotta check if it works
                // And then just clean up
                puzzle[y][x] = letter;

                // Make sure the letter works
                if(checkworks(x, y, N)){
                    checkend(x, y, N);
                }
                // Otherwise, you just keep looping
            }
            // If none of them worked, gotta recurse
            puzzle[y][x] = '+';
        }
        // Else if it's a filled in spot, ignore it and move on
        else if(current == '-'){
            // If we're in the last spot, nice. Keep going.
            checkend(x, y, N);
        }
        // Otherwise, it's already a set letter, and you gotta work with it
        else{
            // Make sure the letter works
            if(checkworks(x, y, N)){
                checkend(x, y, N);
            }
        }
    }
}
