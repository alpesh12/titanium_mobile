/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package org.appcelerator.titanium;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.kroll.common.TiFastDev;
import org.appcelerator.titanium.util.TiActivitySupport;
import org.appcelerator.titanium.util.TiRHelper;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class TiRootActivity extends TiLaunchActivity
	implements TiActivitySupport
{
	private static final String LCAT = "TiRootActivity";
	private static final boolean DBG = TiConfig.LOGD;

	@Override
	public String getUrl()
	{
		return "app.js";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		TiApplication tiApp = getTiApp();

		if (checkMissingLauncher(savedInstanceState)) {
			// Android bug 2373 detected and we're going to restart.
			return;
		}

		if (tiApp.isRestartPending() || TiBaseActivity.isUnsupportedReLaunch(this, savedInstanceState)) {
			super.onCreate(savedInstanceState); // Will take care of scheduling restart and finishing.
			return;
		}

		tiApp.setCurrentActivity(this, this);

		Log.checkpoint(LCAT, "checkpoint, on root activity create, savedInstanceState: " + savedInstanceState);

		tiApp.setRootActivity(this);

		super.onCreate(savedInstanceState);

		tiApp.verifyCustomModules(this);
	}

	@Override
	protected void windowCreated()
	{
		// Use settings from tiapp.xml
		ITiAppInfo appInfo = getTiApp().getAppInfo();
		getIntent().putExtra(TiC.PROPERTY_FULLSCREEN, appInfo.isFullscreen());
		getIntent().putExtra(TiC.PROPERTY_NAV_BAR_HIDDEN, appInfo.isNavBarHidden());
		super.windowCreated();
	}

	// Lifecyle

	@Override
	protected void onResume()
	{
		Log.checkpoint(LCAT, "checkpoint, on root activity resume. activity = " + this);
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		try {
			int backgroundId = TiRHelper.getResource("drawable.background");
			if (backgroundId != 0) {
				Drawable d = this.getResources().getDrawable(backgroundId);
				if (d != null) {
					Drawable bg = getWindow().getDecorView().getBackground();
					getWindow().setBackgroundDrawable(d);
					bg.setCallback(null);
				}
			}
		} catch (Exception e) {
			Log.e(LCAT, "Resource not found 'drawable.background': " + e.getMessage());
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (DBG) {
			Log.d(LCAT, "root activity onDestroy, activity = " + this);
		}
		TiFastDev.onDestroy();
	}

}
