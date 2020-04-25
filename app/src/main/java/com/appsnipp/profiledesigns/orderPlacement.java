package com.appsnipp.profiledesigns;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class orderPlacement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placement);
        Intent intent = new Intent(orderPlacement.this, String.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(orderPlacement.this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(orderPlacement.this, "123")
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .setContentTitle("Pesticide Order!")
                .setContentText("Your Order Placed Successfully !!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(orderPlacement.this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(12, mBuilder.build());
        createNotificationChannel();

    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PestiCom";
            String description = "Order Placed Successfully !!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void onBackPressed()
    {
        SharedPreferences pref;
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        Intent intent=new Intent(orderPlacement.this,MainActivity.class);
        intent.putExtra("UName",pref.getString("UName",""));
        intent.putExtra("UPass", pref.getString("UPass",""));
        startActivity(intent);
    }
}
