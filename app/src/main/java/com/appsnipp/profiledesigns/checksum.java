package com.appsnipp.profiledesigns;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class checksum extends AppCompatActivity implements PaytmPaymentTransactionCallback {
    String custid="", orderId="", mid="";
    Serializable orderAddress=null;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //Intent intent = getIntent();
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        orderId = genOrderId();
        custid = pref.getString("UName","");

        mid = "lpsvEw59219385907682"; /// your marchant key
        orderAddress=getIntent().getSerializableExtra("orderaddress");
        sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
// vollye , retrofit, asynch
    }
    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(checksum.this);
        //private String orderId , mid, custid, amt;
        String url ="https://pkj.000webhostapp.com/paytm/generateChecksum.php";
        String varifyurl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+orderId;
        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }
        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(checksum.this);
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+custid+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+getIntent().getIntExtra("price",100)+"&WEBSITE=WEBSTAGING"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {
                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getStagingService();
            // when app is ready to publish use production service
             //]
            // PaytmPGService  Service = PaytmPGService.getProductionService();
            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values

            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT",""+getIntent().getIntExtra("price",100));
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            //paramMap.put( "EMAIL" , "abc@gmail.com");   // no need
            // paramMap.put( "MOBILE_NO" , "9144040888");  // no need
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,Service.mCertificate);
            // start payment service call here
            Service.startPaymentTransaction(checksum.this, true, true,
                    checksum.this  );
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
        if(!bundle.get("STATUS").equals("TXN_FAILURE")) {
            SharedPreferences pref;
            pref = getSharedPreferences("user_details", MODE_PRIVATE);
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
            DatabaseReference db = ref1.child("Users").child(pref.getString("UName", "")).child("orders").child(orderId);
            db.child("price").setValue(getIntent().getIntExtra("price", 100));
            db.child("name").setValue(getIntent().getStringExtra("name"));
            db.child("catagory").setValue(getIntent().getStringExtra("catagory"));
            db.child("customer").setValue(pref.getString("UName",""));
            db.child("seller").setValue(getIntent().getStringExtra("seller"));
            db.child("image").setValue(getIntent().getStringExtra("image"));
            db.child("Delivery").setValue("Pending");
            db.child("payment").setValue("Done");
            db.child("orderaddress").setValue(orderAddress);
            db.child("ordid").setValue(orderId);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            DatabaseReference dbn = db.child("orderstatus").child("Stage1");
            dbn.child("messege").setValue("Order placed successfully");
            dbn.child("date").setValue(dtf.format(now));
            dbn.child("orderStatus").setValue("COMPLETED");
            dbn.child("orderStatusno").setValue(1);

            DatabaseReference dbn1 = db.child("orderstatus").child("Stage2");
            dbn1.child("messege").setValue("Order Confirming");
            dbn1.child("date").setValue(dtf.format(now));
            dbn1.child("orderStatus").setValue("ACTIVE");
            dbn1.child("orderStatusno").setValue(2);
            DatabaseReference dbn4 = db.child("orderstatus").child("Stage3");
            dbn4.child("messege").setValue("Order Shipped");
            dbn4.child("date").setValue(dtf.format(now));
            dbn4.child("orderStatus").setValue("INACTIVE");
            dbn4.child("orderStatusno").setValue(3);
            DatabaseReference dbn2 = db.child("orderstatus").child("Stage4");
            dbn2.child("messege").setValue("Order out for delivery");
            dbn2.child("date").setValue(dtf.format(now));
            dbn2.child("orderStatus").setValue("INACTIVE");
            dbn2.child("orderStatusno").setValue(4);
            DatabaseReference dbn3 = db.child("orderstatus").child("Stage5");
            dbn3.child("messege").setValue("Order Successfully Deliverd");
            dbn3.child("date").setValue(dtf.format(now));
            dbn3.child("orderStatus").setValue("INACTIVE");
            dbn3.child("orderStatusno").setValue(5);



            DatabaseReference ref12 = FirebaseDatabase.getInstance().getReference();
            DatabaseReference db12 = ref12.child("Users").child(getIntent().getStringExtra("seller")).child("delivery").child(orderId);
            db12.child("price").setValue(getIntent().getIntExtra("price", 100));
            db12.child("name").setValue(getIntent().getStringExtra("name"));
            db12.child("catagory").setValue(getIntent().getStringExtra("catagory"));
            db12.child("customer").setValue(pref.getString("UName",""));
            db12.child("seller").setValue(getIntent().getStringExtra("seller"));
            db12.child("image").setValue(getIntent().getStringExtra("image"));
            db12.child("Delivery").setValue("Pending");
            db12.child("payment").setValue("Done");
            db12.child("orderaddress").setValue(orderAddress);
            db12.child("ordid").setValue(orderId);
            DatabaseReference dbn12 = db12.child("orderstatus").child("Stage1");
            dbn12.child("messege").setValue("Order placed successfully");
            dbn12.child("date").setValue(dtf.format(now));
            dbn12.child("orderStatus").setValue("COMPLETED");
            dbn12.child("orderStatusno").setValue(1);

            DatabaseReference dbn112 = db12.child("orderstatus").child("Stage2");
            dbn112.child("messege").setValue("Order Confirming");
            dbn112.child("date").setValue(dtf.format(now));
            dbn112.child("orderStatus").setValue("ACTIVE");
            dbn112.child("orderStatusno").setValue(2);
            DatabaseReference dbn412 = db12.child("orderstatus").child("Stage3");
            dbn412.child("messege").setValue("Order Shipped");
            dbn412.child("date").setValue(dtf.format(now));
            dbn412.child("orderStatus").setValue("INACTIVE");
            dbn412.child("orderStatusno").setValue(3);
            DatabaseReference dbn212 = db12.child("orderstatus").child("Stage4");
            dbn212.child("messege").setValue("Order out for delivery");
            dbn212.child("date").setValue(dtf.format(now));
            dbn212.child("orderStatus").setValue("INACTIVE");
            dbn212.child("orderStatusno").setValue(4);
            DatabaseReference dbn312 = db12.child("orderstatus").child("Stage5");
            dbn312.child("messege").setValue("Order Successfully Deliverd");
            dbn312.child("date").setValue(dtf.format(now));
            dbn312.child("orderStatus").setValue("INACTIVE");
            dbn312.child("orderStatusno").setValue(5);

            Intent i = new Intent(checksum.this, orderPlacement.class);
            startActivity(i);
        }
        else {
            Toast.makeText(checksum.this,"Payment failure !!",Toast.LENGTH_LONG).show();
            Intent i = new Intent(checksum.this, MainActivity.class);
            startActivity(i);
        }
    }
    @Override
    public void networkNotAvailable() {
    }
    @Override
    public void clientAuthenticationFailed(String s) {
    }
    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
    }
    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
    }
    @Override
    public void onBackPressedCancelTransaction() {
        SharedPreferences pref;
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        Intent intent=new Intent(checksum.this,MainActivity.class);
        intent.putExtra("UName",pref.getString("UName",""));
        intent.putExtra("UPass", pref.getString("UPass",""));
        startActivity(intent);
        Log.e("checksum ", " cancel call back respon  " );
    }
    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
    }
    public String genOrderId() {
        Random r = new Random(System.currentTimeMillis());
        return "ORDER" + (1 + r.nextInt(2)) * 10000 + r.nextInt(10000);
    }
}