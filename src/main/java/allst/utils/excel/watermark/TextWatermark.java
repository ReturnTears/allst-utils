package allst.utils.excel.watermark;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import allst.utils.excel.util.FontImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * @author Hutu
 * @since 2023-10-23 上午 10:23
 */
public class TextWatermark {
    public static void main(String[] args) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            FileOutputStream out = new FileOutputStream("C:\\Users\\June\\Desktop\\aa.xlsx");

            XSSFSheet sheet = workbook.createSheet("Sheet1");
            workbook.getSheet("Sheet1");

            //add picture data to this workbook.
//                FileInputStream is = new FileInputStream("/Users/Tony/Downloads/data_image.png");
//            byte[] bytes = IOUtils.toByteArray(is);



            BufferedImage image = TextWatermark.drawText("重庆农村商业银行", new Font("microsoft-yahei", Font.PLAIN, 20), Color.BLUE, Color.WHITE, 300, 300);
            // 导出到字节流B
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);

            int pictureIdx = workbook.addPicture(os.toByteArray(), Workbook.PICTURE_TYPE_PNG);
//            is.close();

            //add relation from sheet to the picture data
            String rID = sheet.addRelation(null, XSSFRelation.IMAGES, workbook.getAllPictures().get(pictureIdx)).getRelationship().getId();
            //set background picture to sheet
            sheet.getCTWorksheet().addNewPicture().setId(rID);

            workbook.write(out);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static BufferedImage drawText(String text, Font font, Color textColor, Color backColor, double height, double width) {
        //定义图片宽度和高度
        BufferedImage img = new BufferedImage((int) width, (int) height, TYPE_INT_ARGB);

        Graphics2D loGraphic = img.createGraphics();

        //获取文本size
        FontMetrics loFontMetrics = loGraphic.getFontMetrics(font);
        int liStrWidth = loFontMetrics.stringWidth(text);
        int liStrHeight = loFontMetrics.getHeight();

        //文本显示样式及位置
        loGraphic.setColor(backColor);
        loGraphic.fillRect(0, 0, (int) width, (int) height);
        loGraphic.translate(((int) width - liStrWidth) / 2, ((int) height - liStrHeight) / 2);
        loGraphic.rotate(Math.toRadians(-45));

        loGraphic.translate(-((int) width - liStrWidth) / 4, -((int) height - liStrHeight) / 4);
        loGraphic.setFont(font);
        loGraphic.setColor(textColor);
        loGraphic.drawString(text, ((int) width - liStrWidth) / 4, ((int) height - liStrHeight) / 2);
        loGraphic.drawString(text, ((int) width - liStrWidth) / 2, ((int) height - liStrHeight) / 4);
        loGraphic.drawString(text, ((int) width - liStrWidth) / 6, ((int) height - liStrHeight) / 20);
        loGraphic.dispose();
        return img;
    }
}
