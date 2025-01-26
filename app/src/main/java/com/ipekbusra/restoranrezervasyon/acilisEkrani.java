package com.ipekbusra.restoranrezervasyon;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class acilisEkrani extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acilis_ekrani);

        databaseHelper = new DatabaseHelper(this);

        // Veritabanı işlemleri arka planda yapılacak
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Veritabanı işlemleri yapılacak
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                // Yemek verilerinin eklenip eklenmediğini kontrol ediyoruz
                if (!databaseHelper.isYemekVerileriEklenmis(db)) {
                    // Eğer veriler eklenmemişse, CSV'den verileri ekliyoruz
                    databaseHelper.readCsvAndInsertToDatabase(db);
                }

                // 3 saniye beklemek için Handler kullanıyoruz
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Kullanıcı kayıtlı mı kontrol ediyoruz
                        if (databaseHelper.isKullaniciKayitli()) {
                            // Kullanıcı kayıtlıysa ana menüye yönlendir
                            Intent intent = new Intent(acilisEkrani.this, anaMenu.class);
                            startActivity(intent);
                        } else {
                            // Kullanıcı kayıtlı değilse kayıt ol ekranına yönlendir
                            Intent intent = new Intent(acilisEkrani.this, kayitOl.class);
                            startActivity(intent);
                        }
                        // Açılış ekranını kapatıyoruz
                        finish();
                    }
                }, 3000);  // 3 saniye bekleme
            }
        }).start();
    }
}


//3 saniye boyunca bu ekran gösterilir, daha sonra AnaMenu adlı aktiviteye geçiş yapılır.