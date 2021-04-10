package sql;

import com.timeline.Timeline;
import sql.table_enums.DBTables;
import sql.table_enums.TimelineColumns;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * A class with static methods for accessing and manipulating the Timelines table of the database.
 * @author Stefan Jägstrand & Anton Munter
 * Changed so that keywords are added to db, workaround solution because JDBC dosn't like the setArray() sql method, returned SQLFeatureNotSupportedException.
 * So column keywords inserts Arrays.toString(). Open to suggestions! /Mattias
 */
public class TimelineMethods {
    private static String DBConnection = "jdbc:mysql://134.209.81.97:3306/timewaves";
    private static String DBUsername = "test";
    private static String DBPassword = "Test1234#";


    /**
     * Gets all the existing keywords of the timelines in the database.
     * @return  An ArrayList with Strings.
     * @author Susanna Persson sp222xw
     */

    public static ArrayList<String> getExistingKeywords() {
        ArrayList<String> keywordList = new ArrayList<>();

            try {
                // Create connection to db
                Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

                // Create and execute statement.
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM " + DBTables.TIMELINES  + " WHERE " + TimelineColumns.KEYWORDS + " NOT LIKE '%null%'");
                ResultSet result = statement.executeQuery();

                // Splits the keywords column into separate keywords and adds them to the list.
                while (result.next()) {

                    String keywordString = result.getString( TimelineColumns.KEYWORDS.toString() );
                    String[] keywords = new String[0];
                    if (keywordString != null) {
                        keywords = keywordString.split(",");
                    }

                    Collections.addAll(keywordList, keywords);
                }

                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        return keywordList;
    }

    /**
     * Inserts the passed timeline object to the server. Returns true if it was successfully posted, false otherwise.
     * @param timeline The timeline to insert into the server
     * @return The SQL response code for the insert. 0 representing success and anything else being some manner of error.
     */
    public static int insertTimeline(Timeline timeline) {

        int sqlCode;
        try {
            Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword); // Open a connection with the server
            String insertStatement = "INSERT INTO " + DBTables.TIMELINES +
                    " (" + TimelineColumns.NAME + ", " + TimelineColumns.START + ", " + TimelineColumns.END +
                    ", " + TimelineColumns.TIMEFRAME + ", " + TimelineColumns.KEYWORDS + ") VALUES (?, ?, ?, ?, ?)";
            String[] keywords = timeline.getKeywords();

            PreparedStatement postTimeline = conn.prepareStatement(insertStatement);

            postTimeline.setString(1, timeline.getName());
            postTimeline.setLong(2, timeline.getStartUnit());
            postTimeline.setLong(3, timeline.getEndUnit());
            postTimeline.setString(4, timeline.getUnitName());
            // Add keywords to db.
            postTimeline.setString(5, Arrays.toString(keywords).replace("[","").replace("]",""));


            sqlCode = postTimeline.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            sqlCode = e.getErrorCode();
        }

        return sqlCode;
    }

    /**
     * Retrieves a timeline from the database based on the name of the timeline. Returns null if no match was found
     * @param timelineName The name of the timeline
     * @return The matching timeline in the database if found, null otherwise
     */
    public static Timeline selectTimeline(String timelineName) {
        Timeline timeline = null;
        try {
            Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword); // Open a connection with the server
            String insertStatement = "SELECT * FROM " + DBTables.TIMELINES + " WHERE " + TimelineColumns.NAME + "=?";

            PreparedStatement selectTimeline = conn.prepareStatement(insertStatement);

            selectTimeline.setString(1, timelineName);
            ResultSet selectResult = selectTimeline.executeQuery();

            timeline = timelinesForResultSet(selectResult).get(0); // Should just be a single hit, so take the first one

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeline;
    }

    /** Returns an ArrayList containing all timelines entities
     *  by calling statement "SELECT * FROM Timelines" and converting them
     *  to TimelineObject(s).
     *
     *  ArrayList will be empty if no timelines are stored
     *  in the database.
     * @author Anton Munter & Stefan Jägstrand
     * @return ArrayList<TimelineObject> containing timeline objects
     */
    public static ArrayList<Timeline> getTimelines() {
        ArrayList<Timeline> timeLineList = new ArrayList<>();

        // Populate the List!
        try {
            // Create connection to db
            Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);
            // Create and execute statement.
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM " + DBTables.TIMELINES);
            ResultSet result = statement.executeQuery();

            timeLineList = timelinesForResultSet(result);

            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        timeLineList.sort(null);
        return timeLineList;
    }

    /**
     * Deletes timeline from database by sending statement
     * "DELETE FROM Timelines WHERE Name =?" + (name of passed timeline)
     *
     * @param tl Timeline to delete from databse
     * @author ???
     * @modified-by Stefan Jägstrand 04/23
     * Changed statement from "TimelineID" to "Name".
     * @modified (again by stefan) 04-27 Quick fix!
     */
    public static void deleteTimeline (Timeline tl) {
        try {
            // Make connection to the database.
            Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

            EventMethods.deleteEvents(tl);

            // Create a prepared statement for deleting user.
            PreparedStatement st = con.prepareStatement("DELETE FROM " + DBTables.TIMELINES + " WHERE " + TimelineColumns.NAME + " = ?");

            // Add parameters to statement.
            st.setString(1, tl.getName());

            // Execute query.
            st.execute();

            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a Timeline with new information. Must be passed a Timeline object with the updated values.
     * @param tl Timeline object with updated values
     * @author Elias Frigård ef222xf
     */
    public static void updateTimeline(Timeline tl) {
        try {
            // Make connection to the database.
            Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

            PreparedStatement ps = con.prepareStatement("UPDATE " + DBTables.TIMELINES  +
                    " SET " + TimelineColumns.NAME + " = ?, " + TimelineColumns.START + " = ?, " +
                    TimelineColumns.END + " = ?, " + TimelineColumns.TIMEFRAME + " = ?, " + TimelineColumns.KEYWORDS + " = ? " +
                    "WHERE " + TimelineColumns.TIMELINEID + " = ?");

            // Update parameters.
            ps.setString(1, tl.getName());
            ps.setLong(2, tl.getStartUnit());
            ps.setLong(3, tl.getEndUnit());
            ps.setString(4, tl.getUnitName());
            ps.setString(5, Arrays.toString(tl.getKeywords()).replace("[","").replace("]",""));

            // Find specific Timeline to update.
            ps.setInt(6, tl.getId());

            // Execute query and close connection.
            ps.execute();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int setTODD(Timeline newTODD) {
        int sqlCode;
        try {
            setTODDsToZero(); // Start by setting all other timelines TODD status to 0

            Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);
            String query = "UPDATE " + DBTables.TIMELINES + " SET " + TimelineColumns.TODD + "=1 WHERE " + TimelineColumns.TIMELINEID + "=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, newTODD.getId());
            sqlCode = stmt.executeUpdate();
            conn.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            sqlCode = throwables.getErrorCode();
        }


        return sqlCode;
    }

    private static void setTODDsToZero() throws SQLException {

        Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);
        String query = "UPDATE Timelines SET " + TimelineColumns.TODD + "=0 WHERE " + TimelineColumns.TODD + "=1";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.executeUpdate();
        conn.close();

    }

    private static ArrayList<Timeline> timelinesForResultSet(ResultSet timelineSet) throws SQLException {
        ArrayList<Timeline> list = new ArrayList<>();
        String[] keywords;

        // Create TimeLineObjects for every entity in result and add it to list.
        timelineSet.beforeFirst();
        while (timelineSet.next()) {
            // Getting all the values for the local timeline object
            int timelineId = timelineSet.getInt( TimelineColumns.TIMELINEID.toString() );
            String timelineName = timelineSet.getString( TimelineColumns.NAME.toString() );
            long timelineStartUnit = timelineSet.getLong( TimelineColumns.START.toString() );
            long timelineEndUnit = timelineSet.getLong( TimelineColumns.END.toString() );
            String timeframeType = timelineSet.getString( TimelineColumns.TIMEFRAME.toString() );
            boolean TODD = timelineSet.getInt( TimelineColumns.TODD.toString() ) == 1;

            // split the inserted Arrays.toString() back to an array
            String keywordString = timelineSet.getString( TimelineColumns.KEYWORDS.toString() );
            if (keywordString != null) {
                keywords = keywordString.split(",");
            } else {
                keywords = new String[]{};
            }

            Timeline timeline = new Timeline(timelineId, timelineName, timelineStartUnit, timelineEndUnit, timeframeType, keywords);
            timeline.setTODD(TODD);
            RatingMethods.getRatingSets(timeline);
            list.add(timeline);
        }

        return list;
    }


}