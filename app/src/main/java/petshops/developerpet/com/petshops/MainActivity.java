package petshops.developerpet.com.petshops;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import petshops.developerpet.com.petshops.Activities.Notifications;
import petshops.developerpet.com.petshops.Auth_Activities.LoginActivity;
import petshops.developerpet.com.petshops.DataBase.SharedReferenceHelper;
import petshops.developerpet.com.petshops.Model.User;
import petshops.developerpet.com.petshops.Silder_Fragments.Categories_History;
import petshops.developerpet.com.petshops.Silder_Fragments.Contact_Us;
import petshops.developerpet.com.petshops.Silder_Fragments.My_Plan;
import petshops.developerpet.com.petshops.Silder_Fragments.Purchase_History;
import petshops.developerpet.com.petshops.Silder_Fragments.UserProfileFragment;
import petshops.developerpet.com.petshops.data.StaticConfig;
import petshops.developerpet.com.petshops.fragments.Adopation_Fragment;
import petshops.developerpet.com.petshops.fragments.BaseHomeFragment;
import petshops.developerpet.com.petshops.fragments.FriendsFragment;
import petshops.developerpet.com.petshops.fragments.Purchase_Fragment;
import petshops.developerpet.com.petshops.fragments.Sale_Fragment;
import petshops.developerpet.com.petshops.service.SensorService;
import petshops.developerpet.com.petshops.service.ServiceUtils;
import petshops.developerpet.com.petshops.util.ImageUtils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;



import static petshops.developerpet.com.petshops.fragments.Home_Fragment.LinearLayout_Search;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public static final int RequestPermissionCode = 1;
    private ViewPager viewPager;
    private TabLayout tabLayout = null;
    private ViewPagerAdapter adapter;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private Toolbar  toolbarSilderBar;
    public static Toolbar toolbar;

    LocationManager locationManager;
    FrameLayout frame_view ;
    boolean show_search_bar = false;

    Intent mServiceIntent;
    private SensorService mSensorService;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    TextView TitleBar;
    String[] TitleText = {
            "My Profile",
            "My Sales",
            "My Adoptions",
            "My Purchases",
            "My Categories ",
            "My Plan",
            "Terms Conditions",
            "Privacy Policy",
            "Contact Us",
            "About Us",
            "Inbox"
    };

    SharedReferenceHelper shar;
    CircleImageView imageProfile;


    Animation animFadeIn, animFabeOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animFabeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);


        ctx = this;
        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }

        shar = SharedReferenceHelper.getInstance(this);

        if(Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                //Toast.makeText(this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }

        // LanguageHelper.changeLocale(getResources(), SharedReferenceHelper.getInstance(this).getLanguage());

        initFirebase();
        viewPager = (ViewPager) findViewById(R.id.pager);
        frame_view = (FrameLayout) findViewById(R.id.frame);
        initTab();

        ((ImageView) findViewById(R.id.backToHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionsBarInfo(false);
            }
        });

        TitleBar = (TextView) findViewById(R.id.setTitleBar);

        checkGps();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarSilderBar = (Toolbar) findViewById(R.id.toolbarSilderBar);
        setSupportActionBar(toolbar);
        //setSupportActionBar(toolbarSilderBar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //navigationView.setNavigationItemSelectedListener(this);
        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        imageProfile = navHeader.findViewById(R.id.imageProfile);
        final TextView myname = navHeader.findViewById(R.id.myName);
        TextView myaddress = navHeader.findViewById(R.id.myAddress);
        LinearLayout myprofile = navHeader.findViewById(R.id.editProfile);
            FirebaseDatabase.getInstance().getReference("user").child(StaticConfig.UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        User userdata = dataSnapshot.getValue(User.class);
                        myname.setText(userdata.name);
                       // Glide.with(MainActivity.this).load(userdata.getImage()).into(imageProfile);
                        setImageAvatar(MainActivity.this, userdata.avata);

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        myaddress.setText(shar.getEmail());

        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new UserProfileFragment());
                TitleBar.setText(TitleText[0]);
                showActionsBarInfo(true);
            }
        });

        showActionsBarInfo(false);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // loadHomeFragment(b);
                Bundle data = new Bundle();//create bundle instance
                data.putString("key_value", "String to pass");//  purchase_history.setArguments(data); // put string to pass with a key value
                Purchase_History purchase_history = new Purchase_History();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                switch (item.getItemId()) {
                    case R.id.nav_sales:
                        data.putString("key_value", "sale");//put string to pass with a key value
                        //loadFragment(new Sales_History().setArguments(data));
                        purchase_history.setArguments(data);
                        fragmentTransaction.replace(R.id.frame, purchase_history);
                        fragmentTransaction.commitAllowingStateLoss();
                        TitleBar.setText(TitleText[1]);
                        showActionsBarInfo(true);
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_adoptions:
                        data.putString("key_value", "adopation");//put string to pass with a key value
                        //loadFragment(new Purchase_History().setArguments(data));
                        purchase_history.setArguments(data);
                        fragmentTransaction.replace(R.id.frame, purchase_history);
                        fragmentTransaction.commitAllowingStateLoss();
                        drawer.closeDrawers();
                        TitleBar.setText(TitleText[2]);
                        showActionsBarInfo(true);
                        break;
                  //  case R.id.nav_purchases:
                        //loadFragment(new Categories_History());
                        //TitleBar.setText(TitleText[3]);
                        //showActionsBarInfo(true);
                        //break;
                    case R.id.nav_categories:
                        loadFragment(new Categories_History());
                        TitleBar.setText(TitleText[4]);
                        showActionsBarInfo(true);
                        break;
                    case R.id.nav_plan:
                        loadFragment(new My_Plan());
                        TitleBar.setText(TitleText[5]);
                        showActionsBarInfo(true);
                        break;
                    case R.id.nav_terms_contions:
                        CopyReadAssets("terms_and_conditions_of_papp.pdf");

/*
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //intent.setDataAndType(uri, "application/msword");
                        Uri uri2 = null;
                        try {
                            uri2 = Uri.parse(String.valueOf(new File(String.valueOf(getAssets().open("terms_and_conditions_of_papp.pdf")))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        intent.setDataAndType(uri2, "application/pdf");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
*/
                        // loadFragment(new Terms_Conditions());
                        // TitleBar.setText(TitleText[6]);
                       // showActionsBarInfo(true);
                        break;
                    case R.id.nav_privacy_policy:
                        //loadFragment(new Privacy_Policy());
                       // TitleBar.setText(TitleText[7]);
                       // showActionsBarInfo(true);
                        CopyReadAssets("privacy.pdf");
                        break;
                    case R.id.nav_contact_us:
                        loadFragment(new Contact_Us());
                        TitleBar.setText(TitleText[8]);
                        showActionsBarInfo(true);
                        break;
                  //  case R.id.nav_about_us:
                        //loadFragment(new About_Us());
                        //TitleBar.setText(TitleText[9]);
                        //showActionsBarInfo(true);
                    //    break;
                    case R.id.nav_rate_us:
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(myAppLinkToMarket);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(MainActivity.this, " unable to find market app", Toast.LENGTH_LONG).show();
                        }
                        //loadFragment(new About_Us());
                        //TitleBar.setText(TitleText[9]);
                        //showActionsBarInfo(true);
                        break;
                    case R.id.nav_inbox_chat:
                        loadFragment(new FriendsFragment());
                        TitleBar.setText(TitleText[10]);
                        showActionsBarInfo(true);
                        break;
                    case R.id.nav_notifications:
                        startActivity(new Intent(MainActivity.this, Notifications.class));
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        //loadFragment(new FriendsFragment());
                        //TitleBar.setText(TitleText[11]);
                        //showActionsBarInfo(true);
                        break;
                }
                return false;
            }
        });

    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commitAllowingStateLoss();
        drawer.closeDrawers();

    }
    // finish drawer
    void checkGps() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //  showGPSDisabledAlertToUser();
            showSettingsAlert();
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setImageAvatar(Context context, String imgBase64){
        try {
            Resources res = getResources();
            Bitmap src;
            if (imgBase64.equals("default")) {
                src = BitmapFactory.decodeResource(res, R.drawable.default_avata);
            } else {
                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }
            imageProfile.setImageDrawable(ImageUtils.roundedImage(context, src));
        }catch (Exception e){
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.enable_gps);
        alertDialog.setMessage(R.string.enable_gps_info);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null){
             //       StaticConfig.UID = user.getUid();
                } else {
                    MainActivity.this.finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }
        };
    }

    private void CopyReadAssets(String pathFile) {
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), pathFile);
        try {
            in = assetManager.open(pathFile);
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        }catch (Exception e) {}
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + getFilesDir() + "/"+pathFile), "application/pdf");
        startActivity(intent);
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {out.write(buffer, 0, read);}
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // showActionsBarInfo(false);
        super.onBackPressed();
    }

    void showActionsBarInfo(boolean showing){
        if(showing) {
            toolbarSilderBar.setVisibility(View.VISIBLE);
            frame_view.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
        }else{
            viewPager.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            frame_view.setVisibility(View.GONE);
            toolbarSilderBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        ServiceUtils.stopServiceFriendChat(getApplicationContext(), false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        ServiceUtils.startServiceFriendChat(getApplicationContext());
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search){
            if(show_search_bar){
                LinearLayout_Search.setVisibility(View.VISIBLE);
                show_search_bar = false;
            }else{
                LinearLayout_Search.setVisibility(View.GONE);
                show_search_bar = true;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // tabs
    private void initTab() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons(0);
    }

    private void setupTabIcons(int p) {
        final int[] tabIcons = {
                R.mipmap.home,
                R.mipmap.sale,
                R.mipmap.pay,
                R.mipmap.hum_dog,
                R.mipmap.ic_email,
                //R.mipmap.history
        };
        final int[] tabIconsSelected = {
                R.mipmap.home,
                R.mipmap.sale,
                R.mipmap.pay,
                R.mipmap.hum_dog,
                R.mipmap.ic_email,
               // R.mipmap.history
        };

        for(int a = 0 ; a < 5;a++){
            if(p == a){
                tabLayout.getTabAt(a).setIcon(tabIconsSelected[a]).setCustomView(R.layout.costomtab);
            }else{
                tabLayout.getTabAt(a).setIcon(tabIcons[a]);//.setCustomView(R.layout.costomtab);
            }
        }
        // tabLayout.getTabAt(0).setIcon(tabIcons[0]).setCustomView(R.layout.costomtab);
    }

    private void setupViewPager(final ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new BaseHomeFragment(), "HOME");
        adapter.addFrag(new Sale_Fragment(), "SALE");
        adapter.addFrag(new Purchase_Fragment(), "PAY");
        // adapter.addFrag(new Products_Fragment(), "SellProducts");
        adapter.addFrag(new Adopation_Fragment(), "DOGS");
        adapter.addFrag(new FriendsFragment(),"FRIEND");

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                tabLayout.setupWithViewPager(viewPager);
                setupTabIcons(position);
                String[] titles = {"Home","Add Sale","Buy", "Adoptions", "My Inbox"};
                showActionsBarInfo(false);
                toolbar.setTitle(titles[position]);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    void msg(String text){
        Toast.makeText(getApplicationContext(), text , Toast.LENGTH_LONG).show();
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            // return null to display only the icon
            return null;
        }
    }
    // set permissions
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE,
                READ_PHONE_STATE,
                CAMERA,
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
        }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean WriteDataPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadDataPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean GpsPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean CameraPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean Gps2Permission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStatusPermission = grantResults[5] == PackageManager.PERMISSION_GRANTED;

                    if (WriteDataPermission &&
                            ReadDataPermission &&
                            GpsPermission &&
                            CameraPermission &&
                            Gps2Permission &&
                            ReadStatusPermission
                            ) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(),ACCESS_COARSE_LOCATION );
        int FourPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE );
        int FivePermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int SixPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FivePermissionResult == PackageManager.PERMISSION_GRANTED &&
                SixPermissionResult == PackageManager.PERMISSION_GRANTED ;

    }
    public static class FileOpen {

        public static void openFile(Context context, File url) throws IOException {
            // Create URI
            File file=url;
            Uri uri = Uri.fromFile(file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if(url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if(url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if(url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if(url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
