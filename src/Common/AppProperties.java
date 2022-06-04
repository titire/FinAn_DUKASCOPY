package Common;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.json.Property;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ariccini
 */
public class AppProperties extends Properties{
    private static AppProperties AP;
    private String PropertyFile;
    // private Properties P ;
    
    public static AppProperties getAppProperties() throws IOException{
        if ( AP == null ){
            AP = new AppProperties("Config.properties");
        }
        
        return AP;
    }
    private AppProperties(String PropertyFile) throws FileNotFoundException, IOException{
        this.PropertyFile = PropertyFile;
        //P = new Properties();
        this.load(new FileReader(this.PropertyFile));
    }
 
    
    // public static String userName = "titire9EU"; //DEMO2nfpdb";
    public static String Password = "99u5ot51"; // nfpdb";
    public static String Account = "2078436"; //"2071220"; // "2035066";
    
    public static String StartDate = "2020/01/01 00:00:00";
    public static String StopDate = "2021/01/02 23:00:00";
    
    public static double initialDeposit = 200;
    public static double importoTrade = 0.001;   // in milioni di euro
}