package com.yihecloude.hacker.HackerTopic2;

import java.util.Scanner;

public class Topic2 {
	public static void main(String[] args) {
		Topic2 t = new Topic2();
		
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入开始时间(hh:mm:ss)=");
		String lineStart = sc.nextLine();
		System.out.println("请输入结束时间(hh:mm:ss)=");
		String lineEnd = sc.nextLine();
		System.out.println(t.getDiffTime(lineStart, lineEnd));
	}

	
	@SuppressWarnings("unused")
	private int getDiffTime(String startTime,String endTime){
		String [] startTimeArr = startTime.split(":");
		String [] endTimeArr = endTime.split(":");
		int a = (Integer.parseInt(endTimeArr[0]) - Integer.parseInt(startTimeArr[0]));//整点的重合次数
		int b = Integer.parseInt(endTimeArr[1])/5 - Integer.parseInt(endTimeArr[0]) >= 0?1:0;
		int c = Integer.parseInt(startTimeArr[1])/5 - Integer.parseInt(startTimeArr[0]) > 0?-1:0;
		return a+b+c;
	}
}
