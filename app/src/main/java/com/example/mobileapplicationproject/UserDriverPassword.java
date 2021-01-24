package com.example.mobileapplicationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mobileapplicationproject.model.EditAccount;
import com.example.mobileapplicationproject.model.GetProfile;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDriverPassword extends AppCompatActivity {
    APIInterface apiInterface;
    StringBuilder sb;
    private TextInputEditText Email, Password;
    String email, password;
    private static final String FILE_NAME = "example.txt";
    Button update;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sb = new StringBuilder();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        setContentView(R.layout.user_driver_password);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = Email.getText().toString();
                password = Password.getText().toString();

                update();
            }
        });
        getInfo();
    }

    public void profile_text(View view) {
        Intent intent = new Intent(this, ProfileTabbed.class);
        startActivity(intent);
    }

    private void getInfo() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }

            Call<GetProfile> getProfileCall =  apiInterface.getProfile(sb.toString());
            getProfileCall.enqueue(new Callback<GetProfile>() {
                @Override
                public void onResponse(Call<GetProfile> call, Response<GetProfile> response) {
                    if (!response.isSuccessful()) {

                        APIError apiError = ErrorUtils.parseError(response);
                        Toast.makeText(getApplicationContext(), "error "+ apiError.getMessage(), Toast.LENGTH_LONG).show();

                        return;
                    }
                    GetProfile getProfile = response.body();
                    Email.setText(getProfile.getEmail());

                }

                @Override
                public void onFailure(Call<GetProfile> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),  "a" +t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private void update() {
        EditAccount editAccount = new EditAccount(email, password);
        Call<EditAccount> calllEditAccount = apiInterface.editAccount(sb.toString(),editAccount);
        calllEditAccount.enqueue(new Callback<EditAccount>() {
            @Override
            public void onResponse(Call<EditAccount> call, Response<EditAccount> response) {
                if (!response.isSuccessful()) {

                    APIError apiError = ErrorUtils.parseError(response);
                    Toast.makeText(getApplicationContext(), "error "+ apiError.getMessage(), Toast.LENGTH_LONG).show();

                    return;
                }
                Intent intent = new Intent(getApplicationContext(), UserDriverPassword.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<EditAccount> call, Throwable t) {

            }
        });

    }
}