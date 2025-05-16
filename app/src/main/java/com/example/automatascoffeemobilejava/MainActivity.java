package com.example.automatascoffeemobilejava;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

//---------------COSAS PENDIENTES---------------
//Revisar lo del campo de la contraseña, el PASSWORD TRUE me arroja que es deprecated, cehcar eso
//PONER SOMBRAS


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //-Inicio-
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //-Inicializar los elementos de la vista
        Button btnLogin = findViewById(R.id.btnLogin);
        EditText txtUser = findViewById(R.id.txtUser);
        EditText txtPassword = findViewById(R.id.txtPassword);
        ConstraintLayout loginCard = findViewById(R.id.loginCard);
        FrameLayout opacityCard = findViewById(R.id.opacityCard);
        ConstraintLayout topCard = findViewById(R.id.topCard);
        ConstraintLayout bottomCard = findViewById(R.id.bottomCard);
        topCard.setScaleX(0);
        topCard.setScaleY(0);
        topCard.setVisibility(View.GONE);
        bottomCard.setScaleX(0);
        bottomCard.setScaleY(0);
        bottomCard.setTranslationY(300);
        bottomCard.setVisibility(View.GONE);


        //Obtiene los DP
        float DP = 100f;
        float PX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP, getResources().getDisplayMetrics());


        //-Barra de estado transparente-
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        //Para cargar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }




        //---LOGIN---
        btnLogin.setOnClickListener(v -> {
            String username = txtUser.getText().toString();
            String password = txtPassword.getText().toString();

            if (username.equals("ale") && password.equals("123")) {
                loginCard.animate()
                        .scaleX(1.15f)
                        .scaleY(1.15f)
                        .setDuration(100)
                        .withEndAction(() -> {
                            loginCard.animate()
                                    .scaleX(0f)
                                    .scaleY(0f)
                                    .setDuration(200)
                                    .withEndAction(() -> loginCard.setVisibility(View.GONE))
                                    .start();
                            opacityCard.animate()
                                    .alpha(0f)
                                    .setDuration(200)
                                    .withEndAction(() -> opacityCard.setVisibility(View.GONE))
                                    .withEndAction(() -> {
                                        topCard.setVisibility(View.VISIBLE);
                                        topCard.animate()
                                                .scaleX(1.0f)
                                                .scaleY(1.0f)
                                                .setDuration(200)
                                                .start();
                                        bottomCard.setVisibility(View.VISIBLE);
                                        bottomCard.animate()
                                                .scaleX(1.0f)
                                                .scaleY(1.0f)
                                                .setDuration(200)
                                                .start();
                                    });
                        });
                //Oculta el teclado al hacer click en el botón
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } else {
                loginCard.animate()
                                .translationX(15f)
                                .setDuration(50)
                                .withEndAction(() -> {
                                    loginCard.animate()
                                            .translationX(-15f)
                                            .setDuration(50)
                                            .withEndAction(() -> {
                                                loginCard.animate()
                                                        .translationX(0f)
                                                        .setDuration(50)
                                                        .start();
                                            })
                                            .start();
                                });
                Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });



        //---BOTTOM CARD---
        bottomCard.setOnTouchListener(new View.OnTouchListener() {
            float dY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dY = v.getY() - event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        v.animate()
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //MODO OSCURO DEL MAPA
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_blue_style)
            );
            if (!success) {
                Log.e("MAPA", "El estilo no se aplicó.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MAPA", "Archivo de estilo no encontrado", e);
        }

        LatLng coordenadas = new LatLng(25.814700, -108.979991);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15));
    }


}
