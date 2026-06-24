
package com.nic.nerie.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Patterns {
    
    public static final String PATTERN_EMAIL = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String PATTERN_EMAIL_OPT = "^([_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))?$";
    
    public static final String PATTERN_NAME = "^[a-zA-z\\.\\s]*$"; 
    public static final String PATTERN_USERID = "^[a-zA-z@\\.\\s]*$"; 
    //public static final String PATTERN_NAME_SIZE = "^[a-zA-Z\\.\\s]{5,20}$"; 
    //public static final String PATTERN_NAME = "^[a-zA-Z\\. ]*$";    
            
    public static final String PATTERN_ALPHA_NUMERIC = "[a-zA-Z0-9]*";
    //public static final String PATTERN_ALLOW_ALL = "^[a-zA-Z0-9\\s\\d\\(\\)\\{\\}\\[\\]\\/\\-@#$%&!_=.'`~]*$";
    public static final String PATTERN_ALLOW_ALL = "^[a-zA-Z0-9\\s\\d\\(\\)\\{\\}\\[\\]\\/\\-@#$!_=.'`~]*$";
    
    public static final String PATTERN_YEAR = "^([1-9]){1}([0-9]){3}$";
    public static final String PATTERN_MONTH = "^(0[1-9]|1[0-2])$";
    public static final String PATTERN_DAY = "^(0[1-9]|[1-2][0-9]|3[0-1])$";
    public static final String PATTERN_DATE = "^([1-9]){1}([0-9]){3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$";
//    public static final String PATTERN_DATE = "^(0[1-9]|[1-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-([1-9]){1}([0-9]){3}$";
    //public static final String PATTERN_DATE = "^(3[01]|[12][0-9]|0[1-9])-(1[0-2]|0[1-9])-[0-9]{4}$";
    
    public static final String PATTERN_NUM = "^\\d+$";
    public static final String PATTERN_NUM_OPT = "^\\d*$";
    public static final String PATTERN_PERCENTAGE2D = "^\\d+(\\.\\d{1,2})?$";
    public static final String PATTERN_PERCENTAGE2D_OPT = "(^\\d+(\\.\\d{1,2}))?$";
    //public static final String PATTERN_PERCENTAGE2D = "^[0-9]+(\\.[0-9][0-9]?)?$";
    public static final String PATTERN_POSITIVEINTEGER = "^(?!^0)\\d{1,9}$";
    public static final String PATTERN_MONEY = "[1-9][0-9]{0,7}(\\.?([0-9]){2})*";
    public static final String PATTERN_MOBILE = "([1-9]){1}([0-9]){9}";
    public static final String PATTERN_MOBILE_OPT = "(([1-9]){1}([0-9]){9})?";
    public static final String PATTERN_LANDLINE_OPT = "^(([0-9]){10}[0-9]?[0-9]?[0-9]?)?$";
    public static final String PATTERN_LANDLINE = "([0-9]){10}[0-9]?[0-9]?[0-9]?";
    public static final String PATTERN_PINCODE = "([1-9]){1}([0-9]){5}$";
    
    public static final String PATTERN_NEWOLD = "^(N|O)$";
    public static final String PATTERN_ROLE = "^(A|U|H|C|S|P)$";
    public static final String PATTERN_GENDER = "^(M|F|X)$";
    public static final String PATTERN_GENDER_OPT = "^(M|F|X|-)$";
    public static final String PATTERN_MARITALSTATUS = "^(M|U)$";
    public static final String PATTERN_YESNO = "^(Y|N)$";
    public static final String PATTERN_SCSTOBCGEN = "^(SC|ST|OBC|GEN)$";
    public static final String PATTERN_DIVISION = "^(1st|2nd|3rd)$";
    public static final String PATTERN_TRUEFALSE = "^(true|false)$";    
    public static final String PATTERN_RELATION = "^(F|M)$";    
    
    public static final String PATTERN_IMAGE_CONTANT = "^(image/jpeg|image/jpg|image/pjpeg|image/png|image/x-png)$";
    public static final String PATTERN_DOCUMENT_CONTANT = "^(image/jpeg|image/jpg|image/pjpeg|image/png|image/x-png|application/pdf|application/force-download)$";
    public static final String PATTERN_PDF_CONTANT = "^(application/pdf)$";
    public static final String PATTERN_UUID = "\\w*-\\w*-\\w*-\\w*-\\w*";    
    
    
    public static boolean PatternMatche(String pattern, String inputStr) {
        return Pattern.matches(pattern, inputStr);
    }

    public static boolean PatternCompile(String pattern, String inputStr) {
        Pattern p = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(inputStr);
        return m.find();
    }    
    
    public static boolean PatternCompileMatche(String pattern, String inputStr) {
        boolean success = false;
        Pattern p = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(inputStr);
        if(m.find()==true && Pattern.matches(pattern, inputStr)==true){
            success= true;
        }
        return success;
    }  
    
    
    public static boolean CheckNewBirthDeathRegNo(String inputStr) {        
        String re1="(MEG)";
        String re2="(-)";
        String re3="(BC|DC)";
        String re4="(\\/)";
        String re5="((?:[a-z][a-z]+))";
        String re6="(\\/)";
        String re7="((?:(?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3})))(?![\\d])";
        String re8="(\\/)";
        String re9="(\\d+)";

        Pattern p = Pattern.compile(re1+re2+re3+re4+re5+re6+re7+re8+re9,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(inputStr);
        return m.find();
    }  
    public static boolean CheckDelayedBirthDeathRegNo(String inputStr) {        
        String re1="(MEG)";
        String re2="(-)";
        String re3="(BC|DC)";
        String re4="(\\/)";
        String re5="((?:[a-z][a-z]+))";
        String re6="(\\/)";
        String re7="((?:(?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3})))(?![\\d])";
        String re8="(\\/)";
        String re9="(D)";
        String re10="(\\/)";
        String re11="(\\d+)";

        Pattern p = Pattern.compile(re1+re2+re3+re4+re5+re6+re7+re8+re9+re10+re11,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(inputStr);
        return m.find();
    }            
    public static boolean Checknumber(String inputStr,int length){
        boolean success = false;
        if(inputStr!=null && inputStr.length()<=length && PatternCompile(PATTERN_NUM,inputStr)){           
            success = true;            
        }        
        return success;
    }     
    public static boolean CheckRegNo(String inputStr){
        boolean success = false;
        if(inputStr!=null && inputStr.length()<=30){
            if(CheckNewBirthDeathRegNo(inputStr) || CheckDelayedBirthDeathRegNo(inputStr) || Checknumber(inputStr,7)){
                success = true;
            }
        }           
        return success;
    }               
    public static boolean CheckUUID(String inputStr){
        boolean success = false;
        if(inputStr!=null && inputStr.length()<=50 && PatternCompile(PATTERN_UUID,inputStr)){           
            success = true;            
        }
        return success;
    }               
    
    public static boolean isCharExixts(String inputStr){
        inputStr=inputStr.toLowerCase();
        boolean exists = false;
        if(inputStr.contains("<") || inputStr.contains(">") || 
           inputStr.contains("%") || inputStr.contains("script") || 
           inputStr.contains("script") || inputStr.contains("iframe") || inputStr.contains("img")){           
            exists = true;            
        }
        return exists;
    }     
    
    public static boolean isSpcFound(String value) 
    {
        boolean returnvalue=false;
        if (value != null) 
        {                 
            value=value.trim();
            if(PatternCompile("<script>(.*?)</script>", value)){
                returnvalue= true;
            }else if(PatternCompile("@import", value)){
                returnvalue= true;
            }else if(PatternCompile("javascript:", value)){
                returnvalue= true;
            }else if(PatternCompile("vbscript:", value)){
                returnvalue= true;
            }else if(PatternCompile("iframe", value)){
                returnvalue= true;
            }else if(PatternCompile("img:", value)){
                returnvalue= true;
            }else if(PatternCompile("src:", value)){
                returnvalue= true;
            }else if(PatternCompile("<script>", value)){
                returnvalue= true;
            }else if(PatternCompile("</script>", value)){
                returnvalue= true;            
            }else if(PatternCompile("<script(.*?)>", value)){
                returnvalue= true;
            }else if(PatternCompile("eval\\((.*?)\\)", value)){
                returnvalue= true;
            }else if(PatternCompile("expression\\((.*?)\\)", value)){
                returnvalue= true;
            }else if(PatternCompile("onload(.*?)=", value)){
                returnvalue= true;
            }else if(PatternCompile("<", value)){
                returnvalue= true;
            }else if(PatternCompile(">", value)){
                returnvalue= true;
            }else if(PatternCompile("%", value)){
                returnvalue= true;
            }else if(PatternCompile("%", value)){
                returnvalue= true;
            }else if(PatternCompile("script", value)){
                returnvalue= true;
            }
        }
        return returnvalue;
    }        
    
    
    // public static void main(String arg[]){
    //     String txt="MEG-BC/XAS/2017/700";
        //String txt="MEG-BC/XAS/2017/D/700";
        //String txt="MEG-DC/XAS/2017/700000";
        //System.out.println(CheckRegNo(txt));
        
//        String uuid="1312dd4f-672a-43ea-ba0b-c052d6376296";
        //if(PatternCompile(PATTERN_UUID,uuid) && uuid.length()<50){
//        if(CheckUUID(uuid)){
//            System.out.println(true);
//        }else{
//            System.out.println(false);
//        }
        
//        if(Checknumber("1A",2)){
//            System.out.println(true);
//        }else{
//            System.out.println(false);
//        }        
        
        
//        txt="MEG-BC/SMB/2017/1\",\"$1593\":\"";
//        
//        if(CheckRegNo(txt)){
//            System.out.println(true);
//        }else{
//            System.out.println(false);
//        }        
//        
//        String pn="919599866667";
//        System.out.println("919599866667"+" pn : "+pn.substring(2,12));
        
        
         //System.out.println(PatternCompileMatche(Patterns.PATTERN_SPC, "MEG-BC/SMB/2017/1<script>"));

//        String value="'<script>alert(2316)</script>";
//        System.out.println("value BF : "+value);
//        Pattern scriptPattern = Pattern.compile("script", Pattern.CASE_INSENSITIVE);
//        value = scriptPattern.matcher(value).replaceAll("");
//        System.out.println("value AF : "+value);
        
        
//        System.out.println("isSpcFound : "+isSpcFound("<script>alert(616)</script>"));
    // } 
    
    
 
  
    
}
