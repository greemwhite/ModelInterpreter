package ce.jku.at.pojos;

/*
 * 
 * Class for the coordinates of the borders of the classification classes
 * It includes Getters and Setters
 * 
 */

public class Classify {
	private int id;
	private double xs;
	private double xe;
	private double ys;
	private double ye;
	
	public Classify() {
	}
	
	public void setId(int id) {
		this.id = id;
	}
	 
	public void setXstart(double xs) {
		this.xs = xs;
	}
	
	public void setXend(double xe) {
		this.xe = xe;
	}
	
	public void setYstart(double ys) {
		this.ys = ys;
	}
	
	public void setYend(double ye) {
		this.ye = ye;
	}
	
	public int getId() {
		return id;
	}
	
	public double getXstart() {
		return xs;
	}
	
	public double getXend() {
		return xe;
	}
	
	public double getYstart() {
		return ys;
	}
	
	public double getYend() {
		return ye;
	}
}
