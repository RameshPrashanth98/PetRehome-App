package PostAd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oop.petrehome.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import user.Login;

public class DisplayDogAd extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private float x1,x2,y1,y2;
    private int MIN_DISTANCE =150;
    private GestureDetector gestureDetector;

    TextView  display_dog_ad_title,display_dog_ad_location,display_dog_ad_breed,
            display_dog_ad_age,display_dog_ad_gender,display_dog_ad_size,display_dog_ad_description,
            display_dog_ad_date,display_dog_ad_email,display_dog_ad_mobile,t1,t2,t3,t4,t5,t6,t7,t8,view_count_txt,dog_listing_days_on_site;

    ImageView display_dog_ad_image;
    String dis,city,verificationDONE="";

    Button display_dog_ad_send_msg,display_dog_ad_call,display_dog_ad_edit_btn,verified_user_txt,unverified_user_txt;
    ProgressBar progressBar_display_ad,progressBar_display_ad_img;

    DatabaseReference databaseReference,databaseReferenceUSER;
    FirebaseAuth fAuth;
    DocumentReference documentReference;
    FirebaseFirestore fstore;
    StorageReference storageReference;
    String userID ,USERID,IMGNUMBER;
    ImageSlider imageSlider;
    List<SlideModel> slideModels;
    Boolean alreadyExecuted =false;
    int viewCount;
    public Long VCcount;
    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    Date pdate,cdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_dog_ad);
         this.gestureDetector =new GestureDetector(getApplicationContext(),this);


        display_dog_ad_title =findViewById(R.id.display_dog_ad_title);
        display_dog_ad_location =findViewById(R.id.display_dog_ad_location);
        display_dog_ad_breed =findViewById(R.id.display_dog_ad_breed);
        display_dog_ad_age =findViewById(R.id.display_dog_ad_age);
        display_dog_ad_gender =findViewById(R.id.display_dog_ad_gender);
        display_dog_ad_size =findViewById(R.id.display_dog_ad_size);
        display_dog_ad_description =findViewById(R.id.display_dog_ad_description);
        display_dog_ad_date =findViewById(R.id.display_dog_ad_date);
        display_dog_ad_email =findViewById(R.id.display_dog_ad_email);
        display_dog_ad_mobile =findViewById(R.id.display_dog_ad_mobile);
        verified_user_txt =findViewById(R.id.verified_user_txt);
        unverified_user_txt =findViewById(R.id.unverified_user_txt);
        view_count_txt =findViewById(R.id.view_count_txt);
        dog_listing_days_on_site =findViewById(R.id.dog_listing_days_on_site);

        t1 =findViewById(R.id.textView15);
        t2 =findViewById(R.id.textView16);
        t3 =findViewById(R.id.textView17);
        t4 =findViewById(R.id.textView18);
        t5 =findViewById(R.id.textView23);
        t6 =findViewById(R.id.textView25);
        t7 =findViewById(R.id.textView27);
        t8 =findViewById(R.id.textView28);

        progressBar_display_ad =findViewById(R.id.progressBar_display_ad);
        progressBar_display_ad_img =findViewById(R.id.progressBar_display_ad_img);

        progressBar_display_ad.setVisibility(View.VISIBLE);
        progressBar_display_ad_img.setVisibility(View.VISIBLE);

        display_dog_ad_send_msg =findViewById(R.id.display_dog_ad_send_msg);
        display_dog_ad_call =findViewById(R.id.display_dog_ad_call);
        display_dog_ad_edit_btn =findViewById(R.id.display_dog_ad_edit_btn);

        //hide all text
        hideText();

        //get current date
        try {
            cdate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        imageSlider =findViewById(R.id.display_dog_ad_image);

        slideModels = new ArrayList<>();

         USERID = getIntent().getExtras().getString("USERID");
         IMGNUMBER = getIntent().getExtras().getString("IMGNUMBER");


         //fetching values from DB
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DogListings").child(USERID).child("Listings").child(IMGNUMBER);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.exists()){
                    display_dog_ad_title.setText(snapshot.child("title").getValue().toString());
                    display_dog_ad_breed.setText(snapshot.child("breed").getValue().toString());
                    display_dog_ad_gender.setText(snapshot.child("gender").getValue().toString());
                    display_dog_ad_age.setText(snapshot.child("age").getValue().toString());
                    display_dog_ad_size.setText(snapshot.child("size").getValue().toString());
                    display_dog_ad_description.setText(snapshot.child("description").getValue().toString());
                    display_dog_ad_email.setText(snapshot.child("email").getValue().toString());
                    display_dog_ad_mobile.setText(snapshot.child("phone").getValue().toString());
                    display_dog_ad_date.setText(snapshot.child("date").getValue().toString());
                    display_dog_ad_location.setText(snapshot.child("city").getValue().toString()+", "+snapshot.child("district").getValue().toString());
                    dis = snapshot.child("district").getValue().toString();
                    city = snapshot.child("city").getValue().toString();
                    VCcount = (Long) snapshot.child("viewCount").getValue();
                    viewCount = VCcount.intValue();
                    progressBar_display_ad.setVisibility(View.INVISIBLE);

                    try {
                        pdate = sdf.parse(Objects.requireNonNull(snapshot.child("date").getValue()).toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //show all text after loading completed
                    showText();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        //display verified or not verified on listing
        databaseReferenceUSER = FirebaseDatabase.getInstance().getReference().child("users").child(USERID);
        databaseReferenceUSER.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    verificationDONE =snapshot.child("verified").getValue().toString();
                    if (verificationDONE.equals("y")){
                        verified_user_txt.setVisibility(View.VISIBLE);
                    }else {
                        unverified_user_txt.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //displaying images on the slider
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef=  storageRef.child("users/"+USERID+"/"+ IMGNUMBER+"/img1.jpg");
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (!(uri.equals(Uri.EMPTY))){
                    slideModels.add(new SlideModel(uri.toString()));
                    imageSlider.setImageList(slideModels,true);
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                for (int i =2;i<5;i++){
                    Integer x = (Integer)i;
                    StorageReference fileRef=  storageRef.child("users/"+USERID+"/"+ IMGNUMBER+"/img"+x.toString() +".jpg");
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (!(uri.equals(Uri.EMPTY))){
                                slideModels.add(new SlideModel(uri.toString()));
                            }
                            imageSlider.setImageList(slideModels,true);

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            progressBar_display_ad_img.setVisibility(View.INVISIBLE);
                        }
                    });
                }


            }
        });


        //call button function
        display_dog_ad_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + display_dog_ad_mobile.getText().toString()));
                startActivity(intent);
            }
        });

        //send email button function
        display_dog_ad_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uriText =
                        "mailto:"+display_dog_ad_email.getText().toString() +
                                "?subject=" + Uri.encode("PetRehome : "+display_dog_ad_title.getText().toString()) +
                                "&body=" + Uri.encode("");

                Uri uri = Uri.parse(uriText);

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                if (sendIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(sendIntent, "Send email"));
                }
            }
        });

        //location button function
        display_dog_ad_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + display_dog_ad_location.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        //edit button
        display_dog_ad_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), EditDogListing.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("USERID",USERID);
                intent.putExtra("IMGNUMBER",IMGNUMBER);

                intent.putExtra("TITLE",display_dog_ad_title.getText().toString());
                intent.putExtra("BREED",display_dog_ad_breed.getText().toString());
                intent.putExtra("AGE",display_dog_ad_age.getText().toString());
                intent.putExtra("GENDER",display_dog_ad_gender.getText().toString());
                intent.putExtra("SIZE",display_dog_ad_size.getText().toString());
                intent.putExtra("DESCRIPTION",display_dog_ad_description.getText().toString());
                intent.putExtra("EMAIL",display_dog_ad_email.getText().toString());
                intent.putExtra("MOBILE",display_dog_ad_mobile.getText().toString());
                intent.putExtra("DISTRICT",dis);
                intent.putExtra("CITY",city);
                startActivity(intent);

            }
        });




    }

    public Long dateDifferent(Date pdate,Date cdate){
        long diff = cdate.getTime() - pdate.getTime();
        TimeUnit time = TimeUnit.DAYS;
        long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);

        return diffrence;

    }


    //slide down for back navigation
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                float valueX = x2-x1;
                float valueY = y2-y1;
                if (Math.abs(valueX)> MIN_DISTANCE){
                    if (x2>x1){

                    }
                }
                else if (Math.abs(valueY)> MIN_DISTANCE){
                    if (y2>y1){
                        finish();
                    }

                }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public  void hideText(){
        t1.setVisibility(View.INVISIBLE);
        t2.setVisibility(View.INVISIBLE);
        t3.setVisibility(View.INVISIBLE);
        t4.setVisibility(View.INVISIBLE);
        t5.setVisibility(View.INVISIBLE);
        t6.setVisibility(View.INVISIBLE);
        t7.setVisibility(View.INVISIBLE);
        t8.setVisibility(View.INVISIBLE);
        display_dog_ad_send_msg.setVisibility(View.INVISIBLE);
        display_dog_ad_call.setVisibility(View.INVISIBLE);
    }
    @SuppressLint("SetTextI18n")
    public  void showText(){
        t1.setVisibility(View.VISIBLE);
        t2.setVisibility(View.VISIBLE);
        t3.setVisibility(View.VISIBLE);
        t4.setVisibility(View.VISIBLE);
        t5.setVisibility(View.VISIBLE);
        t6.setVisibility(View.VISIBLE);
        t7.setVisibility(View.VISIBLE);
        t8.setVisibility(View.VISIBLE);
        display_dog_ad_send_msg.setVisibility(View.VISIBLE);
        display_dog_ad_call.setVisibility(View.VISIBLE);

        //display days on site
        if (dateDifferent(pdate,cdate)==0){
            dog_listing_days_on_site.setText("Less than 24h ago");
        }else if(dateDifferent(pdate,cdate)==1){
            dog_listing_days_on_site.setText(dateDifferent(pdate,cdate).toString()+" Day ago");
        }else{
            dog_listing_days_on_site.setText(dateDifferent(pdate,cdate).toString()+" Days ago");
        }

        dog_listing_days_on_site.setVisibility(View.VISIBLE);



        //view count
        if(!alreadyExecuted) {
            viewCount();
            alreadyExecuted = true;
        }


    }

    private void viewCount(){

        //show edit button if listing is own by current user
        if (fAuth.getCurrentUser() != null)
            if ( fAuth.getCurrentUser().getUid().equals(USERID)){
                display_dog_ad_edit_btn.setVisibility(View.VISIBLE);
            } else display_dog_ad_edit_btn.setVisibility(View.INVISIBLE);

            //increase view count by 1 and save in DB
        Map<String,Object> viewUser = new HashMap<>();
        viewUser.put("viewCount", ++viewCount);
        databaseReference.updateChildren(viewUser);
        view_count_txt.setText(String.valueOf(viewCount)+" views");
        view_count_txt.setVisibility(View.VISIBLE);
    }



}