package payroll;

import javax.swing.JOptionPane;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;


//for checking-in and checking-out
public class AttendanceController {
    private boolean hasCheckedIn = false;
    private boolean hasCheckedOut = false;
    
        public List<Object[]> fetchAttendanceDataByDateRange(Date fromDate, Date toDate) {
        List<Object[]> attendanceData = new ArrayList<>();

        try {
            MyConnection myConnection = MyConnection.getInstance();
            Connection connection = myConnection.connect();

            if (connection != null) {
                String query = "SELECT a.AttID, a.ID, e.Last_Name, e.First_Name, a.LogDate, a.LogTime, a.Status " +
                        "FROM employee_details e " +
                        "LEFT JOIN attendance a ON e.EmployeeID = a.ID " +
                        "WHERE a.LogDate BETWEEN ? AND ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setDate(1, fromDate);
                    preparedStatement.setDate(2, toDate);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        Object[] rowData = {
                                resultSet.getInt("AttID"),
                                resultSet.getInt("ID"),
                                resultSet.getString("Last_Name"),
                                resultSet.getString("First_Name"),
                                resultSet.getDate("LogDate"),
                                resultSet.getTime("LogTime"),
                                resultSet.getString("Status")
                        };
                        attendanceData.add(rowData);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception (log it, show a message, etc.)
        }

        return attendanceData;
    }

        public void logAttendance(AdminPortal adminPortal, Employee currentEmployee, String status, JLabel lblCheckIn, JLabel lblCheckOut) {
                try {
                MyConnection myConnection = MyConnection.getInstance();
                Connection connection = myConnection.connect();

                if (connection != null) {
                    // Check if the employee has already checked in or checked out
                    if (status.equals("Logged In") && hasCheckedIn) {
                        lblCheckIn.setText("You have already logged in at " + getCurrentTime());
                        return; // Exit the method to avoid recording attendance again
                    } else if (status.equals("Logged Out") && hasCheckedOut) {
                        lblCheckOut.setText("You have already logged out at " + getCurrentTime());
                        return; // Exit the method to avoid recording attendance again
                    }

                    String insertQuery = "INSERT INTO attendance (ID, LogDate, LogTime, Status) VALUES (?, CURDATE(), CURTIME(), ?)";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                        preparedStatement.setInt(1, currentEmployee.getEmployeeID());
                        preparedStatement.setString(2, status);

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            // Retrieve the generated keys
                            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                            if (generatedKeys.next()) {
                                int attendanceID = generatedKeys.getInt(1);
                                String timestamp = getTimestampForAttendance(attendanceID, status);

                                JOptionPane.showMessageDialog(adminPortal, "Attendance recorded successfully at " + timestamp, "Success", JOptionPane.INFORMATION_MESSAGE);


                                if (status.equals("Logged In")) {
                                    hasCheckedIn = true; // Update the variable after a successful check-in
                                    lblCheckIn.setText("You have already logged in at " + timestamp);
                                } else if (status.equals("Logged Out")) {
                                    hasCheckedOut = true; // Update the variable after a successful check-out
                                    lblCheckOut.setText("You have already logged out at " + timestamp);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(adminPortal, "Failed to record attendance. No rows affected.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(adminPortal, "Error recording attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            }


        public String getTimestampForAttendance(int employeeID, String status) {
            String timestamp = null;

            try {
                MyConnection myConnection = MyConnection.getInstance();
                Connection connection = myConnection.connect();

                if (connection != null) {
                    String selectQuery = "SELECT LogDate, LogTime FROM attendance WHERE AttID = ? AND Status = ?";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                        preparedStatement.setInt(1, employeeID);
                        preparedStatement.setString(2, status);

                        ResultSet resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            // Retrieve the date and time from the result set
                            String logDate = resultSet.getString("LogDate");
                            String logTime = resultSet.getString("LogTime");

                            // Combine date and time to form the timestamp
                            timestamp = logDate + " " + logTime;
                        } else {
                            // If no records found, set a different message
                            timestamp = "No attendance records found for " + status;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the SQL exception appropriately (e.g., log it, throw a custom exception)
                timestamp = "Error retrieving timestamp: " + e.getMessage();
            }

            return timestamp;
        }

            public boolean hasCheckedIn() {
                return hasCheckedIn;
            }

            public boolean hasCheckedOut() {
                return hasCheckedOut;
            }


        private String getCurrentTime() {
            // Get the current time in the desired format
            SimpleDateFormat dateFormatTime = new SimpleDateFormat("hh:mm:ss a");
            return dateFormatTime.format(new java.util.Date());
        }
        
        public List<Object[]> fetchAttendanceDataBySearchTerm(String searchTerm) {
            List<Object[]> retrievedData = new ArrayList<>();

            try {
                MyConnection myConnection = MyConnection.getInstance();
                Connection connection = myConnection.connect();

                if (connection != null) {
                    String selectQuery = "SELECT AttID, ID, Last_Name, First_Name, LogDate, LogTime, Status " +
                    "FROM attendance a " +
                    "LEFT JOIN employee_details e ON a.ID = e.EmployeeID " +
                    "WHERE a.ID = ?";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                        preparedStatement.setString(1, searchTerm);

                        ResultSet resultSet = preparedStatement.executeQuery();

                        while (resultSet.next()) {
                            Object[] rowData = {
                                    resultSet.getInt("AttID"),
                                    resultSet.getInt("ID"),
                                    resultSet.getString("Last_Name"),
                                    resultSet.getString("First_Name"),
                                    resultSet.getDate("LogDate"),
                                    resultSet.getTime("LogTime"),
                                    resultSet.getString("Status")
                            };

                            retrievedData.add(rowData);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the SQL exception appropriately
            }

            return retrievedData;
        }
        

        public void logAttendance(NonAdminPortal nonAdminPortal, Employee currentEmployee, String status, JLabel lblCheckIn, JLabel lblCheckOut) {
                try {
                MyConnection myConnection = MyConnection.getInstance();
                Connection connection = myConnection.connect();

                if (connection != null) {
                    // Check if the employee has already checked in or checked out
                    if (status.equals("Logged In") && hasCheckedIn) {
                        lblCheckIn.setText("You have already logged in at " + getCurrentTime());
                        return; // Exit the method to avoid recording attendance again
                    } else if (status.equals("Logged Out") && hasCheckedOut) {
                        lblCheckOut.setText("You have already logged out at " + getCurrentTime());
                        return; // Exit the method to avoid recording attendance again
                    }

                    String insertQuery = "INSERT INTO attendance (ID, LogDate, LogTime, Status) VALUES (?, CURDATE(), CURTIME(), ?)";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                        preparedStatement.setInt(1, currentEmployee.getEmployeeID());
                        preparedStatement.setString(2, status);

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            // Retrieve the generated keys
                            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                            if (generatedKeys.next()) {
                                int attendanceID = generatedKeys.getInt(1);
                                String timestamp = getTimestampForAttendance(attendanceID, status);

                                JOptionPane.showMessageDialog(nonAdminPortal, "Attendance recorded successfully at " + timestamp, "Success", JOptionPane.INFORMATION_MESSAGE);


                                if (status.equals("Logged In")) {
                                    hasCheckedIn = true; // Update the variable after a successful check-in
                                    lblCheckIn.setText("You have already logged in at " + timestamp);
                                } else if (status.equals("Logged Out")) {
                                    hasCheckedOut = true; // Update the variable after a successful check-out
                                    lblCheckOut.setText("You have already logged out at " + timestamp);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(nonAdminPortal, "Failed to record attendance. No rows affected.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(nonAdminPortal, "Error recording attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            }


             
}