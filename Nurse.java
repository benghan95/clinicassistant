package clinicsystem;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Nurse {

    static ArrayList<Patient> patientList = new ArrayList<>();
    static ArrayList<Patient> patientQueue = new ArrayList<>();
    static ArrayList<MedicineQueue> medicineQueue = new ArrayList<>();
    static ArrayList<Medicine> medicineList = new ArrayList<>();
    static ArrayList<Illness> illnessList = new ArrayList<>();
    static boolean connectedToChat = true;
    static int writeStatusForChat;

    public static void main(String[] args) throws InterruptedException {
        Socket socket = null;
        OutputStream out = null;
        DataOutputStream dos = null;
        InputStream in = null;
        DataInputStream dis = null;

        String host = "localhost";
        int port = 1111;
        int choice = 0;

        System.out.println("Connecting to server..");

        try {
            socket = new Socket(host, port);

            System.out.println("Connected to server.");
            out = socket.getOutputStream();
            dos = new DataOutputStream(out);
            in = socket.getInputStream();
            dis = new DataInputStream(in);

            dos.writeInt(1); //to let the server know that a nurse connected
            System.out.println("Loading patient data..");
            patientList = getPatientList(socket, patientList);
            System.out.println("Loading medicine..");
            getMedicineList(socket);
            System.out.println("Loading illness..");
            loadIllnessList(socket);
            System.out.println("Loaded!");

        } catch (IOException e) {
            System.out.println("Cannot connect to server.");
        }

        while (choice != 8) {
            choice = mainMenu();

            switch (choice) {
                case 1: {
                    registerPatients(socket);
                    break;
                }
                case 2: {
                    viewPatients(patientList);
                    break;
                }
                case 3: {
                    viewQueue(socket);
                    break;
                }
                case 4: {
                    startChat(socket);
                    break;
                }
                case 5: {
                    viewMedicineQueue(socket);
                    break;
                }
                case 6: {
                    refreshMedicineQueue(socket);
                    break;
                }
                case 7: {
                    searchIllness();
                    break;
                }
                case 8: {
                    System.out.println("Goodbye!");
                    System.exit(-1);
                    break;
                }
            }

        }

    }

    public static int mainMenu() {

        int choice = 0;
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nWelcome to system!");

        System.out.println("1. Register patients.");
        System.out.println("2. View patients list.");
        System.out.println("3. View patient queue.");
        System.out.println("4. Chat with doctor.");
        System.out.println("5. View medicine queue.");
        System.out.println("6. Refresh medicine queue.");
        System.out.println("7. Search illness.");
        System.out.println("8. Exit.");

        System.out.println("\nYou have " + patientQueue.size() + " patients in queue.");
        System.out.println("You have " + medicineQueue.size() + " medicine to prescribe in queue.");

        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Please enter only an integer. Please try again.");
        }

        return choice;
    }

    public static ArrayList<Patient> getPatientList(Socket socket, ArrayList<Patient> patientList) {
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

        return patientList;
    }

    public static void registerPatients(Socket socket) {
        Scanner scanner = new Scanner(System.in);
        long ICint = 0;
        String IC = null;
        int count = 0;

        try {
            System.out.println("Enter IC : ");
            ICint = scanner.nextLong();
        }
        catch (InputMismatchException e) {
            System.out.println("Please enter only integers.");
            return;
        }
        
        IC = String.valueOf(ICint);
        System.out.println("Searching for patient..");

        for (int i = 0; i < patientList.size(); i++) {
            if (patientList.get(i).IC.equals(IC)) {
                count++;
                System.out.println("Patient found! Added to queue.\n\n");
                patientQueue.add(patientList.get(i));
            }
        }

        if (count == 0) {
            System.out.println("Patient not found. Registering new entry.\n");
            registerNewPatient(IC, socket);
        }

    }

    public static ArrayList<Patient> registerNewPatient(String IC, Socket socket) {
        String fname;
        String lname;
        int age;
        int cnumber;
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Enter first name : ");
            fname = scanner.next();
            System.out.println("Enter last name : ");
            lname = scanner.next();
            System.out.println("Enter age : ");
            age = scanner.nextInt();
            System.out.println("Enter contact number : ");
            cnumber = scanner.nextInt();
        
            OutputStream output = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(output);
            InputStream input = socket.getInputStream();
            DataInputStream dis = new DataInputStream(input);

            dos.writeInt(1); //to let server know nurse is registering new patient
            dos.writeUTF(IC);
            dos.writeUTF(fname);
            dos.writeUTF(lname);
            dos.writeInt(age);
            dos.writeInt(cnumber);

            System.out.println("Adding entry..");
            int id = dis.readInt();

            patientList.add(new Patient(id, IC, fname, lname, age, cnumber));
            patientQueue.add(new Patient(id, IC, fname, lname, age, cnumber));

            System.out.println("Added new entry!\n\n");
        } catch (IOException e) {
            System.out.println("Cannot connect to server in register new patient for nurse.");
        }
        catch (InputMismatchException e) {
            System.out.println("Please enter only integers and text in the appropriate fields.");
        }
        return patientQueue;
    }

    public static void viewPatients(ArrayList<Patient> patientList) {
        for (int i = 0; i < patientList.size(); i++) {
            System.out.println(patientList.get(i).toString());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter any key to go back to main menu.");
        scanner.next();
    }

    public static void viewQueue(Socket socket) {
        Scanner scanner = new Scanner(System.in);
        int patientNumber ;

        if (patientQueue.isEmpty()) {
            System.out.println("There is no patient in the queue.");
            return;
        }

        for (int i = 0; i < patientQueue.size(); i++) {
            System.out.println((i + 1) + ". Patient ID : " + patientQueue.get(i).PatientID
                    + "\nName : " + patientQueue.get(i).firstName + "\n");
        }

        System.out.println("Select patient to send to doctor. Press 0 to return to main menu.");
        
        try {
            patientNumber = scanner.nextInt();
            if (patientNumber == 0) {
                return;
            }
            patientNumber -= 1;
        }
        catch (InputMismatchException e) {
            System.out.println("Please enter only integers.");
            return;
        }
        

        if ((patientNumber >= 0) && (patientNumber < patientQueue.size())) {
            if (patientNumber != -1) {
                try {
                    OutputStream output = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(output);
                    dos.writeInt(4);
                    dos.writeInt((patientQueue.get(patientNumber).PatientID));

                    patientQueue.remove(patientNumber);
                    System.out.println("Patient " + (patientNumber + 1) + " sent to doctor! Removed from queue.");
                } catch (IOException e) {
                    System.out.println("Cannot create streams in view queue.");
                }

            } else {
                return;
            }
        } else {
            System.out.println("Number entered is not in queue. Please try again.");
        }
    }

    public static void startChat(Socket socket) {

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

        }

        System.out.println("Disconnected from chat. Enter anything to return to menu.");
        
        if (!writeThread.isAlive()) {
            Scanner scanner = new Scanner(System.in);
            scanner.next();
        }
        
        while (writeThread.isAlive()) {
            
        }

    }

    private static void viewMedicineQueue(Socket socket) {
        int choice = 0;
        Scanner scanner = new Scanner(System.in);
        double price = 0;
        int amount = 0;

        if (medicineQueue.size() == 0) {
            System.out.println("There is no medicine in queue. Try refreshing.");
        } else {
            for (int i = 0; i < medicineQueue.size(); i++) {
                System.out.println((i + 1) + ". Patient ID is " + medicineQueue.get(i).patientID);
                System.out.println("Medicine prescribed is " + medicineQueue.get(i).medicinePrescribed);
                System.out.println("Amount is " + medicineQueue.get(i).amount);
                amount = medicineQueue.get(i).amount;
            }

            System.out.println("Choose which to prescribe : ");
            try {
                choice = scanner.nextInt();
                choice -= 1;
                int count = 0;

                for (int i = 0; i < medicineList.size(); i++) {
                    if (medicineList.get(i).medicineName.equals(medicineQueue.get(choice).medicinePrescribed)) {
                        count++;
                        
                        price = medicineList.get(i).price * amount;
                        break;
                    }
                }

                if (count == 0) {
                    System.out.println("Medicine does not exist in database. Creating new entry..");

                    try {
                        OutputStream output = socket.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(output);
                        InputStream input = socket.getInputStream();
                        DataInputStream dis = new DataInputStream(input);

                        dos.writeInt(9);

                        String medicineName;
                        String medicineUnit;
                        double medicinePrice;
                        int medicineID;

                        medicineName = medicineQueue.get(choice).medicinePrescribed;

                        System.out.println("Enter medicine unit : ");
                        medicineUnit = scanner.next();

                        System.out.println("Enter price per unit : ");
                        medicinePrice = scanner.nextDouble();

                        dos.writeUTF(medicineName);
                        dos.writeUTF(medicineUnit);
                        dos.writeDouble(medicinePrice);

                        System.out.println("Adding new medicine..");
                        medicineID = dis.readInt();

                        medicineList.add(new Medicine(medicineID, medicineName, medicineUnit, medicinePrice));
                        System.out.println("Added successfully!\n");

                        price = medicinePrice * amount;
                    } catch (IOException e) {
                        System.out.println("Cannot get IO streams in prescribe medicine.");
                    }

                }

            } catch (InputMismatchException e) {
                System.out.println("Please enter only integers.");
                return;
            }

            System.out.println("\nTotal price for the medicine is : " + price);
            
            
            //after prescribing medicine, delete from queue and database
            
            try {
                OutputStream output = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(output);
                
                
                dos.writeInt(11);
                dos.writeInt(medicineQueue.get(choice).queueID);
            }
            catch (IOException e) {
                System.out.println("Cannot create IO streams in delete medicine");
            }
            
            medicineQueue.remove(choice); //remove the medicine queue from the queue after prescription
            
        }
        
        System.out.println("\nEnter any key to continue.");
        scanner.next();
    }

    private static void getMedicineList(Socket socket) {
        int medicineID;
        String medicineName;
        String medicineUnit;
        double price;

        try {
            OutputStream output = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(output);
            InputStream input = socket.getInputStream();
            DataInputStream dis = new DataInputStream(input);

            dos.writeInt(8);

            int numberOfMedicine = dis.readInt();

            for (int i = 0; i < numberOfMedicine; i++) {
                medicineID = dis.readInt();
                medicineName = dis.readUTF();
                medicineUnit = dis.readUTF();
                price = dis.readDouble();

                medicineList.add(new Medicine(medicineID, medicineName, medicineUnit, price));
            }

        } catch (IOException e) {
            System.out.println("Cannot get IO streams.");
        }

    }

    public static void refreshMedicineQueue(Socket socket) {

        try {
            InputStream input = socket.getInputStream();
            DataInputStream dis = new DataInputStream(input);
            OutputStream output = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(output);

            dos.writeInt(10);
            int found = 0;
            

            int inQueue = dis.readInt();

            if (inQueue > 0) {
                int queueID;
                int patientID;
                String medicineName;
                int medicineAmount;

                for (int i = 0; i < inQueue; i++) {
                    queueID = dis.readInt();
                    patientID = dis.readInt();
                    medicineName = dis.readUTF();
                    medicineAmount = dis.readInt();

                    for (i = 0; i < medicineQueue.size(); i++) {
                        if (medicineQueue.get(i).queueID == queueID) {
                        found++;
                        }
                    }
                    
                    if (found == 0) {
                        medicineQueue.add(new MedicineQueue(queueID, patientID, medicineName, medicineAmount));
                        System.out.println("Updated!");
                    }
                }

            } else {
                System.out.println("No new queue.\n");
            }

        } catch (IOException e) {
            System.out.println("Cannot get IO streams in refresh medicine queue.");
        }

    }

    private static void searchIllness() {
        String userSearch;
        Scanner scanner = new Scanner(System.in);
        int found = 0;
        
        System.out.println("Enter illness to search for : ");
        userSearch = scanner.next();
        
        for (int i = 0; i < illnessList.size(); i++) { 
            if (illnessList.get(i).medicalCondition.equals(userSearch)) {
                System.out.println("\nFound!");
                System.out.println("Patient ID is " + illnessList.get(i).patientID);
                System.out.println("Date is " + illnessList.get(i).date + "   " + illnessList.get(i).time);
                found++;
            }
        }
        
        if (found == 0) {
            System.out.println("No such condition found.");
        }
    }

    private static void loadIllnessList(Socket socket) {
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

                illnessList.add(new Illness(patientID, date, time, medicalCondition, medicinePrescribed, medicineAmount));
            }
        } catch (IOException e) {
            System.out.println("Cannot get IO stream in load patient history.");
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

            while (connectedToChat) {
                try {
                    input = dis.readUTF();
                    if (input.equals(end)) {
                        connectedToChat = false;
                    } else {
                        System.out.println(input);
                    }

                } catch (IOException e) {
                    System.out.println("Cannot read messages from server.");
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
                        if (output.equals(end)) {
                            dos.writeUTF("/end");
                            connectedToChat = false;
                            break;
                        } else {
                            dos.writeUTF(output);
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
