package com.example.booknest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booknest.databinding.RowCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> implements Filterable {
    private Context context;
    public ArrayList<ModelCategory> categoryArrayList,filterList;
    //view binding
    private RowCategoryBinding binding;
    //instance of our filter class
    private FilterCategory filter;
    public AdapterCategory(Context context,ArrayList<ModelCategory> categoryArrayList){
        this.context=context;
        this.categoryArrayList=categoryArrayList;
        this.filterList=categoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind row_category.xml
        binding=RowCategoryBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
//get data
        ModelCategory model=categoryArrayList.get(position);
        String id=model.getId();
        String category=model.getCategory();
        String uid= model.getUid();
        long timestamp= model.getTimestamp();
//set data
        holder.categoryTv.setText(category);
        //handle click delete category
        holder.deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //confirm delete dialog
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure u want to delete this category?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //begin delete
                                Toast.makeText(context,"Deleting...",Toast.LENGTH_SHORT).show();
                                deleteCategory(model,holder);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            }
        });

    }

    private void deleteCategory(ModelCategory model, HolderCategory holder) {
       //get id of category to delete
        String id = model.getId();
        //Firebase DB
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //deleted successfully
                        Toast.makeText(context,"Successfully deleted...",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to delete
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterCategory(filterList,this);
        }
        return filter;
    }

    /*View holder class to hold UI views for row_category.xml*/
class HolderCategory extends RecyclerView.ViewHolder{
    //ui views of row-category.xml
        TextView categoryTv;
        ImageButton deleteBtn;
    public HolderCategory(@NonNull View itemView){
        super(itemView);
        //init ui views
        categoryTv=binding.categoryTv;
        deleteBtn=binding.deleteBtn;
    }
    }
}
