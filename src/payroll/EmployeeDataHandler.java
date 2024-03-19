
package payroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

//Code for updating the First name based on the user logged in using the UserID and EmployeeID
public class EmployeeDataHandler {

    public Employee fetchEmployeeDetails(String username) {
        int employeeID = getEmployeeIDForUser(username);
        // Fetch other details based on employeeID from your database
        // Replace with actual logic to fetch from the database
        String lastName = null;
        String firstName = null;
        Date birthday = null;
        String address = null;
        String phoneNum = null;
        String sssNum = null;
        String philhealthNum = null;
        String pagibigNum = null;
        String tinNum = null;

                // Execute SQL query to fetch employee details from the database
            try {
                MyConnection myConnection = MyConnection.getInstance();
                Connection connection = myConnection.connect();

                if (connection != null) {
                    String selectQuery = "SELECT * FROM employee_details WHERE EmployeeID = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                        preparedStatement.setInt(1, employeeID);
                        ResultSet resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            // Retrieve employee details from the result set
                            lastName = resultSet.getString("Last_Name");
                            firstName = resultSet.getString("First_Name");
                            // Retrieve other details similarly
                        }
                    }
                }
            } catch (SQLException ex) {
                // Handle SQL exception
                ex.printStackTrace(); // Print stack trace for debugging
            }

        // Return an Employee instance
        return new Employee(employeeID, lastName, firstName, birthday, address, phoneNum, sssNum, philhealthNum, pagibigNum, tinNum);
    }


    public String getFirstNameForUser(String username) {
    try {
        MyConnection myConnection = MyConnection.getInstance();
        Connection connection = myConnection.connect();

        if (connection != null) {
            String selectQuery = "SELECT ed.First_Name FROM login l " +
                    "JOIN employee_details ed ON l.EmployeeID = ed.EmployeeID " +
                    "WHERE l.username = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, username);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("First_Name");
                } else {
                    // Handle the case where the username is not found
                    return "User not found";
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle the SQL exception
        return "Error retrieving first name";
    }

    return ""; // Default value or handle error appropriately
}
    
    

    
    private void updateFirstNameFromDatabase(String username, JLabel lblEmployeeName) {
    try {
        MyConnection myConnection = MyConnection.getInstance();
        Connection connection = myConnection.connect();

        if (connection != null) {
            String selectQuery = "SELECT ed.First_Name FROM login l " +
                    "JOIN employee_details ed ON l.EmployeeID = ed.EmployeeID " +
                    "WHERE l.username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, username);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    lblEmployeeName.setText(resultSet.getString("First_Name") + "!");
                } else {
                    JOptionPane.showMessageDialog(null, "Employee not found.");
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error retrieving employee details: " + ex.getMessage());
    }
}
    
   
        

        private void updateEmployeeNameFromDatabase(String username, JLabel lblEmpid, JLabel lblLname, JLabel lblFname) {
    try {
        MyConnection myConnection = MyConnection.getInstance();
        Connection connection = myConnection.connect();

        if (connection != null) {
            String selectQuery = "SELECT ed. EmployeeID, ed. Last_Name, ed.First_Name FROM login l " +
                    "JOIN employee_details ed ON l.EmployeeID = ed.EmployeeID " +
                    "WHERE l.username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, username);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    lblEmpid.setText(String.valueOf("EmployeeID"));
                    lblLname.setText(resultSet.getString("Last_Name"));
                    lblFname.setText(resultSet.getString("First_Name"));
                } else {
                    JOptionPane.showMessageDialog(null, "Employee not found.");
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error retrieving employee details: " + ex.getMessage());
    }
}

    private int getEmployeeIDForUser(String username) {
    try {
        MyConnection myConnection = MyConnection.getInstance();
        Connection connection = myConnection.connect();

        if (connection != null) {
            String selectQuery = "SELECT EmployeeID FROM login WHERE username = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, username);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt("EmployeeID");
                } else {
                    // Handle the case where the user ID is not found
                    return -1; // or throw an exception
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle the SQL exception
    }

    return -1; // Default value or handle error appropriately
}

    
    
    public PayrollReport fetchPayrollReportDetails(int employeeID) {
    PayrollReport payrollReport = null;

    try {
        MyConnection myConnection = MyConnection.getInstance();
        Connection connection = myConnection.connect();

        if (connection != null) {
            String selectQuery = "SELECT `Position`, `Basic Salary`, `Rice Subsidy`, `Phone Allowance`, `Clothing Allowance`, " +
                    "`Total Allowances`, `Philhealth`, `SSS`, `Pagibig`, `Total Deductions`, `Taxable Income`, " +
                    "`Withholding Tax`, `Net Pay`, `Date`, `Month`, `Salary Period`, `Year`, " +
                    "`Days Worked`, `Gross Income` FROM `payroll_report` WHERE `employeeID` = ? ORDER BY `Date` DESC LIMIT 1";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, employeeID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String position = resultSet.getString("Position");
                        double basicSalary = parseDouble(resultSet.getString("Basic Salary"));
                        Date date = resultSet.getDate("Date");
                        String month = resultSet.getString("Month");
                        String salaryPeriod = resultSet.getString("Salary Period");
                        int year = resultSet.getInt("Year");
                        int daysWorked = resultSet.getInt("Days Worked");
                        double grossIncome = parseDouble(resultSet.getString("Gross Income"));
                        double riceSubsidy = parseDouble(resultSet.getString("Rice Subsidy"));
                        double phoneAllowance = parseDouble(resultSet.getString("Phone Allowance"));
                        double clothingAllowance = parseDouble(resultSet.getString("Clothing Allowance"));
                        double totalAllowances = parseDouble(resultSet.getString("Total Allowances"));
                        double philhealth = parseDouble(resultSet.getString("Philhealth"));
                        double sss = parseDouble(resultSet.getString("SSS"));
                        double pagibig = parseDouble(resultSet.getString("Pagibig"));
                        double totalDeductions = parseDouble(resultSet.getString("Total Deductions"));
                        double taxableIncome = parseDouble(resultSet.getString("Taxable Income"));
                        double withholdingTax = parseDouble(resultSet.getString("Withholding Tax"));
                        double netPay = parseDouble(resultSet.getString("Net Pay"));
                        


                        payrollReport = new PayrollReport(position, basicSalary, date, month, salaryPeriod, year,
                                daysWorked, grossIncome, riceSubsidy, phoneAllowance, clothingAllowance, totalAllowances, philhealth,
                                sss, pagibig, totalDeductions, taxableIncome, withholdingTax, netPay);
                    }
                }
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        // Handle SQL exception as needed
    }

    return payrollReport;
}




// Helper method to parse double from string with comma as decimal separator
private double parseDouble(String valueStr) {
    double value = 0.0; // Default value in case parsing fails
    if (valueStr != null) {
        // Replace commas with empty strings and replace the last period with an empty string
        valueStr = valueStr.replaceAll(",", "").replaceFirst("\\.(?!.*\\.)$", "");
        try {
            value = Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            // Log or handle the exception
            e.printStackTrace();
            // Set default value or take other corrective actions
        }
    }
    return value;
}

    
}