package com.example.automatascoffeemobilejava;

import static com.example.automatascoffeemobilejava.utils.DimensionUtils.dpToPx;


import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.example.automatascoffeemobilejava.data.API;
import com.example.automatascoffeemobilejava.data.requests.CompleteRequest;
import com.example.automatascoffeemobilejava.data.requests.DataRequest;
import com.example.automatascoffeemobilejava.data.requests.LoginRequest;
import com.example.automatascoffeemobilejava.data.requests.LogoutRequest;
import com.example.automatascoffeemobilejava.data.responses.CompleteResponse;
import com.example.automatascoffeemobilejava.data.responses.DataResponse;
import com.example.automatascoffeemobilejava.data.responses.DeliveryResponse;
import com.example.automatascoffeemobilejava.data.responses.DetailsResponse;
import com.example.automatascoffeemobilejava.data.responses.DirectionsResponse;
import com.example.automatascoffeemobilejava.data.responses.LoginResponse;
import com.example.automatascoffeemobilejava.data.responses.LogoutResponse;
import com.example.automatascoffeemobilejava.model.Repartidor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Socket socket;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private int id_repartidor = -1; // Valor inicial por defecto
    private int id_Pedido = -1; // Valor inicial por defecto
    private List<int[]> estadoPedidos = new ArrayList<>(); // Lista para almacenar el estado de los pedidos


    //-METODO PRINCIPAL-
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //---INICIO DE LA APLICACIÓN---
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //---INICIALIZACIÓN DEL SOCKET.IO---
        try {
            socket = IO.socket("https://automatas-coffee-api.onrender.com");
            socket.connect();
            socket.on(Socket.EVENT_CONNECT, args -> Log.d("SOCKET", "Conectado al servidor"));
            socket.on("actualizar_mapa", args -> {
                Log.d("SOCKET", "Datos recibidos: " + args[0]);
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //---CARGAR ELEMENTOS DEL DISEÑO---

        Button btnLogin = findViewById(R.id.btnLogin);
        EditText txtUser = findViewById(R.id.txtUser);
        EditText txtPassword = findViewById(R.id.txtPassword);
        ConstraintLayout loginCard = findViewById(R.id.loginCard);
        FrameLayout opacityCard = findViewById(R.id.opacityCard);
        ConstraintLayout topCard = findViewById(R.id.topCard);
        ConstraintLayout bottomCard = findViewById(R.id.bottomCard);
        ImageButton bottomCardOpenerCloser = findViewById(R.id.bottomCardOpenerCloser);
        ImageButton topCardOpenerCloser = findViewById(R.id.topCardOpenerCloser);
        ImageButton topCardUserButton = findViewById(R.id.topCardUserButton);
        ConstraintLayout infoCard = findViewById(R.id.infoCard);
        Button infoCardCloser = findViewById(R.id.infoCardCloser);
        ConstraintLayout completeCard = findViewById(R.id.completeCard);
        Button completeButton = findViewById(R.id.completeButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button completeCardButton = findViewById(R.id.completeCardButton);
        Button logoutButton = findViewById(R.id.logoutButton);


        int bottomCardMaximumSize = 375;
        int bottomCardMinimalSize = 135;

        float bottomCardmMaximumHeight = dpToPx(this, bottomCardMaximumSize);
        float bottomCardMinimalHeight = dpToPx(this, bottomCardMinimalSize);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;

        float notificationBarHeight = dpToPx(this, 38);

        float maxY = screenHeight - bottomCardmMaximumHeight;
        float minY = screenHeight - bottomCardMinimalHeight;

        float bottomCardClosedHeight = screenHeight + notificationBarHeight - bottomCardMinimalHeight;
        float bottomCardOpenedHeight = screenHeight + notificationBarHeight - bottomCardmMaximumHeight;

        ViewGroup.LayoutParams bottomCardLayoutParams = bottomCard.getLayoutParams();
        bottomCardLayoutParams.height = (int) dpToPx(this, bottomCardMaximumSize);
        ViewGroup.LayoutParams topCardLayoutParams = topCard.getLayoutParams();
        topCardLayoutParams.height = (int) dpToPx(this, 60);

        topCard.setScaleX(0);
        topCard.setScaleY(0);
        topCard.setVisibility(View.GONE);
        bottomCard.setScaleX(0);
        bottomCard.setScaleY(0);
        bottomCard.setTranslationY(bottomCardMinimalHeight);
        bottomCard.setVisibility(View.GONE);
        infoCard.setScaleY(0);
        infoCard.setScaleX(0);
        infoCard.setVisibility(View.GONE);
        completeCard.setScaleY(0);
        completeCard.setScaleX(0);
        completeCard.setVisibility(View.GONE);
        opacityCard.setClickable(true);


        //---ELEMENTOS EXTRAS---

        //-Declarar vibrador-
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //-Barra de estado transparente-
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        //Carga el mapa
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        //---UBICACIÓN Y SEGUIMIENTO DE UBICACIÓN---

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    enviarUbicacion(lat, lng); // Enviar ubicación al servidor
                }
            }
        };
        startLocationUpdates();


        //---BACKEND---

        //Realiza las solicitudes http, usará esa url y la api para el trabajo necesario
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.100.214:8000/")
                .addConverterFactory(GsonConverterFactory.create()) //Convertidor de JSON a objeto, al enviar y recibir datos
                .build();
        API api = retrofit.create(API.class);

        txtUser.setText("alex075");
        txtPassword.setText("Evolve075_");


        //---FUNCIONAMIENTO DEL DISEÑO---

        //login
        btnLogin.setOnClickListener(v -> {
            LoginRequest loginRequest = new LoginRequest(txtUser.getText().toString(), txtPassword.getText().toString());
            api.login(loginRequest).enqueue(new retrofit2.Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();

                        if (loginResponse.isSuccess()) {
                            Toast.makeText(MainActivity.this, loginResponse.getStatus(), Toast.LENGTH_SHORT).show();
                            int id = loginResponse.getId();
                            id_repartidor = id; // Asigna el ID del repartidor


                            // Animaciones y lógica de UI tras el inicio de sesión
                            opacityCard.setClickable(false);
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
                                                            .scaleX(1.1f)
                                                            .scaleY(1.1f)
                                                            .setDuration(100)
                                                            .withEndAction(() -> {
                                                                topCard.animate()
                                                                        .scaleX(1.0f)
                                                                        .scaleY(1.0f)
                                                                        .setDuration(100)
                                                                        .start();
                                                            });
                                                    bottomCard.setVisibility(View.VISIBLE);
                                                    bottomCard.animate()
                                                            .scaleX(1.1f)
                                                            .scaleY(1.1f)
                                                            .setDuration(100)
                                                            .withEndAction(() -> {
                                                                bottomCard.animate()
                                                                        .scaleX(1.0f)
                                                                        .scaleY(1.0f)
                                                                        .setDuration(100)
                                                                        .start();
                                                            });
                                                });
                                    });

                            // Vibración y ocultar teclado
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(100);
                            }
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            View view = getCurrentFocus();
                            if (view != null) {
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }

                            logueado(api, id);

                        } else {
                            Toast.makeText(MainActivity.this, loginResponse.getStatus(), Toast.LENGTH_SHORT).show();
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(100);
                            }
                        }
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                            Log.e("API_ERROR", "Error en la respuesta del servidor: " + error);
                            Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("API_ERROR", "Error al procesar el cuerpo del error", e);
                            Toast.makeText(MainActivity.this, "Error al procesar el error del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error al conectarse con el servidor" + t, Toast.LENGTH_SHORT).show();
                }
            });
        });

        //topCard posición inicial
        topCard.post(new Runnable() {
            @Override
            public void run() {
                topCard.setY(dpToPx(MainActivity.this, 50));
            }
        });

        //topCard botón para abrir y cerrar
        topCardOpenerCloser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (topCard.getHeight() == dpToPx(MainActivity.this, 60)) {
                            ValueAnimator animator = ValueAnimator.ofFloat(dpToPx(MainActivity.this, 60), dpToPx(MainActivity.this, 200));
                            animator.addUpdateListener(ValueAnimator -> {
                                ;
                                float value = (float) animator.getAnimatedValue();
                                topCardLayoutParams.height = (int) value;
                                topCard.setLayoutParams(topCardLayoutParams);
                            });
                            animator.setDuration(200);
                            animator.start();
                        } else {
                            ValueAnimator animator = ValueAnimator.ofFloat(dpToPx(MainActivity.this, 200), dpToPx(MainActivity.this, 60));
                            animator.addUpdateListener(ValueAnimator -> {
                                float value = (float) animator.getAnimatedValue();
                                topCardLayoutParams.height = (int) value;
                                topCard.setLayoutParams(topCardLayoutParams);
                            });
                            animator.setDuration(200);
                            animator.start();
                        }
                    default:
                        return false;
                }
            }
        });

        //bottomCard táctil
        bottomCard.setOnTouchListener(new View.OnTouchListener() {
            float dY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dY = v.getY() - event.getRawY() - notificationBarHeight;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float newY = event.getRawY() + dY;
                        if (newY < maxY) newY = maxY;
                        if (newY > minY) newY = minY;
                        v.setY(newY + notificationBarHeight);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (v.getY() - 10 < bottomCardOpenedHeight + (bottomCardClosedHeight - bottomCardOpenedHeight) / 2) {
                            bottomCard.animate()
                                    .translationY(0)
                                    .setDuration(200)
                                    .withEndAction(() -> {
                                        bottomCard.animate()
                                                .translationY(20)
                                                .setDuration(100)
                                                .start();
                                    });
                        } else if (v.getY() + 10 > bottomCardClosedHeight - (bottomCardClosedHeight - bottomCardOpenedHeight) / 2) {
                            bottomCard.animate()
                                    .translationY(bottomCardmMaximumHeight - bottomCardMinimalHeight + 20)
                                    .setDuration(200)
                                    .withEndAction(() -> {
                                        bottomCard.animate()
                                                .translationY(bottomCardmMaximumHeight - bottomCardMinimalHeight)
                                                .setDuration(100)
                                                .start();
                                    });
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });

        //bottomCard posición inicial
        bottomCard.post(new Runnable() {
            @Override
            public void run() {
                bottomCard.setY(bottomCardmMaximumHeight - bottomCardMinimalHeight);
            }
        });

        //bottomCard botón para abrir y cerrar
        bottomCardOpenerCloser.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (bottomCard.getY() + 10 > bottomCardClosedHeight) {
                            bottomCard.animate()
                                    .translationY(0)
                                    .setDuration(200)
                                    .withEndAction(() -> {
                                        bottomCard.animate()
                                                .translationY(20)
                                                .setDuration(100)
                                                .start();
                                    });
                        } else {
                            bottomCard.animate()
                                    .translationY(bottomCardmMaximumHeight - bottomCardMinimalHeight + 20)
                                    .setDuration(200)
                                    .withEndAction(() -> {
                                        bottomCard.animate()
                                                .translationY(bottomCardmMaximumHeight - bottomCardMinimalHeight)
                                                .setDuration(100)
                                                .start();
                                    });
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });

        //userCard botón para abrirlo
        topCardUserButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        infoCard.setVisibility(View.VISIBLE);
                        opacityCard.setVisibility(View.VISIBLE);
                        opacityCard.setClickable(true);
                        opacityCard.animate()
                                .alpha(1f)
                                .setDuration(200)
                                .start();
                        infoCard.animate()
                                .scaleY(1.05f)
                                .scaleX(1.05f)
                                .setDuration(150)
                                .withEndAction(
                                        () -> infoCard.animate()
                                                .scaleY(1f)
                                                .scaleX(1f)
                                                .setDuration(150)
                                                .start());
                    default:
                        return false;
                }
            }
        });

        //x para cerrar la infoCard
        infoCardCloser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        opacityCard.setClickable(false);
                        opacityCard.animate()
                                .alpha(0f)
                                .setDuration(200)
                                .withEndAction(() -> opacityCard.setVisibility(View.GONE));
                        infoCard.animate()
                                .scaleY(0f)
                                .scaleX(0f)
                                .setDuration(150)
                                .withEndAction(() -> infoCard.setVisibility(View.GONE));
                        return true;
                    default:
                        return false;
                }
            }
        });

        //boton para completar el pedido
        completeCardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        completeCard.setVisibility(View.VISIBLE);
                        opacityCard.setVisibility(View.VISIBLE);
                        opacityCard.setClickable(true);
                        opacityCard.animate()
                                .alpha(1f)
                                .setDuration(200)
                                .start();
                        completeCard.animate()
                                .scaleY(1.05f)
                                .scaleX(1.05f)
                                .setDuration(150)
                                .withEndAction(
                                        () -> completeCard.animate()
                                                .scaleY(1f)
                                                .scaleX(1f)
                                                .setDuration(150)
                                                .start());
                    default:
                        return false;
                }
            }
        });

        //boton para cancelar el completado del pedido
        cancelButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        opacityCard.setClickable(false);
                        opacityCard.animate()
                                .alpha(0f)
                                .setDuration(200)
                                .withEndAction(() -> opacityCard.setVisibility(View.GONE));
                        completeCard.animate()
                                .scaleY(0f)
                                .scaleX(0f)
                                .setDuration(150)
                                .withEndAction(() -> completeCard.setVisibility(View.GONE));
                        return true;
                    default:
                        return false;
                }
            }
        });

        //boton para confirmar el completado del pedido
        completeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Aquí puedes agregar la lógica para completar el pedido
                        Toast.makeText(MainActivity.this, "Pedido completado", Toast.LENGTH_SHORT).show();
                        opacityCard.setClickable(false);
                        opacityCard.animate()
                                .alpha(0f)
                                .setDuration(200)
                                .withEndAction(() -> opacityCard.setVisibility(View.GONE));
                        completeCard.animate()
                                .scaleY(0f)
                                .scaleX(0f)
                                .setDuration(150)
                                .withEndAction(() -> completeCard.setVisibility(View.GONE));
                        CompleteRequest completeRequest = new CompleteRequest(id_Pedido);
                        api.complete(completeRequest).enqueue(new retrofit2.Callback<CompleteResponse>() {
                            @Override
                            public void onResponse(Call<CompleteResponse> call, Response<CompleteResponse> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Pedido completado exitosamente", Toast.LENGTH_SHORT).show();
                                    realizarConsulta(api, id_Pedido);
                                    completarPedido(api, id_Pedido);
                               } else {
                                    Toast.makeText(MainActivity.this, "Error al completar el pedido", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<CompleteResponse> call, Throwable t) {
                                Log.e("COMPLETE", "Error al conectarse con el servidor: " + t.getMessage());
                                Toast.makeText(MainActivity.this, "Error al conectarse con el servidor", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true;
                    default:
                        return false;
                }
            }
        });

        //boton para cerrar sesión
        logoutButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        opacityCard.setClickable(false);
                        opacityCard.animate()
                                .alpha(0f)
                                .setDuration(200)
                                .withEndAction(() -> opacityCard.setVisibility(View.GONE));
                        topCard.animate()
                                .scaleX(0f)
                                .scaleY(0f)
                                .setDuration(200)
                                .withEndAction(() -> topCard.setVisibility(View.GONE));
                        bottomCard.animate()
                                .scaleX(0f)
                                .scaleY(0f)
                                .translationY(bottomCardMinimalHeight)
                                .setDuration(200)
                                .withEndAction(() -> bottomCard.setVisibility(View.GONE));
                        infoCard.animate()
                                .scaleX(0f)
                                .scaleY(0f)
                                .setDuration(200)
                                .withEndAction(() -> infoCard.setVisibility(View.GONE))
                                .start();

                        loginCard.setVisibility(View.VISIBLE);
                        loginCard.animate()
                                .scaleX(1.15f)
                                .scaleY(1.15f)
                                .setDuration(200)
                                .withEndAction(() -> {
                                    loginCard.animate()
                                            .scaleX(1.0f)
                                            .scaleY(1.0f)
                                            .setDuration(200)
                                            .start();
                                    opacityCard.setVisibility(View.VISIBLE);
                                    opacityCard.setClickable(true);
                                    opacityCard.animate()
                                            .alpha(1f)
                                            .setDuration(200)
                                            .start();
                                });
                        LogoutRequest logoutRequest = new LogoutRequest(id_repartidor);
                        Log.d("LOGOUT_REQUEST", new Gson().toJson(logoutRequest));
                        Log.d("LOGOUT_REQUEST", new Gson().toJson(logoutRequest));
                        api.logout(logoutRequest).enqueue(new retrofit2.Callback<LogoutResponse>() {
                            @Override
                            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                                Log.e("LOGOUT", "Error al conectarse con el servidor: " + t.getMessage());
                                Toast.makeText(MainActivity.this, "Error al conectarse con el servidor", Toast.LENGTH_SHORT).show();
                            }
                        });


                        return true;
                    default:
                        return false;

                }
            }
        });

    }


    //-METODO DE PERMISOS Y SEGUIMIENTO DE UBICACIÓN-
    private void startLocationUpdates() {
        //-API DE LA UBICACIÓN MODERNA-
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    //-CARACTERÍSTICAS DEL MAPA (PUNTO DE INICIO, PUNTO DE UBICACIÓN, ESTILO)-
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //-Modo oscuro en el mapa-
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_light_brown_style)
            );
            if (!success) {
                Log.e("MAPA", "El estilo no se aplicó.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MAPA", "Archivo de estilo no encontrado", e);
        }

        //-Muestra el puntito azul en el mapa-
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        //-Desactiva el botón de ubicación-
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        //-Posición inicial-
        LatLng coordenadas = new LatLng(25.814700, -108.979991);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15));
    }


    public void logueado(API api, int id) {

        TextView txtNombre = findViewById(R.id.txtNombre);
        TextView txtTelefono = findViewById(R.id.txtTelefono);
        TextView txtCurp = findViewById(R.id.txtCURP);
        TextView txtSangre = findViewById(R.id.txtSangre);
        TextView txtVigencia = findViewById(R.id.txtVigencia);
        TextView txtPedido1 = findViewById(R.id.txtPedido1);
        TextView txtPedido2 = findViewById(R.id.txtPedido2);
        TextView txtPedido3 = findViewById(R.id.txtPedido3);
        ImageButton pedidoButton1 = findViewById(R.id.pedidoButton1);
        ImageButton pedidoButton2 = findViewById(R.id.pedidoButton2);
        ImageButton pedidoButton3 = findViewById(R.id.pedidoButton3);

        // OBTENCIÓN DE DATOS DEL REPARTIDOR
        DataRequest dataRequest = new DataRequest(id);
        api.data(dataRequest).enqueue(new retrofit2.Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DataResponse dataResponse = response.body();
                    Repartidor data = dataResponse.getData();

                    // Asignación de datos a los TextViews
                    txtNombre.setText(data.getNombre() + " " + data.getApellido1() + " " + data.getApellido2());
                    txtTelefono.setText(data.getTelefono());
                    txtCurp.setText(data.getCurp());
                    txtSangre.setText(data.getTipo_sangre());
                    txtVigencia.setText(data.getVigencia_licencia());

                    id_repartidor = data.getId();

                    // OBTENCIÓN DE PEDIDOS después de obtener los datos del repartidor
                    api.pedidos(id_repartidor, 3).enqueue(new retrofit2.Callback<DeliveryResponse>() {
                        @Override
                        public void onResponse(Call<DeliveryResponse> call, Response<DeliveryResponse> response) {
                            DeliveryResponse deliveryResponse = response.body();
                            String[] deliveries = deliveryResponse.getDeliveries();

                            if(deliveries != null && deliveries.length > 0) {
                                for (String id : deliveries) {
                                    enlistarPedidos(Integer.parseInt(id));
                                }
                            }


                            if (response.isSuccessful() && response.body() != null) {
                                pedidoButton1.setOnClickListener(v -> {
                                    resetButtonColors();
                                    pedidoButton1.setColorFilter(Color.GRAY);
                                    String text = txtPedido1.getText().toString();
                                    int id_pedido = extractIdFromText(text);
                                    realizarConsulta(api, id_pedido);
                                });

                                pedidoButton2.setOnClickListener(v -> {
                                    resetButtonColors();
                                    pedidoButton2.setColorFilter(Color.GRAY);
                                    String text = txtPedido2.getText().toString();
                                    int id_pedido = extractIdFromText(text);
                                    realizarConsulta(api, id_pedido);
                                });

                                pedidoButton3.setOnClickListener(v -> {
                                    resetButtonColors();
                                    pedidoButton3.setColorFilter(Color.GRAY);
                                    String text = txtPedido3.getText().toString();
                                    int id_pedido = extractIdFromText(text);
                                    realizarConsulta(api, id_pedido);
                                });


                                String status = deliveryResponse.getStatus();

                                if (deliveries != null && deliveries.length > 0) {
                                    txtPedido1.setText(deliveries.length > 0 ? "Pedido con ID: " + deliveries[0] : "");
                                    txtPedido2.setText(deliveries.length > 1 ? "Pedido con ID: " + deliveries[1] : "");
                                    txtPedido3.setText(deliveries.length > 2 ? "Pedido con ID: " + deliveries[2] : "");

                                    pedidoButton1.setVisibility(deliveries.length > 0 ? View.VISIBLE : View.INVISIBLE);
                                    pedidoButton2.setVisibility(deliveries.length > 1 ? View.VISIBLE : View.INVISIBLE);
                                    pedidoButton3.setVisibility(deliveries.length > 2 ? View.VISIBLE : View.INVISIBLE);
                                } else {
                                    txtPedido1.setText("No hay pedidos disponibles");
                                    txtPedido2.setText("");
                                    txtPedido3.setText("");

                                    pedidoButton1.setVisibility(View.INVISIBLE);
                                    pedidoButton2.setVisibility(View.INVISIBLE);
                                    pedidoButton3.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Error al obtener los pedidos", Toast.LENGTH_SHORT).show();
                            }
                        }

                        private void resetButtonColors() {
                            pedidoButton1.setColorFilter(Color.parseColor("#ff5100"));
                            pedidoButton2.setColorFilter(Color.parseColor("#ff5100"));
                            pedidoButton3.setColorFilter(Color.parseColor("#ff5100"));
                        }


                        @Override
                        public void onFailure(Call<DeliveryResponse> call, Throwable t) {
                            Log.d("URL", call.request().url().toString()); // Imprime la URL incluso en caso de fallo
                            Log.e("PEDIDOS", "Error al conectarse con el servidor: " + t.getMessage());
                            Toast.makeText(MainActivity.this, "Error al conectarse con el servidor", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(MainActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                Log.e("DATA", "Error al conectarse con el servidor: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error al conectarse con el servidor", Toast.LENGTH_SHORT).show();
            }
        });

        pedidoButton1.setOnClickListener(v -> {
            String text = txtPedido1.getText().toString();
            int id_pedido = extractIdFromText(text);
            realizarConsulta(api, id_pedido);
        });

        pedidoButton2.setOnClickListener(v -> {
            String text = txtPedido2.getText().toString();
            int id_pedido = extractIdFromText(text);
            realizarConsulta(api, id_pedido);
        });

        pedidoButton3.setOnClickListener(v -> {
            String text = txtPedido3.getText().toString();
            int id_pedido = extractIdFromText(text);
            realizarConsulta(api, id_pedido);
        });

    }

    private int extractIdFromText(String text) {
        String[] parts = text.split(": ");
        return parts.length > 1 ? Integer.parseInt(parts[1].trim()) : -1;
    }


    private void realizarConsulta(API api, int id_pedido) {
        TextView txtPedidoPara = findViewById(R.id.txtPedidoPara);
        TextView txtDireccion = findViewById(R.id.txtDireccion);
        TextView txtTiempo = findViewById(R.id.txtTiempo);
        TextView txtDistancia = findViewById(R.id.txtDistancia);
        TextView txtDetallesPedido = findViewById(R.id.txtDetallesPedido);

        Call<DetailsResponse> call = api.getDetails(id_pedido);
        call.enqueue(new retrofit2.Callback<DetailsResponse>() {
            @Override
            public void onResponse(Call<DetailsResponse> call, Response<DetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DetailsResponse details = response.body();
                    if (details.getPurchase() != null) {
                        String status = details.getPurchase().getEstatus(); // Obtén el estado del pedido

                        if (Objects.equals(status, "Entregado")) {
                            // Pedido completado
                            txtPedidoPara.setText("PEDIDO COMPLETADO");
                            txtPedidoPara.setTextColor(Color.parseColor("#006400"));

                            // Limpia los demás campos
                            txtDireccion.setText("");
                            txtTiempo.setText("");
                            txtDistancia.setText("");
                            txtDetallesPedido.setText("");

                            // Limpia el mapa
                            if (mMap != null) {
                                mMap.clear();
                            }
                        } else {
                            // Pedido en curso, procesa normalmente
                            id_Pedido = details.getPurchase().getId();

                            txtPedidoPara.setText("Pedido para: " + details.getPurchase().getUsuario());
                            txtPedidoPara.setTextColor(Color.parseColor("#333333"));
                            txtDireccion.setText("Dirección: " + details.getPurchase().getLatitud() + ", " + details.getPurchase().getLongitud());
                            txtTiempo.setText("Tiempo: " + " min");
                            txtDistancia.setText("Distancia: " + " km");

                            StringBuilder detalles = new StringBuilder();
                            for (DetailsResponse.Detail detalle : details.getDetails()) {
                                String nombreProducto = detalle.getProducto().getNombre();
                                String tamañoProducto = detalle.getTamaño().getNombre();
                                String precioProducto = detalle.getPrecio();

                                detalles.append(nombreProducto)
                                        .append(" (")
                                        .append(tamañoProducto)
                                        .append(") - $")
                                        .append(precioProducto)
                                        .append("\n");
                            }
                            txtDetallesPedido.setText(detalles.toString().trim());

                            LatLng destino = new LatLng(
                                    Double.parseDouble(details.getPurchase().getLatitud()),
                                    Double.parseDouble(details.getPurchase().getLongitud())
                            );
                            String apiKey = "AIzaSyCyGHnAIzv3n8PjibgZ7HQTMwzbuMvktDY"; // Reemplaza con tu clave de API
                            mostrarRuta(api, destino, apiKey);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DetailsResponse> call, Throwable t) {
                Log.e("API_ERROR", "Error al obtener los detalles del pedido", t);
            }
        });
    }

    private PolylineOptions currentPolyline; // Variable para almacenar la polilínea actual

    private void mostrarRuta(API api, LatLng destino, String apiKey) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        TextView txtDireccion = findViewById(R.id.txtDireccion);
        TextView txtTiempo = findViewById(R.id.txtTiempo);
        TextView txtDistancia = findViewById(R.id.txtDistancia);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng posicionActual = new LatLng(location.getLatitude(), location.getLongitude());

                // Llama a la API de direcciones
                String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                        + posicionActual.latitude + "," + posicionActual.longitude
                        + "&destination=" + destino.latitude + "," + destino.longitude
                        + "&key=" + apiKey;

                Call<DirectionsResponse> call = api.getDirections(url);
                call.enqueue(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<DirectionsResponse.Route> routes = response.body().getRoutes();
                            if (routes != null && !routes.isEmpty()) {
                                DirectionsResponse.Route route = routes.get(0);
                                String puntos = route.getOverviewPolyline().getPoints();
                                List<LatLng> listaPuntos = decodePolyline(puntos);

                                // Obtén la distancia, duración y dirección
                                String distancia = route.getLegs().get(0).getDistance().getText();
                                String duracion = route.getLegs().get(0).getDuration().getText();
                                String direccion = route.getLegs().get(0).getEndAddress();

                                // Actualiza los TextView
                                txtDistancia.setText("Distancia: " + distancia);
                                txtTiempo.setText("Tiempo: " + duracion);
                                txtDireccion.setText("Dirección: " + direccion);

                                // Elimina la ruta anterior si existe
                                if (currentPolyline != null) {
                                    mMap.clear();
                                }

                                // Dibuja la nueva ruta en el mapa
                                currentPolyline = new PolylineOptions()
                                        .addAll(listaPuntos)
                                        .color(Color.rgb(139, 69, 19)) // Color café
                                        .width(10f);
                                mMap.addPolyline(currentPolyline);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionActual, 15));
                            } else {
                                Toast.makeText(MainActivity.this, "No se encontraron rutas.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error al obtener direcciones.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e("API_DIRECCIONES", "Error al conectar con la API de direcciones", t);
                    }
                });
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            poly.add(new LatLng(lat / 1E5, lng / 1E5));
        }
        return poly;
    }

    private void enviarUbicacion(double lat, double lng) {
        if (id_repartidor != -1 && socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("id", id_repartidor);
                data.put("lat", lat);
                data.put("lng", lng);
                socket.emit("ubicacion", data);
                Log.d("SOCKET", "Ubicación enviada: " + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("SOCKET", "Socket no conectado o id_repartidor no válido");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (socket != null) {
            socket.disconnect();
        }
    }

    private void cargarPedidos(API api) {
        TextView txtPedido1 = findViewById(R.id.txtPedido1);
        TextView txtPedido2 = findViewById(R.id.txtPedido2);
        TextView txtPedido3 = findViewById(R.id.txtPedido3);
        ImageButton pedidoButton1 = findViewById(R.id.pedidoButton1);
        ImageButton pedidoButton2 = findViewById(R.id.pedidoButton2);
        ImageButton pedidoButton3 = findViewById(R.id.pedidoButton3);

        txtPedido1.setText("");
        txtPedido2.setText("");
        txtPedido3.setText("");

        // OBTENCIÓN DE PEDIDOS después de obtener los datos del repartidor
        api.pedidos(id_repartidor, 3).enqueue(new retrofit2.Callback<DeliveryResponse>() {
            @Override
            public void onResponse(Call<DeliveryResponse> call, Response<DeliveryResponse> response) {
                estadoPedidos = new ArrayList<>();
                DeliveryResponse deliveryResponse = response.body();
                String[] deliveries = deliveryResponse.getDeliveries();

                if(deliveries != null && deliveries.length > 0) {
                    for (String id : deliveries) {
                        enlistarPedidos(Integer.parseInt(id));
                    }
                }


                if (response.isSuccessful() && response.body() != null) {
                    pedidoButton1.setOnClickListener(v -> {
                        resetButtonColors();
                        pedidoButton1.setColorFilter(Color.GRAY);
                        String text = txtPedido1.getText().toString();
                        int id_pedido = extractIdFromText(text);
                        realizarConsulta(api, id_pedido);
                    });

                    pedidoButton2.setOnClickListener(v -> {
                        resetButtonColors();
                        pedidoButton2.setColorFilter(Color.GRAY);
                        String text = txtPedido2.getText().toString();
                        int id_pedido = extractIdFromText(text);
                        realizarConsulta(api, id_pedido);
                    });

                    pedidoButton3.setOnClickListener(v -> {
                        resetButtonColors();
                        pedidoButton3.setColorFilter(Color.GRAY);
                        String text = txtPedido3.getText().toString();
                        int id_pedido = extractIdFromText(text);
                        realizarConsulta(api, id_pedido);
                    });


                    String status = deliveryResponse.getStatus();

                    if (deliveries != null && deliveries.length > 0) {
                        txtPedido1.setText(deliveries.length > 0 ? "Pedido con ID: " + deliveries[0] : "");
                        txtPedido2.setText(deliveries.length > 1 ? "Pedido con ID: " + deliveries[1] : "");
                        txtPedido3.setText(deliveries.length > 2 ? "Pedido con ID: " + deliveries[2] : "");

                        pedidoButton1.setVisibility(deliveries.length > 0 ? View.VISIBLE : View.INVISIBLE);
                        pedidoButton2.setVisibility(deliveries.length > 1 ? View.VISIBLE : View.INVISIBLE);
                        pedidoButton3.setVisibility(deliveries.length > 2 ? View.VISIBLE : View.INVISIBLE);
                    } else {
                        txtPedido1.setText("No hay pedidos disponibles");
                        txtPedido2.setText("");
                        txtPedido3.setText("");

                        pedidoButton1.setVisibility(View.INVISIBLE);
                        pedidoButton2.setVisibility(View.INVISIBLE);
                        pedidoButton3.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error al obtener los pedidos", Toast.LENGTH_SHORT).show();
                }
            }

            private void resetButtonColors() {
                pedidoButton1.setColorFilter(Color.parseColor("#ff5100"));
                pedidoButton2.setColorFilter(Color.parseColor("#ff5100"));
                pedidoButton3.setColorFilter(Color.parseColor("#ff5100"));
            }


            @Override
            public void onFailure(Call<DeliveryResponse> call, Throwable t) {
                Log.d("URL", call.request().url().toString()); // Imprime la URL incluso en caso de fallo
                Log.e("PEDIDOS", "Error al conectarse con el servidor: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error al conectarse con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void enlistarPedidos(int id_pedido) {
        estadoPedidos.add(new int[]{id_pedido, 0});
        Log.d("PEDIDO_ENLISTADO", "Pedido con ID: " + id_pedido + " enlistado.");
    }

    public void completarPedido(API api, int id_pedido) {
        for(int i=0; i < estadoPedidos.size(); ++i) {
            if(estadoPedidos.get(i)[0] == id_pedido) {
                estadoPedidos.get(i)[1] = 1; // Cambia el estado a completado
                break;
            }
        }

        // Si falta un pedido por completar, no se puede continuar
        for (int i=0; i < estadoPedidos.size(); ++i) {
            if(estadoPedidos.get(i)[1] == 0) {
                return;
            }
        }

        // Si todos los pedidos están completados, puedes avanzar
        cargarPedidos(api);
    }
}