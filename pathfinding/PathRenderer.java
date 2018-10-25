package de.bastard.frameworks.pathfinding;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.bastard.frameworks.gdx.GdxGfxPort;
import de.bastard.frameworks.graphics2d.GfxPort;

import com.badlogic.gdx.graphics.Color; import de.bastard.frameworks.util.Position;

import static de.bastard.CONFIG.TILE_SIZE;

public class PathRenderer {

    private static final int GOAL = 0;
    private static final int AHEAD = 1;
    private static final int TURN = 2;
    private static final int U_TURN = 3;
    private static final int FLAG = 4;

    public static void renderPath(Path path, GfxPort gfxPort, Object[] textures) {
        renderPath(path, gfxPort, textures, new Color(1f,1f,1f,1f));
    }

    public static void renderPath(Path path, GfxPort gfxPort, Object[] textures, Color color) {
        gfxPort.setColor(color);
        WayPoint prev = null;
        WayPoint current = path.getStart();
        int textureId;
        Direction d2 = null;
        for (WayPoint next : path.getList()) {
            d2 = current.getDirection(next);
            if (prev != null) {
                Direction d = prev.getDirection(current);
                Direction rd = new Direction(d, d2);
                boolean flip = false;
                switch (rd.d) {
                    case Direction.FORWARD:
                        textureId = AHEAD;
                        break;
                    case Direction.LEFT:
                        textureId = TURN;
                        flip = true;
                        break;
                    case Direction.RIGHT:
                        textureId = TURN;
                        break;
                    case Direction.BACK:
                        textureId = U_TURN;
                        break;
                    default:
                        textureId = AHEAD;
                }
                gfxPort.drawTexture(textures[textureId], current.x * TILE_SIZE, current.y * TILE_SIZE, flip, false, d.toRotation());
            }
            if (current.flagged)
                gfxPort.drawTexture(textures[FLAG], current.x * TILE_SIZE, current.y * TILE_SIZE);
            prev = current;
            current = next;
        }
        if (d2 != null) {
            gfxPort.drawTexture(textures[GOAL], current.x * TILE_SIZE, current.y * TILE_SIZE, d2.toRotation());
            if (current.flagged)
                gfxPort.drawTexture(textures[FLAG], current.x * TILE_SIZE, current.y * TILE_SIZE);
        }
        gfxPort.setColor(new Color(1f,1f,1f,1f));
    }

}
