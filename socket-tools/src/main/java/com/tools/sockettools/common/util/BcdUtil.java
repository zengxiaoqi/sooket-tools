/**
 * Copyright ® 2012 Eastcom Co. Ltd.
 * All right reserved.
 */
package com.tools.sockettools.common.util;

/**
 * by shif
 */
public class BcdUtil {

	/**
	 * Ascii字节数组转换成Bcd字节数组</br> src 源字节数组</br> len 转换后bcd字节数组的长度 不足右补 0</br>
	 */
	public static byte[] asciiToBcd(byte[] src, int len) {
		byte[] bcd = new byte[len];
		byte[] asc = fillBytes(src, len * 2, (byte) 0x30);

		for (int i = 0; i < len; i++) {
			bcd[i] = (byte) ((byte) byteToBcd(asc[i * 2]) << 4 ^ (byteToBcd(asc[i * 2 + 1]) & 0x0f));
		}
		return bcd;
	}
	 /**
   * Ascii字节数组转换成Bcd字节数组</br> src 源字节数组</br> len 转换后bcd字节数组的长度 不足右补 </br>
   */
  public static byte[] asciiToBcd(byte[] src, int len,int fillByte) {
    byte[] bcd = new byte[len];
    byte[] asc = fillBytes(src, len * 2, (byte) fillByte);

    for (int i = 0; i < len; i++) {
      bcd[i] = (byte) ((byte) byteToBcd(asc[i * 2]) << 4 ^ (byteToBcd(asc[i * 2 + 1]) & 0x0f));
    }
    return bcd;
  }
	/**
	 * Ascii字节数组转换成Bcd字节数组</br> src 源字节数组</br> len 转换后bcd字节数组的长度 不足右补 0</br>
	 */
	public static byte[] asciiToBcd(byte[] src) {
		int len = src.length;
		if (len % 2 != 0) {
			len = len / 2 + 1;
		}
		else {
			len = len / 2;
		}
		return asciiToBcd(src, len);
	}

	/**
	 * Bcd字节数组转换成Ascii字节数组</br> src 源字节数组</br>
	 */
	public static byte[] bcdToAscii(byte[] src) {
		byte[] asc = new byte[src.length * 2];
		asc = fillBytes(src, src.length * 2, (byte) 0x00);
		for (int i = 0; i < src.length; i++) {
			asc[i * 2] = bcdToByte((byte) (src[i] >> 4 & 0x0f));
			asc[i * 2 + 1] = bcdToByte((byte) (src[i] & 0x0f));
		}
		return asc;
	}
	
	/**
	 * Bcd字节数组转换成Ascii字节数组.
	 * 若Ascii字节数组长度为奇数位，则需要对最后一个byte进行处理，避免因AsciiToBcd时bcd数组最后一个字节的后4位补0
	 * ，而导致反转的时候得到原始ascii数据失真
	 * 
	 * @param src 源字节数组
	 * @param asciiLenOdd Ascii字节数组长度是否为奇数位
	 * @return
	 */
	public static byte[] bcdToAscii(byte[] src, boolean asciiLenOdd) {
		byte[] ret = bcdToAscii(src);
		if (asciiLenOdd) {
			ret[ret.length-1] = bcdToByte((byte) ((src[src.length - 1] & 0xf0) >> 4));
		}
		return ret;
	}

	/**
	 * Long转换成Bcd字节数组</br> src 源数字</br> len 转换后的字节数组长度 不足右补 0</br> flag
	 * 0--转换成16进制 1--转换成10进制
	 */
	public static byte[] longToBcd(long src, int len, int flag) {
		byte[] re = new byte[len];
		long tmp, high, low;
		if (src < 0)
			throw new RuntimeException(String.format(
					"number: [%d] convert bcd error", src));

		for (int i = len - 1; i >= 0; i--) {
			if (src == 0)
				break;
			if (flag == 1) {
				tmp = src % 100;
				src /= 100;
				high = tmp / 10;
				low = tmp % 10;
			} else {
				tmp = src % 256;
				src /= 256;
				high = tmp / 16;
				low = tmp % 16;

			}
			re[i] = (byte) (high << 4 ^ low);

		}
		return re;
	}

	/**
	 * Int转换成Bcd字节数组</br> src 源数字</br> len 转换后的字节数组长度 不足右补 0</br>
	 */
	public static byte[] intToBcd(int src, int len, int flag) {
		return longToBcd(src, len, flag);
	}

	/**
	 * Short转换成Bcd字节数组</br> src 源数字</br> len 转换后的字节数组长度 不足右补 0</br>
	 */
	public static byte[] shortToBcd(short src, int len, int flag) {
		return longToBcd(src, len, flag);
	}

	/**
	 * Bcd数组转换成Long</br> src 源字节数组</br> flag 0--转换成16进制 1--转换成10进制
	 */
	public static long bcdToLong(byte[] src, int flag) {
		byte high, low;
		long re = 0;

		if (flag == 0) {
			for (int i = 0; i < src.length; i++) {
				re *= 256;
				high = (byte) (src[i] >> 4 & 0x0F);
				low = (byte) (src[i] & 0x0f);
				re += Long.valueOf(high) * 16 + Long.valueOf(low);
			}
		} else {
			for (int i = 0; i < src.length; i++) {
				re *= 100;
				high = (byte) (src[i] >> 4 & 0x0F);
				low = (byte) (src[i] & 0x0f);
				re += Long.valueOf(high) * 10 + Long.valueOf(low);
			}
		}
		return re;
	}

	/**
	 * Bcd数组转换成Int</br> src 源字节数组</br>
	 */
	public static int bcdToInt(byte[] src, int flag) {
		return (int) bcdToLong(src, flag);
	}

	/**
	 * Bcd数组转换成Short</br> src 源字节数组</br>
	 */
	public static short bcdToShort(byte[] src, int flag) {
		return (short) bcdToLong(src, flag);
	}

	/**
	 * Ascii字节转换成Bcd</br>
	 */
	private static byte byteToBcd(byte src) {
		byte re = src;
		if (src <= 0x39 && src >= 0x30)			// 0-9
			re = (byte) (src - 0x30);
		else if (src <= 0x46 && src >= 0x41)	// A-F
			re = (byte) (src - 0x37);
		else if (src <= 0x66 && src >= 0x61)	// a-f
			re = (byte) (src - 0x57);
		return re;
	}

	/**
	 * Bcd字节转换成Ascii</br>
	 */
	private static byte bcdToByte(byte src) {
		byte re = src;
		if (src <= 0x09 && src >= 0x00)			// 0-9
			re = (byte) (src + 0x30);
		else if (src <= 0x0f && src >= 0x0a)	// A-F/a-f
			re = (byte) (src + 0x37);
		return re;
	}

	/**
	 * 尾部填充字节数组</br> len 长度</br> fill 填充字节</br>
	 */
	private static byte[] fillBytes(byte[] src, int len, byte fill) {
		byte[] des = new byte[len];
		int llen = src.length > len ? len : src.length;
		int rlen = src.length < len ? len : src.length;
		for (int i = 0; i < rlen; i++) {
			if (i < llen)
				des[i] = src[i];
			else
				des[i] = fill;
		}
		return des;
	}

	/**
	 * 十六进制格式打印Bcd字符数组</br>
	 */
	public static String dump(byte[] bcd) {
		StringBuilder sbBuilder = new StringBuilder();
		int pre = 0;

		for (int i = 0; i < (bcd.length + 15) / 16; i++) {
			pre = i * 16;
			for (int j = pre; j < pre + 16; j++) {
				if (j >= bcd.length) {
					sbBuilder.append(String.format("   "));
					continue;
				}
				sbBuilder.append(String.format("%02X ", bcd[j]));
			}
			sbBuilder.append("     |      ");
			for (int j = pre; j < pre + 16; j++) {
				if (j >= bcd.length)
					break;
				// 0x20 - 0x7e 可打印
				if (bcd[j] >= 0x20) {
					sbBuilder.append(String.format("%c", bcd[j] & 0x000000ff));
				} else {
					sbBuilder.append("*");
				}
			}
			sbBuilder.append("\n");
		}

		return sbBuilder.toString();
	}

	/**
	 * test
	 */
	public static void main(String[] args) {
		
		byte[] bcd = asciiToBcd("123456ABCDEFabcdef9".getBytes());
		System.out.println(dump(bcd));
		System.out.println(dump(bcdToAscii(bcd, true)));

		// System.out.print(dump(intToBcd(123, 16)));
		// System.out.print(dump(intToBcd(474747, 16)));
		// System.out.print(dump(longToBcd(123, 4)));
		//
		System.out.print((dump(asciiToBcd(
				"1234567890123456789=0508201781999168302".getBytes(), 32))));
		System.out.print((dump(longToBcd(126, 4, 1))));
		System.out.print((bcdToLong(longToBcd(126, 4, 0), 1)));
		//
		// System.out.print(dump(bcdToAscii(asciiToBcd("abc1234".getBytes(),
		// 16))));
		// System.out.println(bcdToLong(longToBcd(123, 16)));
		// System.out.println(bcdToLong(longToBcd(Long.MAX_VALUE, 16)));
		
		System.out.println(bcdToLong(new byte[]{(byte) 0xB3, (byte) 0xB3, (byte) 0xB3, (byte) 0xB3}, 0));
	}

}
