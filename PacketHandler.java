import java.net.DatagramPacket;

public class PacketHandler extends Thread{
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

