package com.xiaotian.filebrowser;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.xiaotian.filebrowser.ui.fragment.FileFragment;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        FileFragment fragment = FileFragment.newInstance("文件");
        tx.add(R.id.framlayout, fragment,"ONE");
        tx.commit();

    }
}
