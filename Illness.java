package clinicsystem;

import java.util.Date;

public class Illness {

    public int patientID;
    public String date;
    public String time;
    public String medicalCondition;
    public String medicinePrescribed;
    public int medicineAmount;

    public Illness (int id, String date, String time, String medicalCondition, String medicinePrescribed, int medicineAmount) {
        patientID = id;
        this.date = date;
        this.time = time;
        this.medicalCondition = medicalCondition;
        this.medicinePrescribed = medicinePrescribed;
        this.medicineAmount = medicineAmount;
    }
    
    public String toString() {
        return ("Patient ID is : " + patientID + "\nDate of consultation was : " + date +
                "\nTime was : " + time + "\nMedical condition is as follows : \n" + medicalCondition
                + "\nMedicine given was : " + medicinePrescribed + "\nAmount was : " + medicineAmount + "\n");
    }
}
