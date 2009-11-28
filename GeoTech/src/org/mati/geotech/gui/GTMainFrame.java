package org.mati.geotech.gui;


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

import org.apache.log4j.Logger;
import org.mati.geotech.gui.states.GTMFState;
import org.mati.geotech.gui.states.GTMFStateChangeListener;
import org.mati.geotech.model.GeoObject;
import org.mati.geotech.model.GeoPoint;
import org.mati.geotech.model.Rect;
import org.mati.geotech.model.ResManager;
import org.mati.geotech.model.ResManager.MapSource;


public class GTMainFrame extends JFrame implements GTMFStateChangeListener {
	private static final long serialVersionUID = 2630322136338626255L;
	GTMFState _state = GTMFState.start();
	GTCanvas _convas;
	private double _mx=0;
	private double _my=0;
	private double _scrollSpeedX=0.001;
	private double _scrollSpeedY=0.001;
	private double _scrollSpeedZ=0.1;
	private JTabbedPane _centorTabs;
	//private JPanel _filterPane;
	private JToolBar _coPane;
	
	private static Logger log = Logger.getRootLogger();
	
	//private ObjectHtmlInfoPane _objView = new ObjectHtmlInfoPane();
	
	static public ObjectCatalog _objCat;
	private GeoObject newObj = null;
	private JPopupMenu makeMenu(final int x, final int y) {
		ViewPort vp = _convas.getViewPort();
		Rect selRect = vp.scrToMapRect(new Rect(x-16, y-16, 32,32));
		
		LinkedList<GeoObject> objs = _convas.getResManager().getObjects(selRect);
		JPopupMenu m = new JPopupMenu();
		
		for(final GeoObject o : objs) {
			m.add(new AbstractAction(o.getName()){
				private static final long serialVersionUID = 5225606861823603958L;
				@Override
				public void actionPerformed(ActionEvent arg0) {
					//_objView.setObjectId(o.getId());
					//_objView.setVisible(true);
					//_centorTabs.setSelectedIndex(1);
				}
				
			});
		}
		if(m.getComponentCount()>0)	m.addSeparator();
		m.add(new AbstractAction("По центру"){
			private static final long serialVersionUID = 5225606861823603958L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ViewPort vp = _convas.getViewPort();
				_convas.getViewPort().setViewWorldX(vp.scrToWorldX(x));
				_convas.getViewPort().setViewWorldY(vp.scrToWorldY(y));
			}
			
		});
		return m;
	}
	
	private void init() {
		// setting title of window
		setIconImage(Images.getImage("earth"));
		setTitle("GeoTeck Demo");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_convas = new GTCanvas();
		
		this.setJMenuBar(makeMenuBar());
		
		log.info("loading...");
		ResManager rm = _convas.getResManager();
		rm.loadObjects();
		
		JPanel infoPanel = new JPanel();
		infoPanel.add(new JButton(new AbstractAction("Загрузить объекты"){
			private static final long serialVersionUID = 5225606861823603958L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("loading...");
				ResManager rm = _convas.getResManager();

				rm.loadObjects();
			}
		}));
		
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		_convas.setPreferredSize(new Dimension(0,0));
		_convas.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON1) {
					_state = _state.proccessEvent(GTMFState.Event.MOUSE_LEFT_CLICK);
				}
				if(e.getButton()==MouseEvent.BUTTON2) {
					_state = _state.proccessEvent(GTMFState.Event.MOUSE_MIDDLE_CLICK);
				}
				else if (e.getButton() == MouseEvent.BUTTON3 ) {
					_state = _state.proccessEvent(GTMFState.Event.MOUSE_RIGHT_CLICK);
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(_state.getCursor());
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON1) {
					_state = _state.proccessEvent(GTMFState.Event.MOUSE_LEFT_DOWN);
				}
				else if (e.getButton() == MouseEvent.BUTTON3 ) {
					_state = _state.proccessEvent(GTMFState.Event.MOUSE_RIGHT_DOWN);
				}
				_mx = e.getX();
				_my = e.getY();
				_convas.repaint();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON1) {
					_state = _state.proccessEvent(GTMFState.Event.MOUSE_LEFT_UP);
				}
				else if (e.getButton() == MouseEvent.BUTTON3 ) {
					_state = _state.proccessEvent(GTMFState.Event.MOUSE_RIGHT_UP);
				}

				if(e.isPopupTrigger() && _state.isMenuEnable()) {
					JPopupMenu m = makeMenu(e.getX(), e.getY());
					m.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		_convas.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				ViewPort vp = _convas.getViewPort();
				_scrollSpeedX=vp.getViewWorldWidth()/_convas.getSize().getWidth();
				_scrollSpeedY=vp.getViewWorldHeight()/_convas.getSize().getHeight();
				
				vp.translateInMap(
						(_mx - e.getX())*_scrollSpeedX, 
						-(e.getY() - _my)*_scrollSpeedY,0);
				_mx = e.getX();
				_my = e.getY();
				
				_convas.repaint();	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				ViewPort vp = _convas.getViewPort();
				vp.setMousePos(e.getX(), e.getY());
				double x = e.getX();
				double y = e.getY();		
				LinkedList<GeoObject> objs = _convas.getResManager().getObjects(vp.scrToMapRect(new Rect(x-16,y-16,32,32)));
				for(GeoObject o:objs) {
					o.setShowText(true);
				}
				_convas.repaint();
				
			}
			
		});
		
		_convas.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				_scrollSpeedZ = 0.05 * Math.abs(_convas.getViewPort().getZ());
				_convas.getViewPort().translateInMap(0, 0, e.getWheelRotation()*_scrollSpeedZ);
				_convas.repaint();
			}			
		});
		
		
		_centorTabs = new JTabbedPane(JTabbedPane.LEFT);
		_centorTabs.add("Карта", _convas);
		// _centorTabs.add("Информация",_objView);
		// _centorTabs.add("Фильтр",_filterPane);
		_centorTabs.add("Настройки", new ConfigPane());
		getContentPane().add(makeToolBar(), BorderLayout.NORTH);
		_coPane = makeCreateObjectPane();
		getContentPane().add(_coPane,BorderLayout.SOUTH);
		getContentPane().add(_centorTabs,BorderLayout.CENTER);
		_convas.getResManager().setMapSourceType(MapSource.VIRTUAL_EARTH_ALL);
		//pack();
	}
	
	/*
	private JPanel makeFilterPane() {
		JPanel fp = new JPanel(new BorderLayout());
		JPanel cp = new JPanel();

		cp.setLayout(new GridLayout(_objCat.getCategoris().size(),1));
		
		for(String title : _objCat.getCategoris()) {
			HashMap<String, Boolean> lines = new HashMap<String, Boolean>();
			for(String objName: _objCat.getObjectNames(title)) {
				lines.put(objName, Boolean.parseBoolean(_objCat.getObjectProps(title, objName).get("filtred")));
			}
			JPanel gr = new JPanel(new GridLayout(lines.size(),2));
			for(String name: lines.keySet()) {
				gr.add(new JLabel(name));
				gr.add(new JCheckBox(new AbstractAction(title+"."+name) {
					private static final long serialVersionUID = 3249723749L;
					@Override
					public void actionPerformed(ActionEvent e) {
						Scanner sc = new Scanner(e.getActionCommand());
						sc.useDelimiter("\\.");
						String title = sc.next(); String name = sc.next();
						if(Boolean.parseBoolean(_objCat.getObjectProps(title, name).get("filtred"))) {
							_objCat.getObjectProps(title, name).put("filtred", "false");							
						}
						else 
						{
							_objCat.getObjectProps(title, name).put("filtred", "true");							
						}
					}
				}));
			}
			gr.setBorder(BorderFactory.createTitledBorder(title));
			cp.add(gr);
		}
		
		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(new JButton(new AbstractAction("Применить"){
			private static final long serialVersionUID = 806301999602116981L;
			@Override
			public void actionPerformed(ActionEvent e) {
				_centorTabs.setSelectedIndex(0);
				_convas.getResManager().setFilter(_objCat);
				log.debug("update filter");
			}
		}));
		fp.add(new JScrollPane(cp),BorderLayout.CENTER);
		fp.add(buttons,BorderLayout.SOUTH);
		return fp;
	}
	*/
	private JMenuBar makeMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu mFile = new JMenu("Файл");
		//JMenu mView = new JMenu("Вид");
		menuBar.add(mFile);
		//menuBar.add(mView);
		
		mFile.add(new AbstractAction("Выход"){
			private static final long serialVersionUID = 234234L;
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		final JMenuItem objCreating = new JMenuItem();
		objCreating.setAction(new AbstractAction("Добавление объектов"){
			private static final long serialVersionUID = 234254L;
			@Override
			public void actionPerformed(ActionEvent e) {
				if(objCreating.isSelected()) {
					objCreating.setSelected(false);	
				}
				else {
					objCreating.setSelected(true);
				}
			}
		});
		
		//mView.add(objCreating);
		
		return menuBar;
	}

	public GTMainFrame() {
		setSize(1000,730);
		_objCat = new ObjectCatalog();
		//_filterPane = makeFilterPane();
		_state.addListner(this);
		init();
	}

	private JToolBar makeToolBar() {
		JToolBar tb = new JToolBar(JToolBar.HORIZONTAL);

		/*
		tb.add(new AbstractAction("Панель объектов") {
			private static final long serialVersionUID = 12346564L;
			@Override
			public void actionPerformed(ActionEvent e) {
				_coPane.setVisible(!_coPane.isVisible());
				_convas.repaint();
			}
		});
		*/
		/*
		tb.add(new AbstractAction("google") {
			private static final long serialVersionUID = 134644124L;
			@Override
			public void actionPerformed(ActionEvent e) {
				_convas.getResManager().setMapSourceType(MapSource.GOOGLE);
			}
		});
		*/
		tb.add(new AbstractAction("Virtual Earth (vector)") {
			private static final long serialVersionUID = 53234624L;
			@Override
			public void actionPerformed(ActionEvent e) {
				_convas.getResManager().setMapSourceType(MapSource.VIRTUAL_EARTH_LINES);
			}
		});
		tb.add(new AbstractAction("Virtual Earth (photo)") {
			private static final long serialVersionUID = 1233524L;
			@Override
			public void actionPerformed(ActionEvent e) {
				_convas.getResManager().setMapSourceType(MapSource.VIRTUAL_EARTH_PHOTO);
			}
		});
		tb.add(new AbstractAction("Virtual Earth") {
			private static final long serialVersionUID = 1234624L;
			@Override
			public void actionPerformed(ActionEvent e) {
				_convas.getResManager().setMapSourceType(MapSource.VIRTUAL_EARTH_ALL);
			}
		});
		return tb;
	}
	
	private JToolBar makeCreateObjectPane() {
		JToolBar tb = new JToolBar(JToolBar.HORIZONTAL);
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
	
		JPanel panels[] = new JPanel[_objCat.getCategoris().size()];
		final Object[] catNames = _objCat.getCategoris().toArray();
		for(int i = 0; i < panels.length; i++) {
			final String catName = (String)catNames[i];
			panels[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
			for(final String objName: _objCat.getObjectNames((String)catNames[i])) {
				panels[i].add(objName,new JButton(new AbstractAction("",_objCat.getObjImage(catName, objName)){
					private static final long serialVersionUID = -9102811539109037140L;
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if(catName.equalsIgnoreCase("Транспортировка")) {
							_state = _state.proccessEvent(GTMFState.Event.NEW_OBJECT_LINES);
							_convas.getResManager().setNewLineObject(_objCat.create(catName, objName));
						}
						else {
							newObj = _objCat.create(catName, objName);
							_state = _state.proccessEvent(GTMFState.Event.NEW_OBJECT_PNT);
						}
					}
				}));
			}
			tabs.add((String)catNames[i],panels[i]);
		}
		tb.add(tabs);
		tb.setVisible(false);
		return tb;
	}

	@Override
	public void stateChenged(GTMFState state) {
		setCursor(state.getCursor());
	}

	@Override
	public void createPntObjectReq() {
		ResManager rm = _convas.getResManager();
		
		newObj.setStartLvl(rm.getMapLvl());
		newObj.getProps().put("name", "new obj");
		newObj.getProps().put("id", "100");
		newObj.getProps().put("lon", ""+_convas.getViewPort().scrToMapX(_mx));
		newObj.getProps().put("lat", ""+_convas.getViewPort().scrToMapY(_my));
		NewObjectDialog nod = new NewObjectDialog(this,newObj);
		nod.setModal(true);
		nod.setVisible(true);
		if(nod.isOk()) {
			try {
				rm.createObj(newObj);
				rm.recvObject(newObj);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
			newObj=null;
		}
	}

	@Override
	public void createLinePntReq() {
		GeoObject go = _convas.getResManager().getNewObjectLines();
		go.getPoints().add(new GeoPoint(_convas.getViewPort().scrToMapX(_mx),_convas.getViewPort().scrToMapY(_my)));
		_convas.repaint();
	}

	@Override
	public void removeLinePntReq() {
		GeoObject go = _convas.getResManager().getNewObjectLines();
		go.getPoints().remove(go.getPoints().lastElement());
		_convas.repaint();
	}

	@Override
	public void createLineObj() {
		GeoObject go = _convas.getResManager().getNewObjectLines();
		go.updateRect();
		
		go.setStartLvl(_convas.getResManager().getMapLvl());
		go.getProps().put("name", "new obj");
		go.getProps().put("lon", ""+_convas.getViewPort().scrToMapX(_mx));
		go.getProps().put("lat", ""+_convas.getViewPort().scrToMapY(_my));
		NewObjectDialog nod = new NewObjectDialog(this,go);
		nod.setModal(true);
		nod.setVisible(true);
		if(nod.isOk()) {
			try {
				_convas.getResManager().createObj(go);
				_convas.getResManager().recvObject(go);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
			_convas.getResManager().newLineObject();
		}		
	}
	

}
