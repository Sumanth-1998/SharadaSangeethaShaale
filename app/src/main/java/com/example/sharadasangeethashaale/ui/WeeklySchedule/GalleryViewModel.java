package com.example.sharadasangeethashaale.ui.WeeklySchedule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is WeeklySchedule fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}