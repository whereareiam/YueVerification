package me.whereareiam.yuiverification.model.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VerificationMessages {
	private Channel channel;
	private Steps steps;
	private Timeout timeout;
	private Command command;

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
		private AdditionalLanguage additionalLanguage;
		private End end;

		@Getter
		@Setter
		public static class Welcome {
			private String title;
			private List<String> description;
		}

		@Getter
		@Setter
		public static class AdditionalLanguage {
			private String title;
			private List<String> description;
		}

		@Getter
		@Setter
		public static class End {
			private String title;
			private List<String> description;
		}
	}

	@Getter
	@Setter
	public static class Timeout {
		private String kickReason;
	}

	@Getter
	@Setter
	public static class Command {
		private Verify verify;

		@Getter
		@Setter
		public static class Verify {
			private String description;
			private String example;
			private Variables variables;
			private Success success;
			private Error error;

			@Getter
			@Setter
			public static class Variables {
				private String user;
			}

			@Getter
			@Setter
			public static class Success {
				private String title;
				private List<String> description;
			}

			@Getter
			@Setter
			public static class Error {
				private NotFound notFound;

				@Getter
				@Setter
				public static class NotFound {
					private String title;
					private List<String> description;
				}
			}
		}
	}
}
