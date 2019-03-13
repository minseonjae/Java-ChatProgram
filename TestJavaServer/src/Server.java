import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;


public class Server {
	
	private static final String ADDRESS = "127.0.0.1";
	private static final int PORT = 2450, PLAYERS = 10;
	
	private static HashMap<String, Player> players = new HashMap<>();
	
	public static boolean debug = true;
	private static Scanner scanner = null;
	
	public static HashMap<String, Player> getPlayers() {
		return players;
	}
	
	private static ServerSocket server = null;
	
	public static void main(String[] args) {
		try {
			if (debug) System.out.println("ServerSocket Create Start");
			
			server = new ServerSocket(PORT, PLAYERS, InetAddress.getByName(ADDRESS));

			if (debug) System.out.println("ServerSocket Create Stop");
			
			System.out.println("서버가 실행됐습니다!");
			
			if (debug) System.out.println("Server Thread Start");
			
			new Thread(new Runnable() {
				public void run() {
					try {
						if (debug) System.out.println("Server While Start");
						
						while (!server.isClosed()) {
							
							if (debug) System.out.println("Client Join Wait");
							
							Socket socket = server.accept();

							if (debug) System.out.println("Client Join");
							
							String address = socket.getInetAddress().getHostAddress();
							
							if (debug) System.out.println("Player Class Create Start");
							
							Player player = new Player();

							if (debug) System.out.println("Player Class Init Start");
							
							player.setStart(player.init(socket, address));

							if (debug) System.out.println("Player Client Thread Start");
							
							player.start();
							
							if (debug) System.out.println("Player Class Save Start");
							
							players.put(address, player);

							if (debug) System.out.println("Player Class Save Stop");
							
							System.out.println("IP : " + address + "에서 접속");
						}
					} catch (Exception e) {
						e.printStackTrace();
						if (debug) System.out.println("Server While Error");
						stopServer();
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
						while (!server.isClosed()) {
							String message = scanner.nextLine();
							System.out.println("Alert : " + message);

							if (debug) System.out.println("Client sendMessage Start");
							
							for (Player p : players.values()) p.sendMessage(message);
							
							if (debug) System.out.println("sendMessage Players " + players.size());
						}
					} catch (Exception e) {
						e.printStackTrace();
						if (debug) System.out.println("Scanner NULL Check");
						if (scanner != null) {
							if (debug) System.out.println("Scanner Close Check");
							scanner.close();
						}
						if (debug) System.out.println("Scanner While Error");
						stopServer();
					}
				}
			}).start();
			
		} catch (Exception e) {
			e.printStackTrace();
			if (debug) System.out.println("main(String[] args) Error");
			stopServer();
		}
	}
	
	public static void stopServer() {
		try {
			for (Player p : players.values()) {
				if (debug) System.out.println("Client Stop Address : " + p.getAddress());
				p.disconnect();
			}if (debug) System.out.println("Server Socket Close Start");
			server.close();
			server = null;
		} catch (Exception e) {
			e.printStackTrace();if (debug) System.out.println("stopServer() Error");
		}
	}
}
