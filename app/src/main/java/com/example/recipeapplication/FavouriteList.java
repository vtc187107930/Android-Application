package com.example.recipeapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavouriteList extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference database1, database2;
    private StorageReference mStorageRef;

    ImageView back;
    TextView noFavourite;
    ListView favouriteList;

    ArrayList<String> favouriteRecipeTitle = new ArrayList<>();
    ArrayList<String> favouriteRecipeDes = new ArrayList<>();
    ArrayList<String> favouriteRecipeImage = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_list);

        back = findViewById(R.id.favourite_back);
        noFavourite = findViewById(R.id.noFavourite);
        favouriteList = findViewById(R.id.favourite_list);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        favouriteRecipeTitle.clear();
        favouriteRecipeDes.clear();
        favouriteRecipeImage.clear();
        favouriteList.setAdapter(null);
        getRecipeTitle();
    }

    private void getRecipeTitle() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        database1 = FirebaseDatabase.getInstance().getReference("recipesFavoriteList");
        database1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getUid())) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child(user.getUid()).getChildren()) {
                        favouriteRecipeTitle.add(dataSnapshot1.getKey());
                    }
                    getRecipeDes();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRecipeDes() {
        database2 = FirebaseDatabase.getInstance().getReference("recipes");
        database2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 1, t = 0; i <= favouriteRecipeTitle.size(); i++, t++) {
                    favouriteRecipeDes.add(t, dataSnapshot.child(favouriteRecipeTitle.get(t)).child("Description").getValue().toString());

                }
                getRecipeImage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRecipeImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        for (int f = 1, t = 0; f <= favouriteRecipeTitle.size(); f++, t++) {
            mStorageRef.child("recipeImage/" + favouriteRecipeTitle.get(t) + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String temp = uri.toString();
                    favouriteRecipeImage.add(temp);
                    if (favouriteRecipeImage.size() == favouriteRecipeTitle.size()) {

                        favouriteList.setVisibility(View.VISIBLE);
                        noFavourite.setVisibility(View.INVISIBLE);

                        FavouriteList.CustomerAdapter customerAdapter = new CustomerAdapter();

                        favouriteList.setAdapter(customerAdapter);
                    }

                }
            });
        }
    }

    class CustomerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return favouriteRecipeImage.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.favourite_list_view, null);

            ImageView image = convertView.findViewById(R.id.favorite_list_image);
            TextView title = convertView.findViewById(R.id.favorite_list_title);
            TextView des = convertView.findViewById(R.id.favorite_list_description);
            ImageView delete = convertView.findViewById(R.id.favorite_list_item_delete);

            Picasso.with(convertView.getContext()).load(favouriteRecipeImage.get(position)).into(image);
            title.setText(favouriteRecipeTitle.get(position));
            des.setText(favouriteRecipeDes.get(position));
            des.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), RecipePage.class);
                    i.putExtra("RecipeName", favouriteRecipeTitle.get(position));
                    startActivity(i);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

                    alertDialogBuilder
                            .setMessage("Are You Sure Delete " + favouriteRecipeTitle.get(position) + " Recipe Favourite??")
                            .setCancelable(false)
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mAuth = FirebaseAuth.getInstance();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    DatabaseReference a = FirebaseDatabase.getInstance().getReference("recipesFavoriteList").child(user.getUid()).child(favouriteRecipeTitle.get(position));
                                    a.removeValue();
                                    favouriteRecipeTitle.remove(position);
                                    favouriteRecipeDes.remove(position);
                                    favouriteRecipeImage.remove(position);
                                    notifyDataSetChanged();
                                }
                            })
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();
                }
            });

            return convertView;
        }
    }
}
