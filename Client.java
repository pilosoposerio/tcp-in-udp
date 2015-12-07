import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

	private static final int MAXIMUM_BUFFER_SIZE = 512;
	private static DatagramSocket clientSocket;
	private static InetAddress serverAddress;
	private static int serverPort=0;
	private static int selfPort=0;
	public static void main(String[] args) {
		if(args.length < 2){
			System.out.println("Usage is: java Server <own port> <server address> <server port>");
		}
		try{
			selfPort = Integer.parseInt(args[0]);
		}catch(NumberFormatException nfe){
			selfPort = 0;
		}
		
		try{
			serverAddress = InetAddress.getByName(args[1]);
			serverPort = Integer.parseInt(args[2]);
		}catch(NumberFormatException nfe){
			serverPort = 8080;
		}catch(UnknownHostException uhe){
			try {
				serverAddress = InetAddress.getByName("localhost");
			} catch (UnknownHostException e) {
				System.out.println("Server address is unknown!");
				return;
			}
		}
		
		try {
			System.out.println("Binding UDP server to port "+selfPort+"...");
			clientSocket = new DatagramSocket(selfPort);
			System.out.println("Bind successful!");
		} catch (SocketException e) {
			System.out.println("Cannot bind socket to port "+selfPort);
			return;
		}
		
		while(true){
			byte[] buff = new byte[MAXIMUM_BUFFER_SIZE];
			DatagramPacket packet = new DatagramPacket(buff,buff.length);
			
			try {
				System.out.println("UDP Server ready! Waiting for connections...");
				clientSocket.receive(packet);
				new PacketHandler(packet).start();
			} catch (IOException e) {
				System.out.println("Failed to receive a packet... "+e.getMessage());
			}
		}
	}
	
	public static void send(InetAddress address, int port, String message){
		byte[] buff = message.getBytes();
		DatagramPacket packet = new DatagramPacket(buff, buff.length,address,port);
		try {
			clientSocket.send(packet);
		} catch (IOException e) {
			System.out.println("Unable to send packet: "+message);
		}
	}
}


