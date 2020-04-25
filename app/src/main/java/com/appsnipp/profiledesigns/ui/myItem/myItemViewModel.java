package com.appsnipp.profiledesigns.ui.myItem;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class myItemViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public myItemViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}