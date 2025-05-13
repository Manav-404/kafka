import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Main {
  public static void main(String[] args){

     ServerSocket serverSocket = null;
     Socket clientSocket = null;
     int port = 9092;
     try {
       serverSocket = new ServerSocket(port);

       serverSocket.setReuseAddress(true);
       clientSocket = serverSocket.accept();

       /*
       The request contains:
       message size: 4 bytes
       request header:
        request api key: 2 bytes
        request api version: 2 bytes
        correlation id: 4 bytes

        Goal: Find correlation id and respond

        */

         InputStream inputStream = clientSocket.getInputStream();
         byte[] messageSizeBytes = inputStream.readNBytes(4);
         byte[] apiKeyBytes = inputStream.readNBytes(2);
         byte[] apiVersion = inputStream.readNBytes(2);
         byte[] correlationIdBytes = inputStream.readNBytes(4);

         int correlationId = ByteBuffer.wrap(correlationIdBytes).getInt();

         var response = ByteBuffer.allocate(4).putInt(correlationId).array();
         clientSocket.getOutputStream().write(response);


     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     } finally {
       try {
         if (clientSocket != null) {
           clientSocket.close();
         }
       } catch (IOException e) {
         System.out.println("IOException: " + e.getMessage());
       }
     }
  }
}
