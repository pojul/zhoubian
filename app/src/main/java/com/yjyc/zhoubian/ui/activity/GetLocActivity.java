package com.yjyc.zhoubian.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.yjyc.zhoubian.R;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GetLocActivity extends BaseActivity {

    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.center_loc)
    ImageView centerLoc;
    @BindView(R.id.sure)
    TextView sure;
    private BaiduMap mBaiduMap;

    private LatLng rawLoc;
    private LatLng selectedLoc;
    private GeoCoder geoCoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_loc);
        ButterKnife.bind(this);

        String rawLocStr = getIntent().getStringExtra("rawLoc");
        try{
            rawLoc = new Gson().fromJson(rawLocStr, LatLng.class);
        }catch(Exception e){
        }
        mBaiduMap = mapView.getMap();
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(getGeoCoderResultListener);
        centerToLoc();

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                selectedLoc = mapStatus.target;
            }
        });

    }

    private void latlngToAddress(LatLng latlng) {
        LoadingDialog.showLoading(this);
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                .location(latlng));
    }

    private void centerToLoc() {
        MapStatus.Builder mapStstusBuilder = new MapStatus.Builder()
                .zoom(mBaiduMap.getMaxZoomLevel() - 3);
        if(rawLoc != null){
            mapStstusBuilder.target(rawLoc);
        }
        MapStatus mMapStatus = mapStstusBuilder.build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    private OnGetGeoCoderResultListener getGeoCoderResultListener =  new OnGetGeoCoderResultListener() {
        //经纬度转换成地址
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            LoadingDialog.closeLoading();
            if (result == null ||  result.error != SearchResult.ERRORNO.NO_ERROR) {
                showToast("获取地址失败");
                return;
            }
            String resultStr = new Gson().toJson(result);
            Intent intent = new Intent();
            intent.putExtra("locResult", resultStr);
            setResult(RESULT_OK, intent);
            finish();
        }
        //把地址转换成经纬度
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {
            // 详细地址转换在经纬度
            LoadingDialog.closeLoading();
            //String address=result.getAddress();
        }
    };

    @OnClick({R.id.sure})
    public void click(View v){
        switch (v.getId()){
            case R.id.sure:
                if(selectedLoc == null){
                    showToast("请移动地图选择位置");
                    return;
                }
                latlngToAddress(selectedLoc);
                break;
        }
    }
}
