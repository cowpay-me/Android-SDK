package com.cowpay.cowpaysdk.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.cowpay.cowpaysdk.R;


public class AppDialogFragment extends DialogFragment {

    private TextView tvTitle, tvSubTitle;
    private Button tvPositive, tvNegative;
    private ImageView dialogIcon;
    private final static String TITLE_KEY = "title";
    private final static String SUB_TITLE_KEY = "subTitle";
    private final static String POSITIVE_KEY = "positive";
    private final static String NEGATIVE_KEY = "negative";
    private final static String DIALOG_ICON = "DIALOG_ICON";

    public DialogCallback negativeCallback;
    public DialogCallback positiveCallback;


    public static AppDialogFragment newInstance(String title, String subTitle, String positive, String negative) {
        AppDialogFragment fragment = new AppDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putString(SUB_TITLE_KEY, subTitle);
        args.putString(POSITIVE_KEY, positive);
        args.putString(NEGATIVE_KEY, negative);
        fragment.setArguments(args);
        return fragment;
    }

    public static AppDialogFragment newInstance(@DrawableRes int drawableRes, String title, String subTitle, String positive, String negative) {
        AppDialogFragment fragment = new AppDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putString(SUB_TITLE_KEY, subTitle);
        args.putString(POSITIVE_KEY, positive);
        args.putString(NEGATIVE_KEY, negative);
        args.putInt(DIALOG_ICON, drawableRes);
        fragment.setArguments(args);
        return fragment;
    }


    public AppDialogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        // get args
        if (getArguments() == null) {
            dismiss();
            return;
        }
        setViews(
                getArguments().getString(TITLE_KEY),
                getArguments().getString(SUB_TITLE_KEY),
                getArguments().getString(POSITIVE_KEY),
                getArguments().getString(NEGATIVE_KEY),
                getArguments().getInt(DIALOG_ICON, -1)
        );
    }

    private void initView(View view) {
        setCancelable(false);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvSubTitle = view.findViewById(R.id.tvSubTitle);
        tvPositive = view.findViewById(R.id.tvPositive);
        tvNegative = view.findViewById(R.id.tvNegative);
        dialogIcon = view.findViewById(R.id.dialogIcon);

        // buttons
        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveCallback != null) positiveCallback.perform();
                dismiss();
            }
        });

        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negativeCallback != null) negativeCallback.perform();
                dismiss();
            }
        });
    }

    private void setViews(String title, String subTitle, String positive, String negative, int drawable) {
        handleView(tvTitle, title);
        handleView(tvSubTitle, subTitle);
        handleView(tvPositive, positive);
        handleView(tvNegative, negative);
        handleView(dialogIcon, drawable);
    }

    private void handleView(TextView view, String item) {
        if (item == null || item.isEmpty()) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(item);
            view.setVisibility(View.VISIBLE);
        }
    }

    private void handleView(ImageView view, int item) {
        if (item == -1) {
            view.setVisibility(View.GONE);
        } else {
            view.setImageDrawable(ContextCompat.getDrawable(requireContext(), item));
            view.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), android.R.color.transparent));
        return dialog;
    }
}