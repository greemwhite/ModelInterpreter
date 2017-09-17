package ce.jku.at.classification;

/*
 * 
 * This class contains the improvement of the calculated assignments
 * 
 */

import java.util.ArrayList;

import ce.jku.at.pojos.Connection;
import ce.jku.at.pojos.Connections;
import ce.jku.at.pojos.Element;
import ce.jku.at.pojos.Elements;
import ce.jku.at.xmlreader.XMLReaderList;

public class ImproveAssignment {
	private ArrayList<Element> subjects = new ArrayList<Element>();
	private ArrayList<Element> activities = new ArrayList<Element>();
	private ArrayList<Element> messages = new ArrayList<Element>();
	private XMLReaderList xmlreader;
	private Elements e = new Elements();

	public ImproveAssignment(XMLReaderList xmlreader) {
		this.xmlreader = xmlreader;
	}
	
	// This method overwrites the assignment of activites to subjects
	public String improveActivityAssignment(int actId, int newSubId) {
		e = xmlreader.getElementList();
		activities = e.getActivties();
		subjects = e.getSubjects();
		boolean isSub = false;
		
		// Check if the newSubId subject exits 
		for (Element x : subjects) {
			if (newSubId == x.getId()) {
				isSub = true;
			}
		}

		int ctn = 0;

		// If the subject exists, the assignment is overwritten
		if (isSub) {
			for (Element x : activities) {
				if (x.getId() == actId) {
					x.setSubjectEuc(newSubId);
					x.setSubjectClass(newSubId);
					x.setClassifiction(true);
					activities.set(ctn, x);
				}
				ctn++;
			}
			return "Activity with ID" + actId + " is successfully assigned to Subject with ID" + newSubId;
		} else {
			return "Subject with ID" + newSubId + " does not exist";
		}
	}

	// This method contains the assignment of Messages between activities
	public String improveMessageAssignment(int messId, int newStartId, int newEndId) {
		e = xmlreader.getElementList();
		messages = e.getMessage();
		activities = e.getActivties();
		boolean isActStart = false;
		boolean isActEnd = false;

		// Check if the newStartId and newEndId are existing activities		
		for (Element x : activities) {
			if (newStartId == x.getId()) {
				isActStart = true;
			}
			if (newEndId == x.getId()) {
				isActEnd = true;
			}
		}

		int ctn = 0;

		// If the activites are existign, the messages and their assignment are overwritten
		if (isActStart && isActEnd) {

			for (Element x : messages) {
				if (x.getId() == messId) {
					x.setMessageStartId(newStartId);
					x.setMessageEndId(newEndId);
					messages.set(ctn, x);
				}
				ctn++;
			}
			return "The message with ID" + messId + " is assigned between activity ID" + newStartId + " and ID" + newEndId;
		} else {
			return "The ID of the start or end activity is not correct";
		}
	}
}
