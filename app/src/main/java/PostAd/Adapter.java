package PostAd;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oop.petrehome.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

    StorageReference storageReference;
    String UserID;
    List<String> title;
    List<String> breed;
    List<String> district;
    List<String> city;
    List<String> gender;
    List<Integer> images;
    Context ctx;
    LayoutInflater layoutInflater;

    public Adapter (Context ctx,List<String> title,List<String> breed,List<String> gender,List<String> district,List<String> city,List<Integer> images, String UserID){
        this.title=title;
        this.breed=breed;
        this.district=district;
        this.city=city;
        this.gender=gender;
        this.images =images;
        this.ctx=ctx;
        this.UserID=UserID;
        this.layoutInflater=LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.listing_ad_box_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(title.get(position));
        holder.breed.setText(breed.get(position));
        holder.location.setText(city.get(position)+", "+district.get(position));

        holder.gender.setText(gender.get(position));
//        Toast.makeText(ctx, UserID, Toast.LENGTH_SHORT).show();

//        holder.img.setImageURI(images.get(position));
//        holder.img.setImageURI(images.get(position));
//        Picasso.get().load(images.get(position)).into(holder.img);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef=  storageRef.child("users/"+UserID+"/"+ images.get(position).toString() +"/img1.jpg");
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.img);
            }
        });

    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView  title,breed,gender,location;
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

           breed =itemView.findViewById(R.id.ad_box_breed_txt);
           gender =itemView.findViewById(R.id.ad_box_gender_txt);
           title =itemView.findViewById(R.id.ad_box_title_txt);
           location =itemView.findViewById(R.id.ad_box_location_txt);

           img=itemView.findViewById(R.id.ad_box_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),"clicked on"+getAdapterPosition(),Toast.LENGTH_SHORT).show();
                }
            });

        }
    }




}