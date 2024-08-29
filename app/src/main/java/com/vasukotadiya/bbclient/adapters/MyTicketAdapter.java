package com.vasukotadiya.bbclient.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vasukotadiya.bbclient.R;
import com.vasukotadiya.bbclient.model.TicketModel;

public class MyTicketAdapter extends FirebaseRecyclerAdapter<TicketModel,MyTicketAdapter.MyTicketAdapterViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
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
        }
    }
}
