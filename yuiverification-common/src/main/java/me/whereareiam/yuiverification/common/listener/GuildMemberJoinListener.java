package me.whereareiam.yuiverification.common.listener;

import lombok.AllArgsConstructor;
import me.whereareiam.yuiverification.api.service.VerificationService;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GuildMemberJoinListener extends ListenerAdapter {
	private final VerificationService verificationService;

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		verificationService.verify(event.getUser().getIdLong());
	}
}
