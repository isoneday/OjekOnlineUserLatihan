package com.imastudio.ojekonlineuserjuly;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imastudio.ojekonlineuserjuly.helper.DirectionMapsV2;
import com.imastudio.ojekonlineuserjuly.helper.GPSTracker;
import com.imastudio.ojekonlineuserjuly.helper.HeroHelper;
import com.imastudio.ojekonlineuserjuly.helper.MyContants;
import com.imastudio.ojekonlineuserjuly.helper.SessionManager;
import com.imastudio.ojekonlineuserjuly.model.Distance;
import com.imastudio.ojekonlineuserjuly.model.Duration;
import com.imastudio.ojekonlineuserjuly.model.LegsItem;
import com.imastudio.ojekonlineuserjuly.model.ResponseBooking;
import com.imastudio.ojekonlineuserjuly.model.ResponseWaypoint;
import com.imastudio.ojekonlineuserjuly.model.RoutesItem;
import com.imastudio.ojekonlineuserjuly.network.InitRetrofit;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoRideActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.imgpick)
    ImageView imgpick;
    @BindView(R.id.lokasiawal)
    TextView lokasiawal;
    @BindView(R.id.lokasitujuan)
    TextView lokasitujuan;
    @BindView(R.id.edtcatatan)
    EditText edtcatatan;
    @BindView(R.id.txtharga)
    TextView txtharga;
    @BindView(R.id.txtjarak)
    TextView txtjarak;
    @BindView(R.id.txtdurasi)
    TextView txtdurasi;
    @BindView(R.id.requestorder)
    Button requestorder;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
    private GoogleMap mMap;
    private GPSTracker gps;
    private double latawal;
    private double lonawal;
    private String namelocation;
    private LatLng lokasiku;
    private double latakhir;
    private double lonakhir;
    private List<RoutesItem> dataRoute;
    private List<LegsItem> dataLegs;
    private Distance jarak;
    private Duration durasi;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goride);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        HeroHelper.cekStatusGPS(this);
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
            gps = new GPSTracker(this);
        myLocation();
    }

    private void myLocation() {
        if (gps.canGetLocation()) {
            latawal = gps.getLatitude();
            lonawal = gps.getLongitude();
            namelocation = posisiku(latawal, lonawal);
            lokasiawal.setText(namelocation);
            //buat objek untuk mengatur tampilan map
            lokasiku = new LatLng(latawal, lonawal);
            mMap.addMarker(new MarkerOptions().position(lokasiku).title(namelocation))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiku, 17));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    private String posisiku(double latawal, double lonawal) {
        namelocation = null;
        Geocoder geocoder = new Geocoder(GoRideActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(latawal, lonawal, 1);
            if (list != null && list.size() > 0) {
                namelocation = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();

                //fetch data from addresses
            } else {
                Toast.makeText(GoRideActivity.this, "kosong", Toast.LENGTH_SHORT).show();
                //display Toast message
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return namelocation;
    }

    @OnClick({R.id.lokasiawal, R.id.lokasitujuan, R.id.requestorder})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lokasiawal:
                setLokasi(MyContants.LOKASIAWAL);
                break;
            case R.id.lokasitujuan:
                setLokasi(MyContants.LOKASITUJUAN);
                break;
            case R.id.requestorder:
                requestOrderan();
                break;
        }
    }

    private void requestOrderan() {
        manager = new SessionManager(this);
        String device = HeroHelper.getDeviceUUID(this);
        String token = manager.getToken();
        String iduser =manager.getIdUser();
        Float jarak = Float.parseFloat(HeroHelper.removeLastChar(txtjarak.getText().toString()));
        String awal = lokasiawal.getText().toString();
        String akhir =lokasitujuan.getText().toString();
        String catatan = edtcatatan.getText().toString();
        InitRetrofit.getInstance().insertBooking(
                device,
                token,
                jarak,
                iduser,
                String.valueOf(latawal),
                String.valueOf(lonawal),
                awal,
                String.valueOf(latakhir),
                        String.valueOf(lonakhir),
                akhir,
                catatan
        ).enqueue(new Callback<ResponseBooking>() {
            @Override
            public void onResponse(Call<ResponseBooking> call, Response<ResponseBooking> response) {
                String result= response.body().getResult();
                String msg= response.body().getMsg();
                if (result.equals("true")){
                    Toast.makeText(GoRideActivity.this, msg, Toast.LENGTH_SHORT).show();
                   int idbooking =response.body().getIdBooking();
                   Intent a = new Intent(GoRideActivity.this,WaitingDriverActivity.class);
                   a.putExtra("idbooking",idbooking);
                   manager.setBooking(String.valueOf(idbooking));
                   startActivity(a);
                }else{
                    Toast.makeText(GoRideActivity.this, msg, Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<ResponseBooking> call, Throwable t) {
                Toast.makeText(GoRideActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    //untuk menampilkan pencarian lokasi
    private void setLokasi(int lokasi) {
        AutocompleteFilter filter = new AutocompleteFilter.Builder().
                setCountry("ID")
                .build();

        Intent i = null;
        try {
            i = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(filter)
                    .build(GoRideActivity.this);
            startActivityForResult(i, lokasi);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    //untuk menangkap return dari activty for result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyContants.LOKASIAWAL && resultCode == RESULT_OK) {
            Place p = PlaceAutocomplete.getPlace(this, data);
            latawal = p.getLatLng().latitude;
            lonawal = p.getLatLng().longitude;
            LatLng awal = new LatLng(latawal, lonawal);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(awal).title(namelocation))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup));

            namelocation = p.getAddress().toString();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(awal, 17));
            lokasiawal.setText(namelocation);
        } else if (requestCode == MyContants.LOKASITUJUAN && resultCode == RESULT_OK) {
            Place p = PlaceAutocomplete.getPlace(this, data);
            latakhir = p.getLatLng().latitude;
            lonakhir = p.getLatLng().longitude;
            LatLng akhir = new LatLng(latakhir, lonakhir);
//            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(akhir).title(namelocation))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup));

            namelocation = p.getAddress().toString();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(akhir, 17));
            lokasitujuan.setText(namelocation);
            aksesRute();
        }
    }

    private void aksesRute() {
        String key = getString(R.string.google_maps_key);
        InitRetrofit.getInstanceGoogle().setRute(
                lokasiawal.getText().toString(),
                lokasitujuan.getText().toString(),
                key
        ).enqueue(new Callback<ResponseWaypoint>() {
            @Override
            public void onResponse(Call<ResponseWaypoint> call, Response<ResponseWaypoint> response) {
             String status = response.body().getStatus();
             if (status.equals("OK")){
                 dataRoute = response.body().getRoutes();
                dataLegs =dataRoute.get(0).getLegs();
                jarak =dataLegs.get(0).getDistance();
                durasi =dataLegs.get(0).getDuration();
                double harga= Double.parseDouble(HeroHelper.removeLastChar(jarak.getText()))*10000;
                txtjarak.setText(jarak.getText());
                txtdurasi.setText(durasi.getText());
                txtharga.setText(HeroHelper.toRupiahFormat2(String.valueOf(harga)));
                //untuk membuat garis dari 1 titik ke titk ke 2
                 DirectionMapsV2 mapsV2 = new DirectionMapsV2(GoRideActivity.this);
                 String datapoint = dataRoute.get(0).getOverviewPolyline().getPoints();
                 mapsV2.gambarRoute(mMap, datapoint);
             }
            }

            @Override
            public void onFailure(Call<ResponseWaypoint> call, Throwable t) {

            }
        });
    }
}
