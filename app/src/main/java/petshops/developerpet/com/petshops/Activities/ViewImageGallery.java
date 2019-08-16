package petshops.developerpet.com.petshops.Activities;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import petshops.developerpet.com.petshops.R;


public class ViewImageGallery extends AppCompatActivity {

    public static List<String> imageIDs = new ArrayList<>();
    Gallery gallery;
    ImageView imageView;
    ImageSwitcher imageSwitcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_gallery);

        gallery = (Gallery) findViewById(R.id.gallery1);
        imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(this).load(imageIDs.get(0)).into(imageView);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, final int position, long id) {
                //Toast.makeText(getBaseContext(), "pic" + (position + 1) + "selected", Toast.LENGTH_SHORT).show();
                Glide.with(ViewImageGallery.this).load(imageIDs.get(position)).into(imageView);

            }
            });
        ((ImageView)findViewById(R.id.backToHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_down, R.anim.slide_down);
                finish();
            }
        });


    }


    class ImageAdapter extends BaseAdapter {
        Context context;
        int itemBackground;
        public ImageAdapter(Context c) {
            context = c;
            //---setting the style---
            TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            itemBackground = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();
        }
        //---returns the number of images---
        public int getCount() {
            return imageIDs.size();
        }
        //---returns the item---
        public Object getItem(int position) {
            return position;
        }
        //---returns the ID of an item---
        public long getItemId(int position) {
            return position;
        }
        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(ViewImageGallery.this);
                Glide.with(ViewImageGallery.this)
                        .load(imageIDs.get(position))
                        .into(imageView);
                // imageView.setImageResource(imageIDs[position]);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new Gallery.LayoutParams(200, 200));
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
    }

}