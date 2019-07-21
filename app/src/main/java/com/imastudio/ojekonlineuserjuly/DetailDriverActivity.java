package com.imastudio.ojekonlineuserjuly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imastudio.ojekonlineuserjuly.model.DataDetailDriver;
import com.imastudio.ojekonlineuserjuly.model.ResponseDetailDriver;
import com.imastudio.ojekonlineuserjuly.network.InitRetrofit;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailDriverActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.lokasiawal)
    TextView lokasiawal;
    @BindView(R.id.lokasitujuan)
    TextView lokasitujuan;
    @BindView(R.id.txtnamadriver)
    TextView txtnamadriver;
    @BindView(R.id.linear2)
    LinearLayout linear2;
    @BindView(R.id.txthpdriver)
    TextView txthpdriver;
    @BindView(R.id.linear1)
    LinearLayout linear1;
    private GoogleMap mMap;
    private String iddriver;
    private List<DataDetailDriver> dataDriver;
    private double latdriver;
    private double londriver;
    private LatLng posisidriver;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_lokasi_driver);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        iddriver = intent.getStringExtra("iddriver");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        timer = new Timer();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        detailInfoDriver();

    }

    private void detailInfoDriver() {
        InitRetrofit.getInstance().detailDriver(iddriver).enqueue(new Callback<ResponseDetailDriver>() {
            @Override
            public void onResponse(Call<ResponseDetailDriver> call, Response<ResponseDetailDriver> response) {
                String result = response.body().getResult();
                String msg = response.body().getMsg();
                if (result.equals("true")) {
                    dataDriver = response.body().getData();
              txtnamadriver.setText(dataDriver.get(0).getUserNama());
            txthpdriver.setText(dataDriver.get(0).getUserHp());
                    //set map information
                    latdriver = Double.parseDouble(dataDriver.get(0).getTrackingLat());
                    londriver = Double.parseDouble(dataDriver.get(0).getTrackingLng());
                    posisidriver = new LatLng(latdriver, londriver);

                    mMap.addMarker(new MarkerOptions().position(posisidriver))
                            .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posisidriver, 17));
                    //padding maps
                    mMap.setPadding(40, 150, 50, 120);
                    // menampilkan compas
                    mMap.getUiSettings().setCompassEnabled(true);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    Toast.makeText(DetailDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailDriverActivity.this, msg, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseDetailDriver> call, Throwable t) {

            }
        });
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
                        detailInfoDriver();
                    }
                }, 0,3000);
//                String idbooking = String.valueOf(tangkapIdBooking);
//                Log.d("iddriver ",idbooking);

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    @OnClick(R.id.txthpdriver)
    public void onViewClicked() {
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+dataDriver.get(0).getUserHp())));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetailDriverActivity.this,HistoryActivity.class));
    }
}
