package com.wiktyl.model;

public class Data {
    public long getKoszt() {
        return koszt;
    }

    public void setKoszt(long koszt) {
        this.koszt = koszt;
    }

    public String getCzas_trwania() {
        return czas_trwania;
    }

    public void setCzas_trwania(String czas_trwania) {
        this.czas_trwania = czas_trwania;
    }

    public short getTrudnosc() {
        return trudnosc_projektu;
    }

    public void setTrudnosc(short trudnosc) {
        this.trudnosc_projektu = trudnosc;
    }

    public void setZysk(long zysk) {this.zysk = zysk;}

    public long getZysk() {return zysk;}

    private long koszt;
    private String czas_trwania;
    private short trudnosc_projektu;
    private long zysk;
}
