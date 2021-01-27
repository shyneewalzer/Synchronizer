package com.example.mobileapplicationproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.DebugMode;
import com.example.mobileapplicationproject.model.Post;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterForm extends AppCompatActivity {

    DebugMode dm = new DebugMode();

    private TextView textViewResult;
    private RadioButton radioButton;
    APIInterface apiInterface;
    String email, password, accountType;
    TextInputEditText emailInput, passwordInput;
    RadioGroup accountRadio;
    Button signUpRegister;
    TextView txt_usertypeinfo;

    ConstraintLayout lo_main;
    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        emailInput = (TextInputEditText) findViewById(R.id.emailInput);
        passwordInput = (TextInputEditText) findViewById(R.id.passwordInput);
        accountRadio = (RadioGroup) findViewById(R.id.radioButton);
        signUpRegister = (Button) findViewById(R.id.signUpRegister);
        lo_main = findViewById(R.id.lo_main);
        pbar = findViewById(R.id.pbar);
        txt_usertypeinfo = findViewById(R.id.txt_usertypeinfo);

        signUpRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                // get selected radio button from radioGroup
                // find the radiobutton by returned id
                if(email.matches(""))
                {
                    Toast.makeText(getApplicationContext(), "Please Input Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( password.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please Input Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (accountRadio.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(RegisterForm.this, "Please Select Type ", Toast.LENGTH_LONG).show();
                    return;
                }

                int selectedId = accountRadio.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);
                accountType = radioButton.getText().toString();



                lo_main.setVisibility(View.GONE);
                pbar.setVisibility(View.VISIBLE);

                createPost();
            }
        });

        accountRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId==R.id.radio_button_left_register)
                {
                    txt_usertypeinfo.setText(R.string.indiv_info);
                    txt_usertypeinfo.setVisibility(View.VISIBLE);
                }
                else if(checkedId==R.id.radio_button_center_register)
                {
                    txt_usertypeinfo.setText(R.string.driver_info);
                    txt_usertypeinfo.setVisibility(View.VISIBLE);
                }
                else if(checkedId==R.id.radio_button_right_register)
                {
                    txt_usertypeinfo.setText(R.string.estab_info);
                    txt_usertypeinfo.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void createPost() {

        Post post = new Post(email, password, accountType);

        Call<Post> call = apiInterface.createPost(post);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {

                    APIError apiError = ErrorUtils.parseError(response);
                    Toast.makeText(RegisterForm.this, "error "+ apiError.getMessage(), Toast.LENGTH_LONG).show();

                    lo_main.setVisibility(View.VISIBLE);
                    pbar.setVisibility(View.GONE);

                    return;
                }
                Toast.makeText(RegisterForm.this, "We send a verification to your email address", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(RegisterForm.this, Login.class);
                startActivity(myIntent);

                lo_main.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(RegisterForm.this, t.getMessage(), Toast.LENGTH_LONG).show();
                lo_main.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
            }
        });

    }

    public void login_register_txt(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void back_arrow_register(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }


}