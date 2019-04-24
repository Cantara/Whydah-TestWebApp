package net.whydah.identity.spring;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Loosely based upon code from Gunnar Skjold (Origin AS)
 *
 * @author Gunnar Skjold
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@XmlRootElement(name = "applicationcredential")
public class WhydahApplicationCredential {

    private WhydahApplicationcredentialParams params;
    private String applicationId;
    private String applicationSecret;

    public WhydahApplicationCredential(String applicationID, String applicationSecret) {
        this.params = new WhydahApplicationcredentialParams();
        this.params.setApplicationID(applicationID);
        this.params.setApplicationSecret(applicationSecret);
    }

    protected WhydahApplicationCredential() {
    }

    @XmlElement(name = "params")
    protected WhydahApplicationcredentialParams getParams() {
        return params;
    }

    protected void setParams(WhydahApplicationcredentialParams params) {
        this.params = params;
    }

    public String toXml() {
        return "<applicationcredential>\n" +
                "      <params>\n" +
                "         <applicationID>" + getApplicationId() + "</applicationID>\n" +
                "         <applicationSecret>" + getApplicationSecret() + "</applicationSecret>\n" +
                "      </params>\n" +
                "   </applicationcredential>";
    }

    public String getApplicationId() {
        String applicationId = "not-set";
        if (params != null) {
            applicationId = params.getApplicationID();
        }
        return applicationId;
    }

    public String getApplicationSecret() {
        String applicationSecret = "not-set";
        if (params != null) {
            applicationSecret = params.getApplicationSecret();
        }
        return applicationSecret;
    }

    private static class WhydahApplicationcredentialParams {
        private String applicationID, applicationSecret;

        public String getApplicationID() {
            return applicationID;
        }

        public void setApplicationID(String applicationID) {
            this.applicationID = applicationID;
        }

        public String getApplicationSecret() {
            return applicationSecret;
        }

        public void setApplicationSecret(String applicationSecret) {
            this.applicationSecret = applicationSecret;
        }
    }
}
