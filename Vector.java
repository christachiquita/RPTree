
import java.util.Arrays;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author christachiquita
 */
public class Vector {
    
    /* ---Variables--- */
    private int dim;
    private double[] points; 
    private int id; //itemid
    
    /* ---Constructors--- */
    public Vector(){
        
    }
    
    //dim = dimension or factor
    public Vector(int dim){
        this.dim = dim;
    }
    
    //dim = dimension, points : item point (from Matrix Q, e.g. item 3 has points[Q02, Q12, Q22]
    public Vector(int dim, double[] points){
        this.dim = dim;
        this.points = points;
    }
    
    //points = item points, see explanation above
    public Vector(double[] points){
        this.points = points;
        this.dim = points.length;
    }
    
    /* ---Getters and Setters--- */
    public void setID(int id){
        this.id = id;
    }
    
    public void setDim(int dim){
        this.dim = dim;
    }
    
    public void setPoints(double[] points){
        this.points = points;
    }
    
    public int getID(){
        return this.id;
    }
    
    public int getDim(){
        return this.dim;
    }
    
    public double[] getPoints(){
        return this.points;
    }
    
    public boolean isEqual(Vector v){
        return (Arrays.equals(this.points, v.points));
    }
    
    public double getPointByIndex(int idx){
        return this.points[idx];
    }
    
    //The vector projection is the unit vector of Vector by the scalar projection of u on v.
    public Vector Projection(Vector v){
        Vector vec = new Vector(this.dim);
        double[] p = new double[this.dim];
        double ovec = this.Dot(v)/getvlen(v);
        for (int i=0; i<p.length; i++){
            p[i] = v.getPointByIndex(i)*ovec;
        }
        vec.setPoints(p);
        vec.setID(this.id);
        return vec;
    }
    
    //represent length but calculated same as dot product
    public double len(Vector v){
        return this.Dot(v);
    }
    
    public double getvlen(Vector v){
        double res = 0;
        for (int i=0; i<v.getDim(); i++){
           res += v.getPointByIndex(i)*v.getPointByIndex(i);
        }
        return res;
    }
    
    public double Dot(Vector v){ //Dot Product of this vector with Vector v
        double res = 0;
        for (int i=0; i<v.getDim(); i++){
            res+=this.getPointByIndex(i) * v.getPointByIndex(i);
        }
        return res;
    }
    
    public void PrintVector(){
        System.out.print("\n< ");
        for (int i=0; i<this.dim; i++){
            System.out.print(this.getPointByIndex(i)+" ");
        }
        System.out.print(">\n");
    }
    
    //Distance between this vector and Vector v2
    public double calcDist(Vector v2){
        double sumsq = 0;
        for (int i=0; i<this.getDim(); i++){
            sumsq += (this.getPointByIndex(i)-v2.getPointByIndex(i))*(this.getPointByIndex(i)-v2.getPointByIndex(i));
        }
        return sumsq;
    }
    
}
