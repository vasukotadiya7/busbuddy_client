package com.vasukotadiya.bbclient.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vasukotadiya.bbclient.R;
import com.vasukotadiya.bbclient.model.TicketModel;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
            holder.Cancel.setText("Canceled!!!");
            holder.Cancel.setClickable(false);
        }
        else {
            holder.Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                    try {
                        String btime = model.getStartTime();
                        Date date = sdf.parse(model.getDate());
                        sdf.applyPattern("yyyy/MM/dd");
                        String bdate = sdf.format(date);
                        String cdate = sdf.format(new Date().getTime());
                        bcalender.set(Calendar.YEAR, Integer.parseInt(bdate.split("/")[0]));
                        bcalender.set(Calendar.MONTH, Integer.parseInt(bdate.split("/")[1]) - 1);
                        bcalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(bdate.split("/")[2]));

                        if (btime.contains("PM")) {
                            bcalender.set(Calendar.AM_PM, Calendar.PM);
                            String btime1 = btime.replace("PM", "");
                            bcalender.set(Calendar.HOUR, Integer.parseInt(btime1.split(":")[0]));
                            bcalender.set(Calendar.MINUTE, Integer.parseInt(btime1.split(":")[0]));
                        } else {
                            bcalender.set(Calendar.AM_PM, Calendar.AM);
                            String btime1 = btime.replace("AM", "");

                            bcalender.set(Calendar.HOUR, Integer.parseInt(btime1.split(":")[0]));
                            bcalender.set(Calendar.MINUTE, Integer.parseInt(btime1.split(":")[1]));
                        }
                        long bt = bcalender.getTimeInMillis();
                        long ct = Calendar.getInstance().getTimeInMillis();
                        long diff = bt - ct;
                        diff = diff / 3600000;
                        Toast.makeText(v.getContext(), String.valueOf(diff), Toast.LENGTH_SHORT).show();
                        CancelDialog(diff,model.getPrice(), v.getContext());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
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
    public void CancelDialog(long diff,String price,Context context)
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

        builder.setPositiveButton("Yes, Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Canceled Successfully", Toast.LENGTH_SHORT).show();
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
