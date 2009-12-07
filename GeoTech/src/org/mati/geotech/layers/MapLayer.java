package org.mati.geotech.layers;

import javax.media.opengl.GL;

import org.mati.geotech.gui.ViewPort;
import org.mati.geotech.model.Rect;
import org.mati.geotech.model.ResManager;
import org.mati.geotech.model.cellcover.CellCover;
import org.mati.geotech.model.cellcover.CellCoverListener;
import org.mati.geotech.model.cellcover.MapGridCellView;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

public class MapLayer extends AbstractMapLayer {
    CellCover cellCover = new CellCover();
    MapGridCellView[][] mapGrid = null;

    public MapLayer(ResManager res, ViewPort vp) {
        super(res, vp);
        cellCover.addListner(resourceManager);
        cellCover.addListner(coverListner);
    }

    @Override
    public void paint(GL gl) {
        try {
            updateMapGrid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mapGrid != null) {
            gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
                    GL.GL_REPLACE);
            for (int i = 0; i < cellCover.getCellCountH(); i++) {
                for (int j = 0; j < cellCover.getCellCountW(); j++) {
                    if (mapGrid[i][j]
                            .haveOverlap(new Rect(-180, -90, 360, 180)))
                        drawCell(gl, mapGrid[i][j]);
                }
            }
        }
    }

    private void updateMapGrid() {
        cellCover.setViewWindow(viewPort.getViewWorldX() - viewPort.getViewWorldWidth() / 2,
                viewPort.getViewWorldY() - viewPort.getViewWorldHeight() / 2, viewPort
                        .getViewWorldWidth(), viewPort.getViewWorldHeight(),
                getScreenWidth() / viewPort.getViewWorldWidth(), getScreenHeight()
                        / viewPort.getViewWorldHeight());
    }

    private void drawCell(GL gl, MapGridCellView cell) {
        try {
            Texture t = getTexture(cell);
            t.enable();
            t.bind();
            TextureCoords tc = t.getImageTexCoords();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2d(tc.left(), tc.top());
            gl.glVertex3d(cell.getX(), cell.getY(), 0);

            gl.glTexCoord2d(tc.right(), tc.top());
            gl.glVertex3d(cell.getX() + cell.getWidth(), cell.getY(), 0);

            gl.glTexCoord2d(tc.right(), tc.bottom());
            gl.glVertex3d(cell.getX() + cell.getWidth(), cell.getY()
                    + cell.getHeight(), 0);

            gl.glTexCoord2d(tc.left(), tc.bottom());
            gl.glVertex3d(cell.getX(), cell.getY() + cell.getHeight(), 0);
            gl.glEnd();
            t.disable();
        } catch (Exception e) {
            e.printStackTrace();
            gl.glDisable(GL.GL_TEXTURE);
        }
        boolean drawRects = false;
        if (drawRects) {
            gl.glColor3d(0.5, 0, 0);
            gl.glBegin(GL.GL_LINE_LOOP);
            gl.glVertex2d(cell.getX(), cell.getY());
            gl.glVertex2d(cell.getX(), cell.getY() + cell.getHeight());
            gl.glVertex2d(cell.getX() + cell.getWidth(), cell.getY()
                    + cell.getHeight());
            gl.glVertex2d(cell.getX() + cell.getWidth(), cell.getY());
            gl.glEnd();
        }
    }

    private Texture getTexture(MapGridCellView cell) throws Exception {
        return resourceManager.getMapTexture(resourceManager.makePathFor(cell));
    }

    CellCoverListener coverListner= new CellCoverListener() {
        @Override
        public void gridPositionChanged(double x, double y, double cw, double ch,
                int n, int m) {
        }

        @Override
        public void gridSizeChanged(int n, int m) {
            mapGrid = cellCover.getGridMartix();
        }

        @Override
        public void levelChanged(int newLvl, int prevLvl) {
        }
    };
    

}
