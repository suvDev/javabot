package javabot.operations;

import com.antwerkz.maven.SPI;
import javabot.BotEvent;
import javabot.Message;
import javabot.dao.FactoidDao;
import javabot.model.Factoid;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@SPI(BotOperation.class)
public class ForgetFactoidOperation extends BotOperation {
    @Autowired
    private FactoidDao factoidDao;

    @Override
    public boolean isStandardOperation() {
        return true;
    }

    @Override
    public List<Message> handleMessage(final BotEvent event) {
        final String channel = event.getChannel();
        String message = event.getMessage();
        final String sender = event.getSender();
        final List<Message> responses = new ArrayList<Message>();
        if (message.startsWith("forget ")) {
            message = message.substring("forget ".length());
            if (message.endsWith(".") || message.endsWith("?") || message.endsWith("!")) {
                message = message.substring(0, message.length() - 1);
            }
            final String key = message.toLowerCase();
            forget(responses, event, channel, sender, key);
        }
        return responses;
    }

    protected void forget(final List<Message> responses, final BotEvent event, final String channel,
                          final String sender, final String key) {
        final Factoid factoid = factoidDao.getFactoid(key);
        if (factoid != null) {
            if (!factoid.getLocked() || isAdminUser(event)) {
                responses.add(new Message(channel, event, String.format("I forgot about %s, %s.", key, sender)));
                factoidDao.delete(sender, key);
            } else {
                responses.add(new Message(channel, event, String.format("Only admins can delete locked factoids, %s.",
                        sender)));
            }
        } else {
            responses.add(new Message(channel, event,
                    String.format("I never knew about %s anyway, %s.", key, sender)));
        }
    }
}
