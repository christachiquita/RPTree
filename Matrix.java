/*
 * Matrix class (row x col)
 * two dimensional arrays of double
 * Include :
 * 1. Four Constructor with different parameters :
        a. Matrix (number of row, number of col) --> double[row][col]
        b. Matrix (number of row, number of col, double value for each cell) --> same value for all cells
        c. Matrix (matrix) --> copy matrix, same number of col, same number or row
        d.
 * 2. Getters and setters
 * 3. Matrix arithmethic operation : Add (matrix, matrix), Substract (matrix, matrix), Multiply (double, matrix), Dot(matrix, matrix)
 * 4. Transpose
 * 5. UVDecomp (matrix) : construct new UVDecomp (see UVDecomp class)
 */

/**
 *
 * @author ChristaChiquita
 */

public class Matrix {
    
    /* ---Variables--- */
    private double[][] matrix;
    private int row, col;
    /* ---Variables--- */
    
    /* ---Constructors--- */
    public Matrix (int row, int col){
        this.matrix = new double[row][col];
        this.row = row;
        this.col = col;
    }
    
    
    public Matrix (int row, int col, double val){
        this.matrix = new double[row][col];
        this.row = row;
        this.col = col;
        for (int i=0; i<row; i++){
            for (int j=0; j<col; j++){
                this.matrix[i][j] = val;
            }
        }
    }
    
    public Matrix (double[][] othermatrix){
        this.row = othermatrix.length;
        this.col = othermatrix[0].length;
        this.matrix = new double[this.row][this.col];
    }
    /* ---Constructors--- */
    
    
    /* ---Getters and Setters--- */
    public double[][] getMatrix(){
        return this.matrix;
    }
    
    public int getRow(){
        return this.row;
    }
    
    public int getCol(){
        return this.col;
    }
    
    public void setRow(int r){
        this.row = r;
    }
    
    public void setCol(int c){
        this.col = c;
    }
    /* ---Getters and Setters--- */
    
    
    public void checkDimension(Matrix B){
        if (this.row!=B.getRow()){
            throw new IllegalArgumentException("Both matrices must have same number of rows");
        }
        if (this.col!=B.getCol()){
            throw new IllegalArgumentException("Both matrices must have same number of columns");
        }
    }
    
    public void UpdateRow (double[] rows, int row){
        for (int i=0; i<this.col; i++){
            //System.out.println("before : "+this.getMatrix()[row][i]);
            this.getMatrix()[row][i] = rows[i];
            //System.out.println("after : "+this.getMatrix()[row][i]);
        }
    }
    
    public void UpdateCol (double[] cols, int col){
        for (int i=0; i<this.row; i++){
            this.getMatrix()[i][col] = cols[i];
        }
    }
    
    /* ---Arithmetic Operations--- */
    
    /* matrix + B */
    public Matrix Add (Matrix B){
        checkDimension(B);
        Matrix result = new Matrix(this.row, this.col);
        for (int i=0; i<this.row; i++){
            for (int j=0; j<this.col; j++){
                result.getMatrix()[i][j] = this.matrix[i][j]+B.getMatrix()[i][j];
            }
        }
        return result;
    }
    
    /* matrix - B */
    public Matrix Subtract (Matrix B){
        checkDimension(B);
        Matrix result = new Matrix(this.row, this.col);
        for (int i=0; i<this.row; i++){
            for (int j=0; j<this.col; j++){
                result.getMatrix()[i][j] = this.matrix[i][j]-B.getMatrix()[i][j];
            }
        }
        return result;
    }
    
    /* B - matrix */
    public Matrix SubtractFrom (Matrix B){
        checkDimension(B);
        Matrix result = new Matrix(this.row, this.col);
        for (int i=0; i<this.row; i++){
            for (int j=0; j<this.col; j++){
                result.getMatrix()[i][j] = B.getMatrix()[i][j]-this.matrix[i][j];
            }
        }
        return result;
    }
    
    /* matrix[i][j] - n */
    public Matrix SubtractWithVal(double n){
        Matrix result = new Matrix(this.row, this.col);
        for (int i=0; i<this.row; i++){
            for (int j=0; j<this.col; j++){
                result.getMatrix()[i][j]-=n;
            }
        }
        return result;
    }
    
    /* n * matrix */
    public Matrix Mul(double n){
        Matrix result = new Matrix(this.row, this.col);
        for (int i=0; i<this.row; i++){
            for (int j=0; j<this.col; j++){
                result.getMatrix()[i][j] = n*this.matrix[i][j];
            }
        }
        return result;
    }
    
    /* matrix * B */
    public Matrix Dot (Matrix B){
        Matrix result = new Matrix(this.row, B.col);
        double temp=0;
        int idx=0;
        for (int i=0; i<this.row; i++){
            for (int j=0; j<B.col; j++){
                for (int k=0; k<this.col; k++){
                    temp += (this.getMatrix()[i][k]*B.getMatrix()[k][idx]);
                }
                idx++;
                result.getMatrix()[i][j] = temp;
                temp=0;
            }
            temp=0;
            idx=0;
        }
        return result;
    }
    
    /* ---Arithmetic Operations--- */
    
    
    public void Print(){
        for (int i=0; i<this.getRow(); i++){
            System.out.print(" ");
            for (int j=0; j<this.getCol(); j++){
                System.out.print (this.getMatrix()[i][j]+" ");
            }
            System.out.print(" \n");
        }
    }
}

/*by : CC*/
