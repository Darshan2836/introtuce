package com.darshanudagire.introtuce.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.darshanudagire.introtuce.MainActivity;
import com.darshanudagire.introtuce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import dev.jai.genericdialog2.GenericDialog;
import dev.jai.genericdialog2.GenericDialogOnClickListener;


public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.NewOrdersVH> {

    private static final String TAG = "DeliveredAdapter";
    private List<HashMap> userList;
    private Context context;
    private String searchedText;

    public AllUsersAdapter(List<HashMap> userList,Context context){
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewOrdersVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        context = parent.getContext();

        return new NewOrdersVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewOrdersVH holder, int position) {

        holder.name.setText(userList.get(position).get("firstName").toString() + " " +userList.get(position).get("lastName").toString());
        holder.age.setText(userList.get(position).get("age").toString());
        holder.sex.setText(userList.get(position).get("gender").toString());
        holder.city.setText(userList.get(position).get("homeTown").toString());
        Glide.with(holder.itemView)
                .load(userList.get(position).get("profileImage").toString())
                .centerInside()
                .into(holder.profileImage);
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Deleting user from firebase..!!");
                progressDialog.setCancelable(false);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                new GenericDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage("Are you sure to Delete this User?")
                        .setTitle("Please confirm")
                        .setDialogTheme(R.style.GenericDialogTheme)
                        .addNewButton(R.style.YesBtn, new GenericDialogOnClickListener() {
                            @Override
                            public void onClick(View view) {
                                progressDialog.show();
                                db.collection("Users").document(userList.get(position).get("phoneNumber").toString()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(context,"User Deleted",Toast.LENGTH_LONG).show();
                                            Intent intent= new Intent(context, MainActivity.class);
                                            context.startActivity(intent);
                                            ((Activity)context).finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(context,task.getException().toString(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        })
                        .addNewButton(R.style.NoBtn, new GenericDialogOnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //cancel
                            }
                        })
                        .generate();

            }
        });
    }



    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class NewOrdersVH extends RecyclerView.ViewHolder{
        @BindView(R.id.profile_image)
        de.hdodenhof.circleimageview.CircleImageView profileImage;
        @BindView(R.id.deleteBtn_id)
        AppCompatButton deleteBtn;
        @BindView(R.id.name_id)
        AppCompatTextView name;
        @BindView(R.id.sex_id)
        AppCompatTextView sex;
        @BindView(R.id.age_id)
        AppCompatTextView age;
        @BindView(R.id.city_id)
        AppCompatTextView city;

        public NewOrdersVH(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);

        }
    }
}
