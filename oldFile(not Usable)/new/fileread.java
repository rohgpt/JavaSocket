import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class fileread {
    public static void main(String[] args) throws IOException {
        FileInputStream s= new FileInputStream("demoPDF.pdf");
        FileOutputStream o=new FileOutputStream("demoPDF2.pdf");

        byte[] currentChunk=new byte[520];
        
        int result=0;
        while(result!=-1){
            result=s.read(currentChunk);
            String str=new String(currentChunk);
            System.out.println("chunk ---------");
            System.out.println(str);
            o.write(currentChunk);
        }

        
    }
}
