package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MultiPointItem;
import com.amap.api.maps.model.MultiPointOverlay;
import com.amap.api.maps.model.MultiPointOverlayOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pub.devrel.easypermissions.EasyPermissions;


public class search extends AppCompatActivity implements
        PoiSearch.OnPoiSearchListener
        , AMap.OnMapClickListener,AMap.OnMapLongClickListener,
        GeocodeSearch.OnGeocodeSearchListener, View.OnKeyListener{
    private MapView mMapView = null;
    //请求权限码
    private static final int REQUEST_PERMISSIONS = 9527;
    //定位样式
    private MyLocationStyle myLocationStyle = new MyLocationStyle();
    //城市
    private String city;

    //地图控制器
    private AMap aMap = null;
    //位置更改监听
    private LocationSource.OnLocationChangedListener mListener;
    //定义一个UiSettings对象
    private UiSettings mUiSettings;

    //城市码
    private String cityCode = null;

    //地理编码搜索
    private GeocodeSearch geocodeSearch;
    //解析成功标识码
    private static final int PARSE_SUCCESS_CODE = 1000;
    private EditText etAddress;
    private ListView scene_list;
    private List<String> scenes = new ArrayList<String>();
    private List<Double> latitude = new ArrayList<Double>();
    private List<Double> longitude = new ArrayList<Double>();
    private UserDataBaseHelper dbHelper;
    private SQLiteDatabase Userdb;
    private String_to_Bytes UserScene;
    private SharedPreferences sp;
    private Random r = new Random(); // 生成随机时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new UserDataBaseHelper(this, "UserInfo.db", null, 1);
        Userdb = dbHelper.getWritableDatabase();
        sp = getSharedPreferences("UserInfo",MODE_PRIVATE);
        UserScene = new String_to_Bytes(Userdb, sp.getString("name", "UNK"), "scene_num", "scene_list");

        setContentView(R.layout.activity_search);
        MapsInitializer.updatePrivacyShow(this,true,true);
        MapsInitializer.updatePrivacyAgree(this,true);
        scene_list = findViewById(R.id.scenelist);
        scene_list.setVisibility(View.GONE);
        //获取地图控件引用
        try{
            initMap(savedInstanceState);
        }catch (Exception e){
            System.out.println("e = " + e);
        }
        etAddress = findViewById(R.id.et_address);
        //添加监听
        etAddress.setOnKeyListener(this);

    }

    private void click(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(search.this, android.R.layout.simple_list_item_1, scenes);
        scene_list.setAdapter(adapter);

        scene_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = scenes.get(position);
                Toast.makeText(search.this,item,Toast.LENGTH_SHORT).show();
            }
        });

        scene_list.setOnItemLongClickListener(new  AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter.getItem(position);
                Double lat = latitude.get(position);
                Double lgt = longitude.get(position);
                Double time = r.nextInt(8)*0.5+2;
                Log.d("add_scene",item);
                AlertDialog dialog = new AlertDialog.Builder(search.this)
                        .setMessage("是否添加该景点")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(search.this, "点击了取消按钮", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String scene_info = item+"#"+lat+"#"+lgt+"#"+time;

                                UserScene.Add_item(Userdb,scene_info);
                                Log.d("add_scene",scene_info);
                                Toast.makeText(search.this, "添加"+item+"啦", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
                return true;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater mi=getMenuInflater();/*把文件变成Menu对象*/
        mi.inflate(R.menu.menu,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId") // 这个是目录
    public boolean onOptionsItemSelected(MenuItem Item)
    {
        Toast toast;
        switch (Item.getItemId())
        {
            case R.id.search:
                toast= Toast.makeText(search.this,"你已经在搜索界面啦",Toast.LENGTH_LONG);
                toast.show();
                break;
            case R.id.calender:
                Intent intent2cal =  new Intent(search.this, calender.class);
                startActivity(intent2cal);
                break;
            case  R.id.myself:
                Intent intent2my =  new Intent(search.this,UserInfoShow.class);
                startActivity(intent2my);
                break;
            case R.id.exit:
                Intent intent2log =  new Intent(search.this, MainActivity.class);
                startActivity(intent2log);
                break;
            default:
                return super.onOptionsItemSelected(Item);
        }

        return true;
    }

    //移动到指定经纬度
    private void MovetoLat(AMap aMap, LatLng latlng) {
        CameraPosition cameraPosition = new CameraPosition(latlng, 15, 0, 30);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        aMap.moveCamera(cameraUpdate);
        //添加标点
        // aMap.addMarker(new MarkerOptions().position(latlng).snippet("DefaultMarker"));

    }


    /**
     * 键盘点击
     *
     * @param v
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            //获取输入框的值
            String address = etAddress.getText().toString().trim();
            if (address == null || address.isEmpty()) {
                showMsg("请输入地址");
            }else {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                // name表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode
//                GeocodeQuery query = new GeocodeQuery(address,city);
//                geocodeSearch.getFromLocationNameAsyn(query);

                PoiSearch.Query query = new PoiSearch.Query(address,"",city);
                query.setPageSize(10);
                query.setPageNum(0);

                ServiceSettings.updatePrivacyShow(this,true,true);
                ServiceSettings.updatePrivacyAgree(this,true);

                try {
                    PoiSearch poiSearch = new PoiSearch(this, query);
                    poiSearch.setOnPoiSearchListener(this);
                    poiSearch.searchPOIAsyn();
                } catch (AMapException e) {
                    e.printStackTrace();
                }

            }
            return true;
        }
        return false;
    }

    /**
     * 坐标转地址
     * @param regeocodeResult
     * @param rCode
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
        //解析result获取地址描述信息
        if(rCode == PARSE_SUCCESS_CODE){
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            //显示解析后的地址
            String formatAddress = regeocodeAddress.getFormatAddress();
            showMsg("地址："+formatAddress);

        }else {
            showMsg("获取地址失败");
        }
    }

    /**
     * 地址转坐标
     * @param geocodeResult
     * @param rCode
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
        if (rCode == PARSE_SUCCESS_CODE) {
            List<GeocodeAddress> geocodeAddressList = geocodeResult.getGeocodeAddressList();
            if(geocodeAddressList!=null && geocodeAddressList.size()>0){
                LatLonPoint latLonPoint = geocodeAddressList.get(0).getLatLonPoint();
                //显示解析后的坐标
                showMsg("坐标：" + latLonPoint.getLongitude()+"，"+latLonPoint.getLatitude());
                MovetoLat(aMap, new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()));
            }

        } else {
            showMsg("获取坐标失败");
        }
    }



    /**
     * 地图单击事件
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        latlonToAddress(latLng);
    }

    /**
     * 地图长按事件
     * @param latLng
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        showMsg("长按了地图，经度："+latLng.longitude+"，纬度："+latLng.latitude);
    }

    /**
     * 通过经纬度获取地址
     * @param latLng
     */
    private void latlonToAddress(LatLng latLng) {
        //位置点  通过经纬度进行构建
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        //逆编码查询  第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 20, GeocodeSearch.AMAP);
        //异步获取地址信息
        geocodeSearch.getFromLocationAsyn(query);
    }


    /**
     * 初始化地图
     * @param savedInstanceState
     */
    private void initMap(Bundle savedInstanceState) throws AMapException {
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        aMap = mMapView.getMap();

        // 设置定位监听
        // aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);

        //构造对象并监听
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);

        //设置最小缩放等级为16 ，缩放级别范围为[3, 20]
        aMap.setMinZoomLevel(3);

        myLocationStyle.interval(2000);
        myLocationStyle.showMyLocation(true);

        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色  都为0则透明
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 100));
        // 自定义精度范围的圆形边框宽度  0 无宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色  都为0则透明
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 100));
        // 设置定位方式
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        // 设置默认定位按钮是否显示，非必须设置
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        myLocationStyle.anchor(1.0f,1.0f);
        aMap.setMyLocationEnabled(true);

        // 设置地图点击事件
        aMap.setOnMapClickListener((AMap.OnMapClickListener) this);
        // 设置地图长按事件
        aMap.setOnMapLongClickListener((AMap.OnMapLongClickListener) this);

        //实例化UiSettings类对象
        mUiSettings = aMap.getUiSettings();
        //隐藏缩放按钮
        mUiSettings.setZoomControlsEnabled(false);
        //显示比例尺 默认不显示
        mUiSettings.setScaleControlsEnabled(true);
    }

    /**
     * Toast提示
     * @param msg 提示内容
     */
    private void showMsg(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 请求权限结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //设置权限请求结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }



    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
    /**
     * POI搜索返回
     *
     * @param poiResult POI所有数据
     * @param
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        ArrayList<PoiItem> poiItems = poiResult.getPois();
        PoiItem topitem = poiItems.get(0);
        MovetoLat(aMap, new LatLng(topitem.getLatLonPoint().getLatitude(), topitem.getLatLonPoint().getLongitude()));
        scenes.clear();
        latitude.clear();
        longitude.clear();
        for (PoiItem poiItem : poiItems) {
            Log.d("search", " Title：" + poiItem.getTitle() + " Snippet：" + poiItem.getSnippet() + "position:" + poiItem.getLatLonPoint().getLongitude() + poiItem.getLatLonPoint().getLatitude());
            LatLng itemlatlng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(itemlatlng)
                    .title(poiItem.getTitle())
                    .snippet(poiItem.getSnippet())
                    .draggable(true)
                    .visible(true)
                    .anchor(0.5f, 1f)
                    .alpha(0.8f);

            latitude.add(poiItem.getLatLonPoint().getLatitude());
            longitude.add(poiItem.getLatLonPoint().getLongitude());
            scenes.add(poiItem.getTitle());
            aMap.addMarker(markerOptions);
        }
        scene_list.setVisibility(View.VISIBLE);
        click();
    }
    /**
     * POI中的项目搜索返回
     *
     * @param poiItem 获取POI item
     * @param i
     */
    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
    }

}

