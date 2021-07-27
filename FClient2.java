
import java.net.*;
import java.io.*;
import java.util.*;

public class FClient2 {

    private static final double LOSS_RATE = 0.3;
    private static final int AVERAGE_DELAY = 100;

    public static void main(String[] args) throws InterruptedException {

        DatagramSocket cs = null;
        FileOutputStream fos = null;
        Random random = new Random();

        try {

            cs = new DatagramSocket();
            Scanner sc = new Scanner(System.in);
            int flag = 0;
            byte[] rd, sd,writ;
         
            String reply, ack, data;
            DatagramPacket sp, rp;
            int count = 1;
            int rseq, i, j, temp;
            boolean end = false;
            String req = "";
            String reqArray[] = null;
            String fileType[] = null;
            if (flag == 0) {
                System.out.println("Send File Request:");
                req = sc.nextLine();
                reqArray = req.split(" ");
                flag++;
            }

            if (reqArray[0].equals("REQUEST")) {
                // write received html data
                fileType = reqArray[1].split("\\.");
                fos = new FileOutputStream(fileType[0] + "_recived." + fileType[1]);
                sd = reqArray[1].getBytes();
                String ddata = new String(sd);
                System.out.println(ddata + " " + sd.length);
                sp = new DatagramPacket(sd, sd.length, InetAddress.getByName(args[0]), Integer.parseInt(args[1]));

                cs.send(sp);

                System.out.println("Requesting " + reqArray[1] + " from server" + InetAddress.getByName(args[0]) + ":" + Integer.parseInt(args[1]) + " serverport");
                while (!end) {
                    // delay
                    if (random.nextDouble() < LOSS_RATE) {
                        System.out.println(" No frame received");
                        continue;
                    }
                    //Thread.sleep((int) (random.nextDouble() * 2 * AVERAGE_DELAY));
                    // get next consignment
                    rd = new byte[526];
                    rp = new DatagramPacket(rd, rd.length);
                    cs.receive(rp);
                    reply = new String(rp.getData());
                    // String[] splitted=reply.split(" ");
                    // String[] payload=Arrays.copyOfRange(splitted, 2, splitted.length-1);
                    // String payload="".join(" ", payload)
                    i = reply.indexOf(" ");
                    j = reply.indexOf(" ", reply.indexOf(" ") + 1);
                    rseq = Integer.parseInt(reply.substring(i + 1, j));
                    if (count - rseq == 2) {
                        temp = count - 1;
                        System.out.println("Frame no. " + rseq + " already previously received.So discarding to avoid duplicacy.");
                        ack = "ACK " + temp + " \r\n";
                        // send ACK      
                        sd = ack.getBytes();
                        sp = new DatagramPacket(sd, sd.length, InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
                        cs.send(sp);
                        continue;
                    } else {
                        System.out.println(reply);
                        // concat consignment
                        if (reply.trim().contains("END")) {// if last consignment
                            reply = reply.trim();
                            // int ind=reply.indexOf("END");
                            
                            count = 0;
                            //System.out.println("Message "+reply.length()+" "+reply.charAt(reply.length()-1)+" "+reply.charAt(reply.length()-2));
                            System.out.println(reply.length());
                            // System.out.println(reply);
                            data = reply.substring(j + 1, reply.length()-4);

                            writ=Arrays.copyOfRange(rd, j+1, reply.length()-4);
                            System.out.println("Byte[0]= "+writ[0]);
                            System.out.println("Byte[last]= "+writ[writ.length-1]);
                            Thread.sleep(500);
                            end = true;
                        } else {
                            int sub=0;
                            if(rseq!=0){
                                sub=(int)Math.log10(rseq);
                            }
                            writ=Arrays.copyOfRange(rd, j+1, rd.length - 8+sub);
                            System.out.println("Byte[0]= "+writ[0]);
                            System.out.println("Byte[last]= "+writ[writ.length-1]);
                        }
                        
                        
                        fos.write(writ);
                        ack = "ACK " + count + " \r\n";
                        // send ACK      
                        sd = ack.getBytes();
                        sp = new DatagramPacket(sd, sd.length, InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
                        cs.send(sp);
                        count++;
                    }

                }
            } else {
                System.out.println("Invalid REQUEST FORMAT\n Give input in format of REQUEST fileName CRLF");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());

        } finally {

            try {
                if (fos != null) {
                    fos.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
