package payroll;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//to implement search for employee table
public class EmployeeDataController {
    private DefaultTableModel tableModel;
    DefaultTableModel jobDetailsTableModel;

    public EmployeeDataController(DefaultTableModel tableModel, DefaultTableModel jobDetailsTableModel) {
        this.tableModel = tableModel;
        this.jobDetailsTableModel = jobDetailsTableModel;
    }

    public void fetchDataFromDatabase() {//for table employee_details
        try {
        MyConnection myConnection = MyConnection.getInstance();
        Connection connection = myConnection.connect();

        if (connection != null) {   //Select the columns and its data
            String query = "SELECT EmployeeID, Last_Name, First_Name, Birthday, Address, Phone_Number, SSS, Philhealth, Pag_ibig, TIN FROM employee_details";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query);//pre-compile the query
                 ResultSet resultSet = preparedStatement.executeQuery()) { //execute the query

                // Iterate through the result set and add rows to the table model
                while (resultSet.next()) {
                    Object[] rowData = {
                            resultSet.getInt("EmployeeID"),
                            resultSet.getString("Last_Name"),
                            resultSet.getString("First_Name"),
                            resultSet.getString("Birthday"),
                            resultSet.getString("Address"),
                            resultSet.getString("Phone_Number"),
                            resultSet.getString("SSS"),
                            resultSet.getString("Philhealth"),
                            resultSet.getString("Pag_ibig"),
                            resultSet.getString("TIN"),
                            
                    };
                    tableModel.addRow(rowData);
                }
                        // Notify listeners that the table data has changed
                        tableModel.fireTableDataChanged();
            }

        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Error fetching data from database: " + e.getMessage());  //Display if the database is not connected
    }
    }

    public void filterAndDisplayData(String lastNameSearchTerm, String firstNameSearchTerm) {
    tableModel.setRowCount(0); // Clear existing data in the table

    try {
        MyConnection myConnection = MyConnection.getInstance();
        Connection connection = myConnection.connect();

        if (connection != null) {
            String query = "SELECT EmployeeID, Last_Name, First_Name, Birthday, Address, Phone_Number, SSS, Philhealth, Pag_ibig, TIN " +
                           "FROM employee_details " +
                           "WHERE Last_Name LIKE ? AND First_Name LIKE ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, "%" + lastNameSearchTerm + "%");
                preparedStatement.setString(2, "%" + firstNameSearchTerm + "%");
                
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Object[] rowData = {
                                resultSet.getInt("EmployeeID"),
                                resultSet.getString("Last_Name"),
                                resultSet.getString("First_Name"),
                                resultSet.getString("Birthday"),
                                resultSet.getString("Address"),
                                resultSet.getString("Phone_Number"),
                                resultSet.getString("SSS"),
                                resultSet.getString("Philhealth"),
                                resultSet.getString("Pag_ibig"),
                                resultSet.getString("TIN"),
                        };
                        tableModel.addRow(rowData);
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Error fetching data from database: " + e.getMessage());
    }
}

    
    public void fetchDataJobDetailsFromDatabase() {
       try {
           MyConnection myConnection = MyConnection.getInstance();
           Connection connection = myConnection.connect();

           if (connection != null) {
               String query = "SELECT jobId, EmployeeID, Last_Name, First_Name, Status, Position, Immediate_Supervisor, Basic_Salary, Rice_Subsidy, Phone_Allowance, Clothing_Allowance FROM job_details";

               try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                    ResultSet resultSet = preparedStatement.executeQuery()) {

                   // Clear existing data in the job details table
                   jobDetailsTableModel.setRowCount(0);

                   // Iterate through the result set and add rows to the job details table model
                   while (resultSet.next()) {
                       Object[] rowData = {
                               resultSet.getInt("jobId"),
                               resultSet.getInt("EmployeeID"),
                               resultSet.getString("Last_Name"),
                               resultSet.getString("First_Name"),
                               resultSet.getString("Status"),
                               resultSet.getString("Position"),
                               resultSet.getString("Immediate_Supervisor"),
                               resultSet.getDouble("Basic_Salary"),
                               resultSet.getDouble("Rice_Subsidy"),
                               resultSet.getDouble("Phone_Allowance"),
                               resultSet.getDouble("Clothing_Allowance"),
                       };
                       jobDetailsTableModel.addRow(rowData);
                   }

                   // Notify listeners that the job details table data has changed
                   jobDetailsTableModel.fireTableDataChanged();
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
           System.err.println("Error fetching job details data from database: " + e.getMessage());
       }
   }

    
}