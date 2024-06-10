package com.sih2020.abhyuday.Ambulance;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.sih2020.abhyuday.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    ImageView editProfileBtn;
    TextView changePassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        editProfileBtn = view.findViewById(R.id.iv_edit);
        changePassword = view.findViewById(R.id.tv_change_password);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editProfileBtn.setOnClickListener(v -> {
            EditProfileDialog editProfileDialog = new EditProfileDialog();
            editProfileDialog.show(getFragmentManager().beginTransaction(), EditProfileDialog.TAG);
        });

        changePassword.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.change_password_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext())
                    .setView(dialogView)
                    .setTitle("Change Password");

            AlertDialog alertDialog = builder.show();
            MaterialButton saveBtn = dialogView.findViewById(R.id.btn_save);
            MaterialButton cancelBtn = dialogView.findViewById(R.id.btn_cancel);
            cancelBtn.setOnClickListener(v1 -> {
                alertDialog.dismiss();
            });
            saveBtn.setOnClickListener(v1 -> {
                alertDialog.dismiss();
            });
        });
    }
}
