package me.whereareiam.yuiverification.model.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VerificationMessages {
	private Channel channel;
	private PrivateMessage privateMessage;
	private Steps steps;
	private Timeout timeout;
	private Command command;
	private Audit audit;

	@Getter
	@Setter
	public static class Channel {
		private String name;
		private String description;
		private String message;
	}

	@Getter
	@Setter
	public static class PrivateMessage {
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

	@Getter
	@Setter
	public static class Audit {
		private Started started;
		private Completed completed;
		private Step step;
		private Timeout timeout;
		private Abandoned abandoned;
		private Failed failed;
		private Welcome welcome;

		@Getter
		@Setter
		public static class Started {
			private Auto auto;
			private Manual manual;

			@Getter
			@Setter
			public static class Auto {
				private String title;
				private List<String> description;
				private Fields fields;

				@Getter
				@Setter
				public static class Fields {
					private String target;
					private String method;
				}
			}

			@Getter
			@Setter
			public static class Manual {
				private String title;
				private List<String> description;
				private Fields fields;

				@Getter
				@Setter
				public static class Fields {
					private String target;
					private String initiator;
					private String method;
				}
			}
		}

		@Getter
		@Setter
		public static class Completed {
			private String title;
			private List<String> description;
			private Fields fields;

			@Getter
			@Setter
			public static class Fields {
				private String target;
				private String duration;
				private String method;
			}
		}

		@Getter
		@Setter
		public static class Step {
			private Completed completed;

			@Getter
			@Setter
			public static class Completed {
				private String title;
				private List<String> description;
				private Fields fields;

				@Getter
				@Setter
				public static class Fields {
					private String target;
					private String stepName;
				}
			}
		}

		@Getter
		@Setter
		public static class Timeout {
			private String title;
			private List<String> description;
			private Fields fields;

			@Getter
			@Setter
			public static class Fields {
				private String target;
				private String timeLimit;
				private String timeSpent;
			}
		}

		@Getter
		@Setter
		public static class Abandoned {
			private String title;
			private List<String> description;
			private Fields fields;

			@Getter
			@Setter
			public static class Fields {
				private String target;
				private String currentStep;
			}
		}

		@Getter
		@Setter
		public static class Failed {
			private String title;
			private List<String> description;
			private Fields fields;

			@Getter
			@Setter
			public static class Fields {
				private String target;
				private String error;
			}
		}

		@Getter
		@Setter
		public static class Welcome {
			private String title;
			private List<String> description;
			private Fields fields;

			@Getter
			@Setter
			public static class Fields {
				private String target;
				private String time;
			}
		}
	}
}
