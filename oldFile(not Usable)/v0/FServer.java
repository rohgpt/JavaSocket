import java.net.*;
import java.rmi.server.SocketSecurityException;
import java.io.*;
import java.util.*;
 
public class FServer {
 
	public static void main(String[] args) {
 
		DatagramSocket ss = null;
		FileInputStream fis = null;
		DatagramPacket rp, sp;
		byte[] rd,future ;
		byte[] prev;
		boolean seq=false;
		InetAddress ip=null;
		int port=0;
		
		try {
			ss = new DatagramSocket(Integer.parseInt(args[0]));
			
			
			System.out.println("Server is up....");


			// read file into buffer
			fis = new FileInputStream("demoText.html");
			future=new byte[512];
			fis.read(future);
			prev=future;
			int consignment;
			String strConsignment;
			int result = 0; // number of bytes read
			
			while(true && result!=-1){
				
				rd=new byte[100];
				future=new byte[512];
				rp = new DatagramPacket(rd,rd.length);
				
				try{
					
					ss.receive(rp);}
				catch(Exception ex){
					sp=new DatagramPacket(prev,prev.length,ip,port);
					ss.send(sp);
					ss.setSoTimeout(3000);
					System.out.println("Sending Same Frame as timeout occur");
					continue;
				}
				// get client's consignment request from DatagramPacket
				ip = rp.getAddress(); 
				port =rp.getPort();
				System.out.println("Client IP Address = " + ip);
				System.out.println("Client port = " + port);

				strConsignment = new String(rp.getData());
				consignment = Integer.parseInt(strConsignment.trim());
				System.out.println("Client ACK = " + consignment);

				// prepare data
				result = fis.read(future);
				if (result == -1) {
					
					String Data=new String(prev);
					prev=(Data.trim()+" END").getBytes();

					consignment = -1;
				}
			
				System.out.println(new String(prev));
				System.out.println("-----------------PREV DONE NEXT ---------");
				System.out.println(prev.length);
				System.out.println(future.toString());

				sp=new DatagramPacket(prev,prev.length,ip,port);
				prev=future;
				 
				ss.send(sp);
				ss.setSoTimeout(3000);
				 
				rp=null;
				sp = null;
				 
				System.out.println("Sent Consignment #" + consignment);
				
			}
			
		} catch (IOException ex) {
			System.out.println(ex.getMessage());

		} finally {
			System.out.println("hello");
			try {
				if (fis != null)
					fis.close();
					
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
		
	}
}