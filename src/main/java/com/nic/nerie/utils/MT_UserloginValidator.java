package com.nic.nerie.utils;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import static com.nic.nerie.utils.Patterns.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MT_UserloginValidator implements Validator {
    @Autowired
    MessageSource messages;

    @Override
    public boolean supports(Class<?> type) {
        return MT_Userlogin.class.isAssignableFrom(type);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String validate_invalid = messages.getMessage("validate.invalid", null, "invalid", null);

        MT_Userlogin tuserlogin = null;
        if (target instanceof MT_Userlogin) {
            tuserlogin = (MT_Userlogin) target;
        }
        String data = "";
        boolean errorFlag = false;

        data = (tuserlogin.getUsername() == null) ? tuserlogin.getUsername() : tuserlogin.getUsername().trim();
        if (data == null || data.length() > 100 || isSpcFound(data) || !Patterns.PatternCompileMatche(Patterns.PATTERN_NAME, data)) {
            errors.rejectValue("username", "", validate_invalid);
        }

        data = (tuserlogin.getUserdescription() == null) ? tuserlogin.getUserdescription() : tuserlogin.getUserdescription().trim();
        if (data != null && data.length() > 0) {
            if (data.length() > 300 || isSpcFound(data)) {
                errors.rejectValue("userdescription", "", validate_invalid);
            }
        }

        data = (tuserlogin.getUserid() == null) ? tuserlogin.getUserid() : tuserlogin.getUserid().trim();
        //if (data==null || data.length()>50 || isSpcFound(data) || !Patterns.PatternCompileMatche(Patterns.PATTERN_NAME,data))
        if (data == null || data.length() > 50 || isSpcFound(data)) {
            errors.rejectValue("userid", "", validate_invalid);
        }

        data = (tuserlogin.getUserpassword() == null) ? tuserlogin.getUserpassword() : tuserlogin.getUserpassword().trim();
        if (data != null && data.length() > 0) {
            if (data.length() > 512 || isSpcFound(data)) {
                errors.rejectValue("password", "", validate_invalid);
            }
        }

        data = (tuserlogin.getMoffices().getOfficecode()== null) ? tuserlogin.getMoffices().getOfficecode() : tuserlogin.getMoffices().getOfficecode().trim();
        if (data == null || data.length() > 4 || isSpcFound(data) || !Patterns.PatternCompileMatche(Patterns.PATTERN_NUM, data)) {
            errors.rejectValue("username", "", validate_invalid);
        }

        data = (tuserlogin.getUsermobile() == null) ? tuserlogin.getUsermobile() : tuserlogin.getUsermobile().trim();
        if (data == null || data.length() > 10 || isSpcFound(data) || !Patterns.PatternCompileMatche(Patterns.PATTERN_MOBILE, data)) {
            errors.rejectValue("username", "", validate_invalid);
        }
    }
}
