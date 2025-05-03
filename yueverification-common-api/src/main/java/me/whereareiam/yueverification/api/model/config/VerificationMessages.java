package me.whereareiam.yueverification.api.model.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VerificationMessages {
	private Channel channel;
	private Steps steps;

	@Getter
	@Setter
	public static class Channel {
		private String name;
		private String description;
		private String message;
	}

	@Getter
	@Setter
	public static class Steps {
		private Welcome welcome;

		@Getter
		@Setter
		public static class Welcome {
			private String title;
			private List<String> description;
		}
	}
}
