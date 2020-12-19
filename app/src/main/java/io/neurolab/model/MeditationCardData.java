package io.neurolab.model;

public class MeditationCardData {

    private int mIcon;
    private String mHead;
    private String mDesc;

    public MeditationCardData(int icon, String head, String desc) {
        mIcon = icon;
        mHead = head;
        mDesc = desc;
    }

    public int getIcon() {
        return mIcon;
    }

    public String getHead() {
        return mHead;
    }

    public String getDesc() {
        return mDesc;
    }
}


