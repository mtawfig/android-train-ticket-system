package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.R;

import java.util.ArrayList;
import java.util.List;

public class SeatPickerActivity extends AppCompatActivity {

    static SectionsPagerAdapter mSectionsPagerAdapter;

    static ViewPager mViewPager;

    static Integer ticketIndex;

    static SharedDataSingleton sharedData = SharedDataSingleton.getInstance();

    static Integer trainCapacity;
    static Integer carriageCapacity;
    static Integer halfCarriageCapacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_picker);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ticketIndex = getIntent().getExtras().getInt("ticketIndex");
        trainCapacity = sharedData.getTrainCapacity().get(ticketIndex);
        carriageCapacity = 20;
        halfCarriageCapacity = carriageCapacity/2;

        // Set up the ViewPager with the sections adapter.
        /*
      The {@link ViewPager} that will host the section contents.
     */
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return (int)Math.ceil(((trainCapacity / (float)halfCarriageCapacity) - 1) / 2 + 1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Confort - 1st Class";
            } else {
                return "Tourist - 2nd Class";
            }
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_TITLE = "section_title";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_SECTION_TITLE, (String) mSectionsPagerAdapter.getPageTitle(sectionNumber - 1));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_seat_picker, container, false);
            TextView sectionLabel = (TextView) rootView.findViewById(R.id.section_label);
            sectionLabel.setText(getArguments().getString(ARG_SECTION_TITLE));

            TextView sectionSubLabel = (TextView) rootView.findViewById(R.id.section_sub_label);
            sectionSubLabel.setText(getString(R.string.carriage_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            Button nextButton = (Button) rootView.findViewById(R.id.next_carriage_button);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            });

            Button backButton = (Button) rootView.findViewById(R.id.back_carriage_button);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                }
            });

            if (getArguments().getInt(ARG_SECTION_NUMBER) == (int)Math.ceil(((trainCapacity / (float)halfCarriageCapacity) - 1) / 2 + 1)) {
                nextButton.setVisibility(View.GONE);
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                backButton.setVisibility(View.GONE);
            }

            final Integer current = calculateCurrent(getArguments().getInt(ARG_SECTION_NUMBER));

            GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(rootView.getContext(), current));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    Integer image = (Integer) parent.getItemAtPosition(position);

                    Integer seatPosition = (current + position);

                    ImageView imageView = (ImageView) v;
                    Integer currentItem = mViewPager.getCurrentItem();

                    switch (image) {
                        case R.drawable.train_seat_disabled:
                            Toast.makeText(rootView.getContext(), "The seat is already taken",
                                    Toast.LENGTH_SHORT).show();
                            break;

                        case R.drawable.train_seat_disabled_180:
                            Toast.makeText(rootView.getContext(), "The seat is already taken",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case R.drawable.train_seat_normal:
                            Toast.makeText(rootView.getContext(), "The seat " + seatPosition + " is free",
                                    Toast.LENGTH_SHORT).show();
                            imageView.setImageResource(R.drawable.train_seat_picked);
                            sharedData.getArraySeatNumber().set(ticketIndex, seatPosition);

                            break;
                        case R.drawable.train_seat_normal_180:
                            Toast.makeText(rootView.getContext(), "The seat " + seatPosition + " is free",
                                    Toast.LENGTH_SHORT).show();
                            imageView.setImageResource(R.drawable.train_seat_picked_180);
                            sharedData.getArraySeatNumber().set(ticketIndex, seatPosition);

                            break;
                    }
                }
            });

            if (getArguments().getInt(ARG_SECTION_NUMBER) != 1 && ((current + halfCarriageCapacity) < trainCapacity)) {

                View line = rootView.findViewById(R.id.train_split_line);
                line.setVisibility(View.VISIBLE);

                GridView gridview2 = (GridView) rootView.findViewById(R.id.gridview2);
                gridview2.setAdapter(new ImageAdapter(rootView.getContext(), current + halfCarriageCapacity));
                gridview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {

                        Integer image = (Integer) parent.getItemAtPosition(position);

                        Integer seatPosition = (current + halfCarriageCapacity + position);

                        ImageView imageView = (ImageView) v;

                        switch (image) {
                            case R.drawable.train_seat_disabled:
                                Toast.makeText(rootView.getContext(), "The seat is already taken",
                                        Toast.LENGTH_SHORT).show();
                                break;

                            case R.drawable.train_seat_disabled_180:
                                Toast.makeText(rootView.getContext(), "The seat is already taken",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case R.drawable.train_seat_normal:
                                imageView.setImageResource(R.drawable.train_seat_picked);

                                sharedData.getArraySeatNumber().set(ticketIndex, seatPosition);
                                break;
                            case R.drawable.train_seat_normal_180:
                                imageView.setImageResource(R.drawable.train_seat_picked_180);
                                sharedData.getArraySeatNumber().set(ticketIndex, seatPosition);
                                break;
                        }
                    }
                });
                gridview2.setVisibility(View.VISIBLE);
            }

            return rootView;
        }

        public int calculateCurrent(int position) {

            if (position == 1)
                return 0;
            else
                return -(halfCarriageCapacity + carriageCapacity) + carriageCapacity * position;
        }

        public class ImageAdapter extends BaseAdapter {

            private Context mContext;

            // references to our images
            private List<Integer> mThumbIds;

            public ImageAdapter(Context c, Integer current) {
                mContext = c;
                mThumbIds = new ArrayList<>();

                for (int i = current; i < (((current + halfCarriageCapacity) > trainCapacity) ? trainCapacity : (current + halfCarriageCapacity)); i++) {

                    if (sharedData.getArraySeatNumber().get(ticketIndex) == i) {
                        if (i % 2 == 0) {
                            mThumbIds.add(R.drawable.train_seat_picked);
                        } else {
                            mThumbIds.add(R.drawable.train_seat_picked_180);
                        }
                        continue;
                    }

                    if (sharedData.getFreeSeats().get(ticketIndex).contains(i)) {
                        if (i % 2 == 0)
                            mThumbIds.add(R.drawable.train_seat_normal);
                        else
                            mThumbIds.add(R.drawable.train_seat_normal_180);
                    } else {
                        if (i % 2 == 0)
                            mThumbIds.add(R.drawable.train_seat_disabled);
                        else
                            mThumbIds.add(R.drawable.train_seat_disabled_180);
                    }
                }
            }

            public int getCount() {
                return mThumbIds.size();
            }

            public Object getItem(int position) {
                return mThumbIds.get(position);
            }

            public long getItemId(int position) {
                return 0;
            }

            // create a new ImageView for each item referenced by the Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView;
                if (convertView == null) {
                    // if it's not recycled, initialize some attributes
                    imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new GridView.LayoutParams(140, 140));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setPadding(4, 4, 4, 4);
                } else {
                    imageView = (ImageView) convertView;
                }

                imageView.setImageResource(mThumbIds.get(position));
                return imageView;
            }
        }
    }
}
