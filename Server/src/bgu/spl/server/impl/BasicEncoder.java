package bgu.spl.server.impl;

import java.nio.charset.Charset;

import bgu.spl.server.Encoder;

public class BasicEncoder implements Encoder {
	private Charset charset;
	
	public BasicEncoder (String charsetDesc)
	{
		charset = Charset.forName(charsetDesc);
	}

	public byte[] toBytes(String str) {
		return str.getBytes(charset);
	}

	public String fromBytes(byte[] buf) {
		return new String(buf,0,buf.length,charset);
	}

	public Charset getCharset() {
		return charset;
	}

}
