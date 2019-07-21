package com.imastudio.ojekonlineuserjuly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.imastudio.ojekonlineuserjuly.helper.HeroHelper;
import com.imastudio.ojekonlineuserjuly.helper.MyContants;
import com.imastudio.ojekonlineuserjuly.helper.SessionManager;
import com.imastudio.ojekonlineuserjuly.model.DataDetailDriver;
import com.imastudio.ojekonlineuserjuly.model.ResponseDetailDriver;
import com.imastudio.ojekonlineuserjuly.network.InitRetrofit;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivityActivity extends AppCompatActivity {

    @BindView(R.id.txtReview)
    TextView txtReview;
    @BindView(R.id.ivReviewFoto)
    ImageView ivReviewFoto;
    @BindView(R.id.txtReviewUserNama)
    TextView txtReviewUserNama;
    @BindView(R.id.ratingReview)
    RatingBar ratingReview;
    @BindView(R.id.txtReview2)
    TextView txtReview2;
    @BindView(R.id.edtReviewComment)
    MaterialEditText edtReviewComment;
    @BindView(R.id.txtReview3)
    TextView txtReview3;
    @BindView(R.id.cboReview)
    CheckBox cboReview;
    @BindView(R.id.btnReview)
    Button btnReview;
    private SessionManager manager;
    private String idbooking;
    private String iddriver;
    private float nilaiRating;
    private List<DataDetailDriver> dataRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_activity);
        ButterKnife.bind(this);
        manager = new SessionManager(this);
        idbooking = getIntent().getStringExtra(MyContants.IDBOOKING);
        iddriver = getIntent().getStringExtra(MyContants.IDDRIVER);
        ratingReview.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                nilaiRating = rating;
            }
        });
    }

    @OnClick(R.id.btnReview)
    public void onViewClicked() {
        String token = manager.getToken();
        String device = HeroHelper.getDeviceUUID(this);
        String iduser = manager.getIdUser();
        String comment = edtReviewComment.getText().toString();
        InitRetrofit.getInstance().review(
                token,
                device,
                iduser,
                iddriver,
                idbooking,
                String.valueOf(nilaiRating),
                comment
        ).enqueue(new Callback<ResponseDetailDriver>() {
            @Override
            public void onResponse(Call<ResponseDetailDriver> call, Response<ResponseDetailDriver> response) {
                String result = response.body().getResult();
                String msg = response.body().getMsg();
                if (result.equals("true")) {
                    Toast.makeText(ReviewActivityActivity.this, msg, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ReviewActivityActivity.this,MainActivity.class));
                } else {
                    Toast.makeText(ReviewActivityActivity.this, msg, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseDetailDriver> call, Throwable t) {
                Toast.makeText(ReviewActivityActivity.this, "gagal" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
