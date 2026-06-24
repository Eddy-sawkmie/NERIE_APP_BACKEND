package com.nic.nerie.captcha.service;

import cn.apiclub.captcha.Captcha;
import com.nic.nerie.captcha.model.CaptchaPrincipal;
import com.nic.nerie.utils.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {
    private CaptchaPrincipal captchaPrincipal;

    @Autowired
    public CaptchaService(CaptchaPrincipal captchaPrincipal) {
        this.captchaPrincipal = captchaPrincipal;
        initCaptcha(captchaPrincipal);
    }

    public CaptchaPrincipal getCaptchaPrincipal() {
        return captchaPrincipal;
    }

    public void resetCaptchaPrincipal() {
        initCaptcha(captchaPrincipal);
    }

    public CaptchaPrincipal generateNewCaptcha() {
        Captcha captcha = CaptchaUtil.createCaptcha(240, 70);
        CaptchaPrincipal captchaPrincipal = new CaptchaPrincipal();
        captchaPrincipal.setHiddentCaptcha(captcha.getAnswer());
        captchaPrincipal.setCaptcha("");
        captchaPrincipal.setRealCaptcha(CaptchaUtil.encodeCaptcha(captcha));
        return captchaPrincipal;
    }

    private void initCaptcha(CaptchaPrincipal captchaPrincipal) {
        Captcha captcha = CaptchaUtil.createCaptcha(240, 70);
        captchaPrincipal.setHiddentCaptcha(captcha.getAnswer());
        captchaPrincipal.setCaptcha("");// value entered by User
        captchaPrincipal.setRealCaptcha(CaptchaUtil.encodeCaptcha(captcha));
    }
}
