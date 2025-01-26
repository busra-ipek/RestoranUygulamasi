package com.ipekbusra.restoranrezervasyon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class profil extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    String ad, soyad, eposta,telefon;
    TextView textView_ad_veri, textView_soyad_veri, textView_eposta_veri, textView_telefon_veri;
    Button button_profili_duzenle;
    BottomNavigationView bottomNavigationView;
    int kullaniciId;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        textView_ad_veri = findViewById(R.id.textView_ad_veri);
        textView_soyad_veri = findViewById(R.id.textView_soyad_veri);
        textView_eposta_veri = findViewById(R.id.textView_eposta_veri);
        textView_telefon_veri = findViewById(R.id.textView_telefon_veri);
        button_profili_duzenle = findViewById(R.id.button_profili_duzenle);
        bottomNavigationView = findViewById(R.id.bottom_navigation_profil);

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        kullaniciId = (int) sharedPreferences.getLong("kullaniciId", -1); // Kullanıcı ID'sini alıyoruz
        Log.d("profil", "Profil alınan Kullanıcı ID: " + kullaniciId);

        databaseHelper = new DatabaseHelper(this);

        // Kullanıcı bilgilerini alınır
        Cursor cursor = databaseHelper.getKullanici(kullaniciId);

        if (cursor != null && cursor.moveToFirst()) {
            // Veritabanından alınan verileri TextView'lere atıyoruz
            kullaniciId = cursor.getInt(cursor.getColumnIndex("id"));
            ad = cursor.getString(cursor.getColumnIndex("ad"));
            soyad = cursor.getString(cursor.getColumnIndex("soyad"));
            eposta = cursor.getString(cursor.getColumnIndex("eposta"));
            telefon = cursor.getString(cursor.getColumnIndex("telefon"));

            // TextView'lere atama yapıyoruz
            textView_ad_veri.setText(ad);
            textView_soyad_veri.setText(soyad);
            textView_eposta_veri.setText(eposta);
            textView_telefon_veri.setText(telefon);

            cursor.close(); // Cursor kapatılıyor
        }

        View.OnClickListener profilDuzenle = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profil.this, profilDuzenle.class);
                startActivity(intent);
            }
        };
        button_profili_duzenle.setOnClickListener(profilDuzenle);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if(itemId == R.id.menu_anasayfa){
                    Intent intent = new Intent(this, anaMenu.class );
                    startActivity(intent);
                    finish();
            }
            else if (itemId == R.id.menu_rezervasyon) {
                    Intent intent = new Intent(profil.this, rezervasyon.class );
                    startActivity(intent);
                    finish();    //mevcut aktiviteyi kapatacak.
            }
            return true;
        });

    }
}