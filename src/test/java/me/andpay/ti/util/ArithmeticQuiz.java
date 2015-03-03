package me.andpay.ti.util;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 小学算术测试题生成器类。
 * 
 * @author sea.bao
 */
public class ArithmeticQuiz {
	private static int genDigit(Random rnd, int min, int max) {
		return min + rnd.nextInt(max-min);
	}
	
	private static String formatDigit(int d, int size) {
		StringBuffer str = new StringBuffer();
		str.append(d);
		while(str.length() < size) {
			str.append(" ");
		}
		
		return str.toString();
	}
	
	public static void main(String[] args) {
		Set<String> set = new HashSet<String>();
		int addItemCount = 20, subItemCount = 15, mulItemCount = 10, divItemCount = 15;
		File fileQuiz = new File("/tmp/quiz");
		File fileQuizWithAns = new File("/tmp/quiz-ans");
		fileQuiz.delete();
		fileQuizWithAns.delete();
		
		Random rnd = new Random(System.currentTimeMillis());
		for ( int i=0; i < addItemCount; ) {
			int a = genDigit(rnd, 100, 1000);
			int b = genDigit(rnd, 100, 1000);
			int s = a + b;
			StringBuffer line = new StringBuffer();
			line.append(formatDigit(a, 4));
			line.append(" + ");
			line.append(formatDigit(b, 4));
			line.append(" = ");
			
			if (set.contains(line.toString())) {
				continue;
			} else {
				set.add(line.toString());
				i++;
			}
			
			FileUtil.appendFile(fileQuiz, line.toString()+"\n");
			line.append(s);
			FileUtil.appendFile(fileQuizWithAns, line.toString()+"\n");
		}
		
		set.clear();
		for ( int i=0; i < subItemCount; ) {
			int a = genDigit(rnd, 500, 1000);
			int b = genDigit(rnd, 1, 500);
			int s = a - b;
			StringBuffer line = new StringBuffer();
			line.append(formatDigit(a, 4));
			line.append(" - ");
			line.append(formatDigit(b, 4));
			line.append(" = ");
			
			if (set.contains(line.toString())) {
				continue;
			} else {
				set.add(line.toString());
				i++;
			}
			
			FileUtil.appendFile(fileQuiz, line.toString()+"\n");
			line.append(s);
			FileUtil.appendFile(fileQuizWithAns, line.toString()+"\n");
		}
		
		set.clear();
		for ( int i=0; i < mulItemCount; ) {
			int a = genDigit(rnd, 100, 1000);
			int b = genDigit(rnd, 2, 10);
			int s = a * b;
			StringBuffer line = new StringBuffer();
			line.append(formatDigit(a, 4));
			line.append(" x ");
			line.append(formatDigit(b, 2));
			line.append(" = ");
			
			if (set.contains(line.toString())) {
				continue;
			} else {
				set.add(line.toString());
				i++;
			}
			
			FileUtil.appendFile(fileQuiz, line.toString()+"\n");
			line.append(s);
			FileUtil.appendFile(fileQuizWithAns, line.toString()+"\n");
		}
		
		set.clear();
		for ( int i=0; i < divItemCount; ) {
			int a = genDigit(rnd, 100, 300);
			int b = genDigit(rnd, 2, 10);
			int s = a * b;
			if ( s >= 1000 ) {
				continue;
			}
			
			StringBuffer line = new StringBuffer();
			line.append(formatDigit(s, 4));
			line.append(" / ");
			line.append(formatDigit(b, 2));
			line.append(" = ");
			
			if (set.contains(line.toString())) {
				continue;
			} else {
				set.add(line.toString());
				i++;
			}
			
			FileUtil.appendFile(fileQuiz, line.toString()+"\n");
			line.append(a);
			FileUtil.appendFile(fileQuizWithAns, line.toString()+"\n");
		}
	}

}
