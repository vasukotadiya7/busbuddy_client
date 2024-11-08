package com.vasukotadiya.bbclient.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.vasukotadiya.bbclient.R;
import com.vasukotadiya.bbclient.model.TicketModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyTicketAdapter extends FirebaseRecyclerAdapter<TicketModel,MyTicketAdapter.MyTicketAdapterViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    Calendar bcalender=Calendar.getInstance();
    public MyTicketAdapter(@NonNull FirebaseRecyclerOptions<TicketModel> options) {
        super(options);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBindViewHolder(@NonNull MyTicketAdapter.MyTicketAdapterViewHolder holder, int position, @NonNull TicketModel model) {
//        TicketModel ticket = myList.get(position);
        holder.BusNo.setText(model.getBusNo());
        holder.Date.setText(model.getDate());
        holder.From.setText(model.getFromLocation());
        holder.To.setText(model.getToLocation());
        holder.StartTime.setText(model.getStartTime());
        holder.EndTime.setText(model.getEndTime());
        holder.BusType.setText(model.getBusType());
        holder.SeatNo.setText(model.getSeatNo());
        holder.Passenger.setText(model.getPassengerName());
        if(model.getisCanceled()){

            holder.Cancel.setBackgroundResource(R.color.material_dynamic_neutral30);
            holder.Cancel.setText("Canceled!!!");
            holder.Cancel.setClickable(false);
        }
        else if(model.getisReviewed()){
            holder.Cancel.setBackgroundResource(R.color.green);
            holder.Cancel.setText("Thanks for reviewing :) ");
            holder.Cancel.setClickable(false);
        }
        else {

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                        String btime = model.getStartTime();
                        Date date = null;
                        try {
                               date = sdf.parse(model.getDate());
                        } catch (ParseException e) {
                                  e.printStackTrace();
                        }
                        sdf.applyPattern("yyyy/MM/dd");
                        String bdate = sdf.format(date);
                        String cdate = sdf.format(new Date().getTime());
                        bcalender.set(Calendar.YEAR, Integer.parseInt(bdate.split("/")[0].trim()));
                        bcalender.set(Calendar.MONTH, Integer.parseInt(bdate.split("/")[1].trim()) - 1);
                        bcalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(bdate.split("/")[2].trim()));

                        if (btime.contains("PM")) {
                            bcalender.set(Calendar.AM_PM, Calendar.PM);
                            String btime1 = btime.replace("PM", "");
                            bcalender.set(Calendar.HOUR, Integer.parseInt(btime1.split(":")[0].trim()));
                            bcalender.set(Calendar.MINUTE, Integer.parseInt(btime1.split(":")[0].trim()));
                        } else {
                            bcalender.set(Calendar.AM_PM, Calendar.AM);
                            String btime1 = btime.replace("AM", "");

                            bcalender.set(Calendar.HOUR, Integer.parseInt(btime1.split(":")[0].trim()));
                            bcalender.set(Calendar.MINUTE, Integer.parseInt(btime1.split(":")[1].trim()));
                        }
                        long bt = bcalender.getTimeInMillis();
                        long ct = Calendar.getInstance().getTimeInMillis();
                        long diff = bt - ct;
                        diff = diff / 3600000;
                        if(diff<=0){
                            holder.Cancel.setBackgroundResource(R.color.green);
                            holder.Cancel.setText("Give Review ");
                            holder.Cancel.setOnClickListener(v->reviewDialog(model.getDate(),model.getFromLocation()+model.getToLocation(),model.getBusNo(),model.getSeatNo(),v.getContext()));
                        }
                        else{

                            long finalDiff = diff;

                            holder.Cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                                public void onClick(View v) {

                                Toast.makeText(v.getContext(), String.valueOf(finalDiff), Toast.LENGTH_SHORT).show();
                                String BusInfoA = model.getBusNo()+","+model.getFromLocation()+","+model.getToLocation()+","+model.getStartTime()+","+model.getEndTime();
                                String BusInfoU = model.getBusNo()+model.getFromLocation()+model.getToLocation()+model.getSeatNo();


                                CancelDialog(finalDiff,model.getPrice(),BusInfoA,BusInfoU,model.getDate(),model.getFromLocation()+model.getToLocation(),model.getBusNo(),model.getSeatNo(), v.getContext());
                                }
                            });
                        }
        }
    }




    @NonNull
    @Override
    public MyTicketAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_ticket,parent,false);

        return new MyTicketAdapter.MyTicketAdapterViewHolder(view);

    }

    class MyTicketAdapterViewHolder extends RecyclerView.ViewHolder{
        public TextView BusNo, Date, From, To, StartTime, EndTime, BusType, SeatNo,Passenger;
        public AppCompatButton Cancel;
        public MyTicketAdapterViewHolder(@NonNull View itemView){
            super(itemView);
            this.BusNo = itemView.findViewById(R.id.ticketBusNumber);
            this.Date = itemView.findViewById(R.id.ticketDate);
            this.From = itemView.findViewById(R.id.ticketFrom);
            this.To = itemView.findViewById(R.id.ticketTo);
            this.StartTime = itemView.findViewById(R.id.ticketStart);
            this.EndTime = itemView.findViewById(R.id.ticketEnd);
            this.BusType = itemView.findViewById(R.id.ticketBusType);
            this.SeatNo = itemView.findViewById(R.id.ticketSeat);
            this.Passenger = itemView.findViewById(R.id.ticketPassenger);
            this.Cancel= itemView.findViewById(R.id.BtnCancel);
        }
    }

    private void reviewDialog(String Date,String FromToLocation,String Busno,String SeatNo,Context context) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LinearLayout linearLayout=new LinearLayout(context);
        EditText editText=new EditText(context);
        LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                    LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);

        builder.setTitle("User Review").setMessage("Write a brief review for bus no "+Busno).setCancelable(true);
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.addView(editText);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                assert firebaseUser!=null;
                String User=firebaseUser.getUid();

                DatabaseReference Reviews=FirebaseDatabase.getInstance().getReference().child("Reviews");
                DatabaseReference UserTicket=FirebaseDatabase.getInstance().getReference().child("Tickets").child("UserSideCheck").child(User);
                HashMap<String,Object > userticket=new HashMap<>();

                HashMap<String,Object > review=new HashMap<>();
                userticket.put("isReviewed",true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    review.put(String.valueOf(new Date().toLocaleString()),editText.getText().toString());
                }
                UserTicket.child(Busno+FromToLocation+SeatNo).updateChildren(userticket).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                Reviews.child(Busno).updateChildren(review);
                        dialog.dismiss();
                        Toast.makeText(context, "Thank you for reviewing bus", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void CancelDialog(long diff,String price,String busInfoA,String busInfoU,String Date,String FromToLocation,String BusNo, String seatno, Context context)
    {
        Double Price=Double.parseDouble(price);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LinearLayout linearLayout=new LinearLayout(context);
        final ImageView imageView=new ImageView(context);
        if(diff>48){

        imageView.setImageResource(R.drawable.cp48);
        Price= 0.9*Price;
        }
        else if(diff >24){
            imageView.setImageResource(R.drawable.cp24);
            Price= 0.7*Price;

        }
        else if( diff >12 ){
            imageView.setImageResource(R.drawable.cp12);
            Price= 0.4*Price;

        }
        else {
            imageView.setImageResource(R.drawable.cp0);
            Price= 0*Price;

        }
        builder.setTitle("Cancellation Charges").setCancelable(true).setMessage(Price + "INR will be refunded of total amount of "+price+" INR."+"Refund will initialized in 2-3 working days.");

        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.addView(imageView);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        Double finalPrice = Price;
        builder.setPositiveButton("Yes, Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                String User = user.getUid();

                DatabaseReference AdminTicketList = FirebaseDatabase.getInstance().getReference().
                        child("Tickets").child("AdminSideCheck").child(Date).child(busInfoA);

                DatabaseReference usersTicketList = FirebaseDatabase.getInstance().getReference().
                        child("Tickets").child("UserSideCheck").child(User);

                DatabaseReference busDetailsList = FirebaseDatabase.getInstance().getReference().
                        child("Buses").child(Date).child(FromToLocation);

                usersTicketList.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, Object> ticketInformation = new HashMap<>();
                        ticketInformation.put("isCanceled",true);
                        usersTicketList.child(busInfoU).updateChildren(ticketInformation);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Try after some time !", Toast.LENGTH_SHORT).show();
                    }
                });

                AdminTicketList.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, Object> ticketInformation = new HashMap<>();
                        ticketInformation.put("isCanceled",true);
                        ticketInformation.put("RefundAmount",String.valueOf(finalPrice));
                        AdminTicketList.child(User+seatno).updateChildren(ticketInformation);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                    busDetailsList.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String newSeats = snapshot.child(BusNo).child("NumberOfSeat").getValue(String.class);
                    HashMap<String, Object> bus = new HashMap<>();
                            HashMap<String, Object> busbooked = new HashMap<>();

                            // add code of deleting that seatno from booked child
                            GenericTypeIndicator<HashMap<String,Integer>> genericTypeIndicator=new GenericTypeIndicator<HashMap<String,Integer>>() {};


//                            final HashMap<String,Integer> books= snapshot.child(BusNo).child("Booked").getValue(genericTypeIndicator);

                    bus.put("NumberOfSeat", String.valueOf(Integer.parseInt(newSeats)+1));
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                books.forEach((k,v)->{
//                                });
//                            }
                            busbooked.put(seatno,0);
                            busDetailsList.child(BusNo).child("Booked").updateChildren(busbooked);
                    busDetailsList.child(BusNo).updateChildren(bus).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            Toast.makeText(context, "Canceled Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

//                    Toast.makeText(context, newSeats, Toast.LENGTH_SHORT).show();
//                    busDetailsList.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            HashMap<String, Object> bus = new HashMap<>();
//                            String newSeats = String.valueOf(Integer.parseInt(snapshot.child(BusNo).child("NumberOfSeat").getValue().toString()) + 1);
//                            bus.put("NumberOfSeat", newSeats);
//                            busDetailsList.child(BusNo).updateChildren(bus);
////                            Toast.makeText(context, newSeats, Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Cancellation Terminated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.create().show();

    }
}
