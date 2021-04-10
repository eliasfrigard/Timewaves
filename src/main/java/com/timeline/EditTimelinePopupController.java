package com.timeline;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sql.TimelineMethods;
import sql.EventMethods;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Elias Holmér, most code is reused from createTimelinePopupController.
 */

public class EditTimelinePopupController {

    //private static ArrayList<String> localListOfTimelines = null;
    private Timeline currentTL = AppProperties.getLatestTimeline();
    @FXML
    private Button editCancel;
    @FXML
    private TextField editTimelineName;
    @FXML
    private TextField editStartTime;
    @FXML
    private TextField editEndTime;
    @FXML
    private MenuButton editUnitsMenuButton;
    @FXML
    private CheckBox editConvertTime;

    @FXML
    private Label editNameErrorLabel;
    @FXML
    private Label editUnitErrorLabel;
    @FXML
    private Label editStartEndErrorLabel;
    @FXML
    private StackPane showWhen;

    @FXML
    private TextField editKeywordEntered;
    @FXML
    private StackPane editShowWhen;
    @FXML
    private ListView<String> editDisplayKeywords;
    private String editTimeUnit;
    private String editInputName;
    private ObservableList<String> data = FXCollections.observableArrayList();
    private PrimaryWindowController PWC;
    private ArrayList<TimelineEvent> events = null;
    private long firstEvent;
    private long lastEvent;

    public void initialize() {
        editTimelineName.setText(currentTL.getName());
        editStartTime.setText(String.valueOf(currentTL.getStartUnit()));
        editEndTime.setText(String.valueOf(currentTL.getEndUnit()));

        // Default selected unit of time to the timeline value.
        editTimeUnit = currentTL.getUnitName();
        editUnitsMenuButton.setText(editTimeUnit);
    }

    public void setParentController(PrimaryWindowController controller) {
        this.PWC = controller;
    }

    public void convertTime(ActionEvent event) {
        if (editConvertTime.isSelected()) {
            editStartTime.setText(String.valueOf(currentTL.getStartUnit()));
            editEndTime.setText(String.valueOf(currentTL.getEndUnit()));
            editStartTime.setDisable(true);
            editEndTime.setDisable(true);
        } else {
            editStartTime.setDisable(false);
            editEndTime.setDisable(false);
        }
    }

    /**
     * Close popup scene on cancel button click
     *
     * @author Mattias Holmgren mh224ps
     */
    public void closePopUp() {
        Stage thisStage = (Stage) editCancel.getScene().getWindow();
        thisStage.close();
    }

    /**
     * Assigns current time unit depending on user's selected menu item.
     * Shows selected time unit on menu. Hides error labels (if there was any).
     *
     * @param event
     * @author Eric Haga
     */
    @FXML
    private void handleSelectedTimeUnit(ActionEvent event) {
        editTimeUnit = ((MenuItem) event.getSource()).getText();
        editUnitsMenuButton.setText(editTimeUnit);
        editUnitErrorLabel.setVisible(false);
    }

    /**
     * Checks whether the username entered is formatted according to the rules for timeline name formatting.
     *
     * @return True if formatted well, false otherwise.
     * @author Eric Haga
     */
    private boolean isTimelineNameFormatValid() {

        boolean matches = true;

        // Format name (trim whitespaces at the beginning/end and remove extra whitespaces between words).
        editInputName = editTimelineName.getText().trim().replaceAll("\\s{2,}", " ");

        // Check if name field is empty.
        if (editInputName.length() == 0) {
            editNameErrorLabel.setText("Please, choose a name for your timeline");
            matches = false;

            // Confirm that name is between 4 and 55 chars.
        } else if (editInputName.length() < 4) {
            editNameErrorLabel.setText("The name is too short (Min. 4 chars)");
            matches = false;
        } else if (editInputName.length() > 55) {
            editNameErrorLabel.setText("The name is too long (Max. 55 chars).");
            matches = false;
        }
        return matches;
    }

    /**
     * Checks if timeline name (input) is unique.
     *
     * @return true if timeline name is unique, false if not.
     * @author Eric
     * @modified by Stefan 04/23 - changed from comparing ID to comparing name instead.
     */
    private boolean isTimelineNameUnique() {

        if (!currentTL.getName().equalsIgnoreCase(editTimelineName.getText())) {
            for (Timeline storedTimeline : AppProperties.listGlobal) {
                if (storedTimeline.getName().equalsIgnoreCase(editInputName)) { // Changed from getID() to comparing the names instead. /SJ

                    return false;
                }
            }
        }
        editNameErrorLabel.setText(""); // Clear error message.
        return true;
    }

    /**
     * Checks if a time unit is 1) selected, 2) specified in text field; 3) made of digits.
     * Also confirms if start unit is smaller than end units.
     *
     * @return true if time unit criteria is fulfilled, false otherwise.
     * @author Eric Haga
     */
    private boolean isTimeUnitCriteriaFulfilled() {

        String start = editStartTime.getText();
        String end = editEndTime.getText();
        boolean isFulfilled = true;

        if(!this.events.isEmpty()) {
            if (this.firstEvent < Long.parseLong(editStartTime.getText())) {
                editStartEndErrorLabel.setText("Events not covered by chosen range");
                isFulfilled = false;
            }
            if (this.lastEvent > Long.parseLong(editEndTime.getText())) {
                editStartEndErrorLabel.setText("Events not covered by chosen range");
                isFulfilled = false;
            }
        }

        // Confirms that a time unit (years, months, etc) is selected.
        if (!editUnitsMenuButton.getText().equals(editTimeUnit)) {
            editUnitErrorLabel.setText("Please, choose a time unit.");
            isFulfilled = false;

        } else { // If time unit is selected, check if start/end field is empty.
            if ((start.length() == 0) || (end.length() == 0)) {
                editStartEndErrorLabel.setText("Please, specify a start and end unit.");
                isFulfilled = false;

            } else { // If not empty, check if start/end dates contain only numbers.
                if ((!start.matches("[0-9]+")) || (!end.matches("[0-9]+"))) {
                    editStartEndErrorLabel.setText("Start/end units can only be numbers");
                    isFulfilled = false;

                    // If only numbers, confirm that start unit is smaller than end unit.
                } else if (Long.parseLong(start) >= Long.parseLong(end)) {
                    editStartEndErrorLabel.setText("Start unit should be smaller than end unit.");
                    isFulfilled = false;
                }
            }
        }
        return isFulfilled;
    }

    /**
     * Checks if all user input fields are correct before sending to database.
     *
     * @return true if input is valid and complete, false if not.
     * @author Eric Haga
     */
    private boolean isUserInputCorrect() {
        if (isTimelineNameFormatValid()) {
            if (!isTimelineNameUnique()) {
                return false;
            }
        } else {
            return false;
        }
        return isTimeUnitCriteriaFulfilled();
    }

    /**
     * Updates timeline if values are changed to SQL Database when OK button is clicked,
     * then closes PopUp. Working on how to update primary window in real time to the changed timeline.
     *
     * @author Elias Holmér
     * @modified Stefan Jägstrand 05-06 Added handleTimelineEvent() & updateMenu() calls to update locally.
     */
    @FXML
    public void onSubmit() {

        // Validate input
        if(isUserInputCorrect()) {
            if (!editStartTime.getText().equals(String.valueOf(currentTL.getStartUnit())) && !editConvertTime.isSelected()) {
                if (isTimeUnitCriteriaFulfilled()) {
                    currentTL.setStartUnit(Long.parseLong(editStartTime.getText()));
                }
            }
            if (!editEndTime.getText().equals(String.valueOf(currentTL.getEndUnit())) && !editConvertTime.isSelected()) {
                if (isTimeUnitCriteriaFulfilled()) {
                    currentTL.setEndUnit(Long.parseLong(editEndTime.getText()));
                }
            }
            if (!editTimelineName.getText().equals(currentTL.getName())) {
                currentTL.setName(editTimelineName.getText());
            }
            if(!editUnitsMenuButton.getText().equals(currentTL.getUnitName()) && !editConvertTime.isSelected()){
                currentTL.setUnitName(editUnitsMenuButton.getText());
            }
            if(!editKeywordEntered.getText().isEmpty()){
                String[] keys = currentTL.getKeywords();
                String[] temp = new String[keys.length+1];
                for(int i = 0; i < keys.length; i++){
                    temp[i] = keys[i];
                }
                temp[keys.length] = editKeywordEntered.getText();
                Arrays.sort(temp);
                currentTL.setKeywords(temp);
            }
            if(editConvertTime.isSelected()){
                convertTimelineUnits(currentTL,editUnitsMenuButton.getText());
            }

            // Update database
            TimelineMethods.updateTimeline(currentTL);
            // Update main window timeline & menu.
            PWC.handleTimeLineEvent(currentTL);
            PWC.updateMenu();
            closePopUp();
        }
    }

    class DeleteItem extends ListCell<String> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("-");

        // Elements of Hbox added, alignment set to right. Removes selected item on action.
        public DeleteItem() {
            super();
            button.getStyleClass().add("add-button");
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            // Remove row at button index
            button.setOnAction(event -> {
                getListView().getItems().remove(getItem());
                // Hide ListView if list has been cleared
                if (getListView().getItems().isEmpty()) {
                    EditTimelinePopupController.this.showWhen.setVisible(false);
                }
            });
        }

        // Customize visuals of cell with Hbox content
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                label.setText(item);
                setGraphic(hbox);
            }
        }
    }


    @FXML
    public void addKeyword() {
        editDisplayKeywords.setCellFactory(param -> new EditTimelinePopupController.DeleteItem()); // ListCell imported to ListView
        if (editKeywordEntered.getText().length() > 0) {
            data.add(editKeywordEntered.getText());

            if (data.size() > 0) {
                editShowWhen.setVisible(true);
                editDisplayKeywords.setItems(data);
                editDisplayKeywords.getScene();
            }
        }
    }

    /**
     * Updates the timeline and all connected events to the target time unit.
     * @param tl Timeline to update.
     * @param targetUnitStr String time unit.
     * @author Elias Frigår ef222xf
     */
    public static void convertTimelineUnits (Timeline tl, String targetUnitStr) {
        // Return without doing anything if units are the same.
        if (tl.getUnitName().equalsIgnoreCase(targetUnitStr)) {
            return;
        }

        // Get big decimal representation of timeline and target time units.
        BigDecimal previousUnit = getDecimalRepresentation(tl.getUnitName());
        BigDecimal targetUnit = getDecimalRepresentation(targetUnitStr);

        // Divide the numbers above to get the multiplier for conversion.
        BigDecimal multiplier = previousUnit.divide(targetUnit, 20, RoundingMode.CEILING);

        // Control that the end of the timeline would be within the reach of max long value.
        BigDecimal endNodeAsBig = new BigDecimal(Long.toString(tl.getEndUnit()));
        BigDecimal maxValue = endNodeAsBig.divide(targetUnit, 20, RoundingMode.CEILING);
        BigDecimal maxLongValue = new BigDecimal(Long.MAX_VALUE);

        // If the end of the timeline would be outside the reach of max long value, print error message.
        if (maxValue.compareTo(maxLongValue) > 0) {
            // Conversion is too big, print error message.
            System.out.println("Conversion too big.");
        }

        // Update timeline values in the application object instance.
        tl.setStartUnit(bigIntegerMultiplication(tl.getStartUnit(), multiplier));
        tl.setEndUnit(bigIntegerMultiplication(tl.getEndUnit(), multiplier));
        tl.setUnitName(targetUnitStr);

        // Update the timeline in the database.
        TimelineMethods.updateTimeline(tl);

        // Get all events connected to timeline.
        ArrayList<TimelineEvent> eventList = EventMethods.selectEvents(tl);

        // Update each event.
        for (TimelineEvent event : eventList) {
            // Update place on timeline for each event.
            event.setPlaceOnTimeline(bigIntegerMultiplication(event.getPlaceOnTimeline(), multiplier));

            // Update the event in the database.
            EventMethods.updateEventInfo(event);
        }
    }

    /**
     * Accepts a long and Big Decimal and multiplies them.
     * @param value long
     * @param multiplier BigDecimal
     * @return long
     * @author Elias Frigård ef222xf
     */
    private static long bigIntegerMultiplication(long value, BigDecimal multiplier) {
        if (value == 0) return 0
                ;
        // Convert value to BigDecimal numerator.
        BigDecimal numerator = new BigDecimal(Long.toString(value));

        // Divide numerator and multiplier.
        BigDecimal result = numerator.multiply(multiplier);

        // Convert result to long.
        return result.longValue();
    }

    /**
     * Converts a time unit name into a Big Decimal value.
     * @param unitName String
     * @return BigDecimal
     * @author Elias Frigård ef222xf
     */
    private static BigDecimal getDecimalRepresentation (String unitName) {
        BigDecimal decimalRepresentation = new BigDecimal("1");

        // Units as milliseconds.
        switch (unitName) {
            case "Seconds":
                decimalRepresentation = new BigDecimal("1000");
                break;
            case "Minutes":
                decimalRepresentation = new BigDecimal("60000");
                break;
            case "Hours":
                decimalRepresentation = new BigDecimal("3600000");
                break;
            case "Days":
                decimalRepresentation = new BigDecimal("86400000");
                break;
            case "Months":
                decimalRepresentation = new BigDecimal("2629746000");
                break;
            case "Years":
                decimalRepresentation = new BigDecimal("31556952000");
                break;
            case "Decades":
                decimalRepresentation = new BigDecimal("315569520000");
                break;
            case "Centuries":
                decimalRepresentation = new BigDecimal("3155695200000");
                break;
            case "Millenniums":
                decimalRepresentation = new BigDecimal("31556952000000");
                break;
            case "Million years":
                decimalRepresentation = new BigDecimal("31556952000000000");
                break;
            case "Billion years":
                decimalRepresentation = new BigDecimal("31556952000000000000");
                break;
        }

        return decimalRepresentation;
    }


    public void setEventlist(ArrayList<TimelineEvent> e) {

        this.events = e;
        if (!this.events.isEmpty()) {
            long smallest = this.events.get(0).getPlaceOnTimeline();
            long largest = this.events.get(0).getPlaceOnTimeline();
            for (int i = 1; i < this.events.size(); i++) {
                long comparator = this.events.get(i).getPlaceOnTimeline();
                if (comparator < smallest) {
                    smallest = comparator;
                }
                if (comparator > largest) {
                    largest = comparator;
                }
            }
            this.firstEvent = smallest;
            this.lastEvent = largest;
        }
    }
}
