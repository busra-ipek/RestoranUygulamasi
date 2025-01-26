package com.ipekbusra.restoranrezervasyon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;

public class yemekCesitleri extends AppCompatActivity {
    private String yemekKategorisi;
    private TextView textView_yemekKategorisi;
    private TextView[] textViewYemekAd, textViewFiyat,textViewKalori;
    private ImageView[] imageView;
    private ImageButton imageButton_ana_menu_don;
    private CardView[] cardView;
    private BottomNavigationView bottomNavigationView;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yemek_cesitleri);

        dbHelper = new DatabaseHelper(this);
        textView_yemekKategorisi = findViewById(R.id.textView_yemekCesitIsmi);
        imageButton_ana_menu_don = findViewById(R.id.imageButton_back_ana_menu);
        bottomNavigationView = findViewById(R.id.bottom_navigation_yemek_cesitleri);

        textViewYemekAd = new TextView[] { findViewById(R.id.textView_yemek_isim1), findViewById(R.id.textView_yemek_isim2),
                findViewById(R.id.textView_yemek_isim3), findViewById(R.id.textView_yemek_isim4),
                findViewById(R.id.textView_yemek_isim5), findViewById(R.id.textView_yemek_isim6),
                findViewById(R.id.textView_yemek_isim7), findViewById(R.id.textView_yemek_isim8) };

       cardView = new CardView[]{ findViewById(R.id.card_yemek1), findViewById(R.id.card_yemek2),
                findViewById(R.id.card_yemek3), findViewById(R.id.card_yemek4),
                findViewById(R.id.card_yemek5), findViewById(R.id.card_yemek6),
                findViewById(R.id.card_yemek7), findViewById(R.id.card_yemek8) };

        textViewFiyat = new TextView[]{ findViewById(R.id.textView_fiyat1), findViewById(R.id.textView_fiyat2),
                findViewById(R.id.textView_fiyat3), findViewById(R.id.textView_fiyat4),
                findViewById(R.id.textView_fiyat5), findViewById(R.id.textView_fiyat6),
                findViewById(R.id.textView_fiyat7), findViewById(R.id.textView_fiyat8) };

        textViewKalori = new TextView[]{ findViewById(R.id.textView_kalori1), findViewById(R.id.textView_kalori2),
                findViewById(R.id.textView_kalori3), findViewById(R.id.textView_kalori4),
                findViewById(R.id.textView_kalori5), findViewById(R.id.textView_kalori6),
                findViewById(R.id.textView_kalori7), findViewById(R.id.textView_kalori8) };

        imageView = new ImageView[]{ findViewById(R.id.imageView1), findViewById(R.id.imageView2),
                findViewById(R.id.imageView3), findViewById(R.id.imageView4),
                findViewById(R.id.imageView5), findViewById(R.id.imageView6),
                findViewById(R.id.imageView7), findViewById(R.id.imageView8) };

        bottomMenu();

        View.OnClickListener geriAnaMenu = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(yemekCesitleri.this, anaMenu.class);
                startActivity(intent);
            }
        };
        imageButton_ana_menu_don.setOnClickListener(geriAnaMenu);

        yemekKategorisi = getIntent().getStringExtra("yemekKategori");
        textView_yemekKategorisi.setText(yemekKategorisi);

        yemekleriGetirVeGoster(yemekKategorisi);

    }

    private void bottomMenu() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if(itemId == R.id.menu_anasayfa){
                Intent intent = new Intent(this, anaMenu.class );
                startActivity(intent);
                finish();

            }
            else if (itemId == R.id.menu_rezervasyon) {
                Intent intent = new Intent(yemekCesitleri.this, rezervasyon.class );
                startActivity(intent);
                finish();

            }
            else if (itemId == R.id.menu_profil) {
                Intent intent = new Intent(yemekCesitleri.this, profil.class );
                startActivity(intent);
                finish();    //mevcut aktiviteyi kapatacak.
            }
            return true;
        });
    }

    public void yemekleriGetirVeGoster(String kategori) {
        // Veritabanından kategoriye göre yemekleri çekiyoruz
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT yemek_adi, resim, kalori, fiyat FROM Yemekler WHERE kategori = ?", new String[]{kategori});

        int index = 0;
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast() && index < 8) {  // 8 yemek görüntüleniyor
                @SuppressLint("Range") String yemekAdi = cursor.getString(cursor.getColumnIndex("yemek_adi"));
                @SuppressLint("Range") String resimYolu = cursor.getString(cursor.getColumnIndex("resim"));
                @SuppressLint("Range") int kalori = cursor.getInt(cursor.getColumnIndex("kalori"));
                @SuppressLint("Range") int fiyat = cursor.getInt(cursor.getColumnIndex("fiyat"));

                // TextView'lere verileri yerleştir
                textViewYemekAd[index].setText(yemekAdi);
                textViewFiyat[index].setText(String.format("%d TL", fiyat));
                textViewKalori[index].setText(String.format("%d kcal", kalori));

                // Resimleri assets klasöründen yükle
                setResimFromAssets(resimYolu, imageView[index]);

                // CardView'i görünür yapıyoruz
                cardView[index].setVisibility(View.VISIBLE);

                // CardView'a tıklandığı zaman click metodunu çalıştırılacak.
                int finalIndex = index;  // finalIndex kullanarak doğru index ile işlem yapıyoruz
                cardView[index].setOnClickListener(v -> {
                    Intent intent = new Intent(yemekCesitleri.this, yemekDetay.class);
                    intent.putExtra("yemekAdi", yemekAdi);  // Yemek adı
                    intent.putExtra("resimYolu", resimYolu);  // Resim yolu
                    startActivity(intent);
                });

                index++;
                cursor.moveToNext();  // Sonraki satıra geç
            }
        }
        cursor.close();  // Cursor'u kapat
    }

    public void setResimFromAssets(String resimYolu, ImageView imageView) {
        try {
            // assets klasöründen resim dosyasını alınır
            InputStream is = getAssets().open(resimYolu);  // Örneğin 'corba/kabak1.png' gibi
            Drawable yemekResmi = Drawable.createFromStream(is, null);  // InputStream ile resim dosyasını Drawable'a çeviriyoruz
            imageView.setImageDrawable(yemekResmi);  // Drawable'ı ImageView'a set ediyoruz
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

/*
getIntent() ile Intent nesnesine erişilir. Bu intent nesnesinde putExtre ile bu aktiviteye bir veri taşınmıştı.
getStringExtra(String key) metodu, Intent'teki bir anahtar (key) ile ilişkilendirilmiş String verisini döndürür.
intent nesnesi "yemekCesidIsmi" adında bir anahtarı kullanıştı parantez içine de bu yazılır.
Önceki aktiviteden gelen değer de burada oluşturduğumuz string değişkene atanır.
 */