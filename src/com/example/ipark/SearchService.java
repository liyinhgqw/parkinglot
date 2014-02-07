package com.example.ipark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class SearchService {

	public SearchService() {

	}

	public String searchParking(String where, double dist, int hour, int mins) {
		Log.i("**", where);
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet();
			// String url = buildUrl(Constants.API_SEARCH_METHOD, new
			// String[]{"ids="},
			// new
			// String[]{"f8b78cf497fbcd02b5147f01,fd8826e6dab27c560b1cc77e"});
			String url = buildUrl(Constants.API_PATH, new String[] { "location", "&dis" },
					new String[] { where, "" + dist });

			Log.i("**", url);
			get.setURI(new URI(url));
			HttpResponse response = client.execute(get);

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String data = stream2String(entity.getContent());
				return data;
			}

		} catch (Exception ex) {
			Log.i("exception", ex.getMessage());
		}

		return null;

	}

	private String stream2String(InputStream is) {
		StringBuilder sb = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line = "";
		try {
			while ((line = rd.readLine()) != null)
				sb.append(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();
	}

	private String buildUrl(String path, String[] args, String[] argsVal) {
		String ret = null;
		ret = Constants.API_BASEURL + path;

		for (int i = 0; i < args.length; i++) {
			if (i == 0) {
				ret += "?" + args[i] + "=" + argsVal[i];
			} else {
				ret += "&" + args[i] + "=" + argsVal[i];
			}
		}

		return ret;
	}

}
