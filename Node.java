
import java.util.ArrayList;

/**
 *
 * @author christachiquita
 */
public class Node {
    
    /* ---Variables--- */
    private ArrayList<Vector> leaf;
    private Node left;
    private Node right;
    private Node parent;
    private Vector U;
    private int boundary;
    private double bvalue;
    private ArrayList<Vector> dataset;
    private ArrayList<DistanceVector> distvec;
    private int iteration;
    
    /* ---Constructors--- */
    public Node(){
        
    }
    
    //assign Vector of Leaves
    public Node (ArrayList<Vector> vecleaves){
        this.leaf = vecleaves;
    }
    
    //dataset = ArrayList of Item Vector
    //u = Unit Vector from random file
    //boundary = median index of sorted dataset
    public Node (ArrayList<Vector> dataset, Vector u, int boundary){
        this.dataset = dataset;
        this.U = u;
        this.boundary = boundary;
    }
    
    /* ---Getters and Setters--- */
    public void setIter(int i){
        this.iteration = i;
    }
    
    public void setDataSet(ArrayList<Vector> dataset){
        this.dataset = dataset;
    }
    
    public void setBValue(double b){
        this.bvalue = b;
    }
    
    public void setLeft(Node left){
        this.left = left;
    }
    
    public void setRight(Node right){
        this.right = right;
    }
    
    public void setLeaf(ArrayList<Vector> leaf){
        this.leaf = leaf;
    }
    
    public void setU(Vector u){
        this.U = u;
    }
    
    public void setBoundary(int b){
        this.boundary = b;
    }
    
    public ArrayList<Vector> getDataSet(){
        return this.dataset;
    }
    
    public Node getLeft(){
        return this.left;
    }
    
    public Node getRight(){
        return this.right;
    }
    
    public double getBValue(){
        return this.bvalue;
    }
    
    public int getIter(){
        return this.iteration;
    }
    
    public ArrayList<Vector> getLeaf(){
        return this.leaf;
    }
    
    public Vector getU(){
        return this.U;
    }
    
    public int getBoundary(){
        return this.boundary;
    }
    
    public ArrayList<DistanceVector> getDistVec(){
        return this.distvec;
    }
    
    //Assign Vectors with index<=boundary as Left Node
    public Node LeftS(ArrayList<Vector> S){
        ArrayList<Vector> l = new ArrayList<Vector>();
        for (int i=S.size()-1 ; i>=0; i--){
            if (i<=this.boundary){
                l.add(S.get(i));
            }
        }
        return new Node(l, getU(), getBoundary());
    }
    
    //Assign Vectors with index>boundary as Right Node
    public Node RightS(ArrayList<Vector> S){
        ArrayList<Vector> r = new ArrayList<Vector>();
        for (int i=S.size()-1 ; i>=0; i--){
            if (i>this.boundary){
                r.add(S.get(i));
            }
        }
        
        return new Node(r, getU(), getBoundary());
    }
    
    public void printLeaves(){
        for (int i=0; i<this.getLeaf().size(); i++){
            System.out.print("leaf-"+this.getLeaf().get(i).getID()+" :: ");
            this.getLeaf().get(i).PrintVector();
            System.out.print("\n");
        }
    }
    
    public void printDataSet(){
        for (int i=0; i<this.getDataSet().size(); i++){
            System.out.println("\nDataset - "+this.getDataSet().get(i).getID()+" : ");
            this.getDataSet().get(i).PrintVector();
            System.out.print("\n");
        }
    }
    
    //get Distance of all leaf nodes to query Vector and assign them to distvec, which is ArrayList of DistanceVector
    public void getDistance(Vector q){
        double dist=0;
        this.distvec = new ArrayList<DistanceVector>();
        if (this.getLeaf()!=null){
            for (int i =0; i<this.getLeaf().size(); i++){
                dist = this.getLeaf().get(i).calcDist(q);
                DistanceVector dv = new DistanceVector(this.getLeaf().get(i).getID(), dist);
                this.distvec.add(dv);
            }
        }
    }
    
    //FindNN Operation. q = query Vector, numOfNN = number of nearest neighbour that will be found
    public ArrayList<DistanceVector> getNN2 (Vector q, int numOfNN){
        ArrayList<DistanceVector> NNVectors = new ArrayList<DistanceVector>();
        if (this.getLeaf()!=null){
            this.getDistance(q);
            int[] nnidx = getMinDisVec(q, numOfNN);
            for (int i=0; i<this.distvec.size(); i++){
                NNVectors.add(new DistanceVector(this.distvec.get(i).getID(), this.distvec.get(i).getDistance()));
            }
        }
        else {
            Vector proj = q.Projection(this.getU());
            double projlen = proj.len(this.getU());
            double boundarylen = this.getBValue();
            if (projlen <= boundarylen){
                NNVectors = this.getLeft().getNN2(q, numOfNN);
            }
            else {
                NNVectors = this.getRight().getNN2(q, numOfNN);
            }
        }
        return NNVectors;
    }
    
    //return array of (int)Item ID (length = numOfNN) as the top nearest neighbour
    public int[] getMinDisVec(Vector q, int numOfNN){
        int minid[] = new int[numOfNN];
        int idx = 0;
        double min = 100000;
        ArrayList<DistanceVector> distveccp = this.distvec;
        this.getDistance(q);
        if (distveccp.size()<numOfNN){
            for (int i=0; i<distveccp.size(); i++){
                if (distveccp.get(i).getID() != (q.getID()-1)){
                    minid[i] = distveccp.get(i).getID();
                }
            }
            return minid;
        }
        
        int x = numOfNN;
        if (distveccp.size()<numOfNN){
            x = distveccp.size();
        }
        
        for (int n=0; n<x; n++){
            for (int i=0; i<distveccp.size(); i++){
                if (distveccp.get(i).getDistance()<min && distveccp.get(i).getID()!=(q.getID()-1)){
                    min = distveccp.get(i).getDistance();
                    minid[n] = distveccp.get(i).getID();
                    idx = i;
                }
            }
            if (idx<distveccp.size()){
                distveccp.remove(idx);
            }
            min = 100000;
        }
        return minid;
    }
    
}

/* by : CC*/

