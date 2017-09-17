package ce.jku.at.pojos;

/*
 * 
 * A connection has a unique id and it can be named, it also includes a start and an endpoint 
 * 
 */

public class Connection  {
	private int uuid;
	private String conName;
	
	private EndPoint startPoint;
	private boolean directedAtStart;
	private EndPoint endPoint;
	private boolean directedAtEnd;
	
	
	public Connection() {
	}

	public int getUuid() {
		return uuid;
	}

	public String getConName() {
		return conName;
	}

	public EndPoint getStartpoint() {
		return startPoint;
	}
	
	public boolean getStartDirection() {
		return directedAtStart;
	}

	public EndPoint getEndpoint() {
		return endPoint;
	}
	
	public boolean getEndDirection() {
		return directedAtEnd;
	}
	
	public void setUuid (int uuid) {
		this.uuid = uuid;
	}
	
	public void setConName (String conName) {
		this.conName = conName;
	}

	public void setStartPoint(EndPoint startPoint) {
		this.startPoint = startPoint;
	}

	public void setStartDirection(boolean directedAtStart) {
		this.directedAtStart = directedAtStart;
	}

	public void setEndPoint (EndPoint endPoint) {
		this.endPoint = endPoint;
	}
	
	public void setEndDirection(boolean directedAtEnd) {
		this.directedAtEnd = directedAtEnd;
	}


	public String toString() {
		return "[[" + this.uuid + "][" + this.conName + "][" + this.startPoint + "][" + directedAtStart + "][" + this.endPoint + "][" + directedAtEnd + "]]";
	}

}
