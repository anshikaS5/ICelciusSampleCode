package vmokshagroup.com.myapplication;

/**
 * Created by anshikas on 13-06-2016.
 */
public class SSIDDetailsModel {

    private  String SSID;
    private  String Channel;
    private  String Security;

    public SSIDDetailsModel() {

    }

    public String getChannel() {
        return Channel;
    }

    public void setChannel(String channel) {
        Channel = channel;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getSecurity() {
        return Security;
    }

    public void setSecurity(String security) {
        Security = security;
    }
}
