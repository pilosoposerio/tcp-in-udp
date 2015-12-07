import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ThreadLocalRandom;

public class Server {
	private static final int MAXIMUM_BUFFER_SIZE = 512;
	private static DatagramSocket serverSocket;
	private static State state = State.NONE;
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
		
		listener.start();
	}
	
	private static Thread listener = new Thread(new Runnable(){
		@Override
		public void run() {
			while(true){
				byte[] buff = new byte[MAXIMUM_BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(buff,buff.length);
				
				try {
					System.out.println("UDP Server ready! Waiting for connections...");
					serverSocket.receive(packet);
					Packet p = Packet.valueOf(new String(buff).trim());
					if(state == State.NONE){
						
						if(p.isSyncFlag()){ //packet myst be a SYN packet
							InetAddress clientAddress = packet.getAddress();
							int clientPort = packet.getPort();

							//send ACK+SYN packet
							Packet ackSyncPacket = new Packet();
							ackSyncPacket.setAckFlag(true);
							ackSyncPacket.setAckNum(p.getSyncNum()+1);
							ackSyncPacket.setSyncFlag(true);
							ackSyncPacket.setSyncNum(ThreadLocalRandom.current().nextInt(1, 5000));
							send(clientAddress, clientPort, ackSyncPacket.toString());
							state = State.SYN_RECV;
						}
					}

				} catch (IOException e) {
					System.out.println("Failed to receive a packet... "+e.getMessage());
				}
			}
		}
	});
	
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

