package com.timeline;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sql.TimelineMethods;
import sql.UserMethods;

import java.util.ArrayList;

import java.util.Iterator;


/**
 * @author Elias Holmér/Anton Munter
 * Store program wide properties
 */
public class AppProperties {
    public static final String eventImgPath = "src/main/resources/images/eventPictures/";
    public static final String nullPath = "no-path-jones";
    public static ObservableList<Timeline> listGlobal = FXCollections.observableArrayList();
    public static ArrayList<User> userList = new ArrayList<>();
    public static Timeline latestTimeline = null;
    private static User user = new User();

    // Changed name from setNewTimeline to updateLatestTimeline / STEFAN 04/23
    public static void updateLatestTimeline(Timeline tl) {
        latestTimeline = tl;
    }

    public static Timeline getLatestTimeline() {
        return latestTimeline;
    }

    /**
     * Gets all timelines in the listGLobal as an Arraylist of Timelines.
     * @author Susanna Persson
     */
    public static ArrayList<Timeline> getAllTimelines() {
        ArrayList<Timeline> tls = new ArrayList<>();
        for (Timeline tl : listGlobal) {
            tls.add(tl);
        }
        return tls;
    }

    /**
     * @author Elias Holmér/Anton Munter
     * Load timelines from database.
     */
    public static void loadTimelines(){
        listGlobal.addAll(TimelineMethods.getTimelines());
    }

    /**
     * @author Elias Holmér/Anton Munter
     * Load timelines from database.
     */
    public static void loadUsers(){
        userList.addAll(UserMethods.getUserList());
    }

    /**
     * @author Anton Munter
     * @return Return current user
     */
    public static User getUser() {
        return user;
    }

    /**
     * Update current user
     * @param newUser Current user
     * @author Anton Munter
     */
    public static void updateUser(User newUser) {
        user = newUser;
    }

    /**
     * Method for looking up a timeline based on its ID
     * @param timelineID The ID for the timeline
     * @return The timeline matching the passed ID
     */
    public static Timeline findTimeline(int timelineID) {
        Timeline targetLine = null;
        Iterator<Timeline> it = listGlobal.iterator();
        while (it.hasNext() && targetLine == null) {
            Timeline currentLine = it.next();
            if (currentLine.getId() == timelineID) targetLine = currentLine;
        }
        return targetLine;
    }

    /** Sets TODD field to false in all timelines in listGlobal.
     *
     * @return boolean true if a change was made, false if not.
     * @author Stefan Jägstrand 05-11
     */
    public static boolean resetAllToddFlags () {
        // Iterate through listGlobal and set TODD to false.
        boolean foundTODD = false;
        Iterator<Timeline> it = listGlobal.iterator();
        while (it.hasNext()) {
            Timeline currentLine = it.next();

            if (currentLine.isTODD()) {
                currentLine.setTODD(false);
                foundTODD = true;
            }
        }
        // Returns true if a TODD field was changed
        return foundTODD;
    }

    /**
     * @author Anton Munter
     */
    public static void terminateProgram() {
        System.exit(0);
    }
}