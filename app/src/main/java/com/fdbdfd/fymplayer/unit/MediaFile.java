package com.fdbdfd.fymplayer.unit;


import org.litepal.crud.DataSupport;


public class MediaFile extends DataSupport{

    private String path;


    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
