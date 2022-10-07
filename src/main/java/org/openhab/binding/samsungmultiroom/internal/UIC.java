package org.openhab.binding.samsungmultiroom.internal;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("UIC")
public class UIC {

    private String method;
    private String version;
    private String speakerip;
    private String user_identifier;
    @XStreamImplicit
    private List<Response> response;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSpeakerip() {
        return speakerip;
    }

    public void setSpeakerip(String speakerip) {
        this.speakerip = speakerip;
    }

    public String getUser_identifier() {
        return user_identifier;
    }

    public void setUser_identifier(String user_identifier) {
        this.user_identifier = user_identifier;
    }

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }

    public Response getFirstResponse() {
        if (this.response.isEmpty()) {
            return null;
        } else {
            return this.response.get(0);
        }
    }
}
