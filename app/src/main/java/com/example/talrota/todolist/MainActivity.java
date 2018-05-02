package com.example.talrota.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Dialog;
import android.text.format.DateFormat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    ArrayList<String> todoArray = new ArrayList<>();
    ListView show;
    CustomAdapter adapter;

    int day, month, year, hour, minute;
    int userDay, userMonth, userYear, userHour, userMinute;
    EditText newItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        Query queryRef = databaseReference.child("todo");
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                todoArray.add(snapshot.getValue().toString());
                show = (ListView)findViewById(R.id.todoView);
                adapter = new CustomAdapter(MainActivity.this, android.R.layout.simple_list_item_1, todoArray);
                show.setAdapter(adapter);
                show.setSelection(show.getAdapter().getCount()-1);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
        });

        setContentView(R.layout.activity_main);
        show = (ListView)findViewById(R.id.todoView);
        adapter = new CustomAdapter(MainActivity.this, android.R.layout.simple_list_item_1, todoArray);
        show.setAdapter(adapter);
        show.setSelection(show.getAdapter().getCount()-1);


        show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String firstWord = todoArray.get(position).split(" ",2)[0];
                if (firstWord.equals("call") || firstWord.equals("Call")) {
                    optionDialog(todoArray.get(position), position);
                }
                else {
                    deleteInputDialog(todoArray.get(position), position);
                }

            }
        });


    }


    public void addItemDialog(View view){
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        newItem = (EditText)subView.findViewById(R.id.dialogEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("List item");
        builder.setMessage("Insert your new item");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Calendar curr = Calendar.getInstance();
                year = curr.get(Calendar.YEAR);
                month = curr.get(Calendar.MONTH);
                day = curr.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, MainActivity.this, year, month, day);
                datePickerDialog.show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public void optionDialog(final String item, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);

        builder.setTitle("Edit item");
        builder.setMessage("Would you like to do with "+ item+ "?" );
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedItem = todoArray.get(position);
                Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                todoArray.remove(selectedItem);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference();
                Query queryRef = databaseReference.child("todo").orderByValue().equalTo(selectedItem);
                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                        snapshot.getRef().setValue(null);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }
                });
                adapter.notifyDataSetChanged();

            }
        });
        builder.setNeutralButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String leftSide = item.split("\n", 2)[0];
                String phoneNumber = leftSide.split(" ", 2)[1];
                intent.setData(Uri.parse("tel:"+ phoneNumber));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();


    }

    public void deleteInputDialog(String item, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);

        builder.setTitle("Delete item");
        builder.setMessage("Would you like to delete "+ item+ "?" );
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String selectedItem = todoArray.get(position);
                Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                todoArray.remove(selectedItem);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference();
                Query queryRef = databaseReference.child("todo").orderByValue().equalTo(selectedItem);

                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                        snapshot.getRef().setValue(null);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }
                });
                adapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        userYear = i;
        userMonth = i1 + 1;
        userDay = i2;

        Calendar curr = Calendar.getInstance();
        hour = curr.get(Calendar.HOUR_OF_DAY);
        minute = curr.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, MainActivity.this, hour, minute,
                DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        userHour = i;
        userMinute = i1;

        String finalItem = newItem.getText().toString() +"\n"+ userMonth +"/" + userDay + "/" + userYear + " " + userHour + ":" + userMinute;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("todo").push().setValue(finalItem);

        Toast.makeText(getApplicationContext(), "Item was added successfully", Toast.LENGTH_SHORT).show();

    }



}

