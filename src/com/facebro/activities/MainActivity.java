package com.facebro.activities;
import com.facebook.*;
import com.facebook.model.*;
import com.facebro.facebro.R;

import android.widget.TextView;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {
	
	private static final int LOGIN = 0;
	private static final int LOGGED = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS +1;
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	//Flag para indicar se a activity está visível(true)
	private boolean isResumed = false;
	private UiLifecycleHelper uiHelper;
	private MenuItem settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		 FragmentManager fm = getSupportFragmentManager();
		    fragments[LOGIN] = fm.findFragmentById(R.id.loginFragment);
		    fragments[LOGGED] = fm.findFragmentById(R.id.loggedFragment);
		    fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

		    FragmentTransaction transaction = fm.beginTransaction();
		    for(int i = 0; i < fragments.length; i++) {
		        transaction.hide(fragments[i]);
		    }
		    transaction.commit();
		
				
		// start Facebook Login
		  Session.openActiveSession(this, true, new Session.StatusCallback() {

		    // callback when session changes state
		    @Override
		    public void call(Session session, SessionState state, Exception exception) {

		    	if (session.isOpened()) {
		    		// make request to the /me API
		    		Request.newMeRequest(session, new Request.GraphUserCallback() {

		    		  // callback after Graph API response with user object
		    		  @Override
		    		  public void onCompleted(GraphUser user, Response response) {
		    			  if (user != null) {
		    				  /*TextView welcome = (TextView) findViewById(R.id.welcome);
		    				  welcome.setText("Hello " + user.getName() + "!");*/
		    				  System.out.println(user.getName());
		    				}
		    		  }
		    		}).executeAsync();
		    	}
		    }
		  });
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	    // only add the menu when the selection fragment is showing
	    if (fragments[LOGGED].isVisible()) {
	        if (menu.size() == 0) {
	            settings = menu.add(R.string.loggout);
	        }
	        return true;
	    } else {
	        menu.clear();
	        settings = null;
	    }
	    return false;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(settings)) {
	        showFragment(SETTINGS, true);
	        return true;
	    }
	    return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < fragments.length; i++) {
	        if (i == fragmentIndex) {
	            transaction.show(fragments[i]);
	        } else {
	            transaction.hide(fragments[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    transaction.commit();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    Session session = Session.getActiveSession();

	    if (session != null && session.isOpened()) {
	        // if the session is already open,
	        // try to show the selection fragment
	        showFragment(LOGGED, false);
	    } else {
	        // otherwise present the splash screen
	        // and ask the person to login.
	        showFragment(LOGIN, false);
	    }
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    // Only make changes if the activity is visible
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	            // Se a sessão está aberta:
	            // Mostra o Fragmento para quem está Autenticado (logged)
	            showFragment(LOGGED, false);
	        } else if (state.isClosed()) {
	        	// Se a sessão está fechada:
	            // Mostra o Fragmento para quem está Não-Autenticado (login)
	            showFragment(LOGIN, false);
	        }
	    }
	}

	private Session.StatusCallback callback = 
		    new Session.StatusCallback() {
		    @Override
		    public void call(Session session, 
		            SessionState state, Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
		};
	
}
