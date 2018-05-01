package com.winit.sfa.salesman;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.winit.kwalitysfa.salesman.R;

public class MapDialogFragment extends DialogFragment
{
	public View rootView;
	public GoogleMap googleMap;
	public EditText et_editcontactperson,et_editcontactnumber,et_editspecialday,et_editlatlang;
	public TextView tv_editdate;
	public Button btn_Saveedit,btn_Canceledit;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	{

	        MapFragment fragment = new MapFragment();
	        FragmentTransaction transaction = getFragmentManager().beginTransaction();
	        transaction.add(R.id.mapView, fragment).commit();
	        rootView = inflater.inflate(R.layout.chekin_edit_popup, container, false);

	        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
	        
	         et_editcontactperson = (EditText)rootView.findViewById(R.id.et_editcontactperson);
		     et_editcontactnumber = (EditText)rootView.findViewById(R.id.et_editcontactnumber);
		     et_editspecialday = (EditText)rootView.findViewById(R.id.et_editspecialday);
		     et_editlatlang = (EditText)rootView.findViewById(R.id.et_editlatlang);
		    tv_editdate = (TextView)rootView.findViewById(R.id.tv_editdate);
		     btn_Saveedit = (Button)rootView.findViewById(R.id.btn_Saveedit);
		     btn_Canceledit = (Button)rootView.findViewById(R.id.btn_Canceledit);
	        
	        
	        return rootView;

	}

}
