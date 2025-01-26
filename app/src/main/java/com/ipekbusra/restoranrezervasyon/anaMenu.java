package com.ipekbusra.restoranrezervasyon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class anaMenu extends AppCompatActivity {

    private String yemekKategori = "";
    //Bu değişken ile yemek çeşit ismi alınıp gidilecek olana aktivitede başlık olarak yazılacak.
    private ImageButton[] imageButton;
    private TextView[] textView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_menu);

        bottomNavigationView = findViewById(R.id.bottom_navigation_ana_menu);

        imageButton = new ImageButton[]{findViewById(R.id.imageButton_corba), findViewById(R.id.imageButton_anayemek),
                findViewById(R.id.imageButton_makarna), findViewById(R.id.imageButton_salata),
                findViewById(R.id.imageButton_pizza), findViewById(R.id.imageButton_tatli),
                findViewById(R.id.imageButton_icecek),findViewById(R.id.imageButton_yanurun)};

        textView = new TextView[]{findViewById(R.id.textView1), findViewById(R.id.textView2),
                findViewById(R.id.textView3), findViewById(R.id.textView4),
                findViewById(R.id.textView5), findViewById(R.id.textView6),
                findViewById(R.id.textView7), findViewById(R.id.textView8)};


        View.OnClickListener yemekCesitleri = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Basılan ImageButton'a göre doğru TextView'deki ismi al
                    for (int i = 0; i < imageButton.length; i++) {
                        if (v.getId() == imageButton[i].getId()) {
                            yemekKategori = textView[i].getText().toString(); // ilgili yemek ismini al
                            break; // uygun ImageButton bulundu, döngüyü kır
                        }
                    }

                    // Intent ile yemek çeşidi ismini gönder
                    Intent intent = new Intent(anaMenu.this, yemekCesitleri.class);
                    intent.putExtra("yemekKategori", yemekKategori);
                    startActivity(intent);
                }
            };

        for(int i=0; i< imageButton.length; i++){
            imageButton[i].setOnClickListener(yemekCesitleri);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_rezervasyon) {

                Intent intent = new Intent(anaMenu.this, rezervasyon.class );
                startActivity(intent);
                finish();    //mevcut aktiviteyi kapatacak.

            }
            else if (itemId == R.id.menu_profil) {

                Intent intent = new Intent(anaMenu.this, profil.class );
                startActivity(intent);
                finish();    //mevcut aktiviteyi kapatacak.

            }
            return true;
        });
    }
}

/* Intent sınıfı, bir aktiviteyi başlatmak veya bir aktivite arasında veri taşımak için kullanılır. putExtra() metodu,
bir Intent nesnesine ek bilgi (veri) eklememizi sağlar. Bu eklediğimiz veriyi, hedef aktivite (diğer bir ekran) tarafından alabiliriz.
putExtra() metodu, intent nesnesine bir veri ekler. intent nesnesi aktiviteyi başlatmak ve veri göndermek için kullanılır.
putextra() nın 2 parametresi vardır. İlk parametre, gönderdiğiniz verinin anahtar adıdır (bu durumda "yemekCesidIsmi"). Bu anahtar adı,
veriyi hedef aktivitede alırken kullanılacak anahtardır.
İkinci parametre, göndermek istediğiniz veridir (bu durumda yemekCesidIsmi). Burada, yemekCesidIsmi bir String değişkendir ve bu değişkenin
değeri, tıklanan yemek türünü içerecektir.
 */