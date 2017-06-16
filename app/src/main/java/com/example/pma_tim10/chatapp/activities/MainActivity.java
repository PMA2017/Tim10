package com.example.pma_tim10.chatapp.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.fragments.ConversationsTabFragment;
import com.example.pma_tim10.chatapp.fragments.FriendsTabFragment;
import com.example.pma_tim10.chatapp.fragments.PeopleTabFragment;
import com.example.pma_tim10.chatapp.receivers.NetworkConnectivityReceiver;
import com.example.pma_tim10.chatapp.service.IUserService;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.example.pma_tim10.chatapp.utils.SharedPrefUtil;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private NetworkConnectivityReceiver networkConnectivityReceiver;
    private IUserService userService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams)toolbar.getLayoutParams();
        params.setScrollFlags(0);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        networkConnectivityReceiver = new NetworkConnectivityReceiver();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkConnectivityReceiver,intentFilter);

        // set this user online
        userService = new UserService();
        userService.setOnline();
        userService.setFcmToken(new SharedPrefUtil(getApplicationContext()).getString(Constants.USER_FCM_TOKEN_FIELD));

        setTitle("Chat TIM 10");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                goToSettingsActivity();
            case R.id.action_update_profile:
                goToUserDetailsActivity();
                break;
            case R.id.action_sign_out:
                signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToUserDetailsActivity(){
        Intent intent = new Intent(this,UserDetailsActivity.class);
        intent.putExtra(Constants.IE_USER_ID_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
        startActivity(intent);
        finish();
    }

    private void goToSettingsActivity() {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void signOut() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser().getProviders().contains(Constants.FACEBOOK_PROVIDER_ID));
            LoginManager.getInstance().logOut();
        userService.setOffline();
        userService.setFcmToken(null);
        auth.signOut();
        goToLoginActivity();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this,EmailPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // on app exit
        userService.setOffline();
        finish();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkConnectivityReceiver);
        super.onDestroy();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    PeopleTabFragment peopleTab = new PeopleTabFragment();
                    return  peopleTab;
                case 1:
                    FriendsTabFragment friendsTab = new FriendsTabFragment();
                    return friendsTab;
                case 2:
                    ConversationsTabFragment messagesTab = new ConversationsTabFragment();
                    return messagesTab;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "People";
                case 1:
                    return "Friends";
                case 2:
                    return "Messages";
            }
            return null;
        }
    }
}
