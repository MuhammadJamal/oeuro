package com.example.goeuro.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.goeuro.connection.Downloader;
import com.example.goeuro.model.Position;
import com.example.goeuro.model.UserLocation;

public class SuggestionProvider extends ContentProvider {

	public static final String AUTHORITY = "com.example.goeuro.search_suggestion_provider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/search");

	// UriMatcher constant for search suggestions
	private static final int SEARCH_SUGGEST = 1;

	private static final UriMatcher uriMatcher;

	private static final String[] SEARCH_SUGGEST_COLUMNS = { BaseColumns._ID,
			SearchManager.SUGGEST_COLUMN_TEXT_1,
			SearchManager.SUGGEST_COLUMN_INTENT_DATA };

	private static final String URL = "http://pre.dev.goeuro.de:12345/api/v1/suggest/position/en/name/";

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY,
				SEARCH_SUGGEST);
		uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY
				+ "/*", SEARCH_SUGGEST);
	}

	@Override
	public int delete(Uri uri, String arg1, String[] arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			return SearchManager.SUGGEST_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Use the UriMatcher to see what kind of query we have
		switch (uriMatcher.match(uri)) {
		case SEARCH_SUGGEST:

			String query = uri.getLastPathSegment();
			MatrixCursor cursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS, 1);
			Downloader downloader = new Downloader();

			try {
				String string = downloader.downloadUrl(URL + query);

				List<Position> positions = extractSortPostions(string);

				for (Position position : positions) {
					cursor.addRow(new String[] { position.getId(),
							position.getName(), position.getName() });
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return cursor;
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues arg1, String arg2, String[] arg3) {
		throw new UnsupportedOperationException();
	}

	private List<Position> extractSortPostions(String string)
			throws JSONException {
		ArrayList<Position> positions = new ArrayList<Position>();
		JSONObject parentJsonObject = new JSONObject(string);

		JSONArray resultsArray = parentJsonObject.getJSONArray("results");

		for (int i = 0; i < resultsArray.length(); i++) {
			JSONObject positionObject = resultsArray.getJSONObject(i);
			JSONObject geoObject = positionObject.getJSONObject("geo_position");
			float dis = getDistance(geoObject.getDouble("latitude"),
					geoObject.getDouble("longitude"), UserLocation
							.getLocation().getLatitude(), UserLocation
							.getLocation().getLongitude());
			Position position = new Position(positionObject.getString("_id"),
					positionObject.getString("name"), dis);
			positions.add(position);
		}
		Collections.sort(positions);
		return positions;
	}

	private float getDistance(double startLatitude, double startLongitude,
			double endLatitude, double endLongitude) {
		float[] results = new float[3];
		Location.distanceBetween(startLatitude, startLongitude, endLatitude,
				endLongitude, results);
		return results[0];
	}
}