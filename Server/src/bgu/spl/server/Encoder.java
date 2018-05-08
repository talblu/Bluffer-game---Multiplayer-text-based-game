package bgu.spl.server;
import java.nio.charset.Charset;

public interface Encoder {
	byte[] toBytes (String str);
	
	String fromBytes (byte[] buf);
	
	Charset getCharset();
}
