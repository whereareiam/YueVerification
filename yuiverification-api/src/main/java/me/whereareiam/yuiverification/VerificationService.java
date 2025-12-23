package me.whereareiam.yuiverification;

import me.whereareiam.yui.model.fluctlight.Fluctlight;

public interface VerificationService {
	void verify();

	void verify(Fluctlight fluctlight);

	void verifyManual(Fluctlight fluctlight, long initiatorId);
}
