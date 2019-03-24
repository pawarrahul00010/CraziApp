package com.technohertz.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {

public LocalDateTime getDate() {
		
	    LocalDateTime now = LocalDateTime.now();  
		   
		return now;
		
	}

public Timestamp getTimestampDate()
{
	Date date= new Date();
	 
	 long time = date.getTime();
	 Timestamp ts = new Timestamp(time);
	 System.out.println(ts);
	 return ts;
	}
}
