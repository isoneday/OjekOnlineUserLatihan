package com.imastudio.ojekonlineuserjuly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.imastudio.ojekonlineuserjuly.model.ResponseWaitingDriver;
import com.imastudio.ojekonlineuserjuly.network.InitRetrofit;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitingDriverActivity extends AppCompatActivity {

    @BindView(R.id.pulsator)
    PulsatorLayout pulsator;
    @BindView(R.id.buttoncancel)
    Button buttoncancel;
    private Timer timer;
    private int tangkapIdBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_driver);
        ButterKnife.bind(this);
        pulsator.start();
        timer = new Timer();
        Intent a = getIntent();
        tangkapIdBooking = a.getIntExtra("idbooking",0);
      checkBooking(tangkapIdBooking);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkBooking(tangkapIdBooking);
            }
        }, 0,3000);
        String idbooking = String.valueOf(tangkapIdBooking);
                Log.d("iddriver ",idbooking);

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    private void checkBooking(int tangkapIdBooking) {
        InitRetrofit.getInstance().cekStatusOrder(String.valueOf(tangkapIdBooking)).enqueue(new Callback<ResponseWaitingDriver>() {
            @Override
            public void onResponse(Call<ResponseWaitingDriver> call, Response<ResponseWaitingDriver> response) {
                String result = response.body().getResult();
                String msg =response.body().getMsg();
                if (result.equals("true")){
                  String  iddriver = response.body().getDriver();
                    Intent i = new Intent(WaitingDriverActivity.this, DetailDriverActivity.class);
                    i.putExtra("iddriver", iddriver);
                    startActivity(i);
                    Toast.makeText(WaitingDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(WaitingDriverActivity.this, msg, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseWaitingDriver> call, Throwable t) {

            }
        });

    }

    @OnClick(R.id.buttoncancel)
    public void onViewClicked() {
    }
}