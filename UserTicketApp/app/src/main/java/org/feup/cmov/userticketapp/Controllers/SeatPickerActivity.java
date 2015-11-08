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
import android.view.MenuItem;
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

    static List<PlaceholderFragment> fragments;

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

        fragments = new ArrayList<>();

        mViewPager = (ViewPager) findViewById(R.id.container_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            PlaceholderFragment fragment = PlaceholderFragment.newInstance(position + 1);

            fragments.add(position, fragment);

            return fragment;
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

    private static int calculateCurrent(int position) {

        if (position == 1)
            return 0;
        else
            return -(halfCarriageCapacity + carriageCapacity) + carriageCapacity * position;
    }

    private static int calculatePosition(int current) {

        if (current >= 0 && current < 10)
            return 1;

        return ((halfCarriageCapacity + carriageCapacity) + current) / carriageCapacity;
    }

    private static void updateFragments() {

        for (PlaceholderFragment fragment : fragments) {
            fragment.updateTextView();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_TITLE = "section_title";

        private Integer section;

        private TextView selectedSeatTextView;

        private GridView gridView, gridView2;
        private static Integer selectedPosition = -1;

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_SECTION_TITLE, (String) mSectionsPagerAdapter.getPageTitle(sectionNumber - 1));
            fragment.setArguments(args);
            return fragment;
        }

        public void updateGridViews(int position) {

            if (gridView != null && selectedPosition < gridView.getChildCount()) {

                ImageView imageView = (ImageView) gridView.getChildAt(selectedPosition);

                if ((int)imageView.getTag() == R.drawable.train_seat_picked) {

                    imageView.setImageResource(R.drawable.train_seat_normal);
                    imageView.setTag(R.drawable.train_seat_normal);

                } else if ((int)imageView.getTag() == R.drawable.train_seat_picked_180) {

                    imageView.setImageResource(R.drawable.train_seat_normal_180);
                    imageView.setTag(R.drawable.train_seat_normal_180);
                }

                if (gridView2 != null && selectedPosition < gridView2.getChildCount()) {

                    ImageView imageView2 = (ImageView) gridView2.getChildAt(selectedPosition);

                    if ((int)imageView2.getTag() == R.drawable.train_seat_picked) {

                        imageView2.setImageResource(R.drawable.train_seat_normal);
                        imageView2.setTag(R.drawable.train_seat_normal);

                    } else if ((int)imageView2.getTag() == R.drawable.train_seat_picked_180) {

                        imageView2.setImageResource(R.drawable.train_seat_normal_180);
                        imageView2.setTag(R.drawable.train_seat_normal_180);
                    }
                }

                selectedPosition = position;
            }
        }

        public void updateTextView() {
            selectedSeatTextView.setText(getResources().getString(R.string.selected_seat, (sharedData.getArraySeatNumber().get(ticketIndex)+1), calculatePosition(sharedData.getArraySeatNumber().get(ticketIndex))));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_seat_picker, container, false);

            TextView sectionLabel = (TextView) rootView.findViewById(R.id.section_label);
            sectionLabel.setText(getArguments().getString(ARG_SECTION_TITLE));

            section = getArguments().getInt(ARG_SECTION_NUMBER);

            TextView sectionSubLabel = (TextView) rootView.findViewById(R.id.section_sub_label);
            sectionSubLabel.setText(getString(R.string.carriage_format, section));

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

            if (section == (int)Math.ceil(((trainCapacity / (float)halfCarriageCapacity) - 1) / 2 + 1)) {
                nextButton.setVisibility(View.GONE);
            } else if (section == 1) {
                backButton.setVisibility(View.GONE);
            }

            selectedSeatTextView = (TextView) rootView.findViewById(R.id.section_seat);
            selectedSeatTextView.setText(getResources().getString(R.string.selected_seat, (sharedData.getArraySeatNumber().get(ticketIndex)+1), calculatePosition(sharedData.getArraySeatNumber().get(ticketIndex))));

            final Integer current = calculateCurrent(section);

            gridView = (GridView) rootView.findViewById(R.id.gridview);
            gridView.setAdapter(new ImageAdapter(rootView.getContext(), current));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    Integer seatPosition = (current + position);

                    ImageView imageView = (ImageView) v;

                    PlaceholderFragment fragment = fragments.get(calculatePosition(sharedData.getArraySeatNumber().get(ticketIndex))-1);

                    switch ((int)imageView.getTag()) {
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
                            imageView.setTag(R.drawable.train_seat_picked);

                            sharedData.getArraySeatNumber().set(ticketIndex, seatPosition);

                            fragment.updateGridViews(position);
                            updateFragments();
                            break;
                        case R.drawable.train_seat_normal_180:
                            imageView.setImageResource(R.drawable.train_seat_picked_180);
                            imageView.setTag(R.drawable.train_seat_picked_180);

                            sharedData.getArraySeatNumber().set(ticketIndex, seatPosition);

                            fragment.updateGridViews(position);
                            updateFragments();
                            break;
                    }
                }
            });

            if (section != 1 && ((current + halfCarriageCapacity) < trainCapacity)) {

                View line = rootView.findViewById(R.id.train_split_line);
                line.setVisibility(View.VISIBLE);

                gridView2 = (GridView) rootView.findViewById(R.id.gridview2);
                gridView2.setAdapter(new ImageAdapter(rootView.getContext(), current + halfCarriageCapacity));
                gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {

                        Integer seatPosition = (current + halfCarriageCapacity + position);

                        ImageView imageView = (ImageView) v;

                        PlaceholderFragment fragment = fragments.get(calculatePosition(sharedData.getArraySeatNumber().get(ticketIndex))-1);

                        switch ((int)imageView.getTag()) {
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
                                imageView.setTag(R.drawable.train_seat_picked);

                                sharedData.getArraySeatNumber().set(ticketIndex, seatPosition);

                                fragment.updateGridViews(position);
                                updateFragments();
                                break;
                            case R.drawable.train_seat_normal_180:
                                imageView.setImageResource(R.drawable.train_seat_picked_180);
                                imageView.setTag(R.drawable.train_seat_picked_180);

                                sharedData.getArraySeatNumber().set(ticketIndex, seatPosition);

                                fragment.updateGridViews(position);
                                updateFragments();
                                break;
                        }
                    }
                });
                gridView2.setVisibility(View.VISIBLE);
            }

            return rootView;
        }

        public class ImageAdapter extends BaseAdapter {

            private Context mContext;

            // references to our images
            private List<Integer> mThumbIds;

            private boolean seatsBoolean(int i) {
                return (i == 0 || i == 2 || i == 5 || i == 7);
            }

            public ImageAdapter(Context c, Integer current) {
                mContext = c;
                mThumbIds = new ArrayList<>();

                int index = 0;
                for (int i = current; i < (((current + halfCarriageCapacity) > trainCapacity) ? trainCapacity : (current + halfCarriageCapacity)); i++) {

                    if (sharedData.getArraySeatNumber().get(ticketIndex) == i) {
                        if (seatsBoolean(index)) {
                            mThumbIds.add(R.drawable.train_seat_picked);
                        } else {
                            mThumbIds.add(R.drawable.train_seat_picked_180);
                        }
                        index++;
                        selectedPosition = mThumbIds.size()-1;
                        continue;
                    }

                    if (sharedData.getFreeSeats().get(ticketIndex).contains(i)) {
                        if (seatsBoolean(index))
                            mThumbIds.add(R.drawable.train_seat_normal);
                        else
                            mThumbIds.add(R.drawable.train_seat_normal_180);
                    } else {
                        if (seatsBoolean(index))
                            mThumbIds.add(R.drawable.train_seat_disabled);
                        else
                            mThumbIds.add(R.drawable.train_seat_disabled_180);
                    }
                    index++;
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
                imageView.setTag(mThumbIds.get(position));
                return imageView;
            }
        }
    }
}
