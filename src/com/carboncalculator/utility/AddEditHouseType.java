package com.carboncalculator.utility;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.carboncalculator.forms.HouseTypeViewPage;

public class AddEditHouseType extends JDialog {
	JButton JBUpdate = new JButton(new ImageIcon("images/save.png"));
	JButton JBReset = new JButton("Reset", new ImageIcon("images/reset.png"));
	JButton JBCancel = new JButton("Cancel", new ImageIcon("images/cancel.png"));

	JLabel JLPic1 = new JLabel();
	JLabel JLBanner = new JLabel("Please fill-up all the required fields.");

	JLabel JLHouseType = new JLabel("House Type:");
	JLabel JLNoOfBeds = new JLabel("No Of Beds:");

	JTextField JTFHouseType = new JTextField();
	JComboBox JCBNoOfBeds;

	Connection cnAEC;
	Statement stAEC;
	ResultSet rsAEC;

	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

	boolean ADDING_STATE;

	public AddEditHouseType(boolean ADD_STATE, JFrame OwnerForm, Connection srcCN, String srcSQL) {
		super(OwnerForm, true);
		cnAEC = srcCN;
		ADDING_STATE = ADD_STATE;
		try {
			stAEC = cnAEC.createStatement();
		} catch (SQLException sqlEx) {
			System.out.println("\nERROR IN frm_add_edit_House(frm_add_edit_House):" + sqlEx + "\n");
		}
		// Work
		JCBNoOfBeds = PublicMethods.fillComboBeds();
		//
		if (ADD_STATE == true) {
			JLPic1.setIcon(new ImageIcon("images/bNew.png"));
			setTitle("Add New House");
			JBUpdate.setText("Add");

		} else {
			JLPic1.setIcon(new ImageIcon("images/bModify.png"));
			setTitle("Modify House");
			JBUpdate.setText("Save");
			try {
				rsAEC = stAEC.executeQuery(srcSQL);
				rsAEC.next();
				JTFHouseType.setText("" + rsAEC.getString("House_Type"));
				// JCBFuelType.setSelectedItem("" +
				// rsAEC.getString("fueltype"));

			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.getMessage());
			}
		}
		JPanel JPContainer = new JPanel();
		JPContainer.setLayout(null);
		// -- Add the JLPic1
		JLPic1.setBounds(5, 5, 32, 32);
		JPContainer.add(JLPic1);

		// -- Add the JLBanner
		JLBanner.setBounds(55, 5, 268, 48);
		JLBanner.setFont(new Font("Dialog", Font.PLAIN, 12));
		JLBanner.setForeground(Color.RED);
		JPContainer.add(JLBanner);

		// ******************** Start adding of input field

		int height = 30;
		int heightGap = 32;
		int y = 50;

		// -- Add FirstName Input Field
		JLHouseType.setBounds(5, y, 105, height);
		JLHouseType.setFont(new Font("Dialog", Font.PLAIN, 12));

		JTFHouseType.setBounds(110, y, 200, height);
		JTFHouseType.setFont(new Font("Dialog", Font.PLAIN, 12));

		JPContainer.add(JLHouseType);
		JPContainer.add(JTFHouseType);

		// -- Add fueltype Input Field
		y += heightGap;
		JLNoOfBeds.setBounds(5, y, 105, height);
		JLNoOfBeds.setFont(new Font("Dialog", Font.PLAIN, 12));

		JCBNoOfBeds.setBounds(110, y, 200, height);
		JCBNoOfBeds.setFont(new Font("Dialog", Font.PLAIN, 12));

		JPContainer.add(JLNoOfBeds);
		JPContainer.add(JCBNoOfBeds);

		// ******************** End adding of input field

		// -- Add the JBUpdate
		y += heightGap;

		JBUpdate.setBounds(5, y, 105, 25);
		JBUpdate.setFont(new Font("Dialog", Font.PLAIN, 12));
		JBUpdate.setMnemonic(KeyEvent.VK_A);
		JBUpdate.addActionListener(JBActionListener);
		JBUpdate.setActionCommand("update");
		JPContainer.add(JBUpdate);

		// -- Add the JBReset
		JBReset.setBounds(112, y, 99, 25);
		JBReset.setFont(new Font("Dialog", Font.PLAIN, 12));
		JBReset.setMnemonic(KeyEvent.VK_R);
		JBReset.addActionListener(JBActionListener);
		JBReset.setActionCommand("reset");
		JPContainer.add(JBReset);

		// -- Add the JBCancel
		JBCancel.setBounds(212, y, 99, 25);
		JBCancel.setFont(new Font("Dialog", Font.PLAIN, 12));
		JBCancel.setMnemonic(KeyEvent.VK_C);
		JBCancel.addActionListener(JBActionListener);
		JBCancel.setActionCommand("cancel");
		JPContainer.add(JBCancel);

		getContentPane().add(JPContainer);
		setSize(325, 180);
		setResizable(false);
		setLocation((screen.width - 325) / 2, ((screen.height - 383) / 2));
	}

	private boolean RequiredFieldEmpty() {
		if (JTFHouseType.getText().equals("") || JCBNoOfBeds.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(null, "Some required fields is/are empty.\nPlease check it and try again.",
					"Carbon Calculator", JOptionPane.WARNING_MESSAGE);
			JTFHouseType.requestFocus();
			return true;
		} else {
			return false;
		}
	}

	/** reset the fields **/
	private void clearFields() {
		JTFHouseType.setText("");
		JCBNoOfBeds.setSelectedIndex(0);
	}

	//
	ActionListener JBActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String srcObj = e.getActionCommand();
			/** if add button is click **/
			if (srcObj == "update") {
				if (RequiredFieldEmpty() == false) {
					// If new entry is added
					if (ADDING_STATE == true) {
						try {
							int noOfBeds = 0;
							if (JCBNoOfBeds.getSelectedIndex() != -1) {
								noOfBeds = Integer.parseInt(JCBNoOfBeds.getSelectedItem().toString());
							}

							String insertSql = "INSERT INTO HouseType(House_Type, no_of_beds) " + "VALUES ('"
									+ JTFHouseType.getText() + "', " + noOfBeds + ")";

							System.out.println(insertSql);
							stAEC.executeUpdate(insertSql);

							// reload the house table
							HouseTypeViewPage.reloadRecord("SELECT * FROM HouseType ORDER BY House_ID ASC");
							// End Display the new record
							JOptionPane.showMessageDialog(null, "New House record has been successfully added.",
									"Carbon Calculator", JOptionPane.INFORMATION_MESSAGE);
							String ObjButtons[] = { "Yes", "No" };
							int PromptResult = JOptionPane.showOptionDialog(null, "Do you want add another record?",
									"Carbon Calculator", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
									ObjButtons, ObjButtons[0]);
							if (PromptResult == 0) {
								clearFields();
								JTFHouseType.requestFocus(true);
							} else {
								dispose();
							}
						} catch (SQLException sqlEx) {
							System.out.println(sqlEx.getMessage());
							JOptionPane.showMessageDialog(null,
									"Sorry we are not able to insert the record. \n Please contact your support team.",
									"Carbon Calculator", JOptionPane.INFORMATION_MESSAGE);
						}
					} else {
						// old entry is updated
						try {
							String RowIndex;
							RowIndex = rsAEC.getString("House_ID");
							int noOfBeds = 0;
							if (JCBNoOfBeds.getSelectedIndex() != -1) {
								noOfBeds = Integer.parseInt(JCBNoOfBeds.getSelectedItem().toString());
							}

							String updateSql = "UPDATE HouseType SET House_Type = '" + JTFHouseType.getText()
									+ "', no_of_beds = " + noOfBeds + " WHERE House_ID = " + RowIndex;

							System.out.println(updateSql);
							stAEC.executeUpdate(updateSql);

							// reload the house table
							HouseTypeViewPage.reloadRecord("SELECT * FROM HouseType ORDER BY House_ID DESC");
							JOptionPane.showMessageDialog(null, "Changes in the record has been successfully save.",
									"Carbon Calculator", JOptionPane.INFORMATION_MESSAGE);
							RowIndex = "";
							dispose();
						} catch (SQLException sqlEx) {
							System.out.println(sqlEx.getMessage());
							JOptionPane.showMessageDialog(null,
									"Sorry we are not able to update the record. \n Please contact your support team.",
									"Carbon Calculator", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			} else if (srcObj == "reset") {
				clearFields();
			} else if (srcObj == "cancel") {
				dispose();
			}
		}
	};

}
