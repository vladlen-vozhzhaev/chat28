package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class User {
    private String name;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream out;
    private UUID uuid;

    public User(Socket socket) throws IOException {
        this.socket = socket;
        this.is = new DataInputStream(this.socket.getInputStream());
        this.out = new DataOutputStream(this.socket.getOutputStream());
        this.uuid = UUID.randomUUID();
    }
    public void setName(String name) {this.name = name;}
    public String getName() {return name;}
    public DataInputStream getIs() {return is;}
    public DataOutputStream getOut() {return out;}
    public UUID getUuid() {return uuid;}
}
