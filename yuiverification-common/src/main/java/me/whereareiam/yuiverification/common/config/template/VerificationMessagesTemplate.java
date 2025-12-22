package me.whereareiam.yuiverification.common.config.template;

import me.whereareiam.yui.localization.format.FileFormat;
import me.whereareiam.yui.localization.format.FileFormats;
import me.whereareiam.yui.localization.provider.LocalizationProvider;
import me.whereareiam.yuiverification.model.config.VerificationMessages;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VerificationMessagesTemplate implements LocalizationProvider<VerificationMessages> {
	@Override
	public Class<VerificationMessages> getModelClass() {
		return VerificationMessages.class;
	}

	@Override
	public FileFormat getFormat() {
		return FileFormats.LOCALE;
	}

	@Override
	public VerificationMessages supply(VerificationMessages messages) {
		// Channel
		VerificationMessages.Channel channel = new VerificationMessages.Channel();
		channel.setName("verification-<p:username>");
		channel.setDescription("Temporary verification channel");
		channel.setMessage("A temporary verification channel was created for you. Please go through all the steps and verify yourself in the message that will appear in the channel.");
		messages.setChannel(channel);

		// Steps
		VerificationMessages.Steps steps = new VerificationMessages.Steps();
		// Steps → Welcome
		VerificationMessages.Steps.Welcome welcome = new VerificationMessages.Steps.Welcome();
		welcome.setTitle("Welcome to the Discord-Server");
		welcome.setDescription(List.of("Welcome <p:mention>, before you can start chatting and having fun on our Discord server, you need to take a few steps.", "", "Please select your primary language by clicking one of the buttons below."));
		steps.setWelcome(welcome);

		// Steps → Additional Language
		VerificationMessages.Steps.AdditionalLanguage additionalLanguage = new VerificationMessages.Steps.AdditionalLanguage();
		additionalLanguage.setTitle("Select Additional Languages & Channels");
		additionalLanguage.setDescription(List.of("Choose any additional languages you understand. These will be used for translations if your primary language is not available.", "", "For each selected language, a dedicated chat channel will be added, allowing you to connect with others in that language.", "Your primary language will also have its own channel automatically."));
		steps.setAdditionalLanguage(additionalLanguage);

		// Steps → End
		VerificationMessages.Steps.End end = new VerificationMessages.Steps.End();
		end.setTitle("You're All Set!");
		end.setDescription(List.of("Congratulations <p:mention>, you have completed the verification process!", "", "You can now join the conversation and chat with others on our Discord server.", "Before you start, please make sure you have read and understood our rules in the <p:rulesChannel> channel.", "", "Enjoy your time here!"));
		steps.setEnd(end);

		messages.setSteps(steps);

		// Timeout
		VerificationMessages.Timeout timeout = new VerificationMessages.Timeout();
		timeout.setKickReason("Failed to complete verification within the time limit");
		messages.setTimeout(timeout);

		// Command
		VerificationMessages.Command command = new VerificationMessages.Command();
		VerificationMessages.Command.Verify verify = new VerificationMessages.Command.Verify();
		verify.setDescription("Manually start verification process for a user");
		verify.setExample("/yui verify @user");

		VerificationMessages.Command.Verify.Variables variables = new VerificationMessages.Command.Verify.Variables();
		variables.setUser("The Discord user to verify");
		verify.setVariables(variables);

		VerificationMessages.Command.Verify.Success success = new VerificationMessages.Command.Verify.Success();
		success.setTitle("Verification Started");
		success.setDescription(List.of(
				"Successfully started verification process for {user}."
		));
		verify.setSuccess(success);

		VerificationMessages.Command.Verify.Error error = new VerificationMessages.Command.Verify.Error();
		VerificationMessages.Command.Verify.Error.NotFound notFound = new VerificationMessages.Command.Verify.Error.NotFound();
		notFound.setTitle("User Not Found");
		notFound.setDescription(List.of(
				"Could not find user {user} in the system."
		));
		error.setNotFound(notFound);
		verify.setError(error);

		command.setVerify(verify);
		messages.setCommand(command);

		// Audit
		VerificationMessages.Audit audit = new VerificationMessages.Audit();
		
		// Audit → Started
		VerificationMessages.Audit.Started started = new VerificationMessages.Audit.Started();
		
		// Audit → Started → Auto
		VerificationMessages.Audit.Started.Auto startedAuto = new VerificationMessages.Audit.Started.Auto();
		startedAuto.setTitle("🔐 Verification Started");
		startedAuto.setDescription(List.of("User <p:mention> began the verification process"));
		VerificationMessages.Audit.Started.Auto.Fields startedAutoFields = new VerificationMessages.Audit.Started.Auto.Fields();
		startedAutoFields.setTarget("Target");
		startedAutoFields.setMethod("Method");
		startedAuto.setFields(startedAutoFields);
		started.setAuto(startedAuto);

		// Audit → Started → Manual
		VerificationMessages.Audit.Started.Manual startedManual = new VerificationMessages.Audit.Started.Manual();
		startedManual.setTitle("🔐 Verification Started (Manual)");
		startedManual.setDescription(List.of("Verification manually started for <p:mention>"));
		VerificationMessages.Audit.Started.Manual.Fields startedManualFields = new VerificationMessages.Audit.Started.Manual.Fields();
		startedManualFields.setTarget("Target");
		startedManualFields.setInitiator("Initiated By");
		startedManualFields.setMethod("Method");
		startedManual.setFields(startedManualFields);
		started.setManual(startedManual);
		
		audit.setStarted(started);

		// Audit → Completed
		VerificationMessages.Audit.Completed completed = new VerificationMessages.Audit.Completed();
		completed.setTitle("✅ Verification Completed");
		completed.setDescription(List.of("User <p:mention> successfully completed verification"));
		VerificationMessages.Audit.Completed.Fields completedFields = new VerificationMessages.Audit.Completed.Fields();
		completedFields.setTarget("Target");
		completedFields.setDuration("Duration");
		completedFields.setMethod("Method");
		completed.setFields(completedFields);
		audit.setCompleted(completed);

		// Audit → Step
		VerificationMessages.Audit.Step step = new VerificationMessages.Audit.Step();
		
		// Audit → Step → Completed
		VerificationMessages.Audit.Step.Completed stepCompleted = new VerificationMessages.Audit.Step.Completed();
		stepCompleted.setTitle("📋 Verification Step Completed");
		stepCompleted.setDescription(List.of("User <p:mention> completed a verification step"));
		VerificationMessages.Audit.Step.Completed.Fields stepCompletedFields = new VerificationMessages.Audit.Step.Completed.Fields();
		stepCompletedFields.setTarget("Target");
		stepCompletedFields.setStepName("Step");
		stepCompleted.setFields(stepCompletedFields);
		step.setCompleted(stepCompleted);
		
		audit.setStep(step);

		// Audit → Timeout
		VerificationMessages.Audit.Timeout auditTimeout = new VerificationMessages.Audit.Timeout();
		auditTimeout.setTitle("⏱️ Verification Timeout");
		auditTimeout.setDescription(List.of("User <p:mention> failed to complete verification in time"));
		VerificationMessages.Audit.Timeout.Fields timeoutFields = new VerificationMessages.Audit.Timeout.Fields();
		timeoutFields.setTarget("Target");
		timeoutFields.setTimeLimit("Time Limit");
		timeoutFields.setTimeSpent("Time Spent");
		auditTimeout.setFields(timeoutFields);
		audit.setTimeout(auditTimeout);

		// Audit → Abandoned
		VerificationMessages.Audit.Abandoned abandoned = new VerificationMessages.Audit.Abandoned();
		abandoned.setTitle("🚪 Verification Abandoned");
		abandoned.setDescription(List.of("User <p:mention> left during verification"));
		VerificationMessages.Audit.Abandoned.Fields abandonedFields = new VerificationMessages.Audit.Abandoned.Fields();
		abandonedFields.setTarget("Target");
		abandonedFields.setCurrentStep("Current Step");
		abandoned.setFields(abandonedFields);
		audit.setAbandoned(abandoned);

		// Audit → Failed
		VerificationMessages.Audit.Failed failed = new VerificationMessages.Audit.Failed();
		failed.setTitle("❌ Verification Failed");
		failed.setDescription(List.of("Verification failed for <p:mention>"));
		VerificationMessages.Audit.Failed.Fields failedFields = new VerificationMessages.Audit.Failed.Fields();
		failedFields.setTarget("Target");
		failedFields.setError("Error");
		failed.setFields(failedFields);
		audit.setFailed(failed);

		messages.setAudit(audit);

		return messages;
	}
}