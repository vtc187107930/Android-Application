package com.example.recipeapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.Timer;
import java.util.TimerTask;

public class RecipeFragment extends Fragment {

    ViewPager viewPager;
    SliderViewPagerAdapter adapter;
    LinearLayout sliderDots;
    private int dotCounts;
    private ImageView[] dots;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    TextView title1, title2, title3, title4, title5, title6;
    TextView dis1, dis2, dis3, dis4, dis5, dis6;
    ImageView img1, img2, img3, img4, img5, img6, item1, item2, item3, item4;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe, container, false);
        title1 = view.findViewById(R.id.recommend_item_title_1);
        title2 = view.findViewById(R.id.recommend_item_title_2);
        title3 = view.findViewById(R.id.recommend_item_title_3);
        title4 = view.findViewById(R.id.recommend_item_title_4);
        title5 = view.findViewById(R.id.recommend_item_title_5);
        title6 = view.findViewById(R.id.recommend_item_title_6);

        dis1 = view.findViewById(R.id.recommend_item_dis_1);
        dis2 = view.findViewById(R.id.recommend_item_dis_2);
        dis3 = view.findViewById(R.id.recommend_item_dis_3);
        dis4 = view.findViewById(R.id.recommend_item_dis_4);
        dis5 = view.findViewById(R.id.recommend_item_dis_5);
        dis6 = view.findViewById(R.id.recommend_item_dis_6);

        img1 = view.findViewById(R.id.recommend_item_image_1);
        img2 = view.findViewById(R.id.recommend_item_image_2);
        img3 = view.findViewById(R.id.recommend_item_image_3);
        img4 = view.findViewById(R.id.recommend_item_image_4);
        img5 = view.findViewById(R.id.recommend_item_image_5);
        img6 = view.findViewById(R.id.recommend_item_image_6);

        item1 = view.findViewById(R.id.item_image_1);
        item2 = view.findViewById(R.id.item_image_2);
        item3 = view.findViewById(R.id.item_image_3);
        item4 = view.findViewById(R.id.item_image_4);

        setSlider(view);
        getRecipe(view);
        getRecipeImage();

        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchRecipe.class);
                i.putExtra("getSearchResult", "Bread");
                startActivity(i);
            }
        });

        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchRecipe.class);
                i.putExtra("getSearchResult", "Cake");
                startActivity(i);
            }
        });

        item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchRecipe.class);
                i.putExtra("getSearchResult", "Salad");
                startActivity(i);
            }
        });

        item4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchRecipe.class);
                i.putExtra("getSearchResult", "Pie");
                startActivity(i);
            }
        });

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), RecipePage.class);
                i.putExtra("RecipeName", "Baked Potato");
                startActivity(i);
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), RecipePage.class);
                i.putExtra("RecipeName", "Broccoli Bites");
                startActivity(i);
            }
        });

        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), RecipePage.class);
                i.putExtra("RecipeName", "The Best Lemon Bars");
                startActivity(i);
            }
        });

        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), RecipePage.class);
                i.putExtra("RecipeName", "Warm Orange Sauce");
                startActivity(i);
            }
        });

        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), RecipePage.class);
                i.putExtra("RecipeName", "Blueberry Sauce");
                startActivity(i);
            }
        });

        img6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), RecipePage.class);
                i.putExtra("RecipeName", "Best Lemonade Ever");
                startActivity(i);
            }
        });

        return view;
    }

    public void getRecipe(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dis1.setText(dataSnapshot.child(title1.getText().toString()).child("Description").getValue().toString());
                dis2.setText(dataSnapshot.child(title2.getText().toString()).child("Description").getValue().toString());
                dis3.setText(dataSnapshot.child(title3.getText().toString()).child("Description").getValue().toString());
                dis4.setText(dataSnapshot.child(title4.getText().toString()).child("Description").getValue().toString());
                dis5.setText(dataSnapshot.child(title5.getText().toString()).child("Description").getValue().toString());
                dis6.setText(dataSnapshot.child(title6.getText().toString()).child("Description").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getRecipeImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageRef.child("recipeImage/" + title1.getText().toString() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String temp = uri.toString();
                Picasso.with(getContext()).load(temp).into(img1);
            }
        });
        mStorageRef.child("recipeImage/" + title2.getText().toString() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String temp = uri.toString();
                Picasso.with(getContext()).load(temp).into(img2);
            }
        });
        mStorageRef.child("recipeImage/" + title3.getText().toString() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String temp = uri.toString();
                Picasso.with(getContext()).load(temp).into(img3);
            }
        });
        mStorageRef.child("recipeImage/" + title4.getText().toString() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String temp = uri.toString();
                Picasso.with(getContext()).load(temp).into(img4);
            }
        });
        mStorageRef.child("recipeImage/" + title5.getText().toString() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String temp = uri.toString();
                Picasso.with(getContext()).load(temp).into(img5);
            }
        });
        mStorageRef.child("recipeImage/" + title6.getText().toString() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String temp = uri.toString();
                Picasso.with(getContext()).load(temp).into(img6);
            }
        });
    }

    public void setSlider(View view) {
        viewPager = view.findViewById(R.id.slider);
        adapter = new SliderViewPagerAdapter(getContext());
        viewPager.setAdapter(adapter);

        sliderDots = view.findViewById(R.id.slider_dots);

        dotCounts = adapter.getCount();
        dots = new ImageView[dotCounts];

        for (int i = 0; i < dotCounts; i++) {
            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.slider_nonactive_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 2, 8, 0);
            sliderDots.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.slider_active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotCounts; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.slider_nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.slider_active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 3000, 6000);
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() == 0) {
                        viewPager.setCurrentItem(1);
                    } else if (viewPager.getCurrentItem() == 1) {
                        viewPager.setCurrentItem(2);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

}
