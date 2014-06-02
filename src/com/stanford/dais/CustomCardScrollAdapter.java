package com.stanford.dais; 

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.glass.widget.CardScrollAdapter;

class CustomCardScrollAdapter extends CardScrollAdapter {

    private List<View> mViews;

    public CustomCardScrollAdapter(List<View> views) {
        mViews = views;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mViews.get(position);
    }

	@Override
	public int getCount() {
		return mViews.size(); 
	}

	@Override
	public Object getItem(int index) {
		if (index < mViews.size()) {
			return mViews.get(index); 
		} else {
			return null; 
		}
	}

	@Override
	public int getPosition(Object view) {
		return mViews.indexOf(view); 
	}
}