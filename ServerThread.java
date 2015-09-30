package clinicsystem;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

public class ServerThread implements Runnable {

    private Socket socket;
    ArrayList<ClientConnection> clientList;
    InputStream input;
    DataInputStream dis;
    OutputStream output;
    DataOutputStream dos;

    public ServerThread(ArrayList<ClientConnection> clientList, Socket socket) {
        this.socket = socket;
        this.clientList = clientList;
    }

    public void run() {
        ArrayList<Patient> patientList = new ArrayList<>();
        ArrayList<Illness> patientHistory = new ArrayList<>();
        ArrayList<Medicine> medicineList = new ArrayList<>();
        ArrayList<MedicineQueue> medicineQueue = new ArrayList<>();

        int request = 0;

        System.out.println("Loading patient data..");
        patientList = DatabaseConnection.loadPatientList(); //loading database data into an easier
        // and faster arraylist
        patientHistory = DatabaseConnection.loadPatientHistory();
        System.out.println("Loading medicine data..");
        medicineList = DatabaseConnection.loadMedicineList();

        System.out.println("Loaded!\n");

        //listening for requests
        try {
            input = socket.getInputStream();
            dis = new DataInputStream(input);
            output = socket.getOutputStream();

            while (request != 20) {
                System.out.println("Waiting for request..");
                request = dis.readInt();

                System.out.println("Request received!");
                System.out.println("Request is " + request);

                switch (request) {

                    case 1: {
                        addNewPatient(patientList);
                        break;
                    }
                    case 2: { //send patient list to client 
                        sendPatientList(patientList);
                        break;
                    }
                    case 3: {
                        startChat(clientList, socket);
                        break;
                    }
                    case 4: {
                        sendPatientToDoctor(clientList, socket);
                        break;
                    }
                    case 5: {
                        sendHistoryList(patientHistory);
                        break;
                    }
                    case 6: {
                        addNewHistory(patientHistory);
                        break;
                    }
                    case 7: {
                        addMedicineQueue(medicineQueue);
                        break;
                    }
                    case 8: {
                        sendMedicineListToNurse(medicineList);
                        break;
                    }
                    case 9: {
                        addNewMedicine(medicineList);
                        break;
                    }
                    case 10: {
                        refreshNurseMedicineQueue(medicineQueue);
                        break;
                    }
                    case 11: {
                        deleteMedicineQueue();
                        break;
                    }
                    case 12: {
                        refreshPatientList(patientList);
                        break;
                    }

                }
            }
        } catch (IOException e) {
            System.out.println("Cannot connect to client.");
        }
    }

    public void addNewPatient(ArrayList<Patient> patientList) {
        try {
            input = socket.getInputStream();
            dis = new DataInputStream(input);
            output = socket.getOutputStream();
            dos = new DataOutputStream(output);

            String IC = dis.readUTF();
            String fname = dis.readUTF();
            String lname = dis.readUTF();
            int age = dis.readInt();
            int cnumber = dis.readInt();

            int id = DatabaseConnection.addNewPatient(IC, fname, lname, age, cnumber);
            patientList.add(new Patient(id, IC, fname, lname, age, cnumber));
            System.out.println("Added new entry in ServerThread.");

            dos.writeInt(id);

        } catch (IOException e) {
            System.out.println("Cannot get IO streams for add new patients in server.");
        }
    }

    public void sendPatientList(ArrayList<Patient> patientList) {
        int id;
        String ic;
        String fname;
        String lname;
        int age;
        int cnumber;

        try {
            input = socket.getInputStream();
            dis = new DataInputStream(input);
            output = socket.getOutputStream();
            dos = new DataOutputStream(output);

            dos.writeInt(patientList.size());

            for (int i = 0; i < patientList.size(); i++) {
                id = patientList.get(i).PatientID;
                dos.writeInt(id);

                ic = patientList.get(i).IC;
                dos.writeUTF(ic);

                fname = patientList.get(i).firstName;
                dos.writeUTF(fname);

                lname = patientList.get(i).lastName;
                dos.writeUTF(lname);

                age = patientList.get(i).age;
                dos.writeInt(age);

                cnumber = patientList.get(i).contactNumber;
                dos.writeInt(cnumber);

            }
        } catch (IOException e) {
            System.out.println("Cannot get IO streams for ServerThread.");
        }

    }

    public void startChat(ArrayList<ClientConnection> clientList, Socket socket) {
        boolean connected = true;
        String outputString = null;

        try {
            InputStream input = socket.getInputStream();
            DataInputStream dis = new DataInputStream(input);
            OutputStream output;
            DataOutputStream dos;
        } catch (IOException e) {
            System.out.println("Cannot start streams for chat.");
        }

        System.out.println("Chat initiated.");

        while (connected) {
            try {
                outputString = dis.readUTF();
                System.out.println(outputString);

                for (int i = 0; i < clientList.size(); i++) {
                    output = clientList.get(i).socket.getOutputStream();
                    dos = new DataOutputStream(output);

                    if (!outputString.equals("/end")) {
                        if (socket != clientList.get(i).socket) {
                            dos.writeUTF("Reply : " + outputString);
                        }
                    } else {
                        dos.writeUTF(outputString);
                        connected = false;
                    }

                }

            } catch (IOException e) {
                System.out.println("Cannot read messages from clients.");
            }

        }
    }

    public void sendPatientToDoctor(ArrayList<ClientConnection> clientList, Socket socket) {

        int patientID;
        InputStream input;
        DataInputStream dis;
        OutputStream output;
        DataOutputStream dos;

        System.out.println("Waiting for patient ID from nurse..");

        try {
            input = socket.getInputStream();
            dis = new DataInputStream(input);

            patientID = dis.readInt();
            System.out.println("Patient ID received is " + patientID);

            for (int i = 0; i < clientList.size(); i++) {
                if (clientList.get(i).userType == 2) {
                    output = clientList.get(i).socket.getOutputStream();
                    dos = new DataOutputStream(output);
                    dos.writeInt(patientID);
                    System.out.println("Written " + patientID + " to doctor.");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot get IO Streams in sendPatientToDoctor.");
        }
    }

    public void sendHistoryList(ArrayList<Illness> patientHistory) {
        int patientID;
        String date;
        String time;
        String medicalCondition;
        String medicinePrescribed;
        int medicineAmount;

        try {
            input = socket.getInputStream();
            dis = new DataInputStream(input);
            output = socket.getOutputStream();
            dos = new DataOutputStream(output);

            dos.writeInt(patientHistory.size());

            for (int i = 0; i < patientHistory.size(); i++) {
                patientID = patientHistory.get(i).patientID;
                dos.writeInt(patientID);

                date = patientHistory.get(i).date;
                dos.writeUTF(date);

                time = patientHistory.get(i).time;
                dos.writeUTF(time);

                medicalCondition = patientHistory.get(i).medicalCondition;
                dos.writeUTF(medicalCondition);

                medicinePrescribed = patientHistory.get(i).medicinePrescribed;
                dos.writeUTF(medicinePrescribed);

                medicineAmount = patientHistory.get(i).medicineAmount;
                dos.writeInt(medicineAmount);

            }
        } catch (IOException e) {
            System.out.println("Cannot get IO streams for ServerThread.");
        }

    }

    private void addNewHistory(ArrayList<Illness> patientHistory) {

        int patientID;
        String medicalCondition;
        String medicinePrescribed;
        int medicineAmount;

        try {
            input = socket.getInputStream();
            dis = new DataInputStream(input);
            output = socket.getOutputStream();
            dos = new DataOutputStream(output);

            patientID = dis.readInt();
            medicalCondition = dis.readUTF();
            medicinePrescribed = dis.readUTF();
            medicineAmount = dis.readInt();

            Illness illness = DatabaseConnection.addNewHistory(patientID, medicalCondition, medicinePrescribed, medicineAmount);
            patientHistory.add(illness);

            dos.writeUTF(illness.date);
            dos.writeUTF(illness.time);

        } catch (IOException e) {
            System.out.println("Cannot get IO streams in addNewHistory.");
        }

    }

    private void addMedicineQueue(ArrayList<MedicineQueue> medicineQueue) {
        try {
            input = socket.getInputStream();
            dis = new DataInputStream(input);
            output = socket.getOutputStream();
            dos = new DataOutputStream(output);
            
            
            int patientID = dis.readInt();
            String medicinePrescribed = dis.readUTF();
            int amount = dis.readInt();
            
            DatabaseConnection.addNewMedicineQueue(patientID, medicinePrescribed, amount);
            System.out.println("Added new entry in ServerThread.");


        } catch (IOException e) {
            System.out.println("Cannot get IO streams for add new patients in server.");
        }
    }

    private void sendMedicineListToNurse(ArrayList<Medicine> medicineList) {
        int medicineID;
        String medicineName;
        String unit;
        double price;

        try {
            input = socket.getInputStream();
            dis = new DataInputStream(input);
            output = socket.getOutputStream();
            dos = new DataOutputStream(output);

            dos.writeInt(medicineList.size());

            for (int i = 0; i < medicineList.size(); i++) {
                medicineID = medicineList.get(i).medicineID;
                dos.writeInt(medicineID);

                medicineName = medicineList.get(i).medicineName;
                dos.writeUTF(medicineName);

                unit = medicineList.get(i).medicineUnit;
                dos.writeUTF(unit);

                price = medicineList.get(i).price;
                dos.writeDouble(price);

            }
        } catch (IOException e) {
            System.out.println("Cannot get IO streams for ServerThread.");
        }
    }

    private void addNewMedicine(ArrayList<Medicine> medicineList) {
        String medicineName;
        String medicineUnit;
        double price;

        try {
            input = socket.getInputStream();
            dis = new DataInputStream(input);
            output = socket.getOutputStream();
            dos = new DataOutputStream(output);

            medicineName = dis.readUTF();
            medicineUnit = dis.readUTF();
            price = dis.readDouble();

            Medicine newMedicine = DatabaseConnection.addNewMedicine(medicineName, medicineUnit, price);

            dos.writeInt(newMedicine.medicineID);
            medicineList.add(newMedicine);

        } catch (IOException e) {
            System.out.println("Cannot get IO streams in addNewHistory.");
        }

    }

    private void refreshNurseMedicineQueue(ArrayList<MedicineQueue> medicineQueue) {
        System.out.println("Loading queue..");
        medicineQueue = DatabaseConnection.getMedicineQueue();
        
        try {
            output = socket.getOutputStream();
            dos = new DataOutputStream(output);
      
            dos.writeInt(medicineQueue.size());

            for (int i = 0; i < medicineQueue.size(); i++) {
                dos.writeInt(medicineQueue.get(i).queueID);
                dos.writeInt(medicineQueue.get(i).patientID);
                dos.writeUTF(medicineQueue.get(i).medicinePrescribed);
                dos.writeInt(medicineQueue.get(i).amount);
            }

        } catch (IOException e) {
            System.out.println("Cannot IO stream in refresh medicine queue.");
        }

        
        
        
        
        
        
        
        
    }

    private void deleteMedicineQueue() {
        int queueID;
        
        try {
        input = socket.getInputStream();
        dis = new DataInputStream(input);
        
        queueID = dis.readInt();
        
        int deleted = DatabaseConnection.deleteMedicineQueue(queueID);
        
        if (deleted == 1) {
            System.out.println("Deleted medicine queue.");
        }
        
        }
        catch (IOException e) {
            System.out.println("Cannot IO stream in deleteMedicineQueue.");
        }
    }

    private void refreshPatientList(ArrayList<Patient> patientList) {
        int oldList = patientList.size();
        
        patientList = DatabaseConnection.loadPatientList();
        
        try {
            output = socket.getOutputStream();
            dos = new DataOutputStream(output);

            dos.writeInt((patientList.size() - oldList));
            
            for (int i = oldList; i < patientList.size(); i++) {
                dos.writeInt(patientList.get(i).PatientID);
                dos.writeUTF(patientList.get(i).IC);
                dos.writeUTF(patientList.get(i).firstName);
                dos.writeUTF(patientList.get(i).lastName);
                dos.writeInt(patientList.get(i).age);
                dos.writeInt(patientList.get(i).contactNumber);
            }
        } catch (IOException e) {
            System.out.println("Cannot get IO for refresh patient List.");
        }
    }

}
