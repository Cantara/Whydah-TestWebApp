package net.whydah.identity.spring;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loosely based upon code from Gunnar Skjold (Origin AS)
 *
 * @author Gunnar Skjold
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Component
public class WhydahService {
    private static final Logger log = LoggerFactory.getLogger(WhydahService.class);

    private RestTemplate restTemplate;

    @Value("${whydah.ssoBaseUrl:https://test.quadim.ai/sso/}")
    private String ssoUrl;

    @Value("${whydah.stsBaseUrl:https://test.quadim.ai/tokenservice/}")
    private String tokeservice;

    @Value("${whydah.applicationId:101}")
    private String applicationId;

    @Value("${whydah.applicationname:Quadim CV}")
    private String applicationname;

    @Value("${whydah.applicationSecret:55fhRM6nbKZ2wfC6RMmMuzXpk}")
    private String applicationSecret;

    @Autowired
    public WhydahService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected ApplicationToken applicationLogon() {
        String resourceUrl = "logon";
        String fullUrl = tokeservice + resourceUrl;
        Map<String, String> params = new HashMap<>();
        WhydahApplicationCredential applicationcredential = new WhydahApplicationCredential(applicationId, applicationSecret);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        String encodedCredential = encode(applicationcredential.toXml());
        formData.add("applicationcredential", encodedCredential);
        String body = "applicationcredential=" + encodedCredential;
        String logonResult = post(fullUrl, body, params);
        log.info("Application logon {} ", logonResult);
        ApplicationToken applicationToken = ApplicationTokenMapper.fromXml(logonResult);
        return applicationToken;
    }


    protected UserToken getUserToken(ApplicationToken applicationToken, String ticket) {
        String resourceUrl = "user/{applicationTokenId}/get_usertoken_by_userticket";
        String fullUrl = tokeservice + resourceUrl;

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("applicationTokenId", applicationToken.getApplicationTokenId());
        MultiValueMap<String, String> formData = new LinkedMultiValueMap();
        formData.add("apptoken", ApplicationTokenMapper.toXML(applicationToken));
        formData.add("userticket", ticket);
        String result = post(fullUrl, formData, urlParams);
        log.info("Returned token:" + result);
        UserToken userToken = UserTokenMapper.fromUserTokenXml(result);
        log.info(userToken.toString());
        return userToken;
    }


    protected String post(String fullUrl, String body, Map<String, String> parms) {
        log.trace("Post to url {}, body {}, params {}", fullUrl, body, parms.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        List<MediaType> acceptMediatype = new ArrayList<>();
        acceptMediatype.add(MediaType.APPLICATION_XML);
        headers.setAccept(acceptMediatype);
        HttpEntity<String> entity = new HttpEntity<String>(body, headers);
        log.debug("Accessing url: {}, params {}", fullUrl, parms.toString());
        ResponseEntity<String> response = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, String.class, parms);
        log.trace("Received from url {} body {} result is {}", fullUrl, body, response.toString());
        //FIXME ensure status codes are ok.
        return response.getBody();
    }


    protected String post(String fullUrl, MultiValueMap<String, String> body, Map<String, String> parms) {
        log.trace("Post to url {}, body {}, params {}", fullUrl, body, parms.toString());
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        List<MediaType> acceptMediatype = new ArrayList<>();
        acceptMediatype.add(MediaType.APPLICATION_XML);
        headers.setAccept(acceptMediatype);
//        HttpEntity<String> entity = new HttpEntity<String>(body, headers);
        log.debug("Accessing url: {}, params {}", fullUrl, parms.toString());
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(fullUrl, HttpMethod.POST, requestEntity, String.class, parms);
        log.trace("Received from url {} body {} result is {}", fullUrl, body, response.toString());
        //FIXME ensure status codes are ok.
        return response.getBody();
    }

    private String encode(String xml) {
        String encodedCredential = "";
        try {
            encodedCredential = URLEncoder.encode(xml, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.info("Could not encode the xml to UTF-8: " + xml);
            throw new RuntimeException("Could not encode the xml to UTF-8: " + xml);
        }
        return encodedCredential;
    }

    public String getSsoUrl() {
        return ssoUrl;
    }
}
