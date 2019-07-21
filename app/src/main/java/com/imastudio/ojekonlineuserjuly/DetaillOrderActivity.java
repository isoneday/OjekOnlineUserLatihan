package com.imastudio.ojekonlineuserjuly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.imastudio.ojekonlineuserjuly.fragment.HistoryFragment;
import com.imastudio.ojekonlineuserjuly.helper.DirectionMapsV2;
import com.imastudio.ojekonlineuserjuly.helper.MyContants;
import com.imastudio.ojekonlineuserjuly.model.DataHistory;
import com.imastudio.ojekonlineuserjuly.model.ResponseWaypoint;
import com.imastudio.ojekonlineuserjuly.model.RoutesItem;
import com.imastudio.ojekonlineuserjuly.network.InitRetrofit;
import com.imastudio.ojekonlineuserjuly.network.RestApi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetaillOrderActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.textView7)
    TextView textView7;
    @BindView(R.id.textView8)
    TextView textView8;
    @BindView(R.id.txtidbooking)
    TextView txtidbooking;
    @BindView(R.id.requestFrom)
    TextView requestFrom;
    @BindView(R.id.requestTo)
    TextView requestTo;
    @BindView(R.id.textView9)
    TextView textView9;
    @BindView(R.id.requestWaktu)
    TextView requestWaktu;
    @BindView(R.id.requestTarif)
    TextView requestTarif;
    @BindView(R.id.textView18)
    TextView textView18;
    @BindView(R.id.requestNama)
    TextView requestNama;
    @BindView(R.id.requestEmail)
    TextView requestEmail;
    @BindView(R.id.requestID)
    TextView requestID;
    @BindView(R.id.CompleteBooking)
    Button CompleteBooking;
    private GoogleMap mMap;
    private int index;
    private int status;
    private DataHistory dataHistory;
    private String iddriver;
    private String idboking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);
        mapFragment.getMapAsync(this);
        iddriver =getIntent().getStringExtra(MyContants.IDDRIVER);
        idboking = getIntent().getStringExtra(MyContants.IDBOOKING);
        index = getIntent().getIntExtra(MyContants.INDEX, 0);
        status = getIntent().getIntExtra(MyContants.STATUS, 0);
        if (status==2){
            dataHistory = HistoryFragment.dataHistoryProses.get(index);
        }else{
            dataHistory = HistoryFragment.dataHistoryComplete.get(index);

        }
        detailRequest();


    }

    private void detailRequest() {
        requestFrom.setText("dari :" + dataHistory.getBookingFrom());
        requestTo.setText("tujuan :" + dataHistory.getBookingTujuan());
        requestTarif.setText("tarif :" + dataHistory.getBookingBiayaUser());
        requestWaktu.setText("jarak :" + dataHistory.getBookingJarak());
//        requestNama.setText("nama :" + dataHistory.getUserNama());
//        requestEmail.setText("email :" + dataHistory.getUserEmail());
        txtidbooking.setText("idbooking:" + dataHistory.getIdBooking());
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
        detailMap();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + dataHistory.getBookingTujuanLat()
                                + "," + dataHistory.getBookingTujuanLng()));
                startActivity(i);
            }
        });
    }
    private void detailMap() {

//get koordinat
        String origin = String.valueOf(dataHistory.getBookingFromLat()) + "," + String.valueOf(dataHistory.getBookingFromLng());
        String desti = String.valueOf(dataHistory.getBookingTujuanLat()) + "," + String.valueOf(dataHistory.getBookingTujuanLng());


        LatLngBounds.Builder bound = LatLngBounds.builder();
        bound.include(new LatLng(Double.parseDouble(dataHistory.getBookingFromLat()), Double.parseDouble(dataHistory.getBookingFromLng())));
        bound.include(new LatLng(Double.parseDouble(dataHistory.getBookingTujuanLat()), Double.parseDouble(dataHistory.getBookingTujuanLng())));
        //  mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 16));
        LatLngBounds bounds = bound.build();
// begin new code:
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
// end of new code

        mMap.animateCamera(cu);

        RestApi service = InitRetrofit.getInstanceGoogle();
        String api = getString(R.string.google_maps_key);
        Call<ResponseWaypoint> call = service.setRute(origin, desti, api);
        call.enqueue(new Callback<ResponseWaypoint>() {
            @Override
            public void onResponse(Call<ResponseWaypoint> call, Response<ResponseWaypoint> response) {
                List<RoutesItem> routes = response.body().getRoutes();

                DirectionMapsV2 direction = new DirectionMapsV2(DetaillOrderActivity.this);
                try {
                    String points = routes.get(0).getOverviewPolyline().getPoints();
                    direction.gambarRoute(mMap, points);

                } catch (Exception e) {
                    Toast.makeText(DetaillOrderActivity.this, "lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseWaypoint> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.CompleteBooking)
    public void onViewClicked() {
        Intent intent =new Intent(DetaillOrderActivity.this,ReviewActivityActivity.class);
        intent.putExtra(MyContants.IDDRIVER, iddriver);

        intent.putExtra(MyContants.IDBOOKING, idboking);
        startActivity(intent);
    }
}
