package models;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


 /*
     message size: 4 bytes
     request header:
     request api key: 2 bytes
     request api version: 2 bytes
     correlation id: 4 bytes
*/

@AllArgsConstructor
@Getter
public class RequestHeaderV2 {

     private int message_size;
     private short api_key;
     private short api_version;
     private int correlation_id;



     public RequestHeaderV2 (BufferedInputStream inputStream) throws IOException {
          byte[] message_size_bytes = inputStream.readNBytes(4);
          this.message_size = this.convertTo(message_size_bytes, Integer.class);
          this.api_key = this.convertTo(inputStream.readNBytes(2), Short.class);
          this.api_version = this.convertTo(inputStream.readNBytes(2), Short.class);
          this.correlation_id = this.convertTo(inputStream.readNBytes(4), Integer.class);


     }

     private <T> T convertTo(byte[] bytes, Class<T> type){
          ByteBuffer buffer = ByteBuffer.wrap(bytes);

          return switch (type.getSimpleName()){
               case "Integer" -> type.cast((buffer.getInt()));
               case "Short" -> type.cast((buffer.getShort()));
               default -> throw new IllegalArgumentException("Unsupported type: " + type);
          };
     }


}
