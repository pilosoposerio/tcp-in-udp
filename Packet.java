
public class Packet {
	private boolean SYN_FLAG;
	private boolean ACK_FLAG;
	private boolean FIN_FLAG;
	private int SYN_NUM;
	private int ACK_NUM;
	private int WINDOW_SIZE;
	private String data;
	
	public Packet(boolean syncFlag, 
			boolean ackFlag, 
			boolean finFlag, 
			int synNum, 
			int ackNum, 
			int windowSize, 
			String data){
		
		this.SYN_FLAG = syncFlag;
		this.ACK_FLAG = ackFlag;
		this.FIN_FLAG = finFlag;
		
		this.SYN_NUM = synNum;
		this.ACK_NUM = ackNum;
		this.WINDOW_SIZE = windowSize;
		
		this.data = data;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("SYNF=").append(SYN_FLAG?1:0).append(";").
		append("SYNN=").append(SYN_NUM).append(";").
		append("ACKF=").append(ACK_FLAG?1:0).append(";").
		append("ACKN=").append(ACK_NUM).append(";").
		append("FINF=").append(FIN_FLAG?1:0).append(";").
		append("WINDOW=").append(WINDOW_SIZE).append(";"). 
		append("DATA=").append(data);
		
		return builder.toString();
	}
	
	public static Packet valueOf(String packetData){
		boolean syncFlag, ackFlag, finFlag;
		int synNum, ackNum, winSize;
		
		syncFlag=ackFlag=finFlag=false;
		synNum=ackNum=winSize=0;
		String data="";
		
		String[] attribs = packetData.split(";");
		
		for(String token: attribs){
			String[] pair = token.split("=");
			switch(pair[0]){
				case "SYNF":
					syncFlag = pair[1]=="1";
					break;
				case "ACKF":
					ackFlag = pair[1]=="1";
					break;
				case "FINF":
					ackFlag = pair[1]=="1";
					break;
				case "SYNN":
					synNum = Integer.parseInt(pair[1]);
					break;
				case "ACKN":
					ackNum = Integer.parseInt(pair[1]);
					break;
				case "WINDOW":
					winSize = Integer.parseInt(pair[1]);
					break;
				case "DATA":
					data = pair[1];
					break;
			}
		}
		return new Packet(syncFlag, ackFlag, finFlag, synNum, ackNum,winSize, data);
	}
	
}

