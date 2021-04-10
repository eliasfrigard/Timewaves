package com.timeline;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sql.UserMethods;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class RegisterController implements Initializable {

    @FXML private PasswordField password;
    @FXML private Label passwordLabel;
    @FXML private PasswordField confirmPassword;
    @FXML private Label confirmPasswordLabel;
    @FXML private TextField username;
    @FXML private TextField email;
    @FXML private TextField confirmEmail;
    @FXML private Label emailLabel;
    @FXML private Label confirmEmailLabel;

    @FXML private Button register;
    @FXML private Label registerLabel;

    @FXML private Button exitWindowBtn;

    private boolean passwordCriteriaIsMet = false;
    private boolean passwordIsMatch = false;
    private boolean usernameIsValid = false;
    private boolean emailIsValid = false;
    private boolean emailIsMatch = false;

    // Below are some tooltip objects to give information about what kind of information should be put in.
    private Tooltip usernameTooltip = new Tooltip();
    private Text usernameTooltipLengthReq = new Text();
    private Text usernameTooltipFirstCharReq = new Text();
    private Text usernameTooltipNoWhitespaceReq = new Text();

    private Tooltip passwordTooltip = new Tooltip();
    private Text passwordTooltipLengthReq = new Text();
    private Text passwordTooltipUppercaseReq = new Text();
    private Text passwordTooltipLowercaseReq = new Text();
    private Text passwordTooltipDigitReq = new Text();
    private Text passwordTooltipBlankspaceReq = new Text();

    /**
     * This method checks whether the input username is 'valid'. No checks are made here against information on the server.
     * @return Whether the contents of the password field is between 4 and 16 characters, and that the first character is a letter character and that there are no whitespace characters
     * @author Jacob Skoog js224wv
     */
    @FXML
    public boolean isUsernameFormatValid() {
        boolean allPassed = true;
        String usernameRegex = "[a-zA-Z]\\S{3,15}";

        String input = username.getText();

        if (input.length() == 0) return false; // To protect the input.substring line

        // First, check the length of the username
        if (input.length() >= 4 && input.length() <= 16) { // If the length is in the accepted interval, make the text for that requirement in the tooltip green
            usernameTooltipLengthReq.strokeProperty().set(Color.GREEN);
        } else { // Else, make it red and fail the username!
            allPassed = false;
            usernameTooltipLengthReq.strokeProperty().set(Color.RED);
        }

        // Second, check the first character is a letter
        if ( input.substring(0, 1).matches("[a-zA-Z]") ) { // If the first character is a letter, make the text green
            usernameTooltipFirstCharReq.strokeProperty().set(Color.GREEN);
        } else { // Else, fail username and make text red
            allPassed = false;
            usernameTooltipFirstCharReq.strokeProperty().set(Color.RED);
        }

        // Third, check that there are no whitespaces!
        if ( input.matches("\\S+")) {
            usernameTooltipNoWhitespaceReq.strokeProperty().set(Color.GREEN);
        } else {
            allPassed = false;
            usernameTooltipNoWhitespaceReq.strokeProperty().set(Color.RED);
        }

        if (allPassed) {
            registerLabel.setVisible(false);
            username.getStyleClass().removeAll("error");
            username.getStyleClass().add("valid");
            this.usernameIsValid = true;
        } else {
            username.getStyleClass().removeAll("valid");
            username.getStyleClass().add("error");
            this.usernameIsValid = false;
        }
        allowRegister();
        return allPassed;
    }

    /**
     * Checks the entered password to ensure that it matches our security criteria and adds a colored border to the
     * password field to match the status. Displays the "passwordLabel" if criteria are not met.
     * The password must be minimum 6 characters and contain:
     * - At least 1 digit
     * - At least 1 lower case letter
     * - At least 1 upper case letter
     * - No blank spaces
     * @return - true if password fulfills the criteria, false if not
     * @author Joel Salo js225fg
     */
    @FXML
    private boolean isPasswordCriteriaFulfilled() {
        String validPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";
        boolean criteriaIsMet = password.getText().matches(validPassword);

        if (password.getText().length() > 0) {
            // criteria is not met
            if (!criteriaIsMet) {
                password.getStyleClass().removeAll("valid");
                password.getStyleClass().add("error");

                // safety measure in case the user changes the password-field after confirming a password
                this.passwordIsMatch = isPasswordMatch();

                this.passwordCriteriaIsMet = false;
                passwordLabel.setVisible(true);
                allowRegister();

                return false;
            }
            // criteria is met
            else {
                password.getStyleClass().removeAll("error");
                password.getStyleClass().add("valid");

                // safety measure in case the user changes the password-field after confirming a password
                this.passwordIsMatch = isPasswordMatch();

                this.passwordCriteriaIsMet = true;
                passwordLabel.setVisible(false);
                allowRegister();

                return true;
            }
        }
        // if all the contents of the password-field gets deleted
        else {
            password.getStyleClass().removeAll("error", "valid");
            this.passwordCriteriaIsMet = false;
            this.passwordIsMatch = isPasswordMatch();
            passwordLabel.setVisible(false);
            allowRegister();

            return false;
        }
    }

    /**
     * Compares the values of the password-fields "password" and "confirmPassword", adds a colored border to the
     * confirm password-field to match the status. Displays the "confirmPasswordLabel" if criteria are not met.
     * Enables the "Register"-button if a match is detected.
     * @return - true if match, false if not
     * @author Joel Salo js225fg
     */
    @FXML
    public boolean isPasswordMatch() {
        boolean isMatch = confirmPassword.getText().equals(password.getText());

        if (confirmPassword.getText().length() > 0) {
            // no match
            if (!isMatch) {
                confirmPassword.getStyleClass().removeAll("valid");
                confirmPassword.getStyleClass().add("error");
                this.passwordIsMatch = false;
                confirmPasswordLabel.setVisible(true);
                allowRegister();

                return false;
            }
            // match
            else {
                confirmPassword.getStyleClass().removeAll("error");
                confirmPassword.getStyleClass().add("valid");
                this.passwordIsMatch = true;
                confirmPasswordLabel.setVisible(false);
                allowRegister();

                return true;
            }
        }
        // if all the contents of the confirm password-field gets deleted
        else {
            confirmPassword.getStyleClass().removeAll("error", "valid");
            this.passwordIsMatch = false;
            confirmPasswordLabel.setVisible(false);

            allowRegister();
            return false;
        }
    }

    /**
     * Checks the entered email to ensure that it matches our criteria and adds a colored border to the
     * text field to match the status. Displays the "emailLabel" if criteria is not met.
     * The email must be a "valid" email address:
     * - Loosely checked; as the user could have a personal domain etc.
     * - Makes sure the format is correct
     * - Permitted by RFC 5322
     * @return - true if email fulfills the criteria, false if not
     * @author Joel Salo js225fg
     */
    @FXML
    private boolean isEmailValid() {
        String validEmail = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        boolean criteriaIsMet = email.getText().matches(validEmail);

        if (email.getText().length() > 0) {
            // criteria is not met
            if (!criteriaIsMet) {
                email.getStyleClass().removeAll("valid");
                email.getStyleClass().add("error");

                // safety measure in case the user changes the email-field after confirming a email
                this.emailIsMatch = isEmailMatch();

                this.emailIsValid = false;
                emailLabel.setVisible(true);
                allowRegister();
                return false;
            }
            // criteria is met
            else {
                email.getStyleClass().removeAll("error");
                email.getStyleClass().add("valid");

                this.emailIsValid = true;
                emailLabel.setVisible(false);

                // safety measure in case the user changes the email-field after confirming a email
                this.emailIsMatch = isEmailMatch();
                allowRegister();
                return true;
            }
        }
        // if all the contents of the text-field gets deleted
        else {
            email.getStyleClass().removeAll("error", "valid");
            this.emailIsValid = false;
            emailLabel.setVisible(false);
            allowRegister();
            return false;
        }
    }
    /**
     * Compares the values of the text-fields "email" and "confirmEmail", adds a colored border to the
     * confirm email-field to match the status. Displays the "confirmEmail" label if criteria are not met.
     * Enables the "Register"-button if a match is detected.
     * @return - true if match, false if not
     * @author Joel Salo js225fg
     */
    @FXML
    public boolean isEmailMatch() {
        boolean isMatch = confirmEmail.getText().equals(email.getText());

        if (confirmEmail.getText().length() > 0) {
            // no match
            if (!isMatch) {
                confirmEmail.getStyleClass().removeAll("valid");
                confirmEmail.getStyleClass().add("error");
                this.emailIsMatch = false;
                confirmEmailLabel.setVisible(true);

                allowRegister();
                return false;
            }
            // match
            else {
                confirmEmail.getStyleClass().removeAll("error");
                confirmEmail.getStyleClass().add("valid");
                this.emailIsMatch = true;
                confirmEmailLabel.setVisible(false);

                allowRegister();
                return true;
            }
        }

        // if all the contents of the confirm email-field gets deleted
        else {
            confirmEmail.getStyleClass().removeAll("error", "valid");
            this.emailIsMatch = false;
            confirmEmailLabel.setVisible(false);

            allowRegister();

            return false;
        }
    }

    // enables or disables the register-button depending on if all criteria have been met or not
    private void allowRegister() {
        boolean allCheck = this.usernameIsValid && this.passwordCriteriaIsMet && this.passwordIsMatch &&
                this.emailIsValid && this.emailIsMatch;

        register.setDisable(!allCheck);
    }

    /**
     * Creates a new user with name and password from text fields and adds it to the database.
     * Assumes that passwords have been matched and controlled with regex.
     *
     * @author Elias Frigård ef222xf
     */
    @FXML
    private void registerUser () {
        try {
            // Creates a user object using username textfield and password password field
            User user = new User(username.getText(), password.getText(), email.getText());

            UserMethods.addUser(user);

            // Clear fields after registering.
            username.clear();
            username.getStyleClass().removeAll("valid", "error");

            password.clear();
            password.getStyleClass().removeAll("valid", "error");

            confirmPassword.clear();
            confirmPassword.getStyleClass().removeAll("valid", "error");

            email.clear();
            email.getStyleClass().removeAll("valid", "error");

            confirmEmail.clear();
            confirmEmail.getStyleClass().removeAll("valid", "error");

            AppProperties.updateUser(user);

            // Add new user to local list of users
            AppProperties.userList.add(user);

            // Go to primary view after successfully registering a User
            switchToPrimaryViewRegisterSuccess(user);
        } catch (SQLException e) {
            // Error code 1062 represents duplicate entries in the database.
            if (e.getErrorCode() == 1062) {
                registerLabel.setText("User already exists!");
                registerLabel.setVisible(true);
            } else {
                registerLabel.setText("Something went wrong...!");
                registerLabel.setVisible(true);
            }
        }
    }

    /**
     *  Goes to Login screen. No data is transferred in this case of window transfer.
     *  @author Susanna Persson sp222xw
     */
    public void switchToLogin() throws IOException {
        SceneMethods.changeScene(View.LOGIN, register);
    }

    /**
     * Go to primary view after successfully registering as a user.
     * @param user - the user that has registered
     * @author Fredrik Ljungner fl222ai
     * @author Susanna Persson sp222xw
     * @author Joel Salo js225fg
     */
    public void switchToPrimaryViewRegisterSuccess(User user) {
        // Get controller of primary scene
        PrimaryWindowController primaryWindowController = (PrimaryWindowController)
                SceneMethods.getController(View.PRIMARY);
        
        SceneMethods.changeScene(View.PRIMARY, register);
    }

    /**
     * Appending tooltips to important fields
     * Can be used for more things like this, running some processes at bootup (think of the on-load stuff you'd do in javascript custom html elements)
     * @param url
     * @param resourceBundle
     * @author Jacob Skoog js224wv
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Tooltip usernameTooltip = new Tooltip("Put in a username with between 4 and 16 characters that starts with a letter and contains only letters and digits.");
        //Tooltip passwordTooltip = new Tooltip("The password must contain at least 6 characters, and there must be at least 1 uppercase letter, 1 lowercase letter and 1 digit.");

        // Setting text and strokecolor for the text for the username field's tooltip
        usernameTooltipLengthReq.setText("Username must be between 4 and 16 characters long");
        usernameTooltipLengthReq.strokeProperty().set(Color.WHITE);
        usernameTooltipFirstCharReq.setText("Must begin with a letter");
        usernameTooltipFirstCharReq.strokeProperty().set(Color.WHITE);
        usernameTooltipNoWhitespaceReq.setText("No whitespaces");
        usernameTooltipNoWhitespaceReq.strokeProperty().set(Color.WHITE);
        VBox usernameTooltipBox = new VBox(usernameTooltipLengthReq, usernameTooltipFirstCharReq, usernameTooltipNoWhitespaceReq);

        // Setting text amd stroke color for the text for the password field's tooltip
        passwordTooltipLengthReq.setText("Password must be at least 6 characters long");
        passwordTooltipLengthReq.strokeProperty().set(Color.WHITE);
        passwordTooltipUppercaseReq.setText("Password must contain at least 1 uppercase letter");
        passwordTooltipUppercaseReq.strokeProperty().set(Color.WHITE);
        passwordTooltipLowercaseReq.setText("Password must contain at least 1 lowercase letter");
        passwordTooltipLowercaseReq.strokeProperty().set(Color.WHITE);
        passwordTooltipDigitReq.setText("Password must contain at least 1 digit");
        passwordTooltipDigitReq.strokeProperty().set(Color.WHITE);
        passwordTooltipBlankspaceReq.setText("Password cannot contain any spaces");
        passwordTooltipBlankspaceReq.strokeProperty().set(Color.WHITE);
        VBox passwordTooltipBox = new VBox(passwordTooltipLengthReq, passwordTooltipUppercaseReq,
                passwordTooltipLowercaseReq, passwordTooltipDigitReq, passwordTooltipBlankspaceReq);

        usernameTooltip.setGraphic(usernameTooltipBox);
        passwordTooltip.setGraphic(passwordTooltipBox);

        Tooltip.install(username, usernameTooltip);
        Tooltip.install(password, passwordTooltip);
    }
}
