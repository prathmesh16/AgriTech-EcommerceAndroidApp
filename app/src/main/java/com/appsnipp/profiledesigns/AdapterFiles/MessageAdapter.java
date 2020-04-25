package com.appsnipp.profiledesigns.AdapterFiles;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsnipp.profiledesigns.AdaptorClasses.Message;
import com.appsnipp.profiledesigns.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marat on 18-07-2019.
 */

public class MessageAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Message> MessageList;
    SharedPreferences pref;

    public MessageAdapter(List<Message> messageList, Context context)
    {
        this.context=context;
        this.MessageList=messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_layout,parent,false);
        return new MessageViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        pref = context.getSharedPreferences("user_details",MODE_PRIVATE);
        Message msg=MessageList.get(position);
        ((MessageViewHolder)holder).MessageText.setText(msg.getMessage());
        //((MessageViewHolder)holder).time.setText(msg.getTime());
        if(msg.getFrom().equals(pref.getString("UName",""))){
            ((MessageViewHolder)holder).MessageText.setGravity(Gravity.RIGHT);
        }else{
            ((MessageViewHolder)holder).MessageText.setGravity(Gravity.LEFT);
        }

        if(msg.getFrom().equals(pref.getString("UName",""))){
            ((MessageViewHolder)holder).MessageText.setText(Html.fromHtml(msg.getMessage()+" <font color='#2d89f9'>0</font><b><small><font color='#c4d9f2'><br>"+msg.getTime()));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((MessageViewHolder)holder).MessageText.getLayoutParams();
            params.gravity = Gravity.RIGHT;

            setMessageUI(((MessageViewHolder)holder), Color.WHITE,R.drawable.msg_send);
            ((MessageViewHolder)holder).MessageText.setLayoutParams(params);


        }else {
            ((MessageViewHolder)holder).MessageText.setText(Html.fromHtml(msg.getMessage()+"<font color='#f5f5f5'>0</font><b><small><font color='#c0c0c0'><br>"+msg.getTime()));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)((MessageViewHolder)holder).MessageText.getLayoutParams();
            params.gravity = Gravity.LEFT;

            setMessageUI(((MessageViewHolder)holder),context.getResources().getColor(R.color.chatGray),R.drawable.msg_recieved);
            ((MessageViewHolder)holder).MessageText.setLayoutParams(params);
        }

    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView MessageText,time;
        public MessageViewHolder(View itemView) {
            super(itemView);
            MessageText=itemView.findViewById(R.id.MsgText);
            //time=itemView.findViewById(R.id.time);
        }
    }

    public void setMessageUI(MessageViewHolder viewHolder,int txtColor,int res){
        viewHolder.MessageText.setBackgroundResource(res);
        viewHolder.MessageText.setTextColor(txtColor);

    }
}