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

public class UpdateRecipe extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference database1, database2;
    private StorageReference mStorageRef;

    TextView textView;
    ListView listView;
    ImageView back;

    ArrayList<String> updateRecipeTitle = new ArrayList<>();
    ArrayList<String> updateRecipeDes = new ArrayList<>();
    ArrayList<String> updateRecipeImage = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_recipe);

        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.update_list);
        back = findViewById(R.id.update_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getRecipeTitle();

    }

    private void getRecipeTitle() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        database1 = FirebaseDatabase.getInstance().getReference("recipesUserRecord");
        database1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getUid())) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child(user.getUid()).getChildren()) {
                        updateRecipeTitle.add(dataSnapshot1.getKey());
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
        database2 = FirebaseDatabase.getInstance().getReference("recipesUser");
        database2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 1, t = 0; i <= updateRecipeTitle.size(); i++, t++) {
                    updateRecipeDes.add(t, dataSnapshot.child(updateRecipeTitle.get(t)).child("Description").getValue().toString());

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
        for (int f = 1, t = 0; f <= updateRecipeTitle.size(); f++, t++) {
            mStorageRef.child("recipesUserImage/" + updateRecipeTitle.get(t) + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String temp = uri.toString();
                    updateRecipeImage.add(temp);
                    if (updateRecipeImage.size() == updateRecipeTitle.size()) {

                        listView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.INVISIBLE);

                        CustomerAdapter customerAdapter = new CustomerAdapter();

                        listView.setAdapter(customerAdapter);
                    }

                }
            });
        }
    }

    class CustomerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return updateRecipeImage.size();
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
            convertView = getLayoutInflater().inflate(R.layout.update_list_view, null);

            ImageView image = convertView.findViewById(R.id.update_list_image);
            TextView title = convertView.findViewById(R.id.update_list_title);
            TextView des = convertView.findViewById(R.id.update_list_description);
            ImageView delete = convertView.findViewById(R.id.list_item_delete);

            Picasso.with(convertView.getContext()).load(updateRecipeImage.get(position)).into(image);
            title.setText(updateRecipeTitle.get(position));
            des.setText(updateRecipeDes.get(position));
            des.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), UpdatePage.class);
                    i.putExtra("getExtra", updateRecipeTitle.get(position));
                    startActivity(i);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

                    alertDialogBuilder
                            .setMessage("Are You Sure Delete " + updateRecipeTitle.get(position) + " Recipe ??")
                            .setCancelable(false)
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    mAuth = FirebaseAuth.getInstance();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    DatabaseReference a = FirebaseDatabase.getInstance().getReference("recipesUserRecord").child(user.getUid()).child(updateRecipeTitle.get(position));
                                    a.removeValue();
                                    DatabaseReference b = FirebaseDatabase.getInstance().getReference("recipesUser").child(updateRecipeTitle.get(position));
                                    b.removeValue();
                                    StorageReference c = FirebaseStorage.getInstance().getReference("recipesUserImage/" + updateRecipeTitle.get(position) + ".jpg");
                                    c.delete();
                                    updateRecipeTitle.remove(position);
                                    updateRecipeDes.remove(position);
                                    updateRecipeImage.remove(position);
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
