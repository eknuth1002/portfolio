package com.example.elliotknuth.c196final;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class CourseRecyclerViewAdapter extends RecyclerView.Adapter<CourseRecyclerViewAdapter.CourseViewHolder> {

    private Context context;
	private ArrayList<DatabaseHandler.CourseObject> cursor;


	public CourseRecyclerViewAdapter(Context context, ArrayList<DatabaseHandler.CourseObject> cursor)
	{
        this.context = context;
        this.cursor = cursor;
    }


    public class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView courseName;
        public ImageView courseInfo;
		public ConstraintLayout courseItem;
		public TextView courseItemId;

        public CourseViewHolder(View itemView) {
            super(itemView);

            courseName = itemView.findViewById(R.id.courseListItemName);
            courseInfo = itemView.findViewById(R.id.courseInfoButton);
			courseItem = itemView.findViewById(R.id.courseItem);
			courseItemId = itemView.findViewById(R.id.courseItemId);
		}
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_item, parent, false);

        return new CourseViewHolder(view);
    }

    @Override
	public void onBindViewHolder(final CourseViewHolder holder, int position)
	{
		if (position == cursor.size())
		{
            return;
        }

		holder.courseName.setText(cursor.get(position).getCourseNumber().concat(" - \n").concat(cursor.get(position).getCourseName()));
		holder.courseItemId.setText(cursor.get(position).getId());
		if (position == cursor.size() - 1)
		{
			holder.courseItem.setBackground(null);
		}
		holder.courseItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
				Intent intent = new Intent(v.getContext(), CourseActivity.class);
				intent.putExtra(Contracts.CourseEntry._ID,
								holder.courseItemId.getText().toString());
				intent.putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.VIEW);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
		return cursor.size();
	}

	public void swapCursor(ArrayList<DatabaseHandler.CourseObject> cursor)
	{
		if (cursor != null)
		{
			this.cursor = cursor;
			notifyDataSetChanged();
		}
    }
}
