package me.whereareiam.yuiverification.common.listener;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yuiverification.common.DefaultVerificationService;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuildMemberRemoveListener extends ListenerAdapter {
	private final DefaultVerificationService verificationService;

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		verificationService.handleUserLeave(event.getUser().getIdLong());
	}
}
