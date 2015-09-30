package clinicsystem;

import java.net.Socket;

public class ClientConnection {
    public Socket socket;
    public int userType;
    
    public ClientConnection (Socket socket, int userType) {
        this.socket = socket;
        this.userType = userType;
    }
}
