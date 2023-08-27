package com.example.ogma.ui.options;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OptionsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public OptionsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Находится в разработке");
    }

    public LiveData<String> getText() {
        return mText;
    }
}