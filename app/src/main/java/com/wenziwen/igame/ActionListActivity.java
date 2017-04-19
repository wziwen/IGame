package com.wenziwen.igame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.wenziwen.igame.bean.ActionBean;
import com.wenziwen.igame.image.CropImageActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ActionListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ADD_ACTION = 100;
    private static final String TAG = "ActionListActivity";

    RecyclerView recyclerView;
    ActionAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_list);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActionAdapter(this);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);


        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(getFilesDir().getAbsolutePath() + "/cache"));
            adapter.addAll((ArrayList<ActionBean>) inputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                CropImageActivity.show(this, REQUEST_ADD_ACTION);
                break;
            case R.id.btn_save:
                break;
            case R.id.btn_start:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (RESULT_OK == resultCode) {
            ActionBean actionBean = (ActionBean) data.getSerializableExtra("action");
            adapter.add(actionBean);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(getFilesDir().getAbsolutePath() + "/cache"));
            outputStream.writeObject(adapter.getDataList());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
