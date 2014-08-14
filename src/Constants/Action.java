package Constants;


/**
 * Represents the various actions that can be performed in analyzing the single quarter or comparing quarter data.
 */
public enum Action {

    PARSE,                   // Parses the text file and outputs it into a JSON file

    CORRELATE,               // Correlates two raw CS data files, 142 and 143 by calculating correlations for
                             // homework, midterm, final, and the correlation between the final grades from 142 and 143.

    PERCENTAGE_DISTRIBUTION, // Writes out the percentage distribution of a single quarter to a text file and the
                             // console. Also writes out the corresponding cutoffs for each of the grades.

    GRAPH,                   // Graphs the distribution of grades of a single quarter.

    GRAPH_GRADE_COMPARSION,  // Graphs the grade correlation data points in a scatter plot. Correlate must be selected.

}
