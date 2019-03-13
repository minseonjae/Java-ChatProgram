import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Player extends Thread{
	
	private boolean start = false;
	
	public void setStart(boolean start) {
		this.start = start;
	}
	public boolean isStarted() {
		return start;
	}
	
	private Socket socket;
	
	private String address;
	
	public String getAddress() {
		return address;
	}
	
	private DataOutputStream dos;
	private DataInputStream dis;
	
	public boolean init(Socket socket, String address) {
		try {
			if (Server.debug) System.out.println(address + " : Init Start");
			this.socket = socket;
			this.address = address;
			if (Server.debug) System.out.println(address + " : DataOutputSteam Create Start");
			dos = new DataOutputStream(socket.getOutputStream());
			if (Server.debug) System.out.println(address + " : DataInputSteam Create Start");
			dis = new DataInputStream(socket.getInputStream());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void disconnect() {
		if (Server.debug) System.out.println(address + " : DisConnect Start");
		if (start) {
			try {
				start = false;
				if (Server.debug) System.out.println(address + " : DataOutputSteam Close Start");
				dos.close();
				if (Server.debug) System.out.println(address + " : DataInputSteam Close Start");
				dis.close();
				socket.close();
				if (Server.debug) System.out.println(address + " : Player Class Remove");
				Server.getPlayers().remove(address);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendMessage(Object message) {
		try {
			if (Server.debug) System.out.println(address + " : SendMessage (" + message.toString() + ")");
			dos.writeUTF(message.toString());
		} catch (IOException e) {
			e.printStackTrace();
			if (Server.debug) System.out.println(address + " : sendMessage(Object message) Error");
			disconnect();
		}
	}
	
	public void run() {
		while (isStarted()) {
			try {
				if (Server.debug) System.out.println(address + " : Read Message Wait");
				String message = dis.readUTF();
				System.out.println(address + " : Message (" + message + ")");
			} catch (Exception e) {
				e.printStackTrace();
				if (Server.debug) System.out.println(address + " : run() Error");
				disconnect();
			}
		}
	}
}
