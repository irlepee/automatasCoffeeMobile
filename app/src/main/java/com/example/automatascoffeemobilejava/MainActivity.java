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
import com.example.automatascoffeemobilejava.data.requests.DataRequest;
import com.example.automatascoffeemobilejava.data.requests.DeliveryRequest;
import com.example.automatascoffeemobilejava.data.requests.LoginRequest;
import com.example.automatascoffeemobilejava.data.responses.DataResponse;
import com.example.automatascoffeemobilejava.data.responses.DeliveryResponse;
import com.example.automatascoffeemobilejava.data.responses.LoginResponse;
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

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//---------------COSAS PENDIENTES---------------
//Revisar lo del campo de la contraseña, el PASSWORD TRUE me arroja que es deprecated, cehcar eso
//PONER SOMBRAS

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    //-METODO PRINCIPAL-
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //---INICIO DE LA APLICACIÓN---
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


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
        ConstraintLayout topCardInfoButtons = findViewById(R.id.topCardInfoButtons);
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        ImageButton imageButton3 = findViewById(R.id.imageButton3);
        ImageButton imageButton5 = findViewById(R.id.imageButton5);
        ImageButton topCardUserButton = findViewById(R.id.topCardUserButton);
        ConstraintLayout infoCard = findViewById(R.id.infoCard);
        Button infoCardCloser = findViewById(R.id.infoCardCloser);
        ConstraintLayout completeCard = findViewById(R.id.completeCard);
        Button completeButton = findViewById(R.id.completeButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button completeCardButton = findViewById(R.id.completeCardButton);

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

                        //El cuerpo es un booleano entonces aquí revisa la condición de este
                        if (loginResponse.isSuccess()) {
                            Toast.makeText(MainActivity.this, loginResponse.getStatus(), Toast.LENGTH_SHORT).show();
                            int id = loginResponse.getId();
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
                            //Vibra
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(100); // 100 ms
                            }
                            //Oculta el teclado
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
                            //Vibra
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(100); // 100 ms
                            }
                        }
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error al conectarse con el servidor", Toast.LENGTH_SHORT).show();
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

        //boton para confirmar el completado del pedidoq
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

                    int id_repartidor = data.getId();

                    // OBTENCIÓN DE PEDIDOS después de obtener los datos del repartidor
                    api.pedidos(id_repartidor, 3).enqueue(new retrofit2.Callback<DeliveryResponse>() {
                        @Override
                        public void onResponse(Call<DeliveryResponse> call, Response<DeliveryResponse> response) {
                            Log.d("URL", call.request().url().toString()); // Imprime la URL antes de procesar la respuesta

                            if (response.isSuccessful() && response.body() != null) {
                                DeliveryResponse deliveryResponse = response.body();
                                String status = deliveryResponse.getStatus();
                                String[] deliveries = deliveryResponse.getDeliveries();

                                txtPedido1.setText("Pedido con id: " + (deliveries.length > 0 ? deliveries[0] : "No hay pedidos"));
                                txtPedido2.setText("Pedido con id: " + (deliveries.length > 1 ? deliveries[1] : "No hay pedidos"));
                                txtPedido3.setText("Pedido con id: " + (deliveries.length > 2 ? deliveries[2] : "No hay pedidos"));
                            } else {
                                Toast.makeText(MainActivity.this, "Error al obtener los pedidos", Toast.LENGTH_SHORT).show();
                            }
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
    }





}
