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

public class TermRecyclerViewAdapter extends RecyclerView.Adapter<TermRecyclerViewAdapter.TermViewHolder> {

    private Context context;
	private ArrayList<DatabaseHandler.TermObject> cursor;

    private SQLiteDatabase database;


	public TermRecyclerViewAdapter(Context context, ArrayList<DatabaseHandler.TermObject> cursor)
	{
        this.context = context;
        this.cursor = cursor;
    }


    public class TermViewHolder extends RecyclerView.ViewHolder {
        public TextView termName;
		public ImageView termInfo;
		public ConstraintLayout termItem;
		public TextView termItemId;


        public TermViewHolder(View itemView) {
            super(itemView);
            termName = itemView.findViewById(R.id.termListItemName);
			termInfo = itemView.findViewById(R.id.termInfoButton);
			termItem = itemView.findViewById(R.id.termItem);
			termItemId = itemView.findViewById(R.id.termItemId);

        }
    }

    @Override
    public TermViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.term_item, parent, false);

        return new TermViewHolder(view);
    }

    @Override
	public void onBindViewHolder(final TermViewHolder holder, int position)
	{


		if (position == cursor.size())
		{
            return;
        }

		holder.termName.setText(cursor.get(position).getTerm());
		holder.termItemId.setText(cursor.get(position).getId());
		if (position == cursor.size() - 1)
		{
			holder.termItem.setBackground(null);
		}

		holder.termItem.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), TermActivity.class);
				intent.putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.VIEW);
				intent.putExtra(Contracts.TermEntry._ID, holder.termItemId.getText().toString());
				v.getContext().startActivity(intent);
			}
		});

    }

    @Override
    public int getItemCount() {
		return cursor.size();
	}

	public void swapCursor(ArrayList cursor)
	{
		if (cursor != null)
		{
			this.cursor = cursor;

            notifyDataSetChanged();
        }
    }


}
