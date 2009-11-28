package org.mati.geotech.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.mati.geotech.model.GeoObject;


public class NewObjectDialog extends JDialog {
	private static final long serialVersionUID = 6843592792212529801L;

	private JFormattedTextField _objLevel = new JFormattedTextField(NumberFormat.getIntegerInstance());
	private GeoObject _go;
	private boolean _ok;
	public boolean isOk() {return _ok;}
	
	public NewObjectDialog(Frame owner, GeoObject go) {
		super(owner);
		setLocationRelativeTo(owner);
		
		_go=go;
		_ok=false;
		_objLevel.setValue(_go.getStartLvl());
		init();
	}

	private void init() {
		JPanel ico = new JPanel(new BorderLayout());
		JPanel btn = new JPanel(new FlowLayout());
		JPanel edit = new JPanel(new BorderLayout());
		btn.add(new JButton(new AbstractAction("OK") {
			private static final long serialVersionUID = 7289238883467771074L;
			@Override
			public void actionPerformed(ActionEvent e) {
				_ok=true;
				NewObjectDialog.this.setVisible(false);
			}
		}));
		
		btn.add(new JButton(new AbstractAction("Отмена") {
			private static final long serialVersionUID = 7289238883467771074L;
			@Override
			public void actionPerformed(ActionEvent e) {
				_ok=false;
				NewObjectDialog.this.setVisible(false);
			}
		}));
		
		
		
		ico.add(new JLabel(Images.getImageIcon("s_o_w")),BorderLayout.CENTER);
		
		edit.add(new JScrollPane(new JTable(new DefaultTableModel() {
			private static final long serialVersionUID = -6502283343667243646L;
			@Override
			public int getColumnCount() { return 2; }
			@Override
			public int getRowCount() { return _go.getProps().keySet().size(); }
			@Override
			public String getColumnName(int column) {
				if(column==0) return "Параметр";
				else if(column==1) return "Значение";
				else return "n/a";
			}
			@Override
			public Object getValueAt(int row, int column) {
				if(column==0) return _go.getProps().keySet().toArray()[row];
				else if(column==1) return _go.getProps().get(_go.getProps().keySet().toArray()[row]);
				else return "n/a";
			}
			@Override
			public void setValueAt(Object value, int row, int column) {
				String key = (String)_go.getProps().keySet().toArray()[row];
				_go.getProps().put(key, (String)value);
				super.setValueAt(value, row, column);
			}
			@Override
			public boolean isCellEditable(int row, int column) {
				if(column==0) return false;
				else if(column==1) return true;
				else return super.isCellEditable(row, column);
			}
		})));
		
		getContentPane().add(ico,BorderLayout.WEST);
		getContentPane().add(edit,BorderLayout.CENTER);
		getContentPane().add(btn,BorderLayout.SOUTH);
		pack();
	}

}
