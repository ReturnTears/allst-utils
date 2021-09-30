package allst.utils.tools;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码工具类
 *
 * @author June
 * @since 2021年09月
 */
public class QrCodeUtil {
    private Integer defaultWidth = 500;
    private Integer defaultHeight = 500;
    private String imgFormat = "jpg";

    public QrCodeUtil() {

    }

    public QrCodeUtil(Integer defaultWidth, Integer defaultHeight, String imgFormat) {
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.imgFormat = imgFormat;
    }

    public Integer getDefaultWidth() {
        return defaultWidth;
    }

    public void setDefaultWidth(Integer defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    public Integer getDefaultHeight() {
        return defaultHeight;
    }

    public void setDefaultHeight(Integer defaultHeight) {
        this.defaultHeight = defaultHeight;
    }

    public String getImgFormat() {
        return imgFormat;
    }

    public void setImgFormat(String imgFormat) {
        this.imgFormat = imgFormat;
    }

    /**
     * 生成二维码
     *
     * @param outputStream 输出流
     * @param content      生成内容
     */
    public void createQrCode(OutputStream outputStream, String content) throws WriterException, IOException {
        BitMatrix bitMatrix = getBitMatrix(content);
        // 开始画二维码
        MatrixToImageWriter.writeToStream(bitMatrix, imgFormat, outputStream);
    }

    /**
     * @param content content
     */
    public String createQrCodeBASE64(String content) throws IOException, WriterException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        createQrCode(outputStream, content);
        // todo
        // BASE64Encoder encoder = new BASE64Encoder();
        // return encoder.encode(outputStream.toByteArray());

        return "";
    }

    /**
     * @param content content
     */
    public BitMatrix getBitMatrix(String content) throws WriterException, IOException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        // 设置UTF-8， 防止中文乱码
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 设置二维码四周白色区域的大小
        hints.put(EncodeHintType.MARGIN, 0);
        // 设置二维码的容错性
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 画二维码，记得调用multiFormatWriter.encode()时最后要带上hints参数，不然上面设置无效
        return multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, defaultWidth, defaultHeight, hints);
    }

    /**
     * 创建二维码
     *
     * @param outFilePath 输出路径
     * @param content     内容
     */
    public void createQrCode(String outFilePath, String content) throws WriterException, IOException {
        OutputStream outputStream = new FileOutputStream(new File(outFilePath));
        createQrCode(outputStream, content);
    }

    /**
     * 读取二维码
     *
     * @param inputStream 输入流
     *
     * @return 二维码内容
     */
    public static String readQrCode(InputStream inputStream) throws Exception {
        // 从输入流中获取字符串信息
        BufferedImage image = ImageIO.read(inputStream);
        // 将图像转换为二进制位图源
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result = reader.decode(bitmap);
        System.out.println(result.getText());
        return result.getText();
    }

    /**
     * 读取二维码
     *
     * @param inFilePath 输入二维码路径
     *
     * @return 二维码内容
     */
    public static String readQrCode(String inFilePath) throws Exception {
        InputStream inputStream = new FileInputStream(new File(inFilePath));
        return readQrCode(inputStream);
    }

    public static void main(String[] args) {
        QrCodeUtil qrCodeUtil = new QrCodeUtil();
        try {
            System.out.println(qrCodeUtil.createQrCodeBASE64("2222"));
        } catch (IOException | WriterException e) {
            e.printStackTrace();
        }
    }
}
