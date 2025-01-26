package com.ipekbusra.restoranrezervasyon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;

public class yemekDetay extends AppCompatActivity {

    private String yemekAd, yemekResim;
    private DatabaseHelper databaseHelper;
    private TextView textView_yemekAciklama, textView_yemekAdi;
    private ImageButton imageButton_geri_yemekCesit;
    private ImageView imageView_yemekResim;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yemek_detay);

        databaseHelper = new DatabaseHelper(this);
        imageButton_geri_yemekCesit = findViewById(R.id.imageButton_back_yemekCesit);
        imageView_yemekResim = findViewById(R.id.imageView_yemek_resim);
        textView_yemekAdi = findViewById(R.id.textView_yemek_adi);
        textView_yemekAciklama = findViewById(R.id.textView_yemek_aciklama);
        bottomNavigationView = findViewById(R.id.bottom_navigation_yemek_detay);

        yemekAd = getIntent().getStringExtra("yemekAdi");
        yemekResim = getIntent().getStringExtra("resimYolu");

        textView_yemekAdi.setText(yemekAd); ////Her yemeğin ismi başlıkta görünecektir.
        setResimFromAssets(yemekResim, imageView_yemekResim);

        yemekAciklama(yemekAd);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if(itemId == R.id.menu_anasayfa){

                    Intent intent = new Intent(this, anaMenu.class );
                    startActivity(intent);
                    finish();

            }
            else if (itemId == R.id.menu_rezervasyon) {

                    Intent intent = new Intent(yemekDetay.this, rezervasyon.class );
                    startActivity(intent);
                    finish();    //mevcut aktiviteyi kapatacak.

            }
            else if (itemId == R.id.menu_profil) {

                    Intent intent = new Intent(yemekDetay.this, profil.class );
                    startActivity(intent);
                    finish();    //mevcut aktiviteyi kapatacak.

            }
            return true;
        });

        View.OnClickListener geriYemekCesitleri = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //bu aktivite kapanıcak, açık olan yemekcesit adlı aktiviteye geçilecek.
            }
        };
        imageButton_geri_yemekCesit.setOnClickListener(geriYemekCesitleri);

    }

    // Resimleri assets klasöründen almak
    public void setResimFromAssets(String resimYolu, ImageView imageView) {
        try {
            InputStream is = getAssets().open(resimYolu);  // assets klasöründen resim dosyasını alıyoruz
            Drawable yemekResim = Drawable.createFromStream(is, null);  // InputStream ile resim dosyasını Drawable'a çeviriyoruz
            imageView.setImageDrawable(yemekResim);  // Drawable'ı ImageView'a set ediyoruz
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void yemekAciklama(String yemekAd){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT aciklama FROM Yemekler WHERE yemek_adi = ?", new String[]{yemekAd});

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String aciklama = cursor.getString(cursor.getColumnIndex("aciklama"));
            textView_yemekAciklama.setText(aciklama);

        }
        cursor.close();
    }
}
