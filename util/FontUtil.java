package util;
import java.awt.Font;
import java.io.File;

public class FontUtil {
    public static Font loadFontUtil(){
        try{
            File font_file = new File("C:\\Eshoping\\resources\\fonts\\Lato-Black.ttf");
            return  Font.createFont(Font.TRUETYPE_FONT, font_file);
        } catch (Exception e){
            e.printStackTrace();
            return new Font("SansSerif", Font.PLAIN, 12);
        }
    }
}
