package com.appsnipp.profiledesigns.ui.Delivery;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class deliveryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public deliveryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}