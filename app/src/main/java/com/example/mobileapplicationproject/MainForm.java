package com.example.mobileapplicationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapplicationproject.model.AccountType;
import com.example.mobileapplicationproject.model.Login;
import com.example.mobileapplicationproject.model.Post;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainForm extends AppCompatActivity {
    private static final String FILE_NAME = "example.txt";
    APIInterface apiInterface;
    String email, password;
    TextInputEditText emailInput, passwordInput;
    Button loginButton;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_form);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        emailInput = (TextInputEditText) findViewById(R.id.emailInput);
        passwordInput = (TextInputEditText) findViewById(R.id.passwordInput);
        loginButton = (Button) findViewById(R.id.loginMain);
        textViewResult = findViewById(R.id.text_view_result);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                if(email.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please Input Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( password.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please Input Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginUser();
            }
        });


    }

    private void loginUser() {
        Login login = new Login(email, password);
        Call<Login> call = apiInterface.loginUser(login);
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (!response.isSuccessful()) {

                    APIError apiError = ErrorUtils.parseError(response);
                    Toast.makeText(getApplicationContext(), "error "+ apiError.getMessage(), Toast.LENGTH_LONG).show();

                    return;
                }

                Login postResponse = response.body();

                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_LONG).show();

                Intent myIntent = new Intent();
                if (postResponse.getAccountType().matches("User")) {
                    myIntent = new Intent(MainForm.this, MainMenuForm.class);
                }
                if (postResponse.getAccountType().matches("Employee")) {
                    myIntent = new Intent(MainForm.this, MainMenuFormEmployee.class);
                }
                if (postResponse.getAccountType().matches("Driver")) {
                    myIntent = new Intent(MainForm.this, MainMenuFormDriverr.class);
                }
                FileOutputStream fos = null;
                try {
                    fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                    fos.write(postResponse.getToken().getBytes());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                startActivity(myIntent);
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Toast.makeText(MainForm.this, "error "+ t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });


    }

    public void signup_main_txt(View view) {
        Intent intent = new Intent(this,RegisterForm.class);
        startActivity(intent);
    }

    public void loginButton(View view) {
        Intent intent = new Intent(this, MainMenuForm.class);
        startActivity(intent);
    }
}