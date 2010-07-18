package org.mati.geotech.gui;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.mati.geotech.layers.AbstractLayer;
import org.mati.geotech.layers.GUILayer;
import org.mati.geotech.layers.GeoGridLayer;
import org.mati.geotech.layers.MSMapLayer;
import org.mati.geotech.layers.TextLayer;
import org.mati.geotech.model.ResManager;
import org.mati.geotech.model.ResManagerListener;
import org.mati.geotech.model.World;

public class MainWindow extends Composite {

    private final class GMouseWheelListener implements MouseWheelListener {
        @Override
        public void mouseScrolled(MouseEvent event) {
            scrollSpeedZ = 0.05 * Math.abs(vport.getZ());
            vport.translateInMap(0, 0, event.count * scrollSpeedZ);
            repaint();
        }
    }

    private final class GMouseListener implements MouseListener {
        @Override
        public void mouseDoubleClick(MouseEvent arg0) {
        	// TODO: zoom in map
        }

        @Override
        public void mouseDown(MouseEvent event) {
            if (event.button == 1) {
                bDrag = true;
                mx = event.x;
                my = event.y;
            }
        }

        @Override
        public void mouseUp(MouseEvent event) {
            if (event.button == 1) {
                bDrag = false;
            }
        }
    }

    private final class GMouseMoveListener implements MouseMoveListener {
        @Override
        public void mouseMove(MouseEvent event) {
            double x = event.x;
            double y = event.y;
            if (bDrag) {
                scrollSpeedX = vport.getViewWorldWidth()
                        / canvas.getSize().x;
                scrollSpeedY = vport.getViewWorldHeight()
                        / canvas.getSize().y;

                vport.translateInMap((mx - x) * scrollSpeedX, (my - y)
                        * scrollSpeedY, 0);
                mx = event.x;
                my = event.y;
            }
            vport.setMousePos(x, y);
            repaint();
        }
    }

    ResManagerListener dataUpdateListner = new ResManagerListener() {
        @Override
        public void stateChanged() {
            repaint();
        }
    };

    private double mx = 0;
    private double my = 0;
    private double scrollSpeedX = 0.001;
    private double scrollSpeedY = 0.001;
    private double scrollSpeedZ = 0.1;

    private ViewPort vport = new ViewPort();
    private ResManager res;
    private GLContext context;
    private GLCanvas canvas;

    private World world= new World(37.24, 55.45, 38, 56);

    private boolean bDrag = false;

    public MainWindow(Composite parent, int flags) {
        super(parent, flags);

        setLayout(new FillLayout());
        GLData data = new GLData();
        data.doubleBuffer = true;
        canvas = new GLCanvas(this, SWT.NONE, data);
        canvas.setCurrent();
        context = GLDrawableFactory.getFactory().createExternalGLContext();
        canvas.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Rectangle bounds = canvas.getBounds();
                if (bounds.height == 0)
                    bounds.height = 1;
                double asp = (double) bounds.width / (double) bounds.height * 2;
                vport.setAspect(asp);
                vport.getScreenRect().setGeometry(event.x, event.y,
                        bounds.width, bounds.height);
                canvas.setCurrent();
                context.makeCurrent();
                GL gl = context.getGL();
                gl.glViewport(event.x, event.y, bounds.width, bounds.height);
                for (AbstractLayer l : world.getLayers())
                    l.setSize(bounds.width, bounds.height);
                context.release();
                repaint();
            }
        });

        res = new ResManager();

        res.addListner(dataUpdateListner);

        world.getLayers().add(new MSMapLayer(res, vport));
        world.getLayers().add(new GeoGridLayer(res, vport));
        world.getLayers().add(new TextLayer(res, vport));
        world.getLayers().add(new GUILayer(res, vport));

        context.makeCurrent();
        try {
            res.init();
            GL gl = context.getGL();
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
            gl.glClearColor(0.1f, 0.2f, 0.1f, 1);

            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
                    GL.GL_NEAREST);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                    GL.GL_LINEAR);
            context.release();
        } catch (Exception e) {
            e.printStackTrace();
            context.release();
            System.exit(1);
        }

        addMouseWheelListener(new GMouseWheelListener());
        canvas.addMouseListener(new GMouseListener());
        canvas.addMouseMoveListener(new GMouseMoveListener());
        
        vport.setViewWorldX(vport.mapToWorldX(world.getX()-world.getWidth()/2));
        vport.setViewWorldY(vport.mapToWorldY(world.getY()-world.getHeight()/2));
    }

    private void repaint() {
        if (!isDisposed()) {
            getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    canvas.setCurrent();
                    context.makeCurrent();
                    GL gl = context.getGL();
                    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_ACCUM_BUFFER_BIT
                            | GL.GL_STENCIL_BUFFER_BIT);
                    gl.glMatrixMode(GL.GL_PROJECTION);
                    gl.glLoadIdentity();
                    GLU glu = new GLU();
                    glu.gluPerspective(vport.getFOV(), vport.getAspect(), vport
                            .getMinZ(), vport.getMaxZ());
                    glu.gluLookAt(vport.getViewWorldX(), vport.getViewWorldY(),
                            -vport.getZ(), vport.getViewWorldX(), vport
                                    .getViewWorldY(), 0, 0, -1, 0);
                    if (res != null) {
                        for (AbstractLayer l : world.getLayers())
                            l.paint(gl);
                    }
                    canvas.swapBuffers();
                    context.release();
                }
            });
        }
    }
}
