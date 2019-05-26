package com.example.elliotknuth.c196final;

import java.util.ArrayList;

public class Validations
{
	public static boolean assessmentsTableEmpty()
	{
		if (DatabaseHandler.getInstance().getAllAssessments().size() == 0)
		{
			return true;
		}
		return false;
	}

	public static boolean coursesTableEmpty()
	{
		if (DatabaseHandler.getInstance().getAllCourses().size() == 0)
		{
			return true;
		}
		return false;
	}

	public static String termExistsInCourse(String id)
	{
		ArrayList<DatabaseHandler.CourseObject> courses = DatabaseHandler.getInstance().getAllCourses();

		for (DatabaseHandler.CourseObject course : courses)
		{
			if (course.getTermId().equals(id))
			{
				return course.getCourseNumber();
			}
		}

		return null;
	}

	public static String courseContainingAssessment(String id)
	{
		ArrayList<DatabaseHandler.AssessmentObject> assessments = DatabaseHandler.getInstance().getAllAssessmentsForCourse(id);

		if (assessments.size() > 0)
		{
			return assessments.get(0).getAssessmentName();
		}

		return null;
	}
}
