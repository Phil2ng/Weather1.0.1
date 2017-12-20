package cn.edu.pku.weather101.weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cn.edu.pku.weather101.R;
import cn.edu.pku.weather101.bean.TodayWeather;
import cn.edu.pku.weather101.util.NetUtil;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    private ImageView mUpdateBtn, mCitySelect;
    private TextView cityTv, timeTv, humidityTv, temperatureNowTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);                                     //更新天气
                    break;
                default:
                    break;
            }
        }

    };.
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                                                     //设置布局文件

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);                               //获取控件
        mUpdateBtn.setOnClickListener(this);                                                        //设置点击监听
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {                       //检查网络
            Log.d("myWeather", "初始网络OK");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }

        initView();                                                                                 //初始化
        mUpdateBtn.performClick();                                                                  //模拟点击更新事件
    }

    void initView() {                                                                               //初始化布局文件
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        temperatureNowTv = (TextView) findViewById(R.id.temperature_now);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        city_name_Tv.setText("N/A");                                                                //设置“N/A”默认值
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        temperatureNowTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    @Override
    public void onClick(View v) {                                                                   //处理点击事件
        long curClickTime = System.currentTimeMillis();
        if (v.getId() == R.id.title_update_btn && (curClickTime - lastClickTime) > MIN_CLICK_DELAY_TIME) {  //判断上一次点击更新按键与本次时间差
            lastClickTime = curClickTime;                                                           //本次点击时间更新上次点击时间
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE); //
            String cityCode = sharedPreferences.getString("city_code", "101010100");//取出key为city_code的值，指定默认值
            Log.d("myWeather", cityCode);                                                      //打印出cityCode

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {                   //检查网络
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
        if (v.getId() == R.id.title_city_manager) {                                                 //判断为点击城市选择按键
            Intent intent = new Intent(this, SelectCity.class);                    //新建Intent交互通信
            intent.putExtra("nowCityName", cityTv.getText());                               //传递当前城市名
            // startActivity(intent);
            startActivityForResult(intent, 1);                                        //开启一个Activity并返回值
        }
    }

    /**
     * @param cityCode
     */
    private void queryWeatherCode(String cityCode) {                                                //根据cityCode得到XML天气数据
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;           //访问地址拼接
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {                                                               //异步请求数据
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);

                    todayWeather = parseXML(responseStr);                                           //解析XML天气数据
                    if (todayWeather != null) {                                                     //传递数据
                        Log.d("myWeather", todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata) {                                                 //解析XML
        TodayWeather todayWeather = null;                                                           //信息存入todayWeather
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();                          //解析XML
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {                                       //判断是否结束
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:                                              //判断当前事件是否为文档开始事件
                        break;
                    case XmlPullParser.START_TAG:                                                   //判断当前事件是否为标签元素开始事件
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();                                      //分配空间
                            break;
                        }
                        if (xmlPullParser.getName().equals("day")) break;
                        if (todayWeather != null) {
                            String xmlKey = xmlPullParser.getName();
                            xmlPullParser.next();
                            switch (xmlKey) {
                                case "city":
                                    todayWeather.setCity(xmlPullParser.getText());
                                    break;
                                case "updatetime":
                                    todayWeather.setUpdatetime(xmlPullParser.getText());
                                    break;
                                case "shidu":
                                    todayWeather.setShidu(xmlPullParser.getText());
                                    break;
                                case "wendu":
                                    todayWeather.setWendu(xmlPullParser.getText());
                                    break;
                                case "pm25":
                                    todayWeather.setPm25(xmlPullParser.getText());
                                    break;
                                case "quality":
                                    todayWeather.setQuality(xmlPullParser.getText());
                                    break;
                                case "fengxiang":
                                    if (fengxiangCount == 0) {
                                        todayWeather.setFengxiang(xmlPullParser.getText());
                                        fengxiangCount++;
                                    }
                                    break;
                                case "fengli":
                                    if (fengliCount == 0) {
                                        todayWeather.setFengli(xmlPullParser.getText());
                                        fengliCount++;
                                    }
                                    break;
                                case "date":
                                    if (dateCount == 0) {
                                        todayWeather.setDate(xmlPullParser.getText());
                                        dateCount++;
                                    }
                                    break;
                                case "high":
                                    if (highCount == 0) {
                                        todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                        highCount++;
                                    }
                                    break;
                                case "low":
                                    if (lowCount == 0) {
                                        todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                        lowCount++;
                                    }
                                    break;
                                case "type":
                                    if (typeCount == 0) {
                                        todayWeather.setType(xmlPullParser.getText());
                                        typeCount++;
                                    }
                                    break;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:                                                     // 判断当前事件是否为标签元素结束事件
                        break;
                }
                eventType = xmlPullParser.next();                                                   // 进入下一个元素并触发相应事件
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;                                                                        //返回解析后的todayWeather对象
    }

    private void updateTodayWeather(TodayWeather todayWeather) {                                    //根据todayWeather更新今日天气
        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        temperatureNowTv.setText("当前温度： " + todayWeather.getWendu() + "℃");
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText(todayWeather.getFengxiang() + ": " + todayWeather.getFengli());
        int[] imagePm25 = {                                                                         //pm2.5图标对应
                R.drawable.biz_plugin_weather_0_50,
                R.drawable.biz_plugin_weather_51_100,
                R.drawable.biz_plugin_weather_101_150,
                R.drawable.biz_plugin_weather_151_200,
                R.drawable.biz_plugin_weather_201_300,
                R.drawable.biz_plugin_weather_201_300,
                R.drawable.biz_plugin_weather_greater_300
        };
        //todayWeather.setPm25("500");
        if (todayWeather.getPm25() != null) {                                                       //判断是否有pm2.5信息
            int pmIndex = Integer.valueOf(todayWeather.getPm25());
            pmIndex = Math.min((pmIndex - 1) / 50, 6);
            pmImg.setImageDrawable(getResources().getDrawable(imagePm25[pmIndex]));                 //根据pm2.5值设置图标
        }

        //todayWeather.setType("哈哈哈");
        Map<String, Integer> imageWeather = new HashMap<String, Integer>() {                        //天气与图片对应
            {
                put("暴雪", R.drawable.biz_plugin_weather_baoxue);
                put("暴雨", R.drawable.biz_plugin_weather_baoyu);
                put("大暴雨", R.drawable.biz_plugin_weather_dabaoyu);
                put("大雪", R.drawable.biz_plugin_weather_daxue);
                put("大雨", R.drawable.biz_plugin_weather_dayu);
                put("多云", R.drawable.biz_plugin_weather_duoyun);
                put("雷阵雨", R.drawable.biz_plugin_weather_leizhenyu);
                put("雷阵雨冰雹", R.drawable.biz_plugin_weather_leizhenyubingbao);
                put("晴", R.drawable.biz_plugin_weather_qing);
                put("沙尘暴", R.drawable.biz_plugin_weather_shachenbao);
                put("特大暴雨", R.drawable.biz_plugin_weather_tedabaoyu);
                put("雾", R.drawable.biz_plugin_weather_wu);
                put("小雪", R.drawable.biz_plugin_weather_xiaoxue);
                put("小雨", R.drawable.biz_plugin_weather_xiaoyu);
                put("阴", R.drawable.biz_plugin_weather_yin);
                put("雨夹雪", R.drawable.biz_plugin_weather_yujiaxue);
                put("阵雪", R.drawable.biz_plugin_weather_zhenxue);
                put("阵雨", R.drawable.biz_plugin_weather_zhenyu);
                put("中雪", R.drawable.biz_plugin_weather_zhongxue);
                put("中雨", R.drawable.biz_plugin_weather_zhongyu);
            }
        };
        int weatherIndex = R.drawable.biz_plugin_weather_qing;                                      //设置默认天气图标
        try {
            weatherIndex = imageWeather.get(todayWeather.getType());                                //根据中文天气信息设置天气图标
        } catch (NullPointerException e) {
            Log.d("myWeather", "出现新的天气类型");
        }
        weatherImg.setImageDrawable(getResources().getDrawable(weatherIndex));                      //设置图标
        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {                 //记录新的城市
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            sharedPreferences.edit().putString("city_code", newCityCode).commit();
            Log.d("myWeather", "选择的城市代码为" + newCityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

}
