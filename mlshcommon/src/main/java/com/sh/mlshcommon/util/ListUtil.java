package com.sh.mlshcommon.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 集合操作常用方法类.
 * Created by liuze on 2019/5/22 11:53
 */
public class ListUtil {

	/**
	 * 判断List不为空,非空返回true,空则返回false
	 *
	 * @param list
	 * @return boolean
	 */
	public static boolean isNotNull(List<?> list) {

		if (null != list) {
			if ((list.size() > 0) && !list.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断List是为空,为空返回true,非空则返回false
	 *
	 * @param list
	 * @return boolean
	 */
	public static boolean isNull(List<?> list) {

		if (null == list || list.size() == 0 || list.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * @Title: removeDuplist @date 2016年7月17日 下午3:55:38 @Description:
	 * 去除集合中重复的内容 @param list @return @throws
	 */
	public static List<String> removeDuplist(List<String> list) {
		if (list != null && list.size() > 0) {
			HashSet<String> hashSet = new HashSet<String>(list);
			list.clear();
			list.addAll(hashSet);
		}
		return list;
	}

	/**
	 *
	 * @Title: removeDuplistInt @date 2016年7月18日 下午5:16:04 @Description:
	 * 去除重复的值 @param list @return @throws
	 */
	public static List<Integer> removeDuplistInt(List<Integer> list) {
		if (list != null && list.size() > 0) {
			HashSet<Integer> hashSet = new HashSet<Integer>(list);
			list.clear();
			list.addAll(hashSet);
		}
		return list;
	}

	/**
	 *
	 * @Title: getCurveValue @date 2016年8月16日 下午4:48:33 @Description:
	 * 计算集合中的最大值和最小值， 返回改后的最大值，间隔，最小值 @param list @return @throws
	 */
	public static List<Long> getCurveValue(List<Long> list) {
		List<Long> curveList = new ArrayList<Long>();

		Long maxValue = 0l;// 集合中最大值
		Long avgValue = 0l;// 间隔值
		Long minValue = 0l;// 最小值

		if (ListUtil.isNotNull(list)) {
			maxValue = Collections.max(list) / 100;
		}

		if (maxValue > 10000) {
			maxValue += 1000;
		} else if (maxValue > 1000) {
			maxValue += 300;
		} else if (maxValue > 100) {
			maxValue += 50;
		} else if (maxValue > 10) {
			maxValue += 5;
		} else {
			maxValue = 10l;
		}

		avgValue = maxValue / 5;

		curveList.add(maxValue);
		curveList.add(avgValue);
		curveList.add(minValue);

		return curveList;
	}


	/**
	 * list集合深度复制
	 * @param src
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		@SuppressWarnings("unchecked")
		List<T> dest = (List<T>) in.readObject();
		return dest;
	}

	/**
	 *对列表字段进行比较排序
	 */
	public static <T> void sortByField(List<T> dtoList, final String fieldName, String order) {
		int compare=1;
		if ("desc".equals(order)){
			compare=-1;
		}
		final int finalCompare = compare;

		Collections.sort(dtoList, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				PropertyDescriptor pd1 = null;
				PropertyDescriptor pd2 = null;
				Object value1 =null;
				Object value2 =null;
				try {
					pd1 = new PropertyDescriptor(fieldName, o1.getClass());
					value1 = pd1.getReadMethod().invoke(o1, null);

					pd2 = new PropertyDescriptor(fieldName, o2.getClass());
					value2 = pd2.getReadMethod().invoke(o2, null);

				} catch (IntrospectionException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}

				if (value1.getClass().equals(Double.class)){
					if ((Double)value1 > (Double)value2) {
						return finalCompare;
					} else if ((Double)value1 < (Double)value2) {
						return -finalCompare;
					}
				}else if (value1.getClass().equals(Integer.class)){
					if ((Integer)value1 > (Integer)value2) {
						return finalCompare;
					} else if ((Integer)value1 < (Integer)value2) {
						return -finalCompare;
					}
				}
				return 0;
			}
		});
	}

}