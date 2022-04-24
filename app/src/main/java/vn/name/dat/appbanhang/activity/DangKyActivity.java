package vn.name.dat.appbanhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.name.dat.appbanhang.R;
import vn.name.dat.appbanhang.retrofit.ApiBanHang;
import vn.name.dat.appbanhang.retrofit.RetrofitClient;
import vn.name.dat.appbanhang.utils.Utils;

public class DangKyActivity extends AppCompatActivity {
    EditText email, pass, repass, mobile, username;
    AppCompatButton button;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);
        initView();
        initControll();
    }

    private void initControll() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangki();
            }
        });

    }

    private void dangki() {
        String str_email = email.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();
        String str_repass = repass.getText().toString().trim();
        String str_mobile = mobile.getText().toString().trim();
        String str_user = username.getText().toString().trim();
        if (TextUtils.isEmpty(str_email)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_pass)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_repass)){
            Toast.makeText(getApplicationContext(), "Nhập lại mật khẩu", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_mobile)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_user)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập số Tên", Toast.LENGTH_SHORT).show();
        }else {
            if (str_pass.equals(str_repass)){
                //post data
                compositeDisposable.add(apiBanHang.dangki(str_email, str_pass, str_user, str_mobile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()){
                                Toast.makeText(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                Utils.user_current.setEmail(str_email);
                                Utils.user_current.setPass(str_pass);
                                Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
            }else {
                Toast.makeText(getApplicationContext(), "Mật khẩu phải giống nhau", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        repass = findViewById(R.id.repass);
        mobile = findViewById(R.id.mobile);
        username = findViewById(R.id.username);
        button = findViewById(R.id.btndangky);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}