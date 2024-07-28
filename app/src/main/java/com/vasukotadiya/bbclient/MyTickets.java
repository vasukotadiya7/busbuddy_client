package com.vasukotadiya.bbclient;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vasukotadiya.bbclient.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MyTickets extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("BusBuddy");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        RecyclerView recyclerView = findViewById(R.id.TicketsRecyclerView);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseUser mauth = FirebaseAuth.getInstance().getCurrentUser();
        assert mauth != null;
        String User = mauth.getUid();

        DatabaseReference Ticket = FirebaseDatabase.getInstance().getReference().child("Tickets").child("UserSideCheck").child(User);


        Ticket.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
                String BusNumber = Objects.requireNonNull(snapshot.child("BusNumber").getValue()).toString();
                String BusType = Objects.requireNonNull(snapshot.child("BusType").getValue()).toString();
                String PassengerName = Objects.requireNonNull(snapshot.child("PassengerName").getValue()).toString();
                String PassengerPhone = Objects.requireNonNull(snapshot.child("PassengerPhone").getValue()).toString();
                String Date = Objects.requireNonNull(snapshot.child("Date").getValue()).toString();
                String From = Objects.requireNonNull(snapshot.child("From").getValue()).toString();
                String TO = Objects.requireNonNull(snapshot.child("TO").getValue()).toString();
                String StartTime = Objects.requireNonNull(snapshot.child("StartTime").getValue()).toString();
                String EndTime = Objects.requireNonNull(snapshot.child("EndTime").getValue()).toString();
                String SeatNo = Objects.requireNonNull(snapshot.child("SeatNo").getValue()).toString();



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
}
