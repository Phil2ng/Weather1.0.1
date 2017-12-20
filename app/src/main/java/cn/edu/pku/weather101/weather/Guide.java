package cn.edu.pku.weather101.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.weather101.R;
import cn.edu.pku.weather101.ViewPagerAdapter;

public class Guide extends Activity implements ViewPager.OnPageChangeListener{
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.iv1, R.id.iv2, R.id.iv3};

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        initViews();
        initDots();
        btn = (Button)views.get(2).findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Guide.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void initDots(){
        dots = new ImageView[views.size()];
        for(int i=0;i<views.size();i++){
            dots[i] = (ImageView)findViewById(ids[i]);
        }
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.page1, null));
        views.add(inflater.inflate(R.layout.page2, null));
        views.add(inflater.inflate(R.layout.page3, null));
        viewPagerAdapter = new ViewPagerAdapter(views, this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int a = 0;a<ids.length;a++){
            if(a==position){
                dots[a].setImageResource(R.drawable.page_indicator_focus);
            }else{
                dots[a].setImageResource(R.drawable.page_indicator_unfocus);
                dots[a].setImageResource(R.drawable.page_indicator_unfocus);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
