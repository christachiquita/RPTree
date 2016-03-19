
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author christachiquita
 */
public class Tree {
    
    private int n; //number of items to be indexed (m in SGD class)
    private int d; //dimensionality of the points (f in SGD class)
    private int MSOL; //MaxSizeOfLeaf : maximum number of points that you can put in a leaf node of a RP-Tree
    private String DataFile; //path of the file that contains the points that need to be indexed
    private String RandGFile; //RandomGaussianFile : path of the file that contains a sequence of floating numbers which are randomly sampled from the Standard Gaussian distribution
    private String QueryFile; //path of the file that contains the queries
    private Node root;
    private Matrix itemsMatrix;
    private double[] RandGArray;
    private int[] QArray;
    private ArrayList<Vector> S;
    
    public void setS(ArrayList<Vector> v){
        this.S = v;
    }
    
    public ArrayList<Vector> getS(){
        return this.S;
    }
    
    public void setN(int n){
        this.n = n;
    }
    
    public void setD(int d){
        this.d = d;
    }
    
    public void setMSOL(int max){
        this.MSOL = max;
    }
    
    public void setDataFile(String df){
        this.DataFile = df;
    }
    
    public void setRandGFile(String gfile){
        this.RandGFile = gfile;
    }
    
    public void setQFile(String qf){
        this.QueryFile = qf;
    }
    
    public void setRoot(Node root){
        this.root = root;
    }
    
    public void setItemsMat(Matrix mat){
        this.itemsMatrix = mat;
    }
    
    public void setRandGArray(double[] randg){
        this.RandGArray = randg;
    }
    
    public void setQArray(int[] qarr){
        this.QArray = qarr;
    }
    
    public String getDataFile(){
        return this.DataFile;
    }
    
    public String getQueryFile(){
        return this.QueryFile;
    }
    
    public double[] getRandGArray(){
        return this.RandGArray;
    }
    
    public int[] getQArray(){
        return this.QArray;
    }
    
    public int getN(){
        return this.n;
    }
    
    public int getD(){
        return this.d;
    }
    
    public int getMSOL(){
        return this.MSOL;
    }
    
    public Node getRoot(){
        return this.root;
    }
    
    public Matrix getItemsMat(){
        return this.itemsMatrix;
    }
    
    public Vector CalculateU(int it){
        double[] points = new double[this.d];
        int idx = this.d*(it-1)+1;
        for (int i=0; i<this.d; i++){
            points[i] = this.RandGArray[idx];
            idx++;
        }
        return new Vector(points);
    }
    
    public Node MakeTree(ArrayList<Vector> S, int iteration){
        if (S.size() <= this.MSOL){
            return new Node(S);
        }
        else {
            //double[] cr = ChooseRule(S);
            Node n = new Node();
            n.setU(CalculateU(iteration));
            //n.setBoundary((int)cr[2]);
            //n.setLeft(MakeTree(LeftS(S, cr[0])));
            //n.setRight(MakeTree(RightS(S, cr[0])));
            return n;
        }
    }
}
