package com.timeline;

import com.timeline.customNodes.RatingBox;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import sql.EventMethods;
import sql.TimelineMethods;
import sql.UserMethods;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Elias Holmér
 */
public class PrimaryWindowController implements Initializable {
    private Timeline currentShownTimeline = new Timeline();
    private Slider zoomSlider = new Slider();
    private ArrayList<TimelineEvent> eventsArr;
    private Search search = new Search();

    // Ratings items
    @FXML RatingBox ratingBox = new RatingBox();

    @FXML private Pane menu;
    @FXML private Pane eventPane;
    @FXML private Pane adminPane;
    @FXML private Pane timelineLinePane;
    @FXML private Pane exportPane;

    @FXML private ScrollPane scrollPane;
    @FXML private ScrollPane adminMenuScrollPane;
    @FXML private ScrollPane timelineScrollPane;

    // For switching theme UI
    @FXML private Button switchThemeBtn;
    @FXML private Pane topBar;
    @FXML private BorderPane rootPane;
    @FXML private ImageView menuImage;
    private String currentTheme;
    private String darkTheme = getClass().getResource("/css/dark.css").toExternalForm();
    private String lightBg = "-fx-background-color: #45648C";
    private String darkBg = "-fx-background-color: #1E2C3D";

    @FXML private Button sign;
    @FXML private Button createButton;
    @FXML private Button createEventButton;
    @FXML private Button editButton;
    @FXML private Button exportTimelineBtn;
    @FXML private Button popup;
    @FXML private Button scrollUp;

    @FXML private ToggleButton menuButton;
    @FXML private ToggleButton adminMenuButton;

    @FXML private Label activeTimelineName;
    @FXML private Label unitName;

    @FXML private Line timelineLine;
    @FXML private Polyline endUnitPolygon;
    @FXML private Polygon scrollLeft;
    @FXML private Polygon scrollRight;

    @FXML private ImageView showZoomSlider;

    @FXML private Text userName;
    @FXML private Text startUnitTag;
    @FXML private Text endUnitTag;
    private double scrollValue = 0;
    private ArrayList<TimelineEvent> currentEventsArr = null;
    private List<Node> diamondList = null;
    private List<Node> infoBoxList = null;

    // - Timeline Of The Day nodes -
    @FXML private ImageView toddImg;
    @FXML private Button setAsToddButton;

    /**
     * Gets called when primaryWindow loads, populates the menu with timeline objects from the database.
     * Loads log-in button if the user proceeds without logging in, and a log-out button if logged in.
     * If the user is logged in, checks admin status and loads appropriate UI
     * Adds a changeListener to AppProperties.listGlobal that shows a newly created timeline in the view
     * Checks whether a key directory exists, if it doesn't it is created
     * @author Elias Holmér (creator of the fillMenu method)?
     * @modified-by Joel Salo js225fg - re-made the fillMenu method to initialize on load instead in order to reduce
     * the amount of necessary calls to the database, full credits to the original author
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        buildMenu(AppProperties.listGlobal);

//        Check if user is registered.
        Image img;
        if (AppProperties.getUser().getUsername() == null) {
            img = new Image(getClass().getResourceAsStream("/images/main/menu/signInButton.png"));
        } else  {
            img = new Image(getClass().getResourceAsStream("/images/main/menu/signOutButton.png"));
        }
        sign.setGraphic(new ImageView(img));

//        Check if user is admin.
        if (AppProperties.getUser().isAdmin()) {
            adminMenu(AppProperties.userList);
            createButton.setVisible(true);
            editButton.setVisible(true);
            adminMenuButton.setVisible(true);
            createEventButton.setDisable(true);
            editButton.setDisable(true);
            setAsToddButton.setDisable(true);
            exportTimelineBtn.setVisible(true);
            exportTimelineBtn.setDisable(true);
            ratingBox.setVisible(false);

        } else {
            createButton.setVisible(false);
            editButton.setVisible(false);
            adminMenuButton.setVisible(false);
            createEventButton.setVisible(false);
            setAsToddButton.setVisible(false);
            exportTimelineBtn.setVisible(false);
            ratingBox.setDisable(true);
            ratingBox.setVisible(false);
        }
//        Listen for changes in list of timelines.
        AppProperties.listGlobal.addListener(new ListChangeListener<Timeline>() {
            @Override
            public void onChanged(Change<? extends Timeline> change) {
                if (AppProperties.getLatestTimeline() != null && currentShownTimeline != null) {
                    handleTimeLineEvent(AppProperties.getLatestTimeline());
                }
                updateMenu();
            }
        });

        // Making a check whether the directory to store saved images from the server exists. If it doesn't, create it.
        File imgDir = new File(AppProperties.eventImgPath);
        if (!imgDir.isDirectory()) imgDir.mkdir();
    }

    /**
     * Returns the number of events currently active.
     * @return int
     * @author Stefan J 05-14
     */
    public int getNoOfEvents() {
        return currentEventsArr.size();
    }

    /**
     * Updates the menu with existing timeline objects.
     * @author Joel Salo jss225fg
     */
    public void updateMenu() {
        buildMenu(AppProperties.listGlobal);
    }

    /**
     * Populates the menu with existing timeline objects currently stored.
     * @author Elias Holmér (creator of the fillMenu method)?
     * @modified-by Joel Salo js225fg - broke out the algorithm for populating the menu for re-usability, full credits
     * to the original author
     * @modified-by Anton Munter - Changed from a Menu to a scrollpane.
     *
     */
    private void buildMenu(ObservableList<Timeline> timelines) {

        // Username check
        if (AppProperties.getUser().getUsername() != null)
            userName.setText("Logged in as: " + AppProperties.getUser().getUsername());

        VBox boxOfTimelines = new VBox();
        if (!timelines.isEmpty()) {
            boxOfTimelines = fillTimelineBox(timelines);
        }
        else {
            Text text = new Text("No Timelines :(");
            text.getStyleClass().add("noTimelines");
            boxOfTimelines.getChildren().add(text);
        }
        scrollPane.setContent(boxOfTimelines);
    }

    /**
     * Fill a VBox with a button for each timeline.
     * @param OBtimelines
     * @return VBox with timelines.
     * @author Anton Munter
     * @modified by Stefan Jägstrand 05-12 - Adding TODD check
     */
    private VBox fillTimelineBox(ObservableList<Timeline> OBtimelines) {

        // Create a copy of passed list and sort it.
        // The copy is so that the changeListener on listGlobal wont fire every time
        // the an element is moved when sorting.
        // The sorting is to get TODD at the very top of the menu! / SJ
        List<Timeline> timelines = OBtimelines.stream().collect(Collectors.toList());
        timelines.sort(null);

        VBox vBox = new VBox();
        ObservableList<HBox> list = FXCollections.observableArrayList();
        int count = 0;
        for (Timeline timeline : timelines) {
            if (count == 6) count = 0;

            HBox hBox = new HBox();

            Button button = new Button();

            // Add event
            button.setOnAction(e -> {
                handleTimeLineEvent(timeline);
                AppProperties.updateLatestTimeline(timeline);
            });

//            Add tooltip
            if (!timeline.getKeywords()[0].isEmpty()) {
                StringBuilder keyWords = new StringBuilder("Keywords: ");
                for (int i = 0; i < timeline.getKeywords().length; i++) {
                        keyWords.append(timeline.getKeywords()[i]).append(", ");
                }
                Tooltip tooltip = new Tooltip();
                tooltip.setText(String.valueOf(keyWords));
                tooltip.setAutoHide(true);
                button.setTooltip(tooltip);
            }

            // Add style and text
            button.getStyleClass().clear();
            button.getStyleClass().add("timeLineButton");

            // Add CSS class depending on if timeline is TODD or not.
            if (timeline.isTODD()) {
                button.getStyleClass().add("buttonTodd");
                button.setText(timeline.getName());
            } else {
                button.getStyleClass().add("button"+count++);
                button.setText(timeline.getName());
            }

            hBox.getChildren().add(button);

            if (AppProperties.getUser().isAdmin()) {
                button.getStyleClass().add("adminWidth");
                Button rmButton = new Button();
                rmButton.getStyleClass().clear();
                rmButton.getStyleClass().add("rmButton");
                rmButton.setText("  X               Tobbe är bäst!");
                rmButton.setOnAction(e -> {
                    handleRemoveTimeLine(timeline);
                });
                hBox.getChildren().add(rmButton);
            }

            list.add(hBox);
        }
        vBox.getChildren().addAll(list);
        return vBox;
    }


    /**
     * Update shown timelines when searchbar is changed.
     * @author Anton Munter
     */
    public void searchTimeline(Event event) {
//        Get text from searchbar.
        TextField tf = (TextField) event.getSource();
        String searchText = tf.getText();

        if (searchText.isEmpty()) { // Update menu with all timelines if searchbar is empty.
            updateMenu();
        } else { // Populate menu with the current searchResults
            ArrayList<Timeline> tl = search.searchNameAndKeywords(searchText);

            ObservableList<Timeline> timelines = FXCollections.observableArrayList(tl);
            buildMenu(timelines);
        }
    }


    /**
     * Used to scroll the scrollPane up and down.
     * @param event The button clicked
     * @author Anton Munter
     */
    public void scroll(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String id = btn.getId();
        double pos = scrollPane.getVvalue();
        if (id.equals("scrollUp"))
            scrollPane.setVvalue(pos - 0.1);
        else
            scrollPane.setVvalue(pos + 0.1);
    }

    /**
     * Opens the CreateTimeline Popup window when the user clicks the 'create' button
     * Passes over a list of the timelines currently loaded into the app to the window, considering implementing a refresh on a separate thread when starting the window which
     * would make that message superfluous.
     * @param event
     * @author Mattias Holmgren mh224ps
     */
    public void createTimeline(ActionEvent event) {
        // Load second scene
        SceneMethods.openPopup(View.CREATENEW, isDarkTheme(), darkBg, lightBg);
        CreateTimelinePopupController controller = (CreateTimelinePopupController) SceneMethods.getController(View.CREATENEW);
    }

    /**
     * Used for changing color on pop-ups
     * @author Victor Ionzon vi222bk
     */
    public boolean isDarkTheme() {
        return currentTheme != null && currentTheme.contains(darkTheme);
    }

    /** Creates the window for edit timeline.
     *  Called when user clicks 'edit timeline' button.
     * @param event
     * @author ???
     * @modified Stefan Jägstrand 05-06 added reference to this controller with setParentController()
     * @modified Victor Ionzon commented out code and added so that it passes through View enum
     * @modified Stefan & Victor 05-14 - Fixed bug where separate controllers were created.
     */
    public void editTimeline(ActionEvent event) {
        // Load FXML
        FXMLLoader theLoader = SceneMethods.openPopup(View.EDITTIMELINE, isDarkTheme(), darkBg, lightBg);
        EditTimelinePopupController controller = theLoader.getController();
        controller.setEventlist(currentEventsArr);
        // Set reference to 'this' PrimaryWindowController
        controller.setParentController(this);
    }

    public Timeline getCurrentShownTimeline(){
        return currentShownTimeline;
    }


    /**
     * Opens the CreateEvent Popup window
     *
     * @param event
     * @author Fredrik Ljunger fl222ai
     * @modified Stefan Jägstrand 04/27: Added setParentController(this)
     * @modified Victor Ionzon commented out code and added so that it passes through View enum
     * @modified Stefan & Victor 05-14 - Fixed bug where separate controllers were created.
     */
    public void addEventPopup(ActionEvent event) {
        FXMLLoader theLoader = SceneMethods.openPopup(View.ADDEVENT, isDarkTheme(), darkBg, lightBg);
        CreateEventPopupController controller = theLoader.getController();

        // Pass the current shown Timeline to the controller (To get Name and ID in the popup)
        controller.setParentController(this); // Set reference to "this" controller! SJ

        controller.setCurrentTimeLine(currentShownTimeline);
        controller.setNameAndID();
        controller.addTooltips();
    }

    /**
     * Make menu visible when menubutton is pressed.
     * @author Anton Munter
     */
    public void menuToggle() {
        menu.setVisible(!menu.isVisible());
    }

    /**
     * Make menu visible when togglebutton is pressed.
     * @author Anton Munter
     */
    public void adminMenuToggle() {
        adminPane.setVisible(!adminPane.isVisible());
    }

    /**
     * Hide menu when a timeline is selected.
     * @author Eric Haga
     */
    public void hideMenu() {
        menu.setVisible(false);
        adminPane.setVisible(false);
    }

    /**
     * Handle the event when timeline is clicked.
     * @param timeLine The timeline clicked.
     * @author Anton Munter
     * @modified-by Patrik Hasselblad (Added some minor visibility for timeline in main window)
     * @modified-by Victor Ionzon (Added unitName to be displayed under the timeline)
     * @modified-by Mattias Holmgren (Added zoomTimeline method, and zoomButton set to visible)
     * @modified-by Stefan Jägstrand (Adding TODD-check) 05-11
     */
    public void handleTimeLineEvent(Timeline timeLine) {
        currentShownTimeline = timeLine;
        hideMenu();
        zoomTimeline();
        showZoomSlider.setVisible(true);

        // Groundwork for displaying proper timeline information (Very ugly and basic).
        activeTimelineName.setText(timeLine.getName());
        eventPane.setVisible(true);
        timelineLine.setVisible(true);
        endUnitPolygon.setVisible(true);
        startUnitTag.setVisible(true);
        endUnitTag.setVisible(true);

        createEventButton.setDisable(false);
        editButton.setDisable(false);
        setAsToddButton.setDisable(false);
        exportTimelineBtn.setDisable(false);
        startUnitTag.setText(Long.toString(timeLine.getStartUnit()));
        endUnitTag.setText(Long.toString(timeLine.getEndUnit()));
        unitName.setText(timeLine.getUnitName());
        populateTimeLine();

        // Render TODD-stuff if timeline is Timeline of the day /SJ
        renderTODDNodes(timeLine);

        // Set rating stuff
        ratingBox.removeEventHandler();
        ratingBox.setTL(timeLine);
        ratingBox.addEventHandler();
        if(AppProperties.getUser().isAdmin()) {
            ratingBox.setVisible(true);
        }
    }

    /**
     * Export the currently viewed timeline.
     * @author Joel Salo js225fg
     */
    public void exportTimeline() {
        populateTimeLine();
        ExportImage.capture(exportPane, switchThemeBtn, showZoomSlider, setAsToddButton, toddImg, ratingBox);
    }

    /**
     * Slider intended for zooming/scaling the timeline. Stored inside a PopOver, content shows when magnifying
     * glass is clicked.
     * @author Mattias Holmgren mh224ps
     *
     * @author Mattias Holmgren mh224ps
     * @modified-by Fredrik Ljungner fl222ai
     * @modified-by Eric Haga 05/14; Made changes on visibility of events depending on the distance between them.
     */
    public void zoomTimeline() {
        // PopOver content
        Label label = new Label("Set Zoom");
        Label tip = new Label("Tip: You can use the scroll-wheel\n to zoom and arrow keys to navigate");
        HBox tipLabel = new HBox(tip);
        tipLabel.setSpacing(10);

        // Test slider values
        zoomSlider.setValue(0);
        zoomSlider.setMin(0);
        zoomSlider.setMax(1);
        zoomSlider.setBlockIncrement(0.1);

        VBox box = new VBox(label, zoomSlider, tipLabel);
        box.setPadding(new Insets(20, 30, 20, 30));
        box.setSpacing(10);

        // Open PopOver on click, sets scroll tipLabel visible
        PopOver zoomTL = new PopOver(box);
        zoomTL.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        zoomTL.setTitle("Set Zoom");
        showZoomSlider.setOnMouseClicked(event -> zoomTL.show(showZoomSlider));

        // Move the diamonds and the info boxes by scrolling over the timeline line
        timelineScrollPane.setOnScroll((ScrollEvent event) -> {

            if (timelineLine.getLayoutX() >= 819) {

                /* Zoom factor for the scrolling
                SOURCE (for Zoom factor): https://stackoverflow.com/questions/39827911/javafx-8-scaling-zooming-scrollpane-relative-to-mouse-position 2020-05-09
                */
                double zoomFactor = 1.05;
                double deltaY = event.getDeltaY();
                if (deltaY < 0){
                    zoomFactor = 2.0 - zoomFactor;
                }

                // Update the position and size of the timeline line
                double diff = (timelineLine.getLayoutX() * zoomFactor) - timelineLine.getLayoutX();
                double timelineLineLayoutX = timelineLine.getLayoutX() * zoomFactor;

                if (timelineLineLayoutX >= 819) {
                    timelineLine.setLayoutX(timelineLine.getLayoutX() * zoomFactor);
                    timelineLine.setEndX(timelineLine.getEndX() * zoomFactor);
                    timelineLine.setStartX(timelineLine.getStartX() - diff);
                    endUnitPolygon.setLayoutX(timelineLine.getLayoutX() + timelineLine.getEndX() - 26.6);
                    endUnitTag.setLayoutX(timelineLine.getLayoutX() + timelineLine.getEndX() + 51.5);
                    scrollLeft.setVisible(true);
                    scrollRight.setVisible(true);

                } else {
                    timelineLine.setLayoutX(startUnitTag.getLayoutX() + 799);
                    timelineLine.setStartX(-startUnitTag.getLayoutX() -657 );
                    timelineLine.setEndX(startUnitTag.getLayoutX() + 409.75);
                    endUnitPolygon.setLayoutX(startUnitTag.getLayoutX() + 1203);
                    endUnitTag.setLayoutX(startUnitTag.getLayoutX() + 1280);
                    scrollRight.setVisible(false);
                    scrollLeft.setVisible(false);
                }

                // Positions and unit converters
                double unitDistanceOnXAxis = (Math.abs(timelineLine.getStartX() - timelineLine.getEndX()) / currentShownTimeline.getRange());
                double linePos = timelineLine.getLayoutX() + timelineLine.getStartX();
                double startUnit = currentShownTimeline.getStartUnit();
                double oldPreviousXPosOnTimeline = 0;
                double previousXPosOnTimeline = 0;

                // Loop the diamonds and info boxes
                for (int i = 0; i < diamondList.size(); i++) {

                    // Get the x-position for the event ((center of the timeline line + starting point (a negative number)) + ((the events place on the time line - start unit for the timeline )) * corresponding distance of timeline range in pixels
                    double eventXPosOnTimeline = (linePos) + (((currentEventsArr.get(i).getPlaceOnTimeline()  - startUnit) * unitDistanceOnXAxis));

                    // And set their new positions
                    diamondList.get(i).setLayoutX(eventXPosOnTimeline);
                    infoBoxList.get(i).setLayoutX(eventXPosOnTimeline);

                    // Configure fade in/out transitions for events appearing/disappearing.
                    // Configure fade in/out transitions for events appearing/disappearing.
                    int finalI = i;
                    FadeTransition fadeIn = new FadeTransition(new Duration(550), infoBoxList.get(i));
                    FadeTransition fadeOut = new FadeTransition(new Duration(140), infoBoxList.get(i));
                    fadeOut.setOnFinished(e -> infoBoxList.get(finalI).setVisible(false));
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);

                    // Change visibility of events depending on the distance between them.
                    if (eventXPosOnTimeline - oldPreviousXPosOnTimeline > 140) {
                        if (!infoBoxList.get(i).isVisible()) {
                            infoBoxList.get(i).setVisible(true);
                            fadeIn.play(); // Make event visible.
                        }
                    } else { // hide events if distance in between is less than 140.
                        if (infoBoxList.get(i).isVisible()) {
                            infoBoxList.get(i).setVisible(false);
                            // Unless there are many "hidden" events in a row.
                            if (i > 6) { // // Check previous 6 events.
                                for (int prevEventIndex = i - 6; prevEventIndex < i; prevEventIndex++) {
                                    // If ANY of them is visible, set current event to invisible.
                                    if (infoBoxList.get(prevEventIndex).isVisible()) {
                                        fadeOut.play();
                                        break;
                                    }
                                    // Otherwise, set current event to visible.
                                    infoBoxList.get(i).setVisible(true);
                                }
                            }
                        }
                    }
                    // Store current and previous x-values for next iteration.
                    oldPreviousXPosOnTimeline = previousXPosOnTimeline;
                    previousXPosOnTimeline = eventXPosOnTimeline;
                }

                timelineScrollPane.setVisible(true);
                eventPane.requestFocus();
            }


        });

        // Move left or right on the timeline line with the LEFT & RIGHT Arrow keys
        eventPane.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.LEFT) {
                if (scrollValue > 0) {
                    scrollValue = scrollValue - 0.1;
                    timelineScrollPane.setHvalue(scrollValue);
                }
            }
            else if (event.getCode() == KeyCode.RIGHT) {

                if (scrollValue < 1) {
                    scrollValue = scrollValue + 0.1;
                    timelineScrollPane.setHvalue(scrollValue);
                }
            }
        });
        zoomSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {


                    if (timelineLine.getLayoutX() >= 819) {

                        double zoomFactor = 1.05;
                        //double deltaY = event.getDeltaY();
                        if ((double) newValue < (double) oldValue) {
                            zoomFactor = 2.0 - zoomFactor;
                        } else if ((double) newValue == (double) oldValue) {
                        }

                        // Update the position and size of the timeline line
                        double diff = (timelineLine.getLayoutX() * zoomFactor) - timelineLine.getLayoutX();
                        double timelineLineLayoutX = timelineLine.getLayoutX() * zoomFactor;


                        if (timelineLineLayoutX >= 819 && (double)newValue != 0) {
                            timelineLine.setLayoutX(timelineLine.getLayoutX() * zoomFactor);
                            timelineLine.setEndX(timelineLine.getEndX() * zoomFactor);
                            timelineLine.setStartX(timelineLine.getStartX() - diff);
                            endUnitPolygon.setLayoutX(timelineLine.getLayoutX() + timelineLine.getEndX() - 26.6);
                            endUnitTag.setLayoutX(timelineLine.getLayoutX() + timelineLine.getEndX() + 51.5);
                            scrollLeft.setVisible(true);
                            scrollRight.setVisible(true);

                        } else {
                            timelineLine.setLayoutX(startUnitTag.getLayoutX() + 799);
                            timelineLine.setStartX(-startUnitTag.getLayoutX() -657 );
                            timelineLine.setEndX(startUnitTag.getLayoutX() + 409.75);
                            endUnitPolygon.setLayoutX(startUnitTag.getLayoutX() + 1203);
                            endUnitTag.setLayoutX(startUnitTag.getLayoutX() + 1280);
                            scrollRight.setVisible(false);
                            scrollLeft.setVisible(false);
                        }

                        // Positions and unit converters
                        double unitDistanceOnXAxis = (Math.abs(timelineLine.getStartX() - timelineLine.getEndX()) / currentShownTimeline.getRange());
                        double linePos = timelineLine.getLayoutX() + timelineLine.getStartX();
                        double startUnit = currentShownTimeline.getStartUnit();
                        double oldPreviousXPosOnTimeline = 0;
                        double previousXPosOnTimeline = 0;

                        // Loop the diamonds and info boxes
                        for (int i = 0; i < diamondList.size(); i++) {

                            // Get the x-position for the event ((center of the timeline line + starting point (a negative number)) + ((the events place on the time line - start unit for the timeline )) * corresponding distance of timeline range in pixels
                            double eventXPosOnTimeline = (linePos) + (((currentEventsArr.get(i).getPlaceOnTimeline()  - startUnit) * unitDistanceOnXAxis));

                            // And set their new positions
                            diamondList.get(i).setLayoutX(eventXPosOnTimeline);
                            infoBoxList.get(i).setLayoutX(eventXPosOnTimeline);

                            // Configure fade in/out transitions for events appearing/disappearing.
                            int finalI = i;
                            FadeTransition fadeIn = new FadeTransition(new Duration(550), infoBoxList.get(i));
                            FadeTransition fadeOut = new FadeTransition(new Duration(140), infoBoxList.get(i));
                            fadeOut.setOnFinished(e -> infoBoxList.get(finalI).setVisible(false));
                            fadeIn.setFromValue(0.0);
                            fadeIn.setToValue(1.0);
                            fadeOut.setFromValue(1.0);
                            fadeOut.setToValue(0.0);

                            // Change visibility of events depending on the distance between them.
                            if (eventXPosOnTimeline - oldPreviousXPosOnTimeline > 140) {
                                if (!infoBoxList.get(i).isVisible()) {
                                    infoBoxList.get(i).setVisible(true);
                                    fadeIn.play(); // Make event visible.
                                }
                            } else { // hide events if distance in between is less than 140.
                                if (infoBoxList.get(i).isVisible()) {
                                    infoBoxList.get(i).setVisible(false);
                                    // Unless there are many "hidden" events in a row.
                                    if (i > 6) { // // Check previous 6 events.
                                        for (int prevEventIndex = i - 6; prevEventIndex < i; prevEventIndex++) {
                                            // If ANY of them is visible, set current event to invisible.
                                            if (infoBoxList.get(prevEventIndex).isVisible()) {
                                                fadeOut.play();
                                                break;
                                            }
                                            // Otherwise, set current event to visible.
                                            infoBoxList.get(i).setVisible(true);
                                        }
                                    }
                                }
                            }
                            // Store current and previous x-values for next iteration.
                            oldPreviousXPosOnTimeline = previousXPosOnTimeline;
                            previousXPosOnTimeline = eventXPosOnTimeline;
                        }

                        timelineScrollPane.setVisible(true);
                        eventPane.requestFocus();
                    }
                }
        );
    }

    /**
     * Influenced by Anton Munter's scroll solution
     * Change horizontal value of ScrollPane, left/right button(polygon) add and subtract to current value.
     * @param event
     * @author Mattias Holmgren mh224ps
     */
    public void scrollTimeline(MouseEvent event) {
        Polygon leftRight = (Polygon) event.getSource();
        double pos = timelineScrollPane.getHvalue();
        if (leftRight.getId().equals("scrollLeft")) {
            timelineScrollPane.setHvalue(pos - 0.1);
        }
        else {
            timelineScrollPane.setHvalue(pos + 0.1);
        }
    }

    /**
     * Hide the selected timeline from view.
     * @author Anton Munter
     * @modified-by Mattias Holmgren, added hide to new elements
     */
    public void hideTimeLineEvent() {
        currentShownTimeline = new Timeline();
        activeTimelineName.setText("");
        timelineLine.setVisible(false);
        endUnitPolygon.setVisible(false);
        scrollLeft.setVisible(false);
        scrollRight.setVisible(false);
        startUnitTag.setText("");
        endUnitTag.setText("");
        unitName.setText("");
        createEventButton.setDisable(true);
        exportTimelineBtn.setDisable(true);
        editButton.setDisable(true);
        setAsToddButton.setDisable(true);
        renderTODDNodes(currentShownTimeline); // Remove TOD label when deleting TL. Eric 11/5
        depopulateTimeLine();
    }

    /**
     * Handle the event when remove button is clicked.
     * @param timeLine The timeline clicked.
     * @author Anton Munter
     * @modified by Stefan & Anton 04/23 to fix bug#62
     */
    public void handleRemoveTimeLine(Timeline timeLine) {
        // Create Strings to be displayed in the confirmation window.
        String title = "Confirm Delete Timeline";
        String header = "Deleting timeline: " + timeLine.getName();
        String content = "Do you really want to delete this timeline?";

        // If confirmation dialog returns true, confirm deletion.
        if (confirmationDialog(title, header, content)) {
            // Delete timeline from the database.
            TimelineMethods.deleteTimeline(timeLine);

            // Delete timeline from local list of timelines.
            AppProperties.listGlobal.remove(timeLine);
            AppProperties.updateLatestTimeline(null); // Added by STEFAN to fix bug#62.

            // Update window if the deleted timeline is the current timeline shown.
            if (timeLine.getName().equals(currentShownTimeline.getName())) // Moved after removing timeline from listglobal /Anton
                hideTimeLineEvent();
        }
    }

    /**
     * Creates a confirmation dialog and returns its result.
     * @param title Window title.
     * @param header Window header.
     * @param text Content text.
     * @return True if confirmed.
     * @author Elias Frigård ef222xf
     */
    private static boolean confirmationDialog (String title, String header, String text) {
        // Create confirmation dialog.
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(header);
        confirm.setContentText(text);
        confirm.initStyle(StageStyle.UTILITY);

        // Show and wait for result.
        Optional<ButtonType> result = confirm.showAndWait();

        // Return result of confirmation button, if present.
        return result.filter(buttonType -> buttonType == ButtonType.OK).isPresent();
    }

    /**
     * Builds admin menu
     * @author Anton Munter
     * @modified-by Joel Salo js225fg
     */
    public void adminMenu(ArrayList<User> userlist) {
        VBox vBox = new VBox();

        if (!userlist.isEmpty()) {
            for (User user : userlist) {
                Button button = new Button();
                // Add style and text
                button.getStyleClass().clear();
                button.getStyleClass().add("adminMenuButtons");
                button.setText(user.getUsername());

                //
                if (user.isAdmin()) {
                    button.getStyleClass().add("isAdminButton");
                } else {
                    button.getStyleClass().add("notAdminButton");
                }

                // Add event
                button.setOnAction(e -> {
                    String title = "Confirm Administrator Status";
                    String header = "Changing the status of user: " + user.getUsername();
                    String content = "Are you sure?";

                    // if confirmation dialog returns true, confirm change of status
                    if (confirmationDialog(title, header, content)) {

                        UserMethods.toggleAdminStatus(user);
                        if (user.isAdmin()) {
                            button.getStyleClass().remove("notAdminButton");
                            button.getStyleClass().add("isAdminButton");
                        } else {
                            button.getStyleClass().remove("isAdminButton");
                            button.getStyleClass().add("notAdminButton");
                        }
                    }});

                vBox.getChildren().add(button);
            }
        }
        adminMenuScrollPane.setContent(vBox);
    }


    /**
     * Logs the user out (at the moment closes the primaryWindow and returns to the log in screen).
     * @author Joel Salo js225fg
     */
    public void logout() {
        AppProperties.updateUser(new User()); // Remove current user by adding an empty user /Anton
        SceneMethods.changeScene(View.LOGIN, sign);
    }


    /**
     * Terminates program when quit button is clicked.
     * @author Anton Munter
     */
    public void quit() {
        AppProperties.terminateProgram();
    }

    /**
     * Removes all the events (small circles) from the Timeline line
     * @author Fredrik Ljungner fl222ai
     */
    public void depopulateTimeLine() {
        // Find all the event circles (in the event pane) remove them
        timelineLinePane.getChildren().removeAll(timelineLinePane.lookupAll(".diamond"));
        timelineLinePane.getChildren().removeAll(timelineLinePane.lookupAll(".infoBox"));
        // Reset line to initial settings
        timelineLine.setLayoutX(startUnitTag.getLayoutX() + 799);
        timelineLine.setStartX(-startUnitTag.getLayoutX() -657 );
        timelineLine.setEndX(startUnitTag.getLayoutX() + 409.75);
        endUnitPolygon.setLayoutX(startUnitTag.getLayoutX() + 1203);
        endUnitTag.setLayoutX(startUnitTag.getLayoutX() + 1280);
        scrollLeft.setVisible(false);
        scrollRight.setVisible(false);
    }

    /**
     * Populates the Timeline Line with events (Diamonds)
     * @author Fredrik Ljungner fl222ai
     * @modified Stefan Jägstrand 04/27; Removed creation of random Timeline. Removed Timeline parameter.
     * Method now gets list of events from database and adds it to currentShownTimeline.
     * @modified-by Eric Haga 05/14; Made changes on visibility of events depending on the distance between them
     *                        05/18; Adding sorting for the list of events.
     *                        05/20; Adding check for visibility of previous events.
     */
    public void populateTimeLine() {

        // First, remove the previous events
        depopulateTimeLine();

        // Get the x- and y-coordinates of the timeline
        double timelineLineXStart = timelineLine.getStartX();
        double timelineLineXEnd = timelineLine.getEndX();
        double timelineLineLengthOnXAxis = Math.abs(timelineLineXStart - timelineLineXEnd);

        // Unit converter to fit on x-axis, for the future, might be appropriate to set a maximum "density" of events, as it were
        double unitDistanceOnXAxis = timelineLineLengthOnXAxis / currentShownTimeline.getRange();

        // Get ArrayList of events from database
        currentEventsArr = EventMethods.selectEvents(currentShownTimeline);

        // Sort list in ascending order of "date"
        currentEventsArr.sort(Comparator.comparing(TimelineEvent::getPlaceOnTimeline));

        infoBoxList = new ArrayList<>();
        diamondList = new ArrayList<>();
        double oldPreviousXPosOnTimeline = 0;
        double previousXPosOnTimeline = 0;

        // Loop the array of events
        for (int i = 0; i < currentEventsArr.size(); i++) {

            // Get the x-position for the event ((center of the timeline line + starting point (a negative number)) + ((the events place on the time line - start unit for the timeline )) * corresponding distance of timeline range in percentage
            double eventXPosOnTimeline = (timelineLine.getLayoutX() + timelineLine.getStartX()) + (((currentEventsArr.get(i).getPlaceOnTimeline()  - currentShownTimeline.getStartUnit()) * unitDistanceOnXAxis));

            // Create a circle that represents the event on the Timeline line
            Rectangle eventDiamond = createDiamond(eventXPosOnTimeline , timelineLine.getLayoutY());
            Tooltip.install(eventDiamond, new Tooltip("Event place on Timeline: " + currentEventsArr.get(i).getPlaceOnTimeline()));

            // Check whether the event's position in timeline is even or odd.
            boolean isEven = i % 2 == 0;

            // Create info section above/below events on the timeline.
            VBox eventInfo = createEventInfoBox(currentEventsArr.get(i), eventXPosOnTimeline , timelineLine.getLayoutY(), isEven);

            // Add event info to lists.
            infoBoxList.add(eventInfo);
            diamondList.add(eventDiamond);

            // Enable popOver for current event (expands information when clicking on diamond).
            showEventPopOver(currentEventsArr.get(i), eventDiamond);

            // Make events not visible if distance between them is too small.
            if (eventXPosOnTimeline - oldPreviousXPosOnTimeline < 140) {
                eventInfo.setVisible(false);
                // Unless there are many invisible events in a row.
                if (i > 6) {
                    // In that case, we check if any of them can be made visible.
                    checkVisibilityOfPreviousEvents(i, infoBoxList.get(i));
                }
            }
            // Save values for next iterations.
            oldPreviousXPosOnTimeline = previousXPosOnTimeline;
            previousXPosOnTimeline = eventXPosOnTimeline;

            //Add the event circle and info labels to the eventPane
            timelineLinePane.getChildren().addAll(eventDiamond, eventInfo);
            timelineLine.setLayoutX(startUnitTag.getLayoutX() + 799);
            timelineLine.setStartX(-startUnitTag.getLayoutX() -657 );
            timelineLine.setEndX(startUnitTag.getLayoutX() + 409.75);
            endUnitPolygon.setLayoutX(startUnitTag.getLayoutX() + 1203);
            endUnitTag.setLayoutX(startUnitTag.getLayoutX() + 1280);
            timelineLinePane.requestFocus();
        }
    }

    /**
     * Checks if one of six previous events is visible.
     * If not, it sets current event box to visible, as a way to prevent empty space in the timeline.
     * @param currEventIndex
     * @param eventInfo
     * @return eventInfo
     * @author Eric Haga
     */
    public Node checkVisibilityOfPreviousEvents(int currEventIndex, Node eventInfo) {

        // Check previous 6 events...
        for (int prevEventIndex = currEventIndex - 6; prevEventIndex < currEventIndex; prevEventIndex++) {
            // If any of the previous 6 events is visible, set current event to invisible.
            if (infoBoxList.get(prevEventIndex).isVisible()) {
                eventInfo.setVisible(false);
                break;
            }
            // If there are no visible events behind, set current event to visible.
            eventInfo.setVisible(true);
        }
        return eventInfo;
    }

    /**
     * Opens PopOver window with event information when clicking on event diamond.
     *
     * @author Eric Haga eh223ub, Fredrik Ljunger fl222ai
     */
    public void showEventPopOver(TimelineEvent event, Rectangle eventDiamond) {

        try {
            // Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(View.SHOWEVENT.getFxml()));
            Parent root = loader.load();

            // Load the controller
            ShowEventController controller = loader.getController();

            // Pass current event to the controller.
            controller.setParentController(this); // Set reference to "this" controller! SJ
            controller.setLoader(root);
            controller.setCurrentEvent(event, eventDiamond);
            controller.setCurrentElements();
            controller.showPopOver();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates information section above/below the event depending on its place in the timeline.
     * Includes event name, short description and time.
     *
     * @param event
     * @param eventXPosOnTimeline
     * @param layoutY
     * @param isEven
     * @return
     *
     * @author Eric Haga
     */
    private VBox createEventInfoBox(TimelineEvent event, double eventXPosOnTimeline, double layoutY, boolean isEven) {

        String name = event.getEventName();
        String desc = event.getShortDesc();
        Label timeLabel = new Label(event.getPlaceOnTimeline() + "");
        Label descLabel;
        Label nameLabel;
        Image image = null;
        VBox box = null;

        // Display only 20 characters in name label.
        if (name.length() > 21) {
            nameLabel = new Label(name.substring(0, 19) + "...");
            Tooltip tooltip = new Tooltip(name);
            nameLabel.setTooltip(tooltip);
        } else {
            nameLabel = new Label(name);
        }
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

        // Display only 22 characters in short description label.
        if (desc.length() > 24) {
            descLabel = new Label(desc.substring(0, 22) + "...");
            Tooltip tooltip = new Tooltip(desc);
            descLabel.setTooltip(tooltip);
        } else {
            descLabel = new Label(desc);
        }

        // Set event image or default image (if no image is present).
        if (event.getImageStream() == null) {
            image = new Image("file:src/main/resources/images/main/defaultImage.png");
        } else {
            image = new Image(event.getImageStream());
        }

        // Set and configure how to show image.
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(90);

        if (isEven) {
            // show info above event if position in timeline is even.
            box = new VBox(imageView, nameLabel, descLabel, timeLabel);
            box.setLayoutY(layoutY - 170);
        } else {
            // show info below event if position on timeline is odd
            box = new VBox(timeLabel, nameLabel, descLabel, imageView);
            box.setLayoutY(layoutY + 40);
        }
        box.setLayoutX(eventXPosOnTimeline - 10);
        box.getStyleClass().add("infoBox");
        box.setSpacing(5);

        return box;
    }

    /**
     * Reworked helper method by Fredrik Ljungner.
     * Instead of returning a circle, returns a rotated square. Fill with random color gradient from palette.
     * Applies css hover feature.
     *
     * @param setX event position on timeline
     * @param setY y position of timeline
     * @return Rectangle
     * @author Mattias Holmgren mh224ps
     */
    private Rectangle createDiamond(double setX, double setY) {
        // Colors from figma palette
        Color[] themeColors = {Color.web("#7A30A6"), Color.web("#70731F"), Color.web("#A67D4B"), Color.web("#DBD44B"), Color.web("#615AA6"), Color.web("#577FB3")};
        Random rng = new Random();
        // Gradients white and random palette color
        Stop[] stops = new Stop[] {new Stop(0, Color.GHOSTWHITE), new Stop(1, themeColors[rng.nextInt(6)])};
        LinearGradient eventColor = new LinearGradient(0, 0, 0.50, 0.50, true, CycleMethod.NO_CYCLE, stops);

        Rectangle diamond = new Rectangle();
        diamond.setWidth(20);
        diamond.setHeight(20);
        // get center rectangle axis values
        double xAxisDiamond =  diamond.getX() + (diamond.getWidth() / 2);
        double yAxisDiamond =  diamond.getY() + (diamond.getHeight() / 2);
        diamond.setFill(eventColor);
        // Contains rotation, scale and shadow effect on hover.
        diamond.getStyleClass().add("diamond");
        // Applies rectangle offsets to event coordinates
        diamond.setLayoutX(setX - xAxisDiamond);
        // diamond.setLayoutY(setY - yAxisDiamond);
        diamond.setLayoutY(setY + 2);

        return diamond;
    }

    /**(Runs when user clicks setAsToddButton)
     * Sets current active timeline as Timeline of The Day (TODD).
     * Resets all other timelines to TODD = false
     * Updates main window to reflect new active TODD.
     * @author Stefan Jägstrand 05-11
     * 05-13 Adding confirm dialog
     */
    @FXML
    public void setAsTodd (ActionEvent action) {

        // Setup confirmwindow
        String title = "Set as timeline of the day?";
        String header = "Confirm choice";
        String content = "This will remove \"timeline of the day\" status from the previous timeline.";

        // Prompt user confirmation
        if (confirmationDialog(title, header, content)) {
            // Update TODD @ database
            int sqlCode = TimelineMethods.setTODD(currentShownTimeline);

            // If all went well, reset TODD in local list of timelines & show changes.
            if (sqlCode == 1) {
                AppProperties.resetAllToddFlags();
                currentShownTimeline.setTODD(true);
                renderTODDNodes(currentShownTimeline);
                // UpdateMenu should sort timelines so TODD is at the top of the menu..
                updateMenu();
            }
        }
    }

    /** Updates mainwindow if passed timeline is TODD (Timeline of the day)
     * Currently updates text in toddLabel, will probably change in future.
     *
     *@param timeline timeline to check if it is TODD
     *@author Stefan Jägstrand
     *@modified-by by Anton Munter
     **/
    public void renderTODDNodes (Timeline timeline) {
        // Update nodes to reflect if active timeline is TODD.
        toddImg.setVisible(timeline.isTODD());
    }

    /**
     * Used for switching theme UI
     * @author Victor Ionzon vi222bk
     */
    public void switchTheme() {
        if (rootPane.getStylesheets().contains(darkTheme)) {
            rootPane.getStylesheets().remove(darkTheme);
            menuImage.setImage(new Image(getClass().getResourceAsStream("/images/main/menu/menu.png"))); // Changed to relative path /Anton
            currentTheme = null;
        } else {
            rootPane.getStylesheets().add(darkTheme);
            menuImage.setImage(new Image(getClass().getResourceAsStream("/images/darkTheme/BackgroundMenuDark.png"))); // Changed to relative path /Anton
            currentTheme = darkTheme;
        }
        ShowEventController.setTheme(currentTheme);
    }
}