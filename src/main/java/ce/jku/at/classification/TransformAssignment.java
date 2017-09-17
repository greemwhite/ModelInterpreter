package ce.jku.at.classification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ce.jku.at.pojos.Element;
import ce.jku.at.pojos.Elements;
import ce.jku.at.xmlreader.XMLReaderList;

/*
 * 
 * This class transforms the correct assigned elements into usable states for the S-BPM structure,
 * according the rules given by the paper "Articulation of subject-oriented business process models", S.Oppl(2015)
 * 
 * The Transformation of the elements to states for the subject behavior diagram is completely implemented.
 * The Subject Interaction Diagram is only solved for providing a sysout overview of the whole transformation rules.
 *  
 */

public class TransformAssignment {
	private XMLReaderList xmlreader;

	public TransformAssignment(XMLReaderList xmlreader) {
		this.xmlreader = xmlreader;
	}

	// This method transforms the card based structure in states which are
	// useable in the subject behavior diagram
	// Because subjects are already filtered in an own list no other
	// transformation is necessary (Paper p. 5, Point 1)
	// The other transformation rules are implemented in the following method
	// (except p. 6, points 4, 5, 7)
	public void provideStates() {
		Elements e = xmlreader.getElementList();
		ArrayList<Element> subjects = new ArrayList<Element>();
		ArrayList<Element> activities = new ArrayList<Element>();
		ArrayList<Element> messages = new ArrayList<Element>();
		ArrayList<Element> states = new ArrayList<Element>();
		subjects = e.getSubjects();
		activities = e.getActivties();
		messages = e.getMessage();

		// Activities (WHAT elements) are directly transformed into function
		// states (Paper p. 5, Point 2)
		for (Element a : activities) {
			a.setFunctionState(true);
			a.setSendState(false);
			a.setReceiveState(false);
			states.add(a);
		}

		// Messages (Exchange items) are converted in new States
		for (Element m : messages) {
			for (Element a : activities) {
				// If a message is outgoing it is transformed in a Send State.
				// Therefore the word Sending is appended and a new unique ID is
				// generated (Paper p. 6, Point 6)
				if (m.getMessageStartId() == a.getId()) {
					Element newElem = new Element();
					newElem.setSubjectClass(a.getSubjectClass());
					newElem.setName("Sending " + m.getName());
					newElem.setId(m.getId() + 100);
					newElem.setDistance(a.getDistance() + 0.001);
					newElem.setYpos(a.getypos() + 0.001);
					newElem.setFunctionState(false);
					newElem.setSendState(true);
					newElem.setReceiveState(false);
					states.add(newElem);
				}

				// If a message is incoming those message is transformed into a
				// Receive State. Therefore the word Receiving is appended and a
				// new unique ID is generated (Paper p. 6, Point 8)
				if (m.getMessageEndId() == a.getId()) {
					Element newElem = new Element();
					newElem.setSubjectClass(a.getSubjectClass());
					newElem.setName("Receiving " + m.getName());
					newElem.setId(m.getId() + 200);
					newElem.setDistance(a.getDistance() - 0.001);
					newElem.setYpos(a.getypos() - 0.001);
					newElem.setFunctionState(false);
					newElem.setSendState(false);
					newElem.setReceiveState(true);
					states.add(newElem);
				}
			}
		}

		// Because the relationship between the elements is defined as a
		// downward sequence the states are sorted according their y-position
		// (Paper p. 5, Point 3)
		Collections.sort(states, new yposComparator());
		
		// Sort the list according the subjects a basis for the next step
		Collections.sort(states, new subjectComparator());
		// Calculate the incoming and outgoing state for the subject behavior diagram
		int actId = 0;
		// The start and the end state are marked with a special id (start[0]; end[500])
		for (int i = 0; i < states.size(); i++) {
			if (i+1 < states.size() && states.get(i).getSubjectClass() == states.get(i+1).getSubjectClass()) {
				states.get(i).setMessageStartId(actId);
				states.get(i).setMessageEndId(states.get(i+1).getId());
				actId = states.get(i).getId();
			} else {
				states.get(i).setMessageStartId(actId);
				states.get(i).setMessageEndId(500);
				actId = 0;
			}
		}
		
		// Save States according their sequence
		for (Element s : states) {
			e.setStates(s);
		}

		// Printout tests if it works correct
		/*for(Element s : states) {
			System.out.println(s.getName() + ": " + s.getId() + "; " + s.getSubjectClass() + "; " + s.getMessageStartId() + "; " + s.getMessageEndId() );
		}
		
		for (Element s : subjects) { 
			System.out.println("\n" + s.getName() + "\n"); 
			for (Element a : states) { 
				if (a.getSubjectClass() ==  s.getId()) {
					System.out.println(a.getName() + "; " + a.getId() + "; " + a.getypos()); 
				} 
			}
		}*/
		 
	}

	// This method transforms the card based structure that it can be used in a
	// subject interaction diagram
	// It just prints out the ID's of the subject between the messages are
	// exchanged
	public void provideInteraction() {
		Elements e = xmlreader.getElementList();
		ArrayList<Element> activities = new ArrayList<Element>();
		ArrayList<Element> messages = new ArrayList<Element>();
		activities = e.getActivties();
		messages = e.getMessage();

		for (Element m : messages) {
			for (Element a : activities) {
				if (m.getMessageStartId() == a.getId() || m.getMessageEndId() == a.getId()) {
					System.out.println(a.getSubjectClass());
				}
			}
		}
	}
}

class yposComparator implements Comparator<Element> {
	@Override
	public int compare(Element e1, Element e2) {
		return e1.getypos() < e2.getypos() ? -1 : e1.getypos() == e2.getypos() ? 0 : 1;
	}
}

class subjectComparator implements Comparator<Element> {
	@Override
	public int compare(Element e1, Element e2) {
		return e1.getSubjectClass() < e2.getSubjectClass() ? -1 : e1.getSubjectClass() == e2.getSubjectClass() ? 0 : 1;
	}
}