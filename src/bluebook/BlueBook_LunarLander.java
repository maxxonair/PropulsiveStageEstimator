package bluebook;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class BlueBook_LunarLander implements  ActionListener {
	
	public static String PROJECT_TITLE = "  BlueBook BETA  V1.05 LunarLander";
    public static String PATH_LOGO = "C:\\logo2.1.jpg";
    public static String BB_delimiter = ",";
    JFileChooser chooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "CascadeFiles", "csv");
    
    public static File myfile_opened; 
    public static String myfile_name =""; 
    
    static public JFrame MAIN_frame;
    
    int extx_main = 1650;
    int exty_main = 900; 
    static int x_init = 1380;
    static int y_init = 830 ;
    
    public static int gg = 235;
    public static Color l_c = new Color(0,0,0);    			// Label Color
   	public static Color bc_c = new Color(255,255,255);			// Background Color
   	public static Color w_c = new Color(gg,gg,gg);				// Box background color
   	public static Color t_c = new Color(255,255,255);				// Table background color
   	
    static DecimalFormat df = new DecimalFormat("#.#");
    static DecimalFormat df2 = new DecimalFormat("#");
    Font menufont = new Font("Verdana", Font.LAYOUT_LEFT_TO_RIGHT, 12);
    Font labelfont_small = new Font("Verdana", Font.LAYOUT_LEFT_TO_RIGHT, 9);
    Font labelfont_small_bold = new Font("Verdana", Font.BOLD, 9);
    Font labelfont_verysmall = new Font("Verdana", Font.BOLD, 7);
    //------------------------------------------------------------------------------------------------------------------------------
    public static DefaultTableModel model_deltav;
    String[] columns_deltav = { "INC", "ID", "Delta-V [m/s]", "ISP [s]", "Steering loss margin [%]", "Margin [%]", "Final Delta-V [m/s]", "Time T+ [days] ", "Engine"};
	public static JTable table_deltav;		// Delta-V Top-Down section	
	public static int table_deltav_NumberOfColumns = 9;
    public static Object[] row_deltav  = new Object[table_deltav_NumberOfColumns];
    //------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------
    public static DefaultTableModel model_equipment;
    String[] columns_equipment = { "INC", "ID", "Group","DV from", "DV to", "Name", "Quantity", "Mass [kg]", "MGA [%]", "MGA [kg]","MER [%]","MER [kg]","PMR [kg]","Mass w/M [kg]"};
	public static JTable table_equipment;		// Delta-V Top-Down section	
	public static int table_equipment_NumberOfColumns = 12;
    public static Object[] row_equipment  = new Object[table_equipment_NumberOfColumns];
    //------------------------------------------------------------------------------------------------------------------------------
    
    public static JButton P2_add, P2_delete, P2_up, P2_down;
    public static JButton P3_add, P3_delete, P3_up, P3_down;
    public static int max_deltav_ID = 0; 
    public static int max_equipment_ID = 0; 
    
    
    public JPanel createContentPane () throws IOException{
    	
		JPanel TOP_GUI = new JPanel();
		TOP_GUI.setBackground(Color.white);
		TOP_GUI.setLayout(new BorderLayout());
		

    	JPanel MainGUI = new JPanel();
    	MainGUI.setBackground(bc_c);
    	MainGUI.setPreferredSize(new Dimension(extx_main, exty_main));
    	//MainGUI.setLocation(0, 30);
    	MainGUI.setLayout(new BorderLayout());
    	
    	//--------------------------------------------------------------------------------------------------------------------------------------------------------
        //Create horizontal menu bar.
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLocation(0, 0);
        menuBar.setSize(extx_main, 20);
        TOP_GUI.add(menuBar,BorderLayout.PAGE_START);

        //Build the first menu.
        JMenu menu = new JMenu("File ");
        menu.setForeground(Color.black);
        menuBar.add(menu);
        menuBar.setBackground(new Color(184,207,229));
        JMenuItem mI1 = new JMenuItem("Open             ");
        mI1.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menu.add(mI1);
        mI1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                              Import_Case();
                    } });
        JMenuItem mI2 = new JMenuItem("Save As                ");
        mI2.setForeground(Color.black);
        mI2.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menu.add(mI2);
        
        mI2.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                	   		   SaveAs_Case();
                    } });
        JMenuItem mI4 = new JMenuItem("Save");
        mI4.setFont(menufont);
        menu.add(mI4);
        mI4.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                               try {
								Save_Case();
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                    } });
        JMenuItem mI5 = new JMenuItem("New");
        menu.add(mI5);
        mI5.setFont(menufont);
        mI5.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                          PrepareNewFile();     
                    } });
        menu.addSeparator();
        JMenuItem mI3 = new JMenuItem("Exit");
        mI3.setFont(menufont);
        menu.add(mI3);
        mI3.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                                System.exit(0);
                    } });
    	//----------------------------------------------------- 
        JPanel P1_page01 = new JPanel();
       // P1_page01.setLayout(null);
        P1_page01.setLocation(0, 0);
        P1_page01.setSize(1350, 900);
        P1_page01.setBackground(bc_c);
    	
        JPanel P1_page02 = new JPanel();
        P1_page02.setLayout(new BorderLayout());
        P1_page02.setLocation(0, 0);
        P1_page02.setSize(1350, 900);
        P1_page02.setBackground(bc_c);
        P1_page02.setForeground(l_c);
        
        JPanel P1_page03 = new JPanel();
        P1_page03.setLayout(new BorderLayout());
        P1_page03.setLocation(0, 0);
        P1_page03.setSize(1350, 900);
        P1_page03.setBackground(bc_c);
        P1_page03.setForeground(l_c);
        //----------------------------------------------------- 
        // Page 01 
        //----------------------------------------------------- 

        //----------------------------------------------------- 
        // Page 02 
        //----------------------------------------------------- 
        int tablewidth5 = 785;
        int tablehight5 = 750;
        JPanel tablepanel = new JPanel();
        tablepanel.setLayout(null);
        //tablepanel.setLocation(215,5);
        tablepanel.setPreferredSize(new Dimension(tablewidth5,tablehight5));
        P1_page02.add(tablepanel, BorderLayout.CENTER);
        
        JPanel P2_sidepanel = new JPanel();
        P2_sidepanel.setLayout(null);
        //P2_sidepanel.setLocation(0,5);
        P2_sidepanel.setPreferredSize(new Dimension(190,tablehight5));
        P1_page02.add(P2_sidepanel, BorderLayout.WEST);

        
        table_deltav = new JTable(){
            private static final long serialVersionUID = 1L;
            
            @SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
            
        	@Override
        	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
	            Component comp = super.prepareRenderer(renderer, row, col);
	            Object value = getModel().getValueAt(row, col);
	           // String val_is = String.valueOf(value);
	            try {
	                if (col == 0 && value.equals(true)) {
	                    comp.setBackground(Color.red);
	                } else if (col == 2 ) {
		                comp.setBackground(Color.blue);
		                comp.setForeground(Color.white);
	                } else if (col == 6 ) {
		                comp.setBackground(Color.red);
		                comp.setForeground(Color.black);
	                } else {
	                comp.setBackground(t_c);
	                comp.setForeground(Color.black);
	                }
	            } catch (NullPointerException e) {
	            	
	            }
	            return comp;
	        }
        	
        }; ;
        
        
        @SuppressWarnings("serial")
		Action action5 = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                @SuppressWarnings("unused")
				TableCellListener tcl5 = (TableCellListener)e.getSource();
               // System.out.println("Row   : " + tcl.getRow());
               // System.out.println("Column: " + tcl.getColumn());
               // System.out.println("Old   : " + tcl.getOldValue());
               // System.out.println("New   : " + tcl.getNewValue());
                //FNC_SC_UPDATE();
            }
        };
    @SuppressWarnings("unused")
	TableCellListener tcl5 = new TableCellListener(table_deltav, action5);
        
        model_deltav = new DefaultTableModel(){

            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
               //all cells false
				if (column == 1 || column == 6 ){
					 return false;
				} else {
					return true; 
				}
            }
        };
        model_deltav.setColumnIdentifiers(columns_deltav);
        table_deltav.setModel(model_deltav);
        ((JTable) table_deltav).setFillsViewportHeight(true);
        table_deltav.setBackground(t_c);
        table_deltav.setForeground(l_c);
        
        table_deltav.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table_deltav.getColumnModel().getColumn(0).setPreferredWidth(40);
        table_deltav.getColumnModel().getColumn(1).setPreferredWidth(40);
        table_deltav.getColumnModel().getColumn(2).setPreferredWidth(80);
        table_deltav.getColumnModel().getColumn(3).setPreferredWidth(60);
        table_deltav.getColumnModel().getColumn(4).setPreferredWidth(130);
        table_deltav.getColumnModel().getColumn(5).setPreferredWidth(90);
        table_deltav.getColumnModel().getColumn(6).setPreferredWidth(130);
        table_deltav.getColumnModel().getColumn(7).setPreferredWidth(110);
        table_deltav.getColumnModel().getColumn(8).setPreferredWidth(90);
        //table_deltav.getColumnModel().getColumn(8).setPreferredWidth(90);
        
        
        JScrollPane spTable5 = new JScrollPane(table_deltav, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        spTable5.setLocation(0,0);
        spTable5.setSize(tablewidth5,tablehight5);
        tablepanel.add(spTable5);
        
        P2_add = new JButton("Add Row");
        P2_add.setLocation(5, 20);
        P2_add.setSize(90, 30);
        P2_add.addActionListener(this);
        P2_sidepanel.add(P2_add);
        P2_delete = new JButton("Delete");
        P2_delete.setLocation(95, 20);
        P2_delete.setSize(90, 30);
        P2_delete.addActionListener(this);
        P2_sidepanel.add(P2_delete);
        P2_up = new JButton("Up");
        P2_up.setLocation(5, 55);
        P2_up.setSize(90, 30);
        P2_up.addActionListener(this);
        P2_sidepanel.add(P2_up);
        P2_down = new JButton("Down");
        P2_down.setLocation(95, 55);
        P2_down.setSize(90, 30);
        P2_down.addActionListener(this);
        P2_sidepanel.add(P2_down);
        
        //----------------------------------------------------- 
        // Page 03 
        //----------------------------------------------------- 
        
        int tablewidth3 = 1200;
        int tablehight3 = 750;
        JPanel tablepanel_3 = new JPanel();
        tablepanel_3.setLayout(null);
        tablepanel_3.setPreferredSize(new Dimension(tablewidth3,tablehight3));
        P1_page03.add(tablepanel_3, BorderLayout.CENTER);
        
        JPanel P3_sidepanel = new JPanel();
        P3_sidepanel.setLayout(null);
        //P2_sidepanel.setLocation(0,5);
        P3_sidepanel.setPreferredSize(new Dimension(190,tablehight3));
        P1_page03.add(P3_sidepanel, BorderLayout.WEST);
        
        table_equipment = new JTable(){
            private static final long serialVersionUID = 1L;
            
            @SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
            
        	@Override
        	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
	            Component comp = super.prepareRenderer(renderer, row, col);
	            Object value = getModel().getValueAt(row, col);
	           // String val_is = String.valueOf(value);
	            try {
	                if (col == 0 && value.equals(true)) {
	                    comp.setBackground(Color.red);
	                } else if (col == 11 ) {
		                comp.setBackground(Color.white);
		                comp.setForeground(Color.black);
	                } else {
	                comp.setBackground(t_c);
	                comp.setForeground(Color.black);
	                }
	            } catch (NullPointerException e) {
	            	
	            }
	            return comp;
	        }	
        }; ;
        
        
        @SuppressWarnings("serial")
		Action action3 = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                @SuppressWarnings("unused")
				TableCellListener tcl5 = (TableCellListener)e.getSource();
               // System.out.println("Row   : " + tcl.getRow());
               // System.out.println("Column: " + tcl.getColumn());
               // System.out.println("Old   : " + tcl.getOldValue());
               // System.out.println("New   : " + tcl.getNewValue());
                //FNC_SC_UPDATE();
            }
        };
    @SuppressWarnings("unused")
	TableCellListener tcl3 = new TableCellListener(table_equipment, action3);
        
        model_equipment = new DefaultTableModel(){
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
               //all cells false
				if (column == 1 || column == 7 || column == 9 || column == 11 ){
					 return false;
				} else {
					return true; 
				}
            }
        };
        model_equipment.setColumnIdentifiers(columns_equipment);
        table_equipment.setModel(model_equipment);
        ((JTable) table_equipment).setFillsViewportHeight(true);
        table_equipment.setBackground(t_c);
        table_equipment.setForeground(l_c);
        
        table_equipment.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table_equipment.getColumnModel().getColumn(0).setPreferredWidth(40);
        table_equipment.getColumnModel().getColumn(1).setPreferredWidth(40);
        table_equipment.getColumnModel().getColumn(2).setPreferredWidth(80);
        table_equipment.getColumnModel().getColumn(3).setPreferredWidth(60);
        table_equipment.getColumnModel().getColumn(4).setPreferredWidth(60);
        table_equipment.getColumnModel().getColumn(5).setPreferredWidth(150);
        table_equipment.getColumnModel().getColumn(6).setPreferredWidth(60);
        table_equipment.getColumnModel().getColumn(7).setPreferredWidth(90);
        table_equipment.getColumnModel().getColumn(8).setPreferredWidth(80);
        table_equipment.getColumnModel().getColumn(9).setPreferredWidth(80);
        table_equipment.getColumnModel().getColumn(10).setPreferredWidth(80);
        table_equipment.getColumnModel().getColumn(11).setPreferredWidth(80);
        table_equipment.getColumnModel().getColumn(12).setPreferredWidth(80);
        table_equipment.getColumnModel().getColumn(13).setPreferredWidth(110);
        //table_deltav.getColumnModel().getColumn(8).setPreferredWidth(90);
        
        
        JScrollPane spTable3 = new JScrollPane(table_equipment, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        spTable3.setLocation(0,0);
        spTable3.setSize(tablewidth3,tablehight3);
        tablepanel_3.add(spTable3);
        
        P3_add = new JButton("Add Row");
        P3_add.setLocation(5, 20);
        P3_add.setSize(90, 30);
        P3_add.addActionListener(this);
        P3_sidepanel.add(P3_add);
        P3_delete = new JButton("Delete");
        P3_delete.setLocation(95, 20);
        P3_delete.setSize(90, 30);
        P3_delete.addActionListener(this);
        P3_sidepanel.add(P3_delete);
        P3_up = new JButton("Up");
        P3_up.setLocation(5, 55);
        P3_up.setSize(90, 30);
        P3_up.addActionListener(this);
        P3_sidepanel.add(P3_up);
        P3_down = new JButton("Down");
        P3_down.setLocation(95, 55);
        P3_down.setSize(90, 30);
        P3_down.addActionListener(this);
        P3_sidepanel.add(P3_down);
    	
        //------------------------------------------------------------------------
    	JTabbedPane tabbedPane = new JTabbedPane();
    	tabbedPane.setLocation(0, 0);
    	tabbedPane.setSize(extx_main, exty_main);
    	
        tabbedPane.addTab("Overview"+"\u2713" , null, P1_page01, null);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        tabbedPane.addTab("Delta-V"+"\u2193" , null, P1_page02, null);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);
        tabbedPane.addTab("Elements"+"\u2191" , null, P1_page03, null);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_3);
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int ACTIVE_TAB = tabbedPane.getSelectedIndex();
		                if (ACTIVE_TAB == 0){

		                } else if (ACTIVE_TAB == 1) {
		         
		                } else if (ACTIVE_TAB == 2) {
		         
		            	}
        }                
        });
        tabbedPane.setSelectedIndex(1);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        MainGUI.add(tabbedPane);
    	//--------------------------------------------------------------------------------------------------------------------------------------------------------
        JScrollPane scroll = new JScrollPane(MainGUI, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setPreferredSize(new Dimension(x_init, y_init));
        TOP_GUI.add(scroll, BorderLayout.CENTER);
        TOP_GUI.setOpaque(true);
        return TOP_GUI;
    }
    
    public void actionPerformed(ActionEvent e)  {
         if(e.getSource() == P2_add)
    {
        	row_deltav[0] = true;
        	row_deltav[1] = "" + max_deltav_ID;
        	max_deltav_ID++;
        	row_deltav[2] = "";
        	row_deltav[3] = "";
        	row_deltav[4] = "";
        	row_deltav[5] = "";
        	row_deltav[6] = "";
        	row_deltav[7] = "";	
        	model_deltav.addRow(row_deltav);
    } 
         
         else if(e.getSource() == P2_delete)
         {
         	int i = table_deltav.getSelectedRow();
         	if (i >= 0){
         		model_deltav.removeRow(i);
         	}
         	else{
         		System.out.println("Delete Error");
         	}
         }
         else if(e.getSource() == P2_up){
             DefaultTableModel model2 =  (DefaultTableModel)table_deltav.getModel();
             int[] rows2 = table_deltav.getSelectedRows();
             model2.moveRow(rows2[0],rows2[rows2.length-1],rows2[0]-1);
             table_deltav.setRowSelectionInterval(rows2[0]-1, rows2[rows2.length-1]-1);
             //----------------------------------------------------------------------
         }
         else if(e.getSource() == P2_down){
             DefaultTableModel model2 =  (DefaultTableModel)table_deltav.getModel();
             int[] rows2 = table_deltav.getSelectedRows();
             model2.moveRow(rows2[0],rows2[rows2.length-1],rows2[0]+1);
             table_deltav.setRowSelectionInterval(rows2[0]+1, rows2[rows2.length-1]+1);
             //------------------------------------------------------------------
         }
         
         if(e.getSource() == P3_add)
    {
        	row_equipment[0] = true;
        	row_equipment[1] = "" + max_equipment_ID;
        	max_equipment_ID++;
        	row_equipment[2] = "0";
        	row_equipment[3] = "";
        	row_equipment[4] = "";
        	row_equipment[5] = "";
        	row_equipment[6] = "";
        	row_equipment[7] = "";	
        	model_equipment.addRow(row_equipment);
    } 
         
         else if(e.getSource() == P3_delete)
         {
         	int i = table_equipment.getSelectedRow();
         	if (i >= 0){
         		model_equipment.removeRow(i);
         	}
         	else{
         		System.out.println("Delete Error");
         	}
         }
         else if(e.getSource() == P3_up){
             DefaultTableModel model2 =  (DefaultTableModel)table_equipment.getModel();
             int[] rows2 = table_equipment.getSelectedRows();
             model2.moveRow(rows2[0],rows2[rows2.length-1],rows2[0]-1);
             table_equipment.setRowSelectionInterval(rows2[0]-1, rows2[rows2.length-1]-1);
             //----------------------------------------------------------------------
         }
         else if(e.getSource() == P3_down){
             DefaultTableModel model2 =  (DefaultTableModel)table_equipment.getModel();
             int[] rows2 = table_equipment.getSelectedRows();
             model2.moveRow(rows2[0],rows2[rows2.length-1],rows2[0]+1);
             table_equipment.setRowSelectionInterval(rows2[0]+1, rows2[rows2.length-1]+1);
             //------------------------------------------------------------------
         }
    }
    
    public void Import_Case() {
  	   //--------------------------------------------------------
      	if (model_deltav.getRowCount()>0){
      	for (int i = model_deltav.getRowCount(); i >= 1; i--) {
      		model_deltav.removeRow(i-1);
      	}
      	}
      	if (model_equipment.getRowCount()>0){
      	for (int i = model_equipment.getRowCount(); i >= 1; i--) {
      		model_equipment.removeRow(i-1);
      	}
      	}

          try {
          	File myfile;
  			myfile = new File(BlueBook_main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
  			JFileChooser chooser = new JFileChooser(myfile);
  			
          chooser.setFileFilter(filter);
          JButton yellowButton = new JButton(); 
          int returnVal = chooser.showOpenDialog(yellowButton);
          if(returnVal == JFileChooser.APPROVE_OPTION) {
          	//System.out.println(chooser.getSelectedFile().getName());
          }
      	   try{
      		   //System.out.println(chooser.getSelectedFile().getName());
      		   
      		   String import_file = chooser.getSelectedFile().getName();
      		   myfile_opened = chooser.getSelectedFile();
      		   myfile_name = import_file;
      		  // FileLabel.setText("CASE : " + myfile_name.split("[.]")[0]);
      		   MAIN_frame.setTitle("" + PROJECT_TITLE + " | " + myfile_name.split("[.]")[0]);
      		    FileInputStream fstream = new FileInputStream(import_file);
      		          DataInputStream in = new DataInputStream(fstream);
      		          BufferedReader br = new BufferedReader(new InputStreamReader(in));
      		          String strLine;
      		          int index=0; 
      		          while ((strLine = br.readLine()) != null)   {
      		        	  if (index == 0 ){
      		        		//  String[] tokens = strLine.split(BB_delimiter);
      		        	  } else {
      		        		String[] tokens = strLine.split(BB_delimiter);
      		        		if (tokens[0].equals("|DELTAV|")) {                 // Delta-V INPUT 		        	  
      		        			for (int i = 0;i<model_deltav.getColumnCount();i++){
      		        				if (i<tokens.length-2){	
      		        					row_deltav[i] = (String) tokens[i+1];	
      		        				}
      		        				String str_int = tokens[1];
      						    	if (str_int.equals("true")) {
      						    		row_deltav[0] = true;
      						    	} else {
      						    		row_deltav[0] = false;
      						    	}
      		        			}
      		        			model_deltav.addRow(row_deltav);
      		        		} else if (tokens[0].equals("|SC|")) {					// S/C INPUT 	
      		        				String str_int = tokens[1];
      						    	if (str_int.equals("true")) {
      						    		row_equipment[0] = true;
      						    	} else {
      						    		row_equipment[0] = false;
      						    	}
      						    	row_equipment[1] = tokens[2];
            		            	row_equipment[1] = tokens[2];
            		            	row_equipment[2] = tokens[3];
            		            	row_equipment[3] = tokens[4];
            		            	row_equipment[4] = tokens[5];
            		            	row_equipment[5] = tokens[6];
            		            	row_equipment[6] = tokens[7];
            		            	row_equipment[7] = tokens[8];
            		            	row_equipment[8] = tokens[9];
            		            	
      		        			model_equipment.addRow(row_equipment);
          		            	//model_deltav.addRow(row_deltav);
      		        		} else {
      		        			System.out.println("Read Error - Line identifier not found");
      		        		}
      		        	  }
      		            	index++;
      		          }
      		          in.close();
      		          }catch (FileNotFoundException e1) {
      						// TODO Auto-generated catch block
      						e1.printStackTrace();
      					} catch (IOException e1) {
  						// TODO Auto-generated catch block
  						e1.printStackTrace();
  					}
  		} catch (URISyntaxException e2) {
  			// TODO Auto-generated catch block
  			e2.printStackTrace();
  		}
      }
    
    public void Save_Case() throws FileNotFoundException{
    	if ( myfile_name.isEmpty()==false) {
        	//File myfile = myfile_opened;
        	//String filePath = myfile.getAbsolutePath();
            File file = myfile_opened ; //new File(filePath + "\\" + myfile_name);
            PrintWriter os;
			os = new PrintWriter(file);
			//String head_line = Minit.getText() + BB_delimiter + Mpayload.getText() + BB_delimiter + cd_tf1.getText() + BB_delimiter + BoilOff_TF.getText() + BB_delimiter + FuelMar_TF.getText() + BB_delimiter + cbMenuItem_BO.isSelected() + BB_delimiter + cbMenuItem_SL.isSelected() + BB_delimiter + cbMenuItem_MAR.isSelected() + BB_delimiter;   
			//os.print(head_line);
			os.println("");
        	for (int i = 0; i < model_deltav.getRowCount(); i++) {  // 					DELTA-V TABLE
        		os.print("|DELTAV|" + BB_delimiter);
        	    for (int col = 0; col < model_deltav.getColumnCount(); col++) {
        	        os.print(model_deltav.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}
        	for (int i = 0; i < model_equipment.getRowCount(); i++) {  // 					S/C TABLE
        		os.print("|SC|" + BB_delimiter);
        	    for (int col = 0; col < model_equipment.getColumnCount(); col++) {;
        	        os.print(model_equipment.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}
            os.close();
    		}
    }
    
    public void SaveAs_Case(){
        try {
        	File myfile;
			myfile = new File(BlueBook_main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    	JFileChooser fileChooser = new JFileChooser(myfile);
    	JButton purpleButton = new JButton();
    	if (fileChooser.showSaveDialog(purpleButton) == JFileChooser.APPROVE_OPTION) {
    	 // File filer = fileChooser.getSelectedFile();
    	}
        File file = fileChooser.getSelectedFile() ;
        String filePath = file.getAbsolutePath();
            file = new File(filePath + ".csv");
    		myfile_opened = file;
            myfile_name = fileChooser.getSelectedFile().getName();
            //FileLabel.setText("CASE : " + myfile_name);
            MAIN_frame.setTitle("" + PROJECT_TITLE + " | " + myfile_name.split("[.]")[0]);
    	PrintWriter os;
		try {
			os = new PrintWriter(file);
			//String head_line = Minit.getText() + BB_delimiter + Mpayload.getText() + BB_delimiter + cd_tf1.getText() + BB_delimiter + BoilOff_TF.getText() + BB_delimiter + FuelMar_TF.getText() + BB_delimiter + cbMenuItem_BO.isSelected() + BB_delimiter + cbMenuItem_SL.isSelected() + BB_delimiter + cbMenuItem_MAR.isSelected() + BB_delimiter; 
			//os.print(head_line);
			os.println("");
        	for (int i = 0; i < model_deltav.getRowCount(); i++) {  // 					DELTA-V TABLE
        		os.print("|DELTAV|" + BB_delimiter);
        	    for (int col = 0; col < model_deltav.getColumnCount(); col++) {
        	        os.print(model_deltav.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}
        	for (int i = 0; i < model_equipment.getRowCount(); i++) {  // 					S/C TABLE
        		os.print("|SC|" + BB_delimiter);
        	    for (int col = 0; col < model_equipment.getColumnCount(); col++) {;
        	        os.print(model_equipment.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}
        	
            os.close();
            //System.out.println("Done!");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Error 01");
		}
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.out.println("Error 02");
		}
    }
    
    public void PrepareNewFile(){
    	
    }
    
    private static void createAndShowGUI() throws IOException{

        JFrame.setDefaultLookAndFeelDecorated(true);
        MAIN_frame = new JFrame("" + PROJECT_TITLE);
        //frame.setLayout(BB_BL);
        //Create and set up the content pane.
        BlueBook_LunarLander demo = new BlueBook_LunarLander();
        JPanel tp = demo.createContentPane();
        //tp.setPreferredSize(new java.awt.Dimension(x_init, y_init));
        //frame.setContentPane(demo.createContentPane());
        
        MAIN_frame.add(tp, BorderLayout.CENTER);
        MAIN_frame.pack();
        MAIN_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setSize(x_init, y_init);
        MAIN_frame.setVisible(true);
        MAIN_frame.setLocationRelativeTo(null);
        
        BufferedImage myImage = ImageIO.read(new File(PATH_LOGO));
        MAIN_frame.setIconImage(myImage);
        
        //new FormResize();
    }
    
    public static void main(String[] args) throws IOException {
  	  // PAYLOAD_LIST = new ArrayList<SubElement>();
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
              try {
					createAndShowGUI();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
          }
      });
  }
}