package sql;

import com.timeline.Timeline;
import sql.table_enums.DBTables;
import sql.table_enums.RatingColumns;

import java.sql.*;

public class RatingMethods {
    // Vital DB access info
    private static String DBConnection = "jdbc:mysql://134.209.81.97:3306/timewaves";
    private static String DBUsername = "test";
    private static String DBPassword = "Test1234#";


    /**
     * Loads the passed timeline with the stored ratings on the database
     * @param tl The timeline to load the ratings into
     */
    public static void getRatingSets(Timeline tl) {
        try {
            Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);
            String getRatingsStatement = "SELECT " + RatingColumns.USERID + ", " + RatingColumns.RATING + " FROM " + DBTables.RATINGS +
            " WHERE " + RatingColumns.TIMELINEID + "=?";
            PreparedStatement stmnt = conn.prepareStatement(getRatingsStatement);
            stmnt.setInt(1, tl.getId());

            // Gets the userID's and their accompanying ratings for the TL
            ResultSet results = stmnt.executeQuery();

            while(results.next()) {
                int userID = results.getInt(1);
                double rating = results.getDouble(2);
                tl.putRating(userID, rating);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static int insertRatingSet(int timelineID, int userID, double rating) {
        int sqlCode;
        try {
            Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);
            String insertRatingSet = "INSERT INTO " + DBTables.RATINGS + " (" + RatingColumns.TIMELINEID + ", " +
                    RatingColumns.USERID + ", " + RatingColumns.RATING + ") VALUES (?, ?, ?);";
            PreparedStatement stmnt = conn.prepareStatement(insertRatingSet);
            stmnt.setInt(1, timelineID);
            stmnt.setInt(2, userID);
            stmnt.setDouble(3, rating);
            sqlCode = stmnt.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            sqlCode = throwables.getErrorCode();
        }
        return sqlCode;
    }

    /**
     * Updates the rating given by a user to a new value
     * @param timelineID The timeline that is being rated anew
     * @param userID The user that is changing his mind on the whole rating thing
     * @param rating The new rating the user wants to give
     * @return The server's response code
     */
    public static int updateRatingSet(int timelineID, int userID, double rating) {
        int sqlCode;
        try {
            Connection conn = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);
            String updateStmnt = "UPDATE " + DBTables.RATINGS + " SET " + RatingColumns.RATING + "=? WHERE " +
                    RatingColumns.TIMELINEID + "=? AND " + RatingColumns.USERID + "=?;";
            PreparedStatement stmnt = conn.prepareStatement(updateStmnt);
            stmnt.setDouble(1, rating);
            stmnt.setInt(2, timelineID);
            stmnt.setInt(3, userID);
            sqlCode = stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            sqlCode = e.getErrorCode();
        }

        return sqlCode;
    }

}
