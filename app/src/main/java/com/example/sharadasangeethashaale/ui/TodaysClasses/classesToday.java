package com.example.sharadasangeethashaale.ui.TodaysClasses;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sharadasangeethashaale.R;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;

import org.joda.time.DateTime;

public class classesToday extends Fragment implements DatePickerListener {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_classes_today, container, false);
        HorizontalPicker picker = (HorizontalPicker) root.findViewById(R.id.datePicker);

        // initialize it and attach a listener

        picker.setListener(this).init();

        picker.setDate(new DateTime().plusDays(4));
        return root;
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {

    }
}