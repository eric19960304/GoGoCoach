package hkucs.comp3330.gogocoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import hkucs.comp3330.gogocoach.firebase.Classes;

public class EditClass extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Button addClassButton;
    private Button resetButton;
    private EditText className;
    private EditText description;
    private EditText noOfPeople;
    private EditText type;
    private EditText price;
    private TextView location;
    private Button locationButton;
    private EditText time;
    private Double latitude;
    private Double longitude;
    private final static int PLACE_PICKER_REQUEST = 999;
    private String originalName;

    private DatabaseReference mRootRef;
    private DatabaseReference classRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        View view =inflater.inflate(R.layout.fragment_post_class, container, false);
        addClassButton = (Button) view.findViewById(R.id.addClassButton);
        resetButton = (Button) view.findViewById(R.id.resetButton);
        className = (EditText) view.findViewById(R.id.className);
        description = (EditText) view.findViewById(R.id.description);
        noOfPeople = (EditText) view.findViewById(R.id.people);
        type= (EditText) view.findViewById(R.id.type);
        time= (EditText) view.findViewById(R.id.time);
        locationButton= (Button) view.findViewById(R.id.pickLocationButton);
        price= (EditText) view.findViewById(R.id.price);
        location = (TextView) view.findViewById((R.id.location));

        Bundle bundle = this.getArguments();
        addClassButton.setText( "EDIT CLASS" );

        originalName=( bundle.getString("classNameID") );
        className.setText( bundle.getString("classNameID") );
        description.setText( bundle.getString("classdescription") );
        noOfPeople.setText( bundle.getString("classnumber") );
        type.setText( bundle.getString("classtype") );
        time.setText( bundle.getString("classtime") );
        price.setText( bundle.getString("classprice") );
        location.setText(String.format(bundle.getString("classlocation") ));

        latitude = ( bundle.getDouble("classlatitude") );
        longitude = ( bundle.getDouble("classlongitude") );

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open location picker
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                LatLng southwest = new LatLng(22.224705, 113.887081);
                LatLng northeast = new LatLng(22.483973, 114.300893);
                LatLngBounds bound = new LatLngBounds(southwest, northeast);
                builder.setLatLngBounds(bound);
                try {
                    // for activty
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                    // for fragment
                    //startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(description.getText().toString().equals("") ||
                        noOfPeople.getText().toString().equals("") ||
                        className.getText().toString().equals("") ||
                        price.getText().toString().equals("") ||
                        time.getText().toString().equals("") ||
                        type.getText().toString().equals("") ||
                        location.getText().toString().equals("")
                ){
                    Toast.makeText(getContext(), "Please enter all information!", Toast.LENGTH_LONG).show();
                    return;
                }

                String userId=mFirebaseUser.getUid();
                String name=mFirebaseUser.getDisplayName();
                String photoUrl = mFirebaseUser.getPhotoUrl().toString();
                Classes data = new Classes(
                        userId,
                        name,
                        description.getText().toString(),
                        noOfPeople.getText().toString(),
                        className.getText().toString(),
                        price.getText().toString(),
                        time.getText().toString(),
                        type.getText().toString(),
                        location.getText().toString(),
                        latitude,
                        longitude,
                        photoUrl
                );

                mRootRef.child("classes").child(userId).child(originalName).removeValue();
                mRootRef.child("classes").child(userId).child(className.getText().toString()).setValue(data);

                Toast.makeText(getContext(), "Class is updated.", Toast.LENGTH_LONG).show();

                Fragment fragment = new MyPostedClassFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);

                fragmentTransaction.commit();


            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                className.setText("");
                description .setText("");
                noOfPeople.setText("");
                type.setText("");
                time.setText("");
                price.setText("");
            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(getActivity(), data);
                    location.setText(String.format("Location: %s", place.getName()));
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
            }
        }
    }

}