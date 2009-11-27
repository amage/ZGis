package gui.states;

import java.awt.Cursor;
import java.util.Vector;

public abstract class GTMFState {	
	protected GTMFState() { }
	
	protected static GTMFState _state = null;
	static final protected GTMFViewState _viewState = new GTMFViewState();
	static final protected GTMFMoveState _moveState = new GTMFMoveState();
	static final protected GTMFSelectPlaceState _selplState = new GTMFSelectPlaceState();
	static final protected GTMFSelMultPntState _sellnState = new GTMFSelMultPntState();
	
	// Interaction stuff
	protected static Vector<GTMFStateChangeListener> _scl = new Vector<GTMFStateChangeListener>();
	public void removeListner(GTMFStateChangeListener eli) { _scl.remove(eli); }
	public void addListner(GTMFStateChangeListener eli) {_scl.add(eli); }
	
	static public GTMFState start() {
		_state = _viewState;
		_viewState.enter();
		return _state;
	}
	
	public enum Event {
		KB_ESC,
		MOUSE_RIGHT_CLICK, MOUSE_LEFT_CLICK, MOUSE_MIDDLE_CLICK, 
		MOUSE_LEFT_DOWN, MOUSE_RIGHT_DOWN, 
		MOUSE_LEFT_UP, MOUSE_RIGHT_UP,
		NEW_OBJECT_PNT, NEW_OBJECT_LINES
	};
	
	public GTMFState proccessEvent(Event e) {
		GTMFState oldState = _state;
		_state = _state.nextState(e);
		if(oldState!=_state) {
			oldState.exit();
			_state.enter();
			for(GTMFStateChangeListener scl:_scl) { scl.stateChenged(_state); }
		}
		return _state;
	}
	
	public boolean isMenuEnable() {return false;}
	abstract public GTMFState nextState(Event e);
	abstract public Cursor getCursor();
	abstract protected void enter();
	abstract protected void exit();
}

class GTMFViewState extends GTMFState {
	@Override
	public GTMFState nextState(Event e) {
		switch (e) {
		case MOUSE_LEFT_DOWN:
			return _moveState;
		case NEW_OBJECT_PNT:
			return _selplState;
		case NEW_OBJECT_LINES:
			return _sellnState;
		default:
			return this;
		}
	}
	
	@Override
	public boolean isMenuEnable() {return true;}
	@Override
	public Cursor getCursor() {
		return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	}
	@Override
	protected void enter() { 
		// System.out.println("new state: view");		
	}

	@Override
	protected void exit() { }
}

class GTMFMoveState extends GTMFState {
	@Override
	public GTMFState nextState(Event e) {
		switch (e) {
		case MOUSE_LEFT_UP:
			if(_state == _moveState)
				return _viewState;
		default:
			return this;
		}
	}
	@Override
	public Cursor getCursor() {
		return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
	}
	@Override
	protected void enter() {
		//System.out.println("new state: move");
	}

	@Override
	protected void exit() { }
}

class GTMFSelectPlaceState extends GTMFState {
	@Override
	public GTMFState nextState(Event e) { 
		switch (e) {
		case MOUSE_LEFT_CLICK:
			for(GTMFStateChangeListener scl:_scl) { scl.createPntObjectReq(); }
			return _viewState;
		case MOUSE_RIGHT_CLICK:
			return _viewState;
		default:
			return this;
		}
	}
	@Override
	public Cursor getCursor() { return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR); }
	@Override
	protected void enter() { }
	@Override
	protected void exit() {  }
}

class GTMFSelMultPntState extends GTMFState {
	private int pntCount=0;
	@Override
	public GTMFState nextState(Event e) { 
		switch (e) {
		case MOUSE_LEFT_CLICK:
			for(GTMFStateChangeListener scl:_scl) { scl.createLinePntReq(); }
			pntCount++;
			return this;
		case MOUSE_MIDDLE_CLICK:
			for(GTMFStateChangeListener scl:_scl) { scl.removeLinePntReq(); }
			pntCount--;
			if(pntCount==0)
				return _viewState;
			else
				return this;
		case MOUSE_RIGHT_CLICK:
			for(GTMFStateChangeListener scl:_scl) { scl.createLineObj(); }
			return _viewState;
		default:
			return this;
		}
	}
	@Override
	public Cursor getCursor() { return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR); }
	@Override
	protected void enter() { pntCount=0; }
	@Override
	protected void exit() { pntCount=0; }
}
