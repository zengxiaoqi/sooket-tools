package com.tools.sockettools.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理API
 * 
 * @author Administrator
 * 
 */
public class DateUtil {

	private final static Logger logger = LoggerFactory
			.getLogger(DateUtil.class);
	public static final String YEAR = "Y";
	public static final String MONTH = "M";
	public static final String DAY = "D";
	// 支持的字符串时间格式，如需增加新的格式，在datePatterns中加入即可
	private static final String[] datePatterns = { "yyyyMMdd", "yyyy-MM-dd",
			"yyyy/MM/dd" };

	public static void main(String[] args) throws ParseException {

		System.out.println(DateUtil.getNowStrDate());

	}

	public static String getNowStrDate() {
		return new SimpleDateFormat("yy/MM/dd:hh:ss:mm").format(new Date());
	}

	public static String getDateYMD() throws ParseException {
		return new SimpleDateFormat("MMdd:hh:ss:mm").format(new Date());
	}

	/**
	 * 根据指定的日期格式将指定格式的源日期字串转换为目标字串
	 * 
	 * @param dateStr
	 *            源日期字符串
	 * @param srcPattern
	 *            源日期格式
	 * @param destPattern
	 *            目标日期格式
	 * @return 指定格式的日期字符串
	 * @throws ParseException
	 */
	public static String formatDate(String dateStr, String srcPattern,
			String destPattern) throws ParseException {
		if (StringUtil.isNullOrBlank(dateStr)
				|| StringUtil.isNullOrBlank(srcPattern)
				|| StringUtil.isNullOrBlank(destPattern)) {
			return dateStr;
		}
		SimpleDateFormat srcSdf = new SimpleDateFormat(srcPattern);
		Date date = null;
		date = srcSdf.parse(dateStr);
		return new SimpleDateFormat(destPattern).format(date);
	}

	/**
	 * 根据指定的增量模式对日期进行加操作 。
	 * 如果传入的是String类型的日期，返回的也是String类型，如果传入的是Date类型，返回的也是Date类型
	 * 
	 * @param date
	 *            - 要进行加操作的日期
	 * @param pattern
	 *            增量模式 "Y":年，"M":月，"D":日,默认对日添加
	 * @param num
	 *            增加的数量，支持负数
	 * @return 增加num数量后的日期字符串或者Date
	 */
	public static Object dateAdd(Object date, String pattern, int num) {
		/*
		 * 数据正确性检查
		 */
		if (date == null) {
			return date;
		}

		if (StringUtil.isNullOrBlank(pattern)) {// 默认对日添加
			pattern = DAY;
		} else if (!YEAR.equals(pattern) && !MONTH.equals(pattern)
				&& !DAY.equals(pattern)) {
			logger.debug("不支持的增量模式！");
			return null;
		}

		/*
		 * 计算日期
		 */
		if (date instanceof Date) {// 计算类型为Date类型
			return _dateAdd((Date) date, pattern, num);
		} else if (date instanceof String) {// 计算类型为String
			// 感觉入参中增加日期字符串的格式更为合理
			Date parseDate = null;
			SimpleDateFormat sdf = null;
			// 从datePatterns获取时间格式，循环解析
			for (int i = 0; i < datePatterns.length; i++) {
				sdf = new SimpleDateFormat(datePatterns[i]);
				// 龌蹉的办法V_V，用异常来判断是否解析失败
				try {
					parseDate = sdf.parse(date.toString());
					break;// 解析成功后马上退出循环
				} catch (ParseException e) {
				}
			}

			if (parseDate == null) {// 字符型时间解析失败
				logger.debug("不支持的时间格式！");
				return null;
			} else {// 字符型时间解析成功
				parseDate = _dateAdd(parseDate, pattern, num);
				return sdf.format(parseDate);
			}
		} else {// 不支持计算的数据类型
			logger.warn("无法解析的数据类型，时间需为Date或String类型！");
			return null;
		}
	}

	/*
	 * 日期计算
	 */
	private static Date _dateAdd(Date date, String pattern, int num) {
		Calendar cal = Calendar.getInstance();
		cal.setTime((Date) date);
		if (pattern.equalsIgnoreCase(YEAR)) {
			cal.add(Calendar.YEAR, num);
		} else if (pattern.equalsIgnoreCase(MONTH)) {
			cal.add(Calendar.MONTH, num);
		} else if (pattern.equalsIgnoreCase(DAY)) {
			cal.add(Calendar.DAY_OF_MONTH, num);
		} else {
			return null;
		}
		return cal.getTime();
	}

}
