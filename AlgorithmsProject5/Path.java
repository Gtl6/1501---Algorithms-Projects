public class Path implements Comparable{
    private int distance;
    private int[] path;

    public Path(int d, int[] pth){
        path = pth;
        distance = d;
    }

    public Path(int d, String pth){
        distance = d;
        String[] nodes = pth.split(",");
        int[] pathes = new int[nodes.length];
        int i = 0;
        for(String s: nodes) pathes[i++] = Integer.parseInt(s);
        path = pathes;
    }

    @Override
    public String toString(){
        StringBuilder returner = new StringBuilder();

        for(int a: path){
            returner.append(a);
            returner.append(',');
        }

        return returner.substring(0, returner.length() - 1);
    }

    public int getLast() {return path[path.length - 1];}
    public int getFromtoLast() {return path[path.length - 2];}

    public int getDistance() {return distance;}

    public boolean nodeInPath(int v) {
        for(int i: path)if(i == v) return true;
        return false;
    }

    public Path newPathWithNodeOnEnd(int n, int addedD){
        int newD = distance + addedD;
        int[] newPath = new int[path.length + 1];
        int j=0;
        for(int i: path) newPath[j++] = i;
        newPath[j] = n;
        return new Path(newD, newPath);
    }

    @Override
    public int compareTo(Object de) {
        return ((Integer)this.getDistance()).compareTo(((Path)de).getDistance());
    }
}
