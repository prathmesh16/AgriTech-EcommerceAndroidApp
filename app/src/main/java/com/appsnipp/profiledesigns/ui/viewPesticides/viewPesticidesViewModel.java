package com.appsnipp.profiledesigns.ui.viewPesticides;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class viewPesticidesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public viewPesticidesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}