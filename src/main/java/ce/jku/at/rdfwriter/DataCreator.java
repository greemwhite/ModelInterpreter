package ce.jku.at.rdfwriter;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import ce.jku.at.pojos.Element;
import ce.jku.at.pojos.Elements;
import ce.jku.at.xmlreader.DataProvider;
import ce.jku.at.xmlreader.XMLReaderList;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * 
 * This class creates a rdf graph which is according to the standard pass ontology
 *
 */

public class DataCreator {
	private XMLReaderList xmlreader;

	public DataCreator(XMLReaderList xmlreader) {
		this.xmlreader = xmlreader;
	}

	public void produceOntology(String modelName, String modelID, String startSubjectID) {
		Elements e = xmlreader.getElementList();
		ArrayList<Element> subjects = new ArrayList<Element>();
		ArrayList<Element> activities = new ArrayList<Element>();
		ArrayList<Element> messages = new ArrayList<Element>();
		ArrayList<Element> states = new ArrayList<Element>();
		subjects = e.getSubjects();
		activities = e.getActivties();
		messages = e.getMessage();
		states = e.getStates();

		OntModel m = initOntModel();

		// Name the process model itself
		Individual processModel = m.createIndividual(PassOntology.PASSProcessModel);
		processModel.addProperty(PassOntology.hasModelComponentLable, m.createTypedLiteral(modelName));
		processModel.addProperty(PassOntology.hasModelComponentID, m.createTypedLiteral(modelID));
		
		// Generate the different variants of Subjects
		for (Element sub : subjects) {	
			if(sub.getId() == Integer.parseInt(startSubjectID)) {
				Individual startSubject = m.createIndividual(PassOntology.StartSubject);
				startSubject.addProperty(PassOntology.hasModelComponentLable, m.createTypedLiteral(sub.getName()));
				startSubject.addProperty(PassOntology.hasModelComponentID, m.createTypedLiteral(String.valueOf(sub.getId())));
			} else {
				Individual subject = m.createIndividual(PassOntology.Subject);
				subject.addProperty(PassOntology.hasModelComponentLable, m.createTypedLiteral(sub.getName()));
				subject.addProperty(PassOntology.hasModelComponentID, m.createTypedLiteral(String.valueOf(sub.getId())));
				subject.addProperty(PassOntology.hasMaximumSubjectInstanceRestriction, m.createTypedLiteral(1));
			}
		}		
		
		// Generate the message exchange for the subject interaction diagram
		for (Element mess : messages) {
			Individual messageExchange = m.createIndividual(PassOntology.MessageExchange);
			messageExchange.addProperty(PassOntology.hasModelComponentLable, m.createTypedLiteral(mess.getName()));
			messageExchange.addProperty(PassOntology.hasModelComponentID, m.createTypedLiteral(String.valueOf(mess.getId())));
			
			for (Element act : activities) {
				if(mess.getMessageStartId() == act.getId()) {
					messageExchange.addProperty(PassOntology.hasSender, m.createTypedLiteral(String.valueOf(act.getSubjectClass())));
				}
				if(mess.getMessageEndId() == act.getId()) {
					messageExchange.addProperty(PassOntology.hasReceiver, m.createTypedLiteral(String.valueOf(act.getSubjectClass())));
				}
			}
		}		

		// Generate the different States for the subject behavior diagram 
		for (Element stat : states) {
			if (stat.getFunctionState()) {
				Individual doState = m.createIndividual(PassOntology.DoState);
				doState.addProperty(PassOntology.hasModelComponentLable, m.createTypedLiteral(stat.getName()));
				doState.addProperty(PassOntology.hasModelComponentID, m.createTypedLiteral(String.valueOf(stat.getId())));
				if (stat.getMessageStartId() == 0) {
					doState.addProperty(PassOntology.hasOutgoingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageEndId())));
				} else {
					if (stat.getMessageEndId() == 500) {
						doState.addProperty(PassOntology.hasIncomingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageStartId())));
					} else {
						doState.addProperty(PassOntology.hasIncomingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageStartId())));
						doState.addProperty(PassOntology.hasOutgoingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageEndId())));
					}
				}
			}
			
			if (stat.getSendState()) {
				Individual sendState = m.createIndividual(PassOntology.SendState);
				sendState.addProperty(PassOntology.hasModelComponentLable, m.createTypedLiteral(stat.getName()));
				sendState.addProperty(PassOntology.hasModelComponentID, m.createTypedLiteral(String.valueOf(stat.getId())));
				if (stat.getMessageStartId() == 0) {
					sendState.addProperty(PassOntology.hasOutgoingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageEndId())));
				} else {
					if (stat.getMessageEndId() == 500) {
						sendState.addProperty(PassOntology.hasIncomingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageStartId())));
					} else {
						sendState.addProperty(PassOntology.hasIncomingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageStartId())));
						sendState.addProperty(PassOntology.hasOutgoingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageEndId())));
					}
				}
			}
			
			if (stat.getReceiveState()) {
				Individual receiveState = m.createIndividual(PassOntology.ReceiveState);
				receiveState.addProperty(PassOntology.hasModelComponentLable, m.createTypedLiteral(stat.getName()));
				receiveState.addProperty(PassOntology.hasModelComponentID, m.createTypedLiteral(String.valueOf(stat.getId())));
				if (stat.getMessageStartId() == 0) {
					receiveState.addProperty(PassOntology.hasOutgoingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageEndId())));
				} else {
					if (stat.getMessageEndId() == 500) {
						receiveState.addProperty(PassOntology.hasIncomingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageStartId())));
					} else {
						receiveState.addProperty(PassOntology.hasIncomingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageStartId())));
						receiveState.addProperty(PassOntology.hasOutgoingTransition, m.createTypedLiteral(String.valueOf(stat.getMessageEndId())));
					}
				}
			}
		}			
		
		try {
			FileOutputStream rdfOut = new FileOutputStream("C:/Users/Max Silber/workspace/ModelInterpreter/models/" + modelID + "_" + modelName + ".xml");
			RDFDataMgr.write(rdfOut, m, Lang.RDFXML);
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private static OntModel initOntModel() {
		OntModel m = ModelFactory.createOntologyModel();

		try {
			InputStream in = FileManager.get().open(
					"C:/Users/Max Silber/workspace/ModelInterpreter/src/main/resources/standard-pass-ont-ingolstadt-rebuild.owl");
			try {
				m.read(in, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JenaException je) {
			System.err.println("ERROR" + je.getMessage());
			je.printStackTrace();
			System.exit(0);
		}

		return m;
	}
}