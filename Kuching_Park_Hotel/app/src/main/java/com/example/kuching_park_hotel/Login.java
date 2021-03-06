package com.example.kuching_park_hotel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    //UI elements
    Button button,button_create_account;
    EditText login_email,login_password;
    String email_details,email_password;

    //temp store data
    Member test_member;

    //RequestQueue and URL's
    RequestQueue rq;
    //email verification TODO - Modify to refer to smdigital
    private final String URL = "http://10.0.2.2/connections/android/email_verification.php";
    //generate and verification of api key TODO - same as smdigital
    private final String API_URL = "http://10.0.2.2/connections/android/api_gen.php";

    //SharedPref
    private static final String SHARED_PREF = "member";
    private static final String MEMBER_OBJECT = "member_object";
    private static final String JWT ="jwt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //somewhere here check whether got previous login details using Shared Preferences
        //if not then do below login page

        //reset shared pref for test sake -  remove if log out button is made
//        resetSharedPref();

        //get shared pref member object and jwt
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(MEMBER_OBJECT,"");
        String stored_jwt = sharedPreferences.getString(JWT,"");
        Log.d("JWT TOKEN",stored_jwt);
        //add code to check token validity

        //then proceed to grab json object from shared pref
        if(json.equals("") && stored_jwt.equals("")){
            //Login stuff if don't have login details stored locally
            button = findViewById(R.id.button_main);
            login_email=findViewById(R.id.editText_login_email);
            login_password=findViewById(R.id.editText_password_email);
            //Create account starts here
            button_create_account=findViewById(R.id.button_create_account);
            button_create_account.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent2 = new Intent(getApplicationContext(),Account_Creation_Activity.class);
                    startActivity(intent2);
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email_details = login_email.getText().toString();
                    email_password = login_password.getText().toString();

                    getData();
                }
            });
        }else{
            test_member = gson.fromJson(json,Member.class);
            apiValid(new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    //add more error handling
                    if(result.equals("1")){
                        Log.d("CONSOLE","AUTOLOGIN SUCCESS!");//this works
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("Member",test_member);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(Login.this,"Error key or account doesn't exist!",
                                Toast.LENGTH_LONG).show();
//                        Log.d("console","ACCOUNT DOESN'T EXIST");//this works
                        //clear SharedPrefs
                        resetSharedPref();
                    }
                }
            },stored_jwt,test_member);
        }
    }



    //for testing purposes, remember to DELETE
    private void resetSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MEMBER_OBJECT,"").apply();
        editor.putString(JWT,"").apply();
    }



    //check token validity, first checks whether JWT token is valid, then check whether user account still exists
    // there is the case where the user account did exist but then got deleted for various reasons, this prevents accidental autologin
    //of deleted account.
    private void apiValid(final VolleyCallback callback,final String jwt,final Member test_member){
        rq = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest sr = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("console","No data returned in apiValid");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("option","verify");
                params.put("jwt",jwt);
                params.put("email",test_member.getEmail());
//                params.put("password",test_member.getPassword()); //modify php script to accomodate
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(sr);
    }


    //interface for execution of function after async task is done
    public interface VolleyCallback{
        void onSuccess(String result);
    }

    //generate token and store in shared pref
    private void apiGen(){
        rq = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest sr = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String jwt = response;
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(JWT,jwt).apply();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this,"Network error generating login token",
                        Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("option","gen");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(sr);
    }

    //Get member data
    private void getData()
    {
        rq = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest sr = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try
                {
                    JSONArray res = new JSONArray(response);
                    if(res.length()==0){
                        Toast.makeText(Login.this, "Email or password does not match",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        //add data into the member object
                        for(int i=0;i<res.length();i++){
                            JSONObject jsonObject = res.getJSONObject(i);

                            test_member  = new Member
                                    (jsonObject.getString("id"),
                                            jsonObject.getString("membership_no"),
                                            jsonObject.getString("username"),
                                            jsonObject.getString("guest_name"),
                                            jsonObject.getString("address"),
                                            jsonObject.getString("email"),
                                            jsonObject.getString("state"),
                                            jsonObject.getString("city"),
                                            jsonObject.getInt("mobile"),
                                            jsonObject.getInt("postcode"));
                            Log.d("member name is",test_member.getGuest_name());
//                            Log.d("MY PASSWORD IS:",test_member.getPassword());
                        }
                        //call genkey here before moving to new activity,save in shared pref
                        apiGen();
                        //use gson to save object into shared pref
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(test_member);
                        editor.putString(MEMBER_OBJECT,json).apply();

                        //Start new activity
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("Member",test_member);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "No internet connection",
                        Toast.LENGTH_SHORT).show();//this works
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                String email = email_details;
                String pwd = email_password;
                params.put("user_email",email);
                params.put("password",pwd);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(sr);
    }
}
