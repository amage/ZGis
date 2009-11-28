package org.mati.geotech.gui;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ObjectHtmlInfoPane extends JPanel {
	private static final long serialVersionUID = 6448829816596022356L;
	JEditorPane _htmlPane = new JEditorPane();
	private int _objId;
	private String _url = "http://127.0.0.1/info.php";
	
	public ObjectHtmlInfoPane() {
		setLayout(new BorderLayout());
		this.setSize(400, 800);
		init();
	}
	
	private void init() {
		_htmlPane.setEditable(false);
		_htmlPane.setText("no data");
		this.add(new JScrollPane(_htmlPane),BorderLayout.CENTER);

	}

	public void setObjectId(int id) {
		_objId = id;
		try {
			_htmlPane.setPage(_url +"?obj="+_objId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
