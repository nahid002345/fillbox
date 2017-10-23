package com.nahid.diordna.fillbox;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView.CommaTokenizer;
import android.widget.TableRow;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class ChoosePlayer extends  Activity {
	AdView adView;
	TableRow trblue;
	TableRow trred;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_player);
		setEventListener();
		AdLoad();
	}

	private void AdLoad(){
		MobileAds.initialize(getApplicationContext(),getResources().getString(R.string.ad_banner_id));
		AdRequest adRequest = new AdRequest.Builder().build();
		adView = (AdView)this.findViewById(R.id.adView);
		adView.loadAd(adRequest);
		adView.setVisibility(View.INVISIBLE);
		adView.setAdListener(new AdListener() {

			@Override
			public void onAdLoaded() {
				// TODO Auto-generated method stub
				super.onAdLoaded();
				adView.setVisibility(View.VISIBLE);
			}
		});
	}

	public void setEventListener() {
		 
		trblue= (TableRow) findViewById(R.id.trBlue);
		trred = (TableRow) findViewById(R.id.trRed);
		
		trblue.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v1) {
				MainActivity.PlayerMove=false;
			  Intent browserIntent = new Intent("android.intent.action.Game");
			    startActivity(browserIntent); 
			} 
		});	
		
		trred.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v1) {
				MainActivity.PlayerMove=true;
			  Intent browserIntent = new Intent("android.intent.action.Game");
			    startActivity(browserIntent); 
			} 
		});	
 
	}
}
