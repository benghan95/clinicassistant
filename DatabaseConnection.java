package clinicsystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DatabaseConnection {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/clinicSystem?zeroDateTimeBehavior=convertToNull";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "";

    public static ArrayList<Patient> loadPatientList() {

        ArrayList<Patient> patientList = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT PatientID, IC, FirstName, LastName, Age, ContactNo FROM patients";
            ResultSet rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                int id = rs.getInt("PatientID");
                String IC = rs.getString("IC");
                String first = rs.getString("FirstName");
                String last = rs.getString("LastName");
                int age = rs.getInt("Age");
                int cnumber = rs.getInt("ContactNo");

                patientList.add(new Patient(id, IC, first, last, age, cnumber));
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return patientList;
    }

    public static int addNewPatient(String IC, String fname, String lname, int age, int cnumber) {
        Connection conn = null;
        Statement stmt = null;
        int id = 0;
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //Automatically gets id
            Statement stmtSearch = conn.createStatement();
            String search;
            search = "SELECT PatientID FROM patients";
            ResultSet rs = stmtSearch.executeQuery(search);
            if (rs.first() == false) {
                id = 100;
            } else {
                rs.afterLast();
                rs.previous();
                id = (rs.getInt("PatientID")) + 1;
            }

            //STEP 4: Execute a query
            System.out.println("Inserting records into the table...");
            stmt = conn.createStatement();

            String sql = "INSERT INTO patients VALUES (" + id + ", " + IC + ", '" + fname + "', '" + lname + "', " + age + ", " + cnumber + ");";

            stmt.executeUpdate(sql);
            System.out.println("Inserted records into the table...");

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return id;
    }

    public static ArrayList<Illness> loadPatientHistory() {

        ArrayList<Illness> patientHistory = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //Create statement
            stmt = conn.createStatement();

            //STEP 4: Execute a query
            String sql = "SELECT `illness`.`Patients_id`,"
                    + "`illness`.`Date_of_Consultation`,"
                    + "`illness`.`Time_of_Consultation`,"
                    + "`illness`.`Medical_Condition`,"
                    + "`illness`.`Medicine_Prescribed`,"
                    + "`illness`.`Medicine_Amount`"
                    + "FROM `clinicSystem`.`illness`;";

            ResultSet medicalHistory = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            while (medicalHistory.next()) {
                int patientID = medicalHistory.getInt("Patients_id");
                String date = medicalHistory.getString("Date_of_Consultation");
                String time = medicalHistory.getString("Time_of_Consultation");
                String medicalCondition = medicalHistory.getString("Medical_Condition");
                String medicinePrescribed = medicalHistory.getString("Medicine_Prescribed");
                int medicineAmount = medicalHistory.getInt("Medicine_Amount");

                patientHistory.add(new Illness(patientID, date, time, medicalCondition, medicinePrescribed, medicineAmount));

            }

            //STEP 6: Clean-up environment
            medicalHistory.close();
            stmt.close();
            conn.close();

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
                // nothing we can do
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return patientHistory;
    }

    public static Illness addNewHistory(int patientID, String condition, String medicinePrescribed, int medicineAmount) {
        Connection conn = null;
        Statement stmt = null;
        Illness newIllness = null;
        int id;

        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //Automatically gets id
            Statement stmtSearch = conn.createStatement();
            String search;
            search = "SELECT illnessID FROM illness";
            ResultSet rs = stmtSearch.executeQuery(search);
            if (rs.first() == false) {
                id = 100;
            } else {
                rs.afterLast();
                rs.previous();
                id = (rs.getInt("illnessID")) + 1;
            }

            //Create statement
            stmt = conn.createStatement();

            //Get the current date and time
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            java.util.Date dateOfConsult = new java.util.Date();

            String date = dateFormat.format(dateOfConsult);
            String time = timeFormat.format(dateOfConsult);

            //Obtain user input to be inserted
            String illness = "INSERT INTO illness VALUES ("
                    + id + ", "
                    + patientID + ", '"
                    + date + "', '"
                    + time + "', '"
                    + condition + "', '"
                    + medicinePrescribed + "', "
                    + medicineAmount + ")";

            stmt.executeUpdate(illness);
            System.out.println("Records has been updated!");

            newIllness = new Illness(patientID, date, time, condition, medicinePrescribed, medicineAmount);

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                // do nothing
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return newIllness;
    }

    public static ArrayList<Medicine> loadMedicineList() {

        ArrayList<Medicine> medicineList = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //Create statement
            stmt = conn.createStatement();

            //STEP 4: Execute a query
            String sql = "SELECT `medicine`.`medicineID`,"
                    + "`medicine`.`medicineName`,"
                    + "`medicine`.`medicineUnit`,"
                    + "`medicine`.`price`"
                    + "FROM `clinicSystem`.`medicine`;";

            ResultSet list = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            while (list.next()) {
                int medicineID = list.getInt("medicineID");
                String medicineName = list.getString("medicineName");
                String unit = list.getString("medicineUnit");
                double price = list.getInt("price");

                medicineList.add(new Medicine(medicineID, medicineName, unit, price));

            }

            //STEP 6: Clean-up environment
            list.close();
            stmt.close();
            conn.close();

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
                // nothing we can do
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return medicineList;
    }

    static Medicine addNewMedicine(String medicineName, String medicineUnit, double price) {
        Connection conn = null;
        Statement stmt = null;
        Medicine newMedicine = null;
        int id;

        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //Automatically gets id
            Statement stmtSearch = conn.createStatement();
            String search;
            search = "SELECT medicineID FROM medicine";
            ResultSet rs = stmtSearch.executeQuery(search);
            if (rs.first() == false) {
                id = 100;
            } else {
                rs.afterLast();
                rs.previous();
                id = (rs.getInt("medicineID")) + 1;
            }
            
            stmt = conn.createStatement();
            //Obtain user input to be inserted
            String medicine = "INSERT INTO medicine VALUES ("
                    + id + ", '"
                    + medicineName + "', '"
                    + medicineUnit + "', "
                    + price + ")";

            stmt.executeUpdate(medicine);
            System.out.println("Records has been updated!");

            newMedicine = new Medicine(id, medicineName, medicineUnit, price);

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                // do nothing
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return newMedicine;
    }

    static void addNewMedicineQueue(int patientID, String medicinePrescribed, int amount) {
        Connection conn = null;
        Statement stmt = null;
        int id;

        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //Automatically gets id
            Statement stmtSearch = conn.createStatement();
            String search;
            search = "SELECT queueID FROM medicineQueue";
            ResultSet rs = stmtSearch.executeQuery(search);
            if (rs.first() == false) {
                id = 1;
            } else {
                rs.afterLast();
                rs.previous();
                id = (rs.getInt("queueID")) + 1;
            }

            //Obtain user input to be inserted
            stmt = conn.createStatement();
            String queue = "INSERT INTO medicineQueue VALUES ("
                    + id + ", " 
                    + patientID + ", '"
                    + medicinePrescribed + "', "
                    + amount + ")";

            stmt.executeUpdate(queue);
            System.out.println("Records has been updated!");


        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                // do nothing
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }

    static ArrayList<MedicineQueue> getMedicineQueue() {
        
        ArrayList<MedicineQueue> queue = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //Create statement
            stmt = conn.createStatement();

            //STEP 4: Execute a query
            String sql = "SELECT `medicineQueue`.`queueID`,"
                    + "`medicineQueue`.`patientID`,"
                    + "`medicineQueue`.`medicineName`,"
                    + "`medicineQueue`.`amount`"
                    + "FROM `clinicSystem`.`medicineQueue`;";

            ResultSet list = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            while (list.next()) {
                int queueID = list.getInt("queueID");
                int patientID = list.getInt("patientID");
                String medicinePrescribed = list.getString("medicineName");
                int amount = list.getInt("amount");

                queue.add(new MedicineQueue(queueID, patientID, medicinePrescribed, amount));

            }

            //STEP 6: Clean-up environment
            list.close();
            stmt.close();
            conn.close();

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
                // nothing we can do
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return queue;
    }

    static int deleteMedicineQueue(int queueID) {
        Connection conn = null;
        Statement stmt = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //Automatically gets id
            stmt = conn.createStatement();
            String delete;
            delete = "DELETE FROM `clinicSystem`.`medicineQueue` WHERE `queueID`='" + queueID + "';";

            stmt.executeUpdate(delete);
            System.out.println("Records has been updated!");

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                // do nothing
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return 1;
    }
}
