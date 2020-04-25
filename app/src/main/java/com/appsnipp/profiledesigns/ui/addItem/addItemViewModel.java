package com.appsnipp.profiledesigns.ui.addItem;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class addItemViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public addItemViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}