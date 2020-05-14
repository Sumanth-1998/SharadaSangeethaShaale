package com.example.sharadasangeethashaale.ui.TodaysClasses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is TodaysClasses fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}