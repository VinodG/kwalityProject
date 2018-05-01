package com.winit.alseer.salesman.dataobject;

import java.util.HashMap;
import java.util.Vector;


@SuppressWarnings("serial")
public class ClassificationDO extends BaseObject
{
	public int SellingSKUClassificationId;
	public String Code = "";
	public String Description   = "";
	public int Sequence;
	public HashMap<String, Vector<TrxDetailsDO>> hmProducts;
	public Vector<String> arrBrands=new Vector<String>();
}
