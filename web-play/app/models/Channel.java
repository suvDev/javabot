package models;

import controllers.AdminController;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "channel")
public class Channel extends Model {
    @CheckWith(IrcChannelNameValidator.class)
    public String name;
    public String key;
    public Date updated;
    @Required
    public boolean logged;

    public static List<Channel> findLogged() {
        return Channel.find("logged = true order by name").fetch();
    }

    @Override
    public Channel save() {
        Channel save = super.save();

        ChannelEvent event = new ChannelEvent(name, true, AdminController.getTwitterContext().screenName, updated);
        event.save();
        return save;
    }

    private static class IrcChannelNameValidator extends Check {
        @Override
        public boolean isSatisfied(Object validatedObject, Object value) {
            setMessage("channel.name");
            return value != null && ((String)value).startsWith("#");
        }
    }
}
