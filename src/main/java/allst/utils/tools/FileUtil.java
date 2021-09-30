package allst.utils.tools;

import allst.utils.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 流操作/文件/路径 等工具方法
 *
 * @author June
 * @since 2021年09月
 */
public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
    /**
     * 常用图片文件后缀
     */
    private static final String[] IMAGE_SUFFIX = new String[]{"jpg", "jpeg", "bmp", "png", "gif", "ico"};
    private static final String TEMP_DIR = "java.io.tmpdir";
    private static MimeMap mimeMap = null;

    public static void main(String[] args) {
        String s1 = "E:/TestData/file.log";
        String s2 = "E:/TestData/file2.log";
        System.out.println(moveFile(s1, s2, true));
    }

    /**
     * 获取当前jdk或者jre的路径
     * 仅到    /jre或者jdk  级.
     * 例如: 返回  D:/02_Development/jdk1.8.0_101x64/jre
     */
    public static String getJdkPath() {
        String path = System.getProperty("java.home");
        if (StringUtil.checkEmpty(path)) {
            path = path.replace('\\', '/');
        }
        return path;
    }

    /**
     * 获取jdk 的bin路径
     */
    public static String getJdkBinPath() {
        String path = System.getProperty("sun.boot.library.path");
        if (!StringUtil.checkEmpty(path)) {
            path = getJdkPath();
            if (StringUtil.checkEmpty(path)) {
                path = appendPathSimple(path, "/bin");
            }
        } else {
            path = path.replace('\\', '/');
        }
        return path;
    }

    /**
     * 移动文件
     *
     * @param fromPath 必须是文件
     * @param toPath   必须是文件
     */
    public static boolean moveFile(String fromPath, String toPath, boolean override) {
        return moveFile(new File(fromPath), new File(toPath), override);
    }

    /**
     * 移动文件
     *
     * @param fromFile 必须是文件
     * @param toFile   必须是文件
     */
    public static boolean moveFile(File fromFile, File toFile, boolean override) {
        if (toFile.exists()) { //如果目标已经存在
            if (!override) return false;
            toFile.delete();
        } else {
            toFile.getParentFile().mkdirs();
        }

        if (fromFile.exists()) {
            return fromFile.renameTo(toFile);
        }
        return false;
    }

    /**
     * 通过后缀名获取mime.  简单的键值对查找.  未找到则返回默认application/octet-stream
     *
     * @param ext
     *
     * @return
     */
    public static String getMime(String ext) {
        return getMime(ext, "application/octet-stream");
    }

    /**
     * 通过后缀名获取mime.  简单的键值对查找
     *
     * @param ext
     *
     * @return
     */
    public static String getMime(String ext, String defMime) {
        if (mimeMap == null) {
            mimeMap = new MimeMap();
        }
        return StringUtil.checkEmpty(mimeMap.getMime(ext), defMime);
    }

    /**
     * 判断后缀名是否是图片  "jpg", "jpeg", "bmp", "png", "gif", "ico"
     *
     * @param suffix 会通过.切取字符串
     */
    public static boolean isImageSuffix(String suffix) {
        if (!StringUtil.checkEmpty(suffix)) return false;
        int index = suffix.lastIndexOf('.');
        if (index > -1) suffix = suffix.substring(index);
        suffix = suffix.toLowerCase();
        for (String s : IMAGE_SUFFIX) {
            if (StringUtil.checkEqual(suffix, s)) return true;
        }
        return false;
    }

    /**
     * 追加文件名  例如:   xx.txt+1 => xx1.txt
     */
    public static String appendToFileName(String path, String append) {
        StringBuilder sb = new StringBuilder();
        int i = path.indexOf(".");
        if (i > 0) {
            sb.append(path.substring(0, i));
            sb.append(append);
            sb.append(path.substring(i, path.length()));
        } else {
            sb.append(path);
            sb.append(append);
        }
        return sb.toString();
    }

    /**
     * 获取系统临时目录.通过java.io.tmpdir属性获取
     */
    public static String getTempDir() {
        String dir = System.getProperty(TEMP_DIR);
        if (StringUtil.checkEmpty(dir)) {
            return dir.replace('\\', '/');
        }
        return "/";
    }

    /**
     * 获取系统临时目录.通过java.io.tmpdir属性获取
     */
    public static String getTempDir(String relPath) {
        String dir = System.getProperty(TEMP_DIR);
        if (!StringUtil.checkEmpty(dir)) {
            dir = "/";
        }
        return Paths.get(dir, relPath).toString().replace('\\', '/');
    }

    /**
     * 创建一个流对象
     */
    public static InputStream buildInputStream(String ss) {
        return new ByteArrayInputStream(ss.getBytes());
    }

    /**
     * 读取流内容
     */
    public static byte[] readStream(InputStream is) throws IOException {
        //		log.trace(format, arg);
        if (is == null) throw new IOException("InputStream can't be null");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4 * 1024];
        int n = 0;
        while ((n = is.read(buf)) > 0) {
            bos.write(buf, 0, n);
        }
        return bos.toByteArray();
    }

    /**
     * 读取字节流
     */
    public static char[] readReader(Reader read) throws IOException {
        if (read == null) throw new IOException("InputStream can't be null");
        CharArrayWriter bos = new CharArrayWriter();
        char[] buf = new char[4 * 1024];
        int n = 0;
        while ((n = read.read(buf)) > 0) {
            bos.write(buf, 0, n);
        }
        return bos.toCharArray();
    }

    /**
     * 读取流内容
     *
     * @param is
     * @param encode
     *
     * @return
     *
     * @throws IOException
     */
    public static String readStreamString(InputStream is, String encode) throws IOException {
        return new String(readStream(is), Charset.forName(encode));
    }

    /**
     * 读取流字符串
     *
     * @param is
     *
     * @return
     *
     * @throws IOException
     */
    public static String readReaderString(Reader is) throws IOException {
        return new String(readReader(is));
    }

    /**
     * 读取流内容
     *
     * @param is
     * @param charset
     *
     * @return
     *
     * @throws IOException
     */
    public static String readStreamString(InputStream is, Charset charset) throws IOException {
        return new String(readStream(is), charset);
    }

    /**
     * 读取流内容,限定长度
     *
     * @param is
     * @param len
     *
     * @return
     *
     * @throws IOException
     */
    public static byte[] readStream(InputStream is, int len) throws IOException {
        if (len < 1) throw new IOException("ReadStream error,len<1");
        byte[] buf = new byte[len];
        int n = is.read(buf);
        if (n < len) {
            byte[] temp = new byte[n];
            System.arraycopy(buf, 0, temp, 0, n);
            return temp;
        }
        return buf;
    }

    /**
     * 读取流内容,限定长度
     *
     * @param is
     * @param len
     *
     * @return
     *
     * @throws IOException
     */
    public static char[] readReader(Reader is, int len) throws IOException {
        if (len < 1) throw new IOException("ReadStream error,len<1");
        char[] buf = new char[len];
        int n = is.read(buf);
        if (n < len) {
            char[] temp = new char[n];
            System.arraycopy(buf, 0, temp, 0, n);
            return temp;
        }
        return buf;
    }

    /**
     * 读取文件的内容
     *
     * @param absPath 文件绝对路径
     *
     * @throws IOException
     */
    public static byte[] readFileByte(String absPath) throws IOException {
        File f = new File(absPath);
        int size = (int) f.length();
        if (size == 0) return new byte[0];
        InputStream is = new FileInputStream(f);
        byte[] b = readStream(is, size);
        close(is);
        return b;
    }

    /**
     * 读取文件的内容
     *
     * @throws IOException
     */
    public static String readFileString(String absPath, String encoding) throws IOException {
        byte[] b = readFileByte(absPath);
        if (encoding == null || encoding.length() < 1) return new String(b, Charset.defaultCharset());
        try {
            return new String(b, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }
    }

    /**
     * 安全的读取文件文本.如果文件不存在将返回null
     *
     * @param absPath
     * @param encoding
     *
     * @return
     */
    public static String readFileStringSafe(String absPath, String encoding) {
        File f = new File(absPath);
        if (!f.exists()) return null;
        int size = (int) f.length();
        if (size == 0) return "";
        try {
            InputStream is = new FileInputStream(f);
            byte[] b = readStream(is, size);
            close(is);
            if (encoding == null || encoding.length() < 1) return new String(b, Charset.defaultCharset());
            return new String(b, encoding);

        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 获取文件的行
     *
     * @throws IOException
     */
    public static List<String> readFileLines(String absPath, String encoding) throws IOException {
        return readStreamLines(new FileInputStream(absPath), encoding);
    }

    /**
     * 将stream读取成行
     */
    public static List<String> readStreamLines(InputStream is, String encoding) {
        List<String> list = new ArrayList<String>();
        Reader reader = null;
        if (encoding == null) reader = new InputStreamReader(is, Charset.defaultCharset());
        else {
            try {
                reader = new InputStreamReader(is, encoding);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        BufferedReader breader = new BufferedReader(reader, 1024);
        String s = null;
        do {
            try {
                while ((s = breader.readLine()) != null) {
                    s = s.trim();
                    if (!s.startsWith("#")) list.add(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (s != null);
        try {
            breader.close();
            reader.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将byte数组写入文件
     *
     * @param absPath
     *
     * @return
     *
     * @throws IOException
     */
    public static void writeFileByte(String absPath, byte[] buf) throws IOException {
        OutputStream os = getOutputStreamCreate(absPath);
        os.write(buf);
        close(os);
    }

    /**
     * 将byte数组写入文件
     *
     * @param absPath
     *
     * @return
     *
     * @throws IOException
     */
    public static void writeFileByte(String absPath, InputStream is) throws IOException {
        OutputStream os = getOutputStreamCreate(absPath);
        copyStream(is, os);
        close(os);
    }

    /**
     * 将InputStream写入文件,并且返回流长度
     *
     * @param absPath
     * @param is
     *
     * @return
     *
     * @throws IOException
     */
    public static long writeFileByteCount(String absPath, InputStream is) throws IOException {
        OutputStream os = getOutputStreamCreate(absPath);
        long count = copyStream2(is, os);
        close(os);
        return count;
    }

    /**
     * 读取包文件的文件,如果未找到则抛出异常
     *
     * @param packPath 包路径,以/开始
     *
     * @return
     */
    public static byte[] readPackageByte(String packPath) throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(packPath);
        if (is != null) {
            return readStream(is);
        }
        throw new IOException("Can't find Package File!");
    }

    //	public void test(){
    //
    //	}

    /**
     * 判断地址是否是绝对地址.linux通过/开始判断. windows通过出现:判断
     */
    public static boolean isAbsolutePath(String path) {
        if (OSUtil.isWindows()) {
            return path.indexOf(':') > -1;
        }
        return path.startsWith("/");
    }

    /**
     * 判断路径是否存在
     */
    public static boolean isExists(String absPath) {
        return new File(absPath).exists();
    }

    /**
     * 通过md5检测文件是否变动,有变动则返回true,文件不存在也返回true
     */
    public static boolean isChangedMd5(String absPath, String md5) {
        File f = new File(absPath);
        if (f.exists()) {
            try {
                InputStream is = new FileInputStream(f);
                byte[] b = EncryptUtil.digestMd5(is);
                String s = NumberUtil.parseByteToHex(b);
                if (md5.equalsIgnoreCase(s)) return false;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 测试文件是否可写
     */
    public static boolean isCanWrite(String path) {
        File f = new File(path);
        return f.canWrite();
    }

    /**
     * 是否可以被重命名
     */
    public static boolean isCanRename(String path) {
        File f = new File(path);
        if (!f.exists()) return true;
        return f.renameTo(f);
    }

    /**
     * 获取重命名的路径.仅修改名称部分,不会更改后缀.例如: (/a.txt,b)=> /b.txt
     */
    public static String getRenamePath(String absPath, String newName) {
        StringBuilder sb = new StringBuilder();
        int index1 = NumberUtil.getBigger(absPath.lastIndexOf('/'), absPath.lastIndexOf('\\'));
        if (index1 > -1) {
            sb.append(absPath.substring(0, index1 + 1));
        }
        sb.append(newName);
        int index2 = absPath.lastIndexOf('.');
        if (index2 > -1 && index2 > index1) {
            sb.append(absPath.substring(index2));
        }
        return sb.toString();
    }

    /**
     * 获取重命名的路径.包含后缀.例如: (/a.txt,b.exe)=> /b.exe
     */
    public static String getRenamePathWithExt(String absPath, String newName) {
        StringBuilder sb = new StringBuilder();
        int index1 = NumberUtil.getBigger(absPath.lastIndexOf('/'), absPath.lastIndexOf('\\'));
        if (index1 > -1) {
            sb.append(absPath.substring(0, index1 + 1));
        }
        sb.append(newName);
        return sb.toString();
    }

    /**
     * 从命名文件.仅改名文件名部分.例如: (/a.txt,b)=> /b.txt
     */
    public static boolean rename(String absPath, String newName) {
        String newPath = getRenamePath(absPath, newName);
        return new File(absPath).renameTo(new File(newPath));
    }

    /**
     * 获取重命名的路径.包含后缀.例如: (/a.txt,b.exe)=> /b.exe
     */
    public static boolean renameWithExt(String absPath, String newName) {
        String newPath = getRenamePathWithExt(absPath, newName);
        return new File(absPath).renameTo(new File(newPath));
    }

    /**
     * 从命名成新路径
     */
    public static boolean renameTo(String absPath, String newPath) {
        return new File(absPath).renameTo(new File(newPath));
    }

    /**
     * 通过系统层面获取相对路径,以/进行分割. 是以/开始
     */
    public static String getRelPath(File dir, File file) {
        try {
            String dirPath = dir.getCanonicalPath();
            String filePath = file.getCanonicalPath();
            if (dirPath.length() > filePath.length()) {
                throw new ServiceException("文件不是在目录下级:目录(%s) 文件(%s)", dirPath, filePath);
            }
            if (!StringUtil.checkEqual(filePath.substring(0, dirPath.length()), dirPath)) {
                throw new ServiceException("文件不是在目录下级:目录(%s) 文件(%s)", dirPath, filePath);
            }
            String relPath = filePath.substring(dirPath.length());
            relPath = relPath.replace('\\', '/');
            return relPath;
        } catch (IOException e) {
            throw new ServiceException("获取文件路径错误:" + e.getMessage());
        }
    }

    /**
     * 通过系统层面获取相对路径,以/进行分割. 是以/开始
     *
     * @param dir
     * @param file
     *
     * @return
     */
    public static String getRelPath(String dir, String file) {
        return getRelPath(new File(dir), new File(file));
    }

    /**
     * 方法仅仅简单进行路径截取
     * 获取相对目录.会将\转换成/进行处理.需要路径是整洁的路径
     * 注意:仅仅是进行长度截取.
     *
     * @param dir  短目录
     * @param path 长目录
     *
     * @return
     */
    @Deprecated
    public static String getRelativePath(String dir, String path) {
        dir = dir.replace('\\', '/');
        path = path.replace('\\', '/');
        int index = path.indexOf(dir);
        if (index > -1) {
            String rel = path.substring(index + dir.length());
            return rel;
        }
        return null;
    }

    /**
     * 简单方式拼接路径.如果都没有结束符则中间增加/,都有则减少一个.
     * 路径中出现其他错误或冗余,不会进行修正		path1+"/"+path2
     *
     * @param path1
     * @param path2
     *
     * @return
     */
    public static String appendPathSimple(String path1, String path2) {

        boolean t1 = path1.endsWith("/") || path1.endsWith("\\");
        boolean t2 = path2.startsWith("/") || path2.startsWith("\\");

        StringBuilder sb = new StringBuilder();
        sb.append(path1);
        if (t1 && t2) {
            sb.append(path2.substring(1));
        } else if (!t1 && !t2) {
            sb.append("/");
            sb.append(path2);
        } else {
            sb.append(path2);
        }
        return sb.toString().replace('\\', '/');
    }

    /**
     * <pre>
     * 组合路径,会将 \替换成/	只适合组合文件的路径
     * 警告:路径中不要出现./而使用../
     * 规则:
     *      路径2中存在:/时,则直接返回路径2
     *      ../进行表示父目录
     *      ./路径不会处理
     * 注意:Http的路径不能使用此方法,应该使用HttpTool.appendPath();
     * </pre>
     *
     * @param path1 主路径
     * @param path2 相对路径
     *
     * @return
     */
    public static String appendPath(String path1, String path2) {
        String temp = null;
        if (path2 != null) {
            if (path2.indexOf(":/") > -1) return StringUtil.replaceChar(path2, '\\', '/');
            else if (path2.startsWith("../") || path2.startsWith("..\\")) {
                int index1 = path1.lastIndexOf("/");
                int index2 = path1.lastIndexOf("\\");
                if (index2 > index1) index1 = index2;
                if (index1 > -1) {
                    path1 = path1.substring(0, index1);
                }
                path1 = StringUtil.replaceChar(path1, '\\', '/');
                path2 = StringUtil.replaceChar(path2, '\\', '/');
                temp = appendParentPath(path1, path2);
            } else if (path1.endsWith("/") || path1.endsWith("\\")) {
                if (path2.startsWith("/") || path2.startsWith("\\")) temp = path1 + path2.substring(1, path2.length());
                else temp = path1 + path2;
            } else {
                if (path2.startsWith("/") || path2.startsWith("\\")) temp = path1 + path2;
                else temp = path1 + "/" + path2;
            }
            return StringUtil.replaceChar(temp, '\\', '/');
        }
        return StringUtil.replaceChar(path1, '\\', '/');
    }

    /**
     * 对路径进行清洗.规则: 将 \ 替换成 /  ,替换多余的  // 为  /
     *
     * @param path
     *
     * @return
     */
    public static String cleanPath(String path) {
        String s = StringUtil.replaceChar(path, '\\', '/');
        while (s.indexOf("//") > -1) {
            s = s.replace("//", "/");
        }
        return s;
    }

    /**
     * <pre>
     * 构建路径,回退 ../路径,路径分隔符使用必须使用/,一个 ../回退一 /
     * d:/a/b/c/d	+	../		==>		d:/a/b/c
     * </pre>
     *
     * @param siteParent
     * @param path
     *
     * @return
     */
    public static String appendParentPath(String siteParent, String path) {
        int index1 = siteParent.lastIndexOf("/");
        int index2 = path.indexOf("../");
        if (index2 > -1 && index1 > -1) {
            return appendParentPath(siteParent.substring(0, index1), path.substring(index2 + 3, path.length()));
        }
        return siteParent + "/" + path;

    }

    /**
     * 获取文件名在目录中文件的顺序.不计算目录.
     *
     * @param dir
     * @param fileName
     *
     * @return
     */
    public static int getFileIndex(File dir, String fileName) {
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
        if (files != null) {
            int index = -1;
            for (File file : files) {
                index++;
                if (StringUtil.checkEqual(file.getName(), fileName)) return index;
            }
            return index;
        }
        return -1;
    }

    /**
     * 获取文件名在目录中文件的顺序.不计算目录.从0开始
     *
     * @param path
     *
     * @return
     */
    public static int getFileIndex(String path) {
        File file = new File(path);
        return getFileIndex(file.getParentFile(), file.getName());
    }

    /**
     * 获取文件名在目录中文件的顺序.不计算目录..从0开始
     */
    public static int getFileIndex(File file) {
        return getFileIndex(file.getParentFile(), file.getName());
    }

    /**
     * 获取父目录,所有分割符是\	格式:E:\01_J2EE\Test6\WebContent\WEB-INF\classes
     * 返回路径不是以\结束
     */
    public static String getParentDir(String path) {
        return new File(path).getParent();
    }

    /**
     * <pre>
     * 获取路径
     * 注意:
     * 在打包情况下,访问包文件中的文件会有错误
     * 调用:	参数			结果
     * </pre>
     *
     * @param relPath 相对路径,	/表示class目录
     */
    protected static String getClassResourcePath(String relPath) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String path = url.getPath();
        try {
            path = decodePath(path); //解码路径
            path = appendPath(path, relPath);
            path = URLDecoder.decode(path, Charset.defaultCharset().displayName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(path).getPath();
    }

    /**
     * 获取web工程的资源,/web-info目录或者/src目录
     *
     * @param relPath
     *
     * @return
     */
    public static String getWebResource(String relPath) {
        if (!relPath.startsWith("/")) relPath = "/" + relPath;
        String path = null;
        if (relPath.startsWith("/WEB-INF")) {
            path = getWebRootDir(relPath);
        } else {
            path = getClassDir(relPath);
        }
        return path;
    }

    /**
     * 判断class是否是在jar中
     *
     * @param cls
     *
     * @return
     */
    public static boolean isClassInJar(Class<?> cls) {
        URL url = cls.getResource("");
        return StringUtil.checkEqual(url.getProtocol(), "jar");
    }

    /**
     * 获取当前class的路径. 如果relPath为空着以/结尾.  否则以relPath结尾
     * 非打包下:  ""或者"/"  获取到类所在的路径就是 xx.class 的文件路径     例如:E:/99_TeamProject/benyou-dzds/ds-ofbiz13.07/bin/org/ofbiz/base/config/
     * 打包下:   ""或者"/"  获取到的是    jar包所在目录
     *
     * @param relPath
     *
     * @return
     */
    public static String getCurrentClassDir(Class<?> cls, String relPath) {
        URL url = cls.getResource("");
        if (StringUtil.checkEqualIgnoreCase(url.getProtocol(), "jar")) {
            String path = url.getPath();
            int si = 0;
            int ei = path.lastIndexOf("/", path.lastIndexOf('!'));
            if (path.startsWith("file:/")) {
                si = 6;
            }
            return appendPath(StringUtil.substring(path, si, ei), relPath);
        }
        String path = url.getPath();
        path = decodePath(path);
        path = new File(path).getPath();
        //后面需要增加/表示当前路径表示目录
        return appendPath(path + "/", relPath);
    }

    /**
     * 注意:功能跟getCurrentClassDomainDir一致.只是实现方式一致<br>
     * 通过getCurrentClassDir获取路径.然后减去类的路径. 如果relPath为空着以/结尾.  否则以relPath结尾.
     * 如果class文件在jar包中.则获取到的路径类似:  获取到的是    jar包所在目录
     *
     * @param cls
     * @param relPath
     *
     * @return
     */
    public static String getCurrentClassRootDir(Class<?> cls, String relPath) {
        URL url = cls.getResource("");
        if (StringUtil.checkEqualIgnoreCase(url.getProtocol(), "jar")) {
            String path = url.getPath();
            int si = 0;
            int ei = path.lastIndexOf("/", path.lastIndexOf('!'));
            if (path.startsWith("file:/")) {
                si = 6;
            }
            return appendPath(StringUtil.substring(path, si, ei), relPath);
        }
        String path = url.getPath();
        //对path切取package
        int plen = cls.getPackage().getName().length() + 1;
        path = path.substring(0, path.length() - plen);
        path = decodePath(path);
        path = new File(path).getPath();
        //后面需要增加/表示当前路径表示目录
        return appendPath(path + "/", relPath);
    }

    /**
     * 注意:功能跟getCurrentClassRootDir一致.只是实现方式一致.暂时先使用getCurrentClassRootDir<br>
     * 获取当前class的路径域路径.  即class的根目录.
     * 非打包下:  ""或"/"获取到 例如:web工程    E:/xx/xx/WEB-INF/classes/    java工程的 E:/xx/bin目录等
     * 打包下:  ""或"/" 获取到    jar包所在的目录. 例如: E:/xx/lib
     *
     * @param cls
     * @param relPath
     *
     * @return
     */
    public static String getCurrentClassDomainDir(Class<?> cls, String relPath) {
        String s = cls.getProtectionDomain().getCodeSource().getLocation().getPath();
        File f = new File(s);
        if (f.isDirectory()) {
            return appendPath(f.getPath() + "/", relPath);
        }
        return appendPath(f.getParent() + "/", relPath);
    }

    /**
     * 获取class所在的目录,分割符使用\		格式:E:\01_J2EE\Test6\WebContent\WEB-INF\classes+相对路径
     * <br>
     * 注意:当进行test时路径会出现错误
     *
     * @param relPath 相对路径
     *
     * @return
     */
    public static String getClassDir(String relPath) {
        return appendPath(getClassResourcePath("/"), relPath);
    }

    /**
     * 获取webroot所在的目录,采用class目录定位	格式:E:\01_J2EE\Test6\WebContent+相对路径
     *
     * @param relPath 相对路径
     *
     * @return
     */
    public static String getWebRootDir(String relPath) {
        return appendPath(getParentDir(getParentDir(getClassDir(null))), relPath);
    }

    /**
     * 获取web-info目录,采用class目录定位		格式:E:\01_J2EE\Test6\WebContent\WEB-INF+相对路径
     *
     * @param relPath 相对路径
     */
    public static String getWebInfoDir(String relPath) {
        return appendPath(getParentDir(getClassDir(null)), relPath);
    }

    /**
     * 获取web工程的class的相对目录
     *
     * @param relPath
     *
     * @return
     */
    public static String getWebClassDir(String relPath) {
        return appendPath(getClassResourcePath("/"), relPath);
    }

    /**
     * 获取路径中的目录部分
     *
     * @param path
     *
     * @return
     */
    public static String getPathDir(String path) {
        return new File(path).getParent();
    }

    /**
     * 获取工作目录,且后面追加相对路径
     */
    public static String getWorkDir(String relPath) {
        Path path = Paths.get(System.getProperty("user.dir"), relPath);
        return path.normalize().toString();
    }

    /**
     * 获取用户目录,且后面追加相对路径
     */
    public static String getUserDir(String relPath) {
        Path path = Paths.get(System.getProperty("user.home"), relPath);
        return path.normalize().toString();
    }

    /**
     * 获取路径的文件名部分
     */
    public static String getPathFileName(String path) {
        return new File(path).getName();
    }

    /**
     * 获取路径的文件部分不包含后缀名
     */
    public static String getPathFileNameNoExt(String path) {
        String name = getPathFileName(path);
        int index = name.lastIndexOf('.');
        if (index > -1) {
            return name.substring(0, index);
        }
        return name;
    }

    /**
     * 获取路径中文件名的后缀名	不包含.
     *
     * @param path
     *
     * @return
     */
    public static String getPathFileExt(String path) {
        String name = getPathFileName(path);
        int index = name.lastIndexOf('.');
        if (index > -1) {
            int len = name.length();
            index = index + 1;
            if (index >= len) return "";
            return name.substring(index, len);
        }
        return "";
    }

    /**
     * 获取简单的文件路径,会解析../和./,是绝对路径.如果获取CanonicalPath失败则返回AbsolutePath
     *
     * @param file
     *
     * @return
     */
    public static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取简单的文件路径,会解析../和./,是绝对路径.如果获取CanonicalPath失败则返回AbsolutePath
     *
     * @param path
     *
     * @return
     */
    public static String getCanonicalPath(String path) {
        return getCanonicalPath(new File(path));
    }

    /**
     * 验证扩展名是否在指定的扩展名中,不区分大小写,后缀名中不包含. ,当exts为空或个数为0时总是返回true,否则路径扩展名必须在exts中才返回true
     *
     * @param path
     * @param exts
     *
     * @return
     */
    public static boolean checkFileExtIn(String path, String... exts) {
        if (exts == null || exts.length < 1) return true;
        String ext = getPathFileExt(path).toLowerCase();
        for (String temp : exts) {
            if (ext.equals(temp.toLowerCase())) return true;
        }
        return false;
    }

    /**
     * 查找文件,可以是绝对路径,也可以时相对路径
     * 搜索路径:	绝对路径  -->	WEB-INF目录相对路径 -->Class目录相对路径
     *
     * @param relPath
     *
     * @return
     */
    public static File findFile(String relPath) {
        File temp = new File(relPath);
        if (!temp.exists()) {
            temp = new File(getWebInfoDir(relPath));
            if (!temp.exists()) {
                temp = new File(getClassDir(relPath));
            }
        }
        return temp;
    }

    /**
     * 获取OutputStream	,文件不存在则创建,创建失败时 返回null
     */
    public static OutputStream getOutputStreamCreate(String absPath) throws IOException {
        return new FileOutputStream(createFile(absPath));
    }

    /**
     * 获取InputStream	,文件不存在则创建,创建失败时 返回null
     */
    public static InputStream getInputStreamCreate(String absPath) throws IOException {
        return new FileInputStream(createFile(absPath));
    }

    /**
     * 获取OutputStream,以追加的模式 ,文件不存在则创建,创建失败时 返回null
     *
     * @param absPath
     *
     * @return
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static OutputStream getOutputStreamCreateAppend(String absPath) throws FileNotFoundException, IOException {
        return new FileOutputStream(createFile(absPath), true);
    }

    /**
     * 获取对象的序列化
     *
     * @param obj
     *
     * @return
     *
     * @throws IOException
     */

    public static byte[] parseObjectToByte(Object obj) throws IOException {
        if (obj == null) throw new IOException("Can't serialization null object");
        if (!(obj instanceof Serializable))
            throw new IOException("Class(" + obj.getClass() + ") is not implement of Serializable interface");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        return bos.toByteArray();
    }

    /**
     * 将序列转换成对象
     */
    public static Object parseByteToObject(byte[] buf) throws IOException {
        return parseByteToObject(buf, 0, buf.length);
    }

    /**
     * 将序列转换成对象
     */
    public static Object parseByteToObject(byte[] buf, int offset, int length) throws IOException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, length);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * 获取目录,不存在时创建,路径是绝对路径
     */
    public static File createDir(String absPath) throws IOException {
        //		String temp=absPath;
        File dir = new File(absPath);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    /**
     * 获取文件,不存在时创建,路径必须是绝对路径
     *
     * @return
     *
     * @throws IOException
     */
    public static File createFile(String absPath) throws IOException {
        String temp = absPath;
        File file = new File(temp);
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (dir != null && !dir.exists()) dir.mkdirs();
            if (!file.createNewFile()) {
                throw new IOException("Can't create File(path=" + absPath + ").");
            }
        }
        return file;
    }

    /**
     * 复制文件,绝对路径
     *
     * @param fpath
     * @param tpath
     *
     * @return
     */
    public static boolean copyFile(String fpath, String tpath, boolean override) {
        boolean re = false;
        try {
            if (!isExists(fpath)) return false;
            if (!override && isExists(tpath)) {
                return false;
            }
            FileInputStream fis = new FileInputStream(fpath);
            FileOutputStream fos = new FileOutputStream(createFile(tpath));
            copyStream(fis, fos);
            close(fis);
            close(fos);
            re = true;
        } catch (Exception e) {
            log.error("Copy File error:" + e.getMessage());
        }
        return re;
    }

    public static boolean copyFile(String fpath, String tpath) {
        return copyFile(fpath, tpath, false);
    }

    /**
     * 复制流,不回关闭流
     *
     * @param is
     * @param os
     *
     * @return
     *
     * @throws IOException
     */
    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[4096];
        int n = 0;
        while ((n = is.read(buf)) > 0) {
            os.write(buf, 0, n);
        }
    }

    /**
     * 复制流,返回流长度
     *
     * @param is
     * @param os
     *
     * @return
     *
     * @throws IOException
     */
    public static long copyStream2(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[4096];
        int n = 0;
        long len = 0;
        while ((n = is.read(buf)) > 0) {
            os.write(buf, 0, n);
            len += n;
        }
        return len;
    }

    /**
     * 复制流,并且计算md5
     *
     * @param is
     * @param os
     *
     * @return
     *
     * @throws IOException
     */
    public static Md5Info copyStreamMd5(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[4096];
        int n = 0;
        long len = 0;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("md5");
            while ((n = is.read(buf)) > 0) {
                os.write(buf, 0, n);
                digest.update(buf, 0, n);
                len += n;
            }
            byte[] md5Bytes = digest.digest();
            return new Md5Info(md5Bytes, len);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * 删除文件,绝对路径.如果传入目录则,采用递归删除
     */
    public static boolean delFile(String abspath) {
        return delFile(new File(abspath));
    }

    /**
     * 删除文件,绝对路径.如果传入目录则,采用递归删除
     */
    public static boolean delFile(File f) {
        if (f.isDirectory()) {
            String[] childs = f.list();
            if (childs != null) {
                for (String child : childs) {
                    File cf = new File(f, child);
                    delFile(cf);
                }
            }
        }
        return f.delete();
    }

    /**
     * 关闭流
     *
     * @param is
     *
     * @return
     */
    public static boolean close(InputStream is) {
        if (is == null) return true;
        try {
            is.close();
            return true;
        } catch (IOException e) {
            try {
                is.close();
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 关闭流
     *
     * @param os
     *
     * @return
     */
    public static boolean closeFlush(OutputStream os) {
        if (os == null) return true;
        try {
            os.flush();
            os.close();
            return true;
        } catch (IOException e) {
            try {
                os.close();
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 关闭流
     *
     * @param os
     *
     * @return
     */
    public static boolean close(OutputStream os) {
        if (os == null) return true;
        try {
            os.close();
            return true;
        } catch (IOException e) {
            try {
                os.close();
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 关闭对象
     *
     * @param is
     *
     * @return
     */
    public static boolean close(Closeable is) {
        if (is == null) return true;
        try {
            is.close();
            return true;
        } catch (IOException e) {
            try {
                is.close();
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 通过日期构建目录结构  返回  结构   2012/06/12/	无/开始,有/结束
     *
     * @return
     */
    public static String buildDirPathByDate() {
        return DateUtil.toDateTime(new Date(), "yyyy/MM/dd/");
    }

    /**
     * 通过月构建目录结构  返回  结构   2012/06/	无/开始,有/结束
     *
     * @return
     */
    public static String buildDirPathByMonth() {
        return DateUtil.toDateTime(new Date(), "yyyy/MM/");
    }

    /**
     * 通过当前是年中的星期数构建目录结构  返回  结构   2012/53/	无/开始,有/结束
     *
     * @return
     */
    public static String buildDirPathByYearWeek() {
        return DateUtil.toDateTime(new Date(), "yyyy/ww/");
    }

    /**
     * 使用gzip方式压缩数据
     */
    public static byte[] gzipByte(byte[] buf, int offset, int length) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(bos);
        gos.write(buf, offset, length);
        close(gos);
        close(bos);
        return bos.toByteArray();
    }

    public static byte[] gzipByte(byte[] buf) throws IOException {
        return gzipByte(buf, 0, buf.length);
    }

    /**
     * 解压gzip数据
     */
    public static byte[] ungzipByte(byte[] buf, int offset, int length) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, length);
        GZIPInputStream gis = new GZIPInputStream(bis);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        copyStream(gis, bos);
        return bos.toByteArray();
    }

    public static byte[] ungzipByte(byte[] buf) throws IOException {
        return ungzipByte(buf, 0, buf.length);
    }

    /**
     * 对路径进行编码
     *
     * @param path
     *
     * @return
     */
    public static String encodePath(String path) {
        try {
            return URLEncoder.encode(path, Charset.defaultCharset().displayName());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 对路径进行解码
     *
     * @param path
     *
     * @return
     */
    public static String decodePath(String path) {
        try {
            return URLDecoder.decode(path, Charset.defaultCharset().displayName());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 遍历目录下的所有 目录和文件
     */
    public static void traversalPath(File file, TraversalFilter filter) {
        File[] files = file.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) traversalPath(f, filter);
            filter.handle(f);
        }
    }

    public static interface TraversalFilter {
        public void handle(File file);
    }

    /**
     * 根据指定名称创建临时文件
     */
    public static File createTempFile(boolean exitDel) throws IOException {
        File file = File.createTempFile(FileUtil.class.getSimpleName() + " - ", ".tmp");
        if (exitDel) file.deleteOnExit();
        return file;
    }

    /**
     * 如果已经存在则不会创建
     *
     * @param name
     *
     * @return
     *
     * @throws IOException
     */
    public static File createTempFile(String name, boolean exitDel) throws IOException {
        String path = appendPath(getTempDir(), name);
        File file = createFile(path);
        if (exitDel) file.deleteOnExit();
        return file;
    }

    /**
     * @param is
     *
     * @return
     *
     * @throws IOException
     */
    public static File createStreamTempFile(InputStream is) throws IOException {
        File f = createTempFile(true);
        FileOutputStream fos = new FileOutputStream(f);
        FileUtil.copyStream(is, fos);
        FileUtil.close(fos);
        return f;
    }

    /**
     * @param buf
     *
     * @return
     *
     * @throws IOException
     */
    public static File createByteTempFile(byte[] buf) throws IOException {
        File f = createTempFile(true);
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(buf);
        fos.flush();
        FileUtil.close(fos);
        return f;
    }

    /**
     * 对流创建输入监听器
     */
    public static InputStreamReadThread buildReadThread(InputStream is, InputStreamListener... listeners) {
        InputStreamReadThread thread = new InputStreamReadThread(is, listeners);
        thread.start();
        return thread;
    }

    /**
     * 对流创建输入监听器
     */
    public static SimpleInputStreamReadThread buildSimpleReadThread(InputStream is, InputStreamListener listeners) {
        SimpleInputStreamReadThread thread = new SimpleInputStreamReadThread(is, listeners);
        thread.start();
        return thread;
    }

    /**
     * 等待多久时间直到InputStream available
     *
     * @param is
     * @param time
     *
     * @return
     */
    public static boolean waitAvailable(InputStream is, long time) {
        if (is == null) {
            return false;
        }
        try {
            long SLEEP_TIME = 10L;
            long elapsedTime = 0;
            do {
                if (is.available() > 0) {
                    return true;
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                    elapsedTime += SLEEP_TIME;
                } catch (InterruptedException e) {

                }
            } while (elapsedTime <= time);
            return is.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 输入流监听器
     *
     * @author pingchundgg
     */
    public static interface InputStreamListener {
        public void onReceive(byte[] data);
    }

    /**
     * 输入流读取线程.
     * 存在问题: 如果缓冲区没有被填满,一直不会调用完成事件
     *
     * @author pingchundgg
     */
    public static class InputStreamReadThread implements InputStreamListener, Runnable {
        private List<InputStreamListener> listeners = new ArrayList<InputStreamListener>();
        private InputStream is = null;
        private Thread thread = null;
        private boolean flag = true;

        public InputStreamReadThread(InputStream is, InputStreamListener... listeners) {
            this.is = is;
            if (listeners != null) {
                for (InputStreamListener listener : listeners)
                    this.listeners.add(listener);
            }

        }

        public void stop() {
            flag = false;
        }

        public void start() {
            if (thread == null) {
                thread = new Thread(this);
                thread.start();
            }
        }

        @Override
        public void onReceive(byte[] data) {
            for (InputStreamListener listener : listeners) {
                listener.onReceive(data);
            }
        }

        @Override
        public void run() {
            while (flag) {
                try {
                    byte[] data = readStream(is);
                    if (data != null && data.length > 0) {
                        onReceive(data);
                    } else {
                        ThreadUtil.safeSleep(10);
                    }
                    Thread.yield(); //放弃时间片
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 简单的InputStreamReadThread
     *
     * @author pingchundgg
     */
    public static class SimpleInputStreamReadThread implements Runnable {
        private List<InputStreamListener> listeners = new ArrayList<>();
        private InputStream is = null;
        private Thread thread = null;
        private boolean flag = true;

        public SimpleInputStreamReadThread(InputStream is) {
            this(is, null);
        }

        public SimpleInputStreamReadThread(InputStream is, InputStreamListener listener) {
            this.is = is;
            if (listener != null) {
                this.listeners.add(listener);
            }
        }

        public void stop() {
            flag = false;
        }

        public void start() {
            if (thread == null) {
                thread = new Thread(this);
                thread.start();
            }

        }

        public void addListener(InputStreamListener listener) {
            this.listeners.add(listener);
        }

        public void addListeners(List<InputStreamListener> listener) {
            this.listeners.addAll(listener);
        }

        public void removeListener(InputStreamListener listener) {
            this.listeners.remove(listener);
        }

        @Override
        public void run() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024 * 4);
            while (flag) {
                try {
                    int available = is.available();
                    if (available > 0) {
                        byte[] buf = new byte[available];
                        is.read(buf, 0, available);
                        onReceive(buf);
                    } else {
                        ThreadUtil.safeSleep(10);
                    }
                    Thread.yield(); //放弃时间片
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        protected void onReceive(byte[] data) {
            if (listeners == null || listeners.size() == 0) {
                return;
            }
            for (InputStreamListener listener : listeners) {
                listener.onReceive(data);
            }
        }

    }

    public static class MimeMap {
        private Map<String, String> mimeMap = new HashMap<String, String>();

        public MimeMap() {
            mimeMap.put("3gp", "video/3gpp");
            mimeMap.put("apk", "application/vnd.android.package-archive");
            mimeMap.put("ai", "application/postscript");
            mimeMap.put("aif", "audio/x-aiff");
            mimeMap.put("aifc", "audio/x-aiff");
            mimeMap.put("aiff", "audio/x-aiff");
            mimeMap.put("asc", "text/plain");
            mimeMap.put("atom", "application/atom+xml");
            mimeMap.put("au", "audio/basic");
            mimeMap.put("avi", "video/x-msvideo");
            mimeMap.put("bcpio", "application/x-bcpio");
            mimeMap.put("bin", "application/octet-stream");
            mimeMap.put("bmp", "image/bmp");
            mimeMap.put("cdf", "application/x-netcdf");
            mimeMap.put("cgm", "image/cgm");
            mimeMap.put("class", "application/octet-stream");
            mimeMap.put("cpio", "application/x-cpio");
            mimeMap.put("cpt", "application/mac-compactpro");
            mimeMap.put("csh", "application/x-csh");
            mimeMap.put("css", "text/css");
            mimeMap.put("dcr", "application/x-director");
            mimeMap.put("dif", "video/x-dv");
            mimeMap.put("dir", "application/x-director");
            mimeMap.put("djv", "image/vnd.djvu");
            mimeMap.put("djvu", "image/vnd.djvu");
            mimeMap.put("dll", "application/octet-stream");
            mimeMap.put("dmg", "application/octet-stream");
            mimeMap.put("dms", "application/octet-stream");
            mimeMap.put("doc", "application/msword");
            mimeMap.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
            mimeMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            mimeMap.put("dtd", "application/xml-dtd");
            mimeMap.put("dv", "video/x-dv");
            mimeMap.put("dvi", "application/x-dvi");
            mimeMap.put("dxr", "application/x-director");
            mimeMap.put("eps", "application/postscript");
            mimeMap.put("etx", "text/x-setext");
            mimeMap.put("exe", "application/octet-stream");
            mimeMap.put("ez", "application/andrew-inset");
            mimeMap.put("flv", "video/x-flv");
            mimeMap.put("gif", "image/gif");
            mimeMap.put("gram", "application/srgs");
            mimeMap.put("grxml", "application/srgs+xml");
            mimeMap.put("gtar", "application/x-gtar");
            mimeMap.put("gz", "application/x-gzip");
            mimeMap.put("hdf", "application/x-hdf");
            mimeMap.put("hqx", "application/mac-binhex40");
            mimeMap.put("htm", "text/html");
            mimeMap.put("html", "text/html");
            mimeMap.put("ice", "x-conference/x-cooltalk");
            mimeMap.put("ico", "image/x-icon");
            mimeMap.put("ics", "text/calendar");
            mimeMap.put("ief", "image/ief");
            mimeMap.put("ifb", "text/calendar");
            mimeMap.put("iges", "model/iges");
            mimeMap.put("igs", "model/iges");
            mimeMap.put("jnlp", "application/x-java-jnlp-file");
            mimeMap.put("jp2", "image/jp2");
            mimeMap.put("jpe", "image/jpeg");
            mimeMap.put("jpeg", "image/jpeg");
            mimeMap.put("jpg", "image/jpeg");
            mimeMap.put("js", "application/x-javascript");
            mimeMap.put("kar", "audio/midi");
            mimeMap.put("latex", "application/x-latex");
            mimeMap.put("lha", "application/octet-stream");
            mimeMap.put("lzh", "application/octet-stream");
            mimeMap.put("m3u", "audio/x-mpegurl");
            mimeMap.put("m4a", "audio/mp4a-latm");
            mimeMap.put("m4p", "audio/mp4a-latm");
            mimeMap.put("m4u", "video/vnd.mpegurl");
            mimeMap.put("m4v", "video/x-m4v");
            mimeMap.put("mac", "image/x-macpaint");
            mimeMap.put("man", "application/x-troff-man");
            mimeMap.put("mathml", "application/mathml+xml");
            mimeMap.put("me", "application/x-troff-me");
            mimeMap.put("mesh", "model/mesh");
            mimeMap.put("mid", "audio/midi");
            mimeMap.put("midi", "audio/midi");
            mimeMap.put("mif", "application/vnd.mif");
            mimeMap.put("mov", "video/quicktime");
            mimeMap.put("movie", "video/x-sgi-movie");
            mimeMap.put("mp2", "audio/mpeg");
            mimeMap.put("mp3", "audio/mpeg");
            mimeMap.put("mp4", "video/mp4");
            mimeMap.put("mpe", "video/mpeg");
            mimeMap.put("mpeg", "video/mpeg");
            mimeMap.put("mpg", "video/mpeg");
            mimeMap.put("mpga", "audio/mpeg");
            mimeMap.put("ms", "application/x-troff-ms");
            mimeMap.put("msh", "model/mesh");
            mimeMap.put("mxu", "video/vnd.mpegurl");
            mimeMap.put("nc", "application/x-netcdf");
            mimeMap.put("oda", "application/oda");
            mimeMap.put("ogg", "application/ogg");
            mimeMap.put("ogv", "video/ogv");
            mimeMap.put("pbm", "image/x-portable-bitmap");
            mimeMap.put("pct", "image/pict");
            mimeMap.put("pdb", "chemical/x-pdb");
            mimeMap.put("pdf", "application/pdf");
            mimeMap.put("pgm", "image/x-portable-graymap");
            mimeMap.put("pgn", "application/x-chess-pgn");
            mimeMap.put("pic", "image/pict");
            mimeMap.put("pict", "image/pict");
            mimeMap.put("png", "image/png");
            mimeMap.put("pnm", "image/x-portable-anymap");
            mimeMap.put("pnt", "image/x-macpaint");
            mimeMap.put("pntg", "image/x-macpaint");
            mimeMap.put("ppm", "image/x-portable-pixmap");
            mimeMap.put("ppt", "application/vnd.ms-powerpoint");
            mimeMap.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            mimeMap.put("ps", "application/postscript");
            mimeMap.put("qt", "video/quicktime");
            mimeMap.put("qti", "image/x-quicktime");
            mimeMap.put("qtif", "image/x-quicktime");
            mimeMap.put("ra", "audio/x-pn-realaudio");
            mimeMap.put("ram", "audio/x-pn-realaudio");
            mimeMap.put("ras", "image/x-cmu-raster");
            mimeMap.put("rdf", "application/rdf+xml");
            mimeMap.put("rgb", "image/x-rgb");
            mimeMap.put("rm", "application/vnd.rn-realmedia");
            mimeMap.put("roff", "application/x-troff");
            mimeMap.put("rtf", "text/rtf");
            mimeMap.put("rtx", "text/richtext");
            mimeMap.put("sgm", "text/sgml");
            mimeMap.put("sgml", "text/sgml");
            mimeMap.put("sh", "application/x-sh");
            mimeMap.put("shar", "application/x-shar");
            mimeMap.put("silo", "model/mesh");
            mimeMap.put("sit", "application/x-stuffit");
            mimeMap.put("skd", "application/x-koan");
            mimeMap.put("skm", "application/x-koan");
            mimeMap.put("skp", "application/x-koan");
            mimeMap.put("skt", "application/x-koan");
            mimeMap.put("smi", "application/smil");
            mimeMap.put("smil", "application/smil");
            mimeMap.put("snd", "audio/basic");
            mimeMap.put("so", "application/octet-stream");
            mimeMap.put("spl", "application/x-futuresplash");
            mimeMap.put("src", "application/x-wais-source");
            mimeMap.put("sv4cpio", "application/x-sv4cpio");
            mimeMap.put("sv4crc", "application/x-sv4crc");
            mimeMap.put("svg", "image/svg+xml");
            mimeMap.put("swf", "application/x-shockwave-flash");
            mimeMap.put("t", "application/x-troff");
            mimeMap.put("tar", "application/x-tar");
            mimeMap.put("tcl", "application/x-tcl");
            mimeMap.put("tex", "application/x-tex");
            mimeMap.put("texi", "application/x-texinfo");
            mimeMap.put("texinfo", "application/x-texinfo");
            mimeMap.put("tif", "image/tiff");
            mimeMap.put("tiff", "image/tiff");
            mimeMap.put("tr", "application/x-troff");
            mimeMap.put("tsv", "text/tab-separated-values");
            mimeMap.put("txt", "text/plain");
            mimeMap.put("ustar", "application/x-ustar");
            mimeMap.put("vcd", "application/x-cdlink");
            mimeMap.put("vrml", "model/vrml");
            mimeMap.put("vxml", "application/voicexml+xml");
            mimeMap.put("wav", "audio/x-wav");
            mimeMap.put("wbmp", "image/vnd.wap.wbmp");
            mimeMap.put("wbxml", "application/vnd.wap.wbxml");
            mimeMap.put("webm", "video/webm");
            mimeMap.put("wml", "text/vnd.wap.wml");
            mimeMap.put("wmlc", "application/vnd.wap.wmlc");
            mimeMap.put("wmls", "text/vnd.wap.wmlscript");
            mimeMap.put("wmlsc", "application/vnd.wap.wmlscriptc");
            mimeMap.put("wmv", "video/x-ms-wmv");
            mimeMap.put("wrl", "model/vrml");
            mimeMap.put("xbm", "image/x-xbitmap");
            mimeMap.put("xht", "application/xhtml+xml");
            mimeMap.put("xhtml", "application/xhtml+xml");
            mimeMap.put("xls", "application/vnd.ms-excel");
            mimeMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            mimeMap.put("xml", "application/xml");
            mimeMap.put("xpm", "image/x-xpixmap");
            mimeMap.put("xsl", "application/xml");
            mimeMap.put("xslt", "application/xslt+xml");
            mimeMap.put("xul", "application/vnd.mozilla.xul+xml");
            mimeMap.put("xwd", "image/x-xwindowdump");
            mimeMap.put("xyz", "chemical/x-xyz");
            mimeMap.put("zip", "application/zip");
        }

        public String getMime(String ext) {
            return mimeMap.get(ext);
        }
    }

    /**
     * md5信息
     *
     * @author cungle
     */
    public static class Md5Info {
        protected String md5;
        protected byte[] md5Bytes;
        protected long length;

        public Md5Info(byte[] md5Bytes, long length) {
            this.md5 = NumberUtil.parseByteToHex(md5Bytes);
            this.md5Bytes = md5Bytes;
            this.length = length;
        }

        public Md5Info(String md5, byte[] md5Bytes, long length) {
            this.md5 = md5;
            this.md5Bytes = md5Bytes;
            this.length = length;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public byte[] getMd5Bytes() {
            return md5Bytes;
        }

        public void setMd5Bytes(byte[] md5Bytes) {
            this.md5Bytes = md5Bytes;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

    }

    /**
     * 复制文件夹到另外一个文件夹
     *
     * @param oldPath
     * @param newPath
     *
     * @throws IOException
     */
    public static void copyDir(String oldPath, String newPath) throws IOException {
        File file = new File(oldPath);
        String[] filePath = file.list();
        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdir();
        }
        for (int i = 0; i < filePath.length; i++) {
            if ((new File(oldPath + File.separator + filePath[i])).isDirectory()) {
                copyDir(oldPath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
            }
            if (new File(oldPath + File.separator + filePath[i]).isFile()) {
                File source = new File(oldPath + File.separator + filePath[i]);
                File dest = new File(newPath + File.separator + filePath[i]);
                if (!(dest.exists())) {
                    Files.copy(source.toPath(), dest.toPath());
                }
            }
        }
    }

    /**
     * 替换文件名称特殊字符
     */
    public static String replaceIllegalChar(String fileName) {
        Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher = pattern.matcher(fileName);
        fileName = matcher.replaceAll("");
        return fileName;
    }

    /**
     * 创建文件锁
     */
    public static FileLock createFileLock(File file) throws IOException {
        FileOutputStream os = new FileOutputStream(file, true);
        return os.getChannel().lock();
        //FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.DELETE_ON_CLOSE);
        //return fileChannel.tryLock();
    }
}
