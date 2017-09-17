package ce.jku.at.pojos;

import java.util.ArrayList;

public class Elements  {
	private ArrayList <Element> elements = new ArrayList<Element>();
	private ArrayList <Element> subjects = new ArrayList<Element>();
	private ArrayList <Element> activities = new ArrayList<Element>();
	private ArrayList <Element> messages = new ArrayList<Element>();
	private ArrayList <Element> states = new ArrayList<Element>();
 	
	public Elements() {
	}
	
	// 	Getter for the elements which is returing the whole elements (subjects, activities, messages)
	public ArrayList<Element> getElements() {
		return elements;
	}
	
	// Getter for the subjects only
	public ArrayList<Element> getSubjects() {
		return subjects;
	}
	
	// Getter for the activities only
	public ArrayList<Element> getActivties() {
		return activities;
	}
	
	// Getter for the messages only
	public ArrayList<Element> getMessage() {
		return messages;
	}
	
	public ArrayList<Element> getStates() {
		return states;
	}
	
	// SetElements does not only add the element to the element list, it also enables the 
	// differentiation according the type of the element (subject, message, activity) and adds
	// it to the specified list.
	public void setElements(Element e) {
		elements.add(e);
		
		if(e.getType().equals("Subject")) {
			subjects.add(e);
		} else {
			if(e.getType().equals("Activity")) {
				activities.add(e);
			} else {
				if(e.getType().equals("Message")) {
					messages.add(e);
				}
			}
		}
	}
	
	public void setStates(Element e) {
		states.add(e);
	}
}
