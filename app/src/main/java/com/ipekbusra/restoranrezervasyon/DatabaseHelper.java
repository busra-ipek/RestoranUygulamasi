package com.ipekbusra.restoranrezervasyon;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RestoranDb";
    private static final int VERSION = 1;
    private Context context;

    private static final String Create_Table_Kullanici = "Create Table Kullanici("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, ad TEXT NOT NULL,"
            + "soyad TEXT NOT NULL, eposta TEXT NOT NULL, telefon TEXT NOT NULL, sifre TEXT NOT NULL);";

    private static final String Create_Table_Yemekler = "CREATE TABLE Yemekler ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, yemek_adi TEXT NOT NULL,"
            + "kategori TEXT NOT NULL, fiyat INTEGER NOT NULL, kalori INTEGER NOT NULL, resim TEXT NOT NULL,"
            + " aciklama TEXT NOT NULL);";

    private static final String Create_Table_Rezervasyon = "CREATE TABLE Rezervasyon ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, kullanici INTEGER NOT NULL,"
            + "kisi_sayisi INTEGER NOT NULL, tarih TEXT NOT NULL, saat TEXT NOT NULL,"
            + "FOREIGN KEY(kullanici) REFERENCES Kullanici(id));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_Table_Kullanici);     //Tablolar oluşturulur.
        db.execSQL(Create_Table_Yemekler);
        db.execSQL(Create_Table_Rezervasyon);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Kullanici");   //versiyon değişirse mevcut tablolar silinir ve yeniden oluşturulur.
        db.execSQL("DROP TABLE IF EXISTS Yemekler ");
        db.execSQL("DROP TABLE IF EXISTS Rezervasyon ");

        onCreate(db);
    }

    public boolean isKullaniciKayitli() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Kullanici LIMIT 1", null);
        boolean kayitliMi = cursor.getCount() > 0; // Eğer veri varsa kullanıcı veritabanında kayıtlıdır
        cursor.close();
        return kayitliMi;
    }

    public Cursor getKullanici(int kullaniciId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // ID'yi kullanarak filtreleme yapıyoruz
        return db.rawQuery("SELECT * FROM Kullanici WHERE id = ?", new String[]{String.valueOf(kullaniciId)});
    }

    public boolean updateKullanici(int kullaniciId, String ad, String soyad, String eposta, String telefon, String sifre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ad", ad);
        values.put("soyad", soyad);
        values.put("eposta", eposta);
        values.put("telefon", telefon);
        values.put("sifre", sifre);

        int rowsUpdated = db.update("Kullanici", values, "id = ?", new String[]{String.valueOf(kullaniciId)});
        Log.d("DatabaseHelper", "Rows Updated: " + rowsUpdated); // Güncelleme başarısını logla
        return rowsUpdated > 0; // Eğer güncelleme yapıldıysa true döner
    }

    public boolean deleteKullanici(int kullaniciId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            int rowsDeleted = db.delete("Kullanici", "id = ?", new String[]{String.valueOf(kullaniciId)});
            Log.d("DatabaseHelper", "Rows Deleted: " + rowsDeleted);
            return rowsDeleted > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Delete Error: " + e.getMessage());
            return false;
        }
    }

    //Buradaki metot ile rezervasyonlar listviewda listelenir.
    public ArrayList<rezervasyonBilgisi> getRezervasyonlar(int kullaniciId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<rezervasyonBilgisi> rezervasyonlar = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT tarih, saat, kisi_sayisi FROM Rezervasyon WHERE kullanici = ?", new String[]{String.valueOf(kullaniciId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String tarih = cursor.getString(cursor.getColumnIndex("tarih"));
                @SuppressLint("Range") String saat = cursor.getString(cursor.getColumnIndex("saat"));
                @SuppressLint("Range") int kisiSayisi = cursor.getInt(cursor.getColumnIndex("kisi_sayisi"));
                rezervasyonlar.add(new rezervasyonBilgisi(tarih, saat, kisiSayisi));
            }
            cursor.close();
        }
        db.close();
        return rezervasyonlar;

    }


    public void readCsvAndInsertToDatabase(SQLiteDatabase db) {
        // Verilerin zaten eklenip eklenmediğini kontrol ediyoruz
        if (isYemekVerileriEklenmis(db)) {
            Log.d("CSV", "Veriler zaten eklenmiş. Yeni veri eklenmeyecek.");
            return;  // Eğer veriler eklenmişse fonksiyondan çıkıyoruz
        }

        InputStream is = null;
        CSVReader reader = null;
        try {
            is = context.getAssets().open("yemekler.csv");
            reader = new CSVReader(new InputStreamReader(is));
            String[] line;

            // İlk satırı (başlıkları) atlıyoruz
            reader.readNext();  // Başlık satırını atlıyoruz

            while ((line = reader.readNext()) != null) {
                Log.d("CSV Line", "Okunan Satır: " + Arrays.toString(line));  // Her satırı logla

                if (line.length == 6) {  // 6 sütun olmalı
                    String yemekAdi = line[0].trim();
                    String kategori = line[1].trim();
                    int fiyat = Integer.parseInt(line[2].trim());
                    int kalori = Integer.parseInt(line[3].trim());
                    String resim = line[4].trim();
                    String aciklama = line[5].trim();

                    // Veritabanına ekleme
                    ContentValues satir = new ContentValues();
                    satir.put("yemek_adi", yemekAdi);
                    satir.put("kategori", kategori);
                    satir.put("fiyat", fiyat);
                    satir.put("kalori", kalori);
                    satir.put("resim", resim);
                    satir.put("aciklama", aciklama);

                    long result = db.insert("Yemekler", null, satir);
                    if (result == -1) {
                        Log.d("Insert Error", "Veri eklenirken hata oluştu.");
                    } else {
                        Log.d("Insert Success", "Veri başarıyla eklendi.");
                    }
                } else {
                    Log.d("CSV Error", "Geçersiz satır formatı: " + Arrays.toString(line));  // Hatalı satır logu
                }
            }

        } catch (IOException | CsvValidationException e) {
            Log.e("CSV Error", "Veriler eklenmedi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (is != null) {
                    is.close();
                }
                db.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isYemekVerileriEklenmis(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Yemekler", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;  // Eğer yemek verileri eklenmişse true döner
    }

}


