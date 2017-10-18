package cn.edu.pku.weather101;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
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
