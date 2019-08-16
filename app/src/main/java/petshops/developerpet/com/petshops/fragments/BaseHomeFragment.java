package petshops.developerpet.com.petshops.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import petshops.developerpet.com.petshops.R;


public class BaseHomeFragment extends Fragment {
    public BaseHomeFragment() {
        // Required empty public constructor
    }
    private ViewPager viewPager;
    private TabLayout tabLayout = null;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_base_home, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorIndivateTab));
        adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFrag(new Home_Fragment() , "Pets Map");
        adapter.addFrag(new ListPets_fragment(), "Pets List");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Map");
        tabLayout.getTabAt(1).setText("List");

        return rootView;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {super(manager);}
        @Override
        public Fragment getItem(int position) {return mFragmentList.get(position);}
        @Override
        public int getCount() {return mFragmentList.size();}
        public void addFrag(Fragment fragment, String title) {mFragmentList.add(fragment);mFragmentTitleList.add(title);}
        @Override
        public CharSequence getPageTitle(int position) {
            // return null to display only the icon
            return null;
        }
    }


}
