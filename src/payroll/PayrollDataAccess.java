package payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class PayrollDataAccess {
        public static ResultSet getEmployeeData(String employeeID) throws SQLException {
        MyConnection myConnection = MyConnection.getInstance();
        Connection connection = myConnection.connect();

        String query = "SELECT job_details.EmployeeID, Last_Name, First_Name, Position, Basic_Salary, Rice_Subsidy, Phone_Allowance,Clothing_Allowance, LogDate, LogTime, attendance.Status " +
                "FROM job_details " +
                "LEFT JOIN attendance ON job_details.EmployeeID = attendance.ID " +
                "WHERE job_details.EmployeeID = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, employeeID);

        return statement.executeQuery();
    }
        
        
}
