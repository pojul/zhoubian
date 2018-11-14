package com.yjyc.zhoubian.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.yjyc.zhoubian.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowLocActivity extends BaseActivity {

    @BindView(R.id.map_view)
    MapView mapView;

    private BaiduMap mBaiduMap;
    private LatLng lat;
    private Marker postMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_loc);
        ButterKnife.bind(this);

        String latStr = getIntent().getStringExtra("lat");
        try{
            lat = new Gson().fromJson(latStr, LatLng.class);
        }catch(Exception e){
        }
        if(lat == null){
            showToast("数据错误");
            finish();
            return;
        }
        mBaiduMap = mapView.getMap();
        location();
    }

    private void location() {
        if(lat == null){
            return;
        }
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        OverlayOptions option = new MarkerOptions()
                .position(lat)
                .icon(bitmap);
        postMarker = (Marker) mBaiduMap.addOverlay(option);

        MapStatus mMapStatus = new MapStatus.Builder()
                .target(lat)
                .zoom(mBaiduMap.getMaxZoomLevel() - 3)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

}
