package com.mytestapp.todoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * Created by dhanikshah on 1/27/16.
 */
public class EditItemDialogFragment extends DialogFragment implements OnEditorActionListener {
    private EditText etEditedText;
    private int pos;
    private String id;
    public interface EditItemDialogListener {
        void onFinishEditDialog(String inputText, int pos, String id);
    }

    public EditItemDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static EditItemDialogFragment newInstance(String text, int pos, String id) {
        EditItemDialogFragment frag = new EditItemDialogFragment();
        Bundle args = new Bundle();
        args.putString("text", text);
        args.putInt("position", pos);
        args.putString("id", id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_edit_item, container);
        etEditedText = (EditText) view.findViewById(R.id.etEditItem);
        etEditedText.setOnEditorActionListener(this);

        Button btn=(Button)view.findViewById(R.id.btnSaveEditItem);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditItemDialogListener listener = (EditItemDialogListener) getActivity();
                listener.onFinishEditDialog(etEditedText.getText().toString(), pos, id);
                dismiss();
            }
        });

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        dismiss();
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        etEditedText = (EditText) view.findViewById(R.id.etEditItem);

        // Fetch arguments from bundle and set title
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        String itemText = getArguments().getString("text");
        pos = getArguments().getInt("position", -2);
        id = getArguments().getString("id", null);

        etEditedText.setText(itemText);
        etEditedText.setSelection(etEditedText.getText().length());

        // Show soft keyboard automatically and request focus to field
        etEditedText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
