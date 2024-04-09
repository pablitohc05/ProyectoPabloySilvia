package proyuctoConjunto;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ProyectoConjunto {

	private JFrame frame;
	private JTable table;
	private JTextField txtFieldCod;
	private JTextField txtFieldNombre;
	private JTextField txtFieldHoras;

	/**
	 * Launch the application.
	 */
	
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProyectoConjunto window = new ProyectoConjunto();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ProyectoConjunto() {
		connect();
		initialize();
		loadData();
		
		
	}
	
	Connection con;
	PreparedStatement pst;
	ResultSet rs;
	
	public void connect() {
		try{
			con=ConnectionSingleton.getConnection();
		}catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
	}
	
	public void loadData() {
		try {
			pst = con.prepareStatement("Select * from asignaturas");
			rs = pst.executeQuery();
			table.setModel(DbUtils.resultSetToTableModel(rs));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void clear() {
		txtFieldCod.setText("");
		txtFieldNombre.setText("");
		txtFieldHoras.setText("");

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 770, 551);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Codigo");
		model.addColumn("Nombre");
		model.addColumn("Horas");
		try {
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM asignaturas");
		while (rs.next()) {
		 Object[] row = new Object[3];
		 row[0] = rs.getInt("cod");
		 row[1] = rs.getString("nombre");
		 row[2] = rs.getInt("horas");
		 model.addRow(row);
		}
		}catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	
}
		
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(26, 61, 311, 171);
		frame.getContentPane().add(scrollPane);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = table.getSelectedRow();
				TableModel model = table.getModel();
				//Codigo, nombre y horas
				txtFieldCod.setText(model.getValueAt(index, 0).toString());
				txtFieldNombre.setText(model.getValueAt(index, 1).toString());
				txtFieldHoras.setText(model.getValueAt(index, 2).toString());
			}
		});
		
		JLabel lblCodigo = new JLabel("Codigo:");
		lblCodigo.setBounds(36, 354, 70, 15);
		frame.getContentPane().add(lblCodigo);
		
		txtFieldCod = new JTextField();
		txtFieldCod.setEditable(false);
		txtFieldCod.setBounds(105, 352, 114, 19);
		frame.getContentPane().add(txtFieldCod);
		txtFieldCod.setColumns(10);
		
		txtFieldNombre = new JTextField();
		txtFieldNombre.setBounds(105, 381, 114, 19);
		frame.getContentPane().add(txtFieldNombre);
		txtFieldNombre.setColumns(10);
		
		JLabel lblNombre = new JLabel("Nombre: ");
		lblNombre.setBounds(36, 383, 70, 15);
		frame.getContentPane().add(lblNombre);
		
		JLabel lblHoras = new JLabel("Horas:");
		lblHoras.setBounds(36, 412, 70, 15);
		frame.getContentPane().add(lblHoras);
		
		txtFieldHoras = new JTextField();
		txtFieldHoras.setBounds(105, 410, 114, 19);
		frame.getContentPane().add(txtFieldHoras);
		txtFieldHoras.setColumns(10);
		
		
		//Boton GUARDAR
		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String nombre = txtFieldNombre.getText();
				String horas = txtFieldHoras.getText();

				if (nombre == null || nombre.isEmpty() || nombre.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Inserte nombre");
					txtFieldNombre.requestFocus();
					return;
				}
				if (horas == null || horas.isEmpty() || horas.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Inserte horas");
					txtFieldHoras.requestFocus();
					return;
				}
				try {
					String sql = "insert into asignaturas (nombre,horas) values (?,?)";
					pst = con.prepareStatement(sql);
					pst.setString(1, nombre);
					pst.setString(2, horas);
					pst.executeUpdate();
					JOptionPane.showMessageDialog(null, "Data insert Success");
					clear();
					loadData();

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnGuardar.setBounds(599, 50, 117, 25);
		frame.getContentPane().add(btnGuardar);
		
		
		
		// Boton BORRAR
		JButton btnBorrar = new JButton("Borrar");
		btnBorrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String codigo = txtFieldCod.getText();
				if (!txtFieldCod.getText().isEmpty()) {
					int result = JOptionPane.showConfirmDialog(null, "Estas seguro?", "Delete",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	
				if (result == JOptionPane.YES_OPTION) {
					try {
						String sql = "delete from asignaturas where cod=?";
						pst = con.prepareStatement(sql);
						pst.setString(1, codigo);
						pst.executeUpdate();
						JOptionPane.showMessageDialog(null, "Eliminado Correctamente");
						clear();
						loadData();

					} catch (SQLException e1) {
						e1.printStackTrace();
						}
					}
				}
			}
		});
		btnBorrar.setBounds(599, 153, 117, 25);
		frame.getContentPane().add(btnBorrar);
		
		
		//Boton ACTUALIZAR
		JButton btnActualizar = new JButton("Actualizar");
		btnActualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String codigo = txtFieldCod.getText();
				String nombre = txtFieldNombre.getText();
				String horas = txtFieldHoras.getText();
				
				if (nombre == null || nombre.isEmpty() || nombre.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Inserte nombre");
					txtFieldNombre.requestFocus();
					return;
				}
				if (horas == null || horas.isEmpty() || horas.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Inserte horas");
					txtFieldHoras.requestFocus();
					return;
				}
				
				try {
					String sql = "update asignaturas set nombre=?,horas=? where cod=?";
					pst = con.prepareStatement(sql);
					pst.setString(1, nombre);
					pst.setString(2, horas);
					pst.setString(3, codigo);
					pst.executeUpdate();
					JOptionPane.showMessageDialog(null, "Actualizacion Correcta");
					clear();
					loadData();

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		btnActualizar.setBounds(599, 104, 117, 25);
		frame.getContentPane().add(btnActualizar);
		
	}
}
