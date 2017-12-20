package cn.edu.pku.weather101.weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.weather101.R;
import cn.edu.pku.weather101.app.MyApplication;
import cn.edu.pku.weather101.bean.City;

import static android.R.layout.simple_expandable_list_item_1;


public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;
    private TextView mTitleName;
    private ListView mListView;
    private List<City> mCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        initViews();                                                                                //初始化布局文件
    }

    private void initViews() {

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);                                                          //监听返回按键

        Intent getIntent = getIntent();                                                             //读取Intent传入的城市名，并显示
        String nowCityName = getIntent.getStringExtra("nowCityName");
        mTitleName = (TextView) findViewById(R.id.title_name);
        mTitleName.setText("当前城市：" + nowCityName);

        mListView = (ListView) findViewById(R.id.list_view);
        MyApplication myApplication = (MyApplication) getApplication();
        mCityList = myApplication.getCityList();                                                    //数据库读取城市列表
        ArrayList mCityName = new ArrayList();
        for (City city : mCityList) {
            String cityName = city.getCity();
            mCityName.add(cityName);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this, simple_expandable_list_item_1, mCityName);
        mListView.setAdapter(adapter);                                                              //设置适配器
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                          //处理点击事件
                Log.d("SelectCity", "您单击了：" + position);
                City city = mCityList.get(position);
                Intent intent = new Intent();
                intent.putExtra("cityCode", city.getNumber());                              //返回选择的cityCode
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:                                                                   //处理返回单击事件
                Intent intent = new Intent();
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cityCode = sharedPreferences.getString("city_code", "101010100");
                intent.putExtra("cityCode", cityCode);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
}
