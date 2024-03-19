package payroll;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import payroll.EmployeeDetails;
import payroll.MyConnection;


//For Employee Details
public class Employee implements EmployeeDetails {
    //Encapsulation
    private int employeeID;
    private final String lastName;
    private final String firstName;
    private final Date birthday;
    private final String address;
    private final String phoneNum;
    private final String sssNum;
    private final String philhealthNum;
    private final String pagibigNum;
    private final String tinNum;

    
public static int getEmployeeIDForUser(String username) {
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





    // Parameterized constructor
    public Employee(int employeeID, String lastName, String firstName, Date birthday, String address,
                    String phoneNum, String sssNum, String philhealthNum, String pagibigNum, String tinNum) {
        this.employeeID = employeeID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.address = address;
        this.phoneNum = phoneNum;
        this.sssNum = sssNum;
        this.philhealthNum = philhealthNum;
        this.pagibigNum = pagibigNum;
        this.tinNum = tinNum;
    }

    // Default constructor
    public Employee() {
        // Initialize fields with default values or leave them uninitialized based on your requirements
        this.employeeID = 0;
        this.lastName = "";
        this.firstName = "";
        this.birthday = null;
        this.address = "";
        this.phoneNum = "";
        this.sssNum = "";
        this.philhealthNum = "";
        this.pagibigNum = "";
        this.tinNum = "";
    }


    // Constructors
    public Employee( String lastName, String firstName, Date birthday, String address, String phoneNum, String sssNum, String philhealthNum, String pagibigNum, String tinNum) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.address = address;
        this.phoneNum = phoneNum;
        this.sssNum = sssNum;
        this.philhealthNum = philhealthNum;
        this.pagibigNum = pagibigNum;
        this.tinNum = tinNum;
    }

    // Getters and setters
    // from the EmployeeDetails interface

    public int getEmployeeID() {
        return employeeID;
    }
    
    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public Date getBirthday() {
        return birthday;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getPhoneNum() {
        return phoneNum;
    }

    @Override
    public String getSssNum() {
        return sssNum;
    }

    @Override
    public String getPhilhealthNum() {
        return philhealthNum;
    }

    @Override
    public String getPagibigNum() {
        return pagibigNum;
    }

    @Override
    public String getTinNum() {
        return tinNum;
    }
    
    // Method to parse birthday string to Date
    private Date parseBirthday(String birthdayString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(birthdayString);
        } catch (ParseException e) {
            // Handle the parsing exception, e.g., log or throw an IllegalArgumentException
            e.printStackTrace();
            return null;
        }
    }
}

