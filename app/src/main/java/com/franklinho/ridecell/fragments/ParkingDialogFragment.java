package com.franklinho.ridecell.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.franklinho.ridecell.R;
import com.franklinho.ridecell.models.ParkingLocation;
import com.franklinho.ridecell.networking.RideCellApi;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by franklinho on 5/26/16.
 */
public class ParkingDialogFragment extends DialogFragment {
    @Bind(R.id.tvCharge) TextView tvCharge;
    @Bind(R.id.tvNumber) TextView tvNumber;
    @Bind(R.id.tvName) TextView tvName;
    @Bind(R.id.btnReserve) Button btnReserve;
    ParkingLocation parkingLocation;
    @Bind(R.id.etReservationTime)
    EditText etReservationTime;
    @Bind(R.id.tvMinReserve) TextView tvMinReserve;
    @Bind(R.id.tvMaxReserve) TextView tvMaxReserve;
    @Bind(R.id.pbProgessAction) View pbProgressAction;

    public ParkingDialogFragment() {

    }

    public static ParkingDialogFragment newInstance(ParkingLocation parkingLocation) {

        Bundle args = new Bundle();

        ParkingDialogFragment fragment = new ParkingDialogFragment();
        args.putParcelable("parkingLocation", parkingLocation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parkingLocation = getArguments().getParcelable("parkingLocation");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_parking, container);
        ButterKnife.bind(this, v);
        tvCharge.setText("$" + parkingLocation.getCostPerMinute());
        tvName.setText(parkingLocation.getName());
        tvNumber.setText(parkingLocation.getId().toString());
        tvMinReserve.setText(parkingLocation.getMinReserveTimeMins().toString());
        tvMaxReserve.setText(parkingLocation.getMaxReserveTimeMins().toString());

        if (parkingLocation.getIsReserved()) {
            btnReserve.setVisibility(View.GONE);
            etReservationTime.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.btnReserve)
    public void makeReservation(View view) {
        if (etReservationTime.getText().toString().equals("0")) {
            Toast.makeText(getContext(), "Enter a number greater than zero to reserve", Toast.LENGTH_LONG).show();
        } else if (Integer.valueOf(etReservationTime.getText().toString()) > parkingLocation.getMaxReserveTimeMins()) {
            Toast.makeText(getContext(), "Requested time exceeds maximum reservation time.", Toast.LENGTH_LONG).show();
        } else if (Integer.valueOf(etReservationTime.getText().toString()) < parkingLocation.getMinReserveTimeMins()) {
            Toast.makeText(getContext(), "Requested time is less than minimum reservation time.", Toast.LENGTH_LONG).show();
        } else {
//            showProgressBar();
            RideCellApi.postMakeReservation(parkingLocation.getId().toString(), etReservationTime.getText().toString(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    hideProgressBar();
                    ParkingLocation returnedLocation = ParkingLocation.fromJSONObject(response);
                    String successString = "Reservation successfully made.\nName: " + parkingLocation.getName() + "\nLength: " + etReservationTime.getText().toString() + "\nReserved Until: " + returnedLocation.getReservedUntil();
                    Toast.makeText(getContext(), successString, Toast.LENGTH_LONG).show();
                    getDialog().dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    hideProgressBar();
                    Toast.makeText(getContext(), "Unable to make reservation: ", Toast.LENGTH_LONG).show();
                    Log.d("RideCell", responseString);
                }
            });

        }
    }

    public void showProgressBar() {
        // Show progress item
        pbProgressAction.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        // Hide progress item
        pbProgressAction.setVisibility(View.GONE);
    }
}
