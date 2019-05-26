package com.example.elliotknuth.c196final;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class AssessmentRecyclerViewAdapter extends RecyclerView.Adapter<AssessmentRecyclerViewAdapter.AssessmentViewHolder> {


    private Context context;
    private ArrayList<DatabaseHandler.AssessmentObject> cursor;

    public AssessmentRecyclerViewAdapter(Context context, ArrayList<DatabaseHandler.AssessmentObject> cursor)
    {
        this.context = context;
        this.cursor = cursor;
    }


    public class AssessmentViewHolder extends RecyclerView.ViewHolder {
        public final TextView assessmentName;
        public final ImageView assessmentItemDeleteButton;
       // public final RecyclerView addCourseAssessmentRV;
        public final View assessmentItemHolder;
        public TextView assessmentId;

        public AssessmentViewHolder(View itemView) {
            super(itemView);

            assessmentName = itemView.findViewById(R.id.assessmentItemName);
            assessmentItemDeleteButton = itemView.findViewById(R.id.imageAssessmentInfoIcon);
            assessmentItemHolder = itemView.findViewById(R.id.assessmentItemHolder);
            assessmentId = itemView.findViewById(R.id.assessmentItemId);
        }
    }

    @Override
    public AssessmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.assessment_item, parent, false);

        return new AssessmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AssessmentViewHolder holder, int position)
    {
        if (position == cursor.size())
        {
            return;
        }

        holder.assessmentName.setText("Course " + cursor.get(position).getCourseNumber() + " - " + cursor.get(position).getAssessmentName() + "\n\n" +
                                      cursor.get(position).getAssessmentType().substring(0, cursor.get(position).getAssessmentType().indexOf(" ")) +
                                      "\n" +
                                      cursor.get(position).getAssessmentType().substring(cursor.get(position).getAssessmentType().indexOf(" ") + 1));
        holder.assessmentId.setText(cursor.get(position).getId());

        if (position == cursor.size() - 1)
        {
            holder.assessmentItemHolder.setBackground(null);
        }


        holder.assessmentItemHolder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), AssessmentActivity.class);
                intent.putExtra(Contracts.AssessmentEntry._ID, holder.assessmentId.getText().toString());
                intent.putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.VIEW);
                v.getContext().startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return cursor.size();
    }

    public void swapCursor(ArrayList<DatabaseHandler.AssessmentObject> cursor)
    {
        if (cursor != null) {
            this.cursor = cursor;

            notifyDataSetChanged();
        }
    }
}
