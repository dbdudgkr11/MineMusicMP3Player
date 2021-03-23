package com.example.minemusicmp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    private RecyclerView recyclerView;
    private RecyclerView recyclerLike;
    //데이터 베이스
    private MusicDBHelper musicDBHelper;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager linearLayoutManager_like;
    private MusicAdapter musicAdapter;
    private MusicAdapter musicAdapter_like;

    private Player player;

    private ArrayList<MusicData> musicDataArrayList = new ArrayList<>();

    private ArrayList<MusicData> musicLikeArrayList = new ArrayList<>();

    private ArrayList<MusicData> findMusicDataArrayList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //외부접근권한설정
        requestPermissionsFunc();

        //find id activity_main;
        findViewByIdFunc();

        //어뎁터 생성

        musicAdapter = new MusicAdapter(getApplicationContext());

        //리사이클러뷰에서 리니어 레이아웃메니저를 적용시켜야한다.
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setAdapter(musicAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        //ArrayList<MusicData>를 가져와서 musicAdapter를 적용시킨다.
        musicDataArrayList = musicDBHelper.compareArrayList();

        // 음악 DB 저장
        insertDB(musicDataArrayList);

        // 어댑터에 데이터 세팅
        recyclerViewListUpdate(musicDataArrayList);


        rePlaceFlag();
    }

    public void recyclerViewListUpdate(ArrayList<MusicData> musicDataArrayList) {
        // 어댑터에 데이터리스트 세팅
        musicAdapter.setMusicList(musicDataArrayList);

        // recyclerView에 어댑터 세팅
        recyclerView.setAdapter(musicAdapter);
        musicAdapter.notifyDataSetChanged();
    }

    public void insertDB(ArrayList<MusicData> musicDataArrayList) {
        boolean returnValue = musicDBHelper.insertMusicDataToDB(musicDataArrayList);

        if(returnValue){
            Toast.makeText(getApplicationContext(), "삽입 성공", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "삽입 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermissionsFunc() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MODE_PRIVATE);
    }

    public void rePlaceFlag() {
        player = new Player();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,player);
        ft.commit();

    }

    public void findViewByIdFunc() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerLike = (RecyclerView) findViewById(R.id.recyclerLike);
    }

    @Override
    protected void onStop() {
        super.onStop();

        boolean returnValue = musicDBHelper.updateMusicDataToDB(musicDataArrayList);

        if(returnValue){
            Toast.makeText(getApplicationContext(), "업뎃 성공", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "업뎃 실패", Toast.LENGTH_SHORT).show();
        }
    }
}