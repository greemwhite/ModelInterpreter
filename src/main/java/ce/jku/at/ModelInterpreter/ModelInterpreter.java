package ce.jku.at.ModelInterpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import org.vaadin.hezamu.canvas.Canvas;
import org.vaadin.hezamu.canvas.Canvas.CanvasImageLoadListener;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import ce.jku.at.classification.CalculateSubjects;
import ce.jku.at.classification.CheckInterpretation;
import ce.jku.at.classification.ImproveAssignment;
import ce.jku.at.classification.TransformAssignment;
import ce.jku.at.pojos.Element;
import ce.jku.at.pojos.Elements;
import ce.jku.at.rdfwriter.DataCreator;
import ce.jku.at.xmlreader.DataProvider;
import ce.jku.at.xmlreader.XMLReaderList;

import com.vaadin.ui.Notification.Type;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */

@SuppressWarnings("serial")
@Theme("mytheme")
public class ModelInterpreter extends UI {
	private String url = new String("");
	private String blue, red, yellow;
	private boolean initial = true;
	private boolean modelForm = true;
	private boolean isActivity = true;
	private boolean modelSelected = false;
	private boolean activityElemSelected = true;
	private TabSheet tabsheet = new TabSheet();
	private Canvas canvas = new Canvas();
	private Canvas orgImage = new Canvas();
	private Canvas messageCanvas = new Canvas();
	private DataProvider data = new DataProvider();
	private XMLReaderList list;
	private int selectedSub = 0;

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		final VerticalLayout layout = new VerticalLayout();

		blue = "Subject";
		red = "Activity";
		yellow = "Message";
		setContent(layout);

		// Title Layout
		HorizontalLayout titleLayout = new HorizontalLayout();
		titleLayout.setWidth("100%");
		Label header = new Label("<h2><b>Interpretation Plattform</b></h2>", ContentMode.HTML);
		header.setStyleName("mypanelcontent");
		Button resetBtn = new Button("Reset Plattform"); 
		// This button(+listener) just resets the fields. Runtime Variables are not reseted.

		// Body Layout
		HorizontalLayout bodyLayout = new HorizontalLayout();

		// Model Selecter Layout
		VerticalLayout selectLayout = new VerticalLayout();
		Label models = new Label("<b>Please select the general Properties</b>", ContentMode.HTML);
		models.setWidth("30%");
		HorizontalLayout selectLayout2 = new HorizontalLayout();
		TextField selectTxt = new TextField("Please Enter Model URL");
		selectTxt.setWidth("350px");
		Button selectBtn = new Button("Select Model");
		Button startBtn = new Button("Run Interpretation");
		startBtn.setSizeUndefined();
		Panel messagePanel = new Panel("Included Elements");
		// This panel includes the images of the selected activities
		messagePanel.setWidth("335px");
		messagePanel.setHeight("350px");
		messageCanvas.setWidth("320px");
		messageCanvas.setHeight("300px");
		messagePanel.setContent(messageCanvas);

		// Classification Layout
		Panel drawingArea = new Panel("Classification Recommendation");
		// The panel is used for a textual logging of the calculating steps
		drawingArea.setWidth("600px");
		drawingArea.setHeight("600px");
		TextArea classify = new TextArea("");
		classify.setHeight("1000px");
		classify.setWidth("1000px");
		classify.setEnabled(false);
		drawingArea.setContent(classify);

		Panel originalPicArea = new Panel("Original ");
		// This panel includes the image of the original structure
		originalPicArea.setHeight("550px");
		originalPicArea.setWidth("510px");
		originalPicArea.setContent(orgImage);
		orgImage.setWidth("500px");
		orgImage.setHeight("500px");

		Panel canvasArea = new Panel("Predicted Model");
		// This panel includes the drawing of the recommended classification
		canvasArea.setWidth("510px");
		canvasArea.setHeight("550px");
		canvas.setWidth("500px");
		canvas.setHeight("500px");
		canvasArea.setContent(canvas);

		tabsheet.addTab(originalPicArea, "Original Model");
		tabsheet.addTab(canvasArea, "Predicted Model");
		tabsheet.addTab(drawingArea, "Classification");

		// Footer Layout
		HorizontalLayout footerLayout = new HorizontalLayout();
		footerLayout.setWidth("70%");
		footerLayout.setSpacing(false);
		Label status = new Label("");
		status.setWidth("550px");
		Button improveBtn = new Button("Improve Assignment");
		improveBtn.setSizeUndefined();
		Button doneBtn = new Button("Accept Classificatition");
		doneBtn.setSizeUndefined();
		footerLayout.addComponents(status, improveBtn, doneBtn);

		// Listeners for tabsheet, buttons and the canvas field
		tabsheet.addSelectedTabChangeListener(selectedTabChange -> {
			showCorrectTab();
		});

		selectBtn.addClickListener(event -> {
			if (!selectTxt.isEmpty()) {
				modelSelected = true;
				tabsheet.setSelectedTab(0);
				url = selectTxt.getValue();
				Notification.show("URL Selected:", url + " is read. Please select the properties.",
						Type.TRAY_NOTIFICATION);
				orgImage.clear();
				orgImage.loadImages(new String[] { url.replaceAll(".xml", ".jpg") });
				orgImage.addImageLoadListener(new CanvasImageLoadListener() {
					public void imagesLoaded() {
						drawImage2Handler(url.replaceAll(".xml", ".jpg"), 0, 0, 500, 500, true);
					}
				});
			}
		});

		Button propBtn = new Button("Properties");
		propBtn.addClickListener(event -> {
			MySub sub = new MySub();
			// Add it to the root component
			UI.getCurrent().addWindow(sub);
		});

		startBtn.addClickListener(event -> {
			if (modelSelected) {
				initial = true;
				DataProvider data = new DataProvider();
				data.dataProvider(url, blue, red, yellow, modelForm);
				if (modelForm) {
					classify.setValue(data.getClassificationDistance() + data.getAssignment() + data.getMessages());
				} else {
					classify.setValue(data.getEuclideanDistance() + data.getMessages());
				}
				canvas.clear();
				drawInitialModel();
				tabsheet.setSelectedTab(1);
				initial = false;
			}
		});

		resetBtn.addClickListener(event -> {
			Page.getCurrent().reload();
		});

		improveBtn.addClickListener(event -> {
			ImproveWindow imp = new ImproveWindow(0);
			UI.getCurrent().addWindow(imp);
		});

		doneBtn.addClickListener(event -> {
			AcceptClassificationDialog acceptClass = new AcceptClassificationDialog();
			UI.getCurrent().addWindow(acceptClass);
		});

		canvas.addMouseDownListener(mouseDetails -> {
			messageCanvas.clear();
			// Scaling is needed to be able to compare with the initial values.
			// Because the canvas field does not start from zero a min-max
			// calculation is used
			double xScaled = ((double) mouseDetails.getClientX() - (double) 500) / ((double) 966 - (double) 500);
			double yScaled = ((double) mouseDetails.getClientY() - (double) 250) / ((double) 650 - (double) 250);

			ImproveWindow imp = new ImproveWindow(findClickedElement(xScaled, yScaled));
			UI.getCurrent().addWindow(imp);

			drawSmallModel(findClickedElement(xScaled, yScaled));
		});

		// Bring Layout together
		titleLayout.addComponents(header, resetBtn);
		selectLayout2.addComponents(propBtn, startBtn);
		selectLayout.addComponents(models, selectTxt, selectBtn, selectLayout2, messagePanel);
		bodyLayout.addComponents(selectLayout, tabsheet);
		layout.addComponents(titleLayout, bodyLayout, footerLayout);
	}

	// This method loads the content of the canvas that the tabsheet does not
	// automatically update
	private void showCorrectTab() {
		if (tabsheet.getTabPosition(tabsheet.getTab(tabsheet.getSelectedTab())) == 0) {
			orgImage.loadImages(new String[] { url.replaceAll(".xml", ".jpg") });
			orgImage.addImageLoadListener(new CanvasImageLoadListener() {
				public void imagesLoaded() {
					drawImage2Handler(url.replaceAll(".xml", ".jpg"), 0, 0, 500, 500, true);
				}
			});
		}

		if (tabsheet.getTabPosition(tabsheet.getTab(tabsheet.getSelectedTab())) == 1) {
			drawInitialModel();
		}
	}

	// This method loads the original elements for providing a visual comparison
	// between the recommended and the original model
	private void drawSmallModel(int elemID) {
		list = data.getXMLReaderList();
		Elements e = list.getElementList();
		ArrayList<Element> subjects = new ArrayList<Element>();
		ArrayList<Element> activities = new ArrayList<Element>();
		ArrayList<Element> messages = new ArrayList<Element>();
		subjects = e.getSubjects();
		activities = e.getActivties();
		messages = e.getMessage();
		String actElemIDs[] = new String[7];

		messageCanvas.clear();

		messageCanvas.setFillStyle("black");
		messageCanvas.setFont("italic bold 12px sans-serif");

		for (Element actAct : activities) {
			if (elemID == actAct.getId()) {
				isActivity = true;
				// First load picture of the subject and add correct ID
				actElemIDs[0] = url.replaceAll("scene_out.xml", "marker_" + actAct.getSubjectClass() + ".jpg");
				messageCanvas.loadImages(new String[] {
						url.replaceAll("scene_out.xml", "marker_" + actAct.getSubjectClass() + ".jpg") });
				messageCanvas.fillText("ID " + String.valueOf(actAct.getSubjectClass()), 150, 40, 50);
				// Then load picture of the activity and also add correct ID
				actElemIDs[1] = url.replaceAll("scene_out.xml", "marker_" + elemID + ".jpg");
				messageCanvas.loadImages(new String[] { url.replaceAll("scene_out.xml", "marker_" + elemID + ".jpg") });
				messageCanvas.fillText("ID " + String.valueOf(elemID), 150, 140, 50);
			}
		}

		for (Element actMess : messages) {
			if (elemID == actMess.getId()) {
				isActivity = false;
				actElemIDs[2] = url.replaceAll("scene_out.xml", "marker_" + elemID + ".jpg");
				messageCanvas.fillText("ID " + elemID, 50, 145, 50);

				for (Element actAct : activities) {
					if (actMess.getMessageStartId() == actAct.getId()) {
						System.out.println(actMess.getMessageStartId());
						actElemIDs[3] = url.replaceAll("scene_out.xml", "marker_" + actAct.getId() + ".jpg");
						messageCanvas.fillText("ID " + actAct.getId(), 20, 180, 50);

						for (Element printSub : subjects) {
							if (printSub.getId() == actAct.getSubjectClass()) {
								actElemIDs[4] = url.replaceAll("scene_out.xml", "marker_" + printSub.getId() + ".jpg");
								messageCanvas.fillText("ID " + printSub.getId(), 20, 110, 50);
							}
						}
					}
					if (actMess.getMessageEndId() == actAct.getId()) {
						actElemIDs[5] = url.replaceAll("scene_out.xml", "marker_" + actAct.getId() + ".jpg");
						messageCanvas.fillText("ID " + actAct.getId(), 280, 180, 50);

						for (Element printSub : subjects) {
							if (printSub.getId() == actAct.getSubjectClass()) {
								actElemIDs[6] = url.replaceAll("scene_out.xml", "marker_" + printSub.getId() + ".jpg");
								messageCanvas.fillText("ID " + printSub.getId(), 280, 110, 50);
							}
						}
					}
				}
				for (int n = 2; n < 7; n++) {
					messageCanvas.loadImages(new String[] { actElemIDs[n] });
				}
			}
		}

		messageCanvas.addImageLoadListener(new CanvasImageLoadListener() {
			public void imagesLoaded() {
				if (isActivity) {
					drawImage2Handler(actElemIDs[0], 75, 50, 150, 75, false);
					drawImage2Handler(actElemIDs[1], 75, 150, 150, 75, false);
				} else {
					drawImage2Handler(actElemIDs[2], 85, 100, 150, 75, false);
					drawImage2Handler(actElemIDs[3], 10, 190, 150, 75, false);
					drawImage2Handler(actElemIDs[4], 10, 10, 150, 75, false);
					drawImage2Handler(actElemIDs[5], 170, 190, 150, 75, false);
					drawImage2Handler(actElemIDs[6], 170, 10, 150, 75, false);
				}
			}
		});
	}

	// This method draws the recommended model on a canvas field
	private void drawInitialModel() {
		if (initial) {
			data.dataProvider(url, blue, red, yellow, modelForm);
		}
		list = data.getXMLReaderList();
		Elements e = list.getElementList();
		ArrayList<Element> subjects = new ArrayList<Element>();
		ArrayList<Element> activities = new ArrayList<Element>();
		ArrayList<Element> messages = new ArrayList<Element>();
		subjects = e.getSubjects();
		activities = e.getActivties();
		messages = e.getMessage();
		String classColors[] = { "orange", "green", "rgb(190, 203, 223)", "rgb(190, 99, 56)", "rgb(13, 99, 56)",
				"rgb(0,255,255)" };
		int countSubs = 0;

		canvas.clear();

		for (Element actSub : subjects) {
			canvas.setFillStyle("blue");
			canvas.fillRect(actSub.getxpos() * 500, actSub.getypos() * 500, 70, 40);
			canvas.setFillStyle(classColors[countSubs]);
			canvas.fillRect(actSub.getxpos() * 500 + 10, actSub.getypos() * 500 + 10, 20, 20);
			canvas.setFillStyle("white");
			canvas.fillRect(actSub.getxpos() * 500 + 40, actSub.getypos() * 500 + 10, 20, 20);
			canvas.setFillStyle("black");
			canvas.setFont("italic bold 12px sans-serif");
			canvas.fillText(String.valueOf(actSub.getId()), actSub.getxpos() * 500 + 45, actSub.getypos() * 500 + 25,
					50);

			for (Element actAct : activities) {
				if (modelForm) {
					if (actSub.getId() == actAct.getSubjectClass()) {
						if (!actAct.getClassification()) {
							canvas.setFillStyle("black");
							canvas.fillRect(actAct.getxpos() * 500 - 5, actAct.getypos() * 500 - 5, 80, 50);
						}
						canvas.setFillStyle("red");
						canvas.fillRect(actAct.getxpos() * 500, actAct.getypos() * 500, 70, 40);
						canvas.setFillStyle(classColors[countSubs]);
						canvas.fillRect(actAct.getxpos() * 500 + 10, actAct.getypos() * 500 + 10, 20, 20);
						canvas.setFillStyle("white");
						canvas.fillRect(actAct.getxpos() * 500 + 40, actAct.getypos() * 500 + 10, 20, 20);
						canvas.setFillStyle("black");
						canvas.setFont("italic bold 12px sans-serif");
						canvas.fillText(String.valueOf(actAct.getId()), actAct.getxpos() * 500 + 45,
								actAct.getypos() * 500 + 25, 50);
					}
				} else {
					if (actSub.getId() == actAct.getSubjectEuc()) {
						canvas.setFillStyle("red");
						canvas.fillRect(actAct.getxpos() * 500, actAct.getypos() * 500, 70, 40);
						canvas.setFillStyle(classColors[countSubs]);
						canvas.fillRect(actAct.getxpos() * 500 + 10, actAct.getypos() * 500 + 10, 20, 20);
						canvas.setFillStyle("white");
						canvas.fillRect(actAct.getxpos() * 500 + 40, actAct.getypos() * 500 + 10, 20, 20);
						canvas.setFillStyle("black");
						canvas.setFont("italic bold 12px sans-serif");
						canvas.fillText(String.valueOf(actAct.getId()), actAct.getxpos() * 500 + 45,
								actAct.getypos() * 500 + 25, 50);
					}
				}
			}
			countSubs++;
		}

		for (Element actMess : messages) {
			canvas.setFillStyle("yellow");
			canvas.fillRect(actMess.getxpos() * 500, actMess.getypos() * 500, 70, 40);
			canvas.setFillStyle("white");
			canvas.fillRect(actMess.getxpos() * 500 + 10, actMess.getypos() * 500 + 10, 20, 20);
			canvas.fillRect(actMess.getxpos() * 500 + 40, actMess.getypos() * 500 + 10, 20, 20);
			canvas.strokeRect(actMess.getxpos() * 500 + 25, actMess.getypos() * 500 - 14, 20, 20);
			canvas.setFillStyle("black");
			canvas.setFont("italic bold 12px sans-serif");
			canvas.fillText(String.valueOf(actMess.getId()), actMess.getxpos() * 500 + 30, actMess.getypos() * 500, 50);
			canvas.fillText(String.valueOf(actMess.getMessageStartId()), actMess.getxpos() * 500 + 15,
					actMess.getypos() * 500 + 22, 50);
			canvas.fillText(String.valueOf(actMess.getMessageEndId()), actMess.getxpos() * 500 + 45,
					actMess.getypos() * 500 + 22, 50);
		}
	}

	// This method returns the element which are selected by the mouseclicks of
	// users
	private int findClickedElement(double x, double y) {
		list = data.getXMLReaderList();
		Elements e = list.getElementList();
		ArrayList<Element> elements = new ArrayList<Element>();
		elements = e.getElements();
		double distanceElemClicked = 1;
		int actElemID = 0;
		CalculateSubjects euclDist = new CalculateSubjects(list);

		for (Element actElem : elements) {
			if (euclDist.euclideanDistance(actElem.getxpos(), actElem.getypos(), x, y) < distanceElemClicked) {
				distanceElemClicked = euclDist.euclideanDistance(x, y, actElem.getxpos(), actElem.getypos());
				actElemID = actElem.getId();
			}
		}
		return actElemID;
	}

	// This method is the handler for the canvas image loader. It is called when
	// the images are one time cached in the browser to get them visualized by
	// one click
	private void drawImage2Handler(String imageURL, double offsetX, double offsetY, double imgWidth, double imgHeight,
			boolean isOrgImage) {
		if (isOrgImage) {
			orgImage.drawImage2(imageURL, offsetX, offsetY, imgWidth, imgHeight);
		} else {
			messageCanvas.drawImage2(imageURL, offsetX, offsetY, imgWidth, imgHeight);
		}
	}

	@WebServlet(urlPatterns = "/*", name = "ModelInterpreterServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = ModelInterpreter.class, productionMode = false)
	public static class ModelInterpreterServlet extends VaadinServlet {
	}

	// This class provides a popup window which is used to select the properties
	// of the original model
	class MySub extends Window {

		public MySub() {
			super("Properties"); // Set window caption
			center();

			VerticalLayout prop_main = new VerticalLayout();
			Panel objPan = new Panel("Please select the color of the elements");
			FormLayout obj = new FormLayout();
			objPan.setContent(obj);

			String color = "Blue,Red,Yellow";
			List<String> colors = Arrays.asList(color.split(","));
			NativeSelect<String> sub = new NativeSelect<>("Subject", colors);
			sub.setIcon(FontAwesome.USER);
			sub.setEmptySelectionAllowed(false);
			if (blue == "Subject") {
				sub.setSelectedItem(colors.get(0));
			} else {
				if (blue == "Activity") {
					sub.setSelectedItem(colors.get(1));
				} else {
					if (blue == "Message") {
						sub.setSelectedItem(colors.get(2));
					}
				}
			}

			NativeSelect<String> act = new NativeSelect<>("Activity", colors);
			act.setIcon(FontAwesome.TAG);
			act.setEmptySelectionAllowed(false);
			if (red == "Activity") {
				act.setSelectedItem(colors.get(1));
			} else {
				if (red == "Message") {
					act.setSelectedItem(colors.get(2));
				} else {
					if (red == "Subject") {
						act.setSelectedItem(colors.get(0));
					}
				}
			}

			NativeSelect<String> mes = new NativeSelect<>("Message", colors);
			mes.setIcon(FontAwesome.ENVELOPE);
			mes.setEmptySelectionAllowed(false);
			if (yellow == "Message") {
				mes.setSelectedItem(colors.get(2));
			} else {
				if (yellow == "Subject") {
					mes.setSelectedItem(colors.get(0));
				} else {
					if (yellow == "Activity") {
						mes.setSelectedItem(colors.get(1));
					}
				}
			}

			obj.setSizeUndefined();
			obj.addComponents(sub, act, mes);

			prop_main.addComponent(objPan);

			Panel layPanel = new Panel("Please select the layout of your physical structure");
			HorizontalLayout lay = new HorizontalLayout();
			layPanel.setContent(lay);

			CheckBox standardCB = new CheckBox("");

			FileResource resource = new FileResource(new File("C:/Users/Max Silber/workspace/ModelInterpreter/src/main/resources/standard.png"));
			Image standardImg = new Image("Standard-Layout", resource);

			CheckBox starCB = new CheckBox("");
			FileResource resource1 = new FileResource(new File("C:/Users/Max Silber/workspace/ModelInterpreter/src/main/resources/star.png"));
			Image starImg = new Image("Star-Layout", resource1);
			starImg.setWidth("20%");

			starCB.addValueChangeListener(event -> {
				if (!starCB.isEmpty()) {
					standardCB.setValue(false);
					modelForm = false;
				}
			});

			standardCB.addValueChangeListener(event -> {
				if (!standardCB.isEmpty()) {
					starCB.setValue(false);
					modelForm = true;
				}
			});

			if (modelForm) {
				standardCB.setValue(true);
			} else {
				starCB.setValue(true);
			}

			lay.addComponents(standardCB, standardImg, starCB, starImg);
			lay.setSizeUndefined();

			prop_main.addComponent(layPanel);

			HorizontalLayout butLayout = new HorizontalLayout();
			butLayout.addComponent(new Button("Save", event -> {
				switch (String.valueOf(sub.getValue())) {
				case "Blue":
					blue = "Subject";
					break;
				case "Red":
					red = "Subject";
					break;
				case "Yellow":
					yellow = "Subject";
					break;
				}

				switch (String.valueOf(mes.getValue())) {
				case "Blue":
					blue = "Message";
					break;
				case "Red":
					red = "Message";
					break;
				case "Yellow":
					yellow = "Message";
					break;
				}

				switch (String.valueOf(act.getValue())) {
				case "Blue":
					blue = "Activity";
					break;
				case "Red":
					red = "Activity";
					break;
				case "Yellow":
					yellow = "Activity";
					break;
				}

				Notification.show("Properties changed: ",
						"Elements: Blue: " + blue + "; Red: " + red + "; Yellow: " + yellow + "; " + "Standard: "
								+ String.valueOf(!standardCB.isEmpty()) + " Star: " + String.valueOf(!starCB.isEmpty()),
						Type.TRAY_NOTIFICATION);
				close();
			}));

			butLayout.addComponent(new Button("Cancel", event -> close()));
			prop_main.addComponent(butLayout);

			setContent(prop_main);
		}
	}

	// This class provides a popup window that shows the options for improving
	// the classification
	class ImproveWindow extends Window {

		public ImproveWindow(int actId) {
			super("Improve Assignment");
			center();
			setSizeUndefined();
			TabSheet improveSelectment = new TabSheet();
			improveSelectment.setWidth("500px");
			improveSelectment.setHeight("450px");

			VerticalLayout improveLayout = new VerticalLayout();
			FormLayout formLayout = new FormLayout();
			HorizontalLayout horizontalLayout = new HorizontalLayout();

			// Fields for the improvement of an activity
			TextField subTxt = new TextField("should accord to Subject ");
			TextField actTxt = new TextField("Activity with ID");
			TextField actSub = new TextField("is actual according to");
			actSub.setEnabled(false);
			actSub.setVisible(false);
			actSub.setValue("");
			improveLayout.setSizeUndefined();
			formLayout.setSizeUndefined();
			horizontalLayout.setSizeUndefined();

			formLayout.addComponents(actTxt, actSub, subTxt);

			// Fields for the improvement of a messageflow
			FormLayout formLayout2 = new FormLayout();
			TextField messTxt = new TextField("Message with ID");
			TextField messAct = new TextField("is exchanged between");
			messAct.setEnabled(false);
			messAct.setVisible(false);
			TextField actTxt1 = new TextField("Start Activity with ID");
			Label actLabel = new Label("should be exchanged between");
			Label actLabel2 = new Label("and");
			TextField actTxt2 = new TextField("End Activity with ID");
			formLayout2.addComponents(messTxt, messAct, actLabel, actTxt1, actLabel2, actTxt2);

			improveSelectment.addTab(formLayout, "Activity Assignment");
			improveSelectment.addTab(formLayout2, "Message Assignment");

			improveLayout.addComponents(improveSelectment, horizontalLayout);
			setContent(improveLayout);

			// By pressing "Save" in the improvement window the improvement gets
			// executed and at the end a notification is shown depending if it
			// is an activity or a message
			horizontalLayout.addComponent(new Button("Save", event -> {
				list = data.getXMLReaderList();
				ImproveAssignment improveAssignment = new ImproveAssignment(list);

				if (activityElemSelected) {
					String res = improveAssignment.improveActivityAssignment(Integer.parseInt(actTxt.getValue()),
							Integer.parseInt(subTxt.getValue()));
					Notification.show("Assignment: ", res, Type.TRAY_NOTIFICATION);
				} else {
					String res = improveAssignment.improveMessageAssignment(Integer.parseInt(messTxt.getValue()),
							Integer.parseInt(actTxt1.getValue()), Integer.parseInt(actTxt2.getValue()));
					Notification.show("Assignment: ", res, Type.TRAY_NOTIFICATION);
				}
				drawInitialModel();
				messageCanvas.clear();
				close();
			}));

			horizontalLayout.addComponent(new Button("Cancel", event -> {
				messageCanvas.clear();
				close();
			}));

			// If the actual model is an activity or a message, the right tab is
			// selected and the fields are filled
			if (actId != 0) {
				list = data.getXMLReaderList();
				Elements e = list.getElementList();
				ArrayList<Element> elements = new ArrayList<Element>();
				elements = e.getElements();

				for (Element act : elements) {
					if (actId == act.getId() && act.getType().equals("Activity")) {
						improveSelectment.setSelectedTab(0);
						actSub.setVisible(true);
						actTxt.setValue(String.valueOf(actId));
						actSub.setValue(String.valueOf(act.getSubjectEuc()));
						activityElemSelected = true;
					}
					if (actId == act.getId() && act.getType().equals("Message")) {
						improveSelectment.setSelectedTab(1);
						messAct.setVisible(true);
						messAct.setValue(String.valueOf("activities " + act.getMessageStartId()) + " and "
								+ String.valueOf(act.getMessageEndId()));
						messTxt.setValue(String.valueOf(act.getId()));
						activityElemSelected = false;
					}
				}
			}
		}
	}

	// This class provides a dialog window which is used to fill in the
	// parameters of the model which are needed for the output of the result
	class AcceptClassificationDialog extends Window {

		public AcceptClassificationDialog() {
			super("Accept Classification"); // Set window caption
			center();
			setSizeUndefined();
			
			VerticalLayout acceptLayout = new VerticalLayout();
			FormLayout formLayout = new FormLayout();
			HorizontalLayout horizontalLayout = new HorizontalLayout();
			acceptLayout.setSizeUndefined();
			formLayout.setSizeUndefined();
			horizontalLayout.setSizeUndefined();			

			TextField modelName = new TextField("Please enter the name of the model");
			TextField modelId = new TextField("The automatic generated model id");
			TextField startSubject = new TextField("Please enter the id of the start subject");

			// The model id is part of the url string and extracted for the
			// dialog window
			String[] urlParts = url.split("/");
			modelId.setValue(urlParts[8]);

			horizontalLayout.addComponent(new Button("Done", event -> {
				// Check that the textfields are not empty
				if(!modelName.getValue().equals("") && !modelId.getValue().equals("") && !startSubject.getValue().equals("")) {
					// Check the calculation type --> if euclidean based, open the window for the improvement of the ranking, else call the transformation
					if(modelForm) {
						TransformAssignment transformStructure = new TransformAssignment(list);
						transformStructure.provideStates();
						// createStates.provideInteraction();
						DataCreator createRDF = new DataCreator(list);
						createRDF.produceOntology(modelName.getValue(), modelId.getValue(), startSubject.getValue());
						Notification.show("Model successfully exported",
								"To interpret another model please reload the site (press F5)", Type.TRAY_NOTIFICATION);
					}else {
						AcceptClassificationReOrderDialog reOrder = new AcceptClassificationReOrderDialog(modelName.getValue(), modelId.getValue(), startSubject.getValue());
						UI.getCurrent().addWindow(reOrder);
					}
					close();
				}				
			}));

			horizontalLayout.addComponent(new Button("Cancel", event -> {
				close();
			}));

			// Bring Layout of the Dialog together
			formLayout.addComponents(modelName, modelId, startSubject);
			acceptLayout.addComponents(formLayout, horizontalLayout);
			setContent(acceptLayout);			
		}
	}
	
	// 	
	class AcceptClassificationReOrderDialog extends Window {

		public AcceptClassificationReOrderDialog(String modelName, String modelId, String startSubject) {
			super("Check Message Order"); // Set window caption
			center();
			setSizeUndefined();
			
			VerticalLayout reOrderLayout = new VerticalLayout();
			HorizontalLayout horizontalLayout = new HorizontalLayout();
			reOrderLayout.setSizeUndefined();
			horizontalLayout.setSizeUndefined();
			
			
			list = data.getXMLReaderList();
			
			Elements e = list.getElementList();
			ArrayList<Element> subjects = new ArrayList<Element>();
			subjects = e.getSubjects();
			
			StringBuilder subs = new StringBuilder();
			
			for (Element s : subjects) {
				subs.append(s.getName() + " ; " + s.getId() + ",");
			}
			
			List<String> subSelect = Arrays.asList(subs.toString().split(","));
			RadioButtonGroup<String> subjectSelect = new RadioButtonGroup<>(
	                "Please select a subject", subSelect);
			
			TwinColSelect<String> selectActivities = new TwinColSelect<>();
			selectActivities.setRows(8);
			selectActivities.setWidth("700px");
			selectActivities.setLeftColumnCaption("Calculated ranking of the activities:");
			selectActivities.setRightColumnCaption("Select 2 activities which order should be changed:");
			
			subjectSelect.addValueChangeListener(event -> {
				String index = event.getValue();
				String []indexParts = index.split(" ; ");
				selectedSub = Integer.parseInt(indexParts[1]);
				selectActivities.setItems(getActivitiesList(selectedSub));				
			});
			
							
			horizontalLayout.addComponent(new Button("Change order", event -> {
				Set<String> rightSelectedItems = selectActivities.getSelectedItems();
				String []elems = rightSelectedItems.toArray(new String[(rightSelectedItems.size())]);
				
				changeActivities(elems[0], elems[1]);
				selectActivities.setItems(getActivitiesList(selectedSub));
			}));
			
			horizontalLayout.addComponent(new Button("Cancel", event -> {
				close();
			}));
			
			horizontalLayout.addComponent(new Button("Done", event -> {
				selectActivities.setItems(getActivitiesList(selectedSub));
				TransformAssignment transformStructure = new TransformAssignment(list);
				transformStructure.provideStates();
				// createStates.provideInteraction();
				DataCreator createRDF = new DataCreator(list);
				createRDF.produceOntology(modelName, modelId, startSubject);
				Notification.show("Model successfully exported",
					"To interpret another model please reload the site (press F5)", Type.TRAY_NOTIFICATION);
				close();
			}));
			
			reOrderLayout.addComponents(subjectSelect, selectActivities, horizontalLayout);
			setContent(reOrderLayout);
		}
	}
	
	public List<String> getActivitiesList(int index) {
		list = data.getXMLReaderList();
		Elements e = list.getElementList();
		ArrayList<Element> activities = new ArrayList<Element>();
		activities = e.getActivties();	
		Collections.sort(activities, new distanceObjectComparator());
		List<String> returnActivities = new ArrayList<>();
		
		for(Element a :  activities) {
			if(a.getSubjectEuc() == index) {
				returnActivities.add(a.getName() +  " ; " + a.getId());
			}
		}
		
		return returnActivities;		
	}
	
	public void changeActivities(String elem1, String elem2) {
		String []act1Id = elem1.split(" ; ");
		String []act2Id = elem2.split(" ; ");
		
		list = data.getXMLReaderList();
		Elements e = list.getElementList();
		ArrayList<Element> activities = new ArrayList<Element>();
		activities = e.getActivties();
		
		
		double oldDistance = 0;
		double oldYpos = 0;
		for(Element a1: activities) {
			if(a1.getId() == Integer.parseInt(act1Id[1])) {
				oldDistance = a1.getDistance();
				oldYpos = a1.getypos();
				for(Element a2 : activities) {
					if(a2.getId() == Integer.parseInt(act2Id[1])) {
						a1.setDistance(a2.getDistance());
						a2.setDistance(oldDistance);
						if(a1.getypos() < a2.getypos()) {
							a1.setYpos(a2.getypos());
							a2.setYpos(oldYpos);
						}
					}
				}
			}
		} 
	}	
}

class distanceObjectComparator implements Comparator<Element> {
	@Override
	public int compare(Element e1, Element e2) {
		return e1.getDistance() < e2.getDistance() ? -1 : e1.getDistance() == e2.getDistance() ? 0 : 1;
	}
}
