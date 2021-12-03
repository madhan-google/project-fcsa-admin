package com.codekiller.fcsaadmin.Datas;

public class EBookData {
    String Pushkey, Filename, DownloadUrl;

    public EBookData() {
    }

    public String getPushkey() {
        return Pushkey;
    }

    public void setPushkey(String pushkey) {
        Pushkey = pushkey;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String filename) {
        Filename = filename;
    }

    public String getDownloadUrl() {
        return DownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        DownloadUrl = downloadUrl;
    }
}
