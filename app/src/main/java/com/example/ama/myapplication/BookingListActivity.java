package com.example.ama.myapplication;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ama.myapplication.Adapter.BookingListAdapter;
import com.example.ama.myapplication.Booking.Search;
import com.example.ama.myapplication.service.APIService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookingListActivity extends AppCompatActivity {
    public static final String URL = "http://airless-shout.000webhostapp.com/";
    private List<com.example.ama.myapplication.Booking.Data> data = new ArrayList<>();
    private BookingListAdapter viewAdapter;
    @BindView(R.id.recycleView_list_booking)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar_list_booking)
    ProgressBar progressBar;
    @BindView(R.id.tgl_pilih)
    TextView _tgl_pilih;
    @BindView(R.id.jam_pilih)
    TextView _jam_pilih;
    @BindView(R.id.message_list_booking)
    TextView _message_booking;
    @BindView(R.id.order_back)
    ImageView _back;
    @BindView(R.id.img_message_booking)
    ImageView _img_message;
//    @BindView(R.id.jam_booking_list)
//    TextView _jam_booking_list;
    public String tgl;
    public String jam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);
        ButterKnife.bind(this);

        Bundle bundle=getIntent().getExtras();
        tgl=bundle.getString("tgl");
        jam=bundle.getString("jam");
        _tgl_pilih.setText(tgl);
        _jam_pilih.setText(jam);

//        ((Search) this.getApplication()).setJam_(jam);
//        _jam_booking_list.setText(jam);

        viewAdapter = new BookingListAdapter(this, data, jam, tgl);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viewAdapter);

        loadBookingList(tgl, jam);


        _back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadBookingList(String tgl_, String jam_) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final HashMap<String, String> params = new HashMap<>();
        params.put("tgl_booking", tgl_);
        params.put("jam_booking", jam_);

        final APIService api = retrofit.create(APIService.class);
        Call<com.example.ama.myapplication.Booking.Value> call = api.viewListBooking(params);
        call.enqueue(new Callback<com.example.ama.myapplication.Booking.Value>() {
            @Override
            public void onResponse(Call<com.example.ama.myapplication.Booking.Value> call, Response<com.example.ama.myapplication.Booking.Value> response) {
                if (response.body().getData()== null){
                    Call<com.example.ama.myapplication.Booking.Message> callM = api.viewListBookingMessage(params);
                    callM.enqueue(new Callback<com.example.ama.myapplication.Booking.Message>() {
                        @Override
                        public void onResponse(Call<com.example.ama.myapplication.Booking.Message> call, Response<com.example.ama.myapplication.Booking.Message> response) {
//                            Log.d("Message", "onResponse: "+response.body().getMessage());
                            progressBar.setVisibility(View.GONE);
                            _message_booking.setText(response.body().getMessage());
                            _img_message.setImageResource(R.drawable.message_img);
                        }

                        @Override
                        public void onFailure(Call<com.example.ama.myapplication.Booking.Message> call, Throwable t) {
                           android.widget.Toast.makeText(getApplicationContext(),"Error : "+t ,android.widget.Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    progressBar.setVisibility(View.GONE);

                    data = response.body().getData();
                    String tes = Integer.toString(data.size());
                    viewAdapter = new BookingListAdapter(BookingListActivity.this, data,jam, tgl);
                    viewAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(viewAdapter);
                    response.isSuccessful();
                }
            }

            @Override
            public void onFailure(Call<com.example.ama.myapplication.Booking.Value> call, Throwable t) {
                android.widget.Toast.makeText(getApplicationContext(),"Error : "+t ,android.widget.Toast.LENGTH_LONG).show();
            }
        });
    }
}
