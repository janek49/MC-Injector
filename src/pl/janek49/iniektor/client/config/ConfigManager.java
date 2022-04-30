package pl.janek49.iniektor.client.config;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.api.WrapperChat;
import pl.janek49.iniektor.client.util.IniektorUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {

    public HashMap<Object, List<Property>> properties;

    public ConfigManager() {
        this.properties = new HashMap<>();
    }

    public void registerProperties(Object object) {
        for (Field fd : object.getClass().getDeclaredFields()) {
            try {
                if (fd.getType().equals(Property.class) || fd.getType().equals(RangeProperty.class)) {
                    Property pt = (Property) fd.get(object);

                    if (!properties.containsKey(object))
                        properties.put(object, new ArrayList<>());

                    properties.get(object).add(pt);

                    Logger.log("RegisterProperties SUCCESS:", object.getClass().getName(), pt.propertyName);
                }
            } catch (Exception ex) {
                Logger.log("RegisterProperties ERROR:", object.getClass().getName(), fd.getName());
                ex.printStackTrace();
            }
        }
    }

    public Property getPropertyFor(Object obj, String name) {
        if (!properties.containsKey(obj))
            return null;

        for (Property pt : properties.get(obj)) {
            if (pt.propertyName.equalsIgnoreCase(name))
                return pt;
        }

        return null;
    }


    public void processChatCommand(Object obj, String[] command) {
        if (command.length < 2)
            return;

        Property pt = getPropertyFor(obj, command[1]);
        if (pt == null) {
            WrapperChat.showChatMessage("No such property '" + command[1] + "' for module: " + obj.getClass().getSimpleName());
            return;
        }

        if (command.length == 2) {
            WrapperChat.showChatMessage(obj.getClass().getSimpleName() + ": §e" + pt.propertyName + "§r <" + pt.getValue().getClass().getSimpleName() + ">");
            WrapperChat.showChatMessage(obj.getClass().getSimpleName() + ": §e" + pt.propertyName + "§r - current value: " + pt.getValue());
            return;
        }

        if (command.length > 2) {
            try {
                if (pt instanceof RangeProperty) {
                    RangeProperty rp = (RangeProperty) pt;
                    float val = Float.parseFloat(command[2]);
                    if ((val >= rp.min && val <= rp.max) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
                        pt.setValue(val);
                    else {
                        WrapperChat.showChatMessage(obj.getClass().getSimpleName() + ": §e" + pt.propertyName + "§r - §cValue outside of range: §r" + rp.min + " - " + rp.max);
                        return;
                    }
                } else if (pt.getValue().getClass().equals(Float.class)) {
                    pt.setValue(Float.parseFloat(command[2]));
                } else if (pt.getValue().getClass().equals(Integer.class)) {
                    pt.setValue(Integer.parseInt(command[2]));
                } else if (pt.getValue().getClass().equals(Boolean.class)) {
                    pt.setValue(Boolean.parseBoolean(command[2]));
                } else if (pt.getValue().getClass().equals(String.class)) {
                    String[] newArr = new String[command.length - 2];
                    System.arraycopy(command, 2, newArr, 0, newArr.length);
                    pt.setValue(String.join(" ", newArr));
                } else if (pt.getValue().getClass().isEnum()) {
                    Enum en = Enum.valueOf((Class) pt.getValue().getClass(), command[2].toUpperCase());
                    pt.setValue(en);
                }
                WrapperChat.showChatMessage(obj.getClass().getSimpleName() + ": §e" + pt.propertyName + "§r - new value: " + pt.getValue());
            } catch (Exception ex) {
                WrapperChat.showChatMessage("§cInvalid value format§r");
                ex.printStackTrace();
            }
        }
    }

    public List<Property> getPropertiesFor(Object o) {
        return properties.get(o);
    }
}
