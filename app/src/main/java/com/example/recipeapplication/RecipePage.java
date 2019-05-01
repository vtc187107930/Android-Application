package com.example.recipeapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class RecipePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    ImageView back, favorite, image;
    TextView title, description, prep, cook, ready;
    TextView ingredient1, step1;

    String titleTemp;

    RelativeLayout ingredient_relat, step_relat;

    boolean favouriteState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_page);

        image = findViewById(R.id.recipe_page_image);
        title = findViewById(R.id.recipe_page_title);
        description = findViewById(R.id.recipe_page_des);
        prep = findViewById(R.id.recipe_page_prep);
        cook = findViewById(R.id.recipe_page_cook);
        ready = findViewById(R.id.recipe_page_ready);
        back = findViewById(R.id.recipe_back);
        favorite = findViewById(R.id.favorite);
        ingredient1 = findViewById(R.id.ingredient1);
        step1 = findViewById(R.id.step1);
        ingredient_relat = findViewById(R.id.recipe_ingredient_relat);
        step_relat = findViewById(R.id.recipe_step_relat);

        titleTemp = getIntent().getStringExtra("RecipeName");

        checkFavourite();

        title.setText(titleTemp);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (!favouriteState) {
                        mDatabase = FirebaseDatabase.getInstance().getReference("recipesFavoriteList");
                        mDatabase.child(user.getUid()).child(titleTemp).setValue("0");
                        favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_fill));
                        Toast.makeText(RecipePage.this, "Add Recipe to Favorite List Successful", Toast.LENGTH_SHORT).show();
                        favouriteState = true;
                    } else {
                        DatabaseReference a = FirebaseDatabase.getInstance().getReference("recipesFavoriteList").child(user.getUid()).child(titleTemp);
                        Log.d("", "onClick: " + a);
                        a.removeValue();
                        favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_line));
                        Toast.makeText(RecipePage.this, "Remove Recipe to Favorite List Successful", Toast.LENGTH_SHORT).show();
                        favouriteState = false;
                    }
                } else {
                    Toast.makeText(v.getContext(), "Please Go To Profile Page Login First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadRecipeImage();
        loadRecipeData();

    }

    private void checkFavourite() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference("recipesFavoriteList").child(user.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(titleTemp)) {
                    favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_fill));
                    favouriteState = true;
                } else {
                    favouriteState = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadRecipeImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageRef.child("recipeImage/" + titleTemp + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String temp = uri.toString();
                Picasso.with(getApplicationContext()).load(temp).into(image);
            }
        });
    }

    private void loadRecipeData() {
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes").child(getIntent().getStringExtra("RecipeName"));
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String des = dataSnapshot.child("Description").getValue().toString();
                description.setText(des);
                String temp_prep = dataSnapshot.child("Time").child("0").getValue().toString();
                prep.setText(temp_prep);
                String temp_cook = dataSnapshot.child("Time").child("1").getValue().toString();
                cook.setText(temp_cook);
                String temp_ready = dataSnapshot.child("Time").child("2").getValue().toString();
                ready.setText(temp_ready);
                String temp_ingredient = dataSnapshot.child("Ingredients").child("0").getValue().toString();
                ingredient1.setText(temp_ingredient + " \n");
                int ingredientNumCount = (int) dataSnapshot.child("Ingredients").getChildrenCount();
                for (int i = 1; i <= ingredientNumCount - 1; i++) {
                    String temp = dataSnapshot.child("Ingredients").child(Integer.toString(i)).getValue().toString();
                    if (i == ingredientNumCount) {
                        ingredient1.append(temp);
                    } else {
                        ingredient1.append(temp + " \n");
                    }
                }
                String temp_step = dataSnapshot.child("Steps").child("0").getValue().toString();
                step1.setText(temp_step + " \n");
                int stepNumCount = (int) dataSnapshot.child("Steps").getChildrenCount();
                for (int i = 1; i <= stepNumCount - 1; i++) {
                    String temp = dataSnapshot.child("Steps").child(Integer.toString(i)).getValue().toString();
                    if (i == stepNumCount) {
                        step1.append("\n" + temp);
                    } else {
                        step1.append("\n" + temp + " \n");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
