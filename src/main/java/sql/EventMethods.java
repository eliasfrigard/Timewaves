package sql;

import com.timeline.AppProperties;
import com.timeline.Timeline;
import com.timeline.TimelineEvent;
import sql.table_enums.DBTables;
import sql.table_enums.EventColumns;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Methods concerning TimeNodes (TimelineEvents) in the database.
 * @author Susanna Persson sp222xw
 */

public class EventMethods {
    private static final String DBConnection = "jdbc:mysql://134.209.81.97:3306/timewaves";
    private static final String DBUsername = "test";
    private static final String DBPassword = "Test1234#";


    /**
     * Adds a TimeNode with the information in the parameters, to the TimeNode table in the database.
     * @param timelineID    The id of the Timeline that owns the TimeNode.
     * @param name          Name of the event.
     * @param time          Place on timeline.
     * @param shortDesc     Short description.
     * @param longDesc      Long description.
     * @param imagePath     The path where the image should be saved in the project
     * @param fileStream    InputStream from the image file itself
     * @return              Returns an sql error code, if any.
     */
    public static int addEvent(int timelineID, String name, long time, String shortDesc, String longDesc, String imagePath, InputStream fileStream) {
        int sqlCode;


        try {
            Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword); // Open a connection with the server
            String query = "INSERT INTO " + DBTables.EVENTS +
                    " (" + EventColumns.TIMELINEID + ", " + EventColumns.NAME + ", " +
                    EventColumns.TIME +", " + EventColumns.SHORTDESC + ", " +
                    EventColumns.LONGDESC + ", " + EventColumns.IMAGEPATH + ", " + EventColumns.IMAGEFILE + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, timelineID);
            stmt.setString(2, name);
            stmt.setLong(3, time);
            stmt.setString(4, shortDesc);
            stmt.setString(5, longDesc);
            stmt.setString(6, imagePath);
            stmt.setBinaryStream(7, fileStream);

            sqlCode = stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            sqlCode = e.getErrorCode();
        }

        return sqlCode;
    }

    /**
     * Adds the passed TimelineEvent to the database
     * @param timeNode The event (or Node) to the database
     * @return the SQL response code to the operation
     */
    public static int addEvent(TimelineEvent timeNode) {
        int sqlCode = Integer.MIN_VALUE;
        try {
            if (timeNode.getImageFile() != null) {
                sqlCode =  addEvent(timeNode.getTimeline().getId(), timeNode.getEventName(), timeNode.getPlaceOnTimeline(), timeNode.getShortDesc(), timeNode.getLongDesc(),
                        timeNode.getImagePath(), new FileInputStream(timeNode.getImageFile()));
            }
            else {
                sqlCode =  addEvent(timeNode.getTimeline().getId(), timeNode.getEventName(), timeNode.getPlaceOnTimeline(), timeNode.getShortDesc(), timeNode.getLongDesc(),
                        timeNode.getImagePath(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlCode;
    }

    /**
     * Gets all events from the TimeNode Table that belong to the passed Timeline object
     * @param timeline The timeline the events shall belong to
     * @return An arraylist with all of the matching events
     */
    public static ArrayList<TimelineEvent> selectEvents(Timeline timeline) {
        ArrayList<TimelineEvent> events = new ArrayList<>();

        try {
            Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword); // Open a connection with the server
            String query = "SELECT * FROM " + DBTables.EVENTS + " WHERE " + EventColumns.TIMELINEID + "=?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, timeline.getId());

            ResultSet nodes = stmt.executeQuery();
            nodes.beforeFirst();
            while (nodes.next()) { // While there are more events in the  ResultSet, run this loop that creates TimelineEvent objects from the information and adds them to an arrayList
                int nodeID = nodes.getInt( EventColumns.EVENTID.toString() );
                String eventName = nodes.getString( EventColumns.NAME.toString() );
                long eventTime = nodes.getLong( EventColumns.TIME.toString() );
                String eventShort = nodes.getString( EventColumns.SHORTDESC.toString() );
                String eventLong = nodes.getString( EventColumns.LONGDESC.toString() );
                String eventPicPath = nodes.getString( EventColumns.IMAGEPATH.toString() );
                InputStream eventPicStream = nodes.getBinaryStream( EventColumns.IMAGEFILE.toString() );

                if (    eventPicStream != null &&
                        !eventPicPath.equals(AppProperties.nullPath) &&
                        !eventPicPath.equals("") )  {
                    eventPicStream = new BufferedInputStream( eventPicStream );
                    writeImageToFile(eventPicPath, eventPicStream); // Writes the fetched binary stream of the image to a file
                }

                TimelineEvent event = new TimelineEvent(timeline, eventName, eventTime, eventShort, eventLong, eventPicPath);
                event.setEventID(nodeID);
                events.add(event);
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    /**
     * Updates the one of the data fields of an event on the database
     * OBSERVE THAT this method will not accept the NodeID column or ImageFile column
     * @param eventToUpdate The event the user has initiated a change for
     * @param columnToChange The column of the event that will be changed
     * @param newValue The new value for the chosen column
     * @return The SQL response code to the query
     * @author Jacob Skoog js224wv made started writing the methof
     * @modified by someone else, who made it actually work properly
     */
    public static int updateEventInfo(TimelineEvent eventToUpdate, EventColumns columnToChange, String newValue) {
        int sqlCode = Integer.MIN_VALUE;

        if (columnToChange == EventColumns.EVENTID) {
            System.out.println("Cannot change the NodeID Column!");
            return sqlCode;
        }
        else if (columnToChange == EventColumns.IMAGEFILE) {
            System.out.println("This method is not suitable for changing the image");
            return sqlCode;
        }

        try {

            Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword); // Open a connection with the server
            PreparedStatement stmnt;
            int eventID = eventToUpdate.getEventID();

            if (columnToChange == EventColumns.TIMELINEID) { // If the ID of the owner Timeline is changed, path must also change
                String query = "UPDATE " + DBTables.EVENTS + " SET " + columnToChange + "=?, " + EventColumns.IMAGEPATH + "=? WHERE " + EventColumns.EVENTID + "=?";
                String path = changeTimelineInPath(eventToUpdate.getImagePath(), eventToUpdate, newValue);
                stmnt = conn.prepareStatement(query);
                stmnt.setInt(1, Integer.parseInt(newValue));
                stmnt.setString(2, path);
                stmnt.setInt(3, eventID);

            } else {
                String query =  "UPDATE " + DBTables.EVENTS + " SET " + columnToChange + "=? WHERE " + EventColumns.EVENTID + "=?";
                stmnt = conn.prepareStatement(query);
                stmnt.setString(1, newValue);
                stmnt.setInt(2, eventID);
            }



            sqlCode = stmnt.executeUpdate();
            conn.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            sqlCode = throwables.getErrorCode();
        }

        return sqlCode;
    }

    /**
     * Updates an event from the application to the database. Does not handle image data yet, feel free to add.
     * @param event TimelineEvent
     * @author Elias Frigård ef222xf
     */
    public static void updateEventInfo (TimelineEvent event) {
        try {
            Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword); // Open a connection with the server

            PreparedStatement ps = con.prepareStatement("UPDATE " + DBTables.EVENTS +
                    " SET " + EventColumns.NAME + " = ?, " + EventColumns.TIME + " = ?, " + EventColumns.SHORTDESC +
                    " = ?, " + EventColumns.LONGDESC + " = ? WHERE " + EventColumns.EVENTID + " = ?");

            // Update parameters.
            ps.setString(1, event.getEventName());
            ps.setLong(2, event.getPlaceOnTimeline());
            ps.setString(3, event.getShortDesc());
            ps.setString(4, event.getLongDesc());

            // Find specific Event to update.
            ps.setInt(5, event.getEventID());

            // Execute query and close connection.
            ps.execute();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the image of the passed event to the passed image stream
     * @param eventToChange The event to give a new picture
     * @param imageStream The new picture for the event
     * @param ogPath The original path of the picture
     * @return The server response code
     */
    public static int updateImage(TimelineEvent eventToChange, InputStream imageStream, String ogPath) {
        int sqlCode;

        try {

            Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword); // Open a connection with the server
            String newPath = generateImgPathWithNodeID(eventToChange, ogPath);

            String query = "UPDATE " + DBTables.EVENTS + " SET " + EventColumns.IMAGEPATH + "=?, " + EventColumns.IMAGEFILE + "=? WHERE " + EventColumns.EVENTID + "=?";
            PreparedStatement stmnt = conn.prepareStatement(query);

            stmnt.setString(1, newPath);
            stmnt.setBinaryStream(2, imageStream);
            stmnt.setInt(3, eventToChange.getEventID());


            sqlCode = stmnt.executeUpdate();
            conn.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            sqlCode = throwables.getErrorCode();
        }

        return sqlCode;
    }

    /**
     * Deletes passed event from database.
     * @param event The event to delete
     * @author Stefan Jägstrand sj223gg 05-04
     */
    public static void deleteEvent (TimelineEvent event) {
        try {
            // Get database ID from event
            int eventID = event.getEventID();

            // Create connection to database, send delete statement with appropriate data.
            Connection conn = DriverManager.getConnection(DBConnection,DBUsername,DBPassword);

            // Delete event based on ID
            PreparedStatement statement = conn.prepareStatement("DELETE FROM " + DBTables.EVENTS + " WHERE " + EventColumns.EVENTID + "=?");
            statement.setInt(1, eventID);

            statement.execute();
            conn.close();

        } catch (SQLException e) {
            System.err.println("SQLException in deleteEvent() ...");
            e.printStackTrace();
        }
    }

    /**
     * Removes all timelines belonging to a Timeline from the server
     * @param timeline The timeline to remove all events from
     */
    public static void deleteEvents (Timeline timeline) {
        Connection con;
        try {
            con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);
            PreparedStatement st2 = con.prepareStatement("DELETE FROM " + DBTables.EVENTS + " WHERE " + EventColumns.TIMELINEID + " = ?");
            st2.setInt(1, timeline.getId());
            st2.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Help method mean to take the binaryStream collected from the server and write it to a file in the directory src/main/resources/images/eventPictures/
     * @param path The path the stream should write to
     * @param imageStream The binary stream of the file
     * @return The file produced and written to
     */
    private static File writeImageToFile(String path, InputStream imageStream) {
        // First read from the file into an array
        File producedFile = null;
        try {
            byte[] buffer = new byte[imageStream.available()];
            imageStream.read(buffer);

            producedFile = new File(path);
            OutputStream outStream = new FileOutputStream(producedFile);
            outStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        return producedFile;
    }

    private static String changeTimelineInPath(String oldPath, TimelineEvent event, String newTimelineID) {
        String newPath = "";

        String[] aroundPeriod = oldPath.split("\\.");
        String suffix = aroundPeriod[1];
        newPath = AppProperties.eventImgPath + newTimelineID + "-" + event.getEventID() + "." + suffix;

        return newPath;
    }

    private static String generateImgPathWithNodeID(TimelineEvent event, String filePath) { // Need to generate a filename based on the owning timeline ID as eventID isn't known on creation
        Timeline owner = event.getTimeline();

        String path = AppProperties.eventImgPath + owner.getId() + "-" + event.getEventID() + getFileExtension(filePath);

        return path;
    }

    /**
     * Help method that gets the file extension (e.g. .png or .jpeg) from a file's path
     * @param filePath
     * @return
     */
    private static String getFileExtension(String filePath) {
        String[] pathSplit = filePath.split("\\.");
        String ext = "." + pathSplit[1];

        return ext;
    }

}
