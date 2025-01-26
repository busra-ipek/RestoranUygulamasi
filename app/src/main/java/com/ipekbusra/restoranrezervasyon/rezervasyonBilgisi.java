package com.ipekbusra.restoranrezervasyon;

public class rezervasyonBilgisi {
    public String tarih;
    public String saat;
    public int kisi_sayisi;

    public rezervasyonBilgisi(String tarih, String saat, int kisi_sayisi) {
        this.tarih = tarih;
        this.saat = saat;
        this.kisi_sayisi = kisi_sayisi;
    }

    public String getTarih(){
        return tarih;
    }

    public String getSaat() {
        return saat;
    }

    public int getKisi_sayisi(){
        return kisi_sayisi;
    }

    @Override
    public String toString() {
        return tarih + "\t\t" + saat + "\n" + kisi_sayisi + " Kişi";
    }
}
