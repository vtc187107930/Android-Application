package com.example.recipeapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchRecipe extends AppCompatActivity {

    TextView searchText, text;
    ImageView back;
    ListView searchList;

    private DatabaseReference mdatabase1, mdatabase2;
    private StorageReference mStorageRef;

    ArrayList<String> searchRecipeTitle = new ArrayList<>();
    ArrayList<String> searchRecipeDes = new ArrayList<>();
    ArrayList<String> searchRecipeImage = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_recipe);

        back = findViewById(R.id.search_back);
        searchText = findViewById(R.id.search_text);
        text = findViewById(R.id.search_textView);
        searchList = findViewById(R.id.search_list);
        searchText.setText(getResources().getString(R.string.search_mark) + getIntent().getStringExtra("getSearchResult"));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchTitle();
    }

    private void searchTitle() {
        mdatabase1 = FirebaseDatabase.getInstance().getReference("recipes");
        mdatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean found;
                String search = getIntent().getStringExtra("getSearchResult");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String recipeName = ds.getKey();
                    found = recipeName.contains(search);
                    if (found) {
                        searchRecipeTitle.add(recipeName);
                        Log.d("", "onDataChange: " + searchRecipeTitle);
                    }
                }
                searchDes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchDes() {
        mdatabase2 = FirebaseDatabase.getInstance().getReference("recipes");
        mdatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 1, t = 0; i <= searchRecipeTitle.size(); i++, t++) {
                    searchRecipeDes.add(t, dataSnapshot.child(searchRecipeTitle.get(t)).child("Description").getValue().toString());
                }
                searchImage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        for (int f = 1, t = 0; f <= searchRecipeTitle.size(); f++, t++) {
            mStorageRef.child("recipeImage/" + searchRecipeTitle.get(t) + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String temp = uri.toString();
                    searchRecipeImage.add(temp);
                    if (searchRecipeImage.size() == searchRecipeTitle.size()) {

                        searchList.setVisibility(View.VISIBLE);
                        text.setVisibility(View.INVISIBLE);

                        SearchRecipe.SearchListAdapter customerAdapter = new SearchRecipe.SearchListAdapter();

                        searchList.setAdapter(customerAdapter);
                    }

                }
            });
        }
    }

    class SearchListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return searchRecipeTitle.size();
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
            convertView = getLayoutInflater().inflate(R.layout.search_list_view, null);

            ImageView image = convertView.findViewById(R.id.search_list_image);
            TextView title = convertView.findViewById(R.id.search_list_title);
            TextView des = convertView.findViewById(R.id.search_list_description);

            Picasso.with(convertView.getContext()).load(searchRecipeImage.get(position)).into(image);
            title.setText(searchRecipeTitle.get(position));
            des.setText(searchRecipeDes.get(position));

            des.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SearchRecipe.this, RecipePage.class);
                    i.putExtra("RecipeName", searchRecipeTitle.get(position));
                    startActivity(i);
                }
            });

            return convertView;
        }
    }

}
