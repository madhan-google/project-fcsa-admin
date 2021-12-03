package com.codekiller.fcsaadmin.Datas;

public class QueryData {
    String Pushkey, Name, Query, mailId;
    boolean isSeen;

    public QueryData() {
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getPushkey() {
        return Pushkey;
    }

    public void setPushkey(String pushkey) {
        Pushkey = pushkey;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getQuery() {
        return Query;
    }

    public void setQuery(String query) {
        Query = query;
    }
}
