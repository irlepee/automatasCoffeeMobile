package com.example.automatascoffeemobilejava;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

//Revisar lo del campo de la contraseña, el PASSWORD TRUE me arroja que es deprecated, cehcar eso
;;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Inicializar los elementos de la vista
        Button btnLogin = findViewById(R.id.btnLogin);
        EditText txtUser = findViewById(R.id.txtUser);
        EditText txtPassword = findViewById(R.id.txtPassword);
        ConstraintLayout loginCard = findViewById(R.id.loginCard);
        FrameLayout opacityCard = findViewById(R.id.opacityCard);

        // Para que la barra de estado sea transparente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            window.setStatusBarColor(Color.TRANSPARENT);
        }


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
                                    .start();
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
    }
}
