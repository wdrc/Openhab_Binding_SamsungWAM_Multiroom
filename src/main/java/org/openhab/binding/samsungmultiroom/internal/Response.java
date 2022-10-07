package org.openhab.binding.samsungmultiroom.internal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Response")
public class Response {
    private String spkname;

    private String pwron;
    private String mute;

    private String volume;

    private String groupname;

    private String timestamp;
    private String cpname;
    private String playstatus;
    private String title;
    private String artist;
    private String album;
    private String tracklength;
    private String thumbnail;

    private String errCode;

    // GetPlayStatus
    private String function;
    private String submode;

    public String getSpkname() {
        return spkname;
    }

    public void setSpkname(String spkname) {
        this.spkname = spkname;
    }

    public String getMute() {
        return mute;
    }

    public void setMute(String mute) {
        this.mute = mute;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCpname() {
        return cpname;
    }

    public void setCpname(String cpname) {
        this.cpname = cpname;
    }

    public String getPlaystatus() {
        return playstatus;
    }

    public void setPlaystatus(String playstatus) {
        this.playstatus = playstatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTracklength() {
        return tracklength;
    }

    public void setTracklength(String tracklength) {
        this.tracklength = tracklength;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getSubmode() {
        return submode;
    }

    public void setSubmode(String submode) {
        this.submode = submode;
    }

    public String getPwron() {
        return pwron;
    }

    public void setPwron(String pwron) {
        this.pwron = pwron;
    }
}
