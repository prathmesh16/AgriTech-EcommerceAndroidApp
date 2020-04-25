package com.appsnipp.profiledesigns.ui.feedback;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class feedbackViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public feedbackViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}