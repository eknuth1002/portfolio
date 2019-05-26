package com.example.elliotknuth.c196final;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class DatabaseHandler
{
	private static DatabaseHandler handlerInstance = null;

	private Cursor termsCursor = null;
	private Cursor coursesCursor = null;
	private Cursor assessmentCursor = null;
	private Cursor alarmsCursor = null;
	private Context context;
	private DBOpenHelper helper;
	private SQLiteDatabase database;
	private ArrayList<TermObject> termsArray = new ArrayList<>();
	private ArrayList<CourseObject> coursesArray = new ArrayList<>();
	private ArrayList<AssessmentObject> assessmentArray = new ArrayList<>();
	private ArrayList<AlarmObject> alarmsArray = new ArrayList<>();
	final static Comparator termsComparator = new Comparator<TermObject>()
	{
		@Override
		public int compare(TermObject o1, TermObject o2)
		{
			String o1LowerCase = o1.getTerm().toLowerCase();
			String o2Lowercase = o2.getTerm().toLowerCase();

			for (int i = 0 ; ; i++)
			{

				if (i == o1LowerCase.length() && i == o2Lowercase.length())
				{
					return 0;
				}
				if (i == o1LowerCase.length() && i != o2Lowercase.length())
				{
					return 1;
				}
				if (i != o1LowerCase.length() && i == o2Lowercase.length())
				{
					return -1;
				}
				if (o1LowerCase.charAt(i) > o2Lowercase.charAt(i))
				{
					return 1;
				}
				if (o1LowerCase.charAt(i) < o2Lowercase.charAt(i))
				{
					return -1;
				}

			}
		}
	};
	final static Comparator coursesComparator = new Comparator<CourseObject>()
	{
		@Override
		public int compare(CourseObject o1, CourseObject o2)
		{
			String o1LowerCase = o1.getCourseNumber().toLowerCase();
			String o2Lowercase = o2.getCourseNumber().toLowerCase();

			for (int i = 0 ; ; i++)
			{

				if (i == o1LowerCase.length() && i == o2Lowercase.length())
				{
					return 0;
				}
				if (i == o1LowerCase.length() && i != o2Lowercase.length())
				{
					return 1;
				}
				if (i != o1LowerCase.length() && i == o2Lowercase.length())
				{
					return -1;
				}
				if (o1LowerCase.charAt(i) > o2Lowercase.charAt(i))
				{
					return 1;
				}
				if (o1LowerCase.charAt(i) < o2Lowercase.charAt(i))
				{
					return -1;
				}

			}
		}
	};
	final static Comparator assessmentsComparator = new Comparator<AssessmentObject>()
	{
		@Override
		public int compare(AssessmentObject o1, AssessmentObject o2)
		{
			String o1LowerCase = o1.getCourseNumber().toLowerCase();
			String o2Lowercase = o2.getCourseNumber().toLowerCase();

			for (int i = 0 ; ; i++)
			{
				//if both courses are the same, go into assessment type check
				if (i == o1LowerCase.length() && i == o2Lowercase.length())
				{
					//o1 is smaller if it is Performance Pre-Assessment and o2 is not
					if (o1.assessmentType.equals(assessmentTypes.PERFORMANCE_PREASSESSMENT.toString()) &&
						!o2.assessmentType.equals(assessmentTypes.PERFORMANCE_PREASSESSMENT.toString()))
					{
						return -1;
					}

					//o2 is smaller if it is Performance Pre-Assessment and o1 is not
					if (!o1.assessmentType.equals(assessmentTypes.PERFORMANCE_PREASSESSMENT.toString()) &&
						o2.assessmentType.equals(assessmentTypes.PERFORMANCE_PREASSESSMENT.toString()))
					{
						return 1;
					}

					//Checks name if both are Performance Pre-assessments
					if (o1.assessmentType.equals(assessmentTypes.PERFORMANCE_PREASSESSMENT.toString()) &&
						o2.assessmentType.equals(assessmentTypes.PERFORMANCE_PREASSESSMENT.toString()))
					{
						String o1NameLowerCase = o1.getAssessmentName().toLowerCase();
						String o2NameLowercase = o2.getAssessmentName().toLowerCase();

						for (int j = 0 ; ; j++)
						{

							if (j == o1NameLowerCase.length() && j == o2NameLowercase.length())
							{
								return 0;
							}
							if (j == o1NameLowerCase.length() && j != o2NameLowercase.length())
							{
								return 1;
							}
							if (j != o1NameLowerCase.length() && j == o2NameLowercase.length())
							{
								return -1;
							}
							if (o1NameLowerCase.charAt(j) > o2NameLowercase.charAt(j))
							{
								return 1;
							}
							if (o1NameLowerCase.charAt(j) < o2NameLowercase.charAt(j))
							{
								return -1;
							}

						}
					}

					//o1 is smaller if it is Objective Pre-Assessment and o2 is not
					if (o1.assessmentType.equals(assessmentTypes.OBJECTIVE_PREASSESSMENT.toString()) &&
						!o2.assessmentType.equals(assessmentTypes.OBJECTIVE_PREASSESSMENT.toString()))
					{
						return -1;
					}

					//o2 is smaller if it is Objective Pre-Assessment and o1 is not
					if (!o1.assessmentType.equals(assessmentTypes.OBJECTIVE_PREASSESSMENT.toString()) &&
						o2.assessmentType.equals(assessmentTypes.OBJECTIVE_PREASSESSMENT.toString()))
					{
						return 1;
					}

					//Checks names if both are Objective Pre-Assessments
					if (o1.assessmentType.equals(assessmentTypes.OBJECTIVE_PREASSESSMENT.toString()) &&
						o2.assessmentType.equals(assessmentTypes.OBJECTIVE_PREASSESSMENT.toString()))
					{
						String o1NameLowerCase = o1.getAssessmentName().toLowerCase();
						String o2NameLowercase = o2.getAssessmentName().toLowerCase();

						for (int j = 0 ; ; j++)
						{

							if (j == o1NameLowerCase.length() && j == o2NameLowercase.length())
							{
								return 0;
							}
							if (j == o1NameLowerCase.length() && j != o2NameLowercase.length())
							{
								return 1;
							}
							if (j != o1NameLowerCase.length() && j == o2NameLowercase.length())
							{
								return -1;
							}
							if (o1NameLowerCase.charAt(j) > o2NameLowercase.charAt(j))
							{
								return 1;
							}
							if (o1NameLowerCase.charAt(j) < o2NameLowercase.charAt(j))
							{
								return -1;
							}

						}
					}

					//o1 is smaller if it is Performance Assessment and o2 is not
					if (o1.assessmentType.equals(assessmentTypes.PERFORMANCE_ASSESSMENT.toString()) &&
						!o2.assessmentType.equals(assessmentTypes.PERFORMANCE_ASSESSMENT.toString()))
					{
						return -1;
					}

					//o2 is smaller if it is Performance Assessment and o1 is not
					if (!o1.assessmentType.equals(assessmentTypes.PERFORMANCE_ASSESSMENT.toString()) &&
						o2.assessmentType.equals(assessmentTypes.PERFORMANCE_ASSESSMENT.toString()))
					{
						return 1;
					}

					//Checks names if both are Performance Assessments
					if (o1.assessmentType.equals(assessmentTypes.PERFORMANCE_ASSESSMENT.toString()) &&
						o2.assessmentType.equals(assessmentTypes.PERFORMANCE_ASSESSMENT.toString()))
					{
						String o1NameLowerCase = o1.getAssessmentName().toLowerCase();
						String o2NameLowercase = o2.getAssessmentName().toLowerCase();

						for (int j = 0 ; ; j++)
						{

							if (j == o1NameLowerCase.length() && j == o2NameLowercase.length())
							{
								return 0;
							}
							if (j == o1NameLowerCase.length() && j != o2NameLowercase.length())
							{
								return 1;
							}
							if (j != o1NameLowerCase.length() && j == o2NameLowercase.length())
							{
								return -1;
							}
							if (o1NameLowerCase.charAt(j) > o2NameLowercase.charAt(j))
							{
								return 1;
							}
							if (o1NameLowerCase.charAt(j) < o2NameLowercase.charAt(j))
							{
								return -1;
							}

						}
					}

					//o1 is smaller if it is Objective Assessment and o2 is not
					if (o1.assessmentType.equals(assessmentTypes.OBJECTIVE_ASSESSMENT.toString()) &&
						!o2.assessmentType.equals(assessmentTypes.OBJECTIVE_ASSESSMENT.toString()))
					{
						return -1;
					}

					//o2 is smaller if it is Objective Assessment and o1 is not
					if (!o1.assessmentType.equals(assessmentTypes.OBJECTIVE_ASSESSMENT.toString()) &&
						o2.assessmentType.equals(assessmentTypes.OBJECTIVE_ASSESSMENT.toString()))
					{
						return 1;
					}

					//Checks names if both are Objective Assessments
					if (!o1.assessmentType.equals(assessmentTypes.OBJECTIVE_ASSESSMENT.toString()) &&
						o2.assessmentType.equals(assessmentTypes.OBJECTIVE_ASSESSMENT.toString()))
					{
						String o1NameLowerCase = o1.getAssessmentName().toLowerCase();
						String o2NameLowercase = o2.getAssessmentName().toLowerCase();

						for (int j = 0 ; ; j++)
						{

							if (j == o1NameLowerCase.length() && j == o2NameLowercase.length())
							{
								return 0;
							}
							if (j == o1NameLowerCase.length() && j != o2NameLowercase.length())
							{
								return 1;
							}
							if (j != o1NameLowerCase.length() && j == o2NameLowercase.length())
							{
								return -1;
							}
							if (o1NameLowerCase.charAt(j) > o2NameLowercase.charAt(j))
							{
								return 1;
							}
							if (o1NameLowerCase.charAt(j) < o2NameLowercase.charAt(j))
							{
								return -1;
							}

						}
					}
				}

				//o1 is smaller if o2 is longer
				if (i == o1LowerCase.length() && i != o2Lowercase.length())
				{
					return -1;
				}

				//o2 is smaller if o1 is longer
				if (i != o1LowerCase.length() && i == o2Lowercase.length())
				{
					return 1;
				}

				//o1 is larger if a letter is higher in the alphabet than its match in o2
				if (o1LowerCase.charAt(i) > o2Lowercase.charAt(i))
				{
					return 1;
				}

				//o1 is smaller is a letter is lower in the alphabet than its match in o2
				if (o1LowerCase.charAt(i) < o2Lowercase.charAt(i))
				{
					return -1;
				}

			}
		}
	};
	final static Comparator alarmsComparator = new Comparator<AlarmObject>()
	{
		@Override
		public int compare(AlarmObject o1, AlarmObject o2)
		{
			SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
			final SimpleDateFormat timeFormatter = new SimpleDateFormat("H:mm a");
			Date date1;
			Date date2;

			try
			{
				date1 = dateFormatter.parse(o1.getDate() + " " + o1.getTime());
				date2 = dateFormatter.parse(o2.getDate() + " " + o2.getTime());


				if (!date1.before(date2) && !date2.before(date1))
				{
					if (o1.getType().equals("Course") && o2.getType().equals("Course"))
					{
						return getInstance().getCourseById(o1.getItemId()).getCourseNumber()
											.compareTo(getInstance().getCourseById(o2.getItemId()).getCourseNumber());
					}
					if (o1.getType().equals("term") && o2.getType().equals("term"))
					{
						return getInstance().getTermById(o1.getItemId()).getTerm().compareTo(getInstance().getTermById(o2.getItemId()).getTerm());
					}

					if (o1.getType().equals("Course") && !o2.getType().equals("Course"))
					{
						return -1;
					}

					if (!o1.getType().equals("Course") && o2.getType().equals("Course"))
					{
						return 1;
					}

					return 0;
				}

				else if (date1.before(date2))
				{
					return -1;
				}

				else if (date2.before(date1))
				{
					return 1;
				}
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}

			return 0;
		}
	};

	//Assessment types from strings.xml array list
	private enum assessmentTypes
	{
		PERFORMANCE_PREASSESSMENT("Performance Pre-Assessment"), PERFORMANCE_ASSESSMENT("Performance Assessment"),
		OBJECTIVE_PREASSESSMENT("Objective Pre-Assessment"), OBJECTIVE_ASSESSMENT("Objective Assessment");

		String text;

		assessmentTypes(String text)
		{
			this.text = text;
		}

		@Override
		public String toString()
		{
			return text;
		}
	}

	private DatabaseHandler()
	{
	}

	private DatabaseHandler(Context context)
	{
		this.context = context;
		helper = new DBOpenHelper(context);
		database = helper.getWritableDatabase();
		termsCursor = database.query(Contracts.TermEntry.TABLE_NAME, null, null, null, null, null, null);
		if (termsCursor.moveToFirst())
		{
			do
			{
				termsArray.add(new TermObject());
			} while (termsCursor.moveToNext());
		}
		//Sorts array by term name
		termsArray.sort(termsComparator);


		coursesCursor = database.query(Contracts.CourseEntry.TABLE_NAME, null, null, null, null, null, null);
		if (coursesCursor.moveToFirst())
		{
			do
			{
				coursesArray.add(new CourseObject());
			} while (coursesCursor.moveToNext());
		}

		//Sorts array by course number
		coursesArray.sort(coursesComparator);

		assessmentCursor = database.query(Contracts.AssessmentEntry.TABLE_NAME, null, null, null, null, null, null);
		if (assessmentCursor.moveToFirst())
		{
			do
			{
				assessmentArray.add(new AssessmentObject());
			} while (assessmentCursor.moveToNext());
		}
		//Sorts array by course number then assessment type in order of
		// PerPre, ObjPre, PerAssess, ObjAssess
		assessmentArray.sort(assessmentsComparator);

		alarmsCursor = database.query(Contracts.AlarmEntry.TABLE_NAME, null, null, null, null, null, null);
		if (alarmsCursor.moveToFirst())
		{
			do
			{
				alarmsArray.add(new AlarmObject());
			} while (alarmsCursor.moveToNext());
		}
		alarmsArray.sort(alarmsComparator);
	}

	private void resortTermsArray()
	{
		termsArray.sort(termsComparator);
	}

	private void resortCoursesArray()
	{
		coursesArray.sort(coursesComparator);
	}

	private void resortAssessmentsArray()
	{
		assessmentArray.sort(assessmentsComparator);
	}

	private void resortAlarmsArray()
	{
		alarmsArray.sort(alarmsComparator);
	}

	public static DatabaseHandler getInstance(Context context)
	{
		if (handlerInstance == null)
		{
			handlerInstance = new DatabaseHandler(context);

		}

		return handlerInstance;
	}

	public static DatabaseHandler getInstance()
	{
		if (handlerInstance == null)
		{
			return null;

		}

		return handlerInstance;
	}

	public ArrayList<String> getTermsNameList()
	{
		ArrayList<String> tempTermList = new ArrayList<>();

		for (TermObject termObject : termsArray)
		{
			tempTermList.add(termObject.getTerm());
		}

		return tempTermList;
	}

	public ArrayList<String> getAssessmentsNameList()
	{
		ArrayList<String> tempAssessmentsList = new ArrayList<>();

		for (AssessmentObject assessmentObject : assessmentArray)
		{
			tempAssessmentsList.add(assessmentObject.getAssessmentName().concat(" for ").concat(assessmentObject.getCourseNumber()));
		}

		return tempAssessmentsList;
	}

	public CourseObject getCourseByPosition(int id)
	{
		if (id < coursesArray.size())
		{
			return coursesArray.get(id);
		}

		return null;
	}

	public int getCoursePositionInArray(String id)
	{
		for (int i = 0 ; i < coursesArray.size() ; i++)
		{
			if (coursesArray.get(i).getId().equals(id))
			{
				return i;
			}
		}

		return -1;
	}

	public CourseObject getCourseById(String id)
	{
		for (CourseObject course : coursesArray)
		{
			if (course.getId().equals(id))
			{
				return course;
			}
		}

		return null;
	}

	public TermObject getTermByPosition(int id)
	{
		if (id < termsArray.size())
		{
			return termsArray.get(id);
		}

		return null;
	}

	public int getTermPositionInArray(String id)
	{
		for (int i = 0 ; i < termsArray.size() ; i++)
		{
			if (termsArray.get(i).getId().equals(id))
			{
				return i;
			}
		}

		return -1;
	}

	public TermObject getTermById(String id)
	{
		for (TermObject term : termsArray)
		{
			if (term.getId().equals(id))
			{
				return term;
			}
		}

		return null;
	}

	public ArrayList<String> getCoursesNameList()
	{
		ArrayList<String> tempCoursesList = new ArrayList<>();
		for (CourseObject courseObject : coursesArray)
		{
			tempCoursesList.add(courseObject.courseName);
		}

		return tempCoursesList;
	}

	public ArrayList<CourseObject> getCoursesInTerm(String id)
	{
		ArrayList<CourseObject> tempCourseList = new ArrayList<>();
		for (CourseObject course : coursesArray)
		{
			if (course.getTermId().equals(id))
			{
				tempCourseList.add(course);
			}
		}
		return tempCourseList;
	}

	public ArrayList<String> getCoursesNumberList()
	{
		ArrayList<String> tempCoursesList = new ArrayList<>();
		for (CourseObject courseObject : coursesArray)
		{
			tempCoursesList.add(courseObject.courseNumber);
		}

		return tempCoursesList;
	}

	public AssessmentObject getAssessmentById(String id)
	{
		for (AssessmentObject assessment : assessmentArray)
		{
			if (assessment.getId().equals(id))
			{
				return assessment;
			}
		}

		return null;
	}

	public AssessmentObject getAssessmentByPosition(int id)
	{
		if (id < assessmentArray.size())
		{
			return assessmentArray.get(id);
		}

		return null;
	}

	public int getAssessmentPositionInArray(String id)
	{
		for (int i = 0 ; i < assessmentArray.size() ; i++)
		{
			if (assessmentArray.get(i).getId().equals(id))
			{
				return i;
			}
		}

		return -1;
	}


	public ArrayList<AssessmentObject> getAllAssessments()
	{
		return assessmentArray;
	}

	public AlarmObject getAlarmById(String alarmId)
	{
		for (AlarmObject alarm : alarmsArray)
		{
			if (alarm.getId().equals(alarmId))
			{
				return alarm;
			}
		}
		return null;
	}

	public class TermObject
	{
		private String id = null;
		private String term = null;
		private String startDate = null;
		private String endDate = null;

		//gets data from current place of cursor
		private TermObject()
		{
			id = termsCursor.getString(termsCursor.getColumnIndex(Contracts.TermEntry._ID));
			term = termsCursor.getString(termsCursor.getColumnIndex(Contracts.TermEntry.COLUMN_TERM));
			startDate = termsCursor.getString(termsCursor.getColumnIndex(Contracts.TermEntry.COLUMN_START_DATE));
			endDate = termsCursor.getString(termsCursor.getColumnIndex(Contracts.TermEntry.COLUMN_END_DATE));
		}

		//gets data from parameters
		public TermObject(String id, String term, String startDate, String endDate)
		{
			this.id = id;
			this.term = term;
			this.startDate = startDate;
			this.endDate = endDate;

		}

		public String getId()
		{
			return id;
		}

		public String getTerm()
		{
			return term;
		}

		public String getStartDate()
		{
			return startDate;
		}

		public String getEndDate()
		{
			return endDate;
		}
	}

	public class CourseObject
	{
		private String id = null;
		private String courseNumber = null;
		private String courseName = null;
		private String courseTerm = null;
		private String courseStatus = null;
		private String courseStartDate = null;
		private String courseEndDate = null;
		private String courseMentorName = null;
		private String courseMentorPhone = null;
		private String courseMentorEmail = null;
		private String courseNotes = null;

		//gets data from current place of cursor
		private CourseObject()
		{
			id = coursesCursor.getString(coursesCursor.getColumnIndex(Contracts.CourseEntry._ID));
			courseNumber = coursesCursor.getString(coursesCursor.getColumnIndex(Contracts.CourseEntry.COLUMN_NUMBER));
			courseName = coursesCursor.getString(coursesCursor.getColumnIndex(Contracts.CourseEntry.COLUMN_NAME));
			courseTerm = coursesCursor.getString(coursesCursor.getColumnIndex(Contracts.CourseEntry.COLUMN_TERM));
			courseStatus = coursesCursor.getString(coursesCursor.getColumnIndex(Contracts.CourseEntry.COLUMN_STATUS));
			courseStartDate = coursesCursor.getString(coursesCursor.getColumnIndex(Contracts.CourseEntry.COLUMN_START_DATE));
			courseEndDate = coursesCursor.getString(coursesCursor.getColumnIndex(Contracts.CourseEntry.COLUMN_END_DATE));
			courseMentorName = coursesCursor.getString(coursesCursor.getColumnIndex(Contracts.CourseEntry.COLUMN_MENTOR_NAME));
			courseMentorPhone = coursesCursor.getString(coursesCursor.getColumnIndex(Contracts.CourseEntry.COLUMN_MENTOR_PHONE));
			courseMentorEmail = coursesCursor.getString(coursesCursor.getColumnIndex(Contracts.CourseEntry.COLUMN_MENTOR_EMAIL));
			courseNotes = coursesCursor.getString(coursesCursor.getColumnIndex(Contracts.CourseEntry.COLUMN_NOTES));
		}

		public CourseObject(String id, String courseNumber, String courseName, int courseTerm, String courseStatus, String courseStartDate,
							String courseEndDate, String courseMentorName, String courseMentorPhone, String courseMentorEmail, String courseNotes)
		{
			this.id = id;
			this.courseNumber = courseNumber;
			this.courseName = courseName;
			this.courseTerm = termsArray.get(courseTerm).getId();
			this.courseStatus = courseStatus;
			this.courseStartDate = courseStartDate;
			this.courseEndDate = courseEndDate;
			this.courseMentorName = courseMentorName;
			this.courseMentorPhone = courseMentorPhone;
			this.courseMentorEmail = courseMentorEmail;
			this.courseNotes = courseNotes;
		}

		public String getId()
		{
			return id;
		}

		public String getCourseNumber()
		{
			return courseNumber;
		}

		public String getCourseName()
		{
			return courseName;
		}

		public String getTermId()
		{
			return courseTerm;
		}

		public String getCourseTerm()
		{
			for (TermObject termObject : termsArray)
			{
				if (termObject.id.equals(courseTerm))
				{
					return termObject.getTerm();
				}


			}
			return "";
		}

		public String getCourseStatus()
		{
			return courseStatus;
		}

		public String getCourseStartDate()
		{
			return courseStartDate;
		}

		public String getCourseEndDate()
		{
			return courseEndDate;
		}

		public String getCourseMentorName()
		{
			return courseMentorName;
		}

		public String getCourseMentorPhone()
		{
			return courseMentorPhone;
		}

		public String getCourseMentorEmail()
		{
			return courseMentorEmail;
		}

		public String getCourseNotes()
		{
			return courseNotes;
		}
	}

	public class AssessmentObject
	{
		private String id = null;
		private String course = null;
		private String assessmentName = null;
		private String assessmentType = null;
		private String assessmentNotes = null;

		//gets data from current place of cursor
		private AssessmentObject()
		{
			id = assessmentCursor.getString(assessmentCursor.getColumnIndex(Contracts.AssessmentEntry._ID));
			course = assessmentCursor.getString(assessmentCursor.getColumnIndex(Contracts.AssessmentEntry.COLUMN_COURSE));
			assessmentName = assessmentCursor.getString(assessmentCursor.getColumnIndex(Contracts.AssessmentEntry.ASSESSMENT_NAME));
			assessmentType = assessmentCursor.getString(assessmentCursor.getColumnIndex(Contracts.AssessmentEntry.COLUMN_ASSESSMENT_TYPE));
			assessmentNotes = assessmentCursor.getString(assessmentCursor.getColumnIndex(Contracts.AssessmentEntry.COLUMN_NOTES));
		}

		//gets data from parameters
		public AssessmentObject(String id, int course, String assessmentName, String assessmentType, String assessmentNotes)
		{
			this.id = id;
			this.course = getCourseByPosition(course).getId();
			this.assessmentName = assessmentName;
			this.assessmentType = assessmentType;
			this.assessmentNotes = assessmentNotes;
		}

		public String getId()
		{
			return id;
		}

		public String getCourseId()
		{
			return course;
		}

		public String getCourseNumber()
		{
			for (CourseObject courseObject : coursesArray)
			{
				if (courseObject.id.equals(course))
				{
					return courseObject.courseNumber;
				}
			}
			return "";
		}

		public String getCourseName()
		{
			for (CourseObject courseObject : coursesArray)
			{
				if (courseObject.id.equals(course))
				{
					return courseObject.courseName;
				}
			}
			return "";
		}

		public String getAssessmentName()
		{
			return assessmentName;
		}

		public String getAssessmentType()
		{
			return assessmentType;
		}

		public String getNotes()
		{
			return assessmentNotes;
		}
	}

	public class AlarmObject
	{
		private String id = null;
		private String itemId;
		private String type = null;
		private String date = null;
		private String time = null;

		//gets data from current place of cursor
		private AlarmObject()
		{
			id = alarmsCursor.getString(alarmsCursor.getColumnIndex(Contracts.AlarmEntry._ID));
			itemId = alarmsCursor.getString(alarmsCursor.getColumnIndex(Contracts.AlarmEntry.COLUMN_ITEM_ID));
			type = alarmsCursor.getString(alarmsCursor.getColumnIndex(Contracts.AlarmEntry.COLUMN_TYPE));
			date = alarmsCursor.getString(alarmsCursor.getColumnIndex(Contracts.AlarmEntry.COLUMN_DATE));
			time = alarmsCursor.getString(alarmsCursor.getColumnIndex(Contracts.AlarmEntry.COLUMN_TIME));
		}

		//gets data from parameters
		public AlarmObject(String id, String itemId, String type, String date, String time)
		{
			this.id = id;
			this.itemId = itemId;
			this.type = type;
			this.date = date;
			this.time = time;
		}

		public String getId()
		{
			return id;
		}

		public String getItemId()
		{
			return itemId;
		}

		public String getDate()
		{
			return date;
		}

		public String getTime()
		{
			return time;
		}

		public String getType()
		{
			return type;
		}
	}

	/*
	TODO add database update and delete for Terms, Courses, and Assessments
	Use _id in Courses for Term and in Assessments for Course
	Update the object from a queue to the array first then update database using updated object and
	prepared statements
	Have update for each table
	Use thread to check for queue update, then process database update
	*/

	public <T> long insert(T t)
	{

		ContentValues cv = new ContentValues();
		if (t.getClass() == TermObject.class)
		{
			TermObject values = (TermObject) t;


			cv.put(Contracts.TermEntry.COLUMN_TERM, values.getTerm());
			cv.put(Contracts.TermEntry.COLUMN_START_DATE, values.getStartDate());
			cv.put(Contracts.TermEntry.COLUMN_END_DATE, values.getEndDate());

			long newRow = database.insert(Contracts.TermEntry.TABLE_NAME, null, cv);

			if (newRow != -1)
			{
				values.id = String.valueOf(newRow);
				termsArray.add(values);
				resortTermsArray();
			}
			return newRow;
		}

		if (t.getClass() == CourseObject.class)
		{
			CourseObject values = (CourseObject) t;

			cv.put(Contracts.CourseEntry.COLUMN_NUMBER, values.getCourseNumber());
			cv.put(Contracts.CourseEntry.COLUMN_NAME, values.getCourseName());
			cv.put(Contracts.CourseEntry.COLUMN_TERM, values.getTermId());
			cv.put(Contracts.CourseEntry.COLUMN_START_DATE, values.getCourseStartDate());
			cv.put(Contracts.CourseEntry.COLUMN_END_DATE, values.getCourseEndDate());
			cv.put(Contracts.CourseEntry.COLUMN_STATUS, values.getCourseStatus());
			cv.put(Contracts.CourseEntry.COLUMN_MENTOR_NAME, values.getCourseMentorName());
			cv.put(Contracts.CourseEntry.COLUMN_MENTOR_PHONE, values.getCourseMentorPhone());
			cv.put(Contracts.CourseEntry.COLUMN_MENTOR_EMAIL, values.getCourseMentorEmail());
			cv.put(Contracts.CourseEntry.COLUMN_NOTES, values.getCourseNotes());

			long newRow = database.insert(Contracts.CourseEntry.TABLE_NAME, null, cv);

			if (newRow != -1)
			{
				values.id = String.valueOf(newRow);
				coursesArray.add(values);
				resortCoursesArray();
			}

			return newRow;
		}

		if (t.getClass() == AssessmentObject.class)
		{
			AssessmentObject values = (AssessmentObject) t;

			cv.put(Contracts.AssessmentEntry.ASSESSMENT_NAME, values.getAssessmentName());
			cv.put(Contracts.AssessmentEntry.COLUMN_COURSE, values.getCourseId());
			cv.put(Contracts.AssessmentEntry.COLUMN_ASSESSMENT_TYPE, values.getAssessmentType());
			cv.put(Contracts.AssessmentEntry.COLUMN_NOTES, values.getNotes());

			long newRow = database.insert(Contracts.AssessmentEntry.TABLE_NAME, null, cv);

			if (newRow != -1)
			{
				values.id = String.valueOf(newRow);
				assessmentArray.add(values);
				resortAssessmentsArray();
			}

			return newRow;
		}

		if (t.getClass() == AlarmObject.class)
		{
			AlarmObject values = (AlarmObject) t;

			cv.put(Contracts.AlarmEntry.COLUMN_TYPE, values.getType());
			cv.put(Contracts.AlarmEntry.COLUMN_ITEM_ID, values.getItemId());
			cv.put(Contracts.AlarmEntry.COLUMN_DATE, values.getDate());
			cv.put(Contracts.AlarmEntry.COLUMN_TIME, values.getTime());

			long newRow = database.insert(Contracts.AlarmEntry.TABLE_NAME, null, cv);

			if (newRow != -1)
			{
				values.id = String.valueOf(newRow);
				try
				{
					SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
					Date alarmDateTime = dateFormatter.parse(values.getDate().concat(" ").concat(values.getTime()));

					dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
					Date alarmDate = dateFormatter.parse(values.getDate());
					String title = "";
					String message = "";
					String due = "";

					if (values.getType().equals("Term"))
					{
						title = getTermById(values.getItemId()).getTerm();
					}
					else if (values.getType().equals("Course"))
					{
						title = getCourseById(values.getItemId()).getCourseName();
					}
					else if (values.getType().equals("Assessment"))
					{
						title = getAssessmentById(values.getItemId()).getAssessmentName();
					}

					if (values.getType().equals("Term"))
					{
						TermObject term = getTermById(values.getItemId());

						if (alarmDate.after(dateFormatter.parse(term.getStartDate())))
						{
							message = " ends on ";
							due = getTermById(values.getItemId()).getEndDate();
						}
						else
						{
							message = " starts on ";
							due = getTermById(values.getItemId()).getStartDate();
						}
					}
					else if (values.getType().equals("Course"))
					{
						CourseObject course = getCourseById(values.getItemId());

						if (alarmDate.after(dateFormatter.parse(course.getCourseStartDate())))
						{
							message = " ends on ";
							due = course.getCourseEndDate();
						}
						else
						{
							message = " starts on ";
							due = course.getCourseStartDate();
						}
					}
					else if (values.getType().equals("Assessment"))
					{
						message = " is due before ";
						due = getCourseById(values.getItemId()).getCourseEndDate();
					}

					NotificationAlarmManager
							.createAlarm(context, values.getType(), title.concat(message).concat(due), Integer.parseInt(values.getId()),
														 alarmDateTime.getTime() - Calendar.getInstance().getTimeInMillis());
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				alarmsArray.add(values);
				resortAlarmsArray();
			}

			return newRow;
		}

		return -1;
	}

	public <T> void update(T t)
	{
		ContentValues cv = new ContentValues();
		if (t.getClass() == TermObject.class)
		{
			TermObject values = (TermObject) t;


			cv.put(Contracts.TermEntry.COLUMN_TERM, values.getTerm());
			cv.put(Contracts.TermEntry.COLUMN_START_DATE, values.getStartDate());
			cv.put(Contracts.TermEntry.COLUMN_END_DATE, values.getEndDate());
			cv.put(Contracts.TermEntry._ID, values.getId());

			long rows =
					database.update(Contracts.TermEntry.TABLE_NAME, cv, Contracts.TermEntry._ID.concat(" = \"").concat(values.getId()).concat("\""),
									null);


			for (int i = 0 ; i < termsArray.size() ; i++)
			{
				if (termsArray.get(i).getId().equals(values.getId()))
				{
					termsArray.set(i, values);
				}
			}

			resortTermsArray();
		}

		if (t.getClass() == CourseObject.class)
		{
			CourseObject values = (CourseObject) t;

			cv.put(Contracts.CourseEntry._ID, values.getId());
			cv.put(Contracts.CourseEntry.COLUMN_NUMBER, values.getCourseNumber());
			cv.put(Contracts.CourseEntry.COLUMN_NAME, values.getCourseName());
			cv.put(Contracts.CourseEntry.COLUMN_TERM, values.getCourseTerm());
			cv.put(Contracts.CourseEntry.COLUMN_START_DATE, values.getCourseStartDate());
			cv.put(Contracts.CourseEntry.COLUMN_END_DATE, values.getCourseEndDate());
			cv.put(Contracts.CourseEntry.COLUMN_STATUS, values.getCourseStatus());
			cv.put(Contracts.CourseEntry.COLUMN_MENTOR_NAME, values.getCourseMentorName());
			cv.put(Contracts.CourseEntry.COLUMN_MENTOR_PHONE, values.getCourseMentorPhone());
			cv.put(Contracts.CourseEntry.COLUMN_MENTOR_EMAIL, values.getCourseMentorEmail());
			cv.put(Contracts.CourseEntry.COLUMN_NOTES, values.getCourseNotes());

			long rows = database.update(Contracts.CourseEntry.TABLE_NAME, cv,
										Contracts.CourseEntry._ID.concat(" = \"").concat(values.getId()).concat("\""), null);

			for (int i = 0 ; i < coursesArray.size() ; i++)
			{
				if (coursesArray.get(i).getId().equals(values.getId()))
				{
					coursesArray.set(i, values);
					break;
				}
			}
			resortCoursesArray();

		}

		if (t.getClass() == AssessmentObject.class)
		{
			AssessmentObject values = (AssessmentObject) t;

			cv.put(Contracts.AssessmentEntry._ID, values.getId());
			cv.put(Contracts.AssessmentEntry.COLUMN_COURSE, values.getCourseId());
			cv.put(Contracts.AssessmentEntry.COLUMN_ASSESSMENT_TYPE, values.getAssessmentType());
			cv.put(Contracts.AssessmentEntry.COLUMN_NOTES, values.getNotes());

			long rows = database.update(Contracts.AssessmentEntry.TABLE_NAME, cv,
										Contracts.AssessmentEntry._ID.concat(" = \"").concat(values.getId()).concat("\""), null);

			for (int i = 0 ; i < termsArray.size() ; i++)
			{
				if (assessmentArray.get(i).getId().equals(values.getId()))
				{
					assessmentArray.set(i, values);
					break;
				}
			}
			resortAssessmentsArray();

		}

		if (t.getClass() == AlarmObject.class)
		{
			AlarmObject values = (AlarmObject) t;

			cv.put(Contracts.AlarmEntry._ID, values.getId());
			cv.put(Contracts.AlarmEntry.COLUMN_TYPE, values.getType());
			cv.put(Contracts.AlarmEntry.COLUMN_ITEM_ID, values.getItemId());
			cv.put(Contracts.AlarmEntry.COLUMN_DATE, values.getDate());
			cv.put(Contracts.AlarmEntry.COLUMN_TIME, values.getTime());

			long rows =
					database.update(Contracts.AlarmEntry.TABLE_NAME, cv, Contracts.AlarmEntry._ID.concat(" = \"").concat(values.getId()).concat
											("\""),
									null);

			for (int i = 0 ; i < alarmsArray.size() ; i++)
			{
				if (alarmsArray.get(i).getId().equals(values.getId()))
				{
					alarmsArray.set(i, values);
				}
			}

			if (rows != 0)
			{

				try
				{
					SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
					Date alarmDateTime = dateFormatter.parse(values.getDate().concat(" ").concat(values.getTime()));
					String title = "";
					String message = "";
					String due = "";

					if (values.getType().equals("Term"))
					{
						getTermById(values.getItemId()).getTerm();
					}
					else if (values.getType().equals("Course"))
					{
						getCourseById(values.getItemId()).getCourseName();
					}

					if (values.getType().equals("Term"))
					{
						if (alarmDateTime.after(dateFormatter.parse(getTermById(values.getItemId()).getStartDate())))
						{
							message = " ends on ";
							due = getTermById(values.getItemId()).getEndDate();
						}
						else
						{
							message = " starts on ";
							getTermById(values.getItemId()).getStartDate();
						}
					}
					else if (values.getType().equals("Course"))
					{
						String courseDate = "";

						if (getCourseById(values.getItemId()).getCourseStartDate().equals(""))
						{
							courseDate = getTermById(getCourseById(values.getItemId()).getTermId()).getStartDate();
						}

						if (alarmDateTime.after(dateFormatter.parse(courseDate)))
						{
							if (getCourseById(values.getItemId()).getCourseEndDate().equals(""))
							{
								courseDate = getTermById(getCourseById(values.getItemId()).getTermId()).getStartDate();
							}

							message = " ends on ";
							due = courseDate;
						}
						else
						{
							if (getCourseById(values.getItemId()).getCourseEndDate().equals(""))
							{
								courseDate = getTermById(getCourseById(values.getItemId()).getTermId()).getStartDate();
							}
							message = " starts on ";
							due = courseDate;
						}
					}

					NotificationAlarmManager.createAlarm(context, title, title.concat(message).concat(due), Integer.parseInt(values.getId()),
														 alarmDateTime.getTime() - Calendar.getInstance().getTimeInMillis());

				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
			}

			resortAlarmsArray();
		}
	}

	public void delete(String table, int id)
	{
		if (Contracts.AlarmEntry.TABLE_NAME.equals(table) || Contracts.TermEntry.TABLE_NAME.equals(table) ||
			Contracts.CourseEntry.TABLE_NAME.equals(table) || Contracts.AssessmentEntry.TABLE_NAME.equals(table))
		{
			if (database.delete(table, Contracts.TermEntry._ID + " = " + id, null) > 0)
			{

				if (Contracts.AlarmEntry.TABLE_NAME.equals(table))
				{
					for (AlarmObject alarm : alarmsArray)
					{
						if (alarm.getId().equals(String.valueOf(id)))
						{
							//NotificationAlarmManager.deleteAlarm(id);  Safe delete required rewrite of id to use code for id based on table and id
							alarmsArray.remove(alarm);
							return;
						}
					}
				}
				if (Contracts.TermEntry.TABLE_NAME.equals(table))
				{
					for (TermObject term : termsArray)
					{
						if (term.getId().equals(String.valueOf(id)))
						{
							termsArray.remove(term);
							return;
						}
					}

				}
				if (Contracts.CourseEntry.TABLE_NAME.equals(table))
				{
					for (CourseObject course : coursesArray)
					{
						if (course.getId().equals(String.valueOf(id)))
						{
							coursesArray.remove(course);
							return;
						}
					}
				}
				if (Contracts.AssessmentEntry.TABLE_NAME.equals(table))
				{
					for (AssessmentObject assessment : assessmentArray)
					{
						if (assessment.getId().equals(String.valueOf(id)))
						{
							assessmentArray.remove(assessment);

							return;
						}
					}
				}
			}
		}
	}

	public void clearTable(String table)
	{
		if (Contracts.AlarmEntry.TABLE_NAME.equals(table) || Contracts.TermEntry.TABLE_NAME.equals(table) ||
			Contracts.CourseEntry.TABLE_NAME.equals(table) || Contracts.AssessmentEntry.TABLE_NAME.equals(table))
		{
			database.delete(table, null, null);
		}
	}

	public ArrayList<TermObject> getAllTerms()
	{
		return termsArray;
	}

	public ArrayList<CourseObject> getAllCourses()
	{
		return coursesArray;
	}

	public ArrayList<AssessmentObject> getAllAssessmentsForCourse(String course)
	{

		ArrayList<AssessmentObject> assessmentsForCourse = new ArrayList<>();
		for (AssessmentObject assessment : assessmentArray)
		{
			if (assessment.getCourseId().equals(course))
			{
				assessmentsForCourse.add(assessment);
			}
		}
		return assessmentsForCourse;
	}

	public ArrayList<AlarmObject> getAllAlarms()
	{
		return alarmsArray;
	}
}