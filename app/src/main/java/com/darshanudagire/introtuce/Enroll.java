package com.darshanudagire.introtuce;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.base.MoreObjects;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class Enroll extends Fragment {

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static int GALLERY_CODE = 1;
    final Calendar myCalendar = Calendar.getInstance();

    private View view;
    private int age;

    private FirebaseFirestore db;
    private boolean isNumberAvailable;
    private Uri profilePicUri;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    //widgets
    @BindView(R.id.profilePic_id)
    AppCompatImageView profilePic;
    @BindView(R.id.dateOfBirth_id)
    TextInputLayout dateOfBirth;
    @BindView(R.id.dateOfBirthEditTxt_id)
    TextInputEditText dateOfBirthEditTxt;
    @BindView(R.id.phoneNumberEditTxt_id)
    TextInputEditText phoneNumberEditTxt;
    @BindView(R.id.firstName_id)
    TextInputLayout firstName;
    @BindView(R.id.lastName_id)
    TextInputLayout lastName;
    @BindView(R.id.gender_id)
    TextInputLayout gender;
    @BindView(R.id.country_id)
    TextInputLayout country;
    @BindView(R.id.state_id)
    TextInputLayout state;
    @BindView(R.id.homeTown_id)
    TextInputLayout homeTown;
    @BindView(R.id.phoneNumber_id)
    TextInputLayout phoneNumber;
    @BindView(R.id.telephoneNumber_id)
    TextInputLayout telephoneNumber;
    @BindView(R.id.addUserBtn_id)
    AppCompatButton addUserBtn;
    @BindView(R.id.checking_id)
    AppCompatTextView checking;
    @BindView(R.id.available_id)
    AppCompatTextView available;
    @BindView(R.id.relativeLayout_id)
    RelativeLayout relativeLayout;


    String firstNameTxt,lastNameTxt,dateOfBirthTxt,genderTxt,countryTxt,stateTxt,homeTownTxt,phoneNumberTxt,telephoneNumberTxt;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Enroll() {

    }


    public static Enroll newInstance(String param1, String param2) {
        Enroll fragment = new Enroll();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_enroll, container, false);
        ButterKnife.bind(this,view);

        //firebase instance
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading data to firebase...!!");


        if (profilePicUri != null) {
            profilePic.setImageURI(profilePicUri);
        }

        //dob
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        dateOfBirthEditTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(view.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //phone number text changer
        phoneNumberEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String number = charSequence.toString();
                if(number.length() == 10)
                {
                    relativeLayout.setVisibility(View.VISIBLE);
                    checking.setVisibility(View.VISIBLE);
                    available.setVisibility(View.INVISIBLE);
                    db.collection("Users").whereEqualTo("phoneNumber",number).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if (task.getResult().getDocuments().size() > 0)
                                {
                                    available.setVisibility(View.VISIBLE);
                                    available.setText("Not Available");
                                    available.setTextColor(getResources().getColor(R.color.md_red_900));
                                    isNumberAvailable = false;
                                }
                                else
                                {
                                    available.setVisibility(View.VISIBLE);
                                    available.setText("Available");
                                    available.setTextColor(getResources().getColor(R.color.md_green_900));
                                    isNumberAvailable = true;

                                }
                                checking.setVisibility(View.INVISIBLE);
                            }
                            else
                            {
                                Toast.makeText(view.getContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else
                {
                    relativeLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //profile pic
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .getIntent(getContext());

                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        //add user on click listner
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkAllFields())
                {
                    if(isNumberAvailable)
                    {
                        if(profilePicUri != null)
                        {
                            progressDialog.show();
                            firstNameTxt = firstName.getEditText().getText().toString();
                            lastNameTxt = lastName.getEditText().getText().toString();
                            dateOfBirthTxt = dateOfBirth.getEditText().getText().toString();
                            genderTxt = gender.getEditText().getText().toString();;
                            countryTxt = country.getEditText().getText().toString();
                            stateTxt = state.getEditText().getText().toString();
                            homeTownTxt = homeTown.getEditText().getText().toString();
                            phoneNumberTxt = phoneNumber.getEditText().getText().toString();
                            telephoneNumberTxt = telephoneNumber.getEditText().getText().toString();

                            HashMap userDetails = new HashMap();
                            userDetails.put("firstName",firstNameTxt);
                            userDetails.put("lastName",lastNameTxt);
                            userDetails.put("dateOfBirth",dateOfBirthTxt);
                            userDetails.put("gender",genderTxt);
                            userDetails.put("country",countryTxt);
                            userDetails.put("state",stateTxt);
                            userDetails.put("homeTown",homeTownTxt);
                            userDetails.put("phoneNumber",phoneNumberTxt);
                            userDetails.put("telephoneNumber",telephoneNumberTxt);
                            userDetails.put("age",String.valueOf(age));
                            userDetails.put("timestamp", FieldValue.serverTimestamp());



                            db.collection("Users").document(phoneNumberTxt).set(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        uploadImages(profilePicUri);

                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(view.getContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(view.getContext(),"Select a Profile Pic",Toast.LENGTH_LONG).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(view.getContext(),"The Number is not available",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return view;
    }

    private boolean checkAllFields() {

        if(firstName.getEditText().getText().toString().trim().equals("")){firstName.setError("Enter First Name"); firstName.requestFocus(); return false;}
        if(lastName.getEditText().getText().toString().trim().equals("")){lastName.setError("Enter Last Name"); lastName.requestFocus(); return false;}
        if(dateOfBirth.getEditText().getText().toString().trim().equals("")){dateOfBirth.setError("Enter Date of Birth"); dateOfBirth.requestFocus(); return false;}
        if(gender.getEditText().getText().toString().trim().equals("")){gender.setError("Enter Gender"); gender.requestFocus(); return false;}
        if(country.getEditText().getText().toString().trim().equals("")){country.setError("Enter Country"); country.requestFocus(); return false;}
        if(state.getEditText().getText().toString().trim().equals("")){state.setError("Enter State"); state.requestFocus(); return false;}
        if(homeTown.getEditText().getText().toString().trim().equals("")){homeTown.setError("Enter Home Town"); homeTown.requestFocus(); return false;}
        if(phoneNumber.getEditText().getText().toString().trim().equals("") && phoneNumber.getEditText().toString().length() != 10){phoneNumber.setError("Enter Phone Number"); phoneNumber.requestFocus(); return false;}
        if(telephoneNumber.getEditText().getText().toString().trim().equals("")){telephoneNumber.setError("Enter Telephone Number"); telephoneNumber.requestFocus(); return false;}
        return true;
    }

    //functions
    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        String date = sdf.format(myCalendar.getTime());
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int dateOfBirthYear = myCalendar.get(Calendar.YEAR);

        age = currentYear-dateOfBirthYear;

        dateOfBirth.setHintAnimationEnabled(date == null);
        dateOfBirthEditTxt.setText(date);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                profilePicUri = resultUri;
                Log.e("resultUri ->", String.valueOf(resultUri));
                profilePic.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("error ->", String.valueOf(error));
            }
        }
    }

    private void uploadImages(Uri uri){
        //first upload images to storage and fetch them urls
        String randomId = UUID.randomUUID().toString();

        final StorageReference imageStoragePath = storageReference.child("ProfileImages").child(randomId + ".jpg");
        UploadTask uploadTask = imageStoragePath.putFile(uri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String imageDownloadUrl;
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri url = uriTask.getResult();
                imageDownloadUrl = url.toString();

                db.collection("Users").document(phoneNumberTxt).update("profileImage",imageDownloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(view.getContext(),"User added Successfully",Toast.LENGTH_LONG).show();
                        // Reload current fragment
//                        FragmentTransaction ftr = getFragmentManager().beginTransaction();
//                        ftr.detach(Enroll.this).attach(Enroll.this).commit();
                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                    }
                });
            }
        });
    }


}