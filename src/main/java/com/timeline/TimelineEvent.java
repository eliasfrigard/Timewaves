package com.timeline;

import java.io.*;
import java.nio.file.Files;

/**
 * TimelineEvent class. Currently very empty, more properties will be added in the future.
 *
 * @author Susanna Persson sp222xw
 */

public class TimelineEvent implements Comparable<TimelineEvent> {
    private int eventID = Integer.MIN_VALUE; // ID for this event, can only be retrieved from the server, not currently in use.
    private Timeline ownerTimeline; // ID for the timeline this event belongs to
    private String eventName; // Event name.
    private long placeOnTimeline; // Place on the timeline range. Range is returned by getRange() from the Timeline class.
    private String shortDesc;
    private String longDesc;
    private String imagePath;
    private File imageFile = null;

    // Constructors

    public TimelineEvent() {
    }

    /**
     * Creates an event with set datafields
     * @param ownerLine The timeline this event belongs to
     * @param eventName The name for the event
     * @param placeOnTimeline The timestamp for the event
     * @param shortDesc A short description of the event
     * @param longDesc A detailed description of the event
     * @param imagePath The path to the picture for the event
     */
    public TimelineEvent(Timeline ownerLine, String eventName, long placeOnTimeline, String shortDesc, String longDesc, String imagePath) {
        this.setTimeline(ownerLine);
        this.setEventName(eventName);
        this.setPlaceOnTimeline(placeOnTimeline);
        this.setShortDesc(shortDesc);
        this.setLongDesc(longDesc);
        this.setImagePath(imagePath);
    }

    // Getters and setters

    public int getEventID() { return eventID; }

    public void setEventID(int id) { eventID = id; }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public long getPlaceOnTimeline() {
        return placeOnTimeline;
    }

    /**
     * Sets the timestamp for this event. Returns true if the passed value is greater than or equal to 0 (that is, it is a 'legal' value)
     * @param placeOnTimeline Timestamp for this event. Must be greater than or equal to 0
     * @return True if passed long l satisfies 'l >= 0'
     */
    public boolean setPlaceOnTimeline(long placeOnTimeline) {
        boolean legalTime = placeOnTimeline >= 0;
        if (legalTime) {
            this.placeOnTimeline = placeOnTimeline;
        }
        return legalTime;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public Timeline getTimeline() {
        return ownerTimeline;
    }

    /**
     * Takes the timeline this event belongs to as an argument and saves its ID for communication with the server.
     * @param ownerTimeline The timeline this event belongs to
     */
    public void setTimeline(Timeline ownerTimeline) {
        this.ownerTimeline = ownerTimeline;
    }

    public String getImagePath() {
        return imagePath;
    }


    /**
     * Used to set the path for where an image fetched from the server will be saved.
     * If a Event has been locally created, one MUST make a separate call to
     * setImageFile after this method call to set that file's path to the file outside of the project directory
     * @param path Path where file will be saved when fetched from the server
     */
    public void setImagePath(String path) {
        imagePath = path;

        if (path.matches(AppProperties.eventImgPath + ".+")) setImageFile(path);
    }

    /**
     * Function for setting the imageFile for the event.
     * Is also called by setImagePath, but can be called separately after when, for example, the object is being freshly created and you need it to have a path outside the project
     * @param path
     */
    public void setImageFile(String path) {
        File fromPath = new File(path);

        if (fromPath.exists()) imageFile = fromPath;

    }

    public File getImageFile() { return imageFile; }

    /**
     * Gets the BufferedInputStream for the image file relating to the imagePath
     * @return The image file as a BufferedInputStream
     */
    public InputStream getImageStream() {
        InputStream bufferedStream = null;

        try {
            if (imageFile != null && imageFile.exists()) bufferedStream = new BufferedInputStream(Files.newInputStream(imageFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bufferedStream;
    }

    @Override
    public int compareTo(TimelineEvent otherEvent) {
        if (this.getPlaceOnTimeline() < otherEvent.getPlaceOnTimeline()) return 1;
        else if (this.getPlaceOnTimeline() == otherEvent.getPlaceOnTimeline()) {
            return this.getEventName().compareToIgnoreCase(otherEvent.getEventName());
        }
        else return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimelineEvent that = (TimelineEvent) o;

        if (getPlaceOnTimeline() != that.getPlaceOnTimeline()) return false;
        return getEventName().equals(that.getEventName());
    }

    @Override
    public int hashCode() {
        int result = getEventName().hashCode();
        result = 31 * result + (int) (getPlaceOnTimeline() ^ (getPlaceOnTimeline() >>> 32));
        return result;
    }
}
