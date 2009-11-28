package org.mati.geotech.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.mati.geotech.utils.config.Config;



public class ConfigPane extends JPanel {
	private static final long serialVersionUID = 6776489796945047741L;	
	private Config _conf;
	private Properties _tmpProps = new Properties();
	
	public ConfigPane() {
		_conf = Config.getInstance();
		init();
	}

	private void init() {
		setLayout(new BorderLayout());
		JPanel tablePane = new JPanel(new BorderLayout());
		tablePane.add(new JScrollPane(new JTable( new DefaultTableModel() {
			private static final long serialVersionUID = -3601699108853884062L;
			@Override
			public int getColumnCount() { return 2; }
			@Override
			public String getColumnName(int column) {
				switch (column) {
				case 0: return "Параметр";
				case 1:	return "Значение";
				default: return "n/a";
				}
			}
			@Override
			public int getRowCount() { return _conf.getProps().size(); }
			@Override
			public Object getValueAt(int row, int column) {
				if(column==0) {
					return _conf.getProps().keySet().toArray()[row];
				}
				else if(column==1) {
					String key = (String)_conf.getProps().keySet().toArray()[row];
					if(_tmpProps.get(key)!=null) 
						return _tmpProps.get(key);
					else 
						return _conf.getProps().get(key);
				}
				else
					return "error";
			}
			
			@Override
			public void setValueAt(Object value, int row, int column) {
				String key = (String)_conf.getProps().keySet().toArray()[row];
				_tmpProps.put(key, value);
				super.setValueAt(value, row, column);
			}
			@Override
			public boolean isCellEditable(int row, int column) {
				if(column==0) return false;
				else if(column==1) return true;
				else return super.isCellEditable(row, column);
			}
		}
		)), BorderLayout.CENTER);
		
		JPanel bpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bpanel.add(new JButton(new AbstractAction("Применить") {
			private static final long serialVersionUID = 7080676027293959678L;

			@Override
			public void actionPerformed(ActionEvent e) {
				_conf.getProps().putAll(_tmpProps);
				_conf.updateAll();
//				try {
////					_conf.save(new FileOutputStream(GeoTeckApp.confName));
//					Logger.getRootLogger().info("config saved");
//				} catch (IOException e1) {
//					Logger.getRootLogger().error(e1.getMessage());
//				}
			}
		}));
		
		bpanel.add(new JButton(new AbstractAction("Сбросить") {
			private static final long serialVersionUID = 7080676027293959678L;

			@Override
			public void actionPerformed(ActionEvent e) {
				_tmpProps.clear();
				ConfigPane.this.updateUI();
			}
		}));		
		add(tablePane,BorderLayout.CENTER);
		add(bpanel,BorderLayout.SOUTH);
	}
	
	
}
