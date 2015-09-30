package clinicsystem;

import java.io.DataInputStream;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        InputStream input = null;
        DataInputStream dis = null;
        Socket clientSocket = null;
        ArrayList<ClientConnection> clientList = new ArrayList<>();
        int port = 1111;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server with port " + port + " is up and running.");
            System.out.println("Host address is : " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Listening for connection."); //starting the server

        } catch (IOException e) {
            System.out.println("Cannot connect to server/Server already running.");
        }

        while (true) {

            try {
                clientSocket = serverSocket.accept();

                //once connection established, receive message to determine nurse or doctor
                input = clientSocket.getInputStream();
                dis = new DataInputStream(input);
                int userType = dis.readInt();

                if (userType == 1) {
                    System.out.println("Nurse has connected.");
                } else {
                    System.out.println("Doctor has connected.");
                }

                clientList.add(new ClientConnection(clientSocket, userType)); //adding clients into the arraylist of clients to keep track
                new Thread(new ServerThread(clientList, clientSocket)).start();
                
            } catch (IOException e) {
                System.out.println("Disconnected.");
                break;
            }
        }
    }
}
