package com.alenor.filmdb.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alenor.filmdb.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagePreviewPager extends FragmentActivity {

    public static void start(Context context, ArrayList<String> images, @Nullable Integer position) {
        Intent i = new Intent(context, ImagePreviewPager.class);
        i.putStringArrayListExtra(EXTRA_IMAGES, images);
        i.putExtra(EXTRA_IMAGE_POSITION, position);
        context.startActivity(i);
    }

    private static final String EXTRA_IMAGES = "extra_images";
    private static final String EXTRA_IMAGE_POSITION = "extra_image_position";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview_pager_layout);

        ArrayList<String> images = getIntent().getStringArrayListExtra(EXTRA_IMAGES);
        int currentPosition = getIntent().getIntExtra(EXTRA_IMAGE_POSITION, -1);

        ImagePreviewAdapter imagePreviewAdapter =
                new ImagePreviewAdapter(getSupportFragmentManager(), images);
        ViewPager viewPager = (ViewPager) findViewById(R.id.image_preview_pager_layout_pager);
        viewPager.setAdapter(imagePreviewAdapter);
        viewPager.setCurrentItem(currentPosition + 1);

        TextView closeButton = (TextView) findViewById(R.id.image_preview_pager_layout_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public static class ImagePreviewAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> images;

        public ImagePreviewAdapter(FragmentManager fm, ArrayList<String> images) {
            super(fm);
            this.images = images;
        }

        @Override
        public Fragment getItem(int position) {
            ImageFragment imageFragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ImageFragment.IMAGE_PATH, images.get(position));
            imageFragment.setArguments(bundle);
            return imageFragment;
        }

        @Override
        public int getCount() {
            return images.size();
        }

    }

    public static class ImageFragment extends Fragment {
        public static final String IMAGE_PATH = "image_path";

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.image_preview_pager_image_fragment_list_item, container, false);
            Bundle args = getArguments();
            Picasso.with(getContext()).load(args.getString(IMAGE_PATH))
                    .placeholder(R.drawable.image_loading_placeholder)
                    .into((ImageView) rootView.findViewById(R.id.image_preview_pager_preview_image));
            return rootView;
        }
    }
}
