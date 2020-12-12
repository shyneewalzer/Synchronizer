package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mobileapplicationproject.model.GetProfile;
import com.example.mobileapplicationproject.model.Login;
import com.example.mobileapplicationproject.model.ResponesTravel;
import com.example.mobileapplicationproject.model.Travel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLocationForm extends AppCompatActivity {
    private static final String FILE_NAME = "example.txt";
    private TextInputEditText itemText, location;
    APIInterface apiInterface;
    ArrayList<String> itemList;
    ArrayAdapter<String> adapter;
    Button addEmail, setProfile;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        setContentView(R.layout.activity_user_location_form);
        lv = (ListView) findViewById(R.id.listview);
        itemText = (TextInputEditText) findViewById(R.id.emailRegister);
        location = (TextInputEditText) findViewById(R.id.location);
        addEmail = (Button) findViewById(R.id.addEmail);
        setProfile = (Button) findViewById(R.id.setProfile);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_menu);
        bottomNavigationView.setSelectedItemId(R.id.location);
        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(UserLocationForm.this , android.R.layout.simple_list_item_multiple_choice,itemList);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.location:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext()
                                , UserProfileViewForm.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext()
                                , MainMenuForm.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.history:
                        startActivity(new Intent(getApplicationContext()
                                , UserTravelForm.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.logout:
                        startActivity(new Intent(getApplicationContext()
                                ,MainForm.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        View.OnClickListener addlistner = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemList.add(itemText.getText().toString());
                itemText.setText("");
                adapter.notifyDataSetChanged();
            }
        };

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                SparseBooleanArray positionchecker = lv.getCheckedItemPositions();

                int count = lv.getCount();
                for(int item = count-1; item>=0; item--){

                    if(positionchecker.get(item)){
                        adapter.remove(itemList.get(item));
                        Toast.makeText(UserLocationForm.this,"Item Delete Successfully",Toast.LENGTH_LONG).show();
                    }
                }

                positionchecker.clear();
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        addEmail.setOnClickListener(addlistner);
        lv.setAdapter(adapter);
        setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTravel();

            }
        });
    }

    private void addTravel() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }
            Travel travel = new Travel();
            travel.setDestination(location.getText().toString());
            travel.setAccEmail(itemList);
            Call<ResponesTravel> travelCall = apiInterface.travelPost(sb.toString(),travel);
            travelCall.enqueue(new Callback<ResponesTravel>() {
                @Override
                public void onResponse(Call<ResponesTravel> call, Response<ResponesTravel> response) {
                    if (!response.isSuccessful()) {

                        APIError apiError = ErrorUtils.parseError(response);
                        Toast.makeText(getApplicationContext(), "error "+ apiError.getMessage(), Toast.LENGTH_LONG).show();

                        return;
                    }
                    ResponesTravel postResponse = response.body();
                    Intent intent = new Intent(getBaseContext(), MainMenuForm.class);
                    intent.putExtra("travelID", postResponse.getTravelId().toString());
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<ResponesTravel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "error "+ t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
//            callTravel.enqueue(new Callback<Travel>() {
//                @Override
//                public void onResponse(Call<Travel> call, Response<Travel> response) {
//                    if (!response.isSuccessful()) {
//
//                        APIError apiError = ErrorUtils.parseError(response);
//                        Toast.makeText(getApplicationContext(), "error "+ apiError.getMessage(), Toast.LENGTH_LONG).show();
//
//                        return;
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Travel> call, Throwable t) {
//                    Toast.makeText(getApplicationContext(), "error "+ t.getMessage(), Toast.LENGTH_LONG).show();
//
//                }
//            });
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

    public void Set(View view) {
        Intent intent = new Intent(this, MainMenuForm.class);
        startActivity(intent);
    }
}