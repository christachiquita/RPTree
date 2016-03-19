/*
 * DistanceVector Class
 * Contains ID (represents item ID) and distance (distance with query vector)
 */

/**
 *
 * @author christachiquita
 */
public class DistanceVector {
    
    /* ---Variables--- */
    private int id;//item id
    private double distance;
    
    /* ---Constructors--- */
    public DistanceVector(){
        
    }
    
    public DistanceVector(int id, double dist){
        this.id = id;
        this.distance = dist;
    }
    
    /* ---Getters and Setters--- */
    public void setID(int id){
        this.id = id;
    }
    
    public void setDistance(double dist){
        this.distance = dist;
    }
    
    public int getID(){
        return this.id;
    }
    
    public double getDistance(){
        return this.distance;
    }
    
    public void PrintVector(){
        System.out.print("\n< ");
        System.out.print("Distance - "+this.id+" = "+this.distance);
        System.out.print(">\n");
    }
}

/* by : CC*/

