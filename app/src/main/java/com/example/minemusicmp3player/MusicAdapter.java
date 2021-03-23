package com.example.minemusicmp3player;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList<MusicData> musicList;


    private OnItemClickListener mListener = null;

    public MusicAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int position) {

        Bitmap albumImg = getAlbumImg(context, Long.parseLong(musicList.get(position).getAlbumArt()), 200);
        if(albumImg != null){
            customViewHolder.albumArt.setImageBitmap(albumImg);
        }


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        customViewHolder.title.setText(musicList.get(position).getTitle());
        customViewHolder.artist.setText(musicList.get(position).getArtist());
        customViewHolder.duration.setText(simpleDateFormat.format(Integer.parseInt(musicList.get(position).getDuration())));

    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }


    public Bitmap getAlbumImg(Context context, Long albumArt, int imgMaxSize){

        BitmapFactory.Options options = new BitmapFactory.Options();

        /*컨텐트 프로바이더(Content Provider)는 앱 간의 데이터 공유를 위해 사용됨.
        특정 앱이 다른 앱의 데이터를 직접 접근해서 사용할 수 없기 때문에
        무조건 컨텐트 프로바이더를 통해 다른 앱의 데이터를 사용해야만 한다.
        다른 앱의 데이터를 사용하고자 하는 앱에서는 URI를 이용하여 컨텐트 리졸버(Content Resolver)를 통해
        다른 앱의 컨텐트 프로바이더에게 데이터를 요청하게 되는데
        요청받은 컨텐트 프로바이더는 URI를 확인하고 내부에서 데이터를 꺼내어 컨텐트 리졸버에게 전달한다.
        */

        ContentResolver contentResolver = context.getContentResolver();


        Uri uri = Uri.parse("content://media/external/audio/albumart/" + albumArt);
        if (uri != null){
            ParcelFileDescriptor fd = null;
            try{
                fd = contentResolver.openFileDescriptor(uri, "r");

                options.inJustDecodeBounds = true;

                int scale = 0;
                if(options.outHeight > imgMaxSize || options.outWidth > imgMaxSize){
                    scale = (int)Math.pow(2,(int) Math.round(Math.log(imgMaxSize / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }
                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;

                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);

                if(bitmap != null){

                    if(options.outWidth != imgMaxSize || options.outHeight != imgMaxSize){
                        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, imgMaxSize, imgMaxSize, true);
                        bitmap.recycle();
                        bitmap = tmp;
                    }
                }
                return bitmap;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public interface OnItemClickListener
    {
        void onItemClick(View v, int pos);
    }


    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView albumArt;
        TextView title;
        TextView artist;
        TextView duration;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.albumArt = itemView.findViewById(R.id.d_ivAlbum);
            this.title = itemView.findViewById(R.id.d_tvTitle);
            this.artist = itemView.findViewById(R.id.d_tvArtist);
            this.duration = itemView.findViewById(R.id.d_tvDuration);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if( pos != RecyclerView.NO_POSITION){

                        mListener.onItemClick(view,pos);
                    }
                }
            });
        }

    }

    public void setMusicList(ArrayList<MusicData> musicList) {
        this.musicList = musicList;
    }
}
