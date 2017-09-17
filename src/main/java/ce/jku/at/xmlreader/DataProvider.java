package ce.jku.at.xmlreader;

import java.io.*;
import java.io.StringReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import ce.jku.at.classification.*;


public class DataProvider { 
	private String eucideanBasedDistance;
	private String classBasedDistance;
	private String assignment;
	private String calculateMess;
	private String startElem;
	private String endElem;
	private XMLReaderList xmlReaderList;
	
	public void dataProvider(String modelUrl, String blue, String red, String yellow, boolean form) {
		XMLReaderList list = new XMLReaderList(blue, red, yellow);
		try {						
			// Create XMLReader
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();

			Client client = ClientBuilder.newClient();
			
			// Set the path for the XML File
			WebTarget target = client.target(modelUrl);
					
			InputSource inputSource = new InputSource(new StringReader(target.request(MediaType.TEXT_XML).get(String.class)));

			// Use a ContentHandler for reading the XML
			xmlReader.setContentHandler(list);

			// Start the parse function
			xmlReader.parse(inputSource);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		// Do the different calculations which are needed for the classification		
		CalculateSubjects calc = new CalculateSubjects(list);
		setEuclideanDistance(calc.calculateEuclideanBasedActivties());
		setMessages(calc.calculateSequence());
		ClassificationSubjects classify = new ClassificationSubjects(list);
		setClassificationDistance(classify.calculateClassBasedActivities());
		CheckInterpretation check = new CheckInterpretation(list);
		setAssignment(check.checkDistances());
		check.sortElemEucl();
		setSubStartElem(check.findStartElem());
		setSubEndElem(check.findEndElem());
		setList(list);
	}
	
	// All Getter and Setter classes
	public void setList (XMLReaderList xmlReaderList) {
		this.xmlReaderList = xmlReaderList;
	}
	
	public XMLReaderList getXMLReaderList() {
		return xmlReaderList;
	}
	
	public void setEuclideanDistance(String eucideanBasedDistance) {
		this.eucideanBasedDistance = eucideanBasedDistance;
	}
	
	public String getEuclideanDistance() {
		return eucideanBasedDistance;
	}
	
	public void setClassificationDistance(String classBasedDistance) {
		this.classBasedDistance = classBasedDistance;
	}
	
	public String getClassificationDistance() {
		return classBasedDistance;
	}
	
	public void setAssignment(String assignment) {
		this.assignment = assignment;
	}
	
	public String getAssignment() {
		return assignment;
	}
	public void setMessages(String calculateMess) {
		this.calculateMess = calculateMess;
	}
	
	public String getMessages() {
		return calculateMess;
	}
	
	public void setSubStartElem(String startElem) {
		this.startElem = startElem;
	}
	
	public String getSubStartElem() {
		return startElem;
	}
	
	public void setSubEndElem(String endElem) {
		this.endElem = endElem;
	}
	
	public String getSubEndElem() {
		return endElem;
	}
	
}
