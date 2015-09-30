package clinicsystem;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Doctor {

    static ArrayList<Patient> patientList = new ArrayList<>();
    static ArrayList<Illness> medicalHistory = new ArrayList<>();
    static boolean connectedToChat = true;
    static int writeStatusForChat;

    //patient info declare here
    static String condition = null;
    static String medicinePrescribed = null;
    static int medicineAmount = 0;

    public static void main(String[] args) {

        Socket socket = null;
        String host = "localhost";
        int port = 1111;
        OutputStream output = null;
        DataOutputStream dos = null;
        InputStream input = null;
        DataInputStream dis = null;

        System.out.println("Connecting to server..");

        try {
            socket = new Socket(host, port);
            System.out.println("Connected to server.");
            input = socket.getInputStream();
            dis = new DataInputStream(input);
            output = socket.getOutputStream();
            dos = new DataOutputStream(output);

            dos.writeInt(2); //to let server know it's a doctor who's connecting

            System.out.println("Loading patient data..");
            getPatientList(socket);
            System.out.println("Loaded patients!");
            System.out.println("Loading patient history..");
            getPatientHistory(socket);
            System.out.println("Loaded history!");
            System.out.println("\n\n\n");

        } catch (IOException e) {
            System.out.println(e);
        }

        //start the doctor
        int option = 0;
        int patientID = 0;
        boolean connected = true;
        Scanner scanner = new Scanner(System.in);

        while (connected) {
            try {
                System.out.println("Waiting for a new patient..");
                patientID = dis.readInt();
                option = 0;
            } catch (Exception e) {
                System.out.println("Class not found.");
            }
            
            while (option != 7) {
                System.out.println("You are now seeing patient " + patientID + "!\n");
                System.out.println("\t1. View Medical History");
                System.out.println("\t2. Input Medical Condition");
                System.out.println("\t3. Prescribe Medicine");
                System.out.println("\t4. Issue Medical Certificate");
                System.out.println("\t5. View patient information");
                System.out.println("\t6. Chat");
                System.out.println("\t7. Finish with this patient");
                try {
                    option = scanner.nextInt();
                }
                catch (InputMismatchException e) {
                    System.out.println("Please enter only integers.");
                    option = 0;
                }

                if (option == 1) {
                    viewMedicalHistory(patientID);

                } else if (option == 2) {
                    inputMedicalCondition(patientID);

                } else if (option == 3) {
                    prescribeMedicine(patientID);

                } else if (option == 4) {
                    issueMC(patientID);

                } else if (option == 5) {
                    viewPatient(patientID, socket);

                } else if (option == 6) {
                    chat(socket);

                } else if (option == 7) {
                    updateDatabase(socket, patientID);
                } else {
                    System.out.println("No such selection!");
                    System.out.println("Please select again");

                }
            }
        
        }
        

    }

    public static void getPatientList(Socket socket) {
        int id;
        String ic;
        String fname;
        String lname;
        int age;
        int cnumber;

        try {
            OutputStream output = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(output);
            InputStream input = socket.getInputStream();
            DataInputStream dis = new DataInputStream(input);

            dos.writeInt(2);

            int numberOfPatients = dis.readInt();

            for (int i = 0; i < numberOfPatients; i++) {
                id = dis.readInt();
                ic = dis.readUTF();
                fname = dis.readUTF();
                lname = dis.readUTF();
                age = dis.readInt();
                cnumber = dis.readInt();

                patientList.add(new Patient(id, ic, fname, lname, age, cnumber));
            }

        } catch (IOException e) {
            System.out.println("Cannot get IO streams.");
        }

    }

    public static void getPatientHistory(Socket socket) {
        int patientSize, patientID;
        String date;
        String time;
        String medicalCondition;
        String medicinePrescribed;
        int medicineAmount;
        OutputStream out = null;
        DataOutputStream dos = null;
        InputStream in = null;
        DataInputStream dis = null;

        try {
            out = socket.getOutputStream();
            dos = new DataOutputStream(out);
            in = socket.getInputStream();
            dis = new DataInputStream(in);

            dos.writeInt(5);
            patientSize = dis.readInt();

            for (int i = 0; i < patientSize; i++) {
                patientID = dis.readInt();
                date = dis.readUTF();
                time = dis.readUTF();
                medicalCondition = dis.readUTF();
                medicinePrescribed = dis.readUTF();
                medicineAmount = dis.readInt();

                medicalHistory.add(new Illness(patientID, date, time, medicalCondition, medicinePrescribed, medicineAmount));
            }
        } catch (IOException e) {
            System.out.println("Cannot get IO stream in load patient history.");
        }

    }

    private static void viewMedicalHistory(int patientID) {

        int count = 0;

        for (int i = 0; i < medicalHistory.size(); i++) {
            if (patientID == medicalHistory.get(i).patientID) {
                System.out.println(medicalHistory.get(i).toString());
                count++;
            }
        }

        if (count == 0) {
            System.out.println("No history found for this patient.");
        }

    }

    private static void inputMedicalCondition(int patientID) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.printf("Please insert patient's medical conditions: ");
            condition = br.readLine();

            System.out.println("Saving..");

        } catch (IOException e) {
            System.out.println("Cannot get IO streams in inputMedicalConditions.");
        }
    }

    private static void prescribeMedicine(int patientID) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Enter medicine to prescribe : ");
            medicinePrescribed = scanner.next();
            System.out.println("Enter amount to prescribe : ");
            medicineAmount = scanner.nextInt();

            medicinePrescribed = medicinePrescribed.toUpperCase();
        } catch (InputMismatchException e) {
            System.out.println("Please enter only text and integers in their respective fields.");
            return;
        }
        System.out.println("\nMedicine added.");
    }

    private static void viewPatient(int patientID, Socket socket) {

        int found = 0;
        for (int i = 0; i < patientList.size(); i++) {
            if (patientList.get(i).PatientID == patientID) {
                System.out.println(patientList.get(i).toString());
                found++;
                break;
            }
        }

        if (found == 0) {
            System.out.println("New patient. Updating info..");
            refreshPatientList(socket);
            for (int i = 0; i < patientList.size(); i++) {
                if (patientList.get(i).PatientID == patientID) {
                    System.out.println(patientList.get(i).toString());
                    break;
                }
            }
        }
    }

    public static void issueMC(int patientID) {

        String institutionName = "Clinic Beng";
        String doctorName = "Beng";
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        Date dateFrom = new Date();
        Date dateTo = new Date();
        String fname = null, lname = null;

        for (int i = 0; i < patientList.size(); i++) {
            if (patientID == patientList.get(i).PatientID) {
                fname = patientList.get(i).firstName;
                lname = patientList.get(i).lastName;
                break;
            }
        }

        System.out.println("\t\tMedical Certificate");
        System.out.println("Date: " + dateFormat.format(date));
        System.out.println("To whom it may concern: ");
        System.out.println("This is to certify that,\t" + fname + " " + lname + "\t");
        System.out.println("was examine and treated as out patient from " + dateFrom + " to " + dateTo + ".");
        System.out.println("Certified by: ");
        System.out.println("Dr. " + doctorName);
        System.out.println(institutionName);

        System.out.println("Enter any key to continue..");
    }

    private static void chat(Socket socket) {
        InputStream input;
        DataInputStream dis = null;
        OutputStream output;
        DataOutputStream dos = null;

        try {
            input = socket.getInputStream();
            dis = new DataInputStream(input);
            output = socket.getOutputStream();
            dos = new DataOutputStream(output);

            dos.writeInt(3); //send 5 as request to server to request for chat
        } catch (IOException e) {
            System.out.println("Cannot send requests to server.");
        }

        System.out.println("Enter your message and hit enter to send.\n");

        connectedToChat = true;
        
        Thread readThread = new Thread(new Read(dis));
        Thread writeThread = new Thread(new Write(dos));
        readThread.start();
        writeThread.start();

        while ((writeThread.isAlive()) && (readThread.isAlive())) {
            //do not end method while chat is ongoing
        }

        System.out.println("Disconnected from chat. Enter anything to return to menu.");

        if (!writeThread.isAlive()) {
            Scanner scanner = new Scanner(System.in);
            scanner.next();
        }
        
        while (writeThread.isAlive()) {

        }

    }

    private static void updateDatabase(Socket socket, int patientID) {
        OutputStream out;
        DataOutputStream dos;
        InputStream in;
        DataInputStream dis;
        
        if ((medicinePrescribed == null) && (condition == null)) {
            System.out.println("No changes updated.");
            return;
        }

        try {
            out = socket.getOutputStream();
            dos = new DataOutputStream(out);
            in = socket.getInputStream();
            dis = new DataInputStream(in);

            dos.writeInt(6);

            System.out.println("Saving changes..");

            dos.writeInt(patientID);
            dos.writeUTF(condition);
            dos.writeUTF(medicinePrescribed);
            dos.writeInt(medicineAmount);

            String date = dis.readUTF();
            String time = dis.readUTF();

            medicalHistory.add(new Illness(patientID, date, time, condition, medicinePrescribed, medicineAmount));
            if (medicinePrescribed != null) {
                dos.writeInt(7);

                dos.writeInt(patientID);
                dos.writeUTF(medicinePrescribed);
                dos.writeInt(medicineAmount);
            }
        } catch (IOException e) {
            System.out.println("Can't update database in updateDatabase.");
        }

    }

    private static void refreshPatientList(Socket socket) {
        int id;
        String ic;
        String fname;
        String lname;
        int age;
        int cnumber;

        try {
            OutputStream output = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(output);
            InputStream input = socket.getInputStream();
            DataInputStream dis = new DataInputStream(input);

            dos.writeInt(12);

            int numberOfPatients = dis.readInt();

            for (int i = 0; i < numberOfPatients; i++) {
                id = dis.readInt();
                ic = dis.readUTF();
                fname = dis.readUTF();
                lname = dis.readUTF();
                age = dis.readInt();
                cnumber = dis.readInt();

                patientList.add(new Patient(id, ic, fname, lname, age, cnumber));
            }

        } catch (IOException e) {
            System.out.println("Cannot get IO streams.");
        }
    }

    public static class Read implements Runnable { //read thread to read messages from chat

        DataInputStream dis;

        public Read(DataInputStream in) {
            dis = in;
        }

        public void run() {
            String input;
            String end = "/end";

            while (connectedToChat == true) {
                try {
                    input = dis.readUTF();
                    if (input.equals(end)) {
                        connectedToChat = false;
                        break;
                    }
                    else 
                    System.out.println(input);
                } catch (IOException e) {
                    System.out.println("Cannot read messages from server.");
                    connectedToChat = false;
                }
            }
        }
    }

    public static class Write implements Runnable { //write thread to write chat messages

        DataOutputStream dos;

        public Write(DataOutputStream dos) {
            this.dos = dos;
        }

        public void run() {
            Scanner scanner = new Scanner(System.in);
            String output;
            String end = "/end";

            while (connectedToChat == true) {
                try {
                    output = scanner.nextLine();
                    if (connectedToChat == true) {
                        dos.writeUTF(output);
                        if (output.equals(end)) {
                            connectedToChat = false;
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Cannot write to server.");
                    connectedToChat = false;
                }


            
            }

        }
    }
}
