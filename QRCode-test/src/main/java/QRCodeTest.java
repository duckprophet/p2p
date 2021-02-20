import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:QRCodeTest
 * Package:PACKAGE_NAME
 * Description:
 *
 * @date:2020/4/13 11:11
 * @author:动力节点
 */
public class QRCodeTest {

    public static void main(String[] args) throws WriterException, IOException {

        Map<EncodeHintType,Object> map = new HashMap<EncodeHintType, Object>();
        map.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        String content = "weixin://wxpay/bizpayurl?pr=3JNICq6";

        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE,200,200,map);

        String filePath = "D://";
        String fileName = "QRCodeTest.jpg";

        Path path = FileSystems.getDefault().getPath(filePath,fileName);

        //将矩阵对象转换为二维码
        MatrixToImageWriter.writeToPath(bitMatrix,"jpg",path);


    }
}
