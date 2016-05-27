package com.franklinho.ridecell.networking;

import com.franklinho.ridecell.models.ParkingLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;


/**
 * Created by franklinho on 5/26/16.
 */
public class RideCellApi  {
    private static AsyncHttpClient client = new AsyncHttpClient();
    public static final String BASE_URL = "http://ridecellparking.herokuapp.com/api/v1/";

    public static void getParkingLocations(JsonHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl("parkinglocations"), responseHandler);
    }

    public static void getParkingLocations(Double latitude, Double longitude, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("lat", latitude);
        params.put("lng", longitude);
        client.get(getAbsoluteUrl("parkinglocations/search"), params, responseHandler);
    }

    public static void postMakeReservation(String parkingId, String minutes, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("minutes", minutes);
        client.post(getAbsoluteUrl("parkinglocations/"+ parkingId+"/reserve/"), params, responseHandler);
    }



    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
