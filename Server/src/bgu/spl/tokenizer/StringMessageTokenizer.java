package bgu.spl.tokenizer;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

public interface StringMessageTokenizer {
	
    void addBytes(ByteBuffer bytes);
    
    boolean hasMessage();
    
    String nextMessage();
    
    ByteBuffer getBytesForMessage(String msg)  throws CharacterCodingException;
}
