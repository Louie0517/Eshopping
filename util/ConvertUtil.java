package util;


import java.time.LocalDate;
import java.util.Date;
import java.io.File;
import javax.swing.JComboBox;
import com.toedter.calendar.JDateChooser;



///--------------------------------------------------------------|||  
///                                                              |||
///     Note: util must be stateless just helper methods,        |||
//            this util composed of convertion of used           |||
//            Java components to String                          |||
///                                                              |||
/// -------------------------------------------------------------|||


public class ConvertUtil {
    public String genderDropDownUtil(JComboBox genderComboBox){
        return (genderComboBox.getSelectedItem() == null) ? null : 
        genderComboBox.getSelectedItem().toString();
    }

    public String roleDropDownUtil(JComboBox roleComboBox){
        return (roleComboBox.getSelectedItem() == null) ? null : roleComboBox.getSelectedItem().toString();
    }

    public String selectedImagePathUtil(File image){
        return (image == null) ? null : image.toString();
    }

    public LocalDate dateChooserUtil(JDateChooser jDateChooser){
        if (jDateChooser == null || jDateChooser.getDate() == null) {
            return null;
        }
        Date date = jDateChooser.getDate();
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    

}
