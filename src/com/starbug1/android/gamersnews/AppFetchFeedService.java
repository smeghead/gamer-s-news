package com.starbug1.android.gamersnews;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.starbug1.android.gamersnews.R;
import com.starbug1.android.newsapp.FetchFeedService;
import com.starbug1.android.newsapp.data.NewsListItem;
import com.starbug1.android.newsapp.utils.UrlUtils;

public class AppFetchFeedService extends FetchFeedService {
	private static final String TAG = "AppFetchFeedService";

	private final Pattern imageUrl_ = Pattern.compile("<img.*?src=\"([^\"]*)\"", Pattern.MULTILINE);
	private Pattern fourgamerContent_ = Pattern.compile("table class=\"img_right_top\"(.*)SOCIALBOOKMARK_BOTTOM_BAR", Pattern.DOTALL);
	private Pattern famitsuContent_ = Pattern.compile("div id=\"textArea\"(.*)/table", Pattern.DOTALL);

	@Override
	protected List<Feed> getFeeds() {
		List<Feed> feeds = new ArrayList<Feed>();
		
		feeds.add(new Feed("4gamer", "http://www.4gamer.net/rss/index.xml") {

			@Override
			public String getImageUrl(String content, NewsListItem item) {
				Matcher m = fourgamerContent_.matcher(content);
				if (!m.find()) {
					return null;
				}
				String mainPart = m.group(1);
				m = imageUrl_.matcher(mainPart);
				if (!m.find()) {
					return null;
				}
				String imageUrl = m.group(1);
				if (imageUrl.startsWith("/")) {
					imageUrl = UrlUtils.findSchemaDomain(item.getLink()) + imageUrl;
				}
				return imageUrl;
			}
			
		});
		feeds.add(new Feed("famitsu", "http://www.famitsu.com/rss/fcom_all.rdf") {

			@Override
			public String getImageUrl(String content, NewsListItem item) {
				Matcher m = famitsuContent_.matcher(content);
				if (!m.find()) {
					return null;
				}
				String mainPart = m.group(1);
				m = imageUrl_.matcher(mainPart);
				if (!m.find()) {
					return null;
				}
				String imageUrl = m.group(1);
				if (imageUrl.startsWith("/")) {
					imageUrl = UrlUtils.findSchemaDomain(item.getLink()) + imageUrl;
				}
				return imageUrl;
			}
			
		});
		
		return feeds;
	}
	
	@Override
	public void onCreate() {
		com.starbug1.android.newsapp.utils.ResourceProxy.R.init(R.class);

		super.onCreate();
	}

	@Override
	protected boolean isValidItem(NewsListItem item) {
		if (item.getSource().equals("famitsu")) {
			if (item.getLink().toString().indexOf("/news/") != -1) {
				return true;
			}
			return false;
		}
		return super.isValidItem(item);
	}
	
	
}
