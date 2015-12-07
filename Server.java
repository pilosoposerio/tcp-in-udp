import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {
	private static final int MAXIMUM_BUFFER_SIZE = 512;
	private static DatagramSocket serverSocket;
	public static void main(String[] args) {
		if(args.length < 1){
			System.out.println("Usage is: java Server <port>");
		}
		
		int port = 0;
		try{
			port = Integer.parseInt(args[0]);
		}catch(NumberFormatException nfe){
			port = 8080;
		}
		try {
			System.out.println("Binding UDP server to port "+port+"...");
			serverSocket = new DatagramSocket(port);
			System.out.println("Bind successful!");
		} catch (SocketException e) {
			System.out.println("Cannot bind socket to port "+port);
			return;
		}
		
		while(true){
			byte[] buff = new byte[MAXIMUM_BUFFER_SIZE];
			DatagramPacket packet = new DatagramPacket(buff,buff.length);
			
			try {
				System.out.println("UDP Server ready! Waiting for connections...");
				serverSocket.receive(packet);
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
			serverSocket.send(packet);
		} catch (IOException e) {
			System.out.println("Unable to send packet: "+message);
		}
	}
}

class PacketHandler extends Thread{
	private DatagramPacket packet;
	public PacketHandler(DatagramPacket packet){
		this.packet = packet;
	}
	
	@Override
	public void run(){
		/*TODO handle packet*/
		
		//temporarily, just print the data.
		String data = new String(packet.getData());
		System.out.println("Packet Data: "+data);
	}
}

