import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

public class Client {

	private static final int MAXIMUM_BUFFER_SIZE = 512;
	private static DatagramSocket clientSocket;
	private static InetAddress serverAddress;
	private static int serverPort=0;
	private static int selfPort=0;
	private static State state = State.NONE;
	private static int ACK_NUM = 0;
	private static int SYNC_NUM = 0;
	public static void main(String[] args) {
		if(args.length < 2){
			System.out.println("Usage is: java Server <own port> <server address> <server port>");
			return;
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
		
		//START LISTENER
		listener.start();
		System.out.println("UDP listener for this client started!");
		
		//START MAIN PROCESS
		/*INITIALIZE THREE WAY HANDSHAKE BY SENDING A SYN PACKET*/
		Packet syncPacket = new Packet();
		syncPacket.setSyncFlag(true);
		SYNC_NUM = ThreadLocalRandom.current().nextInt(1, 5000);
		syncPacket.setSyncNum(SYNC_NUM);
		send(syncPacket.toString());
		state = State.SYN_SEND;
		System.out.println("Threeway handshake 1/3.");
	}
	
	private static Thread listener = new Thread(new Runnable(){
		@Override
		public void run() {
			while(true){
				byte[] buff = new byte[MAXIMUM_BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(buff,buff.length);
				
				try {
					clientSocket.receive(packet);
					Packet p = Packet.valueOf(new String(buff).trim());
					
					if(state == State.SYN_SEND){ //first packet must be an ACK+SYN packet
						if(p.getAckNum() == SYNC_NUM +1){ //ack must be valid
							System.out.println("Threeway handshake 2/3.");
							//send ACK packet to server
							Packet ackPacket = new Packet();
							SYNC_NUM = p.getAckNum();
							ackPacket.setSyncFlag(true);
							ackPacket.setSyncNum(SYNC_NUM);

							ACK_NUM = p.getSyncNum() + 1;
							ackPacket.setAckFlag(true);
							ackPacket.setAckNum(ACK_NUM);

							send(ackPacket.toString());
							state = State.ESTABLISHED;
							System.out.println("Threeway handshake 3/3.");
						}
					}else if(state == State.ESTABLISHED){
						//receive the data
					}

				} catch (IOException e) {
					System.out.println("Failed to receive a packet... "+e.getMessage());
				}
			}
		}
	});
	
	//sends to server by default
	private static void send(String message){
		send(serverAddress, serverPort, message);
	}

	private static void send(InetAddress address, int port, String message){
		byte[] buff = message.getBytes();
		DatagramPacket packet = new DatagramPacket(buff, buff.length,address,port);
		try {
			clientSocket.send(packet);
		} catch (IOException e) {
			System.out.println("Unable to send packet: "+message);
		}
	}
}


