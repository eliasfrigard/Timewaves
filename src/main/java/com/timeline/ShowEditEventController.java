package com.timeline;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import sql.table_enums.EventColumns;
import sql.EventMethods;

import java.io.*;
import java.nio.file.Files;

/**
 * Rewrite on parent controller ShowEditController
 * @author Mattias Holmgren
 */
public class ShowEditEventController {

    private final int NAME_LENGTH_LIMIT = 36;
    private final int SHORT_DESC_LENGTH_LIMIT = 108;

    private ShowEventController parentController;
    private TimelineEvent currentEvent = new TimelineEvent();
    private Stage currentStage = null;
    private Image currentImage = null;
    private File chosenFile = null;

    @FXML private TextField nameEdit;
    @FXML private TextField timeEdit;
    @FXML private TextArea shortEdit;
    @FXML private TextArea longEdit;

    // Used for changing background color.
    @FXML private AnchorPane editEventWindow;
    @FXML private Pane imagePane;

    @FXML private Button saveButton;
    @FXML private Button imageBrowse;
    @FXML private ImageView eventImageEdit;

    /**
     * Set the current selected event (From Primary window)
     *
     * @author Eric Haga, Fredrik Ljungner
     */
    public void setCurrentEvent(TimelineEvent event, Rectangle eventDiamond) {
        this.currentEvent = event;
    }

    /**
     * (Copied from CreateEventPopUpController. EH)
     * Meant to be called when creating this window. Is used to get a reference to showEventController.
     *
     * @Author Stefan Jägstrand sj223gg
     * @param showEventController
     */
    public void setParentController(ShowEventController showEventController) {
        this.parentController = showEventController;
    }

    /**
     * Used for changing background color of edit event window.
     * @author Victor Ionzon vi222bk
     */
    public void changeColor(String theme, String darkTheme) {
        if (theme != null && theme.contains(darkTheme)) {
            editEventWindow.setStyle("-fx-background-color: #1E2C3D");
            imagePane.setStyle("-fx-background-color: #1E2C3D");
        } else {
            editEventWindow.setStyle("-fx-background-color: #45648C");
            imagePane.setStyle("-fx-background-color: #45648C");
        }
    }

    /**
     * Set the name, description, and image belonging to the current event.
     * @author Eric Haga, Fredrik Ljungner
     * @modified-by Mattias Holmgren (Event data carry over to textfield/textarea fields)
     */
    public void setCurrentElements () {
        nameEdit.setText(currentEvent.getEventName());
        timeEdit.setText(currentEvent.getPlaceOnTimeline() + "");
        shortEdit.setText(currentEvent.getShortDesc());
        longEdit.setText(currentEvent.getLongDesc());
        setEventImage();
    }

    /**
     * (Code adapted from Elias Holmer's in PrimaryWindowController // EH)
     * Updates event information in interface and database when save button is clicked.
     *
     * @param onSave
     * @author Elias Holmer
     * @modified-by Eric Haga
     * @modified-by Mattias Holmgren, added image update
     */
    public void handleSaveEvent(ActionEvent onSave) throws IOException {

        if (checkName() && checkShortDesc() && checkTime()) {
            if (!timeEdit.getText().equals("" + currentEvent.getPlaceOnTimeline())) {
                EventMethods.updateEventInfo(currentEvent, EventColumns.TIME, timeEdit.getText());
            }
            if (!shortEdit.getText().equals(currentEvent.getShortDesc())) {
                EventMethods.updateEventInfo(currentEvent, EventColumns.SHORTDESC, shortEdit.getText());
            }
            if (!longEdit.getText().equals(currentEvent.getLongDesc())) {
                EventMethods.updateEventInfo(currentEvent, EventColumns.LONGDESC, longEdit.getText());
            }
            if (!nameEdit.getText().equals(currentEvent.getEventName())) {
                EventMethods.updateEventInfo(currentEvent, EventColumns.NAME, nameEdit.getText());
            }
            if (chosenFile != null && !chosenFile.getPath().equals(currentEvent.getImagePath())) {
                EventMethods.updateImage(currentEvent, new BufferedInputStream(Files.newInputStream(chosenFile.toPath())), chosenFile.getPath() );
            }
            parentController.updateOnEdit();
        }
    }

    /**
     * Closes event popOver when cancel button is clicked.
     * @param event
     * @author Eric Haga
     */
    public void handleCancelEdit(ActionEvent event) {
        parentController.closePopOver();
    }

    /**
     * Set default or configured image from current event to show in popOver.
     * @author Eric Haga, Fredrik Ljungner
     * @modified-by Mattias Holmgren, imported for use in edit window
     */
    private void setEventImage() {
            if (currentEvent.getImageStream() == null) {
                currentImage = new Image("file:src/main/resources/images/main/defaultImage.png");
            } else {
                currentImage = new Image(currentEvent.getImageStream());
            }
            eventImageEdit.setFitHeight(205);
            eventImageEdit.setPreserveRatio(true);
            eventImageEdit.setImage(currentImage);
    }


    public void addPicture (ActionEvent action) { // TODO Fix feedback to the user! Either here on in the checkImageStream method (duplicate on row 270)
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

        chosenFile = fileChooser.showOpenDialog(currentStage);

        try {
            if (chosenFile != null && chosenFile.exists()) {
                BufferedInputStream filestream = new BufferedInputStream(Files.newInputStream(chosenFile.toPath()));
                currentImage = new Image(filestream);
                eventImageEdit.setImage(currentImage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Checks whether the input name for the event so that it is not too long and not empty
     * @author Jacob Skoog js224wv
     *@modified-by Mattias Holmgren, CreateEventPopupController method adjuted for edit
     */
    public boolean checkName() {
        String name = nameEdit.getText();
        if (name.length() > 0 && name.length() <= NAME_LENGTH_LIMIT) {
            nameEdit.getStyleClass().removeAll("error");
            nameEdit.getStyleClass().add("valid");
            saveButton.setDisable(false);
            return true;
        }
        else {
            nameEdit.getStyleClass().removeAll("valid");
            nameEdit.getStyleClass().add("valid");
            return false;
        }
    }

    /**
     * Checks the input short description to make sure it is not too long and not empty
     * @author Jacob Skoog js224wv
     * @modified-by Mattias Holmgren, CreateEventPopupController method adjuted for edit
     */
    public boolean checkShortDesc() {
        String shortDesc = shortEdit.getText();
        if (shortDesc.length() > 0 && shortDesc.length() <= SHORT_DESC_LENGTH_LIMIT) {
            shortEdit.getStyleClass().removeAll("error");
            shortEdit.getStyleClass().add("valid");
            saveButton.setDisable(false);
            return true;
        }
        else {
            shortEdit.getStyleClass().removeAll("valid");
            shortEdit.getStyleClass().add("error");
            return false;
        }
    }
    
    /**
     * Checks that the input time to make sure it is within the bounds of the owner timeline
     * @author Jacob Skoog js224wv
     * *@modified-by Mattias Holmgren, CreateEventPopupController method adjuted for edit
     * @modified-by Eric Haga, added numeric check 06/5.
     */
    public boolean checkTime() {
        long startTime = currentEvent.getTimeline().getStartUnit();
        long endTime = currentEvent.getTimeline().getEndUnit();
        long pointInTime;

        // Check if time is numeric first.
        if (timeEdit.getText().matches("[0-9]+")) {
            pointInTime = Long.parseLong(timeEdit.getText());
        } else {
            Notifications.create().title("Input error").text("Time input must be a number equal to or above 0.").position(Pos.CENTER).showWarning();
            return false;
        }

        if (pointInTime >= startTime && pointInTime <= endTime) {
            timeEdit.getStyleClass().removeAll("error");
            timeEdit.getStyleClass().add("valid");
            return true;
        } else {
            timeEdit.getStyleClass().removeAll("valid");
            timeEdit.getStyleClass().add("error");
            return false;
        }
    }

    /**
     * Adds basic tooltips to the fields for name, time and short description. The tooltips give information on the requirements for the fields.
     * @author Jacob Skoog js224wv
     * @modified-by Mattias Holmgren, CreateEventPopupController method adjuted for edit
     */
    public void addTooltips() {
        // Making tooltips for the criteria
        Tooltip eventNameTT = new Tooltip();
        Tooltip eventShortDescTT = new Tooltip();
        Tooltip eventTimeTT = new Tooltip();

        String nameCriteria = "The name of the event must be between 1 and " + NAME_LENGTH_LIMIT + " character long.";
        String shortDescCriteria = "The short description for the event must be between 1 and " + SHORT_DESC_LENGTH_LIMIT + " characters long.";
        String timeCriteria = "The time must be between " + currentEvent.getTimeline().getStartUnit() + " and " + currentEvent.getTimeline().getEndUnit();

        eventNameTT.setText(nameCriteria);
        eventShortDescTT.setText(shortDescCriteria);
        eventTimeTT.setText(timeCriteria);

        Tooltip.install(nameEdit, eventNameTT);
        Tooltip.install(shortEdit, eventShortDescTT);
        Tooltip.install(timeEdit, eventTimeTT);
    }

}
