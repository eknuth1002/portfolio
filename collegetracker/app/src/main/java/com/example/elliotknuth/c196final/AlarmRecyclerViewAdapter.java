package com.example.elliotknuth.c196final;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class AlarmRecyclerViewAdapter
		extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.AlarmViewHolder>
{

	private Context context;
	private ArrayList<DatabaseHandler.AlarmObject> cursor;

	private SQLiteDatabase database;


	public AlarmRecyclerViewAdapter(Context context, ArrayList<DatabaseHandler.AlarmObject> cursor)
	{
		this.context = context;
		this.cursor = cursor;
	}


	public class AlarmViewHolder extends RecyclerView.ViewHolder
	{
		public TextView alarmName;
		public ImageView alarmInfoButton;
		public ConstraintLayout alarmItem;
		public TextView alarmItemId;
		public TextView alarmItemInfo;


		public AlarmViewHolder(View itemView)
		{
			super(itemView);
			alarmName = itemView.findViewById(R.id.alarmListItemName);
			alarmInfoButton = itemView.findViewById(R.id.alarmInfoButton);
			alarmItem = itemView.findViewById(R.id.alarmItem);
			alarmItemId = itemView.findViewById(R.id.alarmItemId);
			alarmItemInfo = itemView.findViewById(R.id.alarmItemInfo);

		}
	}

	@Override
	public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.alarm_item, parent, false);

		return new AlarmViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final AlarmViewHolder holder, int position)
	{


		if (position == cursor.size())
		{
			return;
		}

		DatabaseHandler.AlarmObject alarm = cursor.get(position);

		//TODO set Time to go from 24hr to 12hr
		holder.alarmName.setText(alarm.getDate().concat("\n").concat(alarm.getTime()));
		holder.alarmItemId.setText(alarm.getId());
		//		ArrayList<String> alarmTypes = new ArrayList<>();
		//		for (String temp : context.getResources().getStringArray(R.array.alarm_types))
		//		{
		//			alarmTypes.add(temp);
		//		}

		if (alarm.getType().equals("Course"))
		{

			holder.alarmItemInfo.setText(
					DatabaseHandler.getInstance().getCourseById(alarm.getItemId()).getCourseName
							());
		}
		else if (alarm.getType().equals("Term"))
		{
			holder.alarmItemInfo.setText((DatabaseHandler.getInstance().getTermById(
					alarm.getItemId()).getTerm()));
		}
		else if (alarm.getType().equals("Assessment"))
		{
			holder.alarmItemInfo.setText(DatabaseHandler.getInstance().getAssessmentById(alarm.getItemId()).getAssessmentName());
		}

		if (position == cursor.size() - 1)
		{
			holder.alarmItem.setBackground(null);
		}

		holder.alarmItem.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), AlarmActivity.class);
				intent.putExtra(Contracts.AlarmEntry.COLUMN_DATE,
								holder.alarmName.getText().toString()
												.concat(holder.alarmItemInfo.getText().toString
														()));
				intent.putExtra(Contracts.AlarmEntry._ID, holder.alarmItemId.getText().toString());
				intent.putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.VIEW);

				v.getContext().startActivity(intent);
			}
		});

	}

	public void updateCursor(ArrayList<DatabaseHandler.AlarmObject> cursor)
	{

		if (cursor != null)
		{
			this.cursor = cursor;
		}

		notifyDataSetChanged();
	}

	@Override
	public int getItemCount()
	{
		return cursor.size();
	}
}


