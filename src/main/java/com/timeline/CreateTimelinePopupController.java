package com.timeline;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sql.TimelineMethods;

public class CreateTimelinePopupController {

    @FXML private Button cancel;
    @FXML private TextField timelineName;
    @FXML private TextField startTime;
    @FXML private TextField endTime;
    @FXML private MenuButton unitsMenuButton;

    @FXML private Label nameErrorLabel;
    @FXML private Label unitErrorLabel;
    @FXML private Label startEndErrorLabel;
    @FXML private Label otherErrorsLabel;

    // Used for changing background color

    @FXML private TextField keywordEntered;
    @FXML private StackPane showWhen;
    @FXML private ListView<String> displayKeywords;
    private String timeUnit;
    private String inputName;
    private ObservableList<String> data = FXCollections.observableArrayList();

    /**
     * Close popup scene on cancel button click
     * @author Mattias Holmgren mh224ps
     */
    public void closePopUp() {
        Stage thisStage = (Stage) cancel.getScene().getWindow();
        thisStage.close();
    }

    /**
     * Assigns current time unit depending on user's selected menu item.
     * Shows selected time unit on menu. Hides error labels (if there was any).
     * @param event
     * @author Eric Haga
     */
    @FXML
    private void handleSelectedTimeUnit(ActionEvent event) {
        timeUnit = ((MenuItem) event.getSource()).getText();
        unitsMenuButton.setText(timeUnit);
        unitsMenuButton.setStyle("-fx-text-fill: #FFFFFF");
        unitErrorLabel.setVisible(false);
    }

    /**
     * Checks whether the username entered is formatted according to the rules for timeline name formatting.
     * @return True if formatted well, false otherwise.
     * @author Eric Haga
     */
    private boolean isTimelineNameFormatValid() {

        boolean matches = true;

        // Format name (trim whitespaces at the beginning/end and remove extra whitespaces between words).
        inputName = timelineName.getText().trim().replaceAll("\\s{2,}", " ");

        // Check if name field is empty.
        if (inputName.length() == 0) {
            nameErrorLabel.setText("Please, choose a name for your timeline");
            matches = false;

        // Confirm that name is between 4 and 55 chars.
        } else if (inputName.length() < 4) {
            nameErrorLabel.setText("The name is too short (Min. 4 chars)");
            matches = false;
        } else if (inputName.length() > 55) {
            nameErrorLabel.setText("The name is too long (Max. 55 chars).");
            matches = false;
        }
        return matches;
    }

    /**
     * Checks if timeline name (input) is unique.
     * @return true if timeline name is unique, false if not.
     * @author Eric
     * @modified by Stefan 04/23 - changed from comparing ID to comparing name instead.
     *
     */
    private boolean isTimelineNameUnique() {

        for (Timeline storedTimeline : AppProperties.listGlobal) {
            if (storedTimeline.getName().equalsIgnoreCase(inputName)) { // Changed from getID() to comparing the names instead. /SJ
                nameErrorLabel.setText("Timeline name already exists!");
                return false;
            }
        }
        nameErrorLabel.setText(""); // Clear error message.
        return true;
    }

    /**
     * Checks if a time unit is 1) selected, 2) specified in text field; 3) made of digits.
     * Also confirms if start unit is smaller than end units.
     * @return true if time unit criteria is fulfilled, false otherwise.
     * @author Eric Haga
     */
    private boolean isTimeUnitCriteriaFulfilled() {

        String start = startTime.getText();
        String end = endTime.getText();
        boolean isFulfilled = true;

        // Confirms that a time unit (years, months, etc) is selected.
        if (!unitsMenuButton.getText().equals(timeUnit)) {
            unitErrorLabel.setText("Please, choose a time unit.");
            isFulfilled = false;

        } else { // If time unit is selected, check if start/end field is empty.
            if ((start.length() == 0) || (end.length() == 0)) {
                startEndErrorLabel.setText("Please, specify a start and end unit.");
                isFulfilled = false;

            } else { // If not empty, check if start/end dates contain only numbers.
                if ((!start.matches("[0-9]+")) || (!end.matches("[0-9]+"))) {
                    startEndErrorLabel.setText("Start/end units can only be numbers");
                    isFulfilled = false;

                    // If only numbers, confirm that start unit is smaller than end unit.
                } else if (Long.parseLong(start) >= Long.parseLong(end)) {
                    startEndErrorLabel.setText("Start unit should be smaller than end unit.");
                    isFulfilled = false;
                }
            }
        }
        return isFulfilled;
    }

    /**
     * Checks if all user input fields are correct before sending to database.
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
     * Adds new timeline to SQL Database when OK button is clicked.
     * Then closes PopUp and shows view for newly created Timeline.
     * @author Eric Haga
     */
    @FXML
    public void onSubmit() {

        if (isUserInputCorrect()) {

            addKeyword();
            long start = Long.parseLong(startTime.getText());
            long end = Long.parseLong(endTime.getText());

            // Add new timeline object to database (temporary ID value until we decide how to handle IDs)

            // Converts the data observable list to a temporary String array to submit to the Timeline constructor.
            String[] keywords = new String[data.size()];
            if (data.size() != 0) {
                for (int i = 0; i < keywords.length; i++) {
                    keywords[i] = data.get(i);
                }
            } else {
                keywords = new String[1];
                keywords[0] = "";
            }

            Timeline newTL = new Timeline(20, inputName, start, end, timeUnit, keywords);

            // Add to local list of timelines if database call is successful.
            int sqlCode = TimelineMethods.insertTimeline(newTL);
            if (sqlCode == 1) {
                Timeline resultingTimeline = TimelineMethods.selectTimeline(newTL.getName()); // Takes the newly created timeline from the server to get its ID
                newTL.setId(resultingTimeline.getId());
                AppProperties.updateLatestTimeline(newTL);
                AppProperties.listGlobal.add(newTL);
                closePopUp();
            }
            else if (sqlCode == 1062) {
                nameErrorLabel.setText("Timeline name already exists!");
            }
            else {
                otherErrorsLabel.setText("Something went wrong...! Error: " + sqlCode);
            }
        }
    }

    /**
     * Adds delete button on individual rows functionality to ListView
     * @Author Mattias Holmgren mh224ps
     */
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
            button.setOnAction(event ->  {
                getListView().getItems().remove(getItem());
                // Hide ListView if list has been cleared
                if (getListView().getItems().isEmpty()) {
                    CreateTimelinePopupController.this.showWhen.setVisible(false);
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

    /**
     * Add input data to list, presents data in ListView.
     * ListView hidden prior to adding.
     * @Author Mattias Holmgren
     */
    @FXML
    public void addKeyword() {
        displayKeywords.setCellFactory(param -> new DeleteItem()); // ListCell imported to ListView
        if (keywordEntered.getText().length() > 0) {
            data.add(keywordEntered.getText());

            if (data.size() > 0) {
                showWhen.setVisible(true);
                displayKeywords.setItems(data);
                displayKeywords.getScene();
            }
        }
    }
}
