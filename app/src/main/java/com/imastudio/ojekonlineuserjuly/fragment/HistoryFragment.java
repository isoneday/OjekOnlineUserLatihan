package com.imastudio.ojekonlineuserjuly.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.imastudio.ojekonlineuserjuly.R;
import com.imastudio.ojekonlineuserjuly.adapter.CustomRecyclerAdapter;
import com.imastudio.ojekonlineuserjuly.helper.HeroHelper;
import com.imastudio.ojekonlineuserjuly.helper.SessionManager;
import com.imastudio.ojekonlineuserjuly.model.DataHistory;
import com.imastudio.ojekonlineuserjuly.model.ResponseHistory;
import com.imastudio.ojekonlineuserjuly.network.InitRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class HistoryFragment extends Fragment {


    int status;
    private RecyclerView listhistory;
    private SessionManager manager;
    public static List<DataHistory> dataHistoryProses;

    public static List<DataHistory> dataHistoryComplete;

    public HistoryFragment(int i) {
        // Required empty public constructor
        status = i;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_proses, container, false);
        listhistory = v.findViewById(R.id.recyclerview);
        getDataHistory();
        return v;
    }

    private void getDataHistory() {
        manager = new SessionManager(getActivity());
        String device = HeroHelper.getDeviceUUID(getActivity());
        String token = manager.getToken();
        String iduser = manager.getIdUser();
        String idbooking =manager.getBooking();
        if (status == 2) {
            InitRetrofit.getInstance().getDataHistory(token, device, String.valueOf(status), iduser).enqueue(new Callback<ResponseHistory>() {
                @Override
                public void onResponse(Call<ResponseHistory> call, Response<ResponseHistory> response) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        dataHistoryProses = response.body().getData();
                        CustomRecyclerAdapter  recyclerAdapter = new CustomRecyclerAdapter(dataHistoryProses,getActivity(),status);
                        listhistory.setAdapter(recyclerAdapter);
                        listhistory.setLayoutManager(new LinearLayoutManager(getActivity()));

                    } else {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<ResponseHistory> call, Throwable t) {
                    Toast.makeText(getActivity(), "gagal" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            InitRetrofit.getInstance().getDataHistory(token, device, String.valueOf(status), iduser).enqueue(new Callback<ResponseHistory>() {
                @Override
                public void onResponse(Call<ResponseHistory> call, Response<ResponseHistory> response) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        dataHistoryComplete = response.body().getData();

                        CustomRecyclerAdapter  recyclerAdapter = new CustomRecyclerAdapter(dataHistoryComplete,getActivity(),status);
                        listhistory.setAdapter(recyclerAdapter);
                        listhistory.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }else{
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                    }
                    }

                @Override
                public void onFailure(Call<ResponseHistory> call, Throwable t) {

                }
            });
        }
    }

}
