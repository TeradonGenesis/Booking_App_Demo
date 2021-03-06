package com.example.kuching_park_hotel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyBookingsFragment extends Fragment {

    private String guest_id;
    private Member test_member;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private ArrayList<Booking_History> bookingHistoryArrayList = new ArrayList<Booking_History>();
    private MyBookingsAdapter myBookingsAdapter;

    public MyBookingsFragment(Member member) {
        test_member = member;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_bookings, container, false);
        requestQueue = MySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue();
        initUI(v);
        getBookings();
        return v;

    }

    public void initUI(View v) {
        recyclerView = v.findViewById(R.id.recycler_bookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        guest_id = test_member.getMember_no();
        Log.i("Room: ", guest_id);
    }

    public void getBookings() {
        String url = "http://10.0.2.2/API/get_bookings.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray array = new JSONArray(response);

                    if(array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject jo = array.getJSONObject(i);

                            Booking_History booking = new Booking_History(jo.getInt("booking_id"), jo.getInt("room_id"), jo.getInt("guest_ref"), jo.getInt("room_qty"), jo.getString("check_in"), jo.getString("check_out"), jo.getString("room_name"), BigDecimal.valueOf(jo.getDouble("total")).floatValue());

                            bookingHistoryArrayList.add(booking);
                        }

                        for (Booking_History booking : bookingHistoryArrayList){
                            Log.i("Room: ", booking.getRoom_name());
                        }

                        myBookingsAdapter = new MyBookingsAdapter(bookingHistoryArrayList);
                        recyclerView.setAdapter(myBookingsAdapter);

                    } else {
                        Toast.makeText(getContext(), "No rooms available", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = "Failed to check. Please check if you have internet connection";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("id", guest_id);
                return MyData;
            }
        };
        requestQueue.add(MyStringRequest);
    }


}