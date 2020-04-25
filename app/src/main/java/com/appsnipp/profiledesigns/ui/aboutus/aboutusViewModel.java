package com.appsnipp.profiledesigns.ui.aboutus;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class aboutusViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public aboutusViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}