package com.greenwich.sherlock.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.greenwich.sherlock.R;
import com.greenwich.sherlock.entity.UserLocation;

public class LocationHistoryAdapter extends BaseAdapter {

	private List<UserLocation> mUserLocations;
	private LayoutInflater mInflater;

	public LocationHistoryAdapter(Context context, List<UserLocation> locations) {
		this.mUserLocations = locations;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mUserLocations != null ? mUserLocations.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mUserLocations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.location_history_item,
					null);

			holder = new ViewHolder();
			holder.time = (TextView) convertView.findViewById(R.id.tvTime);
			holder.address = (TextView) convertView
					.findViewById(R.id.tvAddress);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.time.setText(mUserLocations.get(position).getTime());
		holder.address.setText(mUserLocations.get(position).getAddress());

		return convertView;
	}

	static class ViewHolder {
		TextView time;
		TextView address;
	}

}
