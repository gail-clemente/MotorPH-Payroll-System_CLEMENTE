/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package payroll;

//Interface for Employee Details

import java.util.Date;

public interface EmployeeDetails {
        String getLastName();
        String getFirstName();
        Date getBirthday();
        String getAddress();
        String getPhoneNum();
        String getSssNum();
        String getPhilhealthNum();
        String getPagibigNum();
        String getTinNum();
}
