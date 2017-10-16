package com.ddz.mearchant.models;

/**
 * Created by Administrator on 2017/6/26 0026.
 */

public class EarnBase {
    private EarnAccount account;
    private EarnOnline online;
    private EarnDownLine line;

    public EarnAccount getAccount() {
        return account;
    }

    public void setAccount(EarnAccount account) {
        this.account = account;
    }

    public EarnOnline getOnline() {
        return online;
    }

    public void setOnline(EarnOnline online) {
        this.online = online;
    }

    public EarnDownLine getLine() {
        return line;
    }

    public void setLine(EarnDownLine line) {
        this.line = line;
    }
}
