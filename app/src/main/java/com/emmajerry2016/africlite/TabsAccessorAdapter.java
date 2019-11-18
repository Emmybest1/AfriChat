package com.emmajerry2016.africlite;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            case 1:
                ContactFragment contactFragment = new ContactFragment();
                return contactFragment;

            case 2:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;

            default:
                return null;
        }
    }

    public int getCount(){
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:

                return "chats";

            case 1:
                return "friends";

            case 2:
                return "notifications";

            default:
                return null;
        }
    }
}
