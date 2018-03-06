package edu.ucsb.cs56.projects.utilities.calculator;
import java.util.ArrayList;
import java.lang.NumberFormatException;
import java.util.concurrent.Callable;
import java.text.DecimalFormat;
import java.util.Stack;

/**
* This class represents the portion of the calculator that does all of the
* computations, and sends it to the screen.
* @author Sam Dowell
*/
class Calculator {
	private String entry;
	private int parenCount;
	private boolean displayingResult;
	private JLabelMessageDestination display;
	private JLabelMessageDestination resultDisplay;
	private double result;
    
	/**
	* Constructor
	* @param display The JLabelMessageDestination to send the operations
	* and results to
	*/
	public Calculator(JLabelMessageDestination display, JLabelMessageDestination resultDisplay) {
		displayingResult = false;
		this.display = display;
		this.resultDisplay = resultDisplay;
		resultDisplay.append("Hello!");
		refresh();
		entry = "";
		result = 0.0;
	}
    
	/**
	* Call this method with a String to have the calculator do some operation
	* (i.e. appending a digit to the current number, or appending an operator
	* to the expression)
	*/
	public void append(String s) {
		char d = s.charAt(0);
		if (displayingResult) {
			clear();
			displayingResult = false;
		}
	
		// Checks to see if we have more closed parentheses than open
		if (s.equals(")")) {
			if (parenCount < 1) {
				return;
			}
		}
	
		// If the calculator is blank
		if (entry == "") {
			if ((isOperator(s) && !(s.equals("-"))) || s.equals(")")) {
				return;
			}
			else if (Character.isDigit(d) || s.equals("(") || s.equals("-") || s.equals(".")) {
				entry += s;
				if (s.equals("(")) {
					parenCount++;
				}
			}
		}
	
		// If we're on an operator
		else if (isOperator(entry.substring(entry.length() - 1))) {
			if (Character.isDigit(d)) {
				entry += s;
			}
			else if (s.equals(".")) {
				entry += s;
			}
			else if (s.equals("(")) {
				entry += s;
				parenCount++;
			}
			else if (s.equals("-") && (!isOperator(entry.substring(entry.length() - 2, entry.length() - 1)))) {
				entry += s;
			}
			else {
				return;
			}
		}
	
		// If we're on a number
		else if (Character.isDigit(entry.charAt(entry.length() - 1))) {
			if (Character.isDigit(d) || isOperator(s)) {
				entry += s;
			}
			else if (s.equals("(")) {
				entry += s;
				parenCount++;
			}
			else if (s.equals(".")) {
				if (hasDecimal(entry) == false) {
					entry += s;
				}
				else {
					return;
				}
			}
			else if (s.equals(")")) {
				entry += s;
				parenCount--;
			}
		}
	
		// If we're on a decimal place
		else if (entry.charAt(entry.length() - 1) == '.') {
			if (Character.isDigit(d)) {
				entry += s;
			}
			else if (entry.length() <= 1) {
				return;
			}
			else if (Character.isDigit(entry.charAt(entry.length() -2 ))) {	
				if (isOperator(s)) {
					entry += s;
				}
				else if (s.equals(")")) {
					entry += s;
					parenCount--;
				}
	    		}
		}
	
	
		// If we're on an open parenthese
		else if (entry.charAt(entry.length() - 1) == '(') {
			if (Character.isDigit(d)) {
				entry += s;
			}
			if (s.equals("(")) {
				entry += s;
				parenCount++;
			}
			else if (s.equals(")")) {
				entry += s;
				parenCount--;
			}
			else if (s.equals("-")) {
				entry += s;
			}
			else if (s.equals(".")) {
				entry += s;
			}
		}
	
		// If we're on a closed parenthese
		else if (entry.charAt(entry.length() - 1) == ')') {
			if (isOperator(s)) {
				entry += s;
			}
			if (s.equals(")")) {
				entry += s;
				parenCount--;
			}
			if (Character.isDigit(d)) {
				entry += s;
			}
			if (s.equals("(")) {
				entry += s;
				parenCount++;
			}
		}
		refresh();
	}

	private boolean hasDecimal(String val) {
		for (int i = val.length() - 1; i >= 0; i--) {
			if (val.charAt(i) == '.') {
				return true;
			}
			else if (isOperator(Character.toString(val.charAt(i)))) {
				return false;
			}
		}
		return false;
	}


	/**
	* Checks if a character is an operator.
	*/
	private boolean isOperator(String s) {
		if (s.equals("*") || s.equals("/") || s.equals("+") || s.equals("-") || s.equals("^")) {
			return true;
		}
		else {
			return false;
		}
	}
    
    
	/**
	* Refresh the display to update it to the current state of the expression
	*/
	public void refresh() {
		display.append(entry);
	}
    
	/**
	* Clear out the expression and refresh the display
	*/
	public void clear() {
		entry = "";
		parenCount = 0;
		resultDisplay.append("Cleared");
		refresh();
	}
    
	/**
	* Delete the rightmost character in the expression. Called by using
	* backspace or clicking the Delete button
	*/
	public void delete() {
		if (entry.equals("")) {
			clear();
			return;
		} 
		else if ((entry.substring(entry.length() - 1)).equals(")")) {
			parenCount++;
		} 
		else if ((entry.substring(entry.length() - 1)).equals("(")) {
			parenCount--;
		}
		entry = entry.substring(0, entry.length() - 1);
		refresh();
	}
    
	/**
	* Operate on the current expression and display the result
	*/
	public void operate() {
		if (entry.length() == 0) {
			return;
		}
		if (!isOperator(entry.substring(entry.length() - 1))) {
			result = evaluate(entry);
			displayResult(result);
		}
	}

	/**
	* Evaluates a String arithmetic expression and returns result
	*/
	public double evaluate(String expression) {
		Stack<Double> values = new Stack<Double>();
		Stack<Character> ops = new Stack<Character>();
		for (int i = 0; i < expression.length(); i++) {
			char curr = expression.charAt(i);
			if (i == 0 && curr == '-' && expression.length() > 1) {
				if (curr == '-' && expression.charAt(i + 1) == '(') {
					values.push(-1.0);
					ops.push('*');
					continue;
				}
				StringBuffer sbuf = new StringBuffer();
				sbuf.append(curr);
				i++;
				curr = expression.charAt(i);
				if (isNumberOrDecimal(curr)) {
					while(i < expression.length() && isNumberOrDecimal(curr)) {
						sbuf.append(curr);
						i++;
						if (i < expression.length()) {
							curr = expression.charAt(i);
						}
					}
					if (i < expression.length()) {
						i--;
					}
					values.push(Double.parseDouble(sbuf.toString()));
				}
			}
			else if (isNumberOrDecimal(curr)) {
				StringBuffer sbuf = new StringBuffer();
				while (i < expression.length() && isNumberOrDecimal(curr)) {
					sbuf.append(curr);
					i++;
					if(i < expression.length()) {
						curr = expression.charAt(i);
					}
				}
				if (i < expression.length()) {
					i--;
				}
				values.push(Double.parseDouble(sbuf.toString()));
			} 
			else if (curr == '(') {
				if (expression.charAt(i + 1) == '-' && expression.charAt(i + 2) == '(') {
					ops.push('(');
					values.push(-1.0);
					ops.push('*');
					i++;
					continue;
				}
				if (i != 0) {
					i--;
					curr = expression.charAt(i);
					if (curr >= '0' && curr <= '9') {
						ops.push('*');
					}
					i++;
				}
				curr = expression.charAt(i);
				ops.push(curr);
				i++;
				curr = expression.charAt(i);
				if (curr == '-') {
					StringBuffer sbuf = new StringBuffer();
					sbuf.append(curr);
					i++;
					curr = expression.charAt(i);
					if (isNumberOrDecimal(curr)) {
                        			while (i < expression.length() && isNumberOrDecimal(curr)){
							sbuf.append(curr);
							i++;
							if (i < expression.length()) { 
								curr = expression.charAt(i);
							}
						}
						if (i < expression.length()) {
							i--;
						}
						values.push(Double.parseDouble(sbuf.toString()));
					}
				}
				else {
					i--;
				}
			}
			else if (isOperator(expression.substring(i, i + 1))) {
				while (!ops.empty() && hasPrecedence(curr, ops.peek())) {
					values.push(applyOp(ops.pop(), values.pop(), values.pop()));
				}
				if (curr == '-' && expression.charAt(i + 1) == '(') {
					if ((i > 0) && (expression.charAt(i - 1) >= '0' && expression.charAt(i - 1) <= '9')) {
						ops.push(curr);
						continue;
					}
					values.push(-1.0);
					ops.push('*');
					continue;
				}
				ops.push(curr);
				i++;
				curr = expression.charAt(i);
				if (curr == '-') {
					if (expression.charAt(i + 1) == '(') {
						values.push(-1.0);
						ops.push('*');
						continue;
					}
					StringBuffer sbuf = new StringBuffer();
					sbuf.append(curr);
					i++;
					curr = expression.charAt(i);
					if (isNumberOrDecimal(curr)) {
						while (i < expression.length() && isNumberOrDecimal(curr)) {
							sbuf.append(curr);
							i++;
							if (i < expression.length()) {
								curr = expression.charAt(i);
							}
						}
						if (i < expression.length()) {
							i--;
						}
						values.push(Double.parseDouble(sbuf.toString()));
					}
				}
				else {
					i--;
				}
			} 
			else if (curr == ')') {
				while (ops.peek() != '(') {
					values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                		}
				ops.pop();
				if (i < expression.length() - 1) {
					i++;
					curr = expression.charAt(i);
					if (isNumberOrDecimal(curr)) {
						ops.push('*');
					}
					if (curr == '(') {
						ops.push('*');
					}
					i--;
				}
			}
		}
		while (!ops.empty()) {
			values.push(applyOp(ops.pop(), values.pop(), values.pop()));
		}
		return values.pop();
	}

	/**
	* Checks if the current character in expression is a number or decimal
	*/
	public boolean isNumberOrDecimal(char c) {
		if ((c >= '0' && c <= '9') || c == '.') {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	* Determines if Operator 1 has precedence over Operator 2
	*/
	public boolean hasPrecedence(char op1, char op2) {
		if (op2 == '(' || op2 == ')') {
			return false;
		}
		if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
			return false;
        	}
		else {
			return true;
		}
	}

	/**
	* Returns the result of an operator applied to two operands
	*/
	public double applyOp(char op, double b, double a) {
		switch (op){
		case '+':
			return a + b;
		case '-':
			return a - b;
		case '*':
			return a * b;
		case '^':
			return Math.pow(a, b);
		case '/':
			if (b == 0) {
				throw new UnsupportedOperationException("Cannot divide by zero");
			}
			return a / b;
		}
		return 0;
	}

	/**
	* Used to evaluate an expression in the form array of strings
	*/
	public String getLeft() {
		String answer = Double.toString(result);
		return answer;
	}
    
	/**
	* Displays result by replacing left String
	* @param Double result to be displayed
	*/
	private void displayResult(double result) {
		displayingResult = true;
		DecimalFormat decimalformat = new DecimalFormat("0.00000000E0");
		result = Double.valueOf(decimalformat.format(result));
		resultDisplay.append(Double.toString(result));
	}
}

