package de.bastard.frameworks.network;

import de.bastard.frameworks.network.Data;

import com.badlogic.gdx.graphics.Color; import de.bastard.frameworks.util.Position;
import de.bastard.game.concept.World;

public interface Transferable {

    int getTypeId();
    void setContext(Context context);
    void storeData(Data data);

}
