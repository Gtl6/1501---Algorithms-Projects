public class DirectedEdge implements Comparable{
    private int from;
    private int to;
    private int weight;

    public DirectedEdge(int from, int to, int weight){
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public int getWeight() {return weight;}
    public int getTo() {return to;}
    public int getFrom() {return from;}

    @Override
    public int compareTo(Object de) {
        return ((Integer)this.weight).compareTo(((DirectedEdge)de).getWeight());
    }

    @Override
    public String toString(){
        return "From: " + from + " To: " + to + " w/ w: " + weight;
    }
}
