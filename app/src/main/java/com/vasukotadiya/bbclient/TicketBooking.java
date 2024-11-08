package com.vasukotadiya.bbclient;


import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.vasukotadiya.bbclient.adapters.PassengerListAdapter;
import com.vasukotadiya.bbclient.adapters.ReviewsAdapter;
import com.vasukotadiya.bbclient.model.PassengerInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vasukotadiya.bbclient.model.ReviewModel;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import dev.shreyaspatil.easyupipayment.EasyUpiPayment;
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener;

//import dev.shreyaspatil.easyupipayment.EasyUpiPayment;
//import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener;
//import dev.shreyaspatil.easyupipayment.model.PaymentApp;
//import dev.shreyaspatil.easyupipayment.model.TransactionDetails;

public class TicketBooking extends AppCompatActivity implements PaymentResultListener {

    private TextView BT_BusNumber, BK_Date, BT_FromLocation, BT_ToLocation, BT_StartTime, BT_EndTime, BT_SeatAvailable, BT_BusType, BT_TotalPrice;
    private EditText BT_PassengerName, BT_PhoneNo;
    private Button btnPay, btnAddPassenger;
    private RecyclerView recyclerView,customerReview;
    private ArrayList<PassengerInfo> arrayList;
    private ArrayList<ReviewModel> reviewList;

    private TextView TV_CP;
    private String BusNumber;
    private String Date;
    private String FromLocation;
    private String ToLocation;
    private String startTime;
    private String endTime;
    private String seatAvailable;
    private String BusType;
    private String TicketPrice;


    private String TransactionID;

    private final String UPI = "paytmqr1y11356oue@paytm";
    private EasyUpiPayment easyUpiPayment;

    private boolean isPassengerInfoExpanded = false;
    private boolean isCancellationPolicyExpanded = false;
    private boolean isCustomerReviewsExpanded = false;

    private DatabaseReference seatNo;
    private int no_of_seat;

    PassengerInfo passengerInfo;
    String str_total_price;

    private ArrayList<Integer> selected;
    private HashMap<String,Integer> booked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_booking);


        Instantiate();
        GetStringFromIntent();
        ProgressDialog configure = new ProgressDialog(this);
        configure.setCancelable(false);
        configure.setTitle("Configuring Environment");
        configure.show();
        SetStringToTextView(configure);
        //GetNoOFPlace();
        calculate_price();

        //Here I have created arraylist to call the data
        arrayList = new ArrayList<>();

        PassengerListAdapter adapter = new PassengerListAdapter(arrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        reviewList=new ArrayList<>();


//        TV_CP.setOnClickListener(View-> ShowCPDialog());


        btnAddPassenger.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                if(selected.size()-1<=arrayList.size()){
                    Toast.makeText(TicketBooking.this, "Selected seats passenger added already", Toast.LENGTH_SHORT).show();
                    return;
                }
                String passengerName = BT_PassengerName.getText().toString();
                String phoneNo = BT_PhoneNo.getText().toString();
                if (!passengerName.equals("") && !phoneNo.equals("")) {
                    passengerInfo = new PassengerInfo();
                    passengerInfo.setPassengerName(passengerName);
                    passengerInfo.setPhoneNumber(phoneNo);
                    arrayList.add(passengerInfo);
                    adapter.notifyDataSetChanged();
                    BT_PassengerName.setText("");
                    BT_PhoneNo.setText("");
//                    calculate_price();
                } else
                    Toast.makeText(TicketBooking.this, "Please fill Passenger Details", Toast.LENGTH_SHORT).show();
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!arrayList.isEmpty()) {
                    no_of_seat = Integer.parseInt(seatAvailable);
                    if (no_of_seat <= 0) {
                        Toast.makeText(TicketBooking.this, "Sorry seats are full", Toast.LENGTH_LONG).show();
                    } else {
                        if(selected.size()-1!=arrayList.size()){
                            Toast.makeText(TicketBooking.this, "Please enter all passenger details", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        MakePayment();
                    }

                } else {
                    Toast.makeText(TicketBooking.this, "Please select some places", Toast.LENGTH_LONG).show();
                }


            }
        });

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Reviews").child(BusNumber);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ReviewModel reviewModel=snapshot.getValue(ReviewModel.class);
                try {

                GenericTypeIndicator<HashMap<String,String>> genericTypeIndicator=new GenericTypeIndicator<HashMap<String,String>>() {};
                final HashMap<String,String> reviews=Objects.requireNonNull(snapshot.getValue(genericTypeIndicator));

                for (Map.Entry<String, String> entry : reviews.entrySet()) {
                    reviewList.add(new ReviewModel(entry.getKey(), entry.getValue()));
                }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                    return;
                }

                ReviewsAdapter reviewsAdapter=new ReviewsAdapter(reviewList);
                customerReview.setHasFixedSize(true);
                customerReview.setLayoutManager(new LinearLayoutManager(TicketBooking.this));
                customerReview.setAdapter(reviewsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void MakePayment() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        assert user != null;
//        String UserId = user.getUid();


//        Date c = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
//        String transactionId = df.format(c);;
//
//
//        PaymentApp paymentApp = PaymentApp.ALL;
//
//
//        // START PAYMENT INITIALIZATION
//        EasyUpiPayment.Builder builder = new EasyUpiPayment.Builder(this)
//                .with(paymentApp)
//                .setPayeeVpa(UPI)
//                .setPayeeMerchantCode("QsVxcc73555252784747")
//                .setPayeeName("BusBuddy")
//                .setTransactionId(transactionId)
//                .setTransactionRefId(transactionId)
//                .setDescription("Ticket Payment")
//                .setAmount(str_total_price+".00");
//        // END INITIALIZATION
//        try {
//            // Build instance
//            easyUpiPayment = builder.build();
//
//            // Register Listener for Events
//            easyUpiPayment.setPaymentStatusListener(this);
//
//            // Start payment / transaction
//            easyUpiPayment.startPayment();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//            Toast.makeText(this, "Error: "+ exception.getMessage(), Toast.LENGTH_LONG).show();
//        }


        int amount = Math.round(Float.parseFloat(str_total_price) * 100);

        Checkout checkout = new Checkout();

        // set your id as below
        checkout.setKeyID("rzp_test_mg1CDXoVq8oXYi");

        // set image
        checkout.setImage(R.drawable.bus);

        JSONObject object = new JSONObject();
        try {
            // to put name
            object.put("name", "BusBuddy");

            // put description
            object.put("description", "Test payment");

            // to set theme color
            object.put("theme.color", "");

            // put the currency
            object.put("currency", "INR");

            // put amount
            object.put("amount", amount);

            // put mobile number
            object.put("prefill.contact", "6353359477");

            // put email
            object.put("prefill.email", "vasukotadiya224@gmail.com");

            // open razorpay to checkout activity
            checkout.open(TicketBooking.this, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private void Instantiate() {
        Toolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setTitle("BusBuddy");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        BT_BusNumber = findViewById(R.id.BT_BusNumber);
        BK_Date = findViewById(R.id.BT_date);
        BT_FromLocation = findViewById(R.id.BT_FromLocation);
        BT_ToLocation =  findViewById(R.id.BT_ToLocation);
        BT_StartTime = findViewById(R.id.BT_StartTime);
        BT_EndTime = findViewById(R.id.BT_EndTime);
        BT_SeatAvailable = findViewById(R.id.BT_SeatAvailable);
        BT_BusType = findViewById(R.id.BT_BusType);
        BT_PassengerName = findViewById(R.id.BT_PassengerName);
        BT_PhoneNo = findViewById(R.id.BT_passengerPhone);
        btnAddPassenger = findViewById(R.id.BT_AddPassenger);
        recyclerView = findViewById(R.id.BT_RecyclerView);
        customerReview=findViewById(R.id.PR_recyclerview);
        BT_TotalPrice = findViewById(R.id.BT_TotalPrice);
        btnPay = findViewById(R.id.BT_PayBtn);

//        TV_CP=findViewById(R.id.tv_cancelpolicy);

        // Toggle Passenger Information
        findViewById(R.id.passenger_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPassengerInfoExpanded = !isPassengerInfoExpanded;
                findViewById(R.id.passenger_info_card).setVisibility(
                        isPassengerInfoExpanded ? View.VISIBLE : View.GONE);
            }
        });

        // Toggle Cancellation Policy
        findViewById(R.id.cancellation_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancellationPolicyExpanded = !isCancellationPolicyExpanded;
                findViewById(R.id.cancellation_policy_card).setVisibility(
                        isCancellationPolicyExpanded ? View.VISIBLE : View.GONE);
            }
        });

        // Toggle Customer Reviews
        findViewById(R.id.customer_reviews).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCustomerReviewsExpanded = !isCustomerReviewsExpanded;
                findViewById(R.id.customer_reviews_card).setVisibility(
                        isCustomerReviewsExpanded ? View.VISIBLE : View.GONE);
            }
        });
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
//        booked=getIntent().getIntegerArrayListExtra("Booked");
        booked= (HashMap<String, Integer>) getIntent().getSerializableExtra("Booked");
        selected=getIntent().getIntegerArrayListExtra("Selected");
        String seatNoIDReference = FromLocation + ToLocation;
        seatNo = FirebaseDatabase.getInstance().getReference().child("Buses").child(Date).child(seatNoIDReference).child(BusNumber);

    }


    @SuppressLint("SetTextI18n")
    private void SetStringToTextView(ProgressDialog configure) {
        BT_BusNumber.setText(BusNumber);
        BK_Date.setText(Date);
        BT_FromLocation.setText(FromLocation);
        BT_ToLocation.setText(ToLocation);
        BT_StartTime.setText(startTime);
        BT_EndTime.setText(endTime);
        BT_SeatAvailable.setText(seatAvailable);
        BT_BusType.setText(BusType);
        BT_TotalPrice.setText(TicketPrice+"/-");
        configure.dismiss();
    }

    private void ShowCPDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Cancellation Policy").setCancelable(false).setMessage("For All Buses It Is Same");
        LinearLayout linearLayout=new LinearLayout(this);
        final ImageView imageView=new ImageView(this);

        imageView.setImageResource(R.drawable.cp);
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.addView(imageView);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("I AGREE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    @SuppressLint("SetTextI18n")
    private void calculate_price() {
//        int number_of_traveller = arrayList.size();
        int number_of_traveller = selected.size()-1;
        int Price = Integer.parseInt(TicketPrice);
        int total_price = Price * number_of_traveller;

        str_total_price = Integer.toString(total_price);
        BT_TotalPrice.setText(str_total_price+"/-");
    }

    private void Book_Ticket() {
        try{


        if (no_of_seat <= 0) {
            Toast.makeText(this, "Sorry, Seats Not Available...", Toast.LENGTH_LONG).show();
        } else {


            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            String User = user.getUid();
            String BusInfo = BusNumber + "," + FromLocation + "," + ToLocation + "," + startTime + "," + endTime;


            DatabaseReference AdminTicketList = FirebaseDatabase.getInstance().getReference().
                    child("Tickets").child("AdminSideCheck").child(Date).child(BusInfo);

            DatabaseReference usersTicketList = FirebaseDatabase.getInstance().getReference().
                    child("Tickets").child("UserSideCheck").child(User);


            //Here is the code to store AdminTicketList
            int Length = arrayList.size();
            for (int a = 0; a < Length; a++) {
                HashMap<String, Object> passengerInformation = new HashMap<>();
                passengerInformation.put("PassengerName", arrayList.get(a).getPassengerName());
                passengerInformation.put("PassengerPhone", arrayList.get(a).getPhoneNumber());
//                String seatNo =String.valueOf(Integer.parseInt(seatAvailable)-a);
                String seatNo = String.valueOf(selected.get(a + 1));
                passengerInformation.put("PassengerSeatNo", seatNo);
                passengerInformation.put("Price", TicketPrice);
                passengerInformation.put("TransactionID", TransactionID);
                passengerInformation.put("isCanceled", false);
                passengerInformation.put("RefundAmount",0);
                AdminTicketList.child(User + (seatNo)).updateChildren(passengerInformation);
            }


            //Here is the code to store UserTicketList
            for (int a = 0; a < Length; a++) {
                HashMap<String, Object> ticketInformation = new HashMap<>();
                ticketInformation.put("PassengerName", arrayList.get(a).getPassengerName());
                ticketInformation.put("PassengerPhone", arrayList.get(a).getPhoneNumber());
                ticketInformation.put("BusNo", BusNumber);
                ticketInformation.put("Date", Date);
                ticketInformation.put("FromLocation", FromLocation);
                ticketInformation.put("ToLocation", ToLocation);
                ticketInformation.put("StartTime", startTime);
                ticketInformation.put("EndTime", endTime);
                ticketInformation.put("BusType", BusType);
//                String seatNo =String.valueOf(Integer.parseInt(seatAvailable)-a);
                String seatNo = String.valueOf(selected.get(a + 1));
                ticketInformation.put("SeatNo", seatNo);
                ticketInformation.put("Price", TicketPrice);
                ticketInformation.put("TransactionID", TransactionID);
                ticketInformation.put("isCanceled", false);
                ticketInformation.put("isReviewed",false);
                usersTicketList.child(BusNumber + FromLocation + ToLocation + seatNo).updateChildren(ticketInformation);
            }

//            HashMap<String, Object> bookings = new HashMap<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                booked.forEach((k,v)->{
//                    try{
//                        bookings.put(String.valueOf(v), v);
//                    }catch (IndexOutOfBoundsException e){
//                        e.printStackTrace();
//                    }
//                    catch (NullPointerException ex){
//                        ex.printStackTrace();
//                    }
//                });
                selected.forEach((v) -> {
                    booked.put(String.valueOf(v),v);
                });
            }

            Log.d(TAG, "Book_Ticket: " + booked.toString());
            Toast.makeText(this, booked.toString(), Toast.LENGTH_SHORT).show();
            seatNo.child("Booked").setValue(booked);


            int new_val = Integer.parseInt(seatAvailable) - arrayList.size();
            String st = Integer.toString(new_val);
            Map<String, Object> seat = new HashMap<>();
            seat.put("NumberOfSeat", st);
            seatNo.updateChildren(seat).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent in = new Intent(TicketBooking.this, MyTickets.class);
                        startActivity(in);
                        finish();
                    }
                }
            });


        }

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Book_Ticket: "+e);
        }


    }

//    @Override
//    public void onTransactionCancelled() {
//        Toast.makeText(this, "Cancelled By User", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onTransactionCompleted(@NonNull TransactionDetails transactionDetails) {
//        // Transaction Completed
//        Log.d("TransactionDetails", transactionDetails.toString());
//        TransactionDetails = transactionDetails.toString();
//        switch (transactionDetails.getTransactionStatus()) {
//            case SUCCESS:
//                onTransactionSuccess();
//                break;
//            case FAILURE:
//                onTransactionFailed();
//                break;
//            case SUBMITTED:
//                onTransactionSubmitted();
//                break;
//        }
//    }

//    private void onTransactionSuccess() {
//        // Payment Success
//        Toast.makeText(this, "Transaction Completed Successfully...", Toast.LENGTH_LONG).show();
//        Book_Ticket();
//    }
//
//    private void onTransactionSubmitted() {
//        // Payment Pending
//        Toast.makeText(this, "Pending | Submitted", Toast.LENGTH_LONG).show();
//    }
//
//    private void onTransactionFailed() {
//        // Payment Failed
//        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
//
//    }

    @Override
    public void onPaymentSuccess(String s) {

        Toast.makeText(this, "Transaction Completed Successfully...", Toast.LENGTH_LONG).show();

        TransactionID=s;
        Book_Ticket();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Messedup!!!!", Toast.LENGTH_SHORT).show();
    }
}