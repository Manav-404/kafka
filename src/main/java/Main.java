import common.ErrorCodes;
import models.RequestV2;

import lombok.*;

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

         BufferedInputStream inputStream = new BufferedInputStream(clientSocket.getInputStream());
         RequestV2 headerV2 = new RequestV2(inputStream);


         clientSocket.getOutputStream().write(ByteBuffer.allocate(4).putInt(headerV2.getMessage_size()).array());

         var response = ByteBuffer.allocate(4).putInt(headerV2.getCorrelation_id()).array();
         clientSocket.getOutputStream().write(response);

         short apiVersion = headerV2.getApi_version();
         short errorCode = (short) (apiVersion>=0 && apiVersion<=4 ? 0: ErrorCodes.UNSUPPORTED_VERSION.getCode());
         var errorCodeResponse = ByteBuffer.allocate(2).putShort(errorCode).array();
         clientSocket.getOutputStream().write(errorCodeResponse);


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
