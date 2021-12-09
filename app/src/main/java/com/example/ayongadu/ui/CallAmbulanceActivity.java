package com.example.ayongadu.ui;

import static android.view.View.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.ayongadu.R;
import com.example.ayongadu.database.DatabaseBuilder;
import com.example.ayongadu.databinding.ActivityCallAmbulanceBinding;
import com.example.ayongadu.model.Ambulance;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallAmbulanceActivity extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {
    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private ImageView hoveringMarker;
    private Layer droppedMarkerLayer;

    private ActivityCallAmbulanceBinding binding;
    private Ambulance ambulance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_ambulance);

        ambulance = new Ambulance();
        binding.setOnSaveButtonClicked(onSaveButtonClicked);
        binding.setOnBackButtonClicked(onBackButtonClicked);
        binding.setAmbulance(ambulance);
        // Initialize the mapboxMap view
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);

        binding.imageView.setOnClickListener(v -> dispatchTakePictureIntent());
    }

    public View.OnClickListener onSaveButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(CallAmbulanceActivity.this, "Mohon tunggu", Toast.LENGTH_SHORT).show();
        }
    };

    public View.OnClickListener onBackButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        CallAmbulanceActivity.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull final Style style) {
                enableLocationPlugin(style);
                hoveringMarker = new ImageView(CallAmbulanceActivity.this);
                hoveringMarker.setImageResource(R.drawable.red_marker);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                hoveringMarker.setLayoutParams(params);
                binding.mapView.addView(hoveringMarker);

                initDroppedMarker(style);

                onSaveButtonClicked = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hoveringMarker.getVisibility() == VISIBLE) {
                            LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

                            if (ambulance.getName() == null || ambulance.getPhone() == null || ambulance.getName().isEmpty() || ambulance.getPhone().isEmpty()) {
                                Toast.makeText(CallAmbulanceActivity.this, "Harap lengkapi form!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            saveCallAmbulanceRequest(ambulance.getName(), ambulance.getPhone(), mapTargetLatLng.getLatitude(), mapTargetLatLng.getLongitude());

                            hoveringMarker.setVisibility(View.INVISIBLE);
                            binding.cardSave.setCardBackgroundColor(
                                    ContextCompat.getColor(CallAmbulanceActivity.this, R.color.background));

                            if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                                if (source != null) {
                                    source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                                }
                                droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                                if (droppedMarkerLayer != null) {
                                    droppedMarkerLayer.setProperties(visibility(Property.VISIBLE));
                                }
                            }

                            reverseGeocode(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                        } else {
                            binding.cardSave.setCardBackgroundColor(
                                    ContextCompat.getColor(CallAmbulanceActivity.this, R.color.primary));

                            hoveringMarker.setVisibility(VISIBLE);

                            droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                            if (droppedMarkerLayer != null) {
                                droppedMarkerLayer.setProperties(visibility(NONE));
                            }
                        }
                    }
                };
                binding.setOnSaveButtonClicked(onSaveButtonClicked);
            }
        });
    }

    private void saveCallAmbulanceRequest(String name, String phone, double latitude, double longitude) {
        class SaveCallAmbulance extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                String date = new Date().toString();
                com.example.ayongadu.database.entity.Ambulance ambulance = new
                        com.example.ayongadu.database.entity.Ambulance(name, phone, date, latitude, longitude);

                DatabaseBuilder.getInstance(CallAmbulanceActivity.this)
                        .getDatabase()
                        .appDao()
                        .insertAmbulance(ambulance);

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                startActivity(new Intent(CallAmbulanceActivity.this, HomeActivity.class));
                finish();
            }
        }

        SaveCallAmbulance callAmbulance = new SaveCallAmbulance();
        callAmbulance.execute();
    }

    private void initDroppedMarker(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("dropped-icon-image", Objects.requireNonNull(AppCompatResources.getDrawable(this, R.drawable.blue_marker)));
        loadedMapStyle.addSource(new GeoJsonSource("dropped-marker-source-id"));
        loadedMapStyle.addLayer(new SymbolLayer(DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id").withProperties(
                iconImage("dropped-icon-image"),
                visibility(NONE),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        ));
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        binding.mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Izin lokasi diperlukan!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted && mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                enableLocationPlugin(style);
            }
        } else {
            Toast.makeText(this, "Izin tidak didapatkan", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * This method is used to reverse geocode where the user has dropped the marker.
     *
     * @param point The location to use for the search
     */
    private void reverseGeocode(final Point point) {
        try {
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(getString(R.string.mapbox_access_token))
                    .query(Point.fromLngLat(point.longitude(), point.latitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                    if (response.body() != null) {
                        List<CarmenFeature> results = response.body().features();
                        if (results.size() > 0) {
                            CarmenFeature feature = results.get(0);

                            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                        Toast.makeText(CallAmbulanceActivity.this, feature.placeName(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(CallAmbulanceActivity.this,
                                    "Koordinat: (lat: " + point.latitude() + ") (long: " + point.longitude() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    Log.e("Geocoding Failure: %s", throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            Log.e("Error geocoding: %s", servicesException.toString());
            servicesException.printStackTrace();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(
                    this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.NORMAL);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            binding.imageView.setImageBitmap(imageBitmap);
        }
    }
}