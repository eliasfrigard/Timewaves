package com.timeline;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import org.controlsfx.control.PopOver;
import sql.EventMethods;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ShowEventController implements Initializable {

    private PrimaryWindowController parentController; // MEANT AS A REFERENCE TO PRIMARYWINDOWCONTROLLER // SJ
    private TimelineEvent currentEvent = new TimelineEvent();
    private Rectangle eventDiamond;
    private PopOver popOver = new PopOver();
    private Parent root;
    private Image image;

    @FXML private Label nameLabel;
    @FXML private Label shortDescLabel;
    @FXML private Text longDescText;
    @FXML private Button editButton;
    @FXML private ImageView eventImage;

    // Used for changing theme.
    @FXML private VBox eventDesc;
    private static String currentTheme;
    private final String darkTheme = getClass().getResource("/css/dark.css").toExternalForm();

    @FXML private Button deleteEventButton;

    @FXML private ScrollPane eventPopScroll;
    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

    /**
     * Method to zoom image in event popOver
     * SOURCE: http://www.java2s.com/Code/Java/JavaFX/JavaFXImageZoomExample.htm 2020-05-07
     * @modified Victor Ionzon, Fredrik Ljungner
     * @modified Stefan Jägstrand 05-06. Adding admin check to edit/delete buttons
     * */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if user is admin
        if (AppProperties.getUser().isAdmin()) {
            editButton.setVisible(true);
            deleteEventButton.setVisible(true);
        } else {
            // User is not admin, hide buttons!
            editButton.setVisible(false);
            deleteEventButton.setVisible(false);
        }

        // Handle zoom events
        zoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                eventImage.setFitWidth(zoomProperty.get() * 4);
                eventImage.setFitHeight(zoomProperty.get() * 3);
            }
        });

        eventPopScroll.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });

        eventImage.preserveRatioProperty().set(true);
        eventPopScroll.setContent(eventImage);
    }

    /**
     * Used to set current theme.
     * @author Victor Ionzon, vi222bk
     */
    public static void setTheme(String theme) {
        currentTheme = theme;
    }

    /**
     * Used to change color in event window.
     * @author Victor Ionzon, vi222bk
     */
    public void setEventColor() {
        // Check which theme is active and set background color.
        if (currentTheme != null && currentTheme.contains(darkTheme)) {
            eventDesc.setStyle("-fx-background-color: #1E2C3D");
            eventPopScroll.setStyle("-fx-background: #1E2C3D");
        } else {
            eventDesc.setStyle("-fx-background-color: #45648C");
            eventPopScroll.setStyle("-fx-background: #45648C");
        }
    }

    /**
     * Meant to be called when creating this window. Is used to get a reference to primaryWindowController.
     * Copied from CreateEventPopUpController.
     *
     * @Author Stefan Jägstrand sj223gg
     * @param pwc
     */
    public void setParentController(PrimaryWindowController pwc) {
        this.parentController = pwc;
    }

    /**
     *
     * @param root
     */
    public void setLoader(Parent root) {
        this.root = root;
    }

    /**
     * Set the current selected event (From Primary window)
     *
     * @param event
     * @param eventDiamond
     * @author Eric Haga, Fredrik Ljungner
     */
    public void setCurrentEvent(TimelineEvent event, Rectangle eventDiamond) {
        this.currentEvent = event;
        this.eventDiamond = eventDiamond;
    }


    /**
     * Set the name, description, and image belonging to the current event.
     * @author Eric Haga, Fredrik Ljungner
     */
    public void setCurrentElements () {
        nameLabel.setText(currentEvent.getEventName());
        shortDescLabel.setText(currentEvent.getShortDesc());
        longDescText.setText(currentEvent.getLongDesc());
        setEventImage();
    }

    /**
     * Show PopOver with event's content when a diamond in the timeline is clicked.
     * @author Eric Haga, Fredrik Ljungner
     * @modified Stefan Jägstrand 05-06. Removed (popOver = new PopOver()) to fix "empty hanging popovers" bug.
     */
    public void showPopOver() {
        eventDiamond.setOnMouseClicked(mouseEvent -> {
            popOver.setContentNode(root);
            popOver.show(eventDiamond);
            setEventColor();
        });
    }

    /**
     * Set default or configured image from current event to show in popOver.
     * @author Eric Haga, Fredrik Ljungner
     */
    private void setEventImage() {

        if (currentEvent.getImageStream() == null) {
            image = new Image("file:src/main/resources/images/main/defaultImage.png");
        } else {
            image = new Image(currentEvent.getImageStream());
        }
        eventImage.setFitHeight(204);
        eventImage.setPreserveRatio(true);
        eventImage.setImage(image);
    }

    /** Runs when user wants to delete selected event (clicks delete event button).
     *  Creates a confirmation window. Deletes current event
     *  from database if user confirms, otherwise does nothing.
     * @author Stefan Jägstrand sj223gg 05-04
     */
    public void handleDeleteEvent () {
        // Create confirmation dialog
        String title = "Confirm delete Event?";
        String header = "Deleting event";
        String content = "Do you really want to delete this event?";

        // If confirmation dialog returns true (i.e user clicked "yes")
        if (confirmationDialog(title, header, content)) {
            // Delete event from database
            EventMethods.deleteEvent(currentEvent);
            currentEvent.getTimeline().removeEvent(currentEvent);
            // Update events on main window
            parentController.populateTimeLine();
        }

    }

    /** (Copied from PrimaryWindowController / SJ)
     *
     * Creates a confirmation dialog and returns its result.
     * @param title Window title.
     * @param header Window header.
     * @param text Content text.
     * @return True if confirmed.
     * @author Stefan Jägstrand sj223gg 05-04
     * */
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
     * Changes content of event popOver to edit mode when "edit" button is clicked.
     * Uses ShowEditEventPopOver.fxml as template.
     * @author Mattias Holmgren
     * @modified-by Eric Haga (changed so that new fxml is loaded on same popOver).
     */
    public void switchToEditMode(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(View.EDITEVENT.getFxml()));
            Parent editRoot = loader.load();

            ShowEditEventController editController = loader.getController();
            editController.setParentController(this);
            editController.setCurrentEvent(currentEvent, eventDiamond);
            editController.setCurrentElements();
            editController.addTooltips();
            editController.changeColor(currentTheme, darkTheme);

            popOver.setContentNode(editRoot);
            popOver.setAutoHide(false); // disables autohide when on edit mode.

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates timeline with saved changes.
     * @author Eric Haga
     */
    public void updateOnEdit() {

        parentController.populateTimeLine();
        popOver.setContentNode(root);
    }

    /**
     * Simple method to close popOver.
     * Used in showEditEventController when "cancel" button is clicked.
     * @author Eric Haga
     */
    public void closePopOver() {

        popOver.hide();
        // Ensures autoHide function is reactivated when exiting edit mode.
        popOver.setAutoHide(true);
    }

}
