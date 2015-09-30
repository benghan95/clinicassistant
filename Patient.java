package clinicsystem;

public class Patient {
    public int PatientID;
    public String IC;
    public String firstName;
    public String lastName;
    public int age;
    public int contactNumber;
    
    public Patient(int id, String IC, String first, String last, int age, int number) {
        this.PatientID = id;
        this.IC = IC;
        firstName = first;
        lastName = last;
        this.age = age;
        contactNumber = number;
    }
            
    public String toString() {
        return ("Patient ID is : " + PatientID + "\nIC is : " + IC + "\nFirst name is " + 
                firstName + "\nLast name is : " + lastName + "\nAge is : " + 
                age + "\nContact number is : " + contactNumber + "\n");
    }
            
}
