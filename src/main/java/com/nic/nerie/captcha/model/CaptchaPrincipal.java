package com.nic.nerie.captcha.model;

import jakarta.persistence.Transient;
import org.springframework.stereotype.Component;

@Component
public class CaptchaPrincipal {
	@Transient
	private String captcha;

	@Transient
	private String hiddentCaptcha;

	@Transient
	private String realCaptcha;

	public CaptchaPrincipal() {

	}

	public CaptchaPrincipal(String captcha, String hiddentCaptcha, String realCaptcha) {
		super();
		this.captcha = captcha;
		this.hiddentCaptcha = hiddentCaptcha;
		this.realCaptcha = realCaptcha;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getHiddentCaptcha() {
		return hiddentCaptcha;
	}

	public void setHiddentCaptcha(String hiddentCaptcha) {
		this.hiddentCaptcha = hiddentCaptcha;
	}

	public String getRealCaptcha() {
		return realCaptcha;
	}

	public void setRealCaptcha(String realCaptcha) {
		this.realCaptcha = realCaptcha;
	}

}
