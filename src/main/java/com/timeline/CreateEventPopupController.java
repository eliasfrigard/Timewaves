package com.timeline;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sql.EventMethods;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;


public class CreateEventPopupController {
    private Timeline currentTimeLine = new Timeline();
    private Stage currentStage = null;
    private Image currentImage = null;

    @FXML private Label eventIDLabel;
    @FXML private TextField eventName;
    @FXML private TextField eventTime;
    @FXML private TextField eventShortDesc;
    @FXML private TextArea eventLongDesc;
    @FXML private Button eventOK;
    @FXML private Button eventCancel;
    @FXML private ImageView chosenImage;
    @FXML private Button imageButton;

    // For accessing the selected image, so that it can be posted to the server
    private File chosenFile = null;

    // Limits on name length and shortDesc length
    private final int nameLengthLimit = 36;
    private final int shortDescLengthLimit = 108;

    PrimaryWindowController parentController; // MEANT AS A REFERENCE TO PRIMARYWINDOWCONTROLLER // SJ

    /** Meant to be called when creating this window. Is used to get a reference to primaryWindowController.
     * @Author Stefan Jägstrand sj223gg
     * @created 04/27
     * */
    public void setParentController(PrimaryWindowController pwc) {
        this.parentController = pwc;
    }

    /**
     * Makes it possible to move the window without having a upper window panel
     * @author Fredrik Ljungner fl222ai
     * @modified Victor Ionzon vi222bk Removed drag-and-drop, moved to SceneMethods class.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * Close popup scene on cancel button click
     * @author Mattias Holmgren mh224ps
     */
    public void closeEventPopUp() {
        currentImage = null;
        Stage thisStage = (Stage) eventCancel.getScene().getWindow();
        thisStage.close();
    }

    /**
     * Sets (kind of gets) this pop up window's stage. (Passed from Primary Window controller.
     * Needed to move the window without window panel)
     * @author Fredrik Ljungner fl222ai
     */
    public void setStage(Stage stage) {
        currentStage = stage;
    }

    public void setNameAndID() {
        eventIDLabel.setText("Timeline (ID: " + currentTimeLine.getId() + ") - " + currentTimeLine.getName());
    }

    /**
     * Set the current selected TimeLine (From Primary window)
     * @author Fredrik Ljungner fl222ai
     */
    public void setCurrentTimeLine  (Timeline timeLine) {
        currentTimeLine = timeLine;
    }

    /**
     * Adds basic tooltips to the fields for name, time and short description. The tooltips give information on the requirements for the fields.
     * @author Jacob Skoog js224wv
     */
    public void addTooltips() {
        // Making tooltips for the criteria
        Tooltip eventNameTT = new Tooltip();
        Tooltip eventTimeTT = new Tooltip();
        Tooltip eventShortDescTT = new Tooltip();

        String nameCriteria = "The name of the event must be between 1 and " + nameLengthLimit + " character long.";
        String timeCriteria = "The time for the event must be a number between " + Long.toString(currentTimeLine.getStartUnit()) + " and " + Long.toString(currentTimeLine.getEndUnit());
        String shortDescCriteria = "The short description for the event must be between 1 and " + shortDescLengthLimit + " characters long.";

        eventNameTT.setText(nameCriteria);
        eventTimeTT.setText(timeCriteria);
        eventShortDescTT.setText(shortDescCriteria);

        Tooltip.install(eventName, eventNameTT);
        Tooltip.install(eventTime, eventTimeTT);
        Tooltip.install(eventShortDesc, eventShortDescTT);
    }

    /**
     * Adding an event to a timeline. Currently only adds the event to the database
     *
     * @author Susanna Persson sp222xw
     * @modified by Fredrik Ljungner fl222ai - 04/25 - changed from hardcoded to dynamic (input trough Add Event pop up window)
     * @modified by Stefan Jägstrand 04/27 added parentController.populateTimeline()
     * @modified by Jacob Skoog js224wv Added criteria checks
     */
    @FXML
    private void addEvent() {
        int timelineID = currentTimeLine.getId();
        long time = -1;

        String name = eventName.getText();
        if (!eventTime.getText().equals("")) time = Long.parseLong(eventTime.getText());
        String shortDesc = eventShortDesc.getText();
        String longDesc = eventLongDesc.getText();
        String imagePath = AppProperties.nullPath;
        if (chosenFile != null && chosenFile.exists()) {
            imagePath = generateImgPath(chosenFile.getPath());
        }

        TimelineEvent toAdd = new TimelineEvent(currentTimeLine, name, time, shortDesc, longDesc, imagePath);
        if (chosenFile != null && chosenFile.exists()) toAdd.setImageFile(chosenFile.getPath());

        // Add event to database
        if (checkName() && checkTime() && checkShortDesc()) {

            int sqlCode;

            sqlCode = EventMethods.addEvent(toAdd);

            // Update events in PrimaryWindowController
            parentController.populateTimeLine();

            Stage thisStage = (Stage) eventOK.getScene().getWindow();
            thisStage.close();

        } else {
            eventOK.setDisable(true);
        }
    }

    public void addPicture () { // TODO Fix feedback to the user! Either here on in the checkImageStream method (duplicate on row 270)
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

        chosenFile = fileChooser.showOpenDialog(currentStage);

        try {
            if (chosenFile != null && chosenFile.exists()) {
                BufferedInputStream filestream = new BufferedInputStream(Files.newInputStream(chosenFile.toPath()));
                currentImage = new Image(filestream);
                chosenImage.setImage(currentImage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks whether the input name for the event so that it is not too long and not empty
     * @author Jacob Skoog js224wv
     */
    public boolean checkName() {
        boolean valid = false;
        String name = eventName.getText();
        if (name.length() > 0 && name.length() <= nameLengthLimit) {
            eventName.getStyleClass().removeAll("error");
            eventName.getStyleClass().add("valid");
            valid = true;
            eventOK.setDisable(false);
        } else {
            eventName.getStyleClass().removeAll("valid");
            eventName.getStyleClass().add("error");
        }
        return valid;
    }

    /**
     * Checks that the input time to make sure it is within the bounds of the owner timeline
     * @author Jacob Skoog js224wv
     */
    public boolean checkTime() {
        long time = -1;
        boolean valid = false;

        if (eventTime.getText().matches("\\d{1,}")) {
            time = Long.parseLong(eventTime.getText());
        }
        if (time >= currentTimeLine.getStartUnit() && time <= currentTimeLine.getEndUnit()) {
            eventTime.getStyleClass().removeAll("valid");
            eventTime.getStyleClass().removeAll("error");
            eventTime.getStyleClass().add("valid");
            valid = true;
            eventOK.setDisable(false);
        } else {
            eventTime.getStyleClass().removeAll("valid");
            eventTime.getStyleClass().removeAll("error");
            eventTime.getStyleClass().add("error");
        }
        return valid;
    }

    /**
     * Checks the input short description to make sure it is not too long and not empty
     * @author Jacob Skoog js224wv
     */
    public boolean checkShortDesc() {
        String shortDesc = eventShortDesc.getText();
        boolean valid = false;

        if (shortDesc.length() <= shortDescLengthLimit && shortDesc.length() > 0) {
            eventShortDesc.getStyleClass().removeAll("valid");
            eventShortDesc.getStyleClass().removeAll("error");
            eventShortDesc.getStyleClass().add("valid");
            valid = true;
            eventOK.setDisable(false);
        } else {
            eventShortDesc.getStyleClass().removeAll("valid");
            eventShortDesc.getStyleClass().removeAll("error");
            eventShortDesc.getStyleClass().add("error");
        }
        return valid;
    }

    /**
     * Sets TAB keypress to focus the next button (the OK button).
     * @author Susanna Persson sp222xw
     * @param event
     */
    public void setLongDescAreaBehaviour(KeyEvent event) {
        if (event.getCode().equals(KeyCode.TAB)) {
            eventLongDesc.deletePreviousChar();
            eventOK.requestFocus();
        }
    }

    private String generateImgPath(String filePath) { // Need to generate a filename based on the owning timeline ID as eventID isn't known on creation
        String path = AppProperties.eventImgPath + currentTimeLine.getId() + "-" + parentController.getNoOfEvents() + "" + getFileExtension(filePath);

        return path;
    }

    /**
     * Help method that gets the file extension (e.g. .png or .jpeg) from a file's path
     * @param filePath
     * @return
     */
    private String getFileExtension(String filePath) {
        String[] pathSplit = filePath.split("\\.");
        String ext = "." + pathSplit[1];

        return ext;
    }
}