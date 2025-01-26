package com.ipekbusra.restoranrezervasyon;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class rezervasyon extends AppCompatActivity {

    private int gun, ay, yil, saat, dakika;
    private int kisi_sayisi=1, kullaniciId;
    private String tarihDegeri, saatDegeri;
    private ArrayList<rezervasyonBilgisi> rezervasyonlar;
    private Button button_tarih_sec, button_saat_sec, button_rezervasyon_olustur;
    private BottomNavigationView bottomNavigationView;
    private TextView textView_kisi_sayisi;
    private ImageButton imageButton_kisi_arttir, imageButton_kisi_azalt;
    private ArrayAdapter<rezervasyonBilgisi> adapter;
    private ListView rezervasyon_listesi;
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rezervasyon);

        button_rezervasyon_olustur  = findViewById(R.id.button_rezervasyon);
        bottomNavigationView =  findViewById(R.id.bottom_navigation_rezervasyon);
        button_tarih_sec =  findViewById(R.id.button_tarih_sec);
        button_saat_sec =  findViewById(R.id.button_saat_sec);
        textView_kisi_sayisi = findViewById(R.id.textView_kisi_sayisi);
        imageButton_kisi_arttir =findViewById(R.id.imageButton_arttir);
        imageButton_kisi_azalt =findViewById(R.id.imageButton_azalt);

        rezervasyonlar = new ArrayList<>();
        rezervasyon_listesi = findViewById(R.id.rezervasyon_liste);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rezervasyonlar);
        rezervasyon_listesi.setAdapter(adapter);
        registerForContextMenu(rezervasyon_listesi); //Context menü listeye eklenir.

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        kullaniciId = (int) sharedPreferences.getLong("kullaniciId", -1); // Kullanıcı ID'sini alıyoruz
        Log.d("rezervasyon", "Rezervasyon alınan Kullanıcı ID: " + kullaniciId);

        databaseHelper = new DatabaseHelper(rezervasyon.this);

        rezervasyonlar.addAll(databaseHelper.getRezervasyonlar(kullaniciId));
        adapter.notifyDataSetChanged();

        textView_kisi_sayisi.setText(String.valueOf(kisi_sayisi));
        imageButton_kisi_arttir.setOnClickListener(v -> {
            if(kisi_sayisi < 40){
                kisi_sayisi++;
                textView_kisi_sayisi.setText(String.valueOf(kisi_sayisi));
            }
        });
        imageButton_kisi_azalt.setOnClickListener(v -> {
            if(kisi_sayisi > 1){
                kisi_sayisi--;
                textView_kisi_sayisi.setText(String.valueOf(kisi_sayisi));
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if(itemId == R.id.menu_anasayfa){

                    Intent intent = new Intent(this, anaMenu.class );
                    startActivity(intent);
                    finish();

            }
            else if (itemId == R.id.menu_profil) {

                    Intent intent = new Intent(rezervasyon.this, profil.class );
                    startActivity(intent);
                    finish();    //mevcut aktiviteyi kapatacak.

            }
            return true;
        });

        button_rezervasyon_olustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = databaseHelper.getWritableDatabase(); // bu bağlantı ile veriler veritabanına yazılır.

                    ContentValues satir = new ContentValues();
                    satir.put("tarih", tarihDegeri);
                    satir.put("saat", saatDegeri);
                    satir.put("kisi_sayisi", kisi_sayisi);
                    satir.put("kullanici", kullaniciId);

                    long sonuc = db.insert("Rezervasyon", null, satir); //eklenmezse -1 döner.

                    if (sonuc != -1) {
                        Toast.makeText(rezervasyon.this, "Rezervasyonunuz başarıyla oluşturulmuştur.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(rezervasyon.this, "Rezervasyonunuz oluşturulamamıştır, lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                    }

                    db.close();  // Veritabanı bağlantısını kapatıyoruz
            }
        });
        
        Calendar now = Calendar.getInstance();  //Default olarak bugunun tarih ve saati alınır.
        yil = now.get(Calendar.YEAR);
        ay = now.get(Calendar.MONTH);   //ay bilgisi alınırken ocak 0, şubat 1 şeklinde ilerler. Bu yüzden alttaki metotda +1 eklenir.
        gun = now.get(Calendar.DAY_OF_MONTH);
        saat = now.get(Calendar.HOUR_OF_DAY);
        dakika = now.get(Calendar.MINUTE);
        tarihSaatGuncelle();

        button_saat_sec.setOnClickListener(v -> saatSecimi());
        button_tarih_sec.setOnClickListener(v -> tarihSecimi());
   
    }

    private void tarihSecimi() {
        Calendar now = Calendar.getInstance(); //bugünün tarihi seçilir.
        //Traih seçme ekranı
        DatePickerDialog tarihEkrani = new DatePickerDialog(this,((view, year, month, dayOfMonth) -> {
            yil = year;
            ay = month;
            gun = dayOfMonth;
            tarihSaatGuncelle();
        }),yil,ay, gun);

        tarihEkrani.getDatePicker().setMinDate(System.currentTimeMillis()); //geçmiş tarihler seçilemez.
        tarihEkrani.show();
    }

    private void saatSecimi() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog saatEkrani = new TimePickerDialog(this, ((view, hourOfDay, minute) -> {
            if(hourOfDay >= 0 && hourOfDay < 12){
                Toast toast = Toast.makeText(this,"Gece 12 ile sabah 12 arasında rezervasyon oluşturulamaz.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0); //uyarı mesajı ekranın altı yerine tam ortada gözükecektir.
                toast.show();
            }else{
                saat = hourOfDay;
                dakika = minute;
                tarihSaatGuncelle();
            }
        }), saat, dakika, true);
            saatEkrani.show();
    }

    private void tarihSaatGuncelle() {
        tarihDegeri = gun + "/" + (ay + 1) +  "/" + yil;
        saatDegeri = String.format("%02d:%02d",saat,dakika);
        button_tarih_sec.setText(tarihDegeri); //tarih ve saaat butonların üzerine yazılacak.
        button_saat_sec.setText(saatDegeri);
    }

    // Context Menu oluşturma
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context, menu);  // XML menüsünü şablon olarak kullan
    }

    // Context Menu seçenekleri
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;  // Tıklanan öğenin pozisyonu

        rezervasyonBilgisi rezervasyon = rezervasyonlar.get(position);  // Seçilen rezervasyon nesnesi

        if(item.getItemId() == R.id.menu_sil){

            // Veritabanından silme işlemi yapılabilir
            deleteRezervasyon(position);
            return true;
        } else if (item.getItemId() == R.id.menu_takvime_ekle) {
            takvimeEkle(rezervasyonlar.get(position));
            return true;
        }else
            return super.onContextItemSelected(item);

    }

    // Rezervasyonu veritabanından silme fonksiyonu
    private void deleteRezervasyon(int position) {
        rezervasyonBilgisi rezervasyon = rezervasyonlar.get(position);  // İlgili rezervasyonun bilgileri
        // Veritabanı bağlantısı ve silme işlemi
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete("Rezervasyon", "tarih = ? AND saat = ?", new String[]{rezervasyon.getTarih(), rezervasyon.getSaat()});
        db.close();

        // Listeyi güncelle
        rezervasyonlar.remove(position);  // Listeden çıkar
        adapter.notifyDataSetChanged();    // Listeyi yenile
    }

    //Rezervasyonu telefonun takvim uygulamasına ekleme
    private void takvimeEkle(rezervasyonBilgisi rezervasyon) {
        String tarih = rezervasyon.getTarih();  // Tarih bilgisi
        String saat = rezervasyon.getSaat();    // Saat bilgisi
        int kisiSayi = rezervasyon.getKisi_sayisi();

        // Tarih bilgisini ayır
        String[] tarihParcala = tarih.split("/");  // "6/1/2025" -> ["6", "1", "2025"]
        int gun = Integer.parseInt(tarihParcala[0]);
        int ay = Integer.parseInt(tarihParcala[1]) - 1; // Ayların index değeri 0'dan başladığı için -1 eklenir.
        int yil = Integer.parseInt(tarihParcala[2]);

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(yil, ay, gun, Integer.parseInt(saat.split(":")[0]), Integer.parseInt(saat.split(":")[1]));

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis()) //rezervasyonun başlangıç tarihi ve saati
                .putExtra(CalendarContract.Events.TITLE, "Rezervasyon Bilgileriniz:\n " + tarih + " \t " + saat) //rezervasyonun adı
                .putExtra(CalendarContract.Events.DESCRIPTION, tarih + " tarihinde saat " + saat + " " + kisiSayi + " kişilik rezervasyonunuz bulunmaktadır." )
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Lezzet Ötesi");
        startActivity(intent);  // Takvimi uygulaması açılır.

    }

}
