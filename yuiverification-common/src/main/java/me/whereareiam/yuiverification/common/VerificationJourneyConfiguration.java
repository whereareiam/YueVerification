package me.whereareiam.yuiverification.common;

import me.whereareiam.yui.annotation.journey.JourneyConfiguration;
import me.whereareiam.yui.journey.definition.JourneyConfigurationDefinition;
import org.springframework.stereotype.Component;

@Component
@JourneyConfiguration(journeyId = "verification")
public class VerificationJourneyConfiguration implements JourneyConfigurationDefinition {
}
