package cn.edu.pku.weather101.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.weather101.R;
import cn.edu.pku.weather101.app.MyApplication;
import cn.edu.pku.weather101.bean.City;

import static android.R.layout.simple_expandable_list_item_1;

public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;
    private ListView mListView;
    private List<City> mCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        initViews();
    }

    private void initViews() {

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.list_view);
        MyApplication myApplication = (MyApplication) getApplication();
        mCityList = myApplication.getCityList();
        ArrayList mCityName = new ArrayList();
        for (City city : mCityList) {
            String cityName = city.getCity();
            mCityName.add(cityName);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this, simple_expandable_list_item_1, mCityName);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SelectCity","您单击了：" + position);
                City city = mCityList.get(position);
                Intent intent = new Intent();
                intent.putExtra("cityCode",city.getNumber());
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                Intent intent = new Intent();
                intent.putExtra("cityCode", "101160101");
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
}
