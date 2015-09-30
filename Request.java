package clinicsystem;

import java.io.Serializable;

public class Request implements Serializable{
    public int type;
    public int patientID;
    public String IC;
    public String fname;
    public String lname;
    public int age;
    public int cnumber;
    
    Request(int type, int patientID) {
        this.type = type;
        this.patientID = patientID;
    }
    
    Request(int type, String IC, String fname, String lname, int age, int cnumber) {
        this.type = type;
        this.IC = IC;
        this.fname = fname;
        this.lname = lname;
        this.age = age;
        this.cnumber = cnumber;
        
    }
    
    Request (int type) {
        this.type = type;
    }
}
