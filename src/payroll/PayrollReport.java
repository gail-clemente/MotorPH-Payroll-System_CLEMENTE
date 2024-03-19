package payroll;

import java.util.Date;

public class PayrollReport {
    private String position;
    private double basicSalary;
    private Date date;
    private String month;
    private String salaryPeriod;
    private int year;
    private int daysWorked;
    private double grossIncome;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double totalAllowances;
    private double philhealth;
    private double sss;
    private double pagibig;
    private double totalDeductions;
    private double taxableIncome;
    private double withholdingTax;
    private double netPay;
    

    // Constructor
    public PayrollReport(String position, double basicSalary, Date date, String month, String salaryPeriod,
                         int year, int daysWorked, double grossIncome, double riceSubsidy, double phoneAllowance,
                         double clothingAllowance, double totalAllowances, double philhealth, double sss, double pagibig,
                         double totalDeductions, double taxableIncome, double withholdingTax, double netPay) {
        this.position = position;
        this.basicSalary = basicSalary;
        this.date = date;
        this.month = month;
        this.salaryPeriod = salaryPeriod;
        this.year = year;
        this.daysWorked = daysWorked;
        this.grossIncome = grossIncome;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.totalAllowances = totalAllowances;
        this.philhealth = philhealth;
        this.sss = sss;
        this.pagibig = pagibig;
        this.totalDeductions = totalDeductions;
        this.taxableIncome = taxableIncome;
        this.withholdingTax = withholdingTax;
        this.netPay = netPay;
    }

    // Getters for all fields
    public String getPosition() {
        return position;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public Date getDate() {
        return date;
    }

    public String getMonth() {
        return month;
    }

    public String getSalaryPeriod() {
        return salaryPeriod;
    }

    public int getYear() {
        return year;
    }

    public int getDaysWorked() {
        return daysWorked;
    }

    public double getGrossIncome() {
        return grossIncome;
    }

    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }

    public double getTotalAllowances() {
        return totalAllowances;
    }

    public double getPhilhealth() {
        return philhealth;
    }

    public double getSss() {
        return sss;
    }

    public double getPagibig() {
        return pagibig;
    }

    public double getTotalDeductions() {
        return totalDeductions;
    }

    public double getTaxableIncome() {
        return taxableIncome;
    }

    public double getWithholdingTax() {
        return withholdingTax;
    }

    public double getNetPay() {
        return netPay;
    }
}
