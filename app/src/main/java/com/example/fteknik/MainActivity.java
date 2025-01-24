package com.example.fteknik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Button Cloud
        ImageView btnCloud = findViewById(R.id.btnCloud);
        btnCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Pindah ke halaman WeatherActivity
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                startActivity(intent);
            }

        });


        // Struktur dan Organisasi
        LinearLayout btnOrganisasi = findViewById(R.id.btn_layout_organisasi);
        btnOrganisasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke halaman StrukturOrganisasiActivity
                Intent intent = new Intent(MainActivity.this, Organisasi.class);
                startActivity(intent);
            }
        });

        // Prestasi
        LinearLayout btnPrestasi = findViewById(R.id.btn_layout_prestasi);
        btnPrestasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke halaman PrestasiActivity
                Intent intent = new Intent(MainActivity.this, Prestasi.class);
                startActivity(intent);
            }
        });

        // Dosen
        LinearLayout btnDosen = findViewById(R.id.btn_layout_dosen);
        btnDosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke halaman DosenActivity
                Intent intent = new Intent(MainActivity.this, DosenStaf.class);
                startActivity(intent);

                Toast.makeText(getApplicationContext(), "ini halaman dosen dan staf", Toast.LENGTH_SHORT).show();
            }
        });

        // Sejarah
        LinearLayout btnSejarah = findViewById(R.id.btn_layout_history);
        btnSejarah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke halaman SejarahActivity
                Intent intent = new Intent(MainActivity.this, Sejarah.class);
                startActivity(intent);
            }
        });
    }
}