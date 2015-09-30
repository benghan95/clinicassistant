package clinicsystem;

public class MedicineQueue {
    public int queueID;
    public int patientID;
    public String medicinePrescribed;
    public int amount;
    
    public MedicineQueue(int queueID, int id, String medicine, int amount) {
        this.queueID = queueID;
        patientID = id;
        medicinePrescribed = medicine;
        this.amount = amount;
    }
   
}
