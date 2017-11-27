package com.example.yaphet.phone_audio_radio1.domin;

/**
 * Created by WYF on 2017/11/17.
 */

public class RadioInfo {
    private String artist;
    private String adressAbso;
    private String displayName;
    private String radioSize;
    private String time;

    public RadioInfo(String artist, String adressAbso, String displayName, String radioSize, String time) {
        this.artist = artist;
        this.adressAbso = adressAbso;
        this.displayName = displayName;
        this.radioSize = radioSize;
        this.time = time;
    }

    public RadioInfo() {
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAdressAbso() {
        return adressAbso;
    }

    public void setAdressAbso(String adressAbso) {
        this.adressAbso = adressAbso;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRadioSize() {
        return radioSize;
    }

    public void setRadioSize(String radioSize) {
        this.radioSize = radioSize;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "RadioInfo{" +
                "artist='" + artist + '\'' +
                ", adressAbso='" + adressAbso + '\'' +
                ", displayName='" + displayName + '\'' +
                ", radioSize='" + radioSize + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
