package com.tools.sockettools.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Administrator
 * 
 *         2013-1-17下午1:32:39
 */
public class StringUtil {
	private static String hexString = "0123456789ABCDEF";

	/**
	 * 删指定长度字符串 从指定位置删除字符串的指定长度，如果为正数，则右删除，如果为负数，则左删除
	 * 
	 * @param str
	 * @param index
	 *            起始位置，从0开始，如果起始位置超出字符串长度，起始位置等于字符串的最末
	 * @param len
	 *            指定长度
	 * @return 删除后的字符串
	 */
	public static String deleteString(String str, int index, int len) {
		if (isNullOrBlank(str)) {
			return str;
		}
		index = index > str.length() ? str.length() - 1 : index;// 如果起始位置超出字符串长度，起始位置等于字符串的最末下标
		String deledStr = null;
		int start = 0;
		int end = 0;
		if (len < 0) {// 向左删除
			start = index + len + 1;// 计算起始位置，因长度为负数，且截取的字符串包含起始位置，计算公式=index -
			// len + 1
			end = 0 - len + 1;// 计算结束位置，因截取的字符串不包含结束位置，计算公式=index + 1
		} else {// 向右删除
			start = index;
			end = index + len;// 计算结束位置，因截取的字符串不包含结束位置且从0计算下标，计算公式=index + len
		}
		start = start < 0 ? 0 : start;// 截取位置<0,说明起始位置超出字符串长度，取最开始的位置
		end = end > str.length() ? str.length() : end;
		deledStr = str.substring(start, end);
		return str.replaceAll(deledStr, "");
	}

	/**
	 * 删字符串左空格
	 * 
	 * @param str
	 *            要进行去掉左边空格的字符串
	 * @return 去掉左边空格后的字符串
	 */
	public static String ltrim(String s) {
		if (isNullOrBlank(s)) {
			return s;
		}
		int len = s.length();
		int st = 0;
		char[] val = s.toCharArray();

		while ((st < len) && (val[st] <= ' ')) {
			st++;
		}
		return (st > 0) ? s.substring(st, len) : s;
	}

	/**
	 * 删字符串右空格
	 * 
	 * @param str
	 *            要进行去掉右边空格的字符串
	 * @return 去掉右边空格后的字符串
	 */
	public static String rtrim(String s) {
		if (isNullOrBlank(s)) {
			return s;
		}
		int len = s.length();
		int st = 0;
		char[] val = s.toCharArray();

		while ((st < len) && (val[len - 1] <= ' ')) {
			len--;
		}
		return (len < s.length()) ? s.substring(st, len) : s;
	}

	/**
	 * 判断字符串是否为null或空串
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return 为空或空白返回true否者返回false
	 */
	public static boolean isNullOrBlank(String str) {
		if (str == null || "".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断字符串是否为null或空白串
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return 为空或空白返回true否者返回false
	 */
	public static boolean isNullOrTrimBlank(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * bytes转换成十六进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String bytes2HexStr(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	/**
	 * 十六进制字符串转换成bytes 字符串不包含"0x"
	 * 
	 * @param src
	 * @return
	 */
	public static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
		}
		return ret;
	}

	/**
	 * 十六进制字符串转换成bytes 字符串包含开始"0x"
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStr2Bytes2(String hex) {
		hex = hex.substring(2, hex.length());
		int i = 0;
		byte[] data = new byte[hex.length() / 2];
		for (int n = 0; n < hex.length(); n += 2) {
			String temp = hex.substring(n, n + 2);
			int tt = Integer.valueOf(temp, 16);
			data[i] = (byte) tt;
			i++;
		}
		return data;
	}

	private static byte uniteBytes(String src0, String src1) {
		byte b0 = Byte.decode("0x" + src0).byteValue();
		b0 = (byte) (b0 << 4);
		byte b1 = Byte.decode("0x" + src1).byteValue();
		byte ret = (byte) (b0 | b1);
		return ret;
	}

	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			int d = (bytes[i] & 0x0f) >> 0;
			// /System.out.println("bytes["+i+"]:"+bytes[i]+"_"+d);//bytes[i]得到的是对应的字符ASCII值
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));// 1-4
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));// 1-2

			if (i != (bytes.length - 1)) {
				// sb.append(" ");
			}
		}
		return sb.toString();
	}

	/*
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文） decode(String
	 * bytes)方法里面的bytes字符串必须大写，即toUpperCase()
	 */
	public static String decode(String bytes) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
		byte[] strbaos = null;
		try {
			// 将每2位16进制整数组装成一个字节
			for (int i = 0; i < bytes.length(); i += 2) {
				baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
				// if(i){}
			}
			strbaos = baos.toByteArray();
		} catch (Exception e1) {
			throw e1;
		} finally {
			try {
				baos.close(); // 20130805新增流的关闭
			} catch (IOException e) {
			}
		}
		return new String(strbaos);
	}

	/**
	 * 将CLOB对象转换为String
	 * 
	 * @param clob
	 * @return
	 */
	public static String ClobToString(Clob clob) throws SQLException, IOException {
		if (clob == null)
			return "";
		String reString = "";
		Reader is = null;
		try {
			is = clob.getCharacterStream();
			BufferedReader br = new BufferedReader(is);
			String s = br.readLine();
			StringBuffer sb = new StringBuffer();
			while (s != null) {
				sb.append(s);
				s = br.readLine();
			}
			reString = sb.toString();
		} catch (IOException e) {
			throw e;
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return reString;
	}

	public static String repeat(char c, int counts) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < counts; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 将整形转为16进制字节数组
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] intTo4Bytes(Integer i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	/**
	 * 将16进制字节数组转为整形
	 * 
	 * @param i
	 * @return
	 */
	public static int bytes4ToInt(byte[] bytes) {
		if (bytes.length > 4) {
			System.out.println("bytes length is error! the length must < 4");
		}
		int length = 4;
		int intValue = 0;
		for (int i = length - 1; i >= 0; i--) {
			int offset = (length - 1 - i) * 8; // 24, 16, 8
			intValue += (bytes[i] & 0xFF) << offset;
		}
		return intValue;
	}

	/**
	 * 将整形转为16进制字节数组
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] intToBytes(Integer i) {
		String hexString = Integer.toHexString(i);
		if (hexString.length() == 1) {
			hexString = "0" + hexString;
		}
		return hexStr2Bytes(hexString);
	}

	/**
	 * 将16进制字节数组转为整形
	 * 
	 * @param i
	 * @return
	 */
	public static int bytesToInt(byte[] bytes) {
		String hexString = "0x" + StringUtil.bytes2HexStr(bytes);
		return Integer.decode(hexString);
	}

	/**
	 * 字符串填充操作.
	 * 
	 * @param rs
	 *            源字符串
	 * @param ch
	 *            用于填充的字符，字符只能是可见字符
	 * @param num
	 *            目标字符串字节数组长度
	 * @param left
	 *            左填充还是右填充
	 * @return 补全后的字符串
	 */
	public static String fillChar(String rs, char ch, int num, boolean left) {
		int rsLen = rs.getBytes().length;
		StringBuilder sb = new StringBuilder();
		// 左补全
		if (left) {
			if (num >= rsLen) {
				for (int i = 0; i < num - rsLen; i++) {
					sb.append(ch);
				}
				sb.append(rs);
			} else
				sb.append(rs.substring(0, num));
		} else {
			if (num >= rsLen) {
				sb.append(rs);
				for (int i = 0; i < num - rsLen; i++) {
					sb.append(ch);
				}
			} else
				sb.append(rs.substring(0, num));
		}
		return sb.toString();
	}

	/**
	 * 删除字串的填充字符
	 * 
	 * @param rs
	 *            源字符串
	 * @param ch
	 *            用于填充的字符，字符只能是可见字符
	 * @param left
	 *            true为左填充，false为右填充
	 * @return 删除后的字符串
	 */
	public static String removeFillChar(String rs, char ch, boolean left) {
		if (left) {
			if (rs.charAt(0) != ch)
				return rs;
			int idx = rs.length();
			for (int i = 0; i < rs.length(); i++) {
				if (rs.charAt(i) != ch) {
					idx = i;
					break;
				}
			}
			return rs.substring(idx);
		} else {
			if (rs.charAt(rs.length() - 1) != ch)
				return rs;
			int idx = -1;
			for (int i = rs.length() - 1; i >= 0; i--) {
				if (rs.charAt(i) != ch) {
					idx = i;
					break;
				}
			}
			return rs.substring(0, idx + 1);
		}
	}

	/**
	 * 取值，若值为null或空，则返回默认值
	 */
	public static String getValue(String value, String defValue) {
		if (value == null || "".equals(value))
			return defValue;
		return value;
	}

	public static String join(String[] array, char separator) {
		return join(array, String.valueOf(separator));
	}

	/**
	 * 字符串连接
	 * 
	 * @param array
	 * @param separator
	 * @return
	 */
	public static String join(String[] array, String separator) {
		StringBuilder sb = new StringBuilder();
		if (array == null || array.length == 0)
			return "";
		for (int i = 1; i <= array.length; i++) {
			sb.append(array[i - 1]);
			if (i != array.length) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	/**
	 * 使用空分隔符连接数组.
	 * 
	 * @param array
	 *            数组
	 * 
	 * @return 字符串
	 */
	public static String join(final Object[] array) {
		return join(array, null);
	}

	/**
	 * 使用指定分隔符连接数组.
	 * 
	 * @param array
	 *            数组
	 * @param separator
	 *            分隔符
	 * 
	 * @return 字符串
	 */
	public static String join(final Object[] array, final String separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	/**
	 * 使用指定分隔符连接数组.
	 * 
	 * @param array
	 *            数组
	 * @param separator
	 *            分隔符
	 * @param startIndex
	 *            开始索引号
	 * @param endIndex
	 *            结束索引号
	 * 
	 * @return 字符串
	 */
	public static String join(final Object[] array, String separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		if (separator == null) {
			separator = "";
		}

		// endIndex - startIndex > 0: Len = NofStrings *(len(firstString) +
		// len(separator))
		// (Assuming that all Strings are roughly equally long)
		int bufSize = (endIndex - startIndex);
		if (bufSize <= 0) {
			return "";
		}

		bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + separator.length());

		StringBuffer buf = new StringBuffer(bufSize);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * 使用指定分隔符连接迭代对象.
	 * 
	 * @param iterator
	 *            the iterator
	 * @param separator
	 *            分隔符
	 * @return 字符串
	 */
	public static String join(final Iterator iterator, final String separator) {

		// handle null, zero and one elements before building a buffer
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return "";
		}
		Object first = iterator.next();
		if (!iterator.hasNext()) {
			return first == null ? "" : first.toString();
		}

		// two or more elements
		StringBuffer buf = new StringBuffer(256); // Java default is 16,
		// probably too small
		if (first != null) {
			buf.append(first);
		}

		while (iterator.hasNext()) {
			if (separator != null) {
				buf.append(separator);
			}
			Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}
		return buf.toString();
	}

	/**
	 * 使用指定分隔符连接集合.
	 * 
	 * @param collection
	 *            集合对象
	 * @param separator
	 *            分隔符
	 * 
	 * @return 字符串
	 */
	public static String join(final Collection collection, final String separator) {
		if (collection == null) {
			return null;
		}
		return join(collection.iterator(), separator);
	}

	/**
	 * 使用指定分隔符连接列表.
	 * 
	 * @param lst
	 *            列表
	 * @param colName
	 *            列名
	 * @param spearator
	 *            分隔符
	 * @return the string
	 */
	public static String joinList(final List lst, final String colName, final String spearator) {
		if (lst == null) {
			return null;
		}

		Set set = new LinkedHashSet();
		String str = null;
		for (int i = 0; i < lst.size(); i++) {
			str = getString((Map) lst.get(i), colName);
			if (str != null && str.length() > 0) {
				set.add(str);
			}
		}

		return join(set, spearator);
	}

	/**
	 * 使用指定分隔符连接列表.
	 * 
	 * @param lst
	 *            列表
	 * @param colName
	 *            列名
	 * @param spearator
	 *            分隔符
	 * @return the string
	 */
	public static String joinArray(final String[][] strArray, final int index, final String spearator) {
		if (strArray == null) {
			return null;
		}

		Set set = new LinkedHashSet();
		String str = null;
		for (int i = 0; i < strArray.length; i++) {
			str = strArray[i][index];
			if (str != null && str.length() > 0) {
				set.add(str);
			}
		}

		return join(set, spearator);
	}

	/**
	 * 获取字符串.
	 * 
	 * @param map
	 *            Map对象
	 * @param key
	 *            键(不存在返回空字符串)
	 * 
	 * @return 字符串值
	 */
	public static String getString(final Map map, final Object key) {
		return getString(map, key, "");
	}

	/**
	 * 获取字符串.
	 * 
	 * @param map
	 *            Map对象
	 * @param key
	 *            键(不存在返回缺省值)
	 * @param defaultVal
	 *            缺省值
	 * 
	 * @return 字符串值
	 */
	public static String getString(final Map map, final Object key, final String defaultVal) {
		if (map == null || key == null) {
			return defaultVal;
		}

		Object val = map.get(key);
		return val == null ? defaultVal : val.toString();
	}
}
