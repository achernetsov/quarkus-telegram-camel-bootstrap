package ru.archertech.lab.telegram.commands;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.model.IncomingMessage;
import org.apache.camel.component.telegram.model.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.archertech.lab.telegram.model.TelegramUser;
import ru.archertech.lab.telegram.service.TelegramUserService;

import java.util.Optional;

@ApplicationScoped
public class StartCommand extends RouteBuilder {
    public static final String COMMAND = "direct:tg-cmd-start";

    private static final String IS_NEW_USER_HEADER = "is_new_user";

    @ConfigProperty(name = "telegram.token")
    private String token;

    @Inject
    private TelegramUserService telegramUserService;

    @Override
    public void configure() {
        // @formatter:off
        from(COMMAND)
                .to("direct:find-or-create-user")
                .to("direct:send-greeting");

        // out: TelegramUser
        from("direct:find-or-create-user")
                .log("searching for existing user")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    IncomingMessage message = in.getBody(IncomingMessage.class);
                    User from = message.getFrom();

                    Optional<TelegramUser> maybeUser = telegramUserService.findById(from.getId());
                    in.setHeader(IS_NEW_USER_HEADER, maybeUser.isEmpty());
                    TelegramUser telegramUser;
                    if (maybeUser.isEmpty()){
                        telegramUser = new TelegramUser(from.getId(), from.getUsername(), from.getFirstName());
                        telegramUserService.persist(telegramUser);
                    } else {
                        telegramUser = maybeUser.get();
                    }
                    in.setBody(telegramUser);
                });

        // in: TelegramUser, header: is_new user
        from("direct:send-greeting")
                .setHeader("firstname", simple("${body.getFirstName()}"))
                .choice()
                .when(simple("${headers.is_new_user} == false"))
                    .log("Existing user found, sending greeting")
                    .setBody(simple("Welcome once again, ${headers.firstname}!"))
                    .to("telegram:bots?authorizationToken=" + token)
                .otherwise()
                    .log("New user is created, sending greeting")
                    .setBody(simple("You are new here, ${headers.firstname}, nice to meet you!"))
                    .to("telegram:bots?authorizationToken=" + token)
                .end();
        // @formatter:on
    }
}
