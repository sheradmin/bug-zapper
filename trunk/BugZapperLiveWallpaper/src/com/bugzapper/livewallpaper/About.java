package com.bugzapper.livewallpaper;

/**
 * Created by IntelliJ IDEA.
 * User: sher
 * Date: 6/29/11
 * Time: 12:41 AM
 * To change this template use File | Settings | File Templates.
 */
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class About extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		WebView view = (WebView)findViewById(R.id.text);
		String data = "<html><head></head><body>A country home setting with a bug zapper. Interactive parts: Owl, zapper and the moon.</body></html>";
		view.loadData(data, "text/xml", "UTF-8");
	}
}

