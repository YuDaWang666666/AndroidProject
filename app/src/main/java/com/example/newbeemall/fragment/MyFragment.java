package com.example.newbeemall.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.newbeemall.R;
import com.example.newbeemall.activity.AddressListActivity;
import com.example.newbeemall.activity.LoginActivity;
import com.example.newbeemall.activity.OrderListActivity;
import com.example.newbeemall.util.TokenManager;

public class MyFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvUserName;
    private Button btnLogin, btnLogout;
    private LinearLayout layoutOrders, layoutAddress, layoutTeam;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUserName = view.findViewById(R.id.tv_user_name);
        btnLogin = view.findViewById(R.id.btn_login);
        btnLogout = view.findViewById(R.id.btn_logout);
        layoutOrders = view.findViewById(R.id.layout_orders);
        layoutAddress = view.findViewById(R.id.layout_address);
        layoutTeam = view.findViewById(R.id.layout_team);

        btnLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));

        btnLogout.setOnClickListener(v -> {
            TokenManager.clear(getContext());
            updateUI();
        });

        layoutOrders.setOnClickListener(v -> {
            if (!TokenManager.isLoggedIn(getContext())) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            startActivity(new Intent(getActivity(), OrderListActivity.class));
        });

        layoutAddress.setOnClickListener(v -> {
            if (!TokenManager.isLoggedIn(getContext())) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            startActivity(new Intent(getActivity(), AddressListActivity.class));
        });

        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (TokenManager.isLoggedIn(getContext())) {
            tvUserName.setText(TokenManager.getUserName(getContext()));
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvUserName.setText("未登录");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }
    }
}
