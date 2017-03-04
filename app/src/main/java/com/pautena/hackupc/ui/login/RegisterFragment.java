package com.pautena.hackupc.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pautena.hackupc.R;

/**
 * Created by pautenavidal on 30/8/16.
 */
public class RegisterFragment extends Fragment {
    public static final String TAG = RegisterFragment.class.getSimpleName();
    private static final int REQUEST_TAKE_PHOTO = 0;

    public interface RegisterCallback {
        void onClickLogin();

        void onRegister(String email, String username, String password, String checkPassword);
    }

    private RegisterCallback emptyCallback = new RegisterCallback() {
        @Override
        public void onClickLogin() {

        }

        @Override
        public void onRegister(String email, String username, String password, String checkPassword) {

        }
    };

    public static RegisterFragment newInstance() {

        Bundle args = new Bundle();

        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TextView tvLogin;
    private EditText etEmail;
    private EditText etPassword, etConfirmPassword;
    private EditText etUsername;
    private Button btnRegister;

    private RegisterCallback callback = emptyCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (RegisterCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = emptyCallback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);


        tvLogin = (TextView) view.findViewById(R.id.tv_login);
        etEmail = (EditText) view.findViewById(R.id.email);
        etPassword = (EditText) view.findViewById(R.id.password);
        etConfirmPassword = (EditText) view.findViewById(R.id.confirm_password);
        etUsername = (EditText) view.findViewById(R.id.username);
        btnRegister = (Button) view.findViewById(R.id.btn_register);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onClickLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        return view;
    }

    private void attemptRegister() {
        if (etPassword.getText().toString().trim().length() < 4) {
            String error = getActivity().getResources().getString(R.string.error_invalid_password);
            etPassword.setError(error);
            etPassword.requestFocus();
            return;
        }

        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            String error = getActivity().getResources().getString(R.string.not_equals_password_regsiter);
            etConfirmPassword.setError(error);
            etConfirmPassword.requestFocus();
            return;
        }

        if (!etEmail.getText().toString().trim().contains("@")) {
            String error = getActivity().getResources().getString(R.string.error_invalid_email);
            etEmail.setError(error);
            etEmail.requestFocus();
            return;
        }

        callback.onRegister(etEmail.getText().toString(),
                etPassword.getText().toString(),
                etUsername.getText().toString(),
                etConfirmPassword.getText().toString());


    }

    public void setError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }
}
