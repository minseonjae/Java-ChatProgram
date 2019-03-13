import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;


public class Client {
	
	private static final String SERVER_ADDRESS = "localhost";
	private static final int SERVER_PORT = 2450, SERVER_TIMEOUT = 1000;
	
	private static boolean debug = false;
	private static DataOutputStream dos;
	private static DataInputStream dis;
	private static Socket socket = null;
	private static Scanner scanner = null;
	
	public static void main(String[] args) {
		try {
			if (debug) System.out.println("Socket Create Start");
			
			socket = new Socket();
			
			if (debug) System.out.println("Socket Connect Start");
			
			socket.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT), SERVER_TIMEOUT);

			if (debug) System.out.println("DataOutputStream Create Start");
			
			dos = new DataOutputStream(socket.getOutputStream());

			if (debug) System.out.println("DataInputStream Create Start");
			
			dis = new DataInputStream(socket.getInputStream());

			if (debug) System.out.println("Client Thread Start");
			
			new Thread(new Runnable() {
				public void run() {
					try {
						if (debug) System.out.println("Read Message Wait");
						String message = dis.readUTF();
						System.out.println("Server : " + message);
					} catch (Exception e) {
						e.printStackTrace();
						if (debug) System.out.println("Client Thread Error");
						disconnect();
					}
				}
			}).start();

			if (debug) System.out.println("Scanner Thread Start");
			
			new Thread(new Runnable() {
				public void run() {
					try {
						if (debug) System.out.println("Scanner Class Create Start");
						
						scanner = new Scanner(System.in);

						if (debug) System.out.println("Scanner While Start");						
						while (!socket.isClosed()) {
							String message = scanner.nextLine();
							System.out.println("sendMessage : " + message);

							if (debug) System.out.println("Client sendMessage Start");
							
							dos.writeUTF(message);
						}
					} catch (Exception e) {
						e.printStackTrace();
						if (debug) System.out.println("Scanner NULL Check");
						if (scanner != null) {
							if (debug) System.out.println("Scanner Close Check");
							scanner.close();
						}
						if (debug) System.out.println("Scanner While Error");
						disconnect();
					}
				}
			}).start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void disconnect() {
		try {
			if (debug) System.out.println("Socket NULL Check");
			if (socket != null) {
				if (debug) System.out.println("Socket Close Start");
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (debug) System.out.println("disconnect() Error");
		}
	}
}
