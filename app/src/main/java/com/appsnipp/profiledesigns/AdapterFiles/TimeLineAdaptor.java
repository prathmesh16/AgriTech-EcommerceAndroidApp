package com.appsnipp.profiledesigns.AdapterFiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.appsnipp.profiledesigns.AdaptorClasses.OrderStatus;
import com.appsnipp.profiledesigns.AdaptorClasses.TimeLineModel;
import com.appsnipp.profiledesigns.R;
import com.github.vipulasri.timelineview.TimelineView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TimeLineAdaptor extends RecyclerView.Adapter<TimeLineAdaptor.TimeLineViewHolder>{
    private Context mCtx;
    private List<TimeLineModel> mDataList;
    private OnItemClickListner mListner;
    private String CType;
    private String ordid;
    private String customer;
    SharedPreferences pref;
    public interface OnItemClickListner {
        void OnItemClick(int position);
    }

    void setOnItemClickListner(OnItemClickListner listner) {
        mListner = listner;
    }

    public TimeLineAdaptor(Context mCtx, List<TimeLineModel> mDataList,String CType,String ordid,String customer) {
        this.mCtx = mCtx;
        this.mDataList = mDataList;
        this.CType=CType;
        this.ordid=ordid;
        this.customer=customer;
    }
    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }
    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_timeline, parent, false);
        return new TimeLineViewHolder(view, ViewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final TimeLineViewHolder holder, int position) {
        final TimeLineModel timeLineModel = mDataList.get(position);
            pref = mCtx.getSharedPreferences("user_details",MODE_PRIVATE);
            if (timeLineModel.getOrderStatus().equals("INACTIVE"))
            {
                setMarker(holder, R.drawable.ic_marker_inactive, R.color.colorGrey500,CType,timeLineModel);
            }else if(timeLineModel.getOrderStatus().equals("ACTIVE"))
            {
                setMarker(holder, R.drawable.ic_marker_active, R.color.colorGrey500,CType,timeLineModel);
            }else
            {
                setMarker(holder, R.drawable.ic_marker, R.color.colorGrey500,CType,timeLineModel);
            }
            if (!timeLineModel.getDate().isEmpty())
            {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(timeLineModel.getDate());
            }
            else
                holder.date.setVisibility(View.INVISIBLE);
            holder.messege.setText(timeLineModel.getMessege());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CType.equals("Seller"))
                    {
                        final AlertDialog.Builder alertorderstatusedit = new AlertDialog.Builder(mCtx);
                        LayoutInflater factory = LayoutInflater.from(mCtx);
                        final View newview = factory.inflate(R.layout.editorderstatusdialouge, null);
                        final CheckBox Complete=newview.findViewById(R.id.checkBox1);
                        final CheckBox Active=newview.findViewById(R.id.checkBox2);
                        CheckBox InActive=newview.findViewById(R.id.checkBox3);
                        Button submit=newview.findViewById(R.id.submit);
                        alertorderstatusedit.setTitle(timeLineModel.getMessege());
                        if (timeLineModel.getOrderStatus().equals("COMPLETED"))
                        {
                            Complete.setChecked(true);
                            Complete.setEnabled(false);
                            Active.setChecked(true);
                            Active.setEnabled(false);
                            InActive.setChecked(true);
                            InActive.setEnabled(false);
                            submit.setVisibility(View.INVISIBLE);
                        }
                        else if (timeLineModel.getOrderStatus().equals("ACTIVE"))
                        {
                            InActive.setChecked(true);
                            InActive.setEnabled(false);
                            Active.setChecked(true);
                            Active.setEnabled(false);
                        }
                        else {
                            InActive.setChecked(true);
                            InActive.setEnabled(false);
                        }

                        alertorderstatusedit.setView(newview);
                        final AlertDialog dlg= alertorderstatusedit.show();
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Complete.isChecked())
                                {
                                    DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("delivery");
                                    db.child(ordid).child("orderstatus").child("Stage"+timeLineModel.getOrderStatusno()).child("orderStatus").setValue("COMPLETED");
                                    DatabaseReference db12= FirebaseDatabase.getInstance().getReference().child("Users").child(customer).child("orders");
                                    db12.child(ordid).child("orderstatus").child("Stage"+timeLineModel.getOrderStatusno()).child("orderStatus").setValue("COMPLETED");


                                    if (timeLineModel.getOrderStatusno()<=4)
                                    {
                                        DatabaseReference db3= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("delivery");
                                        db3.child(ordid).child("orderstatus").child("Stage"+(timeLineModel.getOrderStatusno()+1)).child("orderStatus").setValue("ACTIVE");
                                        DatabaseReference db312= FirebaseDatabase.getInstance().getReference().child("Users").child(customer).child("orders");
                                        db312.child(ordid).child("orderstatus").child("Stage"+(timeLineModel.getOrderStatusno()+1)).child("orderStatus").setValue("ACTIVE");
                                    }
                                }else if (Active.isChecked())
                                {
                                    DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("delivery");
                                    db.child(ordid).child("orderstatus").child("Stage"+timeLineModel.getOrderStatusno()).child("orderStatus").setValue("ACTIVE");
                                    DatabaseReference db12= FirebaseDatabase.getInstance().getReference().child("Users").child(customer).child("orders");
                                    db12.child(ordid).child("orderstatus").child("Stage"+timeLineModel.getOrderStatusno()).child("orderStatus").setValue("ACTIVE");
                                }
                                dlg.dismiss();
                            }
                        });

                    }
                }
            });
    }

    private void setMarker(TimeLineViewHolder holder, int imarker, int color, String CType, final TimeLineModel timeLineModel) {
            holder.timelineView.setMarker(VectorDrawableUtils.getDrawable(holder.itemView.getContext(),imarker, ContextCompat.getColor(holder.itemView.getContext(), color)));
            if (CType.equals("Seller"))
            {
                holder.timelineView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder alertorderstatusedit = new AlertDialog.Builder(mCtx);
                        LayoutInflater factory = LayoutInflater.from(mCtx);
                        final View newview = factory.inflate(R.layout.editorderstatusdialouge, null);
                        final CheckBox Complete=newview.findViewById(R.id.checkBox1);
                        final CheckBox Active=newview.findViewById(R.id.checkBox2);
                        CheckBox InActive=newview.findViewById(R.id.checkBox3);
                        Button submit=newview.findViewById(R.id.submit);
                        alertorderstatusedit.setTitle(timeLineModel.getMessege());
                        if (timeLineModel.getOrderStatus().equals("COMPLETED"))
                        {
                            Complete.setChecked(true);
                            Complete.setEnabled(false);
                            Active.setChecked(true);
                            Active.setEnabled(false);
                            InActive.setChecked(true);
                            InActive.setEnabled(false);
                            submit.setVisibility(View.INVISIBLE);
                        }
                        else if (timeLineModel.getOrderStatus().equals("ACTIVE"))
                        {
                            InActive.setChecked(true);
                            InActive.setEnabled(false);
                            Active.setChecked(true);
                            Active.setEnabled(false);
                        }
                        else {
                            InActive.setChecked(true);
                            InActive.setEnabled(false);
                        }

                        alertorderstatusedit.setView(newview);
                        final AlertDialog dlg= alertorderstatusedit.show();
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Complete.isChecked())
                                {
                                    DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("delivery");
                                    db.child(ordid).child("orderstatus").child("Stage"+timeLineModel.getOrderStatusno()).child("orderStatus").setValue("COMPLETED");
                                    DatabaseReference db12= FirebaseDatabase.getInstance().getReference().child("Users").child(customer).child("orders");
                                    db12.child(ordid).child("orderstatus").child("Stage"+timeLineModel.getOrderStatusno()).child("orderStatus").setValue("COMPLETED");


                                    if (timeLineModel.getOrderStatusno()<=4)
                                    {
                                        DatabaseReference db3= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("delivery");
                                        db3.child(ordid).child("orderstatus").child("Stage"+(timeLineModel.getOrderStatusno()+1)).child("orderStatus").setValue("ACTIVE");
                                        DatabaseReference db312= FirebaseDatabase.getInstance().getReference().child("Users").child(customer).child("orders");
                                        db312.child(ordid).child("orderstatus").child("Stage"+(timeLineModel.getOrderStatusno()+1)).child("orderStatus").setValue("ACTIVE");
                                    }
                                }else if (Active.isChecked())
                                {
                                    DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("delivery");
                                    db.child(ordid).child("orderstatus").child("Stage"+timeLineModel.getOrderStatusno()).child("orderStatus").setValue("ACTIVE");
                                    DatabaseReference db12= FirebaseDatabase.getInstance().getReference().child("Users").child(customer).child("orders");
                                    db12.child(ordid).child("orderstatus").child("Stage"+timeLineModel.getOrderStatusno()).child("orderStatus").setValue("ACTIVE");
                                }
                                dlg.dismiss();
                            }
                        });
                    }
                });

            }

    }

    public int getItemCount() {
        return mDataList.size();
    }
    class TimeLineViewHolder extends RecyclerView.ViewHolder {

        TextView messege,date;
        TimelineView timelineView;
        CardView cardView;

            public TimeLineViewHolder(View itemView, int viewType) {
                super(itemView);
                timelineView=(TimelineView) itemView.findViewById(R.id.timeline);
                timelineView.initLine(viewType);
                cardView=itemView.findViewById(R.id.cardtimeline);
                messege=itemView.findViewById(R.id.text_timeline_title);
                date=itemView.findViewById(R.id.text_timeline_date);
            }

        }

    }

