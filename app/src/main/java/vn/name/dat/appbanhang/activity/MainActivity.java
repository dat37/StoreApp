package vn.name.dat.appbanhang.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.name.dat.appbanhang.R;
import vn.name.dat.appbanhang.adapter.LoaiSpAdapter;
import vn.name.dat.appbanhang.adapter.SanPhamMoiAdapter;
import vn.name.dat.appbanhang.model.LoaiSp;
import vn.name.dat.appbanhang.model.SanPhamMoi;
import vn.name.dat.appbanhang.model.SanPhamMoiModel;
import vn.name.dat.appbanhang.model.User;
import vn.name.dat.appbanhang.retrofit.ApiBanHang;
import vn.name.dat.appbanhang.retrofit.RetrofitClient;
import vn.name.dat.appbanhang.utils.Utils;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);
        if (Paper.book().read("user") != null){
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        Anhxa();
        ActionBar();

        if (isConnected(this)){
            //Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        }else {
            Toast.makeText(getApplicationContext(), "kiểm tra lại kết nối internet", Toast.LENGTH_LONG).show();
        }
    }

    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent dienthoai = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        dienthoai.putExtra("loai", 1);
                        //dienthoai.putExtra("loai", mangloaisp.get(i).getId());
                        startActivity(dienthoai);
                        break;
                    case 2:
                        Intent laptop = new Intent(getApplicationContext(), LaptopActivity.class);
                        laptop.putExtra("loai", 2);
                        startActivity(laptop);
                        break;
                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case 6:
                        // xóa key user
                        Paper.book().delete("user");
                        Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();
                        break;
                }
            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            sanPhamMoiModel -> {
                if (sanPhamMoiModel.isSuccess()){
                    mangSpMoi = sanPhamMoiModel.getResult();
                    spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                    recyclerViewManHinhChinh.setAdapter(spAdapter);
                }
            },
            throwable -> {
                Toast.makeText(getApplicationContext(), "Không kết nối được với server"+throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                loaiSpModel -> {
                    if (loaiSpModel.isSuccess()){
                        //Toast.makeText(getApplicationContext(), loaiSpModel.getResult().get(0).getTensanpham(), Toast.LENGTH_LONG).show();
                        mangloaisp = loaiSpModel.getResult();
                        mangloaisp.add(new LoaiSp("Đăng xuất", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRj2Q2NrNfsWjQ7M40wS2uNTiJxrXaGRb1z6A&usqp=CAU"));
                        loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                        listViewManHinhChinh.setAdapter(loaiSpAdapter);
                    }
                }
        ));
    }

    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-HC-Tra-Gop-800-300.png");
        mangquangcao.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Le-hoi-phu-kien-800-300.png");
        mangquangcao.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-big-ky-nguyen-800-300.jpg");
        mangquangcao.add("https://cdn.cellphones.com.vn/media/ltsoft/promotion/iPhone_11-new.png");
        for (int i = 0; i<mangquangcao.size(); i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    private void Anhxa() {
        imgsearch = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toobarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);
        //Khoi tao list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if (Utils.manggiohang == null){
            Utils.manggiohang = new ArrayList<>();
        }else {
            int totalItem = 0;
            for (int i = 0; i < Utils.manggiohang.size(); i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });

        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for (int i = 0; i < Utils.manggiohang.size(); i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected())){
            return  true;
        }else {
            return false;
        }

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}