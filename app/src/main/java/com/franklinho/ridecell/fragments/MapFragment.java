package com.franklinho.ridecell.fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.franklinho.ridecell.R;
import com.franklinho.ridecell.adapters.MapInfoWindowAdapter;
import com.franklinho.ridecell.models.ParkingLocation;

import com.franklinho.ridecell.networking.RideCellApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;


import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


/**
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@RuntimePermissions
public class MapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMarkerClickListener{

    /*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private List<ParkingLocation> parkingLocations;
    private Map<Integer, ParkingLocation> parkingLocationMap;
    private List<Marker> markers;
    @Bind(R.id.btnSearchMap) Button btnSearchMap;
    @Bind(R.id.pbProgessAction) View pbProgressAction;
    boolean userGeneratedCameraChange = false;



    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parkingLocations = new ArrayList<>();
        markers = new ArrayList<>();
        parkingLocationMap = new HashMap<>();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        if (map != null) {
            // Now that map has loaded, let's get our location!
            map.setMyLocationEnabled(true);
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            connectClient();
        }
    }

    @OnClick(R.id.btnSearchMap)
    public void searchMap(View view) {
        Log.d("RideCell", "search map clicked!");
        requestParkingLocations(false);
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getFragmentManager(), "Location Updates");
            }
            return false;
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mapFragment = new SupportMapFragment();
        android.support.v4.app.FragmentTransaction
                transaction = getChildFragmentManager().beginTransaction();

        transaction.replace(R.id.map, mapFragment).commit();
        setUpMapIfNeeded();
    }

    private void requestParkingLocations(final boolean initialRequest) {
//        showProgressBar();
        parkingLocations.clear();
        final int currentSize = 0;
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                hideProgressBar();
                parkingLocations.clear();
                parkingLocations.addAll(ParkingLocation.fromJSONArray(response));
                Toast.makeText(getContext(), "Successful request", Toast.LENGTH_SHORT).show();

                map.clear();
                for (int i = 0; i < parkingLocations.size(); i++) {
                    ParkingLocation parkingLocation = parkingLocations.get(i);
                    parkingLocationMap.put(parkingLocation.getId(), parkingLocation);
//                    Log.d("RideCell", parkingLocation.getName());

                    BitmapDescriptor defaultMarker;
                    if (parkingLocation.getIsReserved() == false) {
                        defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    } else {
                        defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    }



                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.valueOf(parkingLocation.getLat()), Double.valueOf(parkingLocation.getLng())))
                            .title(parkingLocation.getName())
                            .snippet(String.valueOf(i))
                            .icon(defaultMarker));

                    markers.add(marker);
                }

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                int padding = 10;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                userGeneratedCameraChange = false;
                map.animateCamera(cu);
                map.animateCamera(cu, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        userGeneratedCameraChange = false;
                    }

                    @Override
                    public void onCancel() {
                        userGeneratedCameraChange = true;

                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        };
        if (initialRequest == true) {
            RideCellApi.getParkingLocations(responseHandler);
        } else {
            LatLng currentMapTarget = map.getCameraPosition().target;

            if (currentMapTarget != null) {
                RideCellApi.getParkingLocations(currentMapTarget.latitude, currentMapTarget.longitude, responseHandler);
            } else {
                RideCellApi.getParkingLocations(responseHandler);
            }
        }

        hideMapSearchButton();


    }

    // The Map is verified. It is now safe to manipulate the map.
    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            map.setOnMarkerClickListener(this);
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    if (userGeneratedCameraChange) {
                        // reset all marker colors

                        // User caused onCameraChange...
                        if (btnSearchMap.getVisibility() != View.VISIBLE) {
                            showSearchMapButton();
                        }
                        for (Marker othermarker : markers) {
                            othermarker.setAlpha(1.0f);
                        }

                    } else {
                        // The next map move will be caused by user, unless we
                        // do another move programmatically
                        userGeneratedCameraChange = true;


                        // onCameraChange caused by program...
                    }
                }
            });
            MapFragmentPermissionsDispatcher.getMyLocationWithCheck(this);

        } else {
            Toast.makeText(getContext(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void setUpMapIfNeeded() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                loadMap(map);
                requestParkingLocations(true);
            }
        });
    }

    /*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
            map.moveCamera(cameraUpdate);

            if (markers.size() > 0) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                int padding = 100;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                userGeneratedCameraChange = false;
                map.animateCamera(cu);
                map.animateCamera(cu, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        userGeneratedCameraChange = false;
                    }

                    @Override
                    public void onCancel() {
                        userGeneratedCameraChange = true;

                    }
                });
            } else {
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
                map.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        userGeneratedCameraChange = false;
                    }

                    @Override
                    public void onCancel() {
                        userGeneratedCameraChange = true;

                    }
                });
            }
        } else {
            LatLng latLng = new LatLng(37.7749, 122.4194);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
            map.moveCamera(cameraUpdate);
        }
    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(getContext(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(getContext(), "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {


        //Makes no infowindow show up
        map.setInfoWindowAdapter(new MapInfoWindowAdapter(getContext()));

        //Call showInfoWindow to put marker on top of others.
        marker.showInfoWindow();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(marker.getPosition());
        map.animateCamera(cameraUpdate);


        FragmentManager fm = getFragmentManager();
        ParkingDialogFragment parkingDialogFragment = ParkingDialogFragment.newInstance(parkingLocations.get(Integer.valueOf(marker.getSnippet())));
        parkingDialogFragment.show(fm, "fragment_parking");
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        // do nothing
    }


    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    public void showSearchMapButton() {
        btnSearchMap.setVisibility(View.VISIBLE);
        ObjectAnimator fadeAltAnim = ObjectAnimator.ofFloat(btnSearchMap, View.ALPHA, 0, 1);
        fadeAltAnim.setDuration(1000);
        fadeAltAnim.start();
    }

    public void hideMapSearchButton() {

        ObjectAnimator fadeAltAnim = ObjectAnimator.ofFloat(btnSearchMap, View.ALPHA, 0, 1);
        fadeAltAnim.setDuration(1000);
        fadeAltAnim.start();
        btnSearchMap.setVisibility(View.GONE);
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setAllMarkersAlphaToOne() {
        for (final Marker marker : markers) {
            ValueAnimator ani = ValueAnimator.ofFloat(marker.getAlpha(), 1.0f); //change for (0,1) if you want a fade in
            ani.setDuration(500);
            ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    marker.setAlpha((float) animation.getAnimatedValue());
                }
            });
            ani.start();
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
