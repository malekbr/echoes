package org.echoes.backend;

public class PostDoc extends LuceneDoc {

	protected String story;
	public static final String STORY = "story", TYPE = "PostDoc";
	
	public PostDoc(String id, String from, String to, long created_time,
			String message, String story) {
		super(id, from, to, created_time, message);
		this.story = story;
		this.type = PostDoc.TYPE;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

}
