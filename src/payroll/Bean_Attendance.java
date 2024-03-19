package payroll;

import java.sql.Time;
import java.util.Date;

public class Bean_Attendance {
    private int id;
    private Date logdate;
    private Time logtime;
    private String status;
    
    public Bean_Attendance( int id, Date logdate, Time logtime, String status) {
        
        this.id = id;
        this.logdate = logdate;
        this.logtime = logtime;
        this.status = status;
    }
     
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getLogdate() {
        return logdate;
    }

    public void setLogdate(Date logdate) {
        this.logdate = logdate;
    }
    
    public Time getLogtime() {
        return logtime;
    }

    public void setLogtime(Time logtime) {
        this.logtime = logtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
