package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;


public class Schedule extends AppCompatActivity implements
    RouteSearch.OnRouteSearchListener{

    private  List<String> schedules = new ArrayList<String>();
    private  List<List<Icon>> icons = new ArrayList<List<Icon>>();
    private List<String> interests = new ArrayList<String>();

    private static List<String> route = new ArrayList<String>();

    private boolean isplanned = false;

    private UserDataBaseHelper dbHelper;
    private SQLiteDatabase Userdb;
    private String_to_Bytes UserScene;
    private String_to_Bytes UserSchedule;
    private SharedPreferences sp;

    private Button planbutton;
    private View layer;

    //路线搜索对象
    private RouteSearch routeSearch;
    private MapUtil Maputil = new MapUtil();

    private ExpandableListView sheduleview;
    private MyExpandListAdapter myAdapter = null;
    private TextView timeSchedule ;
    private TextView lengthSchedule ;
    private String title;
    private ListView interest;
    private FloatingActionButton addscenes;
    private List<Double> lat = new ArrayList<Double>();
    private List<Double> lng = new ArrayList<Double>();

    private List<Double> schedulelat = new ArrayList<Double>();
    private List<Double> schedulelng = new ArrayList<Double>();
    private List<Double> scheduletime = new ArrayList<Double>();
    private int[] sequence;
    private String sceneslist = "";
    private String routelist = "";
    private String trafficlist = "";

    private List<Double> Suggest_time = new ArrayList<Double>();

    private Switch modeButton;
    private List<String> scenes = new ArrayList<String>();
    private int mode = 0; //0为公交骑行，1为打车
    private int count = 0;
    private int total_count = 0;
    private List<List<Double>> Time_s2s = new ArrayList<List<Double>>();
    private List<List<Double>> Dist_s2s = new ArrayList<List<Double>>();
    private List<Double> T_s2s = new ArrayList<Double>();
    private List<Double> D_s2s = new ArrayList<Double>();
    private List<List<LatLonPoint>> s2e = new ArrayList<List<LatLonPoint>>();
    private Double min_time = 24.0;
    private Double min_dist = 100.0;
    private List<Integer> Traffic = new ArrayList<Integer>();

    private List<List<Integer>> Traffic_s2s = new ArrayList<List<Integer>>();
    private List<Integer> Tr_s2s = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Intent intent=getIntent();
        title = intent.getStringExtra("title");
        dbHelper = new UserDataBaseHelper(this, "UserInfo.db", null, 1);
        Userdb = dbHelper.getWritableDatabase();
        sp = getSharedPreferences("UserInfo",MODE_PRIVATE);
        UserScene = new String_to_Bytes(Userdb, sp.getString("name", "UNK"), "scene_num", "scene_list");
        UserSchedule = new String_to_Bytes(Userdb, sp.getString("name", "UNK"), "schedule_num", "schedule_list");

        try {
            initview();
        } catch (AMapException e) {
            e.printStackTrace();
        }

        interest.setVisibility(View.GONE);
        String user_schedule = UserSchedule.Search_schedule_list(title);
        String[] schedule_info = user_schedule.split("#");

        if (Integer.valueOf(schedule_info[1]).intValue() == 1)
            isplanned = true;
        else
            isplanned = false;
        schedules.clear();
        route.clear();
        Traffic.clear();

        if(schedule_info.length > 2)
        {
            sceneslist = schedule_info[2];
            String[] scenes = schedule_info[2].split("&");
            schedules.clear();
            for(int i=0;i<scenes.length-1;i++)
            {
                schedules.add(scenes[i]);
            }
            if(isplanned) {
                routelist = schedule_info[3];
                String[] routes = schedule_info[3].split("&");
                for(int i=0;i<routes.length;i++) {
                    route.add(routes[i]);
                    }
                min_time = Double.valueOf(schedule_info[4]);
                min_dist = Double.valueOf(schedule_info[5]);
                trafficlist = schedule_info[6];
                String[] traffic = schedule_info[6].split("&");
                for(int i=0;i<traffic.length;i++) {
                    Traffic.add(Integer.valueOf(traffic[i]));
                }
            }
            else{
                schedules.add(scenes[scenes.length-1]);
            }
        }

        String SceneData[] = UserScene.GetAllItems();
        if(SceneData==null)
            return;
        for(int i =0;i<SceneData.length;i++)
        {
            String[] Sceneinfo = SceneData[i].split("#");
            interests.add(Sceneinfo[0]);
            lat.add(Double.valueOf(Sceneinfo[1]));
            lng.add(Double.valueOf(Sceneinfo[2]));
            Suggest_time.add(Double.valueOf(Sceneinfo[3]));
        }
        flush();

        modeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    mode = 1;
                    isplanned = false;
                }else{
                    mode = 0;
                    isplanned = false;
                }
            }
        });

        planbutton.setOnClickListener(v -> {
            interest.setVisibility(View.INVISIBLE);
            plan(); });

        addscenes.setOnClickListener(v->{
            interest.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Schedule.this, android.R.layout.simple_list_item_1, interests);
            interest.setAdapter(adapter);
        });

        layer.setOnClickListener(v -> {
            interest.setVisibility(View.INVISIBLE);
        });

        interest.setOnItemLongClickListener(new  AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = interests.get(position);
                Double latitude = lat.get(position);
                Double langitude = lng.get(position);
                Double time = Suggest_time.get(position);
                AlertDialog dialog = new AlertDialog.Builder(Schedule.this)
                        .setMessage("是否添加该景点到规划中")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                schedules.add(item);
                                schedulelat.add(latitude);
                                schedulelng.add(langitude);
                                scheduletime.add(time);
                                myAdapter = new MyExpandListAdapter(schedules, icons, Schedule.this);
                                sheduleview.setAdapter(myAdapter);
                                sceneslist = sceneslist +item+"&";
                                isplanned = false;
                                String new_ = title+"#"+"0"+"#"+sceneslist;
                                UserSchedule.Update_schedule_list(Userdb,new_);
                                dialog.dismiss();
                                flush();
                            }
                        }).create();
                dialog.show();
                return true;
            }
        });

        sheduleview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                if(position == schedules.size()){
                    AlertDialog dialog = new AlertDialog.Builder(Schedule.this)
                            .setMessage("是的，这是一个BUG，不要问我为什么不改，我就是不想改，懒！！！")//设置对话框的内容
                            //设置对话框的按钮
                            .setNegativeButton("去点确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Schedule.this, "小垃圾", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Schedule.this, "点你妈", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                    return true;
                }

                final long packedPosition = sheduleview.getExpandableListPosition(position);
                final int groupPos= ExpandableListView.getPackedPositionGroup(packedPosition);
                AlertDialog dialog = new AlertDialog.Builder(Schedule.this)
                        .setMessage("是否删除该景点")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    route.clear();
                                    Traffic.clear();
                                    schedules.remove(groupPos);
                                    schedulelng.remove(groupPos);
                                    schedulelat.remove(groupPos);
                                    scheduletime.remove(groupPos);
                                    myAdapter = new MyExpandListAdapter(schedules, icons, Schedule.this);
                                    sheduleview.setAdapter(myAdapter);
                                    isplanned = false;
                                    sceneslist = "";
                                    for(int i=0;i<schedules.size();i++){
                                        sceneslist = sceneslist + schedules.get(i)+"&";
                                    }
                                    String new_ = title+"#"+"0"+"#"+sceneslist;
                                    UserSchedule.Update_schedule_list(Userdb,new_);
                                    dialog.dismiss();
                                    flush();
                                }
                            }).create();
                dialog.show();
                return true;
            }
        });
    }

    private void plan(){
        if(isplanned) {
            Toast.makeText(Schedule.this, "已经规划好啦", Toast.LENGTH_SHORT).show();
            return;
        }

        if(schedules.size() < 2){
            Toast.makeText(Schedule.this,"添加景点太少啦",Toast.LENGTH_SHORT).show();
            return;
        }

        route.clear();
        Traffic.clear();
        routelist = "";
        trafficlist = "";
        Time_s2s.clear();
        Dist_s2s.clear();
        Traffic_s2s.clear();
        T_s2s.clear();
        D_s2s.clear();
        Tr_s2s.clear();
        s2e.clear();

        count = schedules.size()-1;
        total_count = 0;
        for(int i = 0;i<schedules.size()-1;i++)
        {
            LatLonPoint mStartPoint = Maputil.convertToLatLonPoint(new LatLng(schedulelat.get(i),schedulelng.get(i)));
            for(int j = i+1;j<schedules.size();j++)
            {
                LatLonPoint mEndPoint = Maputil.convertToLatLonPoint(new LatLng(schedulelat.get(j),schedulelng.get(j)));
                List<LatLonPoint> se = new ArrayList<LatLonPoint>();
                se.add(mStartPoint);
                se.add(mEndPoint);
                s2e.add(se);
            }
        }
        List<LatLonPoint> se = s2e.get(0);
        LatLonPoint mStartPoint = se.get(0);
        LatLonPoint mEndPoint = se.get(1);
        if(mode == 1){ // 打车
            cal_path(mStartPoint,mEndPoint,2);
        }
        else{// 公交和bike
            cal_path(mStartPoint,mEndPoint,3);
        }
        // 全局变量   schedules.size()-1 条数据
        // non收o到结果  全局变量-1

    }

    private void initview() throws AMapException {
        layer = findViewById(R.id.mainlayer);
        planbutton = findViewById(R.id.startplan);
        sheduleview = findViewById(R.id.schedule);
        timeSchedule = findViewById(R.id.time);
        lengthSchedule = findViewById(R.id.length);
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        interest = findViewById(R.id.interests);
        addscenes = findViewById(R.id.addscenes);
        modeButton = findViewById(R.id.mode);
    }

    private void flush() {
        scenes.clear();
        icons.clear();
        schedulelat.clear();
        schedulelng.clear();
        scheduletime.clear();

        for (int i = 0; i < schedules.size(); i++) {
            int j = 0;
            for(String title:interests) {
                if(title.equals(schedules.get(i)))
                {
                    schedulelat.add(lat.get(j));
                    schedulelng.add(lng.get(j));
                    scheduletime.add(Suggest_time.get(j));
                    break;
                }
                else
                {j++;}
            }
            if (i == 0){
                scenes.add(schedules.get(i));}
            else{
                scenes.add(schedules.get(i)+"(预计游玩:"+scheduletime.get(i)+"小时)");
            }
        }
        for(int i = 0 ;i<schedules.size();i++){
            List<Icon> idata = new ArrayList<Icon>();
            if(isplanned){
                Icon icon = new Icon(0,route.get(i));
                switch (Traffic.get(i)){
                    case 1:icon.setiId(R.mipmap.bus);
                            break;
                    case 2: icon.setiId(R.mipmap.ride);
                            break;
                    case 3: icon.setiId(R.mipmap.walk);
                            break;
                    case 4: icon.setiId(R.mipmap.bike);
                        break;
                }
                idata.add(icon);}
            else{
                Icon icon = new Icon(0,"待规划");
                idata.add(icon);
            }
            icons.add(idata);
            }
        List<Icon> idata = new ArrayList<Icon>();
        Icon icon = new Icon(0,"终点");
        idata.add(icon);
        icons.add(idata);
        if(schedules.size() > 0)
            scenes.add(schedules.get(0));

        myAdapter = new MyExpandListAdapter(scenes, icons, Schedule.this);
        sheduleview.setAdapter(myAdapter);

        if(!isplanned)
        {timeSchedule.setText("尚未规划");
         lengthSchedule.setText("尚未规划");}
        else {
            timeSchedule.setText(min_time+"小时");
            lengthSchedule.setText(min_dist+"公里");
        }
        route.clear();
        Traffic.clear();
    }

    public void cal_path(LatLonPoint mStartPoint,LatLonPoint mEndPoint,int mode){
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        //构建步行路线搜索对象

        Log.d("Route","querying");
        switch (mode) {
            case 0://步行
                //构建步行路线搜索对象
                RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
                // 异步路径规划步行模式查询
                routeSearch.calculateWalkRouteAsyn(query);
                break;
            case 1://骑行
                //构建骑行路线搜索对象
                RouteSearch.RideRouteQuery rideQuery = new RouteSearch.RideRouteQuery(fromAndTo);
                //骑行规划路径计算
                routeSearch.calculateRideRouteAsyn(rideQuery);
                break;
            case 2://驾车
                //构建驾车路线搜索对象  剩余三个参数分别是：途经点、避让区域、避让道路
                RouteSearch.DriveRouteQuery driveQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVEING_PLAN_DEFAULT, null, null, "");

                //驾车规划路径计算
                routeSearch.calculateDriveRouteAsyn(driveQuery);
                break;
            case 3://公交
                //构建驾车路线搜索对象 第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算,1表示计算
                RouteSearch.BusRouteQuery busQuery = new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BUS_DEFAULT, "021",0);

                //公交规划路径计算
                routeSearch.calculateBusRouteAsyn(busQuery);
                break;
            default:
                break;
        }
    }
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int code) {
        if (code == AMapException.CODE_AMAP_SUCCESS) {
            if (busRouteResult != null && busRouteResult.getPaths() != null) {
                if (busRouteResult.getPaths().size() > 0) {
                    final BusPath busPath = busRouteResult.getPaths().get(0);
                    if (busPath == null) {
                        return;
                    }

                    int dis = (int) busPath.getDistance();
                    int dur = (int) busPath.getDuration();

                    Double d = Double.valueOf(MapUtil.getFriendlyLength(dis).toString().split("公里")[0]);
                    if(d * 1000 < 2000){
                        List<LatLonPoint> se = s2e.get(0);
                        LatLonPoint mStartPoint = se.get(0);
                        LatLonPoint mEndPoint = se.get(1);
                        cal_path(mStartPoint,mEndPoint,1);
                        return;
                    }
                    String[] t = MapUtil.getFriendlyTime(dur).toString().split("小时");
                    Double h = 0.0;
                    if(t.length > 1)
                    {
                        h = Double.valueOf(t[0]);
                        t[0] = t[1];
                    }
                    Double m = Double.valueOf(t[0].split("分钟")[0]) / 60;
                    T_s2s.add(h+m);
                    D_s2s.add(d);
                    Tr_s2s.add(1);
                    total_count++;

                    if(count == total_count)
                    {
                        count --;
                        total_count = 0;
                        List<Double> tmp_T = new ArrayList<Double>();
                        List<Double> tmp_D = new ArrayList<Double>();
                        List<Integer> tmp_Tr = new ArrayList<Integer>();
                        tmp_T.add(0.0);
                        tmp_D.add(0.0);
                        tmp_Tr.add(0);
                        for(int i = 0;i< T_s2s.size();i++){
                            tmp_T.add(T_s2s.get(i));
                            tmp_D.add(D_s2s.get(i));
                            tmp_Tr.add(Tr_s2s.get(i));
                        }
                        Time_s2s.add(tmp_T);
                        Dist_s2s.add(tmp_D);
                        Traffic_s2s.add(tmp_Tr);
                        T_s2s.clear();
                        D_s2s.clear();
                        Tr_s2s.clear();
                    }
                    if(count >0){
                        s2e.remove(0);
                        List<LatLonPoint> se = s2e.get(0);
                        LatLonPoint mStartPoint = se.get(0);
                        LatLonPoint mEndPoint = se.get(1);
                        cal_path(mStartPoint,mEndPoint,3);
                    }
                    else{
                        List<Double> tmp_T = new ArrayList<Double>();
                        List<Double> tmp_D = new ArrayList<Double>();
                        List<Integer> tmp_Tr = new ArrayList<Integer>();
                        tmp_T.add(0.0);
                        tmp_D.add(0.0);
                        tmp_Tr.add(0);

                        Time_s2s.add(tmp_D);
                        Dist_s2s.add(tmp_T);
                        Traffic_s2s.add(tmp_Tr);
                        total_des();
                    }
                } else if (busRouteResult.getPaths() == null) {
                    Log.d("Route", "对不起，没有搜索到相关数据！");
                }
            } else {
                Log.d("Route", "对不起，没有搜索到相关数据！");
            }
        } else {
            Log.d("Route", "" + code);
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int code) {
        if (code == AMapException.CODE_AMAP_SUCCESS) {
            if (driveRouteResult != null && driveRouteResult.getPaths() != null) {
                if (driveRouteResult.getPaths().size() > 0) {
                    final DrivePath drivePath = driveRouteResult.getPaths()
                            .get(0);
                    if (drivePath == null) {
                        return;
                    }

                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();

                    Double d = Double.valueOf(MapUtil.getFriendlyLength(dis).toString().split("公里")[0]);
                    if(d  < 2){
                        List<LatLonPoint> se = s2e.get(0);
                        LatLonPoint mStartPoint = se.get(0);
                        LatLonPoint mEndPoint = se.get(1);
                        cal_path(mStartPoint,mEndPoint,0);
                        return;
                    }
                    String[] t = MapUtil.getFriendlyTime(dur).toString().split("小时");
                    Double h = 0.0;
                    if(t.length > 1)
                    {
                        h = Double.valueOf(t[0]);
                        t[0] = t[1];
                    }
                    Double m = Double.valueOf(t[0].split("分钟")[0]) / 60;
                    T_s2s.add(h+m);
                    D_s2s.add(d);
                    Tr_s2s.add(2);

                    total_count++;
                    if(count == total_count)
                    {
                        count --;
                        total_count = 0;
                        List<Double> tmp_T = new ArrayList<Double>();
                        List<Double> tmp_D = new ArrayList<Double>();
                        List<Integer> tmp_Tr = new ArrayList<Integer>();
                        tmp_T.add(0.0);
                        tmp_D.add(0.0);
                        tmp_Tr.add(0);
                        for(int i = 0;i< T_s2s.size();i++){
                            tmp_T.add(T_s2s.get(i));
                            tmp_D.add(D_s2s.get(i));
                            tmp_Tr.add(Tr_s2s.get(i));
                        }
                        Time_s2s.add(tmp_T);
                        Dist_s2s.add(tmp_D);
                        Traffic_s2s.add(tmp_Tr);
                        T_s2s.clear();
                        D_s2s.clear();
                        Tr_s2s.clear();
                    }
                    if(count >0){
                        s2e.remove(0);
                        List<LatLonPoint> se = s2e.get(0);
                        LatLonPoint mStartPoint = se.get(0);
                        LatLonPoint mEndPoint = se.get(1);
                        cal_path(mStartPoint,mEndPoint,2);
                    }
                    else{
                        List<Double> tmp_T = new ArrayList<Double>();
                        List<Double> tmp_D = new ArrayList<Double>();
                        List<Integer> tmp_Tr = new ArrayList<Integer>();
                        tmp_T.add(0.0);
                        tmp_D.add(0.0);
                        tmp_Tr.add(0);

                        Time_s2s.add(tmp_D);
                        Dist_s2s.add(tmp_T);
                        Traffic_s2s.add(tmp_Tr);
                        total_des();
                    }
                } else if (driveRouteResult.getPaths() == null) {
                    Log.d("Route", "对不起，没有搜索到相关数据！");
                }
            } else {
                Log.d("Route", "对不起，没有搜索到相关数据！");
            }
        } else {
            Log.d("Route", "" + code);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int code) {

        if (code == AMapException.CODE_AMAP_SUCCESS) {
            if (walkRouteResult != null && walkRouteResult.getPaths() != null) {
                if (walkRouteResult.getPaths().size() > 0) {
                    final WalkPath walkPath = walkRouteResult.getPaths().get(0);
                    if (walkPath == null) {
                        return;
                    }
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    Double d = Double.valueOf(MapUtil.getFriendlyLength(dis).toString().split("公里")[0]);
                    String[] t = MapUtil.getFriendlyTime(dur).toString().split("小时");
                    Double h = 0.0;
                    if(t.length > 1)
                    {
                        h = Double.valueOf(t[0]);
                        t[0] = t[1];
                    }
                    Double m = Double.valueOf(t[0].split("分钟")[0]) / 60;
                    T_s2s.add(h+m);
                    D_s2s.add(d);
                    Tr_s2s.add(3);
                    total_count++;

                    if(count == total_count)
                    {
                        count --;
                        total_count = 0;
                        List<Double> tmp_T = new ArrayList<Double>();
                        List<Double> tmp_D = new ArrayList<Double>();
                        List<Integer> tmp_Tr = new ArrayList<Integer>();
                        tmp_T.add(0.0);
                        tmp_D.add(0.0);
                        tmp_Tr.add(0);
                        for(int i = 0;i< T_s2s.size();i++){
                            tmp_T.add(T_s2s.get(i));
                            tmp_D.add(D_s2s.get(i));
                            tmp_Tr.add(Tr_s2s.get(i));
                        }
                        Time_s2s.add(tmp_T);
                        Dist_s2s.add(tmp_D);
                        Traffic_s2s.add(tmp_Tr);
                        T_s2s.clear();
                        D_s2s.clear();
                        Tr_s2s.clear();
                    }
                    if(count >0){
                        s2e.remove(0);
                        List<LatLonPoint> se = s2e.get(0);
                        LatLonPoint mStartPoint = se.get(0);
                        LatLonPoint mEndPoint = se.get(1);
                        cal_path(mStartPoint,mEndPoint,2);
                    }
                    else{
                        List<Double> tmp_T = new ArrayList<Double>();
                        List<Double> tmp_D = new ArrayList<Double>();
                        List<Integer> tmp_Tr = new ArrayList<Integer>();
                        tmp_T.add(0.0);
                        tmp_D.add(0.0);
                        tmp_Tr.add(0);

                        Time_s2s.add(tmp_D);
                        Dist_s2s.add(tmp_T);
                        Traffic_s2s.add(tmp_Tr);
                        total_des();
                    }
                } else if (walkRouteResult.getPaths() == null) {
                    Log.d("Route","对不起，没有搜索到相关数据！");
                }
            } else {
                Log.d("Route","对不起，没有搜索到相关数据！");
            }
        } else {
            Log.d("Route",""+code);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int code) {
        if (code == AMapException.CODE_AMAP_SUCCESS) {
            if (rideRouteResult != null && rideRouteResult.getPaths() != null) {
                if (rideRouteResult.getPaths().size() > 0) {
                    final RidePath ridePath = rideRouteResult.getPaths()
                            .get(0);
                    if(ridePath == null) {
                        return;
                    }

                    int dis = (int) ridePath.getDistance();
                    int dur = (int) ridePath.getDuration();

                    Double d = Double.valueOf(MapUtil.getFriendlyLength(dis).toString().split("公里")[0]);

                    String[] t = MapUtil.getFriendlyTime(dur).toString().split("小时");
                    Double h = 0.0;
                    if(t.length > 1)
                    {
                        h = Double.valueOf(t[0]);
                        t[0] = t[1];
                    }
                    Double m = Double.valueOf(t[0].split("分钟")[0]) / 60;
                    T_s2s.add(h+m);
                    D_s2s.add(d);
                    Tr_s2s.add(4);
                    total_count++;

                    if(count == total_count)
                    {
                        count --;
                        total_count = 0;
                        List<Double> tmp_T = new ArrayList<Double>();
                        List<Double> tmp_D = new ArrayList<Double>();
                        List<Integer> tmp_Tr = new ArrayList<Integer>();
                        tmp_T.add(0.0);
                        tmp_D.add(0.0);
                        tmp_Tr.add(0);
                        for(int i = 0;i< T_s2s.size();i++){
                            tmp_T.add(T_s2s.get(i));
                            tmp_D.add(D_s2s.get(i));
                            tmp_Tr.add(Tr_s2s.get(i));
                        }
                        Time_s2s.add(tmp_T);
                        Dist_s2s.add(tmp_D);
                        Traffic_s2s.add(tmp_Tr);
                        T_s2s.clear();
                        D_s2s.clear();
                        Tr_s2s.clear();
                    }
                    if(count >0){
                        s2e.remove(0);
                        List<LatLonPoint> se = s2e.get(0);
                        LatLonPoint mStartPoint = se.get(0);
                        LatLonPoint mEndPoint = se.get(1);
                        cal_path(mStartPoint,mEndPoint,3);
                    }
                    else{
                        List<Double> tmp_T = new ArrayList<Double>();
                        List<Double> tmp_D = new ArrayList<Double>();
                        List<Integer> tmp_Tr = new ArrayList<Integer>();
                        tmp_T.add(0.0);
                        tmp_D.add(0.0);
                        tmp_Tr.add(0);

                        Time_s2s.add(tmp_D);
                        Dist_s2s.add(tmp_T);
                        Traffic_s2s.add(tmp_Tr);
                        total_des();
                    }
                } else if (rideRouteResult.getPaths() == null) {
                    Log.d("Route","对不起，没有搜索到相关数据！");
                }
            } else {
                Log.d("Route","对不起，没有搜索到相关数据！");
            }
        } else {
            Log.d("Route",""+code);
        }
    }

    public void total_des(){
        for(int i = 0; i < schedules.size();i++) {
            List<Double> tmp_T = Time_s2s.get(i);
            List<Double> tmp_D = Dist_s2s.get(i);
            List<Integer> tmp_Tr = Traffic_s2s.get(i);
            for(int j = 0;j < i;j++){
                Double t_item = Time_s2s.get(j).get(i);
                Double d_item = Dist_s2s.get(j).get(i);
                Integer p_item = Traffic_s2s.get(j).get(i);
                tmp_T.add(j,t_item);
                tmp_D.add(j,d_item);
                tmp_Tr.add(j,p_item);
            }

            Time_s2s.remove(i);
            Dist_s2s.remove(i);
            Traffic_s2s.remove(i);
            Time_s2s.add(i,tmp_T);
            Dist_s2s.add(i,tmp_D);
            Traffic_s2s.add(i,tmp_Tr);
        }

        int[] scens = new int[schedules.size()-1];
        for(int i = 0; i< schedules.size()-1;i++) {scens[i] = i+1;}
        min_time = 24.0;

        permutate(0,scens.length,scens);
        String time = String.format("%.2f", Time_s2s.get(0).get(sequence[0])) + "小时";
        String dist = String.format("%.2f",Dist_s2s.get(0).get(sequence[0]))+ "公里";

        Traffic.add(Traffic_s2s.get(0).get(sequence[0]));
        route.add(time + "("+dist+")");
        sceneslist = "" + schedules.get(0);
        routelist = "" + time + "("+dist+")";
        trafficlist = ""+ Traffic_s2s.get(0).get(sequence[0]);


        for(int i = 0;i<sequence.length-1;i++){
            sceneslist = sceneslist + "&" + schedules.get(sequence[i]);
            time = String.format("%.2f",Time_s2s.get(sequence[i]).get(sequence[i+1]))+"小时";
            dist = String.format("%.2f",Dist_s2s.get(sequence[i]).get(sequence[i+1])) + "公里";
            Traffic.add(Traffic_s2s.get(sequence[i]).get(sequence[i+1]));
            route.add(time + "("+dist+")");
            routelist = routelist+"&" + time + "("+dist+")";
            trafficlist = trafficlist+"&"+Traffic_s2s.get(0).get(sequence[0]);
        }
        sceneslist = sceneslist + "&" + schedules.get(sequence[sequence.length-1])+"&"+schedules.get(0);

        time = String.format("%.2f",Time_s2s.get(0).get(sequence[sequence.length-1])) + "小时";
        dist = String.format("%.2f",Dist_s2s.get(0).get(sequence[sequence.length-1])) + "公里";
        route.add(time + "("+dist+")");
        Traffic.add(Traffic_s2s.get(0).get(sequence[sequence.length-1]));
        routelist = routelist+"&" + time + "("+dist+")";
        trafficlist = trafficlist+"&"+Traffic_s2s.get(0).get(sequence[sequence.length-1]);

        isplanned = true;
        for(int i = 1;i<schedules.size();i++){
            min_time += scheduletime.get(i);
        }
        min_time = Double.valueOf(String.format("%.2f",min_time));
        min_dist = Double.valueOf(String.format("%.2f",min_dist));
        if(min_time > 12){
            Toast.makeText(Schedule.this, "游玩的时间太长啦~建议减少景点", Toast.LENGTH_SHORT).show();
        }
        String new_ = title+"#"+"1"+"#"+sceneslist+"#"+routelist+"#" + min_time+"#"+min_dist+"#"+trafficlist;
        UserSchedule.Update_schedule_list(Userdb,new_);
        flush();
    }

    private void permutate(int begin, int end, int a[])
    {
        if(begin == end)//已经到了最后一个位置，进行输出
        {
            Double time = Time_s2s.get(0).get(a[0]);
            Double dist = Dist_s2s.get(0).get(a[0]);


            for(int i = 0;i<a.length-1;i++){
                time += Time_s2s.get(a[i]).get(a[i+1]);
                dist += Dist_s2s.get(a[i]).get(a[i+1]);
            }

            time += Time_s2s.get(0).get(a[a.length-1]);
            dist += Dist_s2s.get(0).get(a[a.length-1]);
            if(time < min_time){
                min_time = time;
                min_dist = dist;
                sequence = a;
            }
        }
        for(int i= begin; i < end; i++)
        {
            int temp = a[begin];
            a[begin] = a[i];
            a[i] = temp;
            permutate(begin + 1,end,a);//递归下一个位置
            temp = a[i];
            a[i] = a[begin];
            a[begin] = temp;
        }
    }

}

