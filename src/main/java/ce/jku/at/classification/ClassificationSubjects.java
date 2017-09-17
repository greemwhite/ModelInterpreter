package ce.jku.at.classification;

/*
 * 
 * This class contains the calculation of the classification with borders 
 * 
 */

import java.util.*;
import ce.jku.at.pojos.*;
import ce.jku.at.xmlreader.*;

public class ClassificationSubjects {
	private ArrayList<Element> subjects = new ArrayList<Element>();
	private ArrayList<Element> activities = new ArrayList<Element>();
	private ArrayList<Classify> borders = new ArrayList<Classify>();
	private XMLReaderList xmlreader;
	private Elements e = new Elements();
	private Classify c = new Classify();
	private Classes classes = new Classes();

	public ClassificationSubjects(XMLReaderList xmlreader) {
		this.xmlreader = xmlreader;
	}

	public String calculateClassBasedActivities() {
		StringBuffer classBasedDistance = null;
		e = xmlreader.getElementList();
		subjects = e.getSubjects();

		classBasedDistance = new StringBuffer("\nCalculate Subject Classes:\n");
		
		// Subjects are sorted based on the x coordinates for being able to build upward classes 
		Collections.sort(subjects, new xposComparator());
		
		double xs = 0, xe = 0, ys = 0, ye = 0;

		// Calculate the class boarders based on the sorted subjects
		for (int i = 0; i < subjects.size(); i++) {
			c = new Classify();
			
			// If the structure contains only one subject, then there is only one class
			if(subjects.size() == 1) {
				xs = 0;
				xe = 1;
				ys = 0;
				ye = 1;
			}

			// Calculate the borders for the first subject
			if (i == 0 && subjects.size() > 1) {
				xs = 0;
				xe = calculateMean(subjects.get(i).getxpos(), subjects.get(i + 1).getxpos());
				ys = subjects.get(i).getypos();
				ye = 1;
			}
			
			// Calculate the borders for subjects that a neither start or end subject
			if (i > 0 && (i + 1) < subjects.size()) {
				xs = calculateMean(subjects.get(i - 1).getxpos(), subjects.get(i).getxpos());
				xe = calculateMean(subjects.get(i + 1).getxpos(), subjects.get(i).getxpos());
				ys = subjects.get(i).getypos();
				ye = 1;
			}
			
			// Calculate the boarders for the endsubject 
			if (i > 0 && (i + 1) >= subjects.size()) {
				xs = calculateMean(subjects.get(i - 1).getxpos(), subjects.get(i).getxpos());
				xe = 1;
				ys = subjects.get(i).getypos();
				ye = 1;
			}
			c.setId(subjects.get(i).getId());
			c.setXend(xe);
			c.setXstart(xs);
			c.setYend(ye);
			c.setYstart(ys);
			classes.setClass(c);
			classBasedDistance.append("\n" + (i+1) + ". Subject "+ "[ID] "+ subjects.get(i).getId() + " [Name] " + subjects.get(i).getName());
		}

		activities = e.getActivties();
		borders = classes.getClasses();
		int countA = 0;

		classBasedDistance.append("\n\nCalculate Activities (Classification):\n");

		// Check to which subjectclasses the activities are according
		for (Element e : activities) {
			for (Classify c : borders) {
				if (e.getxpos() < c.getXend() && e.getxpos() > c.getXstart() && e.getypos() > c.getYstart()
						&& e.getypos() < c.getYend()) {
					e.setSubjectClass(c.getId());
				}
			}
			activities.set(countA, e);
			classBasedDistance.append("\n"+e.toString());
			countA++;
		}
		return classBasedDistance.toString();
	}	

	// Method for calculating the mean
	private double calculateMean(double x1, double x2) {
		return (x1 + x2) / 2;
	}
}


// Different comparators for the positions
class xposComparator implements Comparator<Element> {
	@Override
	public int compare(Element e1, Element e2) {
		return e1.getxpos() < e2.getxpos() ? -1 : e1.getxpos() == e2.getxpos() ? 0 : 1;
	}
}
