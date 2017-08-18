package globaldev.finotek.com.logcollector.util;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * AES instance with one key and one iv for preformance.
 * mode: AES/CBC/PKCS5Padding
 * text input encoding: utf-8
 * text output encoding: base64
 */
public class AesInstance {
	byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private Cipher encCipher = null;
	private Cipher decCipher = null;

	private AesInstance(byte[] key) throws Exception {
		this.encCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		this.decCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		IvParameterSpec ivc = new IvParameterSpec(iv);
		this.encCipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivc);
		this.decCipher.init(Cipher.DECRYPT_MODE, skeySpec, ivc);
	}

	public static AesInstance getInstance(byte[] key) throws Exception {
		return new AesInstance(key);
	}

	public byte[] encBytes(byte[] srcBytes) throws Exception {
		return encCipher.doFinal(srcBytes);
	}

	public byte[] decBytes(byte[] srcBytes) throws Exception {
		return decCipher.doFinal(srcBytes);
	}

	public String encText(String srcStr) throws Exception {
		byte[] srcBytes = srcStr.getBytes("utf-8");
		byte[] encrypted = encBytes(srcBytes);
		return Base64.encode(encrypted);
	}

	public String decText(String srcStr) throws Exception {
		byte[] srcBytes = Base64.decode(srcStr);
		byte[] decrypted = decBytes(srcBytes);
		return new String(decrypted, "utf-8");
	}
}
    
   
   