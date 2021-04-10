package sql;

import com.timeline.User;
import org.apache.commons.codec.digest.DigestUtils;
import sql.table_enums.DBTables;
import sql.table_enums.UserColumns;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class interfaces with a remote database that handles TimeWaves user data.
 * Requires User-class.
 *
 * @author Elias Frigård ef222xf
 */
public class UserMethods {
    private static String DBConnection = "jdbc:mysql://134.209.81.97:3306/timewaves";
    private static String DBUsername = "test";
    private static String DBPassword = "Test1234#";


    /**
     * This method adds a user to the database. Username and Email must be unique.
     *
     * @param user Object to be added to the database.
     */
    public static void addUser (User user) throws SQLException {
        // Make connection to the database.
        Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

        // Create a prepared statement for inserting user.
        PreparedStatement st = con.prepareStatement(
                "INSERT INTO " + DBTables.USERS + " (" + UserColumns.USERNAME + ", " + UserColumns.PASSWORD + ", " + UserColumns.EMAIL + ") VALUES (?, ?, ?)");

        // Add parameters to query.
        st.setString(1, user.getUsername());
        st.setString(2, user.getPassword());
        st.setString(3, user.getEmail());

        // Execute query and close connection.
        st.execute();
        con.close();
    }

    /**
     * Authenticates user by name. Looks for name and corresponding password in database.
     *
     * @param user User to authenticate.
     * @return Returns true if user exists and password is a match.
     */
    public static boolean authenticateUser (User user) {
        // Place username in variable if found, else return false if username == null.
        String userMatch = null;

        try {
            // Make connection to the database.
            Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

            // Find the user by email and matching password.
            PreparedStatement st = con.prepareStatement(
                    "SELECT DISTINCT " + UserColumns.USERNAME + " FROM " + DBTables.USERS + " WHERE " + UserColumns.USERNAME + " = ? AND " + UserColumns.PASSWORD + " = ?");

            // Add parameters to statement.
            st.setString(1, user.getUsername());
            st.setString(2, user.getPassword());

            // Execute query and close connection.
            ResultSet rs = st.executeQuery();

            // Place the distinct (unique) match in the username variable.
            while (rs.next()) userMatch = rs.getString(1);

            // Close connection.
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userMatch != null;
    }

    /**
     * Returns the username if the email passed in exists in the database.
     * @param email the email to search for
     * @return username (String) - value "null" if not found
     */
    public static String verifyEmail (String email) {
        // If no match, set the string to null
        String username = null;

        try {
            // Make connection to the database.
            Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

            // Find the user in the database by name and compare password for authentication, then update the password.
            PreparedStatement st = con.prepareStatement("SELECT DISTINCT " + UserColumns.USERNAME + " FROM " + DBTables.USERS + " WHERE " + UserColumns.EMAIL + " = ?");

            // Add parameters to statement. Check both name and email for same String.
            st.setString(1, email);

            // Execute query and close connection.
            ResultSet rs = st.executeQuery();

            while (rs.next()) username = rs.getString( UserColumns.USERNAME.toString() );

            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Return found email or null.
        return username;
    }


    /**
     * Deletes a user from the database if username or email and password match.
     * @param user Object to delete
     */
    public static void deleteUser (User user) {
        try {
            // Make connection to the database.
            Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

            // Create a prepared statement for deleting user.
            PreparedStatement st = con.prepareStatement("DELETE FROM " + DBTables.USERS + " WHERE " + UserColumns.USERNAME + " = ? AND " + UserColumns.PASSWORD + " = ?");

            // Add parameters to statement.
            st.setString(1, user.getUsername());
            st.setString(2, user.getPassword());

            // Execute query and close connection.
            st.execute();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds user by name, compares passwords for authentication and updates the old password.
     * @param user User for whom to change password.
     * @param newPassword New Password.
     */
    public static void changePassword (User user, String newPassword) {
        try {
            // Make connection to the database.
            Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

            // Find the user in the database by name and compare password for authentication, then update the password.
            PreparedStatement st = con.prepareStatement("UPDATE " + DBTables.USERS + " SET " + UserColumns.PASSWORD + " = ? WHERE " +
                    UserColumns.USERNAME + " = ? AND " + UserColumns.PASSWORD + " = ?");

            // Hash new password using SHA-256 algorithm for comparison and security.
            newPassword = DigestUtils.sha256Hex(newPassword);

            // Add parameters to statement.
            st.setString(1, newPassword);
            st.setString(2, user.getUsername());
            st.setString(3, user.getPassword());

            user.setPassword(newPassword);

            // Execute query and close connection.
            st.execute();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is only used for account recovery - if the users password is to change normally, use the "changePassword"
     * method.
     * Finds user by name, compares email for authentication and updates the old password.
     * @param user User for whom to change password.
     * @param newPassword new, temporary password.
     */
    public static void passwordRecovery(User user, String newPassword) {
        try {
            // Make connection to the database.
            Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

            // Find the user in the database by name and compare password for authentication, then update the password.
            PreparedStatement st = con.prepareStatement("UPDATE " + DBTables.USERS + " SET " + UserColumns.PASSWORD + " = ? WHERE " +
                    UserColumns.USERNAME + " = ? AND " + UserColumns.EMAIL + " = ?");

            // Hash new password using SHA-256 algorithm for comparison and security.
            newPassword = DigestUtils.sha256Hex(newPassword);

            // Add parameters to statement.
            st.setString(1, newPassword);
            st.setString(2, user.getUsername());
            st.setString(3, user.getEmail());

            user.setPassword(newPassword);

            // Execute query and close connection.
            st.execute();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of all the users in the database.
     * @return ArrayList of User Objects.
     * @author Elias Frigård ef222xf
     */
    public static ArrayList<User> getUserList () {
        ArrayList<User> users = new ArrayList<>();

        try {
            // Make connection to the database.
            Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

            // Select all users in the database.
            PreparedStatement st = con.prepareStatement("SELECT * FROM " + DBTables.USERS);

            // Execute query and close connection.
            ResultSet rs = st.executeQuery();

            // Place the distinct (unique) result in the email variable.
            while (rs.next()) {
                String name = rs.getString( UserColumns.USERNAME.toString() );
                String password = rs.getString( UserColumns.PASSWORD.toString() );
                String email = rs.getString( UserColumns.EMAIL.toString() );
                boolean isAdmin = rs.getBoolean( UserColumns.ISADMIN.toString() );
                boolean isInRecovery = rs.getBoolean( UserColumns.INRECOVERY.toString() );
                int userID = rs.getInt( UserColumns.USERID.toString() );

                users.add(new User(name, password, email, isAdmin, isInRecovery, userID));
            }

            // Close connection.
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Toggles the admin status of a user both in the database and on the object instance.
     * @param user User to toggle.
     * @author Elias Frigård ef222xf
     */
    public static void toggleAdminStatus (User user) {
        try {
            // Make connection to the database.
            Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

            // Select all users in the database.
            PreparedStatement st = con.prepareStatement("UPDATE " + DBTables.USERS + " SET " + UserColumns.ISADMIN + " = ? WHERE " + UserColumns.USERID + " = ?");

            // Check whether user is admin or not and reverse that status.
            if (user.isAdmin()) {
                st.setBoolean(1, false);
            } else {
                st.setBoolean(1, true);
            }

            // Where this user ID is found.
            st.setInt(2, user.getUserID());

            // Toggle boolean in the User object.
            user.setAdmin(!user.isAdmin());

            // Execute query and close connection.
            st.execute();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set status of the user to "in recovery" in the database and on the object instance.
     * @param user User to set.
     * @author Elias Frigård ef222xf
     */
    public static void setInRecovery(User user, boolean isInRecovery) {
        try {
            // Make connection to the database.
            Connection con = DriverManager.getConnection(DBConnection, DBUsername, DBPassword);

            // Find the user in the database by name and compare password for authentication, then update the password.
            PreparedStatement st = con.prepareStatement("UPDATE " + DBTables.USERS + " SET " + UserColumns.INRECOVERY + " = ? WHERE " +
                    UserColumns.USERNAME + " = ? AND " + UserColumns.EMAIL + " = ?");

            // Add parameters to statement.
            st.setBoolean(1, isInRecovery);
            st.setString(2, user.getUsername());
            st.setString(3, user.getEmail());

            // set boolean in the User object.
            user.setInRecovery(true);

            // Execute query and close connection.
            st.execute();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
