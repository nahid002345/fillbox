package com.nahid.diordna.fillbox;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class HomeScreen extends Activity {
	private AdView adView;

	Button buttonstrt;
	Button btnSetting;
	Button btnExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addEventListener();
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
	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(
				HomeScreen.this);
		alertbox.setTitle("Warning");
		// alertbox.setIcon(R.drawable.info);
		alertbox.setMessage("Do you want to exit application?");
		alertbox.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
		alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.cancel();
			}
		});
		alertbox.show();
	}
    
	public void addEventListener() {
		 
		buttonstrt = (Button) findViewById(R.id.btnStart);
		btnSetting = (Button) findViewById(R.id.btnSetting);
		btnExit = (Button) findViewById(R.id.btnExit);
		
		buttonstrt.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v1) {
//				showPopupMenu(v1);
			  Intent browserIntent = new Intent("android.intent.action.PlayerType");
			    startActivity(browserIntent); 
			} 
		});	
 
		btnSetting.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v1) {
//				showPopupMenu(v1);
				  Intent browserIntent = new Intent(HomeScreen.this, Settings.class);
				    startActivity(browserIntent); 
			} 
			
		});	
		btnExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				System.exit(0);

			}
		});
 
	}
	
//	  private void showPopupMenu(View v){
//		   PopupMenu popupMenu = new PopupMenu(HomeScreen.this, v);
//		   popupMenu.getMenuInflater().inflate(R.menu.settings, popupMenu.getMenu());
//		    
//	      popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {		   
//		   public boolean onMenuItemClick(android.view.MenuItem item) {
//		    Toast.makeText(HomeScreen.this,
//		      item.toString(),
//		      Toast.LENGTH_LONG).show();
//		    return true;
//		   }
//	      });
//
//		      popupMenu.show();
//		  }
//	  
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.settings, menu);
	        return true;
	    }
    
  

}
