package com.vasukotadiya.bbclient;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vasukotadiya.bbclient.adapters.MyTicketAdapter;
import com.vasukotadiya.bbclient.model.TicketModel;

import java.util.Objects;

public class MyTickets extends AppCompatActivity {

    private RecyclerView recyclerView;
    MyTicketAdapter adapter;

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

        FirebaseUser mauth = FirebaseAuth.getInstance().getCurrentUser();
        assert mauth != null;
        String User = mauth.getUid();

        DatabaseReference Ticket = FirebaseDatabase.getInstance().getReference().child("Tickets").child("UserSideCheck").child(User);

        recyclerView = findViewById(R.id.TicketsRecyclerView);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerOptions<TicketModel> options=
                new FirebaseRecyclerOptions.Builder<TicketModel>()
                        .setQuery(Ticket,TicketModel.class)
                                .build();

        adapter=new MyTicketAdapter(options);
        recyclerView.setAdapter(adapter);

    }
        @Override
        protected void onStart(){
            super.onStart();
            adapter.startListening();
        }

        @Override
        protected  void onStop(){
            super.onStop();
            adapter.startListening();
        }

}
