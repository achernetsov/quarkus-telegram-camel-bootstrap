package ru.archertech.lab.telegram;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.archertech.lab.telegram.commands.StartCommand;

@ApplicationScoped
public class TelegramIncomingMsgProcessor extends RouteBuilder {

    @ConfigProperty(name = "telegram.token")
    private String token;

    @Override
    public void configure() {
        // @formatter:off
        from("telegram:bots?authorizationToken=" + token)
                .log("${body}")
                .choice()
                    .when(simple("${body} == '/start'")).to(StartCommand.COMMAND)
                    .otherwise().to(StartCommand.COMMAND)
                .endChoice();

        // @formatter:on
    }
}
