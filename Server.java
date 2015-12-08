import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

public class Server {
	private static final int MAXIMUM_BUFFER_SIZE = 512;
	private static final String INPUT_DATA_FILE_NAME = "data.in";
	private static DatagramSocket serverSocket;
	private static State state = State.NONE;
	private static int ACK_NUM = 0;
	private static int SYNC_NUM = 0;
	private static int WINDOW_SIZE = 0;
	private static String INPUT_DATA = "";
	public static void main(String[] args) {
		if(args.length < 1){
			System.out.println("Usage is: java Server <port>");
			return;
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
		
		System.out.println("UDP listener for this server started!");
		listener.start();
	}
	
	private static Thread listener = new Thread(new Runnable(){
		@Override
		public void run() {
			while(true){
				byte[] buff = new byte[MAXIMUM_BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(buff,buff.length);
				
				try {
					serverSocket.receive(packet);
					Packet p = Packet.valueOf(new String(buff).trim());
					if(state == State.NONE){
						
						if(p.isSyncFlag()){ //first packet must be a SYN packet
							System.out.println("Threeway handshake 1/3.");
							InetAddress clientAddress = packet.getAddress();
							int clientPort = packet.getPort();

							//send ACK+SYN packet
							Packet ackSyncPacket = new Packet();
							ackSyncPacket.setAckFlag(true);
							ACK_NUM = p.getSyncNum()+1;
							ackSyncPacket.setAckNum(ACK_NUM);

							SYNC_NUM = ThreadLocalRandom.current().nextInt(1, 5000);
							ackSyncPacket.setSyncFlag(true);
							ackSyncPacket.setSyncNum(SYNC_NUM);

							send(clientAddress, clientPort, ackSyncPacket.toString());
							state = State.SYN_RECV;
							System.out.println("Threeway handshake 2/3.");
						}
					}else if(state == State.SYN_RECV){
						if(p.isAckFlag() && p.getAckNum() == SYNC_NUM+1){ //must be an ACK packet and valid ack
							WINDOW_SIZE = p.getWindowSize();
							state = State.ESTABLISHED;
							System.out.println("Threeway handshake 3/3.");
						}
					}else if(state == State.ESTABLISHED){
						//get the data
						Scanner in = new Scanner(new File(INPUT_DATA_FILE_NAME));
						while(in.hasNextLine()){
							INPUT_DATA += in.nextLine();
						}
						in.close();
						System.out.println("Data loaded was:\n"+INPUT_DATA);
						//send the data
					}

					

				} catch(FileNotFoundException fnfe){
					System.out.println("File "+INPUT_DATA_FILE_NAME+" not found! Needed for the program!");
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

