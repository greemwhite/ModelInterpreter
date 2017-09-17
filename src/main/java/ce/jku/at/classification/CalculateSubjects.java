package ce.jku.at.classification;

/*
 * 
 *  This class contains the calculation of the euclidean classification 
 *  and the calculation of the connections. 
 * 
 */

import java.util.*;
import ce.jku.at.pojos.*;
import ce.jku.at.xmlreader.*;

public class CalculateSubjects {
	private ArrayList<Element> subjects = new ArrayList<Element>();
	private ArrayList<Element> activities = new ArrayList<Element>();
	private ArrayList<Element> messages = new ArrayList<Element>();
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private XMLReaderList xmlreader;
	private Elements e = new Elements();
	private Connections c = new Connections();

	public CalculateSubjects(XMLReaderList xmlreader) {
		this.xmlreader = xmlreader;
	}

	//	Method for calculating the distance between the subjects and 
	// their according activities based on the euclidean distance
	public String calculateEuclideanBasedActivties() {
		StringBuffer euclideanBasedActivities = null;
		e = xmlreader.getElementList();
		subjects = e.getSubjects();
		activities = e.getActivties();

		euclideanBasedActivities = new StringBuffer("\nCalculate Subjects:\n");
		
		int countS = 0; // Counter for the subjects

		// Count the subjects and keep their ids in memory
		for (Element x : subjects) {
			x.setSubjectEuc(countS);
			euclideanBasedActivities.append("\n" + x.toString());
			subjects.set(countS, x);
			countS++;
		}
		euclideanBasedActivities.append("\n\nCalculate Activities (Euclidean): \n");
		double res; 
		double diff; 
		int countA = 0; // Counter for the activities

		// For each actitvity the subject with the lowest distance is calculated
		for (Element a : activities) {
			countS = 0;
			res = 1; 	// Because the distance is normalized between the zero and one, 
						// each distance at the beginning must be smaller then one
			for (Element s : subjects) {
				diff = euclideanDistance(a.getxpos(), a.getypos(), s.getxpos(), s.getypos());
				if (diff < res) {
					a.setSubjectEuc(s.getId());
					a.setSubjectClass(s.getId());
					a.setDistance(diff);
					res = diff;
				}
				countS++;
			}
			activities.set(countA, a);
			euclideanBasedActivities.append("\n" + a.toString());
			countA++;
		}
		
		/*System.out.println("\nVorgeschlagene Zuordnung der Aktivitäten:\n");

		for (Element s : subjects) {
			for (Element x : activities) {
				if (x.getSubject() == s.getId()) {
					System.out.println("[[Subject][" + s.getId() + ", " + s.getName() + "][" + x.getId() + ", "
							+ x.getName() + "][" + x.getDistance() + "]]");
				}
			}
		}*/
		return euclideanBasedActivities.toString();
	}
	
	public ArrayList<Element> getEuclideanActivities() {
		return activities;
	}
	
	// Method for calculating the recommendation of the assignment of the messages
	public String calculateSequence() {
		StringBuffer calculateMess = new StringBuffer("\n\nVorgeschlagene Zuordnung der Nachrichten: \n");

		c = xmlreader.getConnectionList();
		connections = c.getConnections();
		e = xmlreader.getElementList();
		messages = e.getMessage();

		int messageId = 0;
		boolean part = true;
		String[] messagesOutput = new String[3];
		int messageCtn = 0;
		int startId = 0;
		int endId = 0;

		// Connections are read in according the following pattern:
		// Connection part1: activity - message; Connection part2: message -
		// activity		
		// For addressing this each connection has to run two times and a new 
		// string array is used for saving the messages for the output
		for (Connection z : connections) {
			EndPoint start = z.getStartpoint();
			EndPoint end = z.getEndpoint();

			if (part) {
				if (!start.getElement().equals("Message")) {
					messagesOutput[1] = "Activity ID " + start.getId();
					startId = start.getId();
					
				} else {
					messageId = start.getId();
				}

				if (!end.getElement().equals("Message")) {
					messagesOutput[1] = "Activity ID " + end.getId();
					startId = end.getId();
					
				} else {
					messageId = end.getId();
				}	
				part = false;
			} else {
				if (!start.getElement().equals("Message")) {
					messagesOutput[2] = "Activity ID " + start.getId();
					endId = start.getId();
				}

				if (!end.getElement().equals("Message")) {
					messagesOutput[2] = "Activity ID " + end.getId();
					endId = end.getId();
				}
				
				for (Element x : messages) {
					if (messageId == x.getId()) {	
						messagesOutput[0] = "[Message][" + messageId + ", " + x.getName() + "]";
						x.setMessageStartId(startId);
						x.setMessageEndId(endId);
						messages.set(messageCtn, x);
					}		
					messageCtn++;
				}	
				part = true;
				messageCtn = 0;
				calculateMess.append("\n[" + messagesOutput[0] + "[Startpoint - " + messagesOutput[1] + "][Endpoint - "
						+ messagesOutput[2] + "]]");
			}
		}
		return calculateMess.toString();
	}

	// Formula for calculation the euclidean Distance
	public double euclideanDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}
}