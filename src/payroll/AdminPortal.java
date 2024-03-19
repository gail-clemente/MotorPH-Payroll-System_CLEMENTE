package payroll;

import java.sql.Connection;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;





public class AdminPortal extends javax.swing.JFrame {
    private String untxt;
    private DefaultTableModel tableModel;//for the table tblEmployeeInformation
    private DefaultTableModel jobDetailsTableModel;
    private EmployeeDataHandler dataHandler;//to update name
    private Employee currentEmployee;
    private boolean attendanceRecorded = false;    
    private boolean hasCheckedIn = false;
    private boolean hasCheckedOut = false;
    private EmployeeDataController dataController;
    private AttendanceController attendanceController;
    private JobDetailsManager jobDetailsManager;
    private TableRowSorter<DefaultTableModel> jobDetailsRowSorter;
    private DefaultTableModel payrollTableModel;
    private Date startDate = null; // Define and initialize startDate as a class member
    private Date endDate = null;
    

    

    public AdminPortal() {
        initComponents();
    }
    

    public AdminPortal(String untxt) throws SQLException {
        this.untxt = untxt;
        initComponents();
        loadTable_Att();
        
        dataHandler = new EmployeeDataHandler();
        currentEmployee = dataHandler.fetchEmployeeDetails(untxt);
        updateFirstNameFromDatabase(untxt);
        
        Timer timer = new Timer(); // For Current Time
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCurrentDateTime();
            }
        }, 0, 1000); // 1000 milliseconds = 1 second

        //for table employee_details, tblEmployeeinformation
        String[] columnNames = {"EmployeeID", "Last_Name", "First_Name", "Birthday", "Address", "Phone_Number", "SSS", "Philhealth", "Pag_ibig", "TIN"};
        tableModel = new DefaultTableModel(columnNames, 0); // for Employee Information table - {columns name from MySQL table}
        // Set the table model to the existing tblEmployeeInformation
        tblEmployeeInformation.setModel(tableModel);
        
        //for table job_details, tblJobDetails
        String[] jobDetailsColumnNames = {"jobId", "EmployeeID", "Last_Name", "First_Name", "Status", "Position", "Immediate_Supervisor", "Basic_Salary", "Rice_Subsidy", "Phone_Allowance", "Clothing_Allowance"};
        jobDetailsTableModel = new DefaultTableModel(jobDetailsColumnNames, 0);
        tblJobDetails.setModel(jobDetailsTableModel);

              
        // Initialize EmployeeDataController
        dataController = new EmployeeDataController(tableModel, jobDetailsTableModel);
        // Fetch data from MySQL and populate the tables
        dataController.fetchDataFromDatabase();
        dataController.fetchDataJobDetailsFromDatabase(); // Call the job details method
        

        payrollTableModel = new DefaultTableModel();
        tblJobPayroll.setModel(payrollTableModel);
        
        // Add columns to the model
        payrollTableModel.addColumn("EmployeeID");
        payrollTableModel.addColumn("Last_Name");
        payrollTableModel.addColumn("First_Name");
        payrollTableModel.addColumn("Position");
        payrollTableModel.addColumn("Basic_Salary");
        payrollTableModel.addColumn("Rice_Subsidy");
        payrollTableModel.addColumn("Phone_Allowance");
        payrollTableModel.addColumn("Clothing_Allowance");
        payrollTableModel.addColumn("LogDate");
        payrollTableModel.addColumn("LogTime");
        payrollTableModel.addColumn("Status");
    
        setTitle("Admin Portal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        // Add a DocumentListener to txtSearch for Employee details
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchAndUpdateTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchAndUpdateTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Plain text components don't fire these events
            }
        });
        
        
            //for search, job details
            txtIDSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterJobDetailsTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterJobDetailsTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Plain text components do not fire these events
            }
        });
            
        attendanceController = new AttendanceController();
        btnCheckIn.addActionListener(this::btnCheckInActionPerformed);// for Check In Button in Admin Dashboard Tab
        btnCheckOut.addActionListener(this::btnCheckOutActionPerformed);// for Check Out Button in Admin Dashboard Tab
        btnClear.addActionListener(this::btnClearActionPerformed);// for Clear Button in Employee Details Tab


        // Display the frame
        setVisible(true);


        com.toedter.calendar.JDateChooser dateChooser = new com.toedter.calendar.JDateChooser();

        
    }
    
    public void loadTable_Att() throws SQLException {//Timesheet, tblAttend
        Bal_Attendance bal = new Bal_Attendance();
        List<Bean_Attendance> list = bal.loadData_Att();
        loadAllDataIntoTable_Att(list);
    }
      
      
    public void search(String str, Date d1, Date d2) throws SQLException {//Timesheet, search Employee ID
    startDate = d1;
    endDate = d2;
    Bal_Attendance bal = new Bal_Attendance();
    List<Bean_Attendance> list = bal.searchID(str, d1, d2);
    
    // Calculate total hours worked
    String totalHoursWorked = bal.calculateTotalHoursWorked(list);
    
    // Calculate overall undertime
    String undertime = bal.calculateOverallUndertime(list);
    String overtime = bal.calculateOverallOvertime(list);
    
    // Calculate number of unique dates
    int numberOfUniqueDates = getNumberOfUniqueDates(list);
    
    // Update labels
    displayNumberOfDaysAndTotalHours(numberOfUniqueDates, totalHoursWorked);
    
    // Calculate undertime based on office hours and lunch break
    int totalMinutesWorked = bal.convertTotalHoursToMinutes(totalHoursWorked);
    int officeHours = 8 * 60; // Convert 8 hours to minutes
    int lunchBreak = 60; // Lunch break in minutes
    
    // Check if total minutes worked is less than office hours
    if (totalMinutesWorked < officeHours) {
        int undertimeMinutes = officeHours - totalMinutesWorked - lunchBreak;
        int undertimeHours = undertimeMinutes / 60;
        undertime = String.valueOf(undertimeHours);
    } else {
        undertime = "0"; // No undertime if worked more than or equal to office hours
    }
    
    // Set overall undertime and overtime on respective labels
    lblUndertime.setText(undertime);
    lblOvertime.setText(overtime);
    
    // Load filtered data into table
    loadAllDataIntoTable_Att(list);
}
  
public int getNumberOfUniqueDates(List<Bean_Attendance> list) {//to avoid duplication counts
    List<Date> uniqueDates = new ArrayList<>();
    for (Bean_Attendance bean : list) {
        if (!uniqueDates.contains(bean.getLogdate())) {
            uniqueDates.add(bean.getLogdate());
        }
    }
    return uniqueDates.size();
}    

public void displayNumberOfDaysAndTotalHours(int numberOfDays, String totalHoursWorked) {//display details using the labels
    lblNumDays.setText(String.valueOf(numberOfDays));
    lblHoursWork.setText(totalHoursWorked); // Display the string directly
    Bal_Attendance bal = new Bal_Attendance();
    int totalMinutesWorked = bal.convertTotalHoursToMinutes(totalHoursWorked);
    lblMinutes.setText(String.valueOf(totalMinutesWorked));
}


    public void loadAllDataIntoTable_Att(List<Bean_Attendance> list) {
        DefaultTableModel dtm = (DefaultTableModel) tblAttend.getModel();
        dtm.setRowCount(0);

        for (Bean_Attendance bean : list) {
            Vector v = new Vector();
            v.add(bean.getId());
            v.add(bean.getLogdate());
            v.add(bean.getLogtime());
            v.add(bean.getStatus());

            dtm.addRow(v);
        }
    }
    
    
      
      
    
    // method to filter the job details table
        private void filterJobDetailsTable() {
            String searchText = txtIDSearch.getText();
            if (jobDetailsRowSorter == null) {
                jobDetailsRowSorter = new TableRowSorter<>(jobDetailsTableModel);
                tblJobDetails.setRowSorter(jobDetailsRowSorter);
            }

            if (searchText.trim().length() == 0) {
                jobDetailsRowSorter.setRowFilter(null);
            } else {
                // Case-insensitive filter for each column
                jobDetailsRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            }
        }
    
    private void updateFirstNameFromDatabase(String username) {
        String firstName = dataHandler.getFirstNameForUser(username);
        lblEmployeeName.setText(firstName + "!");
    }
    
    

    private void searchAndUpdateTable() {
        String searchTerm = txtSearch.getText().trim().toLowerCase();
        dataController.filterAndDisplayData(searchTerm);
    }
    

    public void saveEmployeeToDatabase(Employee employee) {
        try {
            MyConnection myConnection = MyConnection.getInstance();
            Connection connection = myConnection.connect();

            if (connection != null) {
                String insertQuery = "INSERT INTO employee_details (Last_Name, First_Name, Birthday, Address, Phone_Number, SSS, Philhealth, TIN, Pag_ibig) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, employee.getLastName());
                    preparedStatement.setString(2, employee.getFirstName());
                    java.sql.Date sqlDate = new java.sql.Date(employee.getBirthday().getTime());
                    preparedStatement.setDate(3, sqlDate);
                    preparedStatement.setString(4, employee.getAddress());
                    preparedStatement.setString(5, employee.getPhoneNum());
                    preparedStatement.setString(6, employee.getSssNum());
                    preparedStatement.setString(7, employee.getPhilhealthNum());
                    preparedStatement.setString(8, employee.getTinNum());
                    preparedStatement.setString(9, employee.getPagibigNum());

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Data inserted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to insert data. No rows affected.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving data to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    



    private void updateCurrentDateTime() {
        // Get the current date and time in 12-hour format
        SimpleDateFormat dateFormatDate = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat dateFormatTime = new SimpleDateFormat("hh:mm:ss a");

        String currentDate = dateFormatDate.format(new Date());//current date
        String currentTime = dateFormatTime.format(new Date());

        // Update the labels
        lblCurrentDate.setText(currentDate);
        lblCurrentTime.setText(currentTime);
    }
    
    private void handleButtonClick(java.awt.event.ActionEvent evt, String panelName, JButton clickedButton) {
        // Switch to the specified panel when a button is clicked
        CardLayout cardLayout = (CardLayout) centerPanel.getLayout();
        cardLayout.show(centerPanel, panelName);

        // Set the background color of the clicked button to a different color
        clickedButton.setBackground(Color.GRAY);

        // Reset the background color of other buttons
        resetButtonColors(clickedButton);
}

    private void resetButtonColors(JButton clickedButton) {
        // Reset the background color of other buttons
        JButton[] buttons = {btnEmpDash, btnEmpDetails, btnEmpTimesheet, btnPayroll};
        for (JButton button : buttons) {
            if (button != clickedButton) {
                button.setBackground(null);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        btnLogout = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        btnEmpDash = new javax.swing.JButton();
        btnEmpDetails = new javax.swing.JButton();
        btnEmpTimesheet = new javax.swing.JButton();
        btnPayroll = new javax.swing.JButton();
        centerPanel = new javax.swing.JPanel();
        p1Dashboard = new javax.swing.JPanel();
        pnlProfile = new javax.swing.JPanel();
        photopanel = new javax.swing.JDesktopPane();
        img = new javax.swing.JLabel();
        btnAttachPic = new javax.swing.JButton();
        lblGreetings = new javax.swing.JLabel();
        lblEmployeeRole = new javax.swing.JLabel();
        lblEmployeeName = new javax.swing.JLabel();
        btnPayslip = new javax.swing.JButton();
        pnlAttendance = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        lblCurrentTime = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblCurrentDate = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        lblCheckIn = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lblCheckOut = new javax.swing.JLabel();
        btnCheckOut = new javax.swing.JButton();
        btnCheckIn = new javax.swing.JButton();
        p2EmpDet = new javax.swing.JPanel();
        pnlbutton = new javax.swing.JPanel();
        btnJobDetails = new javax.swing.JButton();
        btnEmployeeInformation = new javax.swing.JButton();
        btnBonusesdeductions = new javax.swing.JButton();
        pnlCenter = new javax.swing.JPanel();
        p1EmpInformation = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEmployeeInformation = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JScrollPane();
        txtAdd = new javax.swing.JTextArea();
        txtLastName = new javax.swing.JTextField();
        txtFirstName = new javax.swing.JTextField();
        txtPNumber = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtTinNum = new javax.swing.JTextField();
        txtSSSNum = new javax.swing.JTextField();
        txtPhilhealthNum = new javax.swing.JTextField();
        txtPagibigNum = new javax.swing.JTextField();
        btnAddEmployee = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        dateBirthday = new com.toedter.calendar.JDateChooser();
        p2JobDetails = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtIDSearch = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtEmpIDforJob = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtLNameJob = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtFNameJob = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txtStatusJob = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtBasicSalary = new javax.swing.JTextField();
        btnUpdateJob = new javax.swing.JButton();
        btnClearJob = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        txtPositionJob = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txtSupeJob = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txtRiceJob = new javax.swing.JTextField();
        txtPhoneJob = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txtClothJob = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblJobDetails = new javax.swing.JTable();
        btnRefreshJob = new javax.swing.JButton();
        p3BonusesDeductions = new javax.swing.JPanel();
        p3EmpTime = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        txtIDSearchAttend = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        dateFromAttend = new com.toedter.calendar.JDateChooser();
        dateToAttend = new com.toedter.calendar.JDateChooser();
        jLabel35 = new javax.swing.JLabel();
        btnClearAtt = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        lblHoursWork = new javax.swing.JLabel();
        lblUndertime = new javax.swing.JLabel();
        lblOvertime = new javax.swing.JLabel();
        lblNumDays = new javax.swing.JLabel();
        lblMinutes = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblAttend = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        p4Payroll = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        txtEmpIDpayroll = new javax.swing.JTextField();
        txtLNamePayroll = new javax.swing.JTextField();
        txtFNamePayroll = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        txtPositionPayroll = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        txtSearchPayroll = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        txtBSalary = new javax.swing.JTextField();
        txtWorkingDays = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        txtClear1Payroll = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblJobPayroll = new javax.swing.JTable();
        btnCalculate = new javax.swing.JButton();
        jLabel56 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        cmbSalaryPeriod = new javax.swing.JComboBox<>();
        cmbMonth = new javax.swing.JComboBox<>();
        btnRetrieve = new javax.swing.JButton();
        cmbYear = new javax.swing.JComboBox<>();
        jLabel71 = new javax.swing.JLabel();
        txtMinutes = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        lblSalary = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel57 = new javax.swing.JLabel();
        txtRiceSub = new javax.swing.JTextField();
        txtPhoneAll = new javax.swing.JTextField();
        txtClothAll = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        btnSavePayroll = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        lbltAllowances = new javax.swing.JLabel();
        lblSSS = new javax.swing.JLabel();
        lblPhilhealth = new javax.swing.JLabel();
        lblTDeductions = new javax.swing.JLabel();
        lblTaxableIncome = new javax.swing.JLabel();
        lblPagibig = new javax.swing.JLabel();
        lblWithholdingTax = new javax.swing.JLabel();
        lblNetpay = new javax.swing.JLabel();
        lblGross = new javax.swing.JLabel();
        lblGrossSalary = new javax.swing.JLabel();
        lblGross1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ADMIN");
        setMaximizedBounds(new java.awt.Rectangle(0, 0, 720, 1080));
        setMaximumSize(new java.awt.Dimension(1080, 720));
        setMinimumSize(new java.awt.Dimension(1080, 720));
        setPreferredSize(new java.awt.Dimension(1080, 720));
        setResizable(false);
        setSize(new java.awt.Dimension(1080, 720));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mainPanel.setForeground(new java.awt.Color(204, 204, 204));
        mainPanel.setMaximumSize(new java.awt.Dimension(1080, 720));
        mainPanel.setMinimumSize(new java.awt.Dimension(1080, 720));
        mainPanel.setPreferredSize(new java.awt.Dimension(1080, 720));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        topPanel.setBackground(new java.awt.Color(102, 102, 102));
        topPanel.setMaximumSize(new java.awt.Dimension(1080, 110));
        topPanel.setMinimumSize(new java.awt.Dimension(1080, 110));
        topPanel.setPreferredSize(new java.awt.Dimension(1080, 110));
        topPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnLogout.setBackground(new java.awt.Color(153, 0, 0));
        btnLogout.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("LOGOUT");
        btnLogout.setAlignmentY(0.0F);
        btnLogout.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLogout.setInheritsPopupMenu(true);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        topPanel.add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 20, 90, 40));

        jLabel2.setFont(new java.awt.Font("Bahnschrift", 0, 50)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("MOTORPH");
        topPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 230, 80));

        jLabel3.setFont(new java.awt.Font("Bahnschrift", 0, 50)); // NOI18N
        jLabel3.setText("MOTORPH");
        topPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 230, 70));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pictures/motor.png"))); // NOI18N
        topPanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 0, 310, 110));

        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pictures/motor - Copy.png"))); // NOI18N
        topPanel.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 0, 190, 110));

        mainPanel.add(topPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        buttonPanel.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.borderColor"));
        buttonPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        buttonPanel.setMaximumSize(new java.awt.Dimension(1080, 50));

        btnEmpDash.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        btnEmpDash.setText("Dashboard");
        btnEmpDash.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnEmpDash.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEmpDash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmpDashActionPerformed(evt);
            }
        });

        btnEmpDetails.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        btnEmpDetails.setText("Employee Details");
        btnEmpDetails.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnEmpDetails.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEmpDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmpDetailsActionPerformed(evt);
            }
        });

        btnEmpTimesheet.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        btnEmpTimesheet.setText("Employee Timesheet");
        btnEmpTimesheet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnEmpTimesheet.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEmpTimesheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmpTimesheetActionPerformed(evt);
            }
        });

        btnPayroll.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        btnPayroll.setText("Payroll");
        btnPayroll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnPayroll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayrollActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addComponent(btnEmpDash, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnEmpDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnEmpTimesheet, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnEmpDash, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnEmpDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnEmpTimesheet, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        mainPanel.add(buttonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, -1, -1));

        centerPanel.setBackground(new java.awt.Color(255, 255, 255));
        centerPanel.setAlignmentX(0.0F);
        centerPanel.setAlignmentY(0.0F);
        centerPanel.setMaximumSize(new java.awt.Dimension(1076, 560));
        centerPanel.setMinimumSize(new java.awt.Dimension(1076, 552));
        centerPanel.setPreferredSize(new java.awt.Dimension(1076, 552));
        centerPanel.setLayout(new java.awt.CardLayout());

        p1Dashboard.setBackground(new java.awt.Color(153, 153, 153));
        p1Dashboard.setAlignmentX(0.0F);
        p1Dashboard.setAlignmentY(0.0F);
        p1Dashboard.setMaximumSize(new java.awt.Dimension(1070, 550));
        p1Dashboard.setMinimumSize(new java.awt.Dimension(1070, 550));
        p1Dashboard.setPreferredSize(new java.awt.Dimension(1070, 550));

        pnlProfile.setBackground(new java.awt.Color(204, 204, 204));
        pnlProfile.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        photopanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        img.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        img.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        img.setText("Photo Here");
        img.setMaximumSize(new java.awt.Dimension(196, 196));
        img.setMinimumSize(new java.awt.Dimension(196, 196));
        img.setPreferredSize(new java.awt.Dimension(196, 196));

        photopanel.setLayer(img, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout photopanelLayout = new javax.swing.GroupLayout(photopanel);
        photopanel.setLayout(photopanelLayout);
        photopanelLayout.setHorizontalGroup(
            photopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(photopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(img, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        photopanelLayout.setVerticalGroup(
            photopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(photopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(img, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnAttachPic.setFont(new java.awt.Font("Bahnschrift", 0, 10)); // NOI18N
        btnAttachPic.setText("ATTACH PICTURE");
        btnAttachPic.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnAttachPic.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAttachPic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttachPicActionPerformed(evt);
            }
        });

        lblGreetings.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        lblGreetings.setText("Welcome,");

        lblEmployeeRole.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        lblEmployeeRole.setText("ADMIN");

        lblEmployeeName.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        lblEmployeeName.setText("Name");

        btnPayslip.setFont(new java.awt.Font("Bahnschrift", 1, 13)); // NOI18N
        btnPayslip.setText("SEE PAYSLIP");
        btnPayslip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayslipActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlProfileLayout = new javax.swing.GroupLayout(pnlProfile);
        pnlProfile.setLayout(pnlProfileLayout);
        pnlProfileLayout.setHorizontalGroup(
            pnlProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProfileLayout.createSequentialGroup()
                .addGroup(pnlProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlProfileLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(pnlProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnlProfileLayout.createSequentialGroup()
                                .addComponent(lblEmployeeRole)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAttachPic))
                            .addComponent(photopanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlProfileLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(lblGreetings)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblEmployeeName)))
                .addContainerGap(26, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlProfileLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnPayslip)
                .addGap(78, 78, 78))
        );
        pnlProfileLayout.setVerticalGroup(
            pnlProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProfileLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(photopanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAttachPic)
                    .addComponent(lblEmployeeRole))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGreetings)
                    .addComponent(lblEmployeeName))
                .addGap(85, 85, 85)
                .addComponent(btnPayslip)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlAttendance.setBackground(new java.awt.Color(204, 204, 204));
        pnlAttendance.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setMaximumSize(new java.awt.Dimension(240, 52));
        jPanel1.setMinimumSize(new java.awt.Dimension(240, 52));
        jPanel1.setPreferredSize(new java.awt.Dimension(240, 52));

        jLabel6.setFont(new java.awt.Font("Bahnschrift", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("CURRENT TIME");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(58, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(102, 102, 102));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setMaximumSize(new java.awt.Dimension(240, 52));
        jPanel2.setMinimumSize(new java.awt.Dimension(240, 52));
        jPanel2.setPreferredSize(new java.awt.Dimension(240, 52));

        jLabel1.setFont(new java.awt.Font("Bahnschrift", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CURRENT DATE");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        jSeparator1.setForeground(new java.awt.Color(153, 153, 153));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setMaximumSize(new java.awt.Dimension(274, 52));
        jPanel3.setMinimumSize(new java.awt.Dimension(274, 52));
        jPanel3.setPreferredSize(new java.awt.Dimension(274, 52));

        lblCurrentTime.setFont(new java.awt.Font("Bahnschrift", 0, 30)); // NOI18N
        lblCurrentTime.setForeground(new java.awt.Color(102, 102, 102));
        lblCurrentTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCurrentTime.setText("00:00");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(lblCurrentTime)
                .addContainerGap(143, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblCurrentTime)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setMaximumSize(new java.awt.Dimension(274, 52));
        jPanel4.setMinimumSize(new java.awt.Dimension(274, 52));
        jPanel4.setPreferredSize(new java.awt.Dimension(274, 52));

        lblCurrentDate.setFont(new java.awt.Font("Bahnschrift", 0, 30)); // NOI18N
        lblCurrentDate.setForeground(new java.awt.Color(102, 102, 102));
        lblCurrentDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCurrentDate.setText("MM/DD/YYYY");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(58, Short.MAX_VALUE)
                .addComponent(lblCurrentDate)
                .addGap(48, 48, 48))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblCurrentDate)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(102, 102, 102));
        jPanel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel5.setMaximumSize(new java.awt.Dimension(240, 52));
        jPanel5.setMinimumSize(new java.awt.Dimension(240, 52));

        jLabel7.setFont(new java.awt.Font("Bahnschrift", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Employee Timecard");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(17, 17, 17))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(102, 102, 102));
        jPanel6.setMaximumSize(new java.awt.Dimension(154, 48));
        jPanel6.setMinimumSize(new java.awt.Dimension(154, 48));
        jPanel6.setPreferredSize(new java.awt.Dimension(154, 48));

        jLabel5.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("CHECK-OUT TIME:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(102, 102, 102));
        jPanel7.setMaximumSize(new java.awt.Dimension(154, 48));
        jPanel7.setMinimumSize(new java.awt.Dimension(154, 48));
        jPanel7.setPreferredSize(new java.awt.Dimension(154, 48));

        jLabel8.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("CHECK-IN TIME:");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(204, 204, 204));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel8.setMaximumSize(new java.awt.Dimension(281, 52));
        jPanel8.setMinimumSize(new java.awt.Dimension(281, 52));
        jPanel8.setPreferredSize(new java.awt.Dimension(281, 52));

        lblCheckIn.setFont(new java.awt.Font("Bahnschrift", 1, 14)); // NOI18N
        lblCheckIn.setForeground(new java.awt.Color(87, 108, 167));
        lblCheckIn.setText("You have not check-in yet.");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCheckIn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCheckIn)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(204, 204, 204));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel9.setMaximumSize(new java.awt.Dimension(281, 52));
        jPanel9.setMinimumSize(new java.awt.Dimension(281, 52));
        jPanel9.setPreferredSize(new java.awt.Dimension(281, 52));

        lblCheckOut.setFont(new java.awt.Font("Bahnschrift", 1, 14)); // NOI18N
        lblCheckOut.setForeground(new java.awt.Color(220, 126, 56));
        lblCheckOut.setText("You have not check-out yet.");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCheckOut)
                .addContainerGap(169, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCheckOut)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        btnCheckOut.setBackground(new java.awt.Color(220, 126, 56));
        btnCheckOut.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        btnCheckOut.setForeground(new java.awt.Color(255, 255, 255));
        btnCheckOut.setText("Check-Out");
        btnCheckOut.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCheckOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCheckOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckOutActionPerformed(evt);
            }
        });

        btnCheckIn.setBackground(new java.awt.Color(87, 108, 167));
        btnCheckIn.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        btnCheckIn.setForeground(new java.awt.Color(255, 255, 255));
        btnCheckIn.setText("Check-In");
        btnCheckIn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCheckIn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCheckIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckInActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAttendanceLayout = new javax.swing.GroupLayout(pnlAttendance);
        pnlAttendance.setLayout(pnlAttendanceLayout);
        pnlAttendanceLayout.setHorizontalGroup(
            pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAttendanceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1))
            .addGroup(pnlAttendanceLayout.createSequentialGroup()
                .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAttendanceLayout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43)
                        .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlAttendanceLayout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlAttendanceLayout.createSequentialGroup()
                                .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
                                .addGap(9, 9, 9))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAttendanceLayout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(75, 75, 75)))
                        .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnCheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 40, Short.MAX_VALUE))
        );
        pnlAttendanceLayout.setVerticalGroup(
            pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAttendanceLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAttendanceLayout.createSequentialGroup()
                        .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlAttendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlAttendanceLayout.createSequentialGroup()
                        .addComponent(btnCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(134, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout p1DashboardLayout = new javax.swing.GroupLayout(p1Dashboard);
        p1Dashboard.setLayout(p1DashboardLayout);
        p1DashboardLayout.setHorizontalGroup(
            p1DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p1DashboardLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(pnlProfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlAttendance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        p1DashboardLayout.setVerticalGroup(
            p1DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p1DashboardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(p1DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlProfile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAttendance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(1, Short.MAX_VALUE))
        );

        centerPanel.add(p1Dashboard, "card2");

        p2EmpDet.setBackground(new java.awt.Color(153, 153, 153));
        p2EmpDet.setMaximumSize(new java.awt.Dimension(1060, 550));
        p2EmpDet.setMinimumSize(new java.awt.Dimension(1060, 550));
        p2EmpDet.setPreferredSize(new java.awt.Dimension(1060, 550));

        pnlbutton.setBackground(new java.awt.Color(204, 204, 204));

        btnJobDetails.setFont(new java.awt.Font("Bahnschrift", 0, 17)); // NOI18N
        btnJobDetails.setText("Job Details");
        btnJobDetails.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        btnJobDetails.setMaximumSize(new java.awt.Dimension(512, 44));
        btnJobDetails.setMinimumSize(new java.awt.Dimension(512, 44));
        btnJobDetails.setPreferredSize(new java.awt.Dimension(512, 44));
        btnJobDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJobDetailsActionPerformed(evt);
            }
        });

        btnEmployeeInformation.setFont(new java.awt.Font("Bahnschrift", 0, 17)); // NOI18N
        btnEmployeeInformation.setText("Employee Information");
        btnEmployeeInformation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        btnEmployeeInformation.setMaximumSize(new java.awt.Dimension(512, 44));
        btnEmployeeInformation.setMinimumSize(new java.awt.Dimension(512, 44));
        btnEmployeeInformation.setPreferredSize(new java.awt.Dimension(512, 44));
        btnEmployeeInformation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeInformationActionPerformed(evt);
            }
        });

        btnBonusesdeductions.setFont(new java.awt.Font("Bahnschrift", 0, 17)); // NOI18N
        btnBonusesdeductions.setText("Bonuses & Deductions");
        btnBonusesdeductions.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        btnBonusesdeductions.setMaximumSize(new java.awt.Dimension(512, 44));
        btnBonusesdeductions.setMinimumSize(new java.awt.Dimension(512, 44));
        btnBonusesdeductions.setPreferredSize(new java.awt.Dimension(512, 44));
        btnBonusesdeductions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBonusesdeductionsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlbuttonLayout = new javax.swing.GroupLayout(pnlbutton);
        pnlbutton.setLayout(pnlbuttonLayout);
        pnlbuttonLayout.setHorizontalGroup(
            pnlbuttonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlbuttonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnEmployeeInformation, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(btnJobDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBonusesdeductions, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        pnlbuttonLayout.setVerticalGroup(
            pnlbuttonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlbuttonLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlbuttonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnJobDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEmployeeInformation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBonusesdeductions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlCenter.setBackground(new java.awt.Color(255, 255, 255));
        pnlCenter.setMaximumSize(new java.awt.Dimension(1068, 496));
        pnlCenter.setMinimumSize(new java.awt.Dimension(1068, 496));
        pnlCenter.setPreferredSize(new java.awt.Dimension(1068, 496));
        pnlCenter.setLayout(new java.awt.CardLayout());

        p1EmpInformation.setMaximumSize(new java.awt.Dimension(1060, 500));
        p1EmpInformation.setMinimumSize(new java.awt.Dimension(1060, 500));
        p1EmpInformation.setPreferredSize(new java.awt.Dimension(1060, 500));

        jLabel9.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel9.setText("SEARCH:");

        txtSearch.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        tblEmployeeInformation.setFont(new java.awt.Font("Bahnschrift", 0, 16));
        if(tableModel != null) {
            tblEmployeeInformation.setModel(tableModel
            );
        }
        tblEmployeeInformation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEmployeeInformationMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblEmployeeInformation);

        jLabel12.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel12.setText("Phone Number:");

        jLabel13.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel13.setText("Last Name:");

        jLabel15.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel15.setText("First Name:");

        jLabel16.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel16.setText("Birthday:");

        jLabel17.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel17.setText("Address:");

        txtAdd.setColumns(20);
        txtAdd.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtAdd.setRows(5);
        txtAddress.setViewportView(txtAdd);

        txtLastName.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N

        txtFirstName.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtFirstName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFirstNameActionPerformed(evt);
            }
        });

        txtPNumber.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtPNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPNumberKeyTyped(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel18.setText("Pag-ibig #:");

        jLabel19.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel19.setText("SSS #:");

        jLabel20.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel20.setText("TIN #:");

        jLabel21.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel21.setText("Philhealth #:");

        txtTinNum.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtTinNum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTinNumKeyTyped(evt);
            }
        });

        txtSSSNum.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtSSSNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSSSNumActionPerformed(evt);
            }
        });
        txtSSSNum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSSSNumKeyTyped(evt);
            }
        });

        txtPhilhealthNum.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtPhilhealthNum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPhilhealthNumKeyTyped(evt);
            }
        });

        txtPagibigNum.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtPagibigNum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPagibigNumKeyTyped(evt);
            }
        });

        btnAddEmployee.setFont(new java.awt.Font("Bahnschrift", 1, 16)); // NOI18N
        btnAddEmployee.setText("ADD");
        btnAddEmployee.setAlignmentX(0.5F);
        btnAddEmployee.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        btnAddEmployee.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddEmployee.setMaximumSize(new java.awt.Dimension(92, 27));
        btnAddEmployee.setMinimumSize(new java.awt.Dimension(92, 27));
        btnAddEmployee.setPreferredSize(new java.awt.Dimension(92, 27));
        btnAddEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddEmployeeActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Bahnschrift", 1, 16)); // NOI18N
        btnUpdate.setText("UPDATE");
        btnUpdate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        btnUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnClear.setFont(new java.awt.Font("Bahnschrift", 1, 16)); // NOI18N
        btnClear.setText("CLEAR");
        btnClear.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        btnClear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClear.setMaximumSize(new java.awt.Dimension(92, 27));
        btnClear.setMinimumSize(new java.awt.Dimension(92, 27));
        btnClear.setPreferredSize(new java.awt.Dimension(92, 27));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        dateBirthday.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N

        javax.swing.GroupLayout p1EmpInformationLayout = new javax.swing.GroupLayout(p1EmpInformation);
        p1EmpInformation.setLayout(p1EmpInformationLayout);
        p1EmpInformationLayout.setHorizontalGroup(
            p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p1EmpInformationLayout.createSequentialGroup()
                .addGap(23, 24, Short.MAX_VALUE)
                .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1020, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(p1EmpInformationLayout.createSequentialGroup()
                        .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel17)
                            .addComponent(jLabel13)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addGap(12, 12, 12)
                        .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtAddress)
                            .addComponent(txtLastName)
                            .addComponent(txtFirstName)
                            .addComponent(txtPNumber)
                            .addComponent(dateBirthday, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(p1EmpInformationLayout.createSequentialGroup()
                                .addGap(96, 96, 96)
                                .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(p1EmpInformationLayout.createSequentialGroup()
                                        .addComponent(jLabel19)
                                        .addGap(82, 82, 82)
                                        .addComponent(txtSSSNum))
                                    .addGroup(p1EmpInformationLayout.createSequentialGroup()
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtPhilhealthNum, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(p1EmpInformationLayout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtPagibigNum, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(p1EmpInformationLayout.createSequentialGroup()
                                        .addComponent(jLabel20)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtTinNum, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p1EmpInformationLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAddEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(53, 53, 53)
                                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(53, 53, 53)
                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)))))
                .addContainerGap(20, Short.MAX_VALUE))
            .addGroup(p1EmpInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        p1EmpInformationLayout.setVerticalGroup(
            p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p1EmpInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtSSSNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(txtPhilhealthNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(jLabel18)
                        .addComponent(txtPagibigNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dateBirthday, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p1EmpInformationLayout.createSequentialGroup()
                        .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtPNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13))
                    .addGroup(p1EmpInformationLayout.createSequentialGroup()
                        .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(txtTinNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(p1EmpInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAddEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(83, 83, 83))
        );

        pnlCenter.add(p1EmpInformation, "card2");

        p2JobDetails.setMaximumSize(new java.awt.Dimension(1080, 496));
        p2JobDetails.setMinimumSize(new java.awt.Dimension(1080, 496));

        jLabel11.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel11.setText("SEARCH EMPLOYEE:");

        txtIDSearch.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtIDSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtIDSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDSearchActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel14.setText("Employee ID:");

        txtEmpIDforJob.setEditable(false);
        txtEmpIDforJob.setBackground(new java.awt.Color(255, 255, 255));
        txtEmpIDforJob.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtEmpIDforJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmpIDforJobActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel22.setText("Last Name:");

        txtLNameJob.setEditable(false);
        txtLNameJob.setBackground(new java.awt.Color(255, 255, 255));
        txtLNameJob.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtLNameJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLNameJobActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel23.setText("First Name:");

        txtFNameJob.setEditable(false);
        txtFNameJob.setBackground(new java.awt.Color(255, 255, 255));
        txtFNameJob.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N

        jLabel24.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel24.setText("Status:");

        txtStatusJob.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel25.setText("Basic Salary:");

        txtBasicSalary.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtBasicSalary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBasicSalaryActionPerformed(evt);
            }
        });
        txtBasicSalary.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBasicSalaryKeyTyped(evt);
            }
        });

        btnUpdateJob.setFont(new java.awt.Font("Bahnschrift", 1, 16)); // NOI18N
        btnUpdateJob.setText("UPDATE");
        btnUpdateJob.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        btnUpdateJob.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUpdateJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateJobActionPerformed(evt);
            }
        });

        btnClearJob.setFont(new java.awt.Font("Bahnschrift", 1, 16)); // NOI18N
        btnClearJob.setText("CLEAR");
        btnClearJob.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        btnClearJob.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClearJob.setMaximumSize(new java.awt.Dimension(92, 27));
        btnClearJob.setMinimumSize(new java.awt.Dimension(92, 27));
        btnClearJob.setPreferredSize(new java.awt.Dimension(92, 27));
        btnClearJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearJobActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel26.setText("Position:");

        txtPositionJob.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtPositionJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPositionJobActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel27.setText("Supervisor:");

        txtSupeJob.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtSupeJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSupeJobActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel28.setText("Rice Subsidy:");

        txtRiceJob.setEditable(false);
        txtRiceJob.setBackground(new java.awt.Color(255, 255, 255));
        txtRiceJob.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtRiceJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRiceJobActionPerformed(evt);
            }
        });

        txtPhoneJob.setEditable(false);
        txtPhoneJob.setBackground(new java.awt.Color(255, 255, 255));
        txtPhoneJob.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtPhoneJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneJobActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel29.setText("Phone Allowance:");

        jLabel30.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel30.setText("Clothing Allowance:");

        txtClothJob.setEditable(false);
        txtClothJob.setBackground(new java.awt.Color(255, 255, 255));
        txtClothJob.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtClothJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClothJobActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        jLabel31.setText("BONUSES");

        tblJobDetails.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        if (jobDetailsTableModel != null){
            tblJobDetails.setModel(jobDetailsTableModel);
        }
        tblJobDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblJobDetailsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblJobDetails);

        btnRefreshJob.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        btnRefreshJob.setForeground(new java.awt.Color(204, 0, 51));
        btnRefreshJob.setText("REFRESH");
        btnRefreshJob.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnRefreshJob.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefreshJob.setMaximumSize(new java.awt.Dimension(92, 27));
        btnRefreshJob.setMinimumSize(new java.awt.Dimension(92, 27));
        btnRefreshJob.setPreferredSize(new java.awt.Dimension(92, 27));
        btnRefreshJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshJobActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout p2JobDetailsLayout = new javax.swing.GroupLayout(p2JobDetails);
        p2JobDetails.setLayout(p2JobDetailsLayout);
        p2JobDetailsLayout.setHorizontalGroup(
            p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p2JobDetailsLayout.createSequentialGroup()
                .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1014, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(p2JobDetailsLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel11)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtIDSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(p2JobDetailsLayout.createSequentialGroup()
                            .addGap(28, 28, 28)
                            .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(p2JobDetailsLayout.createSequentialGroup()
                                    .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(p2JobDetailsLayout.createSequentialGroup()
                                            .addComponent(jLabel27)
                                            .addGap(18, 18, 18)
                                            .addComponent(txtSupeJob, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p2JobDetailsLayout.createSequentialGroup()
                                                .addComponent(jLabel14)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtEmpIDforJob, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(57, 57, 57)
                                                .addComponent(jLabel22)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtLNameJob, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(60, 60, 60)
                                                .addComponent(jLabel23))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p2JobDetailsLayout.createSequentialGroup()
                                                .addComponent(jLabel25)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtBasicSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(39, 39, 39)
                                                .addComponent(btnUpdateJob, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(247, 247, 247))))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtFNameJob, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnRefreshJob, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(p2JobDetailsLayout.createSequentialGroup()
                                        .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel24)
                                            .addComponent(jLabel26))
                                        .addGap(42, 42, 42)
                                        .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtPositionJob, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtStatusJob, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(167, 167, 167)
                                        .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(p2JobDetailsLayout.createSequentialGroup()
                                                .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p2JobDetailsLayout.createSequentialGroup()
                                                        .addComponent(jLabel29)
                                                        .addGap(28, 28, 28))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p2JobDetailsLayout.createSequentialGroup()
                                                        .addComponent(jLabel28)
                                                        .addGap(64, 64, 64))
                                                    .addGroup(p2JobDetailsLayout.createSequentialGroup()
                                                        .addComponent(jLabel30)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                                .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtClothJob, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txtPhoneJob, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txtRiceJob, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(31, 31, 31)
                                                .addComponent(btnClearJob, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLabel31))))))))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        p2JobDetailsLayout.setVerticalGroup(
            p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p2JobDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtIDSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtLNameJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(txtFNameJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(txtEmpIDforJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txtStatusJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addGap(6, 6, 6)
                .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtPositionJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(txtRiceJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(txtSupeJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(txtPhoneJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(p2JobDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(txtBasicSalary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(txtClothJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClearJob, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateJob, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefreshJob, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        pnlCenter.add(p2JobDetails, "card3");

        javax.swing.GroupLayout p3BonusesDeductionsLayout = new javax.swing.GroupLayout(p3BonusesDeductions);
        p3BonusesDeductions.setLayout(p3BonusesDeductionsLayout);
        p3BonusesDeductionsLayout.setHorizontalGroup(
            p3BonusesDeductionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1064, Short.MAX_VALUE)
        );
        p3BonusesDeductionsLayout.setVerticalGroup(
            p3BonusesDeductionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 476, Short.MAX_VALUE)
        );

        pnlCenter.add(p3BonusesDeductions, "card4");

        javax.swing.GroupLayout p2EmpDetLayout = new javax.swing.GroupLayout(p2EmpDet);
        p2EmpDet.setLayout(p2EmpDetLayout);
        p2EmpDetLayout.setHorizontalGroup(
            p2EmpDetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p2EmpDetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(p2EmpDetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlbutton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCenter, javax.swing.GroupLayout.PREFERRED_SIZE, 1064, Short.MAX_VALUE))
                .addContainerGap())
        );
        p2EmpDetLayout.setVerticalGroup(
            p2EmpDetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p2EmpDetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlbutton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCenter, javax.swing.GroupLayout.PREFERRED_SIZE, 476, Short.MAX_VALUE)
                .addContainerGap())
        );

        centerPanel.add(p2EmpDet, "card3");

        p3EmpTime.setBackground(new java.awt.Color(153, 153, 153));
        p3EmpTime.setMaximumSize(new java.awt.Dimension(1080, 560));
        p3EmpTime.setMinimumSize(new java.awt.Dimension(1080, 560));

        jLabel32.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setText("SEARCH EMPLOYEE ID:");

        txtIDSearchAttend.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        txtIDSearchAttend.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtIDSearchAttend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDSearchAttendActionPerformed(evt);
            }
        });
        txtIDSearchAttend.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtIDSearchAttendKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtIDSearchAttendKeyTyped(evt);
            }
        });

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("From:");

        dateFromAttend.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        dateFromAttend.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateFromAttendPropertyChange(evt);
            }
        });

        dateToAttend.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        dateToAttend.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateToAttendPropertyChange(evt);
            }
        });

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setText("To:");

        btnClearAtt.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        btnClearAtt.setForeground(new java.awt.Color(51, 0, 153));
        btnClearAtt.setText("CLEAR");
        btnClearAtt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnClearAtt.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClearAtt.setMaximumSize(new java.awt.Dimension(92, 27));
        btnClearAtt.setMinimumSize(new java.awt.Dimension(92, 27));
        btnClearAtt.setPreferredSize(new java.awt.Dimension(92, 27));
        btnClearAtt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearAttActionPerformed(evt);
            }
        });

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setText("Undertime:");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setText("No. of Days:");

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setText("Overtime:");

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 255, 255));
        jLabel41.setText("Total Hours worked:");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(255, 255, 255));
        jLabel42.setText("Total Minutes worked:");

        lblHoursWork.setBackground(new java.awt.Color(255, 255, 255));
        lblHoursWork.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        lblHoursWork.setForeground(new java.awt.Color(255, 255, 255));
        lblHoursWork.setText("0");

        lblUndertime.setBackground(new java.awt.Color(255, 255, 255));
        lblUndertime.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        lblUndertime.setForeground(new java.awt.Color(255, 255, 255));
        lblUndertime.setText("0");

        lblOvertime.setBackground(new java.awt.Color(255, 255, 255));
        lblOvertime.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        lblOvertime.setForeground(new java.awt.Color(255, 255, 255));
        lblOvertime.setText("0");

        lblNumDays.setBackground(new java.awt.Color(255, 255, 255));
        lblNumDays.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        lblNumDays.setForeground(new java.awt.Color(255, 255, 255));
        lblNumDays.setText("0");

        lblMinutes.setBackground(new java.awt.Color(255, 255, 255));
        lblMinutes.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        lblMinutes.setForeground(new java.awt.Color(255, 255, 255));
        lblMinutes.setText("0");

        tblAttend.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        tblAttend.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Date", "Time", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblAttend);

        jLabel34.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setText("<html><b>[!] </b><i> Remember the Hours Worked</i></html>");

        jLabel36.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setText("<html><b>[2]</b>\nSearch an employee using their ID.\n</html>");

        jLabel37.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setText("<html><b>[1]</b>\nPick a date first. For Salary Periods:\n<ul>\n  <li>From Day 1 To 15</li>\n  <li>From Day 16 To end of the Month</li>\n</ul>\n</html>");

        jLabel52.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel52.setForeground(new java.awt.Color(255, 255, 255));
        jLabel52.setText("<html><b>[NOTE:]</b>\nTo see an example, enter 3, 6, or 32.\n</html>");

        javax.swing.GroupLayout p3EmpTimeLayout = new javax.swing.GroupLayout(p3EmpTime);
        p3EmpTime.setLayout(p3EmpTimeLayout);
        p3EmpTimeLayout.setHorizontalGroup(
            p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p3EmpTimeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p3EmpTimeLayout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtIDSearchAttend, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(95, 95, 95)
                        .addComponent(btnClearAtt, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(p3EmpTimeLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(88, 88, 88)
                        .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel39)
                            .addComponent(jLabel40)
                            .addComponent(jLabel41)
                            .addComponent(jLabel42)
                            .addComponent(jLabel38))
                        .addGap(34, 34, 34)
                        .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblHoursWork)
                            .addComponent(lblUndertime)
                            .addComponent(lblOvertime)
                            .addComponent(lblNumDays)
                            .addComponent(lblMinutes)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(p3EmpTimeLayout.createSequentialGroup()
                        .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(p3EmpTimeLayout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(dateFromAttend, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(p3EmpTimeLayout.createSequentialGroup()
                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 362, Short.MAX_VALUE)
                                .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(p3EmpTimeLayout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dateToAttend, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        p3EmpTimeLayout.setVerticalGroup(
            p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p3EmpTimeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateFromAttend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33)
                    .addComponent(jLabel35)
                    .addComponent(dateToAttend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(txtIDSearchAttend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClearAtt, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p3EmpTimeLayout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38)
                            .addComponent(lblUndertime))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel40)
                            .addComponent(lblOvertime))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel39)
                            .addComponent(lblNumDays))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel41)
                            .addComponent(lblHoursWork))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(p3EmpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel42)
                            .addComponent(lblMinutes))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(p3EmpTimeLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        centerPanel.add(p3EmpTime, "card4");

        p4Payroll.setBackground(new java.awt.Color(204, 204, 204));
        p4Payroll.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        p4Payroll.setMaximumSize(new java.awt.Dimension(1080, 560));
        p4Payroll.setMinimumSize(new java.awt.Dimension(1080, 560));
        p4Payroll.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtEmpIDpayroll.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N

        txtLNamePayroll.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        txtLNamePayroll.setPreferredSize(new java.awt.Dimension(90, 22));

        txtFNamePayroll.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        txtFNamePayroll.setPreferredSize(new java.awt.Dimension(90, 22));

        jLabel46.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel46.setText("Employee ID:");

        jLabel47.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel47.setText("Last Name:");

        jLabel48.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel48.setText("First Name:");

        txtPositionPayroll.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N

        jLabel49.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel49.setText("Position:");

        jLabel45.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel45.setText("<html><b>[1]</b>  Search Employee ID:</html>");

        txtSearchPayroll.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        txtSearchPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchPayrollActionPerformed(evt);
            }
        });

        jSeparator2.setForeground(new java.awt.Color(153, 153, 153));

        txtBSalary.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        txtBSalary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBSalaryActionPerformed(evt);
            }
        });

        txtWorkingDays.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N

        jLabel50.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel50.setText("Basic Salary:");

        jLabel53.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel53.setText("Days worked:");

        txtClear1Payroll.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        txtClear1Payroll.setText("CLEAR");
        txtClear1Payroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClear1PayrollActionPerformed(evt);
            }
        });

        tblJobPayroll.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        if (payrollTableModel != null){
            tblJobPayroll.setModel(payrollTableModel);
        }
        tblJobPayroll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblJobPayrollMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblJobPayroll);

        btnCalculate.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        btnCalculate.setText("CALCULATE");
        btnCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateActionPerformed(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel56.setText("Gross Income:");

        jLabel44.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel44.setText("<html>Select Salary Period:<i>(this is to count the days worked)  </i></html>");

        cmbSalaryPeriod.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        cmbSalaryPeriod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Salary Period: 1 - 15", "Salary Period: 16 - end" }));

        cmbMonth.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        cmbMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        btnRetrieve.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        btnRetrieve.setText("RETRIEVE DATES");
        btnRetrieve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRetrieveActionPerformed(evt);
            }
        });

        cmbYear.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        cmbYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2024", "2025", "2026", "2027" }));
        cmbYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbYearActionPerformed(evt);
            }
        });

        jLabel71.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel71.setText("Enter Total Minutes Worked:");

        txtMinutes.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel10.setText("<html><b>[2]</b><i>Click the table to get the Basic Salary  </i></html>");

        jLabel54.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel54.setText("<html><i> (Press Enter)  </i></html>");

        lblSalary.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        lblSalary.setText("0");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearchPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addGap(0, 28, Short.MAX_VALUE)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel56)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addGap(168, 168, 168)
                                        .addComponent(txtClear1Payroll))
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addComponent(cmbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbSalaryPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRetrieve)
                                .addContainerGap())
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addComponent(btnCalculate)
                                .addGap(177, 177, 177))))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollPane5)
                        .addGap(4, 4, 4))))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel46)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEmpIDpayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel49)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtPositionPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel47)
                            .addComponent(jLabel48))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFNamePayroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLNamePayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel53)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtWorkingDays, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel50)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtBSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel71))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(txtMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmpIDpayroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46)
                    .addComponent(jLabel47)
                    .addComponent(txtLNamePayroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFNamePayroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48)
                    .addComponent(txtPositionPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbSalaryPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRetrieve)
                    .addComponent(cmbYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBSalary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50)
                    .addComponent(jLabel71))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtWorkingDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53)
                    .addComponent(txtMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtClear1Payroll))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(btnCalculate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel56)
                            .addComponent(lblSalary))
                        .addContainerGap(40, Short.MAX_VALUE))))
        );

        p4Payroll.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 500, 500));

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel57.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel57.setText("Deductions:");

        txtRiceSub.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        txtRiceSub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRiceSubActionPerformed(evt);
            }
        });

        txtPhoneAll.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        txtPhoneAll.setPreferredSize(new java.awt.Dimension(85, 22));

        txtClothAll.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N

        jLabel58.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel58.setText("Rice Subsidy:");

        jLabel59.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel59.setText("Phone Allowance:");

        jLabel60.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel60.setText("Clothing Allowance: ");

        jLabel61.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel61.setText("SSS:");

        jLabel62.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel62.setText("Philhealth:");

        jLabel63.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel63.setText("Pag-ibig:");

        jLabel64.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel64.setText("Taxable Income:");

        jLabel65.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel65.setText("Withholding Tax:");

        jLabel66.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel66.setText("NET PAY:");

        jLabel67.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel67.setText("Bonuses:");

        jLabel69.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        jLabel69.setText("<html>Total Deductions:<i>(without tax) </i></html>");

        btnSavePayroll.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        btnSavePayroll.setText("SAVE PAYSLIP");
        btnSavePayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavePayrollActionPerformed(evt);
            }
        });

        btnReset.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        btnReset.setText("RESET");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        jSeparator3.setForeground(new java.awt.Color(153, 153, 153));

        jSeparator4.setForeground(new java.awt.Color(153, 153, 153));

        lbltAllowances.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        lbltAllowances.setText("0");

        lblSSS.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        lblSSS.setText("0");

        lblPhilhealth.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        lblPhilhealth.setText("0");

        lblTDeductions.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        lblTDeductions.setText("0");

        lblTaxableIncome.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        lblTaxableIncome.setText("0");

        lblPagibig.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        lblPagibig.setText("0");

        lblWithholdingTax.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        lblWithholdingTax.setText("0");

        lblNetpay.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        lblNetpay.setText("0");

        lblGross.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        lblGross.setText("<html>Gross Salary<i>+ total allowances : </i></html>");

        lblGrossSalary.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        lblGrossSalary.setText("0");

        lblGross1.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N
        lblGross1.setText("Total Allowances:");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3)
            .addComponent(jSeparator4)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnReset))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel65)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblWithholdingTax, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel64)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblTaxableIncome, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel66)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblNetpay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSavePayroll)
                        .addGap(24, 24, 24))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel62)
                        .addGap(18, 18, 18)
                        .addComponent(lblPhilhealth, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel63)
                                    .addComponent(jLabel61))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblSSS, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                                    .addComponent(lblPagibig, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel59)
                                    .addComponent(jLabel58))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtRiceSub)
                                    .addComponent(txtPhoneAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtClothAll, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addGap(40, 40, 40)
                                        .addComponent(lbltAllowances, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addGap(27, 27, 27)
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel10Layout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addComponent(lblGrossSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(lblGross, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblGross1)))))
                            .addComponent(jLabel60))
                        .addContainerGap(95, Short.MAX_VALUE))))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel67)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblTDeductions, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(91, 91, 91))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel67)
                                    .addComponent(lblGross1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtRiceSub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel58)
                                    .addComponent(lbltAllowances))
                                .addGap(4, 4, 4)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtPhoneAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel59)
                                    .addComponent(lblGross, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtClothAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel60)
                                    .addComponent(lblGrossSalary))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel57)
                                .addGap(12, 12, 12))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel62)
                            .addComponent(lblPhilhealth))
                        .addGap(8, 8, 8))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(lblTDeductions)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61)
                    .addComponent(lblSSS))
                .addGap(7, 7, 7)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(lblPagibig))
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel64)
                            .addComponent(lblTaxableIncome))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel65)
                            .addComponent(lblWithholdingTax))
                        .addGap(57, 57, 57)
                        .addComponent(btnReset)
                        .addGap(1, 1, 1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel66)
                            .addComponent(lblNetpay)
                            .addComponent(btnSavePayroll))
                        .addGap(95, 95, 95))))
        );

        p4Payroll.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 40, -1, 460));

        centerPanel.add(p4Payroll, "card5");

        mainPanel.add(centerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 168, -1, 550));

        getContentPane().add(mainPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
        mainPanel.getAccessibleContext().setAccessibleDescription("");

        getAccessibleContext().setAccessibleDescription("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

                //to designate a panel
    private void btnEmployeeInformationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeInformationActionPerformed
        // TODO add your handling code here:
        p1EmpInformation.setVisible(true);//panel that will be visible
        p2JobDetails.setVisible(false);//to hide other panels
        p3BonusesDeductions.setVisible(false);
    }//GEN-LAST:event_btnEmployeeInformationActionPerformed

    private void btnJobDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJobDetailsActionPerformed
        // TODO add your handling code here:
        p1EmpInformation.setVisible(false);
        p2JobDetails.setVisible(true);
        p3BonusesDeductions.setVisible(false);
    }//GEN-LAST:event_btnJobDetailsActionPerformed

    
  
    
    
    private void btnCheckInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckInActionPerformed
        // TODO add your handling code here:
    if (attendanceController.hasCheckedIn()) {
        JOptionPane.showMessageDialog(this, "You have already logged in.", "Information", JOptionPane.INFORMATION_MESSAGE);
    } else {
        attendanceController.logAttendance(this, currentEmployee, "Logged In", lblCheckIn, lblCheckOut);
        // Set the flag and update the label after successfully logging in
        hasCheckedIn = true;
    }
    }//GEN-LAST:event_btnCheckInActionPerformed


    
    //NOT FINISHED
    private void btnAttachPicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttachPicActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File f = chooser.getSelectedFile();

        filename = f.getAbsolutePath();
        ImageIcon imageIcon = new ImageIcon (new ImageIcon(filename).getImage().getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_DEFAULT));
        img.setIcon(imageIcon);

        try{
            File image = new File (filename);
            FileInputStream fix = new FileInputStream(image);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];

            for(int number; (number = fix.read(buf)) != -1;){
                bos.write(buf, 0, number);
            }
            person_image = bos.toByteArray();

        }catch(IOException e){
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btnAttachPicActionPerformed

    private void btnPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayrollActionPerformed
        // TODO add your handling code here
        handleButtonClick(evt, "p4Payroll", btnPayroll);
        p1Dashboard.setVisible(false);
        p2EmpDet.setVisible(false);
        p3EmpTime.setVisible(false);
        p4Payroll.setVisible(true);

    }//GEN-LAST:event_btnPayrollActionPerformed

    private void btnEmpTimesheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmpTimesheetActionPerformed
        // TODO add your handling code here:
        handleButtonClick(evt, "p3EmpTime", btnEmpTimesheet);
        p1Dashboard.setVisible(false);
        p2EmpDet.setVisible(false);
        p3EmpTime.setVisible(true);
        p4Payroll.setVisible(false);
    }//GEN-LAST:event_btnEmpTimesheetActionPerformed

    private void btnEmpDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmpDetailsActionPerformed
        // TODO add your handling code here:
        handleButtonClick(evt, "p2EmpDet", btnEmpDetails);
        p1Dashboard.setVisible(false);
        p2EmpDet.setVisible(true);
        p3EmpTime.setVisible(false);
        p4Payroll.setVisible(false);
    }//GEN-LAST:event_btnEmpDetailsActionPerformed

    private void btnEmpDashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmpDashActionPerformed
        // TODO add your handling code here:
        handleButtonClick(evt, "p1Dashboard", btnEmpDash);
        p1Dashboard.setVisible(true);
        p2EmpDet.setVisible(false);
        p3EmpTime.setVisible(false);

    }//GEN-LAST:event_btnEmpDashActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        // Create a new instance of your login frame
        loginForm loginForm = new loginForm();

        // Set the login frame visible
        loginForm.setVisible(true);

        // Dispose the current admin portal frame
        dispose();

    }//GEN-LAST:event_btnLogoutActionPerformed

    private void txtFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFirstNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFirstNameActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
            // Clear the content of text fields and text area
        txtLastName.setText("");
        txtFirstName.setText("");
        dateBirthday.setDate(null);
        txtAdd.setText("");
        txtPNumber.setText("");
        txtSSSNum.setText("");
        txtPhilhealthNum.setText("");
        txtPagibigNum.setText("");
        txtTinNum.setText("");
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnAddEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddEmployeeActionPerformed
        // TODO add your handling code here:
        // Retrieve data from text fields and text area
        
        //from employee class = textfield name . get the inputted text
        String lastName = txtLastName.getText();
        String firstName = txtFirstName.getText();
        Date birthday = dateBirthday.getDate();
        String address = txtAdd.getText();
        String phoneNum = txtPNumber.getText();
        String sssNum = txtSSSNum.getText();
        String philhealthNum = txtPhilhealthNum.getText();
        String pagibigNum = txtPagibigNum.getText();
        String tinNum = txtTinNum.getText();

        // Create an Employee object        (constructors)
        currentEmployee = new Employee(lastName, firstName, birthday, address, phoneNum, sssNum, philhealthNum, pagibigNum, tinNum);

        // Save employee data to MySQL
        saveEmployeeToDatabase(currentEmployee);
        dataController.fetchDataFromDatabase();
    }//GEN-LAST:event_btnAddEmployeeActionPerformed

    private void btnCheckOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckOutActionPerformed
        // TODO add your handling code here:
    if (attendanceController.hasCheckedOut()) {
        JOptionPane.showMessageDialog(this, "You have already logged out.", "Information", JOptionPane.INFORMATION_MESSAGE);
    } else {
        attendanceController.logAttendance(this, currentEmployee, "Logged Out", lblCheckIn, lblCheckOut);
        // Set the flag and update the label after successfully logging out
        hasCheckedOut = true;
    }
    }//GEN-LAST:event_btnCheckOutActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
        

    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnBonusesdeductionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBonusesdeductionsActionPerformed
        // TODO add your handling code here:
        p1EmpInformation.setVisible(false);
        p2JobDetails.setVisible(false);
        p3BonusesDeductions.setVisible(true);
    }//GEN-LAST:event_btnBonusesdeductionsActionPerformed

    private void tblEmployeeInformationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEmployeeInformationMouseClicked
        // TODO add your handling code here:
        // Get the selected row index
        int selectedRow = tblEmployeeInformation.getSelectedRow();

        // Ensure a row is selected
        if (selectedRow != -1) {
            // Get data from the selected row
            String employeeID = tblEmployeeInformation.getValueAt(selectedRow, 0).toString();
            String lastName = tblEmployeeInformation.getValueAt(selectedRow, 1).toString();
            String firstName = tblEmployeeInformation.getValueAt(selectedRow, 2).toString();
            String stringDate = tblEmployeeInformation.getValueAt(selectedRow, 3).toString();
             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
             Date birthday = null;
             try {
                 birthday = sdf.parse(stringDate);
             } catch (ParseException e) {
                 e.printStackTrace();
             }
            String address = tblEmployeeInformation.getValueAt(selectedRow, 4).toString();
            String phoneNumber = tblEmployeeInformation.getValueAt(selectedRow, 5).toString();
            String sssNum = tblEmployeeInformation.getValueAt(selectedRow, 6).toString();
            String philhealthNum = tblEmployeeInformation.getValueAt(selectedRow, 7).toString();
            String pagibigNum = tblEmployeeInformation.getValueAt(selectedRow, 8).toString();
            String tinNum = tblEmployeeInformation.getValueAt(selectedRow, 9).toString();

            // Set data to text fields and text area
            txtLastName.setText(lastName);
            txtFirstName.setText(firstName);
            dateBirthday.setDate(birthday);
            txtAdd.setText(address); // Use setText for JTextArea
            txtPNumber.setText(phoneNumber);
            txtSSSNum.setText(sssNum);
            txtPhilhealthNum.setText(philhealthNum);
            txtPagibigNum.setText(pagibigNum);
            txtTinNum.setText(tinNum);
        }
    }//GEN-LAST:event_tblEmployeeInformationMouseClicked

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblEmployeeInformation.getSelectedRow();

        if (selectedRow != -1) {
            // Retrieve updated data from text fields and text area
            String lastName = txtLastName.getText();
            String firstName = txtFirstName.getText();
            // Retrieve birthday from the JDateChooser
            Date birthdayDate = dateBirthday.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String birthday = dateFormat.format(birthdayDate);
            String address = txtAdd.getText(); // Use the correct name for the JTextArea
            String phoneNumber = txtPNumber.getText();
            String sssNum = txtSSSNum.getText();
            String philhealthNum = txtPhilhealthNum.getText();
            String pagibigNum = txtPagibigNum.getText();
            String tinNum = txtTinNum.getText();
            int employeeID = Integer.parseInt(tblEmployeeInformation.getValueAt(selectedRow, 0).toString());

            // Call the method from EmployeeDAO to update the employee
            EmployeeDataUpdate.updateEmployeeInDatabase(employeeID, lastName, firstName, birthday, address, phoneNumber, sssNum, philhealthNum, pagibigNum, tinNum);
            dataController.fetchDataFromDatabase();
            
        }
        
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtIDSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIDSearchActionPerformed

    private void txtLNameJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLNameJobActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLNameJobActionPerformed

    private void btnUpdateJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateJobActionPerformed
        // TODO add your handling code here:
        // Get the updated data from the text fields
        int employeeID = Integer.parseInt(txtEmpIDforJob.getText());
        String lastName = txtLNameJob.getText();
        String firstName = txtFNameJob.getText();
        String status = txtStatusJob.getText();
        String position = txtPositionJob.getText();
        String supervisor = txtSupeJob.getText();
        double basicSalary = Double.parseDouble(txtBasicSalary.getText());
        double riceSubsidy = Double.parseDouble(txtRiceJob.getText());
        double phoneAllowance = Double.parseDouble(txtPhoneJob.getText());
        double clothingAllowance = Double.parseDouble(txtClothJob.getText());

        // Initialize JobDetailsManager with data from the selected row
        jobDetailsManager = new JobDetailsManager(
            employeeID,
            lastName,
            firstName,
            status,
            position,
            supervisor,
            basicSalary,
            riceSubsidy,
            phoneAllowance,
            clothingAllowance
        );

        // Update the text fields with data from the selected row
        txtEmpIDforJob.setText(String.valueOf(employeeID));
        txtLNameJob.setText(lastName);
        txtFNameJob.setText(firstName);
        txtStatusJob.setText(status);
        txtPositionJob.setText(position);
        txtSupeJob.setText(supervisor);
        txtBasicSalary.setText(String.valueOf(basicSalary));
        txtRiceJob.setText(String.valueOf(riceSubsidy));
        txtPhoneJob.setText(String.valueOf(phoneAllowance));
        txtClothJob.setText(String.valueOf(clothingAllowance));

        
        // Identify the selected row in tblJobDetails
        int selectedRowIndex = tblJobDetails.getSelectedRow();

        if (selectedRowIndex != -1) {
            // Update the data in the table model
            tblJobDetails.setValueAt(employeeID, selectedRowIndex, 1);
            tblJobDetails.setValueAt(lastName, selectedRowIndex, 2);
            tblJobDetails.setValueAt(firstName, selectedRowIndex, 3);
            tblJobDetails.setValueAt(status, selectedRowIndex, 4);
            tblJobDetails.setValueAt(position, selectedRowIndex, 5);
            tblJobDetails.setValueAt(supervisor, selectedRowIndex, 6);
            tblJobDetails.setValueAt(basicSalary, selectedRowIndex, 7);
            tblJobDetails.setValueAt(riceSubsidy, selectedRowIndex, 8);
            tblJobDetails.setValueAt(phoneAllowance, selectedRowIndex, 9);
            tblJobDetails.setValueAt(clothingAllowance, selectedRowIndex, 10);

                // Update the data in the MySQL database
                jobDetailsManager.updateJobDetailsInDatabase();
        }
    }//GEN-LAST:event_btnUpdateJobActionPerformed

    private void btnClearJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearJobActionPerformed
        // TODO add your handling code here:
                // Update the text fields with data from the selected row
        txtEmpIDforJob.setText("");
        txtLNameJob.setText("");
        txtFNameJob.setText("");
        txtStatusJob.setText("");
        txtPositionJob.setText("");
        txtSupeJob.setText("");
        txtBasicSalary.setText(String.valueOf(""));
        txtRiceJob.setText(String.valueOf(""));
        txtPhoneJob.setText(String.valueOf(""));
        txtClothJob.setText(String.valueOf(""));
    }//GEN-LAST:event_btnClearJobActionPerformed

    private void txtBasicSalaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBasicSalaryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBasicSalaryActionPerformed

    private void txtPositionJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPositionJobActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPositionJobActionPerformed

    private void txtSupeJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSupeJobActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSupeJobActionPerformed

    private void txtEmpIDforJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmpIDforJobActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmpIDforJobActionPerformed

    private void txtRiceJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRiceJobActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRiceJobActionPerformed

    private void txtPhoneJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneJobActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneJobActionPerformed

    private void txtClothJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClothJobActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClothJobActionPerformed

    private void tblJobDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblJobDetailsMouseClicked
        // TODO add your handling code here:
            int selectedRowIndex = tblJobDetails.getSelectedRow();

            if (selectedRowIndex != -1) {
                // Get data from the selected row
                int employeeID = (int) tblJobDetails.getValueAt(selectedRowIndex, 1);
                String lastName = (String) tblJobDetails.getValueAt(selectedRowIndex, 2);
                String firstName = (String) tblJobDetails.getValueAt(selectedRowIndex, 3);
                String status = (String) tblJobDetails.getValueAt(selectedRowIndex, 4);
                String position = (String) tblJobDetails.getValueAt(selectedRowIndex, 5);
                String supervisor = (String) tblJobDetails.getValueAt(selectedRowIndex, 6);
                double basicSalary = (double) tblJobDetails.getValueAt(selectedRowIndex, 7);
                double riceSubsidy = (double) tblJobDetails.getValueAt(selectedRowIndex, 8);
                double phoneAllowance = (double) tblJobDetails.getValueAt(selectedRowIndex, 9);
                double clothingAllowance = (double) tblJobDetails.getValueAt(selectedRowIndex, 10);

                // Set the data to text fields
                txtEmpIDforJob.setText(String.valueOf(employeeID));
                txtLNameJob.setText(lastName);
                txtFNameJob.setText(firstName);
                txtStatusJob.setText(status);
                txtPositionJob.setText(position);
                txtSupeJob.setText(supervisor);
                txtBasicSalary.setText(String.valueOf(basicSalary));
                txtRiceJob.setText(String.valueOf(riceSubsidy));
                txtPhoneJob.setText(String.valueOf(phoneAllowance));
                txtClothJob.setText(String.valueOf(clothingAllowance));
            }
    }//GEN-LAST:event_tblJobDetailsMouseClicked

    private void btnRefreshJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshJobActionPerformed
        // TODO add your handling code here:
        // Call the method to refresh job details data
        dataController.fetchDataJobDetailsFromDatabase();
        
    }//GEN-LAST:event_btnRefreshJobActionPerformed

    private void txtIDSearchAttendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDSearchAttendActionPerformed

          
    }//GEN-LAST:event_txtIDSearchAttendActionPerformed

    
    
    private void btnClearAttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearAttActionPerformed
        // TODO add your handling code here:
        txtIDSearchAttend.setText("");
        dateFromAttend.setDate(null);
        dateToAttend.setDate(null);
    }//GEN-LAST:event_btnClearAttActionPerformed

    
    private void dateFromAttendPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateFromAttendPropertyChange
        // TODO add your handling code here:

         
        
    }//GEN-LAST:event_dateFromAttendPropertyChange

    private void dateToAttendPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateToAttendPropertyChange
        // TODO add your handling code here:

          
        
    }//GEN-LAST:event_dateToAttendPropertyChange

    private void txtIDSearchAttendKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIDSearchAttendKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume(); // Ignore non-numeric characters
        }
    }//GEN-LAST:event_txtIDSearchAttendKeyTyped

    private void txtBasicSalaryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBasicSalaryKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '-' && c != '.') {
            evt.consume(); // Ignore non-numeric characters, hyphen, and decimal points
        }
    }//GEN-LAST:event_txtBasicSalaryKeyTyped

    private void txtPNumberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPNumberKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '-' && c != '.') {
            evt.consume(); // Ignore non-numeric characters, hyphen, and decimal points
        }
    }//GEN-LAST:event_txtPNumberKeyTyped

    private void txtSSSNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSSSNumActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSSSNumActionPerformed

    private void txtSSSNumKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSSSNumKeyTyped
        // TODO add your handling code here:
            char c = evt.getKeyChar();
    if (!Character.isDigit(c) && c != '-' && c != '.') {
        evt.consume(); // Ignore non-numeric characters, hyphen, and decimal points
    }
    }//GEN-LAST:event_txtSSSNumKeyTyped

    private void txtPhilhealthNumKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhilhealthNumKeyTyped
        // TODO add your handling code here:
            char c = evt.getKeyChar();
    if (!Character.isDigit(c) && c != '-' && c != '.') {
        evt.consume(); // Ignore non-numeric characters, hyphen, and decimal points
    }
    }//GEN-LAST:event_txtPhilhealthNumKeyTyped

    private void txtPagibigNumKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPagibigNumKeyTyped
        // TODO add your handling code here:
            char c = evt.getKeyChar();
    if (!Character.isDigit(c) && c != '-' && c != '.') {
        evt.consume(); // Ignore non-numeric characters, hyphen, and decimal points
    }
    }//GEN-LAST:event_txtPagibigNumKeyTyped

    private void txtTinNumKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTinNumKeyTyped
        // TODO add your handling code here:
            char c = evt.getKeyChar();
    if (!Character.isDigit(c) && c != '-' && c != '.') {
        evt.consume(); // Ignore non-numeric characters, hyphen, and decimal points
    }
    }//GEN-LAST:event_txtTinNumKeyTyped

    private void txtRiceSubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRiceSubActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRiceSubActionPerformed

    private void txtBSalaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBSalaryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBSalaryActionPerformed

    private void btnSavePayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavePayrollActionPerformed
        // TODO add your handling code here:
        try {
                MyConnection myConnection = MyConnection.getInstance();
                Connection connection = myConnection.connect();
                Statement stmt = connection.createStatement();
                
                // Get current date and time as a string
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = dateFormat.format(new Date());
                
                String id = txtEmpIDpayroll.getText();
                String LName = txtLNamePayroll.getText();
                String FName = txtFNamePayroll.getText();
                String Position = txtPositionPayroll.getText();
                String BSalary = txtBSalary.getText();
                String month = cmbMonth.getSelectedItem().toString();
                String period = cmbSalaryPeriod.getSelectedItem().toString();
                String year = cmbYear.getSelectedItem().toString();
                String days = txtWorkingDays.getText();
                String groIncome = lblSalary.getText();
                String rice = txtRiceSub.getText();
                String phone = txtPhoneAll.getText();
                String cloth = txtClothAll.getText();
                String tallowa = lbltAllowances.getText();
                String groSal = lblGrossSalary.getText();
                String philhealth = lblPhilhealth.getText();
                String sss = lblSSS.getText();
                String pagibig = lblPagibig.getText();
                String tdeduct = lblTDeductions.getText();
                String taxable = lblTaxableIncome.getText();
                String tax = lblWithholdingTax.getText();
                String netpay = lblNetpay.getText();
                
                String sql = "INSERT INTO payroll_report (employeeID, `Last Name`, `First Name`, Position, `Basic Salary`, Date, Month, `Salary Period`, Year, `Days Worked`, `Gross Income`, `Rice Subsidy`, `Phone Allowance`, `Clothing Allowance`, `Total Allowances`, `Gross Salary`, `Philhealth`, SSS, `Pagibig`, `Total Deductions`, `Taxable Income`, `Withholding Tax`, `Net Pay`) " +
                             "VALUES (" + id + ", '" + LName + "', '" + FName + "', '" + Position + "', '" + BSalary + "', '" + currentDate + "', '" + month + "','" + period + "','" + year + "', '" + days + "', '" + groIncome + "', '" + rice + "', '" + phone + "', '" + cloth + "', '" + tallowa + "', '" + groSal + "', '" + philhealth + "', '" + sss + "', '" + pagibig + "', '" + tdeduct + "', '" + taxable + "', '" + tax + "', '" + netpay + "')";

                // Execute the query
                stmt.executeUpdate(sql);

                JOptionPane.showMessageDialog(null, "Payslip generated successfully!");

                connection.close();
                
                
        }catch(SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error inserting data into the database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
        
    }//GEN-LAST:event_btnSavePayrollActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        txtSearchPayroll.setText("");
        txtEmpIDpayroll.setText("");
        txtPositionPayroll.setText("");
        txtLNamePayroll.setText("");
        txtFNamePayroll.setText("");
        cmbMonth.removeAllItems();
        cmbSalaryPeriod.removeAllItems();
        cmbYear.removeAllItems();
        DefaultTableModel payrollTableModel = (DefaultTableModel) tblJobPayroll.getModel();
        payrollTableModel.setRowCount(0);
        txtBSalary.setText("");
        txtWorkingDays.setText("");
        txtMinutes.setText("");
        txtRiceSub.setText("");
        txtPhoneAll.setText("");
        txtClothAll.setText("");
        lblSalary.setText("");
        lbltAllowances.setText("0");
        lblGrossSalary.setText("0");
        lblSSS.setText("0");
        lblPhilhealth.setText("0");
        lblPagibig.setText("0");
        lblTDeductions.setText("0");
        lblTaxableIncome.setText("0");
        lblWithholdingTax.setText("0");
        lblNetpay.setText("0");

    }//GEN-LAST:event_btnResetActionPerformed

    private void updateTable(String employeeID) {
        payrollTableModel.setRowCount(0); // Clear existing data from the table model

        // Perform the database query
        try {
            ResultSet resultSet = PayrollDataAccess.getEmployeeData(employeeID);

            // Populate the model with data from the result set
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getString("EmployeeID"));
                row.add(resultSet.getString("Last_Name"));
                row.add(resultSet.getString("First_Name"));
                row.add(resultSet.getString("Position"));
                row.add(resultSet.getString("Basic_Salary"));
                row.add(resultSet.getString("Rice_Subsidy"));
                row.add(resultSet.getString("Phone_Allowance"));
                row.add(resultSet.getString("Clothing_Allowance"));
                row.add(resultSet.getString("LogDate"));
                row.add(resultSet.getString("LogTime"));
                row.add(resultSet.getString("Status")); // Adding Status column
                payrollTableModel.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle any exceptions appropriately
        }
    }
       
    private void txtSearchPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchPayrollActionPerformed
        // TODO add your handling code here:
                String employeeID = txtSearchPayroll.getText().trim();

                // Perform database query and update the table
                updateTable(employeeID);

    }//GEN-LAST:event_txtSearchPayrollActionPerformed

    private int convertMonthNameToNumber(String monthName) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        try {
            Date date = monthFormat.parse(monthName);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.MONTH) + 1; // Adding 1 because Calendar.MONTH is zero-based
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the exception or display an error message
            return -1; // Return a placeholder value indicating an error
        }
    }

    private void updateTable(String employeeID, String salaryPeriod, String selectedMonth, String selectedYear) {
        payrollTableModel.setRowCount(0); // Clear existing data from the table model

        try {
            MyConnection myConnection = MyConnection.getInstance();
            Connection connection = myConnection.connect();

            // Convert selectedMonth to its numeric representation
            int month = convertMonthNameToNumber(selectedMonth);

            // Check if the conversion was successful
            if (month == -1) {
                // Handle the error, display an error message, or return from the method
                return;
            }

            // Collect unique dates first
            Set<String> uniqueDates = new HashSet<>();

            String uniqueDatesQuery = "SELECT DISTINCT LogDate FROM job_details " +
                    "LEFT JOIN attendance ON job_details.EmployeeID = attendance.ID " +
                    "WHERE job_details.EmployeeID = ? " +
                    "AND MONTH(LogDate) = ? " +
                    "AND YEAR(LogDate) = ? " +
                    "AND ((DAY(LogDate) <= 15 AND ? = 'Salary Period: 1 - 15') OR (DAY(LogDate) > 15 AND ? = 'Salary Period: 16 - end'))";

            PreparedStatement uniqueDatesStatement = connection.prepareStatement(uniqueDatesQuery);
            uniqueDatesStatement.setString(1, employeeID);
            uniqueDatesStatement.setInt(2, month);
            uniqueDatesStatement.setInt(3, Integer.parseInt(selectedYear));
            uniqueDatesStatement.setString(4, salaryPeriod);
            uniqueDatesStatement.setString(5, salaryPeriod);

            ResultSet uniqueDatesResultSet = uniqueDatesStatement.executeQuery();

            while (uniqueDatesResultSet.next()) {
                uniqueDates.add(uniqueDatesResultSet.getString("LogDate"));
            }

            // Populate the model with data from the result set
            String query = "SELECT job_details.EmployeeID, Last_Name, First_Name, Position, Basic_Salary, Rice_Subsidy, Phone_Allowance,Clothing_Allowance, LogDate, LogTime, attendance.Status " +
                    "FROM job_details " +
                    "LEFT JOIN attendance ON job_details.EmployeeID = attendance.ID " +
                    "WHERE job_details.EmployeeID = ? " +
                    "AND MONTH(LogDate) = ? " +
                    "AND YEAR(LogDate) = ? " +
                    "AND ((DAY(LogDate) <= 15 AND ? = 'Salary Period: 1 - 15') OR (DAY(LogDate) > 15 AND ? = 'Salary Period: 16 - end'))";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, employeeID);
            statement.setInt(2, month);
            statement.setInt(3, Integer.parseInt(selectedYear));
            statement.setString(4, salaryPeriod);
            statement.setString(5, salaryPeriod);

            ResultSet resultSet = statement.executeQuery();

            // Populate the model with data from the result set
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getString("EmployeeID"));
                row.add(resultSet.getString("Last_Name"));
                row.add(resultSet.getString("First_Name"));
                row.add(resultSet.getString("Position"));
                row.add(resultSet.getString("Basic_Salary"));
                row.add(resultSet.getString("Rice_Subsidy"));
                row.add(resultSet.getString("Phone_Allowance"));
                row.add(resultSet.getString("Clothing_Allowance"));
                row.add(resultSet.getString("LogDate"));
                row.add(resultSet.getString("LogTime"));
                row.add(resultSet.getString("Status")); // Adding Status column
                payrollTableModel.addRow(row);
            }

            // Debug: Print unique dates
            System.out.println("Unique Dates: " + uniqueDates);

            // Calculate working days
            int workingDays = PayrollCalculator.getWorkingDays(uniqueDates, salaryPeriod);

            // Debug: Print working days count
            System.out.println("Working Days Count: " + workingDays);

            txtWorkingDays.setText(String.valueOf(workingDays));




        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle any exceptions appropriately
        }
    }

    private void btnRetrieveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRetrieveActionPerformed
    String employeeID = txtSearchPayroll.getText().trim();
    String salaryPeriod = (String) cmbSalaryPeriod.getSelectedItem();
    String selectedMonth = (String) cmbMonth.getSelectedItem();
    String selectedYear = (String) cmbYear.getSelectedItem();

    
        // Perform database query and update the table based on selected options
        updateTable(employeeID, salaryPeriod, selectedMonth, selectedYear);
      
        
    }//GEN-LAST:event_btnRetrieveActionPerformed

    private void tblJobPayrollMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblJobPayrollMouseClicked
        // TODO add your handling code here:
        int selectedRow = tblJobPayroll.getSelectedRow();

    if (selectedRow != -1) {
        String employeeID = tblJobPayroll.getValueAt(selectedRow, 0).toString();
        String lastName = tblJobPayroll.getValueAt(selectedRow, 1).toString();
        String firstName = tblJobPayroll.getValueAt(selectedRow, 2).toString();
        String position = tblJobPayroll.getValueAt(selectedRow, 3).toString();
        String basicSalary = tblJobPayroll.getValueAt(selectedRow, 4).toString();
        String riceSubsidy = tblJobPayroll.getValueAt(selectedRow, 5).toString();
        String phoneAllowance = tblJobPayroll.getValueAt(selectedRow, 6).toString();
        String clothingAllowance = tblJobPayroll.getValueAt(selectedRow, 7).toString();

        // Set data to text fields
        txtEmpIDpayroll.setText(employeeID);
        txtLNamePayroll.setText(lastName);
        txtFNamePayroll.setText(firstName);
        txtPositionPayroll.setText(position);
        txtBSalary.setText(basicSalary);
        txtRiceSub.setText(riceSubsidy);
        txtPhoneAll.setText(phoneAllowance);
        txtClothAll.setText(clothingAllowance);
    }
    }//GEN-LAST:event_tblJobPayrollMouseClicked

    private void cmbYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbYearActionPerformed

    }//GEN-LAST:event_cmbYearActionPerformed

    private void txtIDSearchAttendKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIDSearchAttendKeyReleased
        // TODO add your handling code here:
        String searchString = txtIDSearchAttend.getText();
        if (!searchString.matches("\\d*")) {
        JOptionPane.showMessageDialog(null, "Please enter numeric characters only.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        txtIDSearchAttend.setText(""); // Clear the text field
        return;
    }
        Date searchD1 = dateFromAttend.getDate();
        Date searchD2 = dateToAttend.getDate();
        
        try {
            search(searchString, searchD1, searchD2);
        } catch (SQLException ex) {
            Logger.getLogger(AdminPortal.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_txtIDSearchAttendKeyReleased

    // Method to retrieve the total sum of allowances from the database
    private String getTotalAllowancesFromDatabase(String id) {
        String totalAllowances = "";
        try {
            MyConnection myConnection = MyConnection.getInstance();
            Connection connection = myConnection.connect();
            String query = "SELECT `Rice_Subsidy`, `Phone_Allowance`, `Clothing_Allowance` FROM job_details WHERE ID = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Retrieve individual allowances from the ResultSet
                float riceSubsidy = Float.parseFloat(rs.getString("Rice_Subsidy").replace(",", ""));
                float phoneAllowance = Float.parseFloat(rs.getString("Phone_Allowance").replace(",", ""));
                float clothingAllowance = Float.parseFloat(rs.getString("Clothing_Allowance").replace(",", ""));

                // Calculate the total sum of allowances
                float totalSum = riceSubsidy + phoneAllowance + clothingAllowance;
                totalAllowances = String.format("%.2f", totalSum);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception as needed
        }
        return totalAllowances;
    }

    private void btnCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateActionPerformed
        // TODO add your handling code here:
    String totalHoursWorkedStr = txtMinutes.getText().trim();

    // Check if the input is numeric
    if (!totalHoursWorkedStr.matches("\\d+")) {
        JOptionPane.showMessageDialog(null, "Total hours worked must be a number without decimal points.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String salaryText = txtBSalary.getText().toString().replace(",", "");
    float basicSalary = Float.parseFloat(salaryText);
    String salaryString = Float.toString(basicSalary);
    
    // Parse total hours worked to integer
    int totalHoursWorked = Integer.parseInt(totalHoursWorkedStr);


    // Assuming total weekdays in a month, office hours per day, and minutes per hour are provided
    int totalWeekdaysInMonth = 20; // Assuming 20 weekdays in a month
    int officeHoursPerDay = 8; // Assuming 8 hours per day
    int minutesPerHour = 60;

    // Calculate per minute salary
    float perMinuteSalary = basicSalary / (totalWeekdaysInMonth * officeHoursPerDay * minutesPerHour);

    // Calculate salary
    float salary = totalHoursWorked * perMinuteSalary;
    DecimalFormat decimalFormat4 = new DecimalFormat("#,###.00");
    String formattedNumberSal = decimalFormat4.format(salary);
    lblSalary.setText(formattedNumberSal);
    
    //Calculate Total Allowances
    // Assuming txtRiceSub, txtPhoneAll, and txtClothAll are text fields containing allowance values
    String riceSubsidyText = txtRiceSub.getText().toString().replace(",", "");
    String phoneAllowanceText = txtPhoneAll.getText().toString().replace(",", "");
    String clothingAllowanceText = txtClothAll.getText().toString().replace(",", "");

    // Parse allowance strings to float
    float riceSubsidy = Float.parseFloat(riceSubsidyText);
    float phoneAllowance = Float.parseFloat(phoneAllowanceText);
    float clothingAllowance = Float.parseFloat(clothingAllowanceText);

    // Calculate total allowances
    float totalAllowances = riceSubsidy + phoneAllowance + clothingAllowance;

    // Convert total allowances to string for displaying in label
    String totalAllowancesString = Float.toString(totalAllowances);

    // Set the total allowances string to the label
    lbltAllowances.setText(totalAllowancesString);
    
    // Calculate Gross Salary
    float grossSalary = salary + totalAllowances;
    DecimalFormat decimalFormat3 = new DecimalFormat("#,###.00");
    String formattedNumberGS = decimalFormat3.format(grossSalary);
    lblGrossSalary.setText(formattedNumberGS);
    
    String sssContributionStr = calculateSSSContribution(basicSalary);
    float sssContribution = Float.parseFloat(sssContributionStr);
    DecimalFormat decimalFormat6 = new DecimalFormat("#,###.00");
    String formattedNumberSSS = decimalFormat6.format(sssContribution);
    lblSSS.setText(formattedNumberSSS);
    
    String philHealthContributionStr = calculatePhilHealthContribution(basicSalary);
    float philHealthContribution = Float.parseFloat(philHealthContributionStr);
    DecimalFormat decimalFormat7 = new DecimalFormat("#,###.00");
    String formattedNumberPH = decimalFormat7.format(philHealthContribution);
    lblPhilhealth.setText(formattedNumberPH);
                    
    String pagIBIGContributionStr = calculatePagIBIGContribution(basicSalary);
    float pagIBIGContribution = Float.parseFloat(pagIBIGContributionStr);
    DecimalFormat decimalFormat8 = new DecimalFormat("#,###.00");
    String formattedNumberPG = decimalFormat8.format(pagIBIGContribution);
    lblPagibig.setText(formattedNumberPG);    


    // Calculate Total Deductions
    float totalDeductions = sssContribution + philHealthContribution + pagIBIGContribution;
    // Convert total allowances to string for displaying in label
    String totalDeductString = Float.toString(totalDeductions).replace(",", "");
    lblTDeductions.setText(totalDeductString);
    
    // Calculate Taxable Income
    float taxableIncome = grossSalary - totalDeductions;
    // Convert total allowances to string for displaying in label
    String taxableString = Float.toString(taxableIncome).replace(",", "");
    lblTaxableIncome.setText(taxableString);
    
    // Calculate Withholding Tax based on Taxable Income
    float withholdingTax = calculateWithholdingTax(taxableIncome);
    DecimalFormat decimalFormat2 = new DecimalFormat("#,###.00");
    String formattedNumberTIN = decimalFormat2.format(withholdingTax);
            // Check if withholdingTax is zero and adjust the format accordingly
            if (withholdingTax == 0) {
                formattedNumberTIN = "0.00";
            }

            lblWithholdingTax.setText(formattedNumberTIN);
    
    float totalNetSal = taxableIncome - withholdingTax;
    DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
    String formattedNumber = decimalFormat.format(totalNetSal);
    lblNetpay.setText(formattedNumber);        
    
    
   
            
    }//GEN-LAST:event_btnCalculateActionPerformed

    private void txtClear1PayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClear1PayrollActionPerformed
        // TODO add your handling code here:
        txtSearchPayroll.setText("");
        txtEmpIDpayroll.setText("");
        txtPositionPayroll.setText("");
        txtLNamePayroll.setText("");
        txtFNamePayroll.setText("");
        cmbMonth.removeAllItems();
        cmbSalaryPeriod.removeAllItems();
        cmbYear.removeAllItems();
        DefaultTableModel payrollTableModel = (DefaultTableModel) tblJobPayroll.getModel();
        payrollTableModel.setRowCount(0);
        txtBSalary.setText("");
        txtWorkingDays.setText("");
        txtMinutes.setText("");
        txtRiceSub.setText("");
        txtPhoneAll.setText("");
        txtClothAll.setText("");
    }//GEN-LAST:event_txtClear1PayrollActionPerformed

    private void btnPayslipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayslipActionPerformed
        // TODO add your handling code here:
        // Create an instance of the payslip panel
        payslip payslipPanel = new payslip(untxt);
        
        // Create a dialog to display the payslip panel
        JDialog dialog = new JDialog();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Close dialog on close
        dialog.getContentPane().add(payslipPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center the dialog on the screen
        dialog.setVisible(true);
    }//GEN-LAST:event_btnPayslipActionPerformed
 
    private String calculateSSSContribution(float basicSalary) {
    String sssContribution = "";

    if (basicSalary < 3250) {
        sssContribution = "135.00";
    } else if (basicSalary >= 3250 && basicSalary <= 24750) {
        float[] salaryRanges = {3250, 3750, 4250, 4750, 5250, 5750, 6250, 6750, 7250, 7750, 8250, 8750, 9250,
                9750, 10250, 10750, 11250, 11750, 12250, 12750, 13250, 13750, 14250, 14750, 15250, 15750, 16250,
                16750, 17250, 17750, 18250, 18750, 19250, 19750, 20250, 20750, 21250, 21750, 22250, 22750, 23250,
                23750, 24250, 24750};

        float[] contributions = {135.00f, 157.50f, 180.00f, 202.50f, 225.00f, 247.50f, 270.00f, 292.50f, 315.00f,
                337.50f, 360.00f, 382.50f, 405.00f, 427.50f, 450.00f, 472.50f, 495.00f, 517.50f, 540.00f, 562.50f,
                585.00f, 607.50f, 630.00f, 652.50f, 675.00f, 697.50f, 720.00f, 742.50f, 765.00f, 787.50f, 810.00f,
                832.50f, 855.00f, 877.50f, 900.00f, 922.50f, 945.00f, 967.50f, 990.00f, 1012.50f, 1035.00f, 1057.50f,
                1080.00f, 1102.50f, 1125.00f};

        for (int i = 0; i < salaryRanges.length; i++) {
            if (basicSalary >= salaryRanges[i] && basicSalary < salaryRanges[i + 1]) {
                sssContribution = String.valueOf(contributions[i]);
                break;
            }
        }
    } else {
        sssContribution = "1125.00";
    }

    return sssContribution;
    
}  
    
    private String calculatePhilHealthContribution(float basicSalary) {
    String philHealthContribution = "";
    
    float premiumRate = 0.03f; // Premium rate is 3%
    float monthlyPremium = 0;

    if (basicSalary <= 10000) {
        monthlyPremium = 300;
    } else if (basicSalary > 10000 && basicSalary <= 59999.99) {
        monthlyPremium = basicSalary * premiumRate;
        if (monthlyPremium < 300) {
            monthlyPremium = 300;
        } else if (monthlyPremium > 1800) {
            monthlyPremium = 1800;
        }
    } else if (basicSalary >= 60000) {
        monthlyPremium = 1800;
    }
    
    float employeeShare = monthlyPremium * 0.5f; // Employee's share is 50%

    philHealthContribution = String.valueOf(employeeShare);

    return philHealthContribution;
}
    
    private String calculatePagIBIGContribution(float basicSalary) {
    String pagIBIGContribution = "";
    
    float employeeContributionRate;
    float employerContributionRate;
    
    if (basicSalary >= 1000 && basicSalary <= 1500) {
        employeeContributionRate = 0.01f; // 1%
        employerContributionRate = 0.02f; // 2%
    } else {
        employeeContributionRate = 0.02f; // 2%
        employerContributionRate = 0.02f; // 2%
    }
    
    float totalContribution = basicSalary * (employeeContributionRate + employerContributionRate);
    
    if (totalContribution > 100) {
        totalContribution = 100; // Maximum contribution amount is 100
    }
    
    pagIBIGContribution = String.valueOf(totalContribution);

    return pagIBIGContribution;
}
    
    private float calculateWithholdingTax(float taxableIncome) {
    float withholdingTax = 0;

    if (taxableIncome <= 20832) {
        withholdingTax = 0; // No withholding tax
    } else if (taxableIncome <= 33333) {
        withholdingTax = (taxableIncome - 20833) * 0.20f;
    } else if (taxableIncome <= 66667) {
        withholdingTax = 2500 + (taxableIncome - 33333) * 0.25f;
    } else if (taxableIncome <= 166667) {
        withholdingTax = 10833 + (taxableIncome - 66667) * 0.30f;
    } else if (taxableIncome <= 666667) {
        withholdingTax = 40833.33f + (taxableIncome - 166667) * 0.32f;
    } else {
        withholdingTax = 200833.33f + (taxableIncome - 666667) * 0.35f;
    }

    return withholdingTax;
}
    
    
        
        
        
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        e.printStackTrace(); // Add appropriate handling for ClassNotFoundException
    }

    /* Set the Nimbus look and feel */
    try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        ex.printStackTrace(); // Add appropriate handling for the LookAndFeel exceptions
    }

    SwingUtilities.invokeLater(AdminPortal::new);
    }
    
    //Not finished
    private ImageIcon format = null;
    String filename = null;
    byte[] person_image = null;

    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddEmployee;
    private javax.swing.JButton btnAttachPic;
    private javax.swing.JButton btnBonusesdeductions;
    private javax.swing.JButton btnCalculate;
    private javax.swing.JButton btnCheckIn;
    private javax.swing.JButton btnCheckOut;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClearAtt;
    private javax.swing.JButton btnClearJob;
    private javax.swing.JButton btnEmpDash;
    private javax.swing.JButton btnEmpDetails;
    private javax.swing.JButton btnEmpTimesheet;
    private javax.swing.JButton btnEmployeeInformation;
    private javax.swing.JButton btnJobDetails;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPayroll;
    private javax.swing.JButton btnPayslip;
    private javax.swing.JButton btnRefreshJob;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnRetrieve;
    private javax.swing.JButton btnSavePayroll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdateJob;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JComboBox<String> cmbMonth;
    private javax.swing.JComboBox<String> cmbSalaryPeriod;
    private javax.swing.JComboBox<String> cmbYear;
    private com.toedter.calendar.JDateChooser dateBirthday;
    private com.toedter.calendar.JDateChooser dateFromAttend;
    private com.toedter.calendar.JDateChooser dateToAttend;
    private javax.swing.JLabel img;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblCheckIn;
    private javax.swing.JLabel lblCheckOut;
    private javax.swing.JLabel lblCurrentDate;
    private javax.swing.JLabel lblCurrentTime;
    private javax.swing.JLabel lblEmployeeName;
    private javax.swing.JLabel lblEmployeeRole;
    private javax.swing.JLabel lblGreetings;
    private javax.swing.JLabel lblGross;
    private javax.swing.JLabel lblGross1;
    private javax.swing.JLabel lblGrossSalary;
    private javax.swing.JLabel lblHoursWork;
    private javax.swing.JLabel lblMinutes;
    private javax.swing.JLabel lblNetpay;
    private javax.swing.JLabel lblNumDays;
    private javax.swing.JLabel lblOvertime;
    private javax.swing.JLabel lblPagibig;
    private javax.swing.JLabel lblPhilhealth;
    private javax.swing.JLabel lblSSS;
    private javax.swing.JLabel lblSalary;
    private javax.swing.JLabel lblTDeductions;
    private javax.swing.JLabel lblTaxableIncome;
    private javax.swing.JLabel lblUndertime;
    private javax.swing.JLabel lblWithholdingTax;
    private javax.swing.JLabel lbltAllowances;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel p1Dashboard;
    private javax.swing.JPanel p1EmpInformation;
    private javax.swing.JPanel p2EmpDet;
    private javax.swing.JPanel p2JobDetails;
    private javax.swing.JPanel p3BonusesDeductions;
    private javax.swing.JPanel p3EmpTime;
    private javax.swing.JPanel p4Payroll;
    private javax.swing.JDesktopPane photopanel;
    private javax.swing.JPanel pnlAttendance;
    private javax.swing.JPanel pnlCenter;
    private javax.swing.JPanel pnlProfile;
    private javax.swing.JPanel pnlbutton;
    private javax.swing.JTable tblAttend;
    private javax.swing.JTable tblEmployeeInformation;
    private javax.swing.JTable tblJobDetails;
    private javax.swing.JTable tblJobPayroll;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextArea txtAdd;
    private javax.swing.JScrollPane txtAddress;
    private javax.swing.JTextField txtBSalary;
    private javax.swing.JTextField txtBasicSalary;
    private javax.swing.JButton txtClear1Payroll;
    private javax.swing.JTextField txtClothAll;
    private javax.swing.JTextField txtClothJob;
    private javax.swing.JTextField txtEmpIDforJob;
    private javax.swing.JTextField txtEmpIDpayroll;
    private javax.swing.JTextField txtFNameJob;
    private javax.swing.JTextField txtFNamePayroll;
    private javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtIDSearch;
    private javax.swing.JTextField txtIDSearchAttend;
    private javax.swing.JTextField txtLNameJob;
    private javax.swing.JTextField txtLNamePayroll;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtMinutes;
    private javax.swing.JTextField txtPNumber;
    private javax.swing.JTextField txtPagibigNum;
    private javax.swing.JTextField txtPhilhealthNum;
    private javax.swing.JTextField txtPhoneAll;
    private javax.swing.JTextField txtPhoneJob;
    private javax.swing.JTextField txtPositionJob;
    private javax.swing.JTextField txtPositionPayroll;
    private javax.swing.JTextField txtRiceJob;
    private javax.swing.JTextField txtRiceSub;
    private javax.swing.JTextField txtSSSNum;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSearchPayroll;
    private javax.swing.JTextField txtStatusJob;
    private javax.swing.JTextField txtSupeJob;
    private javax.swing.JTextField txtTinNum;
    private javax.swing.JTextField txtWorkingDays;
    // End of variables declaration//GEN-END:variables



}
