package allst.utils.pay;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.codec.Base64Encoder;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.util.WebUtils;


/**
 * 字串工具类
 */
@SuppressWarnings("restriction")
public abstract class StringUtil {
	
		// 判断是否为空或null
		public static boolean isNullOrEmpty(CharSequence cs){
			return null == cs || 0 == cs.length();
		}
		//判断是否为字符串的null
		public static boolean isStringNull(CharSequence cs){
			return cs.equals("null");
		}
		
		// 判断不为null且不为空
		public static boolean isNotNullAndEmpty(CharSequence cs){
			return !isNullOrEmpty(cs);
		}
		
		// 判断字符串是否包含中文
		public static boolean isChinese(CharSequence cs) {
			if(isNullOrEmpty(cs))
				return false;
			
			char c;
			for (int i = 0; i < cs.length(); i++) {
				c = cs.charAt(i);
				if (isChineseChar(c)) {
					return true;
				}
			}
			return false;
		}

		// 判断是否为中文字符
		public static boolean isChineseChar(char c) {
			Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
			if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
					|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
					|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
					|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
				return true;
			}
			return false;
		}
		
	/**
	 * 截取字符串(默认从0下标开始截取)
	 * @param s 被剪裁字符串
	 * @param end 结束下标(不包含该下标)
	 * @return
	 */
	public static String substring(String s, int end){
		return substring(s, 0, end);
	}
	
	/**
	 * 截取字符串
	 * @param s 被剪裁字符串
	 * @param start 开始下标(包含该下标)
	 * @param end 结束下标(不包含该下标)
	 * @return s为null、空字符串的时候直接返回; 不然返回的就是截取后的字符串
	 */
	public static String substring(String s, int start, int end){
		if(start >= end) throw new IllegalArgumentException("截取区间不得为负数!");
		return s != null && !s.isEmpty() ? (s.length() < end ? s.substring(start, s.length()) : s.substring(start, end)) : s;
	}
	
	/**
	 * 获取简单的base64加密
	 * @param s 被加密的字符串
	 * @return
	 */
	public static String base64(String s){
		return base64(s, "UTF-8");
	}
	
	/**
	 * 获取简单的base64加密
	 * @param s 被加密的字符串
	 * @return
	 */
	public static String base64(String s, String charset){
		try {
			return Base64Encoder.encode(s.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取添加了salt的密码, 默认加密5次
	 * @param password 密码
	 * @return
	 */
	public static String getSaltPassword(Object password){
		return password == null ? null : getSaltPassword(password, 5);
	}
	
	/**
	 * 获取添加了salt的密码
	 * @param password 密码
	 * @param round 加密次数
	 * @return
	 */
	public static String getSaltPassword(Object password, int round){
		if(password == null) return null;
		String md5 = md5uper(password.toString());
		for(int i = 1 ; i < round ; i ++){
			md5 = md5uper(md5);
		}
		// get salt from md5
		String salt = md5uper(md5.substring(16));
		md5 = md5uper(md5+salt)+salt;
		for(int i = 1 ; i < round ; i ++){
			salt = md5uper(md5.substring(16));
			md5 = md5uper(md5+salt)+salt;
		}
		return md5;
	}
	
	/**
	 * 获取随机没有-的uuid
	 * @return
	 */
	public static String uuidWithoutDash(){
		return uuid().replaceAll("-", "");
	}
	
	/**
	 * 获取随机的uuid
	 * @return
	 */
	public static String uuid(){
		return UUID.randomUUID().toString();
	}
	
	/**
	 * 判断是否为URI
	 * @param uri
	 * @return
	 */
	public static boolean isUri(String uri){
		if(uri != null) return uri.matches("^(\\/[\\w]*)*$");
		return false;
	}

	/**
	 * 判断字符串是否为0或1
	 * @param num
	 * @return
	 */
	public static boolean is0or1(String num){
		if(num != null){
			return num.matches("^[01]$");
		}
		return false;
	}
	
	/**
	 * 判断数字是否为0或1
	 * @param num
	 * @return
	 */
	public static boolean is0or1(Integer num){
		if(num != null && (num.equals(1) || num.equals(0))) return true;
		return false;
	}
	
	/**
	 * 判断是否为手机号
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile){
		return mobile != null ? mobile.matches("^1[3|4|5|6|7|8|9]\\d{9}$") : false;
	}
	
	/**
	 * 判断字符串是否为(数字1,数字2,数字3,...)的字符串
	 * @param s 字符串
	 * @return true:匹配; false:不匹配;
	 */
	public static boolean isInNumberBracket(String s){
		return s != null ? s.replaceAll("\\s", "").matches("^\\(\\d+(?:,\\d+)*\\)$") : false;
	}
	
	/**
	 * 判断是否为整数
	 * @param integer 被判断的字符串
	 * @return true: 是整数; false:不为整数或数字
	 */
	public static boolean isNumber(String integer){
		return isNumber(integer, false);
	}
	
	/**
	 * 判断是否为整数
	 * @param integer 被判断的字符串
	 * @param isUnsign 是否为正数
	 * @return true: 是整数; false:不为整数或数字
	 */
	public static boolean isNumber(String integer, boolean isUnsign){
		return integer == null ? false : integer.matches("^"+(isUnsign?"":"[-+]?")+"[0-9]+$");
	}
	
	/**
	 * 判断是否为正整数
	 * @param integer 被判断的字符串
	 * @return true: 是整数; false:不为整数或数字
	 */
	public static boolean isUnsignInteger(String integer){
		return isNumber(integer, true);
	}

	/**
	 * 将一个Double转为int的String，将省略小数点后面的值
	 * @param d
	 * @return
	 */
	public static String doubleToIntString(Double d) {
		return d == null ? null : String.valueOf(((Double) d).intValue());
	}

	/**
	 * 判断是否浮点数或数字
	 * @param decimal 被检查的字符串
	 * @param type "0+":非负浮点数; "+":正浮点数; "-0":非正浮点数; "-":负浮点数; "":浮点数;
	 * @return true: 是浮点数或数字; false: 不为浮点数或数字;
	 */
	public static boolean isDecimal(String decimal, String type) {
		if(decimal == null) return false;
		String eL = "";
		if (type.equals("0+")) eL = "^\\d+(\\.\\d+)?$";// 非负浮点数
		else if (type.equals("+")) eL = "^((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*))$";// 正浮点数
		else if (type.equals("-0")) eL = "^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";// 非正浮点数
		else if (type.equals("-")) eL = "^(-((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*)))$";// 负浮点数
		else eL = "^(-?\\d+)(\\.\\d+)?$";// 浮点数
		return decimal.matches(eL);
	}
	
	/**
	 * 判断是否浮点数或数字
	 * @param decimal
	 * @return true: 是浮点数或数字; false: 不为浮点数或数字;
	 */
	public static boolean isDecimal(String decimal) {
		return isDecimal(decimal, "");
	}

	/**
	 * 判断是否为布尔型
	 * @param bool
	 * @return
	 */
	public static boolean isBoolean(String bool){
		if(bool == null) return false;
		return bool.matches("^(true|false)$");
	}
	
	/**
	 * 检测对象是否存在某数组中
	 * @param value
	 * @param array
	 * @return 存在返回真，不存在返回假
	 */
	public static boolean isInArray(Object value, Object[] array) {
		if (array == null) return false;
		for (Object v : array) {
			if (v.equals(value)) return true;
		}
		return false;
	}
	
	/**
	 * SHA1加密
	 * @param decript
	 * @return
	 */
	public static String SHA1(String decript){
		return SHA1(decript, "UTF-8");
	}
	
	/**
	 * SHA1加密
	 * @param decript 要加密的字符串
	 * @param charset 字符集编码
	 * @return
	 */
	public static String SHA1(String decript, String charset) {
		try {
			MessageDigest digest = MessageDigest
					.getInstance("SHA-1");
			digest.update(decript.getBytes(charset));
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * SHA加密
	 * @param decript
	 * @return
	 */
	public static String SHA(String decript) {
		try {
			MessageDigest digest = MessageDigest
					.getInstance("SHA");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Hex编码.
	 */
	public static String encodeHex(byte[] input) {
		return new String(Hex.encodeHex(input));
	}

	/**
	 * Hex解码.
	 */
	public static byte[] decodeHex(String input) {
		try {
			return Hex.decodeHex(input.toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 加密AES
	 * @param content
	 * @param password
	 * @return
	 */
	public static byte[] encryptAES(String content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return result; // 加密
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密AES
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return
	 */
	public static byte[] decryptAES(byte[] content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(content);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * MD5加密方法, 大写方式输出
	 * @param str 要被加密的字符串
	 * @return 32位大写的字符串
	 */
	public static String md5uper(String str) {
		return md5(str).toUpperCase();
	}

	/**
	 * MD5加密方法
	 * @param str 要被加密的字符串
	 * @returnn 32位的字符串
	 */
	public static String md5(String str) {
		return md5(str, true);
	}

	/**
	 * MD5加密
	 * @param str 要被加密的字符串
	 * @param zero 是否用0进行补位
	 * @return 32位的字符串
	 */
	public static String md5(String str, boolean zero) {
		return md5(str, zero, "utf-8");
	}

	public static String md5(String str, boolean zero, String charset){
		try{
			if(str == null) return null;
			MessageDigest messageDigest = null;
			try {
				messageDigest = MessageDigest.getInstance("MD5");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
			byte[] resultByte = messageDigest.digest(str.getBytes(charset));
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < resultByte.length; ++i) {
				int v = 0xFF & resultByte[i];
				if (v < 16 && zero) result.append("0");
				result.append(Integer.toHexString(v));
			}
			return result.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 验证Email地址是否有效
	 */
	public static boolean validEmail(String email) {
		if(email == null) return false;
		return email.matches("^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
	}

	/**
	 * 验证两个字符串是否相等且不能为空
	 * @param str1
	 * @param str2
	 * @return true: 相等;
	 */
	public static boolean equals(String str1, String str2) {
		if (str1 == null || str1.equals("") || str2 == null || str2.equals("")) {
			return false;
		}
		return str1.equals(str2);
	}
	
	/**
	 * 判断字符串是否为空, 为空则返回默认值
	 * @param string 被判断字符串
	 * @param defaultValue 补位字符串
	 * @return 判断后的字符串
	 */
	public static String toString(String string, String defaultValue){
		defaultValue = defaultValue == null ? "" : defaultValue;
		if(string == null) return defaultValue;
		return string;
	}
	
	/**
	 * 过滤字符串所有非字母或数字的字符
	 * @param wrods 要被过滤的字符串
	 * @return 过滤后的字符串
	 */
/*
	public static String toWords(String words){
		if(words == null) return null;
		return words.replaceAll("[^a-zA-Z0-9]", "");
	}
*/

	/**
	 * 过滤字符串所有非数字或小数点或+-号的字符
	 *
	 * @return 过滤后的字符串
	 */
	public static String toNumberString(String number){
		if(number == null) return null;
		number = number.replaceAll("[^\\-\\+\\d\\.]", "");
		if(!isDecimal(number)) return null;
		return number;
	}

	/**
	 * 将一个字串转为int，如果无空，则返回默认值
	 * @param integer 要转换的数字字符串
	 * @param defaultValue 当转换出错时默认值
	 * @return Integer
	 */
	public static Integer toInt(String integer, Integer defaultValue) {
		if(isNumber(toNumberString(integer)))
			try {
				defaultValue = Integer.parseInt(integer);
			} catch (Exception ex) {}
		return defaultValue;
	}

	/**
	 * 将字符型转为Int型
	 * @param integer 要转换的数字字符串
	 * @return 不为整数则返回null
	 */
	public static Integer toInt(String integer) {
		return toInt(integer, null);
	}

	/**
	 * 转换为double, 如果转换失败则用defaultValue替代
	 * @param decimal 要转换的数字字符串
	 * @param defaultValue 当转换出错时默认值
	 * @return Double
	 */
	public static Double toDouble(String decimal, Double defaultValue) {
		if (toNumberString(decimal) != null)
			try {
				defaultValue = Double.valueOf(decimal);
			} catch (Exception ex) {}
		return defaultValue;
	}

	/**
	 * 字符串转换为double
	 * @param decimal 要转换的数字字符串
	 * @return 不能转换为double时返回null
	 */
	public static Double toDouble(String decimal) {
		return toDouble(decimal, null);
	}

	/**
	 * 转换为boolean, 如果转换失败则用defaultValue替代
	 * @param bool 要转换的字符串
	 * @param defaultValue 当转换出错时默认值
	 * @return Boolean
	 */
	public static Boolean toBoolean(String bool, Boolean defaultValue){
		if(isBoolean(bool))
			try{
				defaultValue = Boolean.valueOf(bool);
			}catch(Exception e){}
		return defaultValue;
	}

	/**
	 * 字符串转换为boolean
	 * @param bool 要转换的字符串
	 * @return 不能转换为boolean时返回null
	 */
	public static Boolean toBoolean(String bool){
		return toBoolean(bool, null);
	}

	/**
	 * 转换为long, 如果转换失败则用defaultValue替代
	 * @param l 要转换的数字字符串
	 * @param defaultValue 当转换出错时默认值
	 * @return Long
	 */
	public static Long toLong(String l, Long defaultValue){
		if(isNumber(l))
			try{
				defaultValue = Long.valueOf(l);
			}catch(Exception e){}
		return defaultValue;
	}

	/**
	 * 字符串转换为long
	 * @param l 要转换的数字字符串
	 * @return 不能转换为boolean时返回null
	 */
	public static Long toLong(String l){
		return toLong(l, null);
	}
	
	/**
	 * 把数组转换成String
	 * @param array 数组
	 * @param separator 分隔符
	 * @return
	 */
	public static String arrayToString(Object[] array, String separator) {
		return arrayToString(array, separator, "", "");
	}
	
	/**
	 * 
	 * 把数组转换成String
	 * @param array 数组
	 * @param separator 分隔符
	 * @param prefix 生成后拼接的前缀
	 * @param suffix 生成后拼接的后缀
	 * @return
	 */
	public static String arrayToString(Object[] array, String separator, String prefix, String suffix) {
		if (array == null) {
			return "";
		}
		String str = "";
		for (int i = 0; i < array.length; i++) {
			if (i != array.length - 1) {
				str += array[i] + separator;
			} else {
				str += array[i];
			}
		}
		return prefix+str+suffix;
	}

	/**
	 * 得到WEB-INF的绝对路径
	 * 
	 * @return
	 */
	public static String getWebInfPath() {
		String filePath = Thread.currentThread().getContextClassLoader().getResource("").toString();
		if (filePath.toLowerCase().indexOf("file:") > -1) {
			filePath = filePath.substring(6, filePath.length());
		}
		if (filePath.toLowerCase().indexOf("classes") > -1) {
			filePath = filePath.replaceAll("/classes", "");
		}
		if (System.getProperty("os.name").toLowerCase().indexOf("window") < 0) {
			filePath = "/" + filePath;
		}
		if (!filePath.endsWith("/"))
			filePath += "/";
		return filePath;
	}

	/**
	 * 得到根目录绝对路径(不包含WEB-INF)
	 * 
	 * @return
	 */
	public static String getRootPath() {
		String filePath = StringUtil.class.getResource("").toString();

		int index = filePath.indexOf("WEB-INF");
		if (index == -1) {
			index = filePath.indexOf("build");
		}

		if (index == -1) {
			index = filePath.indexOf("bin");
		}

		filePath = filePath.substring(0, index);
		if (filePath.startsWith("jar")) {
			// 当class文件在jar文件中时，返回”jar:file:/F:/ …”样的路径
			filePath = filePath.substring(10);
		} else if (filePath.startsWith("file")) {
			// 当class文件在jar文件中时，返回”file:/F:/ …”样的路径
			filePath = filePath.substring(6);
		}

		if (System.getProperty("os.name").toLowerCase().indexOf("window") < 0) {
			filePath = "/" + filePath;
		}

		if (filePath.endsWith("/")) filePath = filePath.substring(0, filePath.length() - 1);
		//System.out.println("getRoot path is "+filePath );
		return filePath;
	}

	/**
	 * 获取得到根目录绝对路径(不包含WEB-INF)+resource
	 * @param resource
	 * @param request
	 * @return
	 */
	public static String getRootPath(String resource, HttpServletRequest request) {
		try {
			return WebUtils.getRealPath(request.getSession()
					.getServletContext(), resource);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 将计量单位字节转换为相应单位
	 */
	public static String getFileSize(Object fileSize) {
		return getFileSize(toLong(fileSize.toString(), 0l));
	}

	/**
	 * 将计量单位字节转换为相应单位
	 */
	public static String getFileSize(long filesize){
		String temp = "";
		Double fileSize = (double)filesize;
		DecimalFormat df = new DecimalFormat("0.00");
		if (fileSize >= 1024) {
			if (fileSize >= 1048576) {
				if (fileSize >= 1073741824) {
					temp = df.format(fileSize / 1024 / 1024 / 1024) + " GB";
				} else {
					temp = df.format(fileSize / 1024 / 1024) + " MB";
				}
			} else {
				temp = df.format(fileSize / 1024) + " KB";
			}
		} else {
			temp = df.format(fileSize / 1024) + " KB";
		}
		return temp;
	}

	/**
	 * 得到一个32位随机字符
	 * 
	 * @return
	 */
	public static String getEntry() {
		Random random = new Random(100);
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(new String(
				"yyyyMMddHHmmssS"));
		return md5(formatter.format(now) + random.nextDouble());
	}

	/**
	 * 将字符串由本地编码转换至给定的编码格式
	 */
	public static String to(String str, String charset) {
		return to(str, System.getProperty("file.encoding"), charset);
	}
	
	/**
	 * 将str给的编码转换至给定的编码格式
	 */
	public static String to(String str, String srtCharset, String charset) {
		if (str == null || str.equals("")) {
			return "";
		}
		try {
			return new String(str.getBytes(srtCharset), charset);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取指定长度的随机数字字符串
	 */
	public static String getRandCode(int n) {
		Random random = new Random();
		String sRand = "";
		for (int i = 0; i < n; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
		}
		return sRand;
	}
	
	/**
	 * 获取指定长度的随机字符串
	 */
	public static String getRandStr(int length){
		String code = "";
		for(int i = 0 ; i < length ; i++){
			int typeCode = (int) (Math.random()*10);
			int lchar =  0;
			switch(typeCode){
				case 0:
				case 1:
				case 2:
					lchar =  65+(int) (Math.random()*26);
					code += ((char)lchar);
					break;
				case 3:
				case 4:
				case 5:
					lchar =  97+(int) (Math.random()*26);
					code += ((char)lchar);
					break;
				case 6:
				case 7:
				case 8:
					code += ((int) (Math.random()*10));
					break;
				case 9:i--;break;
			}
		}
		return code;
	}

	/**
	 * 去除HTML 元素
	 */
	public static String getTxtWithoutHTMLElement(String element) {
		if (null == element || "".equals(element.trim())) {
			return element;
		}

		Pattern pattern = Pattern.compile("<[^<|^>]*>");
		Matcher matcher = pattern.matcher(element);
		StringBuffer txt = new StringBuffer();
		while (matcher.find()) {
			String group = matcher.group();
			if (group.matches("<[\\s]*>")) {
				matcher.appendReplacement(txt, group);
			} else {
				matcher.appendReplacement(txt, "");
			}
		}
		matcher.appendTail(txt);
		String temp = txt.toString().replaceAll("\n", "");
		temp = temp.replaceAll(" ", "");
		return temp;
	}

	/**
	 * trim字符串
	 * @return
	 */
	public static String toTrim(String strtrim) {
		return strtrim != null && !strtrim.equals("") ? strtrim.trim() : null;
	}

	/**
	 * 转义字串的$
	 */
	public static String filterDollarStr(String str) {
		String sReturn = "";
		if (!toTrim(str).equals("")) {
			if (str.indexOf('$', 0) > -1) {
				while (str.length() > 0) {
					if (str.indexOf('$', 0) > -1) {
						sReturn += str.subSequence(0, str.indexOf('$', 0));
						sReturn += "\\$";
						str = str.substring(str.indexOf('$', 0) + 1,
								str.length());
					} else {
						sReturn += str;
						str = "";
					}
				}

			} else {
				sReturn = str;
			}
		}
		return sReturn;
	}

	/**
	 * 压缩HTML格式的字符串
	 */
	public static String compressHtml(String html) {
		if (html == null)
			return null;

		html = html.replaceAll("[\\t\\n\\f\\r]", "");
		return html;
	}

	public static String toCurrency(Double d) {
		if (d != null) {
			DecimalFormat df = new DecimalFormat("￥#,###.00");
			return df.format(d);
		}
		return "";
	}

	public static String toString(Integer i) {
		if (i != null) {
			return String.valueOf(i);
		}
		return "";
	}

	public static String toString(Object i) {
		if (i != null) {
			return String.valueOf(i);
		}
		return "";
	}

	public static String toString(Long i) {
		if (i != null) {
			return String.valueOf(i);
		}
		return "";
	}

	public static String toString(Double d) {
		if (null != d) {
			return String.valueOf(d);
		}
		return "";
	}

	public static String getRandom() {
		int[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
			int index = rand.nextInt(i);
			int tmp = array[index];
			array[index] = array[i - 1];
			array[i - 1] = tmp;
		}
		int result = 0;
		for (int i = 0; i < 6; i++)
			result = result * 10 + array[i];

		return "" + result;
	}

	/**
	 * 处理树型码 获取本级别最大的code 如:301000 返回301999
	 */
	public static int getMaxLevelCode(int code) {
		String codeStr = "" + code;
		StringBuffer str = new StringBuffer();
		boolean flag = true;
		for (int i = codeStr.length() - 1; i >= 0; i--) {
			char c = codeStr.charAt(i);
			if (c == '0' && flag) {
				str.insert(0, '9');
			} else {
				str.insert(0, c);
				flag = false;
			}
		}
		return Integer.valueOf(str.toString());
	}

	/**
	 * 去掉sql的注释
	 */
	public static String delSqlComment(String content) {
		String pattern = "/\\*(.|[\r\n])*?\\*/";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(content);
		if (m.find()) {
			content = m.replaceAll("");
		}
		return content;
	}

	/**
	 * 将InputStream转换为字符串
	 */
	public static String inputStream2String(InputStream is) {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	/**
	 * 将字符串转换为URL通用格式
	 */
	public static String decodeURL(String keyword) {
		return decodeURL(keyword, "UTF-8");
	}
	
	/**
	 * 将字符串转换为给定编码格式的URL通用格式
	 */
	public static String decodeURL(String keyword, String charset) {
		try {
			keyword = URLDecoder.decode(keyword, charset);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return keyword;
	}
	
	/**
	 * 默认以UTF-8编码转义
	 */
	public static String encodeURL(String keyword) {
		return encodeURL(keyword, "UTF-8");
	}
	
	/**
	 * 将字符串转义为URI
	 */
	public static String encodeURL(String keyword, String charset) {
		try {
			keyword = URLEncoder.encode(keyword, charset);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return keyword;
	}

	/**
	 * 进行解析
	 */
	public static String doFilter(String regex, String rpstr, String source) {
		Pattern p = Pattern.compile(regex, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(source);
		return m.replaceAll(rpstr);
	}

	/**
	 * 脚本过滤
	 * @param source
	 * @return
	 */
	public static String formatScript(String source) {
		source = source.replaceAll("javascript", "&#106avascript");
		source = source.replaceAll("jscript:", "&#106script:");
		source = source.replaceAll("js:", "&#106s:");
		source = source.replaceAll("value", "&#118alue");
		source = source.replaceAll("about:", "about&#58");
		source = source.replaceAll("file:", "file&#58");
		source = source.replaceAll("document.cookie", "documents&#46cookie");
		source = source.replaceAll("vbscript:", "&#118bscript:");
		source = source.replaceAll("vbs:", "&#118bs:");
		source = doFilter("(on(mouse|exit|error|click|key))", "&#111n$2",
				source);
		return source;
	}

	/**
	 * 格式化HTML代码
	 * @param htmlContent
	 * @return
	 */
	public static String htmlDecode(String htmlContent) {
		htmlContent = formatScript(htmlContent);
		htmlContent = htmlContent.replaceAll(" ", "&nbsp;")
				.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
				.replaceAll("\n\r", "<br>").replaceAll("\r\n", "<br>")
				.replaceAll("\r", "<br>");
		return htmlContent;
	}

	/**
	 * 动态添加表前缀，对没有前缀的表增加前缀
	 * @param table
	 * @param prefix
	 * @return
	 */
	public static String addPrefix(String table, String prefix) {
		String result = "";
		if (table.length() > prefix.length()) {
			if (table.substring(0, prefix.length()).toLowerCase()
					.equals(prefix.toLowerCase()))
				result = table;
			else
				result = prefix + table;
		} else
			result = prefix + table;

		return result;
	}

	/**
	 * 添加后缀
	 * @param table
	 * @param suffix
	 * @return
	 */
	public static String addSuffix(String table, String suffix) {
		String result = "";
		if (table.length() > suffix.length()) {
			int start = table.length() - suffix.length();
			int end = start + suffix.length();
			if (table.substring(start, end).toLowerCase()
					.equals(suffix.toLowerCase()))
				result = table;
			else
				result = table + suffix;
		} else
			result = table + suffix;

		return result;
	}

	/**
	 * 得到异常的字串
	 * @param aThrowable
	 * @return
	 */
	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();

	}

    private static final char SEPARATOR = '_';

	/**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    /**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }
    
 /**
   * 查询数组中是否包含某个值
   * @param array	数组
   * @param param	查询是否包含的值
   * @return
   */
  public static boolean findArrayContain(Object[] array,Object param){
	  for (Object obj : array) {
		if(obj.equals(param)){
			return true;
		}
	}
	  return false;
  }
  
  //获得随机字符串
  public static String getRandomString(int length) { //length表示生成字符串的长度  
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";     
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();     
	 }
  
	public static boolean isNullOrEmpty(Long sendTime) {
		return null == sendTime ;
	} 
	
	/**
	 * 数组去重
	 * @param array
	 * @return
	 */
	public static String [] getRemoveDuplicateArray(String [] array) {
		List<String> result = new ArrayList<>();  
		boolean flag;  
		for(int i=0;i<array.length;i++){  
		    flag = false;  
		    for(int j=0;j<result.size();j++){  
		        if(array[i].equals(result.get(j))){  
		            flag = true;  
		            break;  
		        }  
		    }  
		    if(!flag){  
		        result.add(array[i]);  
		    }  
		}  
		String[] arrayResult = (String[]) result.toArray(new String[result.size()]);  
        return arrayResult;
	}

	/**
	 * 利用hash去重
	 * @param list
	 * @return
	 */
	public static List removeDuplicate(List list) {
		HashSet h = new HashSet(list);
		list.clear();
		list.addAll(h);
		return list;
	}
	
	/**
	 * double精确小数点
	 * num 数字
	 * digit 精确到第几位
	 * @return
	 */
	public static Double toFixed(double num, int digit) {
		if(num == 0) {
			return 0d;
		}
		return new BigDecimal(num).setScale(digit,   BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	 /**
     * 提供精确的加法运算。
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double addToDouble(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }
    
    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double subToDouble(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }
	
    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mulToDouble(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }
    
    
    
	/**
	 * 对比两个时间的大小 
	 * 9:30 > 8:30
	 */
	public static Boolean contrastHoursAndMinute(String startTime, String endTime) {
		if(StringUtil.isNullOrEmpty(startTime) || StringUtil.isNullOrEmpty(endTime)) {
			return null;
		}
		if(startTime.indexOf(":") == -1 || endTime.indexOf(":") == -1) {
			return null;
		}
		try {
			// 获得时
			Integer startTimeHours= Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))); 
			Integer endTimeHours = Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))); 
			// 获得分
			Integer startTimeMinute= Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1,startTime.length())); 
			Integer endTimeMinute = Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1,endTime.length())); 
			if(startTimeMinute >= 60 || endTimeMinute >= 60) {
				return null;
			}
			if(endTimeHours < startTimeHours) {
				return false;
			}
			// 时相等 判断分钟
			if(endTimeHours == startTimeHours) {
				if(endTimeMinute < startTimeMinute) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 格式化时间
	 * 12:0 => 12:00
	 * @param time
	 * @return
	 */
	public static String hoursAndMinuteFormat(String time) {
		if(StringUtil.isNullOrEmpty(time)) {
			return time;
		}
		if(time.indexOf(":") == -1) {
			return time;
		}
		StringBuffer str = new StringBuffer(time);
		// 获得时
		Integer timeHours= Integer.parseInt(time.substring(0, time.indexOf(":"))); 
		// 获得分
		Integer timeMinute= Integer.parseInt(time.substring(time.indexOf(":") + 1,time.length()));
		if(timeHours < 10) {
			str.insert(0, "0"); 
		}
		if(timeMinute < 10) {
			str.insert(time.length(), "0"); 
		}
		return str.toString();
	}

	/**
	 * 过滤脏字
	 * @param sentence
	 * @return
	 */
	public static String filtration(String sentence) {
		// 使用默认单例（加载默认词典）
	/*	SensitiveFilter filter = SensitiveFilter.DEFAULT;
		// 进行过滤
		String filted = filter.filter(sentence, '*');
		return filted;*/
		// SensitiveFilterService filter = SensitiveFilterService.getInstance();
		// String hou = filter.replaceSensitiveWord(sentence, 1, "*");
		return "hou";
	}

	/**
	 * 包含*说明 有过滤字段，有个前提是不能传入*进入
	 * @param sentence
	 * @return
	 */
	public static Boolean isFiltration(String sentence) {
		/*// 使用默认单例（加载默认词典）
		SensitiveFilter filter = SensitiveFilter.DEFAULT;
		// 进行过滤
		String filted = filter.filter(sentence, '*');*/
		// SensitiveFilterService filter = SensitiveFilterService.getInstance();
		// String hou = filter.replaceSensitiveWord(sentence, 1, "*");
		boolean status = "hou".contains("*");
		if(status){
			return true;
		}
		return false;
	}

	static int i = 0;
	public static int[] MergeToRepeat(int []a,int []b) {
		Map<Integer,Integer> map = new TreeMap<>();
		for (int i = 0; i < a.length; i++) {
			map.put(a[i],a[i]);
		}
		for (int i = 0; i < b.length; i++) {
			map.put(b[i],b[i]);
		}
		Collection<Integer> values = map.values();
		Iterator<Integer> iterator = values.iterator();
		int c [] = new int[values.size()];
		while (iterator.hasNext()){
			c[i++] = iterator.next();
		}
		return c;
	}

	/**
	 *  屏蔽电话号码或者QQ用，屏蔽连续数字
	 *  (前2个数字)中间2个数字(最后2个数字)替换为(第一组数值，保持不变$1)(中间为)(第二组数值，保持不变$2)
	 * @param massege
	 * @return
	 */
	public static String blockYourNumber(String massege){
		return massege = massege.replaceAll("(\\d{2})\\d{2}(\\d{2})", "$1****$2");
	}

	/**
	 * 参数签名加密
	 * @param parameters
	 * @param secret
	 * @return
	 */
	public static String signRequest(TreeMap<String,String> parameters,String secret,String charset){

		TreeMap<String, String> treeMap = new TreeMap<>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		treeMap = (TreeMap<String, String>) parameters;
		System.out.println("升序排序结果："+treeMap);
		StringBuffer sb = new StringBuffer();
		//把map中的集合拼接成字符串
		for(Map.Entry<String, String> entry:treeMap.entrySet()){
			String key = entry.getKey();
			Object value = entry.getValue();
			sb.append(key).append("=").append(value).append("&");
		}
		sb.append("key").append("=").append(secret);
		System.out.println("拼接后的字符："+sb.toString());
		//进行MD5加密
		String sign = DigestUtils.md5Hex(getContentBytes(sb.toString(), charset));
		System.out.println("加密后的签名："+sign);
		return sign;
	}
	/**
	 * @param content
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}

	private static String getRandomNum(int n){
		String val = "";
		Random random = new Random();
		for ( int i = 0; i < n; i++ )
		{
			String str = random.nextInt( 2 ) % 2 == 0 ? "num" : "char";
			if ( "char".equalsIgnoreCase( str ) )
			{ // 产生字母
				int nextInt = random.nextInt( 2 ) % 2 == 0 ? 65 : 97;
				// System.out.println(nextInt + "!!!!"); 1,0,1,1,1,0,0
				val += (char) ( nextInt + random.nextInt( 26 ) );
			}
			else if ( "num".equalsIgnoreCase( str ) )
			{ // 产生数字
				val += String.valueOf( random.nextInt( 10 ) );
			}
		}
		return val;
	}

	public static void main(String[] args) {
		System.out.println(StringUtil.uuidWithoutDash());
	}


}
