package payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import payroll.MyConnection;

public class EmployeeDataUpdate {
    public static void updateEmployeeInDatabase(int employeeID, String lastName, String firstName, String birthday, String address,
                                                String phoneNum, String sssNum, String philhealthNum, String pagibigNum, String tinNum) {
        try {
            MyConnection myConnection = MyConnection.getInstance();
            Connection connection = myConnection.connect();

            if (connection != null) {
                String updateQuery = "UPDATE employee_details SET Last_Name = ?, First_Name = ?, Birthday = ?, Address = ?, "
                        + "Phone_Number = ?, SSS = ?, Philhealth = ?, Pag_ibig = ?, TIN = ? WHERE EmployeeID = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    preparedStatement.setString(1, lastName);
                    preparedStatement.setString(2, firstName);
                    preparedStatement.setString(3, birthday);
                    preparedStatement.setString(4, address);
                    preparedStatement.setString(5, phoneNum);
                    preparedStatement.setString(6, sssNum);
                    preparedStatement.setString(7, philhealthNum);
                    preparedStatement.setString(8, pagibigNum);
                    preparedStatement.setString(9, tinNum);
                    preparedStatement.setInt(10, employeeID);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Employee information updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update employee information", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating employee information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
