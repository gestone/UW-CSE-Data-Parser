UW-CSE-Data-Parser
==================

This console based application takes in score sheets in from University of Washington's introductory CSE 142 and CSE 143 courses posted by Stuart Reges. The application can either take one score sheet in and perform analysis on that quarter in particular, or take in two consecutive quarters of 142 and 143 and calculate and graph the correlation between grades in 142 and 143.

The application has six core features:

1. Parse: Parses the raw data spreadsheet and prints it out into a JSON file. In parsing a single file, each student's midterm, final, overall homework score, and final grade are stored into a JSONObject which is then outputted. If two files are selected, both the student's 142 and 143 midterm, final, homework percentage, and final grades are printed out to allow for comparision.

  [Here is a sample] (https://raw.githubusercontent.com/gestone/UW-CSE-Data-Parser/master/CSDataJSON/SingleQuarter/2014/cse143winter2014.json) of a single parsed course (Stuart's most recent 143 class).

  [Here is a sample] (https://raw.githubusercontent.com/gestone/UW-CSE-Data-Parser/master/CSDataJSON/ComparingQuarters/2014/autumn2013winter2014.json) of two parsed courses which contain data on students who took Autumn 2013 CSE 142 and Winter CSE 143 and their corresponding grades.

2. Correlate: Correlates 
