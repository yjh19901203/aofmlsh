package com.sh.mlshcommon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by liuze on 2019/5/22 12:02
 */
public class RandomUtil {

	private static final Logger log= LoggerFactory.getLogger(RandomUtil.class);

	public static int getRandomValue(int min,int max){
		Random random=new Random();
		int randNumber =random.nextInt(max - min + 1) + min;
		return randNumber;
	}
}
