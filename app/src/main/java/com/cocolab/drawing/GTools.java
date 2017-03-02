package com.cocolab.drawing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GTools {
	static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String ToHexString(byte[] bytes, int Length) {
		if (bytes == null)
			return "";

		char[] chars = new char[Length * 5];
		for (int i = 0; i < Length; i++) {
			int b = bytes[i];
			chars[i * 5] = '0';
			chars[i * 5 + 1] = 'x';
			chars[i * 5 + 2] = hexDigits[(b >> 4 & 0xf)];
			chars[i * 5 + 3] = hexDigits[b & 0xF];
			chars[i * 5 + 4] = ',';
		}
		return new String(chars);
	}

	public static String ToHexString(byte bytes) {
		char[] chars = new char[2];

		int b = bytes;
		chars[0] = hexDigits[bytes >> 4];
		chars[1] = hexDigits[bytes & 0xF];
		return new String(chars);
	}

	// public static void ushortToByteArray(ushort val, int sindex, byte[] data)
	public static void ushortToByteArray(int val, int sindex, byte[] data) {
		data[sindex + 1] = (byte) (((val & 0xff00) >> 8) & 0xff);
		data[sindex] = (byte) (val & 0xff);
	}

	public static int byteArrayToUshort(int sindex, byte[] data) {
		int val;
		val = (int) (((data[sindex + 1] & 0xff) << 8) & 0xff00);
		val += (int) (data[sindex] & 0xff);
		return (val & 0xffff);
	}

	public static void shortToByteArray(short val, int sindex, byte[] data) {
		data[sindex + 1] = (byte) (((val & 0xff00) >> 8) & 0xff);
		data[sindex] = (byte) (val & 0xff);
	}

	public static short byteArrayToShort(int sindex, byte[] data) {
		short val;
		val = (short) (((data[sindex + 1] & 0xff) << 8) & 0xff00);
		val += (short) (data[sindex] & 0xff);
		return val;
	}

	// public static void UInt32ToByteArray(UInt32 val, int sindex, byte[] data)
	public static void UInt32ToByteArray(int val, int sindex, byte[] data) {
		data[sindex + 3] = (byte) ((val & 0xff000000) >> 24);
		data[sindex + 2] = (byte) ((val & 0x00ff0000) >> 16);
		data[sindex + 1] = (byte) ((val & 0x0000ff00) >> 8);
		data[sindex] = (byte) (val & 0x000000ff);
	}

	public static void Int32ToByteArray(int integer, int sindex, byte[] data) {
		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 4);
		buff.putInt(integer);
		buff.order(ByteOrder.BIG_ENDIAN);

		System.arraycopy(buff.array(), 0, data, sindex, 4);
		// return buff.array();
	}

	public static int byteArrayToInt32(int sindex, byte[] data) {
		byte[] bytes = new byte[4];
		System.arraycopy(data, sindex, bytes, 0, 4);

		final int size = Integer.SIZE / 8;
		ByteBuffer buff = ByteBuffer.allocate(size);
		final byte[] newBytes = new byte[size];
		for (int i = 0; i < size; i++) {
			if (i + bytes.length < size) {
				newBytes[i] = (byte) 0x00;
			} else {
				newBytes[i] = bytes[i + bytes.length - size];
			}
		}
		buff = ByteBuffer.wrap(newBytes);
		buff.order(ByteOrder.BIG_ENDIAN);
		return buff.getInt();
	}

	public static void LongToByteArrayforInno(long integer, int sindex, byte[] data) {
		UInt32ToByteArray((int)integer, sindex, data);
		UInt32ToByteArray((int)(integer >> 32), sindex + 4, data);
	}

	public static void LongToByteArray(long integer, int sindex, byte[] data) {
		ByteBuffer buff = ByteBuffer.allocate(Long.SIZE / 8);
		buff.putLong(integer);
		buff.order(ByteOrder.BIG_ENDIAN);

		System.arraycopy(buff.array(), 0, data, sindex, 8);
	}
	
	public static byte[] toByteArray(Number data) {
		Class<? extends Number> dataType = data.getClass();
		int length;
		long value;
		if (Byte.class == dataType) {
			length = Byte.SIZE / Byte.SIZE;
			value = (Byte) data;
		} else if (Short.class == dataType) {
			length = Short.SIZE / Byte.SIZE;
			value = (Short) data;
		} else if (Integer.class == dataType) {
			length = Integer.SIZE / Byte.SIZE;
			value = (Integer) data;
		} else if (Long.class == dataType) {
			length = Long.SIZE / Byte.SIZE;
			value = (Long) data;
		} else
			throw new IllegalArgumentException(
					"Parameter must be one of the following types:\n Byte, Short, Integer, Long");
		byte[] byteArray = new byte[length];
		for (int i = 0; i < length; i++) {
			byteArray[i] = (byte) ((value >> (8 * (length - i - 1))) & 0xff);
		}
		return byteArray;
	}

	public static long byteArrayToLong(int sindex, byte[] data) {
		final int size = Long.SIZE / 8;

		byte[] bytes = new byte[size];
		System.arraycopy(data, sindex, bytes, 0, size);

		ByteBuffer buff = ByteBuffer.allocate(size);
		final byte[] newBytes = new byte[size];
		for (int i = 0; i < size; i++) {
			if (i + bytes.length < size) {
				newBytes[i] = (byte) 0x00;
			} else {
				newBytes[i] = bytes[i + bytes.length - size];
			}
		}
		buff = ByteBuffer.wrap(newBytes);
		buff.order(ByteOrder.BIG_ENDIAN);
		return buff.getLong();
	}

	public static float byteArrayToFloat(int sindex, byte[] data) {
		// float val;
		//
		// // val = (float)(((int)data[sindex + 3] << 24) & 0xff000000);
		// // val += (float)(((int)data[sindex + 2] << 16) & 0x00ff0000);
		// // val += (float)(((int)data[sindex + 1] << 8) & 0x0000ff00);
		// // val += (float)data[sindex];
		// val = (float)((((long)(data[sindex + 3] & 0xff)) << 24) &
		// 0x00ff000000);
		// val += (float)(((data[sindex + 2] & 0xff) << 16) & 0x00ff0000);
		// val += (float)(((data[sindex + 1] & 0xff) << 8) & 0x0000ff00);
		// val += (float)((data[sindex] & 0xff) & 0x000000ff);
		//
		// return val;
		int i = 0;
		int len = 4;
		int cnt = 0;
		byte[] tmp = new byte[len];

		for (i = sindex; i < (sindex + len); i++) {
			tmp[cnt] = data[i];
			cnt++;
		}

		int accum = 0;
		i = 0;
		for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
			accum |= ((long) (tmp[i] & 0xff)) << shiftBy;
			i++;
		}
		return Float.intBitsToFloat(accum);

	}
	
	public static void floatToByteArray(float value, int sindex, byte[] data)
	{
		byte[] array = new byte[4];
	       
        int intBits=Float.floatToIntBits(value);
       
        array[0]=(byte)((intBits&0x000000ff)>>0);
        array[1]=(byte)((intBits&0x0000ff00)>>8);
        array[2]=(byte)((intBits&0x00ff0000)>>16);
        array[3]=(byte)((intBits&0xff000000)>>24);
       
        //return array;
        System.arraycopy(array, 0, data, sindex, 4);
	}

	/*
	 * public static void UInt64ToByteArray(long val, int sindex, byte[] data) {
	 * data[sindex + 7] = (byte)((val & 0xff00000000000000) >> 56); data[sindex
	 * + 6] = (byte)((val & 0x00ff000000000000) >> 48); data[sindex + 5] =
	 * (byte)((val & 0x0000ff0000000000) >> 40); data[sindex + 4] = (byte)((val
	 * & 0x000000ff00000000) >> 32); data[sindex + 3] = (byte)((val &
	 * 0x00000000ff000000) >> 24); data[sindex + 2] = (byte)((val &
	 * 0x0000000000ff0000) >> 16); data[sindex + 1] = (byte)((val &
	 * 0x000000000000ff00) >> 8); data[sindex] = (byte)(val &
	 * 0x00000000000000ff); }
	 */

	public static long byteArrayToUInt32(int sindex, byte[] data) {
		long val = 0;

		val = (long) ((((long) (data[sindex + 3] & 0xff)) << 24) & 0x00ff000000);
		val += (long) (((data[sindex + 2] & 0xff) << 16) & 0x00ff0000);
		val += (long) (((data[sindex + 1] & 0xff) << 8) & 0x0000ff00);
		val += (long) ((data[sindex] & 0xff) & 0x000000ff);

		return val;
	}

	public static long byteArrayToUInt64(int sindex, byte[] data) {
		long val = 0;

		for (int i = 0; i < 8; i++) {
			val <<= 8;
			val ^= (long) (data[i + sindex] & 0xFF);
		}

		return val;
	}

	public static void UInt32ToByteArrayByLength(int val, int length,
			int sindex, byte[] data) {
		if (length >= 4)
			data[sindex + 3] = (byte) ((val & 0xff000000) >> 24);
		if (length >= 3)
			data[sindex + 2] = (byte) ((val & 0x00ff0000) >> 16);
		if (length >= 2)
			data[sindex + 1] = (byte) ((val & 0x0000ff00) >> 8);
		if (length >= 1)
			data[sindex] = (byte) (val & 0x000000ff);
	}

	/*
	 * public static void stringToByteArray(String val, int sindex, int maxSize,
	 * byte[] array) { //if((val.Length + sindex) > array.Length) // return;
	 * 
	 * int i = 0; for (i = 0; i < val.Length; i++) { if (array.Length <= i +
	 * sindex) break;
	 * 
	 * array[sindex + i] = (byte)val[i]; }
	 * 
	 * for (; i < maxSize; i++) array[sindex + 1] = 0x00;
	 * 
	 * }
	 * 
	 * public static void charToByteArray(char[] val, int sindex, int maxSize,
	 * byte[] array) { //if((val.Length + sindex) > array.Length) // return;
	 * 
	 * int i = 0, j = 0; for (i = 0; i < val.Length; i++) { if (array.Length <=
	 * j + sindex) break;
	 * 
	 * array[sindex + j++] = (byte)(val[i] & 0x00ff); array[sindex + j++] =
	 * (byte)((val[i] >> 8) & 0xff); }
	 * 
	 * 
	 * }
	 * 
	 * 
	 * public static String byteArrayToString(int sindex, int strlength, byte[]
	 * array) { String val = String.Empty;
	 * 
	 * if ((strlength + sindex) > array.Length) return String.Empty;
	 * 
	 * for (int i = 0; i < strlength; i++) { val += (char)array[sindex + i]; }
	 * return val; }
	 */

	static byte GetDigits(char c) {
		switch (c) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'A':
			return 10;
		case 'B':
			return 11;
		case 'C':
			return 12;
		case 'D':
			return 13;
		case 'E':
			return 14;
		case 'F':
			return 15;
		}
		return 0;
	}

	public static byte[] StringToHex(String strHex) {
		byte[] msg = new byte[strHex.length() / 2];

		for (int i = 0; i < strHex.length() / 2; i++) {
			int b = 0;

			byte b0 = GetDigits(strHex.charAt(i * 2 + 1));
			byte b1 = GetDigits(strHex.charAt(i * 2));

			b = (b1 * 16 + b0);

			msg[i] = (byte) (b & 0xFF);
		}

		return msg;
	}
}
