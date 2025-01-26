package com.ipekbusra.restoranrezervasyon;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class profilDuzenle extends AppCompatActivity {

    private EditText editText_ad_duzenle, editText_soyad_duzenle, editText_eposta_duzenle, editText_telefon_duzenle, editText_sifre_duzenle;
    private DatabaseHelper databaseHelper;
    private ImageButton imageButton_geri_profil;
    private Button button_kaydet;
    private CardView cardView_hesap_sil;
    private int kullaniciId;
    private String ad, soyad, eposta, sifre, telefon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_duzenle);

        editText_ad_duzenle = findViewById(R.id.editText_ad_giris);
        editText_soyad_duzenle  = findViewById(R.id.editText_soyad_giris);
        editText_eposta_duzenle = findViewById(R.id.editText_eposta_giris);
        editText_telefon_duzenle = findViewById(R.id.editText_telefon_giris);
        editText_sifre_duzenle = findViewById(R.id.editText_sifre_giris);
        imageButton_geri_profil = findViewById(R.id.imageButton_back_profil);
        button_kaydet =  findViewById(R.id.button_kaydet);
        cardView_hesap_sil =  findViewById(R.id.cardView_hesap_sil);

        View.OnClickListener geriProfil = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profilDuzenle.this, profil.class);
                startActivity(intent);
            }
        };
        imageButton_geri_profil.setOnClickListener(geriProfil);

        databaseHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        kullaniciId = (int) sharedPreferences.getLong("kullaniciId", -1); // Kullanıcı ID'sini alıyoruz
        Log.d("profilDuzenle", "Profil Duzenle alınan Kullanıcı ID: " + kullaniciId);

        kullaniciVerileri();

        View.OnClickListener kaydetProfil = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kullaniciVerileriGuncelle();
            }
        };
        button_kaydet.setOnClickListener(kaydetProfil);

        View.OnClickListener hesapSil = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kullaniciVerileriSil();
            }
        };
        cardView_hesap_sil.setOnClickListener(hesapSil);
    }

    @SuppressLint("Range")
    private void kullaniciVerileri() {
        // Veritabanından kullanıcı verilerini alıyoruz
        Cursor cursor = databaseHelper.getKullanici(kullaniciId);
        if (cursor != null && cursor.moveToFirst()) {
            // Verileri EditText'lere yerleştiriyoruz
            editText_ad_duzenle.setText(cursor.getString(cursor.getColumnIndex("ad")));
            editText_soyad_duzenle.setText(cursor.getString(cursor.getColumnIndex("soyad")));
            editText_eposta_duzenle.setText(cursor.getString(cursor.getColumnIndex("eposta")));
            editText_telefon_duzenle.setText(cursor.getString(cursor.getColumnIndex("telefon")));
            editText_sifre_duzenle.setText(cursor.getString(cursor.getColumnIndex("sifre")));
            cursor.close();
        }else {
            Log.d("profilDuzenle", "Kullanıcı verisi bulunamadı.");
        }
    }

    private void kullaniciVerileriGuncelle() {
        ad = editText_ad_duzenle.getText().toString();
        soyad = editText_soyad_duzenle.getText().toString();
        eposta = editText_eposta_duzenle.getText().toString();
        telefon = editText_telefon_duzenle.getText().toString();
        sifre = editText_sifre_duzenle.getText().toString();

        if (!TextUtils.isEmpty(ad) && !TextUtils.isEmpty(soyad) && !TextUtils.isEmpty(eposta) && !TextUtils.isEmpty(telefon) && !TextUtils.isEmpty(sifre)) {
            // Veritabanına güncellenmiş bilgileri kaydediyoruz
            boolean isUpdated = databaseHelper.updateKullanici(kullaniciId, ad, soyad, eposta, telefon, sifre); ////updateKullannici adlı metot kullanılır.
            if (isUpdated) {
                Toast.makeText(profilDuzenle.this, "Bilgileriniz güncellenmiştir.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(profilDuzenle.this, profil.class);
                startActivity(intent);
            } else {
                Toast.makeText(profilDuzenle.this, "Hata oluştu. Lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(profilDuzenle.this, "Lütfen tüm alanları doldurunuz..", Toast.LENGTH_SHORT).show();
        }
    }

    private void kullaniciVerileriSil(){
                // Hesap silme işlemi için onay kutusu gösteriyoruz
         new AlertDialog.Builder(profilDuzenle.this)
                 .setTitle("Hesabınızı silmek istediğinize emin misiniz?")
                 .setMessage("Hesabınızı sildiğinizde tüm verileriniz kaybolacak!")
                 .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         // Hesap silme işlemi veritabanı üzerinden yapılacak
                         boolean isDeleted = databaseHelper.deleteKullanici(kullaniciId); //deleteKullannici adlı metot kullanılır.
                         if (isDeleted) {
                             Toast.makeText(profilDuzenle.this, "Hesabınız başarıyla silinmiştir.", Toast.LENGTH_SHORT).show();
                             Intent intent = new Intent(profilDuzenle.this, acilisEkrani.class);
                             startActivity(intent); // Uygulamadan çıkmak için açılış ekranına yönlendiriyoruz
                         } else {
                             Toast.makeText(profilDuzenle.this, "Hesap silinirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                 .setNegativeButton("Hayır", null)
                 .show();
    }

}