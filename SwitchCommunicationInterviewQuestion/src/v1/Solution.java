package v1;

/**
 * @author Yan
 * @date 2015-09-08
 * @version v1 
 * 
 * QUESTION:
 * Winning Ticket!
 * 
 * Your favorite uncle, Morty, is crazy about the lottery and even crazier about how he picks his “lucky” numbers. 
 * And even though his “never fail” strategy has yet to succeed, Uncle Morty doesn't let that get him down.
 * Every week he searches through the Sunday newspaper to find a string of digits that might be potential lottery picks. 
 * But this week the newspaper has moved to a new electronic format, and instead of a comfortable pile of papers, 
 * Uncle Morty receives a text file with the stories.
 * 
 * Help your Uncle find his lotto picks. Given a large series of number strings, return each that might be suitable for 
 * a lottery ticket pick. Note that a valid lottery ticket must have 7 unique numbers between 1 and 59.
 * 
 * For example, given the following strings:
 * [ “1”, “42". “100848", “4938532894754”, “1234567”]
 * 
 * Your function should return:
 * 4938532894754 -> 49 38 53 28 9 47 54
 * 1234567 -> 1 2 3 4 5 6 7
 * 
 * 
 * SOLUTION:
 * 
 * There are a couple of key points to this problem.
 * 1. The lottery numbers in a set must be unique.
 * 2. In the final list there are no identical lottery 
 *    numbers combinations(eg. [1,2,3,4,5,6,7] and [7,6,5,4,3,2,1] 
 *    are considered as identical sets)
 * 3. Every number in a set must range from 1 to 59
 * 4. Every set contains exactly 7 numbers.
 * 5. 01 to 09 can be regarded 1 to 9
 * 6. The set of numbers should keep the sequence of the number string
 * 7. A single number string may have more than 1 possible lottery numbers combos,
 *    put all of the unique combos in the final list. 
 *    
 * The basic idea of my solution is to use backtracking, because finally I need
 * to return all the lottery numbers sets. I start to check from the first character of the 
 * number string to the end, and at every step I check 1 digit at current index and 2 digits
 * at current and next index, because both of them could be the valid numbers. I use isValid()
 * method to validate them, and at each step I use a tree set check if the current valid number 
 * was add before. If not, add the current valid numbers into the stack and the set. The reason
 * why I use tree set is because The hashset can put the new number at the right place based on 
 * its natural order. I not only need to check if the current number showed up before,
 * but also need to add the sorted set string into the buffer at the end for the duplicate sets check.
 * When the size of the stack reaches 7 and there are no more left characters in the number string, 
 * I firstly check if the sorted numbers set string is in the buffer, if so, skip this set;if not, 
 * make a deep copy of the stack and add it in the final list.
 * 
 * Let us say n is the number of the number strings in the list, 
 * and m is the average length of every number string
 * 
 * 
 * TIME COMPLEXITY: 
 * The backtracking is supposed to take exponential time(2^m) for every number string,
 * however, my code will skip the string whose size is larger than 14, 
 * so m is limited to less than or equals to 14. This way the time complexity for 
 * each string processing becomes a constant time which should less than O(2^14). We
 * can take this as O(1). So the total time should be O(1) * n = O(n).
 * 
 * SPACE COMPLEXITY:
 * Besides the returing list of list of numbers, which is necessary, I use 2 sets to
 * remove duplicates numbers in each set and duplicate sets in final list, and 1 stack
 * to save the numbers temporarily. So the space complexity is O(mn).
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class Solution {

    // the number of numbers on the lottery ticket
    private final int NUMBERS = 7;

    public List<List<Integer>> getLotteryNumbers(List<String> list) {
	// used to save all possible number combinations
	// every list in res contains 7 unique numbers from 1 to 59
	List<List<Integer>> res = new ArrayList<List<Integer>>();

	// a temporary container to save single number
	Stack<Integer> stack = new Stack<Integer>();

	// record the already visited number to avoid the duplicate number
	Set<Integer> visited = new TreeSet<Integer>();

	// record the sorted numbers combinations
	// to avoid the duplicate combination
	Set<String> buffer = new HashSet<String>();

	// do DFS to every string in the list and
	// save the possible nums combo in the res
	for (String str : list) {
	    visited.clear();
	    backTrack(str, 0, visited, buffer, stack, res);
	}

	return res;
    }

    private void backTrack(String str, int curIdx, Set<Integer> visited,
	    Set<String> buffer, Stack<Integer> stack, List<List<Integer>> res) {

	int len = str.length();
	// the number of remaining characters for the rest of the string
	int remainingChars = len - curIdx;
	// the number of remaining space in the stack
	int remainingSpace = this.NUMBERS - stack.size();

	// no need to search deep if the remaining characters
	// is not in the range
	if (remainingChars > remainingSpace * 2
		|| remainingChars < remainingSpace) {
	    return;
	}

	if (stack.size() == this.NUMBERS) {

	    // check if the set appeared before
	    String sortedNumbersString = visited.toString();

	    // if not, add set into the res and sorted string into the buffer
	    if (!buffer.contains(sortedNumbersString)) {
		buffer.add(sortedNumbersString);

		// make a deep copy
		res.add(new ArrayList<Integer>(stack));
	    }

	    return;
	}

	String numStr;
	int numInt;

	// get the current 1-digit string
	numStr = str.substring(curIdx, curIdx + 1);

	// validate current string
	if (isValid(numStr)) {
	    numInt = Integer.parseInt(numStr);

	    // use tree set to check duplication
	    if (!visited.contains(numInt)) {
		visited.add(numInt);
		stack.add(numInt);
		backTrack(str, curIdx + 1, visited, buffer, stack, res);
		visited.remove(numInt);
		stack.pop();
	    }
	}

	// do the same thing to the current 2-digit string
	// as long as the index is not the last one
	if (curIdx < len - 1) {
	    numStr = str.substring(curIdx, curIdx + 2);

	    if (isValid(numStr)) {
		numInt = Integer.parseInt(numStr);
		if (!visited.contains(numInt)) {
		    visited.add(numInt);
		    stack.add(numInt);
		    backTrack(str, curIdx + 2, visited, buffer, stack, res);
		    visited.remove(numInt);
		    stack.pop();
		}
	    }
	}

	return;
    }

    private boolean isValid(String num) {
	int len = num.length();

	// 0 is not a valid number
	if (len == 1) {
	    char c = num.charAt(0);
	    return (c >= '1' && c <= '9');
	}

	char c1 = num.charAt(0);
	char c2 = num.charAt(1);

	// if c1 or c2 is not a number return false
	if (!Character.isDigit(c1) || !Character.isDigit(c2)) {
	    return false;
	}

	if (c1 == '0' && c2 == '0') {
	    return false;
	}

	if (c1 >= '6') {
	    return false;
	}

	return true;

    }

    public static void main(String[] args) {
	String[] strs = { "1", "42", "100848", "4938532894754", "1234567",
		"01020304050607", "7654321", "test1234567", "001234567",
		"9912345678", "12345678", "12223456", "0305221986" };

	List<String> list = new ArrayList<String>(Arrays.asList(strs));

	List<List<Integer>> res = new Solution().getLotteryNumbers(list);

	for (List<Integer> l : res) {
	    System.out.println(l);
	}
    }
}
