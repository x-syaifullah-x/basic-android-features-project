package com.example.androidlabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailsFragment extends Fragment {
    public static final String ARG_IS_LARGE = "arg_is_large";
    public static final String ARG_ID = "arg_id";
    public static final String ARG_TEXT = "arg_text";
    public static final String ARG_IS_SEND = "arg_is_send";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_details, container, false);

        Bundle args = getArguments();

        if (args != null) {

            Button btnHide = view.findViewById(R.id.btn_hide);
            btnHide.setOnClickListener(v -> {
                if (!args.getBoolean(ARG_IS_LARGE)) {
                    requireActivity().finish();
                } else {
                    requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                }
            });

            TextView message = view.findViewById(R.id.tv_message);
            String msg = "Message Here: " + args.getString(ARG_TEXT);
            message.setText(msg);

            TextView messageId = view.findViewById(R.id.tv_message_id);
            String id = "ID=" + args.getLong(ARG_ID);
            messageId.setText(id);

            CheckBox isSend = view.findViewById(R.id.cb_message_is_send);
            isSend.setChecked(args.getBoolean(ARG_IS_SEND));
        }
        return view;
    }
}
