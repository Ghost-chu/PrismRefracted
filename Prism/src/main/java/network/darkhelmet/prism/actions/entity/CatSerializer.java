package network.darkhelmet.prism.actions.entity;

import com.google.gson.annotations.SerializedName;
import network.darkhelmet.prism.utils.MiscUtils;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;

public class CatSerializer extends EntitySerializer {
    @SerializedName(value = "v", alternate = {"var"})
    protected String var = null;

    @Override
    protected void serializer(Entity entity) {
        var = ((Cat) entity).getCatType().name().toLowerCase();
    }

    @Override
    protected void deserializer(Entity entity) {
        Cat.Type type = MiscUtils.getEnum(var, Cat.Type.ALL_BLACK);
        ((Cat) entity).setCatType(type);
    }

    @Override
    protected void niceName(StringBuilder sb, int start) {
        if (var != null) {
            sb.insert(start, MiscUtils.niceName(var)).insert(start + var.length(), ' ');
        }
    }
}
