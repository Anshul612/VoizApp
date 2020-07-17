package com.fromapps.voizapp;

public class VContacts
{
    public String name, status, pic;

    public VContacts(){}

    public VContacts(String name, String status, String pic) {
        this.name = name;
        this.status = status;
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
