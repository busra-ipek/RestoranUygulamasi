package com.ipekbusra.restoranrezervasyon;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class kayitOl extends AppCompatActivity {

    private String ad, soyad, eposta, telefon, sifre;
    private  Button button_kayit_ol;
    private EditText editText_ad_gir, editText_soyad_gir, editText_eposta_gir, editText_telefon_gir, editText_sifre_gir;
    private DatabaseHelper databaseHelper;
    private int kullaniciId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);

        button_kayit_ol =  findViewById(R.id.button_kayit_ol);
        editText_ad_gir =  findViewById(R.id.adEditText);
        editText_soyad_gir = findViewById(R.id.soyadEditText);
        editText_eposta_gir =  findViewById(R.id.epostaEditText);
        editText_telefon_gir =  findViewById(R.id.telefonEditText);
        editText_sifre_gir =  findViewById(R.id.sifreEditText);
        databaseHelper = new DatabaseHelper(kayitOl.this);

        button_kayit_ol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad = editText_ad_gir.getText().toString();
                soyad = editText_soyad_gir.getText().toString();
                eposta = editText_eposta_gir.getText().toString();
                telefon = editText_telefon_gir.getText().toString();
                sifre = editText_sifre_gir.getText().toString();

                SQLiteDatabase db = databaseHelper.getWritableDatabase(); // bu bağlantı ile veriler veritabanına yazılacak.

                ContentValues satir = new ContentValues();
                satir.put("ad", ad); //sütunismi, veri
                satir.put("soyad",soyad);
                satir.put("eposta",eposta);
                satir.put("telefon",telefon);
                satir.put("sifre", sifre);

                if(!TextUtils.isEmpty(ad) && !TextUtils.isEmpty(soyad) && !TextUtils.isEmpty(eposta) && !TextUtils.isEmpty(telefon) && !TextUtils.isEmpty(sifre)) {
                    long sonuc = db.insert("Kullanici", null,satir); //kullanıcı adlı tabloya veriler eklenir.
                    //veri eklenmişse satırın id değeri döndürülür, eklenmemişse -1 olur.
                    if(sonuc != -1){
                        kullaniciId = (int) sonuc;
                        // Kullanıcı ID'sini SharedPreferences'a kaydederiz.
                        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("kullaniciId", kullaniciId); // Kullanıcı ID'sini kaydediyoruz
                        editor.apply();

                        Toast.makeText(kayitOl.this,"Üyelik işleminiz başarıyla gerçekleşmiştir.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(kayitOl.this, anaMenu.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(kayitOl.this,"Üyeliğiniz oluşturulamadı. Tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                    }

                    db.close(); //veritabanı bağlantısı kapatılır.
                }
                else
                    Toast.makeText(kayitOl.this,"Lütfen istenen verileri giriniz.", Toast.LENGTH_SHORT).show();

            }
        });

    }
}