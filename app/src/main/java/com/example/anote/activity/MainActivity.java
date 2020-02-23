package com.example.anote.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.anote.R;
import com.example.anote.adapter.item.MemoAdapter;
import com.example.anote.adapter.item.item.Memo;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button mBtnMenu;
    Button mBtnSearch;
    Button mBtnNewMemo;
    RecyclerView recyclerView;

    Realm realm;
    ArrayList<Memo> memoList = new ArrayList<>();
    Memo memo;
    MemoAdapter memoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionCheck();

        mBtnMenu = findViewById(R.id.btnMenu);
        mBtnSearch = findViewById(R.id.btnSearch);
        mBtnNewMemo = findViewById(R.id.btnNewMemo);

        mBtnNewMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewMemoActivity.class);
                intent.putExtra("requestCode","1");
                startActivityForResult(intent, 1);
            }
        });

        recyclerView = findViewById(R.id.memoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        RealmResults<Memo> realmResults = realm.where(Memo.class)
                .findAllAsync();

        for(Memo memo : realmResults) {
            memoList.add(memo);
        }
        memoAdapter = new MemoAdapter(MainActivity.this, memoList);
        recyclerView.setAdapter(memoAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // create
        if(resultCode == 1){
            final String id = "id" + memoList.size();
            final String date = data.getStringExtra("date");
            final String title = data.getStringExtra("title");
            final String textContents = data.getStringExtra("textContents"); // + image
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    memo = realm.createObject(Memo.class);
                    memo.setId(id);
                    memo.setDate(date);
                    memo.setTitle(title);
                    memo.setTextContents(textContents);
                }
            });
            memoList.add(new Memo(id, title, textContents, textContents, date)); // constructor
            memoAdapter = new MemoAdapter(MainActivity.this, memoList);
            recyclerView.setAdapter(memoAdapter);
        }
        // update
        else if(resultCode == 2){
            final String id = data.getStringExtra("id");
            final String date = data.getStringExtra("date");
            final String title = data.getStringExtra("title");
            final String textContents = data.getStringExtra("textContents"); // + image
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Memo memo = realm.where(Memo.class).equalTo("id", id).findFirst();
                    memo.setId(id);
                    memo.setDate(date);
                    memo.setTitle(title);
                    memo.setTextContents(textContents);
                }
            });
            memoAdapter.notifyDataSetChanged();
        }
    }

    public void permissionCheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> arrayPermission = new ArrayList<>();

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                arrayPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                arrayPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (arrayPermission.size() > 0) {
                String[] strArray = new String[arrayPermission.size()];
                strArray = arrayPermission.toArray(strArray);
                ActivityCompat.requestPermissions(this, strArray, 1000);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults.length < 1) {
                    Toast.makeText(this, "Failed get permission", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission is denied : " + permissions[i], Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(this, "Permission is granted", Toast.LENGTH_SHORT).show();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
