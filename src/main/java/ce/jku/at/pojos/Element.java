package ce.jku.at.pojos;

/*
 * 
 * The Element class includes the different fields of a element that is read in
 * There are also fields for the new calculated classification and their Getter and Setters
 * 
 */

public class Element {
	private String type;
	private int id;
	private String name;
	private double xpos;
	private double ypos;
	private double angle;
	private int subjectEuc;
	private int subjectClass;
	private double distance;
	private int messageStartId;
	private int messageEndId;	
	private boolean corrClassified;
	private boolean functionState;
	private boolean sendState;
	private boolean receiveState;
	

	public Element() {
	}

	public String getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getxpos() {
		return xpos;
	}

	public double getypos() {
		return ypos;
	}

	public double getAngle() {
		return angle;
	}
	
	public int getSubjectEuc() {
		return subjectEuc;
	}
	
	public int getSubjectClass() {
		return subjectClass;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public int getMessageStartId() {
		return messageStartId;
	}	

	public int getMessageEndId() {
		return messageEndId;
	}
	
	public boolean getClassification() {
		return corrClassified;
	}
	
	public boolean getFunctionState() {
		return functionState;
	}
	
	public boolean getSendState() {
		return sendState;
	}
	
	public boolean getReceiveState() {
		return receiveState;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setXpos(double xpos) {
		this.xpos = xpos;
	}

	public void setYpos(double ypos) {
		this.ypos = ypos;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	public void setSubjectEuc(int subjectEuc) {
		this.subjectEuc = subjectEuc;
	}
	
	public void setSubjectClass(int subjectClass) {
		this.subjectClass = subjectClass;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public void setMessageStartId(int messageStartId) {
		this.messageStartId = messageStartId;
	}
	
	public void setMessageEndId(int messageEndId) {
		this.messageEndId = messageEndId;
	}
	
	public void setClassifiction(boolean corrClassified) {
		this.corrClassified = corrClassified;
	}
	
	public void setFunctionState(boolean functionState) {
		this.functionState = functionState;
	}
	
	public void setSendState(boolean sendState) {
		this.sendState = sendState;
	}
	
	public void setReceiveState(boolean receiveState) {
		this.receiveState = receiveState;
	}
	
	public String toString() {
		return this.type +" [ID] " + this.id + " [Name] " + this.name + ", classified to Subject [eucl.] " + subjectEuc + " [class] " + subjectClass + " " + corrClassified;
	}
}
