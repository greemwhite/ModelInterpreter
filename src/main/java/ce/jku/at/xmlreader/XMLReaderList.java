package ce.jku.at.xmlreader;

import java.util.*;
import org.xml.sax.*;
import ce.jku.at.pojos.*;

public class XMLReaderList implements ContentHandler {
	private ArrayList<Element> elements = new ArrayList<Element>();
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private String currentValue = "";
	private Element elem;
	private Connection con;
	private EndPoint end;
	private boolean iselem = false;
	private boolean iscon = false;
	private boolean isendpoint = false;
	private Elements e = new Elements(); 
	private Connections c = new Connections();
	
	private String blue, red, yellow;

	public XMLReaderList(String blue, String red, String yellow) {
		this.blue = blue;
		this.red = red;
		this.yellow = yellow;
	}

	// 	Getter for the elements
	public Elements getElementList() {
		return e;
	}
	
	// 	Getter for the connections	
	public Connections getConnectionList() {
		return c;
	}
	
	//	Method for saving input chars ind currentValue
	public void characters(char[] ch, int start, int length) throws SAXException {
		currentValue = new String(ch, start, length);
	}

	//	Seachring for the start element and setting the right boolean for the case
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		// Check if it is an element
		if (localName.equals("Element")) {
			iselem = true;
			iscon = false;
			isendpoint = false;
			elem = new Element();

			// Change the attributes from read in to the selected ones
			if (atts.getValue("xsi:type").equals("XmlYellowElement")) {
				elem.setType(yellow);
			} else {
				if (atts.getValue("xsi:type").equals("XmlRedElement")) {
					elem.setType(red);
				} else {
					if (atts.getValue("xsi:type").equals("XmlBlueElement")) {
						elem.setType(blue);
					}
				}
			}
		}

		// Check if it is a connection
		if (localName.equals("Connection")) {
			iselem = false;
			iscon = true;
			isendpoint = false;
			con = new Connection();
		}

		// Check if it is a StartPoint
		if (localName.equals("endPoint1")) {
			iselem = false;
			iscon = true;
			isendpoint = true;
			end = new EndPoint();

			// Same way of transforming the attributes
			if (atts.getValue("xsi:type").equals("XmlYellowElement")) {
				end.setElement(yellow);
			} else {
				if (atts.getValue("xsi:type").equals("XmlRedElement")) {
					end.setElement(red);
				} else {
					if (atts.getValue("xsi:type").equals("XmlBlueElement")) {
						end.setElement(blue);
					}
				}
			}
		}

		// Check if it is an EndPoint
		if (localName.equals("endPoint2")) {
			iselem = false;
			iscon = true;
			isendpoint = true;
			end = new EndPoint();

			// Same way of transforming the attributes
			if (atts.getValue("xsi:type").equals("XmlYellowElement")) {
				end.setElement(yellow);
			} else {
				if (atts.getValue("xsi:type").equals("XmlRedElement")) {
					end.setElement(red);
				} else {
					if (atts.getValue("xsi:type").equals("XmlBlueElement")) {
						end.setElement(blue);
					}
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		//	If elements are saved, they are always stored in the fields of elem
		if (iselem) {
			if (localName.equals("UUID")) {
				elem.setId(Integer.parseInt(currentValue));
			}
			if (localName.equals("name")) {
				if (currentValue.equals("\n  ")) {
					elem.setName("");
				} else {
					elem.setName(currentValue);
				}
			}

			if (localName.equals("x")) {
				elem.setXpos(Double.parseDouble(currentValue));
			}

			if (localName.equals("y")) {
				elem.setYpos(Double.parseDouble(currentValue));
			}

			if (localName.equals("angle")) {
				elem.setAngle(Double.parseDouble(currentValue));
			}

			if (localName.equals("Element")) {
				elements.add(elem);
				e.setElements(elem);
			}
		}

		// If a connection is stored, the start and the end points are also stored within
		if (iscon) {
			if (!isendpoint) {
				if (localName.equals("UUID")) {
					con.setUuid(Integer.parseInt(currentValue));
				}

				if (localName.equals("name")) {
					if (currentValue.equals("\n  ")) {
						con.setConName("");
					} else {
						con.setConName(currentValue);
					}
				}
			}
			
			if (isendpoint) {
				if (localName.equals("UUID")) {
					end.setId(Integer.parseInt(currentValue));
				}

				if (localName.equals("endPoint1")) {
					con.setStartPoint(end);
				}
				if (localName.equals("endPoint2")) {
					con.setEndPoint(end);
				}
			}

			if (localName.equals("directed1")) {
				con.setStartDirection(currentValue.equals("true"));
			}

			if (localName.equals("directed2")) {
				con.setEndDirection(currentValue.equals("true"));
			}

			if (localName.equals("Connection")) {
				connections.add(con);
				c.setConnection(con);
			}
		}
	}

	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processingInstruction(String arg0, String arg1) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDocumentLocator(Locator arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startPrefixMapping(String arg0, String arg1) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}
}
