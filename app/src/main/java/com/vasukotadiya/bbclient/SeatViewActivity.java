package com.vasukotadiya.bbclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SeatViewActivity extends AppCompatActivity {
    private ImageView[] seats=new ImageView[41];
    private String BusNumber,Date,FromLocation,ToLocation,startTime,endTime,seatAvailable,BusType,TicketPrice;
    private AppCompatButton bookSeat;
    private HashMap<String,Integer> booked= new HashMap<String,Integer>();
    private ArrayList<Integer> selected=new ArrayList<>();
    private ArrayList<Integer> alreadybkd=new ArrayList<>(41);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_view);
        Instantiate();
        GetStringFromIntent();
        MakeColorSeat();
    }

    private void Instantiate() {
        selected.add(0,0);
        alreadybkd.add(0,0);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("BusBuddy");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        bookSeat=findViewById(R.id.bookbtn);
        seats[1]=findViewById(R.id.s1);
        seats[2]=findViewById(R.id.s2);
        seats[3]=findViewById(R.id.s3);
        seats[4]=findViewById(R.id.s4);
        seats[5]=findViewById(R.id.s5);
        seats[6]=findViewById(R.id.s6);
        seats[7]=findViewById(R.id.s7);
        seats[8]=findViewById(R.id.s8);
        seats[9]=findViewById(R.id.s9);
        seats[10]=findViewById(R.id.s10);
        seats[11]=findViewById(R.id.s11);
        seats[12]=findViewById(R.id.s12);
        seats[13]=findViewById(R.id.s13);
        seats[14]=findViewById(R.id.s14);
        seats[15]=findViewById(R.id.s15);
        seats[16]=findViewById(R.id.s16);
        seats[17]=findViewById(R.id.s17);
        seats[18]=findViewById(R.id.s18);
        seats[19]=findViewById(R.id.s19);
        seats[20]=findViewById(R.id.s20);
        seats[21]=findViewById(R.id.s21);
        seats[22]=findViewById(R.id.s22);
        seats[23]=findViewById(R.id.s23);
        seats[24]=findViewById(R.id.s24);
        seats[25]=findViewById(R.id.s25);
        seats[26]=findViewById(R.id.s26);
        seats[27]=findViewById(R.id.s27);
        seats[28]=findViewById(R.id.s28);
        seats[29]=findViewById(R.id.s29);
        seats[30]=findViewById(R.id.s30);
        seats[31]=findViewById(R.id.s31);
        seats[32]=findViewById(R.id.s32);
        seats[33]=findViewById(R.id.s33);
        seats[34]=findViewById(R.id.s34);
        seats[35]=findViewById(R.id.s35);
        seats[36]=findViewById(R.id.s36);
        seats[37]=findViewById(R.id.s37);
        seats[38]=findViewById(R.id.s38);
        seats[39]=findViewById(R.id.s39);
        seats[40]=findViewById(R.id.s40);

        for (int i = 1; i < seats.length; i++) {
            int finalI = i;
            seats[i].setOnClickListener(View-> SelectSeat(finalI));
        }

        bookSeat.setOnClickListener(View -> bookseat());
    }
    private void GetStringFromIntent() {
        BusNumber = getIntent().getStringExtra("BusNo");
        Date = getIntent().getStringExtra("Date");
        FromLocation = getIntent().getStringExtra("FromLocation");
        ToLocation = getIntent().getStringExtra("ToLocation");
        startTime = getIntent().getStringExtra("StartTime");
        endTime = getIntent().getStringExtra("EndTime");
        seatAvailable = getIntent().getStringExtra("NumberOfSeat");
        BusType = getIntent().getStringExtra("BusType");
        TicketPrice = getIntent().getStringExtra("Price");
        booked= (HashMap<String, Integer>) getIntent().getSerializableExtra("Booked");

    }
    private  void bookseat(){
        if(selected.size()<=1){
            Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent in =new Intent(SeatViewActivity.this,TicketBooking.class);
        in.putExtra("EndTime",endTime);
        in.putExtra("StartTime",startTime);
        in.putExtra("BusNo",BusNumber);
        in.putExtra("NumberOfSeat",seatAvailable);
        in.putExtra("BusType",BusType);
        in.putExtra("FromLocation", FromLocation);
        in.putExtra("ToLocation", ToLocation);
        in.putExtra("Date", Date);
        in.putExtra("Price",TicketPrice);
        in.putExtra("Booked",booked);
        in.putExtra("Selected",selected);
        startActivity(in);
    }
    private void SelectSeat(Integer seatno) {
        Toast.makeText(this, String.valueOf(seatno), Toast.LENGTH_SHORT).show();

        try {
            int v = alreadybkd.indexOf(seatno);
            if (v==-1){
                throw new NullPointerException("Given Direction");
            }
            Toast.makeText(this, "Already Booked!", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            int c = selected.indexOf(seatno);
            if (c == -1) {

                selected.add(seatno);
                seats[seatno].setImageResource(R.drawable.ic_baseline_event_seat_selected);
            } else {
                seats[seatno].setImageResource(R.drawable.ic_baseline_event_seat_empty);
                selected.remove(selected.indexOf(seatno));

            }


        } catch (IndexOutOfBoundsException ex) {
            int p = selected.indexOf(seatno);
            if (p == -1) {

                selected.add(seatno);
                seats[seatno].setImageResource(R.drawable.ic_baseline_event_seat_selected);
            } else {
                seats[seatno].setImageResource(R.drawable.ic_baseline_event_seat_empty);
                selected.remove(selected.indexOf(seatno));

            }
        }
    }
    private void MakeColorSeat(){
        if (booked.size()>1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                booked.forEach((k,v)-> {
                    try {

                        if (v != 0 && v!=41) {
                            alreadybkd.add(v);
                            seats[v].setImageResource(R.drawable.ic_baseline_event_seat_booked);
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}