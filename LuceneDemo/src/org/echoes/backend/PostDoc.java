package org.echoes.backend;

public class PostDoc extends LuceneDoc {

	protected String story;
	public static final String STORY = "story";
	
	public PostDoc(String id, String from, String to, long created_time,
			String message, String story) {
		super(id, from, to, created_time, message);
		this.story = story;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

}
