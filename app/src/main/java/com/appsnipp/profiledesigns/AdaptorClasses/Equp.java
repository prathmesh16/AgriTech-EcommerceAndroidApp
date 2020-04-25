package com.appsnipp.profiledesigns.AdaptorClasses;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Equp {
    private String name;
    private String image;
    private int price;
   // private Uri uriImage;
    private String catagory;
    private rating rating;


    public rating getRating() {
        return rating;
    }

    public void setRating(rating rating) {
        this.rating = rating;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    private String seller;


    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    public static class rating{
        private float avgrating;
        private int people;

        public float getAvgrating() {
            return avgrating;
        }

        public void setAvgrating(float avgrating) {
            this.avgrating = avgrating;
        }

        public int getPeople() {
            return people;
        }

        public void setPeople(int people) {
            this.people = people;
        }
    }
}
