import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * ClassName:MyTest
 * Package:PACKAGE_NAME
 * Description:
 *
 * @date:2020/4/7 11:19
 * @author:动力节点
 */
public class MyTest {

    public static void main(String[] args) throws DocumentException {

        String xmlString = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "\n" +
                "<bookstore>\n" +
                "\n" +
                "<book>\n" +
                "  <title lang=\"eng\">Harry Potter</title>\n" +
                "  <price>29.99</price>\n" +
                "</book>\n" +
                "\n" +
                "<book>\n" +
                "  <title lang=\"eng\">Learning XML</title>\n" +
                "  <price>39.95</price>\n" +
                "</book>\n" +
                "\n" +
                "</bookstore>";

        //将xml格式的字符串转换为Dom对象
        Document document = DocumentHelper.parseText(xmlString);

        //获取dom对象中指定的节点
        //编写xpath路径表达式:bookstore/book[1]/title
        Node node = document.selectSingleNode("/bookstore/book[1]/title");

        //获取节点的文本内容
        String text = node.getText();

        System.out.println(text);
    }
}
