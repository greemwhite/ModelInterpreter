package ce.jku.at.pojos;

import java.util.ArrayList;

public class Connections {

	private ArrayList<Connection> connections = new ArrayList<>();
	
	// 	Getter for the connections with following structure:
	//	ID - name - StartPoint (Type, ID) - direction1 - EndPoint (Type, ID) - direction2
	public ArrayList<Connection> getConnections() {
		return connections;
	}
	
	public void setConnection(Connection c) {
		connections.add(c);
	}
}
