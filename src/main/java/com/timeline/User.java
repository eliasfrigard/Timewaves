package com.timeline;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * A class representing a User of the TimeWaves application.
 * @author Joel Salo js225fg
 */
public class User {
    private String username;
    private String password;
    private String email;
    private boolean isAdmin = false;
    private boolean inRecovery = false;
    private int userID;

    public User () {
    }

    /**
     * Constructs a User by hashing a password String and adding username.
     * @param username String
     * @param password String
     */
    public User (String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    /**
     * Constructs a User by hashing the password, adding username and adding email.
     * @param username String
     * @param password String
     * @param email String
     */
    public User (String username, String password, String email) {
        setUsername(username);
        setPassword(password);
        setEmail(email);
    }

    /**
     * Method for constructing user with predefined, hashed password, admin status and database userID.
     * For example used when getting list of users from the database.
     * @param username String
     * @param hashedPassword String
     * @param isAdmin Boolean
     * @param userID int
     * @param email String
     * @author Elias Frigård ef222xf
     */
    public User (String username, String hashedPassword, String email, boolean isAdmin, boolean inRecovery, int userID) {
        setUsername(username);
        setEmail(email);
        setAdmin(isAdmin);
        setInRecovery(inRecovery);
        this.password = hashedPassword;
        this.userID = userID;
    }

    /**
     * Returns the username of the user.
     * @return - the username (String)
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * @param username - the chosen username (String)
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the hashed password of the user.
     * @return - the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Hashes the password and sets it for the user.
     * @param password - the chosen password to be hashed and stored
     */
    public void setPassword(String password) {
        this.password = DigestUtils.sha256Hex(password);
    }

    /**
     * Sets the email of the user.
     * @param email - the users email address (String)
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the users email address.
     * @return - the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the users admin status, true if administrator false if not.
     * @return - boolean representing if the user is admin
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Sets the whether the user is an administrator or not.
     * @param admin - is the user admin or not (boolean)
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Returns the users admin recovery status, true if user has reset its password false if not.
     * @return - boolean representing if the user is in recovery mode
     */
    public boolean isInRecovery() {
        return inRecovery;
    }

    /**
     * Sets the whether the user is in recovery mode or not.
     * @param recovery - is the user in recovery or not (boolean)
     */
    public void setInRecovery(boolean recovery) {
        inRecovery = recovery;
    }

    public int getUserID () {
        return this.userID;
    }
}
