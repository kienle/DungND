package com.greenwich.sherlock.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.greenwich.sherlock.R;
import com.greenwich.sherlock.entity.User;

public class SearchResultAdapter extends BaseAdapter {
	private List<User> mUsers;
	private LayoutInflater mInflater;

	public SearchResultAdapter(Context context, List<User> users) {
		this.mUsers = users;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setmUsers(List<User> mUsers) {
		this.mUsers = mUsers;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mUsers != null ? mUsers.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.search_result_item, null);

			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(mUsers.get(position).getUsername());

		return convertView;
	}

	static class ViewHolder {
		TextView name;
	}

}
