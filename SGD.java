
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.Math.sqrt;
import java.util.Scanner;


/**
 *
 * @author ChristaChiquita
 */
public class SGD {

    /* ---Variables--- */
    private int n, m, f, r; 
    private double u, L;
    private String ratingfile, iterfile, randfile;
    private int[] ridarray, uidarray, iidarray, ratingarray, iter;
    private int[][] ratingfileM;
    private double[] randarray;
    private Matrix P, Q;
    private Matrix MatR;
    private Matrix NormR;
    private Matrix AvgUserItem;
    
    /* ---Constructors--- */
    public SGD(){
        
    }
    
    /* ---METHODS--- */
    public String readParameter(String paramname) throws IOException{
        System.out.print("Enter "+ paramname +" : ");
        BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        return buf.readLine();
    }
    
    /* ---File Methods--- */
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
    
    public String readFile(String pathname) throws IOException {
        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {        
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
    
    public int[][] FileToMatrix(String fileName) throws FileNotFoundException, IOException{
            String line = "";
            int lines = this.countLines(fileName);
            int[][] con = new int[lines][4];
            FileInputStream inputStream = new FileInputStream(fileName);
            Scanner scanner = new Scanner(inputStream);
            DataInputStream in = new DataInputStream(inputStream);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));

            int lineCount = 0;
            String[] numbers;
            while ((line = bf.readLine()) != null && lineCount<lines)
            {
                numbers = line.split("::");
                    for (int j=0; j<4; j++){
                        con[lineCount][j] = Integer.parseInt(numbers[j]);
                    }
                lineCount++;    
            }
            bf.close();
            return con;
    }
    
    public double[] FileToArray (String fileName) throws IOException{
        String line = "";
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
                //numbers = line.split("::");
                    //for (int j=0; j<4; j++){
                        con[lineCount] = Double.parseDouble(line);
                    //}
                lineCount++;    
            }
            bf.close();
            return con;
    }
    
    public int[] FileToIntArray (String fileName) throws IOException{
        String line = "";
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
                //numbers = line.split("::");
                    //for (int j=0; j<4; j++){
                        con[lineCount] = Integer.parseInt(line);
                    //}
                lineCount++;    
            }
            bf.close();
            return con;
    }
    
    /*Matrix Preprocessing*/
    public void GenerateMatrixR(){
        this.MatR = new Matrix(this.getN(), this.getM());
        int uid, iid;
        for (int u=0; u<this.getUIDArray().length; u++){
                uid = this.getUIDArray()[u];
                iid = this.getIIDArray()[u];
                this.MatR.getMatrix()[uid-1][iid-1] = this.getRatingArray()[u];
        }
    }
    
    public double getAvgRatingUser (int uid){
        double sum = 0;
        int n = 0;
        for (int i=0; i<this.getUIDArray().length; i++){
            if (this.getUIDArray()[i] == uid){
                sum+=this.getRatingArray()[i];
                //System.out.println(i + " : " + this.getUIDArray()[i]+", sum : "+sum);
                n++;
                //System.out.println("n : "+n);
            }
        }
        if (n==0){
            n=1;
        }
        return (sum/n);
    }
    
    public double getAvgRatingItem (int iid){
        double sum = 0;
        int n = 0;
        for (int i=0; i<this.getIIDArray().length; i++){
            if (this.getIIDArray()[i] == iid){
                sum+=this.getRatingArray()[i];
                //System.out.println(i + " : " + this.getIIDArray()[i]+", sum : "+sum);
                n++;
                //System.out.println("n : "+n);
            }
        }
        if (n==0){
            n=1;
        }
        return (sum/n);
    }
    
    public double getAvgRatingUserItem (int uid, int iid){
        //System.out.println("avg user "+uid+" : "+getAvgRatingUser(uid));
        //System.out.println("avg item "+iid+" : "+getAvgRatingItem(iid));
        return (0.5*(getAvgRatingUser(uid)+getAvgRatingItem(iid)));
    }
    
    public void GenerateAvgMatrix(){
        this.AvgUserItem = new Matrix(this.getMatR().getRow(), this.getMatR().getCol());
        int uid, iid;
        for (int i=0; i<this.getMatR().getRow(); i++){
            for (int j=0; j<this.getMatR().getCol(); j++){
                //uid = this.getUIDArray()[i];
                //iid = this.getIIDArray()[j];
                this.AvgUserItem.getMatrix()[i][j] = getAvgRatingUserItem(i+1, j+1);
            }
        }
    }
    
    public void NormalizeMatrix(){
        this.NormR = new Matrix(this.MatR.getRow(), this.MatR.getCol());
        for (int i=0; i<this.NormR.getRow(); i++){
            for (int j=0; j<this.NormR.getCol(); j++){
                this.NormR.getMatrix()[i][j] = this.MatR.getMatrix()[i][j] - this.AvgUserItem.getMatrix()[i][j];
            }
        }
    }
    
    public double RatingAvg(){ //a
        double sum = 0;
        for (int i=0; i<this.getRatingArray().length; i++){
            sum += this.getRatingArray()[i];
        }
        return (sum/this.getRatingArray().length);
    }
    
    /* ---Initialization of the P and Q Matrices--- */
    public double InitValue(int idx) throws IOException{
        double val;
        double a = this.RatingAvg();
        int f = this.getF();
        double b = this.randarray[idx];
        val = (sqrt(a/f))+b;
        return val;
    }
    
    public void generateP() throws IOException{
        Matrix P = new Matrix(this.MatR.getRow(), this.f);
        int idx = 0;
        for (int i=0; i<P.getRow(); i++){
            for (int j=0; j<P.getCol(); j++){
                P.getMatrix()[i][j] = InitValue(idx);
                idx++;
            }
        }
        setP(P);
    }
    
    public void generateQ(int start) throws IOException{
        Matrix Q = new Matrix(this.f, this.MatR.getCol());
        int idx = start;
        for (int i=0; i<Q.getRow(); i++){
            for (int j=0; j<Q.getCol(); j++){
                Q.getMatrix()[i][j] = InitValue(idx);
                idx++;
            }
        }
        setQ(Q);
    }
    
    /* ---SGD Operations--- */
    public double[] qvaluearray (int qx){
        double[] arr = new double[this.f];
        for (int i=0; i<this.f; i++){
            arr[i] = this.Q.getMatrix()[i][qx-1];
        }
        //System.out.println("\n\n--Q Value Array for x = "+qx+"--");
        for (int i=0; i<arr.length; i++){
            //System.out.print(arr[i]+"\n");
        }
        return arr;
    }
    
    public double[] pvaluearray (int pi){
        double[] arr = new double[this.f];
        for (int i=0; i<this.f; i++){
            arr[i] = this.P.getMatrix()[pi-1][i];
        }
        //System.out.println("\n\n--P Value Array for x = "+pi+"--");
        for (int i=0; i<arr.length; i++){
            //System.out.print(arr[i]+", ");
        }
        return arr;
    }
    
    public double getErr(int i, int x){
        double e = 0;
        double pq = 0;
        double[] parr = this.pvaluearray(i);
        double[] qarr = this.qvaluearray(x);
        for (int a=0; a<parr.length; a++){
            pq += parr[a]*qarr[a];
            //System.out.println("parr["+a+"] ("+parr[a]+")*qarr["+a+"] ("+qarr[a]+") = "+parr[a]*qarr[a]);
            //System.out.println ("pq = "+pq);
        }
        //System.out.println("rxi : "+this.getMatR().getMatrix()[i-1][x-1]+"\n\n");
        e = 2 * (this.getMatR().getMatrix()[i-1][x-1] - pq);
        return e;
    }
    
    public double[] Update(double[] arr, double[] otherarr, double err){
        //double[] updatedarr = new double[arr.length];
        //System.out.println("\n\n--Updated--");
        for (int i=0; i<arr.length; i++){
            //System.out.println("arr["+i+"] : "+arr[i]);
            double d = (2 * this.getL() * arr[i]); //System.out.println("L : "+this.getL()+", d : "+d);
            //System.out.println("U : "+this.getU()+", otherarr["+i+"] : "+otherarr[i]);
            arr[i] += this.getU()*(err * otherarr[i] - d);
            //System.out.println("become arr["+i+"] : "+arr[i]);
        }
        //System.out.print("\n");
        for (int i=0; i<arr.length; i++){
            //System.out.print(arr[i]+", ");
        }
        return arr;
    }
    
    public void SGDop (){
        double err=0;
        boolean y = false;
        double[] parr, qarr;
        //while (err!=0 || !y){
            for (int id=0; id<this.getRIDArray().length ; id++){
                //System.out.println("RID : "+this.getRIDArray()[id]);
                int i = this.uidarray[this.getRIDArray()[id]-1];
                int x = this.iidarray[this.getRIDArray()[id]-1];
                parr = this.pvaluearray(i);
                qarr = this.qvaluearray(x);
                err = this.getErr(i, x);
                //System.out.println("err : "+err);
                qarr = Update(qarr, parr, err);
                parr = Update(parr, qarr, err);
                this.getQ().UpdateCol(qarr, x-1);
                this.getP().UpdateRow(parr, i-1);
                //y = true;
            }
        //}
    }
    
    
    
    /* ---Getters & Setters--- */
    public int[] getUserIDs(int[][] con){
        int[] uid = new int[con.length];
        for (int i=0; i<con.length; i++){
            uid[i] = con[i][1];
        }
        return uid;
    }
    
    public int[] getItemIDs(int[][] con){
        int[] uid = new int[con.length];
        for (int i=0; i<con.length; i++){
            uid[i] = con[i][2];
        }
        return uid;
    }
    
    public int[] getRatings(int[][] con){
        int[] uid = new int[con.length];
        for (int i=0; i<con.length; i++){
            uid[i] = con[i][3];
        }
        return uid;
    }
    
    /* ---SETTERS and GETTERS--- */
    public int getN (){
        return this.n;
    }
    
    public int getM (){
        return this.m;
    }
    
    public int getF (){
        return this.f;
    }
    
    public int getR (){
        return this.r;
    }
    
    public double getU(){
        return this.u;
    }
    
    public double getL(){
        return this.L;
    }
    
    public Matrix getP(){
        return this.P;
    }
    
    public Matrix getQ(){
        return this.Q;
    }
    
    public String getRatingFile(){
        return this.ratingfile;
    }
    
    public String getIterFile(){
        return this.iterfile;
    }
    
    public String getRandFile(){
        return this.randfile;
    }
    
    public int[] getRIDArray(){
        return this.ridarray;
    }
    
    public int[] getUIDArray(){
        return this.uidarray;
    }
    
    public int[] getIIDArray(){
        return this.iidarray;
    }
    
    public int[] getRatingArray(){
        return this.ratingarray;
    }
    
    public int[][] getRatingFileM(){
        return this.ratingfileM;
    }
    
    public Matrix getMatR(){
        return this.MatR;
    }
    
    public Matrix getNormR(){
        return this.NormR;
    }
    
    public Matrix getAvgUserItem(){
        return this.AvgUserItem;
    }
    
    public void setN (int n){
        this.n = n;
    }
    
    public void setM (int m){
        this.m = m;
    }
    
    public void setF (int f){
        this.f =f;
    }
    
    public void setR (int r){
        this.r = r;
    }
    
    public void setU (double u){
        this.u = u;
    }
    
    public void setL (double L){
        this.L = L;
    }
    
    public void setP (Matrix P){
        this.P = P;
    }
    
    public void setQ (Matrix Q){
        this.Q = Q;
    }
    
    public void setRatingFile (String path){
        this.ratingfile = path;
    }
    
    public void setIterFile (String path){
        this.iterfile = path;
    }
    
    public void setRandFile (String path){
        this.randfile = path;
    }
    
    public void setRIDArray(int[] rid){
        this.ridarray = rid;
    }
    
    public void setUIDArray(int[] uid){
        this.uidarray = uid;
    }
    
    public void setIIDArray(int[] iid){
        this.iidarray = iid;
    }
    
    public void setRatingArray(int[] ratings){
        this.ratingarray = ratings;
    }
    
    public void setRatingFileMat(int[][] mat){
        this.ratingfileM = mat;
    }
    
    public void setMatR(Matrix R){
        this.MatR = R;
    }
    
    public void setNormR(Matrix norm){
        this.NormR = norm;
    }
    
    public void setAvgUserItem(Matrix avg){
        this.AvgUserItem = avg;
    }
    
    public void PrintParam(){
        System.out.println("--List of Parametets--");
        System.out.println("Number of users (n) : "+this.getN());
        System.out.println("Number of items (m) : "+this.getM());
        System.out.println("Number of factors (f) : "+this.getF());
        System.out.println("Number of ratings (r) : "+this.getR());
        System.out.println("Learning rate (u) : "+this.getU());
        System.out.println("Regularization Parameter (L) : "+this.getL());
    }
    
    public void PrintArray(int[] uid, String name){
        System.out.println(name +" : ");
        for (int i=0; i<uid.length; i++){
            System.out.print(uid[i]+" ");
        }
    }
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException{
        SGD sgd = new SGD();
        String nn, mm, ff, rr, uu, LL, rf, iterf, randf;
        nn=args[0];//sgd.readParameter("number of users"); //n
        mm=args[1];//sgd.readParameter("number of items"); //m
        ff=args[2];//sgd.readParameter("number of factors"); //f
        rr=args[3];//sgd.readParameter("number of ratings"); //r
        uu=args[4];//sgd.readParameter("learning rate"); //u
        LL=args[5];//sgd.readParameter("regularization parameters"); //L
        rf=args[6];//sgd.readParameter("rating file path"); //ratingFile
        iterf=args[7];//sgd.readParameter("iter file path"); //iterFile
        randf=args[8];//sgd.readParameter("iter file path"); //iterFile
        
        sgd.setN(Integer.parseInt(nn));
        sgd.setM(Integer.parseInt(mm));
        sgd.setF(Integer.parseInt(ff));
        sgd.setR(Integer.parseInt(rr));
        sgd.setU(Double.parseDouble(uu));
        sgd.setL(Double.parseDouble(LL));
        /*tester*/
        File f = new File(rf);
        rf = f.getAbsolutePath();
        sgd.setRatingFile(rf);
        
        File itf = new File(iterf);
        iterf = itf.getAbsolutePath();
        sgd.setIterFile(iterf);
        File fr = new File(randf);//"SGD/src/uniform_rand.txt";
        randf = fr.getAbsolutePath();
        sgd.setRandFile(randf);
        
        //Convert File to String
        String ratingfilecontent = sgd.readFile(sgd.getRatingFile());
        int lines = sgd.countLines(sgd.getRatingFile());

        //Convert file contents to matrix RF
        sgd.setRatingFileMat(sgd.FileToMatrix(rf));
        int[][] RF = sgd.getRatingFileM();
        sgd.setUIDArray(sgd.getUserIDs(RF));
        sgd.setIIDArray(sgd.getItemIDs(RF));
        sgd.setRatingArray(sgd.getRatings(RF));
        sgd.GenerateMatrixR();
        sgd.GenerateAvgMatrix();
        sgd.NormalizeMatrix();
        sgd.randarray = sgd.FileToArray(sgd.getRandFile());
        sgd.setRIDArray(sgd.FileToIntArray(sgd.getIterFile()));
        sgd.generateP();
        sgd.generateQ(sgd.getP().getRow()*sgd.getP().getCol());
        sgd.SGDop();
        sgd.getQ().Print();
        
    }
    
}

/* by : CC*/
