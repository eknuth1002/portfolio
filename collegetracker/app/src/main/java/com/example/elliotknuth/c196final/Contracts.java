package com.example.elliotknuth.c196final;

import android.provider.BaseColumns;

public class Contracts
{

	private Contracts()
	{
	}

	public static final class TermEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "termsList";
		public static final String COLUMN_TERM = "term";
		public static final String COLUMN_START_DATE = "startDate";
		public static final String COLUMN_END_DATE = "endDate";
	}

	public static final class CourseEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "courseList";
		public static final String COLUMN_NUMBER = "number";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_TERM = "term";
		public static final String COLUMN_START_DATE = "startDate";
		public static final String COLUMN_END_DATE = "endDate";
		public static final String COLUMN_STATUS = "status";
		public static final String COLUMN_MENTOR_NAME = "mentorName";
		public static final String COLUMN_MENTOR_PHONE = "mentorPhoneNumber";
		public static final String COLUMN_MENTOR_EMAIL = "mentorEmail";
		public static final String COLUMN_PERFORMANCE_PREASSESSMENT = "performancePreassessment";
		public static final String COLUMN_PERFORMANCE_ASSESSMENT = "performanceAssessment";
		public static final String COLUMN_OBJECTIVE_PREASSESSMENT = "objectivePreassessment";
		public static final String COLUMN_OBJECTIVE_ASSESSMENT = "objectiveAssessment";
		public static final String COLUMN_NOTES = "notes";
	}

	public static final class AssessmentEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "assessments";
		public static final String COLUMN_COURSE = "course";
		public static final String ASSESSMENT_NAME = "assessmentName";
		public static final String COLUMN_NOTES = "notes";
		public static final String COLUMN_ASSESSMENT_TYPE = "assessmentType";
	}


	public static final class AlarmEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "alarmList";
		public static final String COLUMN_TYPE = "type";
		public static final String COLUMN_ITEM_ID = "item_id";
		public static final String COLUMN_DATE = "date";
		public static final String COLUMN_TIME = "time";

	}

	public static final class ACTIONS
	{
		public static final String ADD = "add";
		public static final String EDIT = "edit";
		public static final String VIEW = "view";

		public static final String action = "action";
	}

}
