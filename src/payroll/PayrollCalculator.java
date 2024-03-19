package payroll;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PayrollCalculator {
    
    
    public static Set<String> getUniqueDates(ResultSet resultSet) throws SQLException {
            Set<String> uniqueDates = new HashSet<>();

            while (resultSet.next()) {
                String logDate = resultSet.getString("LogDate");
                uniqueDates.add(logDate);
            }

            return uniqueDates;
    }

    private static boolean isInSalaryPeriod(String logDate, String salaryPeriod) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(logDate, formatter);

        if ("Salary Period: 1 - 15".equals(salaryPeriod)) {
            return date.getDayOfMonth() <= 15;
        } else if ("Salary Period: 16 - end".equals(salaryPeriod)) {
            return date.getDayOfMonth() > 15 && date.getDayOfMonth() <= date.lengthOfMonth();
        }

        // Handle other salary period options if needed

        return false;
    }

    public static int getWorkingDays(Set<String> uniqueDates, String salaryPeriod) {
        int workingDays = 0;

        for (String logDate : uniqueDates) {
            if (isInSalaryPeriod(logDate, salaryPeriod)) {
                workingDays++;
            }
        }

        return workingDays;
    }

    public static int calculateOvertime(ResultSet resultSet, String salaryPeriod) throws SQLException {
        int overtimeMinutes = 0;

        // Standard working hours
        LocalTime standardStartTime = LocalTime.parse("08:30:00");
        LocalTime standardEndTime = LocalTime.parse("17:30:00");

        while (resultSet.next()) {
            Time logTime = resultSet.getTime("LogTime");
            LocalTime actualEndTime = logTime.toLocalTime();

            // Check if the actual end time is after the standard end time
            if (actualEndTime.isAfter(standardEndTime)) {
                // Calculate the difference in minutes
                overtimeMinutes += calculateDurationInMinutes(standardEndTime, actualEndTime);
            }
        }

        return overtimeMinutes;
    }

    public static int calculateTotalWorkingDays(Set<String> uniqueDates) throws ParseException {
        int totalWorkingDays = 0;

        for (String logDate : uniqueDates) {
            if (isWorkingDay(logDate)) {
                totalWorkingDays++;
            }
        }

        return totalWorkingDays;
    }
    
    private static boolean isWorkingDay(String logDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(logDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Considering Monday to Friday as working days
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY;
    }

    private static int calculateDurationInMinutes(LocalTime startTime, LocalTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        return (int) duration.toMinutes();
    }

    

    public int calculateMinutes(String employeeID, String selectedOption, String selectedYear, String selectedMonth) throws SQLException {
    MyConnection myConnection = MyConnection.getInstance();
    try (Connection connection = myConnection.connect()) {
        int minutesWorked = 0;

        // Determine the start and end dates for the selected salary period, year, and month
        LocalDate startDate;
        LocalDate endDate;

        // Convert the selected month name to its numeric representation
        int monthValue = Month.valueOf(selectedMonth.toUpperCase()).getValue();

        if ("Salary Period: 1 - 15".equals(selectedOption)) {
            startDate = LocalDate.of(Integer.parseInt(selectedYear), monthValue, 1);
            endDate = LocalDate.of(Integer.parseInt(selectedYear), monthValue, 15);
        } else if ("Salary Period: 16 - end".equals(selectedOption)) {
            startDate = LocalDate.of(Integer.parseInt(selectedYear), monthValue, 16);
            endDate = LocalDate.of(Integer.parseInt(selectedYear), monthValue, Month.of(monthValue).maxLength());
        } else {
            // Handle other salary period options if needed
            throw new IllegalArgumentException("Invalid salary period selected");
        }

        // Construct the SQL query with a join between job_details and attendance tables
        String query = "SELECT SUM(diff_minutes) AS total_minutes " +
                       "FROM ( " +
                       "    SELECT TIMESTAMPDIFF(MINUTE, MIN(a.LogTime), MAX(a.LogTime)) AS diff_minutes " +
                       "    FROM job_details j " +
                       "    JOIN attendance a ON j.EmployeeID = a.ID " +
                       "    WHERE j.EmployeeID = ? " +
                       "        AND a.LogDate BETWEEN ? AND ? " +
                       "        AND (a.Status = 'Logged in' OR a.Status = 'Logged out') " +
                       "    GROUP BY a.LogDate " +
                       ") AS temp";
        
        // Prepare the statement and set parameters
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, employeeID);
            statement.setDate(2, java.sql.Date.valueOf(startDate));
            statement.setDate(3, java.sql.Date.valueOf(endDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    minutesWorked = resultSet.getInt(1);
                }
            }
        }
        return minutesWorked;
        
    } catch (SQLException ex) {
        // Handle exceptions appropriately
        throw ex;
    }
}








}
    
    
    

