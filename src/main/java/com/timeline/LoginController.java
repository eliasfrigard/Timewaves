package com.timeline;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;
import sql.UserMethods;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginController {
    public TextField usernameID = null;
    public PasswordField passwordID = null;
    public Button signInID;

    @FXML
    private Button continueBtn;
    @FXML
    private Button forgotPassBtn;

    @FXML private Label incorrectCredentials;

    /**
     * Authenticates user by name. Looks for name and corresponding password in database (via UserMethods class).
     * Compare user in application.
     *
     * @author Elias Frigård ef222xf
     * @author Fredrik Ljungner fl222ai
     * @author Susanna Persson sp222xw
     * @modified-by Joel Salo js225fg
     */
    public void loginButtonClicked() {
        // --USER AUTHENTICATION PROCESS--
        // Creates and sets the user's parameters from window information.
        User currentUser = new User(); // Replace with actual user.
        currentUser.setUsername(usernameID.getText());
        currentUser.setPassword(passwordID.getText());

        // Authorizes user and saves the result for use in window transfer.
        boolean isAuthorized = UserMethods.authenticateUser(currentUser);

        // --WINDOW TRANSFER PROCESS--
        if (isAuthorized) {

            for (User user : AppProperties.userList) {
                if (user.getUsername().equals(currentUser.getUsername()))
                    AppProperties.updateUser(user);
            }

            // if user is in recovery mode => trigger force change password dialog
            if (AppProperties.getUser().isInRecovery()) {
                // if the cancel-button is clicked in the dialog window => don't continue to primary window scene!
                // if the confirm-button is clicked => continue
                if (!forcedChangePasswordDialog()) {
                    usernameID.clear();
                    passwordID.clear();
                    return;
                }
            }

            SceneMethods.changeScene(View.PRIMARY, continueBtn);

        } else {
            incorrectCredentials.setVisible(true);
            usernameID.clear();
            passwordID.clear();
        }
    }

    public void exitButtonClicked(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Goes to Register screen. No data is transferred in this case of window transfer.
     *
     * @author Susanna Persson sp222xw
     */
    public void switchToRegister(ActionEvent actionEvent) throws IOException {
        // Load register scene
        SceneMethods.changeScene(View.REGISTER, continueBtn);
    }

    /**
     * Goes to Primary View screen. No data is transferred in this case of window transfer.
     *
     * @author Susanna Persson sp222xw
     */
    public void switchToPrimaryWindow(ActionEvent actionEvent) throws IOException {
        // Load primary window scene
        SceneMethods.changeScene(View.PRIMARY, continueBtn);
    }

    /**
     * Creates and manages a dialog window for changing the users password.
     * Will only trigger if the user-account has been flagged as "inRecovery", in order to change the temporary
     * password that has been sent.
     *
     * @return - true if the confirm-button is clicked (password changed), false otherwise
     * @author Joel Salo js225fg
     */
    public boolean forcedChangePasswordDialog() {
        // create the custom dialog.
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change password");
        dialog.setHeaderText("Account recovery: Change your temporary password");

        // set the button types.
        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // create the password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New password");

        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm password");

        Label passwordInfoLabel = new Label("Min. 6 characters (no spaces): upper- and lowercase letters and numbers.");
        passwordInfoLabel.setTextFill(Color.web("#FF0000"));
        passwordInfoLabel.setVisible(false);

        Label passwordNoMatchLabel = new Label("The passwords do not match.");
        passwordNoMatchLabel.setTextFill(Color.web("FF0000"));
        passwordNoMatchLabel.setVisible(false);

        Label passwordMatchLabel = new Label("Password OK! Click \"Confirm\" to save it and login.");
        passwordMatchLabel.setTextFill(Color.web("#008000"));
        passwordMatchLabel.setVisible(false);

        grid.add(passwordNoMatchLabel, 0, 0, 2, 1);
        grid.add(passwordMatchLabel, 0, 0, 2, 1);
        grid.add(passwordInfoLabel, 0, 0, 2, 1);
        grid.add(new Label("New password:"), 0, 1);
        grid.add(newPassword, 1, 1);
        grid.add(new Label("Confirm password:"), 0, 2);
        grid.add(confirmPassword, 1, 2);

        // enable/disable confirm-button depending on whether new pw and confirm pw match and criteria is met
        Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        // same criteria as when going through the registration-form
        String validPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";

        // validation
        newPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            // enables the confirm-button only when the passwords match and meet the criteria
            confirmButton.setDisable(!(newPassword.getText().equals(confirmPassword.getText()) &&
                    newPassword.getText().matches(validPassword)));
            // handles visibility of the info labels
            passwordMatchLabel.setVisible((newPassword.getText().equals(confirmPassword.getText())
                    && newPassword.getText().matches(validPassword)));
            passwordInfoLabel.setVisible(!newPassword.getText().matches(validPassword));
            passwordNoMatchLabel.setVisible(!(confirmPassword.getText().equals(newPassword.getText())) &&
                    newPassword.getText().matches(validPassword));
        });

        confirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            // enables the confirm-button only when the passwords match and meet the criteria
            confirmButton.setDisable(!(confirmPassword.getText().equals(newPassword.getText()) &&
                    confirmPassword.getText().matches(validPassword)));
            // handles visibility of the info labels
            passwordMatchLabel.setVisible((confirmPassword.getText().equals(newPassword.getText())
                    && newPassword.getText().matches(validPassword)));
            passwordNoMatchLabel.setVisible(!(confirmPassword.getText().equals(newPassword.getText())) &&
                    newPassword.getText().matches(validPassword));
        });


        dialog.getDialogPane().setContent(grid);

        // request focus on the current pw field by default
        Platform.runLater(newPassword::requestFocus);

        // get the new password when the confirm-button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return confirmPassword.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        // change password and toggle the users recovery status
        result.ifPresent(newPass -> {
            UserMethods.changePassword(AppProperties.getUser(), newPass);
            UserMethods.setInRecovery(AppProperties.getUser(), false);
            AppProperties.loadUsers();
        });

        // returns true if the password is changed, false if null (cancel is clicked)
        return dialog.getResult() != null;
    }

    /**
     * Initialize account recovery process: generate a new random password and send the recovery email.
     * Flag the users account as "inRecovery" in the database which will force the user to change password on the next
     * login.
     *
     * @author Joel Salo js225fg
     */
    private void initAccRecovery(String username, String email) {
        String temporaryPassword = AccountRecovery.generatePassword();

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        UserMethods.passwordRecovery(user, temporaryPassword);
        UserMethods.setInRecovery(user, true);
        AccountRecovery.sendEmail(user, temporaryPassword);
        AppProperties.loadUsers();
    }

    /**
     * @param event ActionEvent
     * @author Anton Munter
     */
    public void handleForgotPassword(ActionEvent event) {
        Label label = new Label("RESET PASSWORD");
        label.setFont(Font.font("System", 32));
        label.setTextFill(Color.web("#838383"));
        label.setPadding(new Insets(0, 0, 40, 0));
        Text info = new Text("To reset your password, please input your email address below and press \"Send\". " +
                "We'll send you an email with further instructions if the email is connected to a TimeWaves account.");
        info.setWrappingWidth(380);
        info.setFont(Font.font("System", 12));
        info.setFill(Color.web("#838383"));

        TextField email = new TextField();
        email.setPromptText("Email");
        email.setPrefHeight(40);

        Button sendBtn = new Button("Send");
        sendBtn.setDisable(true);
        sendBtn.getStyleClass().add("sendBtn");

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("closeBtn");

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(label, info, email, sendBtn, closeBtn);

        GridPane pane = new GridPane();
        pane.getStyleClass().addAll("popup", "forgotPassWindow");
        pane.setAlignment(Pos.TOP_CENTER);
        pane.getChildren().addAll(vBox);

        PopOver popOver = new PopOver(pane);
        popOver.setDetachable(false);
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        popOver.show(forgotPassBtn);

        //        Update borders on validation
        email.setOnKeyPressed(e -> {
            if (isValidEmail(email.getText())) {
                sendBtn.setDisable(false);
                email.getStyleClass().remove("error");
                email.getStyleClass().add("valid");
            } else {
                sendBtn.setDisable(true);
                email.getStyleClass().remove("valid");
                email.getStyleClass().add("error");
            }
        });

        sendBtn.setOnAction(e -> {
            // Check if email is valid and if email exist in db.
            if (isValidEmail(email.getText())) {
                String username = UserMethods.verifyEmail(email.getText());

                // if email is connected to a user, username will not be null => initiate account recovery
                if (username != null) {
                    initAccRecovery(username, email.getText());
                }

                popOver.hide();
            } else {
                email.getStyleClass().remove("error");
                email.getStyleClass().remove("valid");
                email.getStyleClass().add("error");
            }
        });

        closeBtn.setOnAction(e -> {
            popOver.hide();
        });
    }

    /**
     * @param email String
     * @return True if email is valid.
     * @author Anton Munter
     */
    public boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
