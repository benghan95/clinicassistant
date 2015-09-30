package clinicsystem;

public class Medicine {
    public int medicineID;
    public String medicineName;
    public String medicineUnit;
    public double price;
    
    public Medicine(int id, String name, String unit, double price) {

        medicineID = id;
        medicineName = name;
        medicineUnit = unit;
        this.price = price;
    }
    
}
