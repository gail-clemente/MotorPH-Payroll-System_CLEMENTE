package payroll;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Bal_Attendance {
    
public class Bal_Att {


}
    public List<Bean_Attendance> searchID(String keyword, Date date1, Date date2) throws SQLException {
        MyConnection myConnection = MyConnection.getInstance();
        Connection connection = myConnection.connect();
        
        List<Bean_Attendance> list = new ArrayList<>();
        try {
            String query = "SELECT * FROM attendance WHERE ID = ?";
            // Check if both dates are not null, then add date range conditions to the query
            if (date1 != null && date2 != null) {
                query += " AND LogDate BETWEEN ? AND ?";
            }

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, keyword);

            // Set date parameters if both dates are not null
            if (date1 != null && date2 != null) {
                ps.setDate(2, new java.sql.Date(date1.getTime()));
                ps.setDate(3, new java.sql.Date(date2.getTime()));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID");
                Date logdate = rs.getDate("LogDate");
                Time logtime = rs.getTime("LogTime");
                String status = rs.getString("Status");

                Bean_Attendance bean = new Bean_Attendance(id, logdate, logtime, status);
                list.add(bean);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error searching employees: " + e.getMessage());
        }
        return list;
    }
    
    
    public List<Bean_Attendance> loadData_Att() throws SQLException {
        List<Bean_Attendance> list = new ArrayList<Bean_Attendance>();
        MyConnection myConnection = MyConnection.getInstance();
        Connection connection = myConnection.connect();
        
        try {
            String query = "select * from attendance"; 
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                int id = rs.getInt("ID");
                Date logdate = rs.getDate("LogDate");
                Time logtime = rs.getTime("LogTime");
                String status = rs.getString("Status");
                
                
                Bean_Attendance bean = new Bean_Attendance(id, logdate, logtime, status);
                list.add(bean);
                
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, ""+e);
        }
        return list;
    }
    
public int calculateNumberOfDays(List<Bean_Attendance> filteredData) {
    Set<Date> uniqueDates = new HashSet<>();
    for (Bean_Attendance bean : filteredData) {
        uniqueDates.add(bean.getLogdate());
    }
    return uniqueDates.size();
}

public String calculateTotalHoursWorked(List<Bean_Attendance> filteredData) {
    long totalMillisWorked = 0;
    for (Bean_Attendance bean : filteredData) {
        if (bean.getStatus().equalsIgnoreCase("Logged in")) {
            Optional<Bean_Attendance> loggedOutRecord = filteredData.stream()
                    .filter(b -> b.getLogdate().equals(bean.getLogdate()) && b.getStatus().equalsIgnoreCase("Logged out"))
                    .findFirst();

            if (loggedOutRecord.isPresent()) {
                Time logInTime = bean.getLogtime();
                Time logOutTime = loggedOutRecord.get().getLogtime();
                long millisWorked = calculateMillisWorked(logInTime, logOutTime);
                totalMillisWorked += millisWorked;
            }
        }
    }
    long hours = totalMillisWorked / (60 * 60 * 1000); // Convert milliseconds to hours
    long minutes = (totalMillisWorked % (60 * 60 * 1000)) / (60 * 1000); // Convert remaining milliseconds to minutes
    return hours + " hrs " + minutes + " mins";
}

private long calculateMillisWorked(Time logInTime, Time logOutTime) {
    long standardWorkHoursMillis = 8 * 60 * 60 * 1000; // 8 hours in milliseconds
    long lunchBreakMillis = 60 * 60 * 1000; // 1 hour lunch break in milliseconds

    long totalMillisWorked = logOutTime.getTime() - logInTime.getTime() - lunchBreakMillis;
    totalMillisWorked = Math.max(totalMillisWorked, 0); // Ensure totalMillisWorked is non-negative
    totalMillisWorked = Math.min(totalMillisWorked, standardWorkHoursMillis); // Ensure totalMillisWorked doesn't exceed standard work hours

    return totalMillisWorked;
}


public void updateTotalMinutesWorkedLabel(JLabel lblHoursWork, JLabel lblMinutes) {
        // Assuming you have access to lblHoursWork and jLabel10TMW from this method's context

        // Get the total hours worked string from lblHoursWork
        String totalHoursWorked = lblHoursWork.getText();

        // Call the method to convert total hours worked to minutes
        int totalMinutesWorked = convertTotalHoursToMinutes(totalHoursWorked);

        // Set the result to jLabel10TMW
        lblMinutes.setText(String.valueOf(totalMinutesWorked));
    }

    // Method for converting total hours worked to minutes
    public int convertTotalHoursToMinutes(String totalHoursWorked) {
        // Parse the total hours worked string to extract hours and minutes
        String[] hoursAndMinutes = totalHoursWorked.split("\\s+");
        int hours = Integer.parseInt(hoursAndMinutes[0]); // Extract hours
        int minutes = Integer.parseInt(hoursAndMinutes[2]); // Extract minutes

        // Convert hours to minutes and add to the existing minutes
        int totalMinutesWorked = hours * 60 + minutes;

        return totalMinutesWorked;
    }
 
     
    

    public String calculateOverallUndertime(List<Bean_Attendance> list) {
        int totalUndertimeMinutes = 0;
    for (Bean_Attendance bean : list) {
        if ("Logged in".equals(bean.getStatus())) {
            // Assuming the standard start time threshold is 08:10 AM
            LocalTime logTime = bean.getLogtime().toLocalTime();
            LocalTime startTimeThreshold = LocalTime.of(8, 10); // Standard start time threshold is 8:10 AM

            // Check if logged in time is earlier than the standard start time threshold
            if (logTime.isBefore(startTimeThreshold)) {
                totalUndertimeMinutes += calculateMinutesDifference(logTime, startTimeThreshold);
            }
        }
    }

    // Convert totalUndertimeMinutes to hours and minutes format (HH:MM)
    int hours = totalUndertimeMinutes / 60;
    int minutes = totalUndertimeMinutes % 60;
    return String.format("%02d hrs %02d mins", hours, minutes);
    }

    public String calculateOverallOvertime(List<Bean_Attendance> list) {
    int totalOvertimeMinutes = 0;
    for (Bean_Attendance bean : list) {
        Time logTime = bean.getLogtime();
        if (logTime != null) {
            int logHour = logTime.getHours();
            int logMinute = logTime.getMinutes();
            if (logHour > 17 || (logHour == 17 && logMinute >= 30)) {
                int overtimeMinutes = (logHour - 17) * 60 + logMinute - 30;
                totalOvertimeMinutes += overtimeMinutes;
            }
        }
    }
    
    int totalOvertimeHours = totalOvertimeMinutes / 60;
    int remainingOvertimeMinutes = totalOvertimeMinutes % 60;
    
     return String.format("%02d hrs %02d mins", totalOvertimeHours, remainingOvertimeMinutes);
}


    // Helper method to calculate the difference in minutes between two LocalTime objects
    private int calculateMinutesDifference(LocalTime time1, LocalTime time2) {
        // Calculate the difference in minutes
        return (int) java.time.Duration.between(time1, time2).toMinutes();
    }

    private Time getLoggedInTime(List<Bean_Attendance> list, Date logDate, int id) {
        for (Bean_Attendance bean : list) {
            if (bean.getLogdate().equals(logDate) && bean.getId() == id && bean.getStatus().equals("Logged in")) {
                return bean.getLogtime();
            }
        }
        return null;
    }

    
    

}
