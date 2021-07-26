
import java.net.*;
import java.rmi.server.SocketSecurityException;
import java.io.*;
import java.util.*;


public class FServer {

    private DatagramSocket sock_et;
    private byte[] response;
    private byte[] reply;
    private byte[] snd2;
    private byte[] toSnd;
    private DatagramPacket rec_packet, send_packet;
    private InetAddress rec_ip;
    private int rec_port;

    private String rec_data;

    private int prev_seq_no = -1;
    private int curr_seq = 0;
  

    private FileInputStream clientFile;
    private byte[] currentChunk;
    private byte[] nextChunk;
    public boolean End = false;
    private int result = 512;

    public FServer(int serverPort) throws SocketException {
        
        this.sock_et = new DatagramSocket(serverPort);
        // this.sock_et.setSoTimeout(30);
        this.response = new byte[100];

        this.currentChunk = new byte[512];
        this.nextChunk = new byte[512];

    }

    public int getSequenceNo(String recData) {
        String[] splittedData = recData.split(" ");
        int seqNo = Integer.parseInt(splittedData[1]);
        return seqNo;
    }

    private int readChunk() throws IOException {

        this.nextChunk = new byte[512];
        return this.clientFile.read(this.nextChunk);
    }

    public int sendReply() throws IOException {

        if (curr_seq <= prev_seq_no) {
            if (result == -1 && curr_seq < prev_seq_no) 
                    this.End = true; 
        } else {
            this.currentChunk = new byte[512];
            this.currentChunk = this.nextChunk.clone();
            int prevResult=result;
            result = this.readChunk();

            System.out.println("Byte= " + result);
            String tmp = "RDT " + this.curr_seq + " ";
            byte[] snd1=tmp.getBytes();

            // String str_data = new String(this.nextChunk);
            // System.out.println(str_data);
            
            if (result == -1) {

                byte[] snd3= " END \r\n".getBytes();
                snd2 = Arrays.copyOfRange(this.currentChunk,0,prevResult);
                toSnd=new byte[snd1.length+snd2.length+snd3.length];
                
                int k=0;
                for(int i=0;i<snd1.length;i++){
                    toSnd[k++]=snd1[i];
                }
                for(int i=0;i<snd2.length;i++){
                    toSnd[k++]=snd2[i];
                }
                for(int i=0;i<snd3.length;i++){
                    toSnd[k++]=snd3[i];
                }

                this.clientFile.close();
            } else {
                byte[] snd3= " \r\n".getBytes();
                snd2 = Arrays.copyOfRange(this.currentChunk,0,prevResult);
                
                toSnd=new byte[snd1.length+snd2.length+snd3.length];
                
                int k=0;
                for(int i=0;i<snd1.length;i++){
                    toSnd[k++]=snd1[i];
                }
                for(int i=0;i<snd2.length;i++){
                    toSnd[k++]=snd2[i];
                }
                for(int i=0;i<snd3.length;i++){
                    toSnd[k++]=snd3[i];
                }

            }
            reply = toSnd;
            this.prev_seq_no = this.curr_seq;
        }
        this.send_packet = new DatagramPacket(reply, reply.length, this.rec_ip, this.rec_port);

        // System.out.println("Byte[0]= " + reply[6]);
        // System.out.println("Byte[last]= " + reply[497]);
        if(this.curr_seq>0)
        System.out.println("Byte[0]= " + reply[6+(int)Math.log10(this.curr_seq)]);
        else  System.out.println("Byte[0]= " + reply[6]);
        if(result==-1){
            System.out.println("Byte[last]= " + reply[reply.length - 8]);
        }
        else
        System.out.println("Byte[last]= " + reply[reply.length - 4]);
        System.out.println("total length sent: " + reply.length);
        this.sock_et.send(send_packet);
        
        return result;
    }

    public void getRespose() throws Exception {
        this.response = new byte[100];
        this.rec_packet = new DatagramPacket(response, response.length);
        this.sock_et.receive(this.rec_packet);

        this.rec_ip = rec_packet.getAddress();
        this.rec_port = rec_packet.getPort();
        System.out.println("Client IP Address = " + rec_ip);
        System.out.println("Client port = " + rec_port);

        this.rec_data = new String(rec_packet.getData());

        this.rec_data = rec_data.trim();

        if (prev_seq_no == -1) {
            this.clientFile = new FileInputStream(rec_data);
            this.sock_et.setSoTimeout(30);
            System.out.println("Byte= " + this.readChunk());
            System.out.println("Filename " + rec_data);

        } else {
            System.out.println(rec_data);
            this.curr_seq = this.getSequenceNo(rec_data);
        }

    }

    public static void main(String[] args) {

        int serverPort = Integer.parseInt(args[0]);
        int result = 0;
        try {
            FServer fs = new FServer(serverPort);
            System.out.println("Server is up");
            boolean end = fs.End;
            while (true && !fs.End) {
                try {
                    fs.getRespose();

                } catch (SocketTimeoutException e) {
                    System.out.println("TimeOut Occur");
                } catch (IOException e) {
                    System.out.println("Respond Not Received Or given FileName not exist");
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("other Exception");
                    System.out.println(e.getMessage());
                }
                try {
                    result = fs.sendReply();
                } catch (Exception e) {

                }

            }
            try {
                fs.clientFile.close();
            } catch (IOException ex) {
                System.out.println("Failed to close file,reason may be file pointing to null object");
                System.out.println(ex.getMessage());
            }

        } catch (SocketException ex) {
            System.out.println("Socket Exception Occur");
        } catch (Exception ex) {
            System.out.println("Other Exception Occur");
            System.out.println(ex.getMessage());
        }
    }

}
