package ce.jku.at.pojos;

import java.util.ArrayList;

public class Classes {
	
	private ArrayList<Classify> classes = new ArrayList<>();
	
	// 	Getter for the classification with following structure:
	//	ID - x-Start - x-End - y-Start - y-End
	public ArrayList<Classify> getClasses() {
		return classes;
	}
	
	public void setClass(Classify c) {
		classes.add(c);
	}
	
	
}
