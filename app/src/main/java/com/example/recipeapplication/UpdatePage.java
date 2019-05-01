package com.example.recipeapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdatePage extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 55;

    ImageView back, update;
    ImageView IngredientAdd, IngredientMinus;
    ImageView StepAdd, StepMinus;

    EditText title, description, tips, prep, cook, ready;
    EditText num, ingredient;
    EditText step;
    AspectRatioImageView image;
    Uri createImageUri;

    int stepCount = 50000;
    int stepMarginCount = 50;
    int ingredientNumCount = 30000;
    int ingredientNameCount = 10000;
    int ingredientMarginCount = 50;
    RelativeLayout ingredient_relative, step_relative;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, getmDatabase;
    private StorageReference mStorageRef;

    String getTitle, getDes, getTimePrep, getTimeCook, getTimeReady;

    String temp_num = "";
    String temp_ingred = "";

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_page);

        mAuth = FirebaseAuth.getInstance();

        back = findViewById(R.id.update_back);
        update = findViewById(R.id.update_recipe_button);

        IngredientAdd = findViewById(R.id.ingredient_add);
        IngredientMinus = findViewById(R.id.ingredient_minus);
        StepAdd = findViewById(R.id.step_add);
        StepMinus = findViewById(R.id.step_minus);

        ingredient_relative = findViewById(R.id.ingredient_relative);
        step_relative = findViewById(R.id.step_relative);

        image = findViewById(R.id.update_recipe_image);
        title = findViewById(R.id.update_title);
        description = findViewById(R.id.update_description);
        tips = findViewById(R.id.update_tips);
        prep = findViewById(R.id.update_prep);
        cook = findViewById(R.id.update_cook);
        ready = findViewById(R.id.update_ready);

        num = findViewById(R.id.ingredients_num_1);
        ingredient = findViewById(R.id.ingredients_name_1);

        step = findViewById(R.id.step_1);

        getRecipeData();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkItem();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        IngredientAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIngredientNum();
                createIngredientName();
            }
        });

        IngredientMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteIngredient(v);
            }
        });

        StepAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createStep();
            }
        });

        StepMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStep(v);
            }
        });
    }

    private void getRecipeData() {
        getTitle = getIntent().getStringExtra("getExtra");
        title.setText(getTitle);
        getmDatabase = FirebaseDatabase.getInstance().getReference("recipesUser").child(getTitle);
        getmDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getDes = dataSnapshot.child("Description").getValue().toString();
                description.setText(getDes);
                getTimePrep = dataSnapshot.child("Time").child("0").getValue().toString();
                prep.setText(getTimePrep);
                getTimeCook = dataSnapshot.child("Time").child("1").getValue().toString();
                cook.setText(getTimeCook);
                getTimeReady = dataSnapshot.child("Time").child("2").getValue().toString();
                ready.setText(getTimeReady);
                String temp = dataSnapshot.child("Ingredients").child(Integer.toString(0)).getValue().toString();
                getIntAndString(temp);
                num.setText(temp_num);
                ingredient.setText(temp_ingred);
                temp_num = "";
                temp_ingred = "";
                int ingredCount = (int) dataSnapshot.child("Ingredients").getChildrenCount();
                for (int i = 1; i < ingredCount; i++) {
                    temp = dataSnapshot.child("Ingredients").child(Integer.toString(i)).getValue().toString();
                    getIntAndString(temp);
                    createIngredientNum();
                    createIngredientName();
                    EditText editText_number = findViewById(30000 + i);
                    EditText editText_ingred = findViewById(10000 + i);
                    editText_number.setText(temp_num);
                    editText_ingred.setText(temp_ingred);
                    temp_num = "";
                    temp_ingred = "";
                }
                String temp2 = dataSnapshot.child("Steps").child(Integer.toString(0)).getValue().toString();
                step.setText(temp2);
                int stepCount = (int) dataSnapshot.child("Steps").getChildrenCount();
                for (int i = 1; i < stepCount; i++) {
                    temp2 = dataSnapshot.child("Steps").child(Integer.toString(0)).getValue().toString();
                    createStep();
                    EditText editText_step = findViewById(50000 + i);
                    editText_step.setText(temp2);
                }
                mStorageRef = FirebaseStorage.getInstance().getReference();
                mStorageRef.child("recipesUserImage/" + getTitle + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String temp = uri.toString();
                        Picasso.with(getApplicationContext()).load(temp).into(image);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getIntAndString(String temp) {
        for (int g = 0; g < temp.length(); g++) {
            char c = temp.charAt(g);
            if (Character.isDigit(c)) {
                temp_num = temp_num + c;
            } else {
                if (Character.isAlphabetic(c)) {
                    temp_ingred = temp_ingred + c;
                }
            }
        }
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Profile Image"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            createImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), createImageUri);
                image.setImageBitmap(bitmap);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkItem() {
        String check_title = title.getText().toString().trim();
        String check_description = description.getText().toString().trim();
        String check_prep = prep.getText().toString().trim();
        String check_cook = cook.getText().toString().trim();
        String check_ready = ready.getText().toString().trim();

        if (check_title.isEmpty()) {
            title.setError("Title is required");
            title.requestFocus();
            return;
        }

//        checkTitle();

        if (check_description.isEmpty()) {
            description.setError("Description is required");
            description.requestFocus();
            return;
        }

        if (check_prep.isEmpty()) {
            prep.setError("Prepare Time is required");
            prep.requestFocus();
            return;
        }

        if (check_cook.isEmpty()) {
            cook.setError("Cook Time is required");
            cook.requestFocus();
            return;
        }

        if (check_ready.isEmpty()) {
            ready.setError("Ready Time is required");
            ready.requestFocus();
            return;
        }
        checkIngredient();
    }

    private void checkIngredient() {
        String top_num = num.getText().toString().trim();
        String top_ingredient = ingredient.getText().toString().trim();

        if (top_num.isEmpty()) {
            num.setError("Ingredient Number is required");
            num.requestFocus();
            return;
        }
        for (int i = ingredientNumCount; i >= 30001; i--) {
            EditText below_num = findViewById(i);
            String check_num = below_num.getText().toString().trim();
            if (check_num.isEmpty()) {
                below_num.setError("Ingredient Number is required");
                below_num.requestFocus();
                return;
            }
        }

        if (top_ingredient.isEmpty()) {
            ingredient.setError("Ingredient is required");
            ingredient.requestFocus();
            return;
        }

        for (int i = ingredientNameCount; i >= 10001; i--) {
            EditText below_ingredient = findViewById(i);
            String check_ingredient = below_ingredient.getText().toString().trim();
            if (check_ingredient.isEmpty()) {
                below_ingredient.setError("Ingredient is required");
                below_ingredient.requestFocus();
                return;
            }
        }
        checkStep();
    }

    private void checkStep() {
        String top_step = step.getText().toString().trim();

        if (top_step.isEmpty()) {
            step.setError("Step is required");
            step.requestFocus();
            return;
        }

        for (int i = stepCount; i >= 50001; i--) {
            EditText below_step = findViewById(i);
            String check_step = below_step.getText().toString().trim();
            if (check_step.isEmpty()) {
                below_step.setError("Step is required");
                below_step.requestFocus();
                return;
            }
        }

        saveRecipeToFireBase();
    }

    public void saveRecipeToFireBase() {
        String pas_title = title.getText().toString().trim();
        String pas_des = description.getText().toString().trim();
        String pas_tip = tips.getText().toString().trim();
        mDatabase = FirebaseDatabase.getInstance().getReference("recipesUser");
        mDatabase.child(pas_title).child("Description").setValue(pas_des);
        Map<String, String> ingredientData = new HashMap<>();
        String pas_num = num.getText().toString().trim();
        String pas_ingredient = ingredient.getText().toString().trim();
        ingredientData.put("0", pas_num + " " + pas_ingredient);
        for (int i = ingredientNumCount, t = ingredientNameCount, count = 1; i > 30000 && t > 10000; i--, t--, count++) {
            EditText num_box = findViewById(i);
            EditText ingredient_box = findViewById(t);
            pas_num = num_box.getText().toString().trim();
            pas_ingredient = ingredient_box.getText().toString().trim();
            ingredientData.put(Integer.toString(count), pas_num + " " + pas_ingredient);
        }
        mDatabase.child(pas_title).child("Ingredients").setValue(ingredientData);
        Map<String, String> stepData = new HashMap<>();
        String pas_step = step.getText().toString().trim();
        stepData.put("0", pas_step);
        for (int i = stepCount, count = 1; i > 50000; i--, count++) {
            EditText step_box = findViewById(i);
            pas_step = step_box.getText().toString().trim();
            stepData.put(Integer.toString(count), pas_step);
        }
        mDatabase.child(pas_title).child("Steps").setValue(stepData);
        Map<String, String> timeData = new HashMap<>();
        String pas_prep = prep.getText().toString().trim();
        String pas_cook = cook.getText().toString().trim();
        String pas_ready = ready.getText().toString().trim();
        timeData.put("0", pas_prep);
        timeData.put("1", pas_cook);
        timeData.put("2", pas_ready);
        mDatabase.child(pas_title).child("Time").setValue(timeData);
        mDatabase.child(pas_title).child("Tip").setValue(pas_tip);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("recipesUserImage/" + pas_title + ".jpg");
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        storageReference.putBytes(data);

        buildUserRecord();
    }

    private void buildUserRecord() {
        String save_title = title.getText().toString().trim();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("recipesUserRecord");
        mDatabase.child(currentUser.getUid()).child(save_title).setValue("0");

        Toast.makeText(UpdatePage.this, "Recipe update Success", Toast.LENGTH_SHORT).show();
        finish();
    }

//    private void checkTitle() {
//        String saved_title = title.getText().toString().trim();
//        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot data: dataSnapshot.getChildren()) {
//                    if (data.getKey().equals(saved_title)) {
//                        title.setError("Title Have Been Used");
//                        title.requestFocus();
//                        mDatabase.removeEventListener(this);
//                        return ;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        mDatabase = FirebaseDatabase.getInstance().getReference("recipesUser");
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot data: dataSnapshot.getChildren()) {
//                    if (data.getKey().equals(saved_title)) {
//                        title.setError("Title Have Been Used");
//                        title.requestFocus();
//                        return;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void createIngredientNum() {
        EditText Number = new EditText(ingredient_relative.getContext());
        ingredientNumCount++;
        Number.setId(ingredientNumCount);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(valueToDp(65), valueToDp(40));
        layoutParams.setMargins(valueToDp(10), valueToDp(ingredientMarginCount), 0, 0);
        Number.setLayoutParams(layoutParams);
        Number.setBackgroundResource(R.drawable.create_recipe_background);
        Number.setHint("Num");
        Number.setSingleLine(true);
        Number.setInputType(InputType.TYPE_CLASS_NUMBER);
        Number.setPadding(valueToDp(15), 0, valueToDp(15), 0);
        Number.setTextSize(18);
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(3);
        Number.setFilters(fArray);
        ingredient_relative.addView(Number);
    }

    private void createIngredientName() {
        EditText Name = new EditText(ingredient_relative.getContext());
        ingredientNameCount++;
        Name.setId(ingredientNameCount);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(valueToDp(150), valueToDp(40));
        layoutParams.setMargins(valueToDp(85), valueToDp(ingredientMarginCount), 0, 0);
        ingredientMarginCount = ingredientMarginCount + 50;
        Name.setLayoutParams(layoutParams);
        Name.setBackgroundResource(R.drawable.create_recipe_background);
        Name.setHint("Ingredient");
        Name.setSingleLine(true);
        Name.setPadding(valueToDp(15), 0, valueToDp(15), 0);
        Name.setTextSize(18);
        ingredient_relative.addView(Name);
    }

    private void deleteIngredient(View view) {
        if (ingredientNumCount <= 30000 || ingredientNameCount <= 10000) {
            Toast.makeText(view.getContext(), "Recipe At Least One Ingredient", Toast.LENGTH_SHORT).show();
        } else {
            EditText num = findViewById(ingredientNumCount);
            ingredient_relative.removeView(num);
            ingredientNumCount--;
            EditText name = findViewById(ingredientNameCount);
            ingredient_relative.removeView(name);
            ingredientNameCount--;
            ingredientMarginCount = ingredientMarginCount - 50;
        }
    }

    private void createStep() {
        EditText Step = new EditText(step_relative.getContext());
        stepCount++;
        Step.setId(stepCount);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(valueToDp(225), valueToDp(40));
        layoutParams.setMargins(valueToDp(10), valueToDp(stepMarginCount), 0, 0);
        stepMarginCount = stepMarginCount + 50;
        Step.setLayoutParams(layoutParams);
        Step.setBackgroundResource(R.drawable.create_recipe_background);
        String step_box_num;
        step_box_num = Integer.toString(stepCount - 50000 + 1);
        Step.setHint("Step " + step_box_num);
        Step.setSingleLine(true);
        Step.setPadding(valueToDp(15), 0, valueToDp(15), 0);
        Step.setTextSize(18);
        step_relative.addView(Step);
    }

    private void deleteStep(View view) {
        if (stepCount <= 50000) {
            Toast.makeText(view.getContext(), "Recipe At Least One Step", Toast.LENGTH_SHORT).show();
        } else {
            EditText step = findViewById(stepCount);
            step_relative.removeView(step);
            stepCount--;
            stepMarginCount = stepMarginCount - 50;
        }
    }

    private int valueToDp(int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, getResources().getDisplayMetrics());
    }
}
