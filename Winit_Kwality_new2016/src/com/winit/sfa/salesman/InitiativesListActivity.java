package com.winit.sfa.salesman;

import java.util.Vector;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.InitiativesAdapter;
import com.winit.alseer.salesman.dataaccesslayer.InitiativesDA;
import com.winit.alseer.salesman.dataobject.InitiativeDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.kwalitysfa.salesman.R;

public class InitiativesListActivity extends BaseActivity
{
	private LinearLayout llInitiatives;
	private EditText etSearch;
	private ListView lvInitiatives;
	private TextView tvNodata;
	private Vector<InitiativeDO> vecInitiativeDOs;
	private InitiativesAdapter initiativesAdapter;
	private JourneyPlanDO journeyPlanDO;
	
	private ImageView ivSearchCross;
	
	@Override
	public void initialize() 
	{
		llInitiatives = (LinearLayout)inflater.inflate(R.layout.initiativeslist, null);
		llBody.addView(llInitiatives,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		initializeControls();
		
		if(getIntent().getExtras() != null)
		{
			journeyPlanDO = (JourneyPlanDO) getIntent().getExtras().get("Object");
		}
		
		loaddata();
		
ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
			}
		});
	}
	
	private void initializeControls()
	{
		etSearch = (EditText)llInitiatives.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llInitiatives.findViewById(R.id.ivSearchCross);
		lvInitiatives = (ListView)llInitiatives.findViewById(R.id.lvInitiatives);
		tvNodata = (TextView)llInitiatives.findViewById(R.id.tvNodata);
		
		initiativesAdapter = new InitiativesAdapter(InitiativesListActivity.this, vecInitiativeDOs,journeyPlanDO);
		lvInitiatives.setAdapter(initiativesAdapter);
	}

	private void loaddata()
	{
		Vector<String> vecInitiativeIds = new InitiativesDA().getAllInitiativesIdsForCustomer(journeyPlanDO.site);
		vecInitiativeDOs = new Vector<InitiativeDO>();
		if(vecInitiativeIds.size() > 0)
		{
			for (int i = 0; i < vecInitiativeIds.size(); i++) 
			{
				InitiativeDO initiativeDO = new InitiativesDA().getInitiativesById(vecInitiativeIds.get(i).toString());
				initiativeDO.Status = new InitiativesDA().getInitiativesStatus(journeyPlanDO.site,initiativeDO.InitiativeId);
				initiativeDO.initiativeTradePlanImage = new InitiativesDA().getInitiativesTradePlanId(journeyPlanDO.site,initiativeDO.InitiativeId);
				vecInitiativeDOs.add(initiativeDO);
			}
		}
		
//		vecInitiativeDOs = new InitiativesDA().getAllInitiatives();
		
		if(vecInitiativeDOs.size() > 0)
		{
			lvInitiatives.setVisibility(View.VISIBLE);
			tvNodata.setVisibility(View.GONE);
			
			if(initiativesAdapter != null)
				initiativesAdapter.refreshAdapter(vecInitiativeDOs,journeyPlanDO);
		}
		else
		{
			lvInitiatives.setVisibility(View.GONE);
			tvNodata.setVisibility(View.VISIBLE);
		}
		
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		loaddata();
	}
}
