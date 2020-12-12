package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class UserLocationForm extends AppCompatActivity {
    private TextInputEditText itemText, location;
    ArrayList<String> itemList;
    ArrayAdapter<String> adapter;
    Button addEmail, setProfile;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainMenuForm.class);
                intent.putExtra("location", location.getText().toString());
                intent.putStringArrayListExtra("email", itemList);
                startActivity(intent);

            }
        });
        lv.setAdapter(adapter);
    }

    public void Set(View view) {
        Intent intent = new Intent(this, MainMenuForm.class);
        startActivity(intent);
    }
}