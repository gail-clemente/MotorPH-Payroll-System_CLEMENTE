package payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import payroll.EmployeeDetails;
import payroll.MyConnection;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;


public class JobDetailsManager {
    // Fields for encapsulation
    private int employeeID;
    private String lastName;
    private String firstName;
    private String status;
    private String position;
    private String supervisor;
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private DefaultTableModel jobDetailsTableModel;

    // Constructor for initializing job details
    public JobDetailsManager(int employeeID, String lastName, String firstName, String status, String position,
                             String supervisor, double basicSalary, double riceSubsidy, double phoneAllowance,
                             double clothingAllowance) {
        this.employeeID = employeeID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.status = status;
        this.position = position;
        this.supervisor = supervisor;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
    }
    
        public void setJobDetailsTableModel(DefaultTableModel jobDetailsTableModel) {
        this.jobDetailsTableModel = jobDetailsTableModel;
    }

// Getters and setters for encapsulation
    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    public void setRiceSubsidy(double riceSubsidy) {
        this.riceSubsidy = riceSubsidy;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    public void setPhoneAllowance(double phoneAllowance) {
        this.phoneAllowance = phoneAllowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }

    public void setClothingAllowance(double clothingAllowance) {
        this.clothingAllowance = clothingAllowance;
    }


    // Method for updating job details in the database (abstraction)
    public void updateJobDetailsInDatabase() {
        try {
            
            Connection connection = MyConnection.getInstance().connect();

            if (connection != null) {
                // Create and execute your update SQL statement
                String updateQuery = "UPDATE job_details SET Last_Name=?, First_Name=?, Status=?, Position=?, " +
                        "Immediate_Supervisor=?, Basic_Salary=?, Rice_Subsidy=?, Phone_Allowance=?, " +
                        "Clothing_Allowance=? WHERE EmployeeID=?";
                
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    // Set parameters for the update statement
                    preparedStatement.setString(1, lastName);
                    preparedStatement.setString(2, firstName);
                    preparedStatement.setString(3, status);
                    preparedStatement.setString(4, position);
                    preparedStatement.setString(5, supervisor);
                    preparedStatement.setDouble(6, basicSalary);
                    preparedStatement.setDouble(7, riceSubsidy);
                    preparedStatement.setDouble(8, phoneAllowance);
                    preparedStatement.setDouble(9, clothingAllowance);
                    preparedStatement.setInt(10, employeeID);

                    // Execute the update statement
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating job details in the database: " + e.getMessage());
        }
    }
    
 
    
}



