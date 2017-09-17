package ce.jku.at.classification;

import java.util.*;
import ce.jku.at.pojos.*;
import ce.jku.at.xmlreader.*;

public class CheckInterpretation {
	private XMLReaderList xmlreader;
	private ArrayList<Element> subjects = new ArrayList<Element>();
	private ArrayList<Element> activities = new ArrayList<Element>();
	private ArrayList<Element> states = new ArrayList<Element>();
	private Elements e = new Elements();
	
	public CheckInterpretation(XMLReaderList xmlreader) {
		this.xmlreader = xmlreader;
	}
	
	// Method that checks the classification of a recommended activity. If an activity is classified to another class than the result of the euclidean distance measure then the recommendation is insecure
	public String checkDistances() {
		StringBuffer assignment  = new StringBuffer("\n");
		e = xmlreader.getElementList();
		subjects = e.getSubjects();
		activities = e.getActivties();
		int countA = 0;
				
		for(Element actActivity : activities) {
			if(actActivity.getSubjectEuc() != actActivity.getSubjectClass()) {
				for(Element euclSubj : subjects) {
					if(actActivity.getSubjectEuc() == euclSubj.getId()) {
						for(Element classSubj : subjects) {
							if(actActivity.getSubjectClass() == classSubj.getId()) {
								if(euclSubj.getypos() >= classSubj.getypos()) {
									assignment.append("\nThe assignment of activity [" + actActivity.getId() + "] to subject [" + actActivity.getSubjectClass() + "] is insecure\nIt is maybe part of subject [" + actActivity.getSubjectEuc() + "] Please review that!");
								}
							}
						}
					}
				}
				actActivity.setClassifiction(false);				
			} else {
				assignment.append("\nThe system assigned activity [" + actActivity.getId() + "] to subject [" + actActivity.getSubjectClass() + "]");
				actActivity.setClassifiction(true);
			}
			activities.set(countA, actActivity);
			countA++;
		}
		return assignment.toString();
	}
	
	// Method to deliver the start activity of a subject
	public String findStartElem()  {
		StringBuffer startElem = new StringBuffer("\nStartelements");
		double distance = 1;
		int id = 0;
		
		e = xmlreader.getElementList();
		subjects = e.getSubjects();
		activities = e.getActivties();
		
		for(Element x : subjects) {
			for(Element y : activities) {
				if(y.getSubjectClass() == x.getId()) {
					if(distance > y.getDistance()) {
						id = y.getId();
						distance = y.getDistance();
					}
				}
			}
			startElem.append("Start element for subject ["+ x.getId() + "] is activity [" + id + "]");
			id = 0;
			distance = 1;
		} 
		return startElem.toString();
	}
	
	// Method to deliver the end element of a subject
	public String findEndElem() {
		StringBuffer endElem = new StringBuffer("\nEndelements:");
		double distance = 0;
		int id = 0;
		
		e = xmlreader.getElementList();
		subjects = e.getSubjects();
		activities = e.getActivties();
		
		for(Element x : subjects) {
			for(Element y : activities) {
				if(y.getSubjectClass() == x.getId()) {
					if(distance < y.getDistance()) {
						id = y.getId();
						distance = y.getDistance();
					}
				}
			}
			endElem.append("End element for subject ["+ x.getId() + "] is activity [" + id + "]");
			id = 0;
			distance = 0;
		} 
		return endElem.toString();
	}
	
	// Method to rank the elements according to their distance to the subject
	public void sortElemEucl() {
		CalculateSubjects calcEucl = new CalculateSubjects(xmlreader);
		e = xmlreader.getElementList();
		subjects = e.getSubjects();
		states = e.getStates();
		int countStat = 0;
		
		for(Element stat : states) {
			for(Element sub : subjects) {
				if(stat.getSubjectClass() == sub.getId()) {
					stat.setDistance(calcEucl.euclideanDistance(stat.getxpos(), stat.getypos(), sub.getxpos(), sub.getypos()));
					states.set(countStat, stat);
					System.out.println(stat.getDistance());					
				}
			}
			countStat++;
		}
		
		Collections.sort(states, new distanceComparator());
	}
}

class distanceComparator implements Comparator<Element> {
	@Override
	public int compare(Element e1, Element e2) {
		return e1.getDistance() < e2.getDistance() ? -1 : e1.getDistance() == e2.getDistance() ? 0 : 1;
	}
}


