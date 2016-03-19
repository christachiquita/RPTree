
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;



/**
 *
 * @author christachiquita
 */
public class RPTrees {

    /* ---Variables--- */
    private int n; //number of items to be indexed (m in SGD class)
    private int d; //dimensionality of the points (f in SGD class)
    private int NumOfTrees; //number of RPTrees we want to build
    private int MSOL; //MaxSizeOfLeaf : maximum number of points that you can put in a leaf node of a RP-Tree
    private String DataFile; //path of the file that contains the points that need to be indexed
    private String RandGFile; //RandomGaussianFile : path of the file that contains a sequence of floating numbers which are randomly sampled from the Standard Gaussian distribution
    private String QueryFile; //path of the file that contains the queries
    private Node root;
    private Matrix itemsMatrix;
    private double[] RandGArray;
    private int[] QArray;
    private ArrayList<Vector> S;
    private Tree[] Trees;
    public int iter = 1;
    public ArrayList<int[]> Leaves = new ArrayList<int[]>();
    
    /* ---Constructors--- */
    public RPTrees(){
        
    }
    
    /* ---Getters and Setters--- */
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
    
    public void setNumOfTrees(int num){
        this.NumOfTrees = num;
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
    
    public String getRandGFile(){
        return this.RandGFile;
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
    
    public int getNumOfTrees(){
        return this.NumOfTrees;
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
    
    //return Vector which contains points of three random number from rand file that will be used as unit vector U
    public Vector CalculateU(int it){
        double[] points = new double[this.d];
        int idx = this.d*(it-1)+1;
        for (int i=0; i<this.d; i++){
            points[i] = this.RandGArray[idx-1];
            idx++;
        }
        return new Vector(points);
    }
    
    //MakeTree Operation, return root Node of the tree or Node of leaves if size of S less than or equal too Minimum Number of Leaves (MSOL)
    public Node MakeTree(ArrayList<Vector> S){
        if (S.size() <= this.MSOL){
            Collections.sort(S, new Comparator<Vector>() {
                @Override public int compare(Vector p1, Vector p2) {
                    return p1.getID() - p2.getID(); // Ascending
                }
            });
            Node n = new Node(S);
            int[] leaf = new int[S.size()];
            for (int i=0; i<leaf.length; i++){
                leaf[i] = n.getLeaf().get(i).getID()+1;
            }
            this.Leaves.add(leaf);
            return n;
        }
        else {
            double b = ChooseRule(S, iter);
            Node n = new Node();
            n.setU(this.CalculateU(iter));
            n.setBoundary((int)b);
            
            n.setDataSet(S);
            ArrayList<Vector> sortedS = this.SortedIndex(S, n.getU());
            n.setBValue(sortedS.get(n.getBoundary()).Dot(n.getU()));
            
            Node left = n.LeftS(sortedS);
            Node right = n.RightS(sortedS);
            iter++;
            n.setLeft(MakeTree(left.getDataSet()));
            n.setRight(MakeTree(right.getDataSet()));
            return n;
        }
    }
    
    //return ArrayList of Sorted Vector (sort by length (dot product) of each vector with query vector)
    public ArrayList<Vector> SortedIndex(ArrayList<Vector> Sp, final Vector v){
        ArrayList<Vector> S = Sp;
        Collections.sort(S, new Comparator<Vector>() {
                @Override public int compare(Vector p1, Vector p2) {
                    return Double.compare(p1.len(v), p2.len(v)); // Ascending
                }
            });
        return S;
    }
    
    //will be called everytime makeTree is executed. return (double)median index of S
    //• if n is odd, return the (bn/2c + 1)-th number.
    //• if n is even, return the average of the (n/2)-th and (n/2 + 1)-th number
    public double ChooseRule(ArrayList<Vector> S, int iteration){
        double[] res = new double[3];
        double boundary = getMedIndex(S);
        return boundary;
    }
    
    public double[] GenerateV(int dim, int i){
        double[] v = new double[dim];
        for (int j=0; j<dim; j++){
            v[j] = this.getItemsMat().getMatrix()[j][i];
        }
        return v;
    }
    
    //generate ArrayList of Vector of DataSet from Matrix of Items
    //S = dataset in this case
    public void GenerateS(){
        Matrix m = this.getItemsMat();
        ArrayList<Vector> s = new ArrayList<Vector>();
        
        for (int i=0; i<m.getCol(); i++){
            double[] v = GenerateV(this.d, i);
            Vector nv = new Vector(this.d, v);
            nv.setID(i);
            s.add(nv);
        }
        setS(s);
    }
    
    //return Vector of query item
    public Vector GenerateQ(int id){
        int col = id-1;
        Vector query = new Vector(this.d);
        Matrix m = this.getItemsMat();
        double[] point = new double[this.d];
        for (int i=0; i<this.d; i++){
            point[i] = m.getMatrix()[i][col];
        }
        query.setPoints(point);
        query.setID(id);
        return query;
    }
    
    public void printS(){
        for (int i=0; i<this.getS().size(); i++){
            System.out.println(i+" : <"+this.getS().get(i).getPointByIndex(0)+", "+this.getS().get(i).getPointByIndex(1)+", "+this.getS().get(i).getPointByIndex(2)+">");
        }
    }
    
    public double getMedIndex(ArrayList<Vector> S){
        double med;
        int n=S.size();
        if (n%2==0){
            med = 0.5*((n/2)+((n/2)+1));
        }
        else {
            med = (int)(n/2)+1;
        }
        return med-1;
    }
    
    
    //return true if array n contains Integer a
    public boolean isContain(int[] n, int a){
        for (int i=0; i<n.length; i++){
            if (n[i]==a){
                return true;
            }
        }
        return false;
    }
    
    public int[] getKNN(ArrayList<DistanceVector> all, int num, int q){
        int[] minids = new int[num];
        boolean check=true;
        for (int i=0; i<minids.length; i++){
            minids[i] = -2;
        }
        
        int idmin = -1;
        double[] mindists = new double[all.size()];
        for (int i=0; i<mindists.length; i++){
            mindists[i] = all.get(i).getDistance();
        }
        double min = 100000;
        for (int a=0; a<num; a++){
            for (int i=0; i<mindists.length; i++){
                
                if (mindists[i] < min){
                    if (!isContain(minids, (all.get(i).getID()+1))){
                            min = mindists[i];
                            idmin = i;
                            check=true;
                    }
                }
            }
            minids[a] = all.get(idmin).getID()+1;
            min = 100000;
        }
        return minids;
    }
    
    public void printKNN(Vector[] vq, Node[] trees, int num){
        int numOfNN = num+1;
        for (int a=0; a<vq.length; a++){
            ArrayList<DistanceVector> allNNVec = new ArrayList<DistanceVector>();
            for (int t=0; t<this.getNumOfTrees(); t++){
                    ArrayList<DistanceVector> NNVec = trees[t].getNN2(vq[a], numOfNN);
                    for (int j=0; j<NNVec.size(); j++){
                        allNNVec.add(NNVec.get(j));
                    }
            }
                int[] n = this.getKNN(allNNVec, numOfNN, vq[a].getID());
                for (int i=1; i<n.length; i++){
                   System.out.print(n[i]+" ");
                }
                System.out.print("\n");
        }
    }
    
    /* ---File Operations--- */
    public int countLines (String pathname) throws IOException {
        File file = new File(pathname);
        int lines = 0;
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {        
                lines++;
                String s = scanner.nextLine();
            }
            return lines;
        } finally {
            scanner.close();
        }
    }
    
    public int countCols (String path) throws IOException{
        FileInputStream inputStream = new FileInputStream(path);
        Scanner scanner = new Scanner(inputStream);
        DataInputStream in = new DataInputStream(inputStream);
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        String[] line = bf.readLine().split("\\s+");
        return line.length;
    }
    
    public Matrix FileToMatrix(String fileName) throws FileNotFoundException, IOException{
            String line = "";
            int lines = this.countLines(fileName);
            int cols = this.countCols(fileName);
            Matrix con = new Matrix(lines, cols);
            FileInputStream inputStream = new FileInputStream(fileName);
            Scanner scanner = new Scanner(inputStream);
            DataInputStream in = new DataInputStream(inputStream);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));

            int lineCount = 0;
            String[] numbers;
            while ((line = bf.readLine()) != null && lineCount<lines)
            {
                numbers = line.split("\\s+");
                    for (int j=0; j<cols; j++){
                        con.getMatrix()[lineCount][j] = Double.parseDouble(numbers[j]);
                    }
                lineCount++;    
            }
            bf.close();
            return con;
    }
    
    public ArrayList<int[]> ItemsMatrixToArrayList(){
        ArrayList<int[]> AL = new ArrayList<int[]>();
        int[] cell = new int[2];
        for (int i=0; i<this.getItemsMat().getRow(); i++){
            for (int j=0; j<this.getItemsMat().getCol(); j++){
                cell[0] = i;
                cell[1] = j;
                AL.add(cell);
            }
        }
        return AL;
    }
    
    public int[] FileToIntArray (String fileName) throws IOException{
            String line;
            int lines = this.countLines(fileName);
            int[] con = new int[lines];
            FileInputStream inputStream = new FileInputStream(fileName);
            Scanner scanner = new Scanner(inputStream);
            DataInputStream in = new DataInputStream(inputStream);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));

            int lineCount = 0;
            String[] numbers;
            while ((line = bf.readLine()) != null && lineCount<lines)
            {
                        con[lineCount] = Integer.parseInt(line);
                lineCount++;    
            }
            bf.close();
            return con;
    }
    
    public double[] FileToDoubleArray (String fileName) throws IOException{
            String line;
            int lines = this.countLines(fileName);
            double[] con = new double[lines];
            FileInputStream inputStream = new FileInputStream(fileName);
            Scanner scanner = new Scanner(inputStream);
            DataInputStream in = new DataInputStream(inputStream);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));

            int lineCount = 0;
            String[] numbers;
            while ((line = bf.readLine()) != null && lineCount<lines)
            {
                        con[lineCount] = Double.parseDouble(line);
                lineCount++;    
            }
            bf.close();
            return con;
    }
    
    public void printLeaves (){
        for (int i=0; i<Leaves.size(); i++){
            System.out.print(" { ");
            for (int j=0; j<Leaves.get(i).length; j++){
                System.out.print(Leaves.get(i)[j]+" ");
            }
            System.out.print("} ");
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        RPTrees rpt = new RPTrees();
        
        String nn, dd, numOfTrees, maxleaves, datafile, queryfile, randfile;
        nn = args[0];
        dd = args[1];
        numOfTrees = args[2];
        maxleaves = args[3];
        datafile = args[4]; 
        randfile = args[5];
        queryfile = args[6];
        int numOfNN = 3;
        
        rpt.setN(Integer.parseInt(nn));
        rpt.setD(Integer.parseInt(dd));
        rpt.setNumOfTrees(Integer.parseInt(numOfTrees));
        rpt.setMSOL(Integer.parseInt(maxleaves));
        
        File f = new File(datafile);
        rpt.setDataFile(f.getAbsolutePath());
        rpt.setItemsMat(rpt.FileToMatrix(rpt.getDataFile()));
        
        File qf = new File(queryfile);
        rpt.setQFile(qf.getAbsolutePath());
        rpt.setQArray(rpt.FileToIntArray(rpt.getQueryFile()));
        
        File gf = new File(randfile);
        rpt.setRandGFile(gf.getAbsolutePath());
        rpt.setRandGArray(rpt.FileToDoubleArray(rpt.getRandGFile()));
        
        rpt.GenerateS();
        
        Vector vu = rpt.CalculateU(1);
        
        Vector[] vq = new Vector[rpt.QArray.length];
        for (int i=0; i<rpt.QArray.length; i++){
            vq[i] = rpt.GenerateQ(rpt.getQArray()[i]);
        }
        Node[] trees = new Node[rpt.getNumOfTrees()];
        for (int t=0; t<trees.length; t++){
            Node tes = rpt.MakeTree(rpt.getS());
            trees[t] = tes;
        }
        
        rpt.printKNN(vq, trees, numOfNN);
        
    }
    
}

/* by : CC*/
