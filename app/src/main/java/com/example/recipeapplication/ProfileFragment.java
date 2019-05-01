package com.example.recipeapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {

    public TextView profileName;
    public ImageView profileIconImage, LogInOrOut, createRecipe, updateRecipe, favouriteList, languageList;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        profileName = view.findViewById(R.id.profile_name);
        profileIconImage = view.findViewById(R.id.profile_icon);
        LogInOrOut = view.findViewById(R.id.logInOrOut);

        createRecipe = view.findViewById(R.id.create_recipe);
        updateRecipe = view.findViewById(R.id.update_recipe);
        favouriteList = view.findViewById(R.id.favourite_list);
        languageList = view.findViewById(R.id.language_list);

        LogInOrOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    logoutAlertBox();
                } else {
                    loginAlertBox();
                }
            }
        });

        createRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Intent i = new Intent(getActivity(), CreateRecipe.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.please_login_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Intent i = new Intent(getActivity(), UpdateRecipe.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.please_login_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        favouriteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Intent i = new Intent(getActivity(), FavouriteList.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.please_login_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        languageList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LanguageList.class);
                i.putExtra("sendLanguage", getActivity().getIntent().getStringExtra("getLanguage"));
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            profileName.setText(name);
            mStorageRef = FirebaseStorage.getInstance().getReference();
            mStorageRef.child("profileImage/" + user.getUid() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String temp = uri.toString();
                    Picasso.with(getContext()).load(temp).into(profileIconImage);
                }
            });
            LogInOrOut.setImageDrawable(getResources().getDrawable(R.drawable.ic_logout));
        } else {
            profileName.setText(getResources().getString(R.string.please_login));
            profileIconImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_default));
            LogInOrOut.setImageDrawable(getResources().getDrawable(R.drawable.ic_login));
        }
    }

    private void loginAlertBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder
                .setMessage(getResources().getString(R.string.please_login_first))
                .setCancelable(false)
                .setNegativeButton(getResources().getString(R.string.login), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getActivity(), Login.class);
                        startActivity(i);
                    }
                })
                .setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void logoutAlertBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder
                .setMessage(getResources().getString(R.string.are_you_sure_logout))
                .setCancelable(false)
                .setNegativeButton(getResources().getString(R.string.logout), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                        onResume();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}