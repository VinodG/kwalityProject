package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.Collection;

import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.utilities.StringUtils;

public class BaseDA 
{
	public void getTotalUnits(ProductDO objProduct)
	{
		objProduct.totalCases = StringUtils.getFloat(objProduct.preCases) + StringUtils.getFloat(objProduct.preUnits)/objProduct.UnitsPerCases;
		objProduct.totalUnits = Math.round(StringUtils.getFloat(objProduct.preUnits) + Math.abs(StringUtils.getFloat(objProduct.preCases)*objProduct.UnitsPerCases));
	}
	
	public static <T> Collection<T> filter(Collection<T> col,
			   Predicate<T> predicate) {
			  Collection<T> result = new ArrayList<T>();
			  for (T element : col) {
			   if (predicate.apply(element)) {
			    result.add(element);
			   }
			  }
			  return result;
			 }
	
	public interface Predicate<T> {
		  boolean apply(T type);
		 }
}
