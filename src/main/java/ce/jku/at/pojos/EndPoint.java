package ce.jku.at.pojos;

/*
 *
 * The Endpoints are including the element and a unique id
 * 
 */

public class EndPoint {
	private String element;
	private int id;
	
	public EndPoint()  {
	}
	
	public String getElement() {
		return element;
	}
	
	public int getId () {
		return id;
	}
	
	public void setElement(String element) {
		this.element = element;
	}
	
	public void setId(int id) {
		this.id = id;		
	}
	
	public String toString() {
		return "" + element + ", ID" + id + "";
	}
	
}
