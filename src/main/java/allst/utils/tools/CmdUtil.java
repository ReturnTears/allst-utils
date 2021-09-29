package allst.utils.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * cmd执行工具
 *
 * @author June
 * @since 2021年09月
 */
public class CmdUtil {
    private final Charset writeCharset; //写入时的编码
    private final Charset readCharset; //读取时的编码
    private Process cmdProcess;

    private OutputStream out;
    private InputStream is;
    private InputStream erris;

    private List<ProcessOutHandler> handlers;

    /**
     * 是否异步读取
     */
    private boolean asyncRead = true;

    /**
     * 下面属性,仅异步读取时有效
     */
    private final ByteArrayOutputStream outbos = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errbos = new ByteArrayOutputStream();
    private FileUtil.InputStreamReadThread outThread = null;
    private FileUtil.InputStreamReadThread errThread = null;

    public CmdUtil() {
        writeCharset = Charset.defaultCharset();
        readCharset = Charset.defaultCharset();
    }

    public CmdUtil(String encoding) {
        writeCharset = Charset.forName(encoding);
        readCharset = Charset.forName(encoding);
    }

    /**
     * @param asyncRead 是否异步读取
     */
    public CmdUtil(String writeEncoding, String readEncoding, boolean asyncRead) {
        writeCharset = Charset.forName(writeEncoding);
        readCharset = Charset.forName(readEncoding);
        this.asyncRead = asyncRead;
    }

    /**
     * 执行linux命令.  执行cmd的内容
     */
    public void exeLinuxCommand(String cmd) throws IOException {
        exeRawCommand(cmd);
    }

    /**
     * 执行原始命令.完全执行 cmd的内容
     */
    public void exeRawCommand(String cmd) throws IOException {
        cmdProcess = Runtime.getRuntime().exec(cmd);

        out = cmdProcess.getOutputStream();
        is = cmdProcess.getInputStream();
        erris = cmdProcess.getErrorStream();

        if (asyncRead) {

            outThread = FileUtil.buildReadThread(is, new FileUtil.InputStreamListener() {
                @Override
                public void onReceive(byte[] data) {
                    synchronized (outbos) {
                        try {
                            handleProcessOut(new ProcessOutEvent(data, writeCharset));
                            outbos.write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });
            errThread = FileUtil.buildReadThread(erris, new FileUtil.InputStreamListener() {
                @Override
                public void onReceive(byte[] data) {
                    synchronized (errbos) {
                        try {
                            handleProcessOut(new ProcessOutEvent(data, writeCharset));
                            errbos.write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });
        }
    }

    /**
     * 执行批处理命令  命令会被附加到   cmd /C 后.   仅用于window调用
     */
    public void exeWindowCommand(String cmd) throws IOException {
        exeRawCommand(String.format("cmd /C %s", cmd));
    }

    /**
     * 写入字符串
     */
    public void write(String s) {
        write(s.getBytes(writeCharset));
    }

    /**
     * 写入字符串
     */
    public void write(byte[] b) {
        if (out != null) {
            try {
                out.write(b, 0, b.length);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String readOut() {
        if (asyncRead) {
            synchronized (outbos) {
                String re = new String(outbos.toByteArray(), readCharset);
                outbos.reset();
                return re;
            }
        } else {
            try {
                return new String(FileUtil.readStream(is), readCharset);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public String readError() {
        if (asyncRead) {
            synchronized (errbos) {
                String re = new String(errbos.toByteArray(), readCharset);
                errbos.reset();
                return re;
            }
        } else {
            try {
                return new String(FileUtil.readStream(erris), readCharset);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void stopCmd() {
        if (outThread != null)
            outThread.stop();
        if (errThread != null)
            errThread.stop();
        cmdProcess.destroy();
    }

    public int waitFor() {
        int re = -1;
        try {
            re = cmdProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ThreadUtil.safeSleep(10);
        return re;
    }

    public boolean isAsyncRead() {
        return asyncRead;
    }

    public void setAsyncRead(boolean asyncRead) {
        this.asyncRead = asyncRead;
    }

    /**
     * 处理输出
     */
    public void addProcessOutHandler(ProcessOutHandler handler) {
        if (handlers == null) {
            handlers = new ArrayList<ProcessOutHandler>();
        }
        handlers.add(handler);
    }

    /**
     * 处理输出
     */
    protected void handleProcessOut(ProcessOutEvent e) {
        if (handlers != null && handlers.size() > 0) {
            for (ProcessOutHandler h : handlers) {
                h.onReceive(e);
            }
        }
    }

    /**
     * 进程输出事件
     *
     * @author cungle
     */
    public static class ProcessOutEvent {
        private Charset charset;
        private byte[] buffer;
        private String content;

        public ProcessOutEvent(byte[] buffer, Charset charset) {
            this.buffer = buffer;
            this.charset = charset;
            this.content = new String(buffer, charset);
        }

        public byte[] getBuffer() {
            return buffer;
        }

        public void setBuffer(byte[] buffer) {
            this.buffer = buffer;
        }

        public Charset getCharset() {
            return charset;
        }

        public void setCharset(Charset charset) {
            this.charset = charset;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    /**
     * 进程输出处理器
     *
     * @author cungle
     */
    public static interface ProcessOutHandler {
        public void onReceive(ProcessOutEvent e);
    }
}
