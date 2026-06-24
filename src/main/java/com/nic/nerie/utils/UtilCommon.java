package com.nic.nerie.utils;

import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.Browser;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;

public class UtilCommon {
    // log file - logs/application.log
    private static final Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    public static HashMap<String, String> getClientDetails(HttpServletRequest request) {
        log.debug("Extracting client details from request...");

        // --- User Agent Parsing using UserAgentUtils library ---
        String userAgentString = request.getHeader("User-Agent");
        String osName = "Unknown";
        String browserName = "Unknown";

        if (userAgentString != null && !userAgentString.isEmpty()) {
            try {
                UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
                OperatingSystem os = userAgent.getOperatingSystem();
                Browser browser = userAgent.getBrowser();

                if (os != null) {
                    osName = os.getName();
                }
                if (browser != null) {
                    browserName = browser.getName();
                }
                log.debug("User-Agent parsed: OS='{}', Browser='{}'", osName, browserName);
            } catch (Exception e) {
                log.warn("Could not parse User-Agent string '{}': {}", userAgentString, e.getMessage());
                osName = "Unknown"; // Keep as Unknown on error
                browserName = "Unknown";
            }
        } else {
            log.debug("User-Agent header was null or empty.");
        }


        // IP Address Detection
        String ipAddress = getClientIpAddress(request);
        log.debug("Client IP address determined: '{}'", ipAddress);


        // Page URL
        String pageUrl = getFullRequestUrl(request);
        log.debug("Request URL determined: '{}'", pageUrl);


        // Populate and Return Map
        HashMap<String, String> map = new HashMap<>();
        map.put("os", osName);
        map.put("browser", browserName);
        map.put("ipaddress", ipAddress);
        map.put("pageurl", pageUrl);

        return map;
    }

    private static String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (isValidIpHeader(ipAddress)) {
            return ipAddress.split(",")[0].trim();
        }

        ipAddress = request.getHeader("Proxy-Client-IP");
        if (isValidIpHeader(ipAddress)) {
            return ipAddress;
        }

        ipAddress = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIpHeader(ipAddress)) {
            return ipAddress;
        }

        ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (isValidIpHeader(ipAddress)) {
            return ipAddress.split(",")[0].trim();
        }

        ipAddress = request.getHeader("HTTP_CLIENT_IP");
        if (isValidIpHeader(ipAddress)) {
            return ipAddress;
        }

        ipAddress = request.getHeader("HTTP_X_FORWARDED");
        if (isValidIpHeader(ipAddress)) {
            return ipAddress;
        }

        ipAddress = request.getRemoteAddr();

        return (ipAddress != null && !ipAddress.isEmpty()) ? ipAddress : "Unknown";
    }

    private static boolean isValidIpHeader(String ipHeader) {
        return ipHeader != null && !ipHeader.isEmpty() && !"unknown".equalsIgnoreCase(ipHeader);
    }

    private static String getFullRequestUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (requestURL == null) {
            return "Unknown";
        }

        String fullUrl = requestURL.toString();
        if (queryString != null) {
            fullUrl += "?" + queryString;
        }
        return fullUrl;
    }
}