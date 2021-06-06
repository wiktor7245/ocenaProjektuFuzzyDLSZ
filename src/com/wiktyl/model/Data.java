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
        return trudnosc;
    }

    public void setTrudnosc(short trudnosc) {
        this.trudnosc = trudnosc;
    }

    private long koszt;
    private String czas_trwania;
    private short trudnosc;
}
