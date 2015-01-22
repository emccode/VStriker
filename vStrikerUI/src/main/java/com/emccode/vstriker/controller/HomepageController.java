package com.emccode.vstriker.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import vStrikerBizModel.AccountBiz;
import vStrikerBizModel.AccountDetailBiz;
import vStrikerBizModel.ApiBiz;
import vStrikerEntities.Account;
import vStrikerEntities.Api;
import vStrikerEntities.VwAccountDetail;
import vStrikerTestEngine.Engine;
import vStrikerTestEngine.VEngine;

import com.emccode.vstriker.VStriker;

//@author Sanjeev Chauhan

public class HomepageController {
	@FXML private TabPane tbMain; 
	@FXML private Tab accountTab;
	@FXML private Tab tbAccount;
	@FXML private Tab tbExecution;
	@FXML private Tab tbCfg;
	
	@FXML
	private TableView<VwAccountDetail> accountTable;
	@FXML
	private TableColumn<VwAccountDetail, Boolean> selectColumn;
	@FXML
	private TableColumn<VwAccountDetail, String> nameColumn;
	@FXML
	private TableColumn<VwAccountDetail, String> locationColumn;
	@FXML
	private TableColumn<VwAccountDetail, String> S3Column;
	@FXML
	private TableColumn<VwAccountDetail, String> SwiftColumn;
	@FXML
	private TableColumn<VwAccountDetail, String> AtmosColumn;
	@FXML
	private Button addButton;
	@FXML
	private Button validateButton;
	@FXML
	private Button updateButton;
	@FXML
	private Button deleteButton;
	@FXML
	private TableView<VwAccountDetail> accountDetail;
	@FXML
	private TableColumn<VwAccountDetail, String> APIColumn;
	@FXML
	private TableColumn<VwAccountDetail, String> ProtocolColumn;
	@FXML
	private TableColumn<VwAccountDetail, String> PortColumn;
	@FXML
	private TableColumn<VwAccountDetail, String> KeyColumn;
	@FXML
	private TableColumn<VwAccountDetail, String> EndPointColumn;
	@FXML
	private Button configureButton;

	private VStriker vStriker;
	private ObservableList<VwAccountDetail> accountData;
	private ObservableList<Account> acctList;
	private List<BooleanProperty> selectedRowList;

	@FXML private Parent configurationView;

	@FXML private ConfigurationController configurationViewController;

	@FXML private Parent resultsView;

	@FXML private ResultsController resultsViewController;
	// Constructor
	public HomepageController() {
	}

	// Initialize
	@FXML
	private void initialize() {
		System.out.println("In HomepageController initialize");
		
		// Add Tab Listener
		tbMain.getSelectionModel().selectedItemProperty().addListener(
			    new ChangeListener<Tab>() {
			        @Override
			        public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
			           if(t1.getId().equals("tbExecution"))
			           {
			        	   vStriker.SetTitle("vStriker:Test Execution");
			        	   resultsViewController.LoadLists();
			           }
			           
			           if(t1.getId().equals("tbCfg"))
			           {
			        	   vStriker.SetTitle("vStriker:Configuration");
			        	   resultsViewController.LoadLists();
			           }
			      
			           if(t1.getId().equals("tbAccount"))
			           {
			        	   vStriker.SetTitle("vStriker");
			        	   resultsViewController.LoadLists();
			           }
			        }
			    }
			);
		
		assert accountTab != null : "fx:id=\"accountTab\" was not injected: check your FXML file 'Home.fxml'.";
		// Populate the name column
		nameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
				cellData.getValue().getName()));
		// Populate the location column
		locationColumn
				.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
						cellData.getValue().getAccountLocation()));

		List<BooleanProperty> selectedRowList = setupCheckboxColumn();
		// Populating the Checkbox Column
		selectColumn
				.setCellFactory(CheckBoxTableCell
						.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
							public ObservableValue<Boolean> call(Integer index) {
								System.out.println("Index is: " + index);
								return selectedRowList.get(index);
							}
						}));

		// Populating the API Columns
		// S3 Column
		S3Column.setCellValueFactory(new Callback<CellDataFeatures<VwAccountDetail, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(
					CellDataFeatures<VwAccountDetail, String> p) {
				if (p.getValue().getApiTypeName() != null
						&& p.getValue().getApiTypeName().contains("S3")) {
					return new SimpleStringProperty("Yes");
				} else {
					return new SimpleStringProperty("No");
				}
			}
		});
		// Swift Column
		SwiftColumn
				.setCellValueFactory(new Callback<CellDataFeatures<VwAccountDetail, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(
							CellDataFeatures<VwAccountDetail, String> p) {
						if (p.getValue().getApiTypeName() != null
								&& p.getValue().getApiTypeName()
										.contains("Swift")) {
							return new SimpleStringProperty("Yes");
						} else {
							return new SimpleStringProperty("No");
						}
					}
				});
		// Atmos Column
		AtmosColumn
				.setCellValueFactory(new Callback<CellDataFeatures<VwAccountDetail, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(
							CellDataFeatures<VwAccountDetail, String> p) {
						if (p.getValue().getApiTypeName() != null
								&& p.getValue().getApiTypeName()
										.contains("Atmos")) {
							return new SimpleStringProperty("Yes");
						} else {
							return new SimpleStringProperty("No");
						}
					}
				});
		// Make the checkbox column editable
		selectColumn.setEditable(true);
		accountTable.setEditable(true);
	}

	// Set the main application
	public void setVStrikerApp(VStriker vStriker) {
		System.out.println("In Homepage Controller setVStrikerApp");
		this.vStriker = vStriker;
		configurationViewController.setVStrikerApp(vStriker);
		resultsViewController.setVStrikerApp(vStriker);
		
		
		new AccountDetailBiz();
		// javafx.collections.ObservableList<VwAccountDetail> accountData;
		try {
			accountData = FXCollections.observableArrayList(AccountDetailBiz
					.AccountSelectAll());
			accountTable.setItems(accountData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Add account button clicked
	@FXML
	public void addAccountClicked(ActionEvent event) {
		System.out.println("Add account button clicked");
		vStriker.showAccount();
	}

	// Validate account button clicked
	@FXML
	public void validateAccountClicked(ActionEvent event) {
		System.out.println("Validate account button clicked");
		int selectedRow = getSelectedRow();
		if (selectedRow == -1) {
			System.out.println("Please select an Account to validate");
			return;
		}
		ObservableList<VwAccountDetail> accts = accountTable.getItems();
		try {
			List<Api> listApi = ApiBiz.ApiSelectforAccount((accts.get(selectedRow).getAccountId()));
			for (Api a: listApi) {
				switch (a.getApiType().getApiTypeName()) {
				
				case "S3":
					if (a.getSecretKey() != null && a.getUrl() != null  && a.getSubtenant() != null) {
						Engine e = new VEngine();
						if (e.validateS3Connection(a.getSubtenant(),
								a.getSecretKey(), a.getUrl(), "")) {
							System.out.println("S3 connection is validated");
						} else {
							System.out.println("S3 connection is not working");
						}
					}
					break;
				case "Atmos":
					System.out.println("Atmos");
					break;
				case "Swift":
					System.out.println("Swift");
					break;
				default:
					System.out.println("Please check to ensure the right type of Api is set");
				}
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println("Unable to validate account Apis");	
 			e1.printStackTrace();
		}

	}

	public void setTab(int i)
	{
		SingleSelectionModel<Tab> tb = tbMain.getSelectionModel();
		tb.select(i);
		if(i==2)
			resultsViewController.LoadLists();
		
	}
	
	// Update account button clicked
	@FXML
	public void updateAccountClicked(ActionEvent event) {
		System.out.println("Update account button clicked");
		int selectedRow = getSelectedRow();
		if (selectedRow == -1) {
			System.out.println("Please select an Account to update");
			return;
		}
		vStriker.updateAccount(accountTable.getItems().get(selectedRow));
	}

	// Delete account button clicked
	@FXML
	public void deleteAccountClicked(ActionEvent event) {
		System.out.println("Delete account button clicked");
		int selectedRow = 0;
		for (BooleanProperty b: selectedRowList) {
			if (b.getValue()) {
				selectedRow = selectedRowList.indexOf(b);
				System.out.println("selected row is: " + selectedRow);
			}
		}
		if (selectedRow == 0) {
			System.out.println("Please select a row to delete");
			return;
		}
		ObservableList<VwAccountDetail> accts = accountTable.getItems();

		// Delete rows from the API table
		try {
		
			ApiBiz.ApiDeleteByAcount(accts.get(selectedRow).getAccountId());
			ObservableList<VwAccountDetail> acctDetails = accountDetail.getItems();
			acctDetails.removeAll(acctDetails);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println("Unable to delete rows in Api table");	
 			e1.printStackTrace();
		}
		// Delete the account from Account table
		try {
			AccountBiz.AccountDelete(accts.get(selectedRow).getAccountId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to delete account");
			e.printStackTrace();
		}
		accts.remove(selectedRow);
		// Clear checkboxes
		for(BooleanProperty b: selectedRowList) {
			b.setValue(false);
		}
	}

	// Configure account button clicked
	@FXML
	public void configureAccountClicked(ActionEvent event) {
		System.out.println("Configure account button clicked");
		vStriker.showAddConfiguration();
	}

	private List<BooleanProperty> setupCheckboxColumn() {
		// Associate a BooleanProperty to every cell in the first column.
		selectedRowList = new ArrayList<BooleanProperty>();
		try {
			// Create a boolean property for every account that exists
			acctList = FXCollections.observableArrayList(AccountBiz
					.AccountSelectAll());
			for (Account a : acctList) {
				selectedRowList.add(new SimpleBooleanProperty());
			}
			// Add a listener for each boolean property
			for (BooleanProperty b : selectedRowList) {
				b.addListener(new ChangeListener<Boolean>() {
					public void changed(ObservableValue<? extends Boolean> obs,
							Boolean wasSelected, Boolean isSelected) {
						System.out.println("isSelected: " + isSelected);
						// Change all other BooleanProperty to false
						if (b.getValue()) {
							// This means b just was selected so every other
							// property should be unchecked
							for (BooleanProperty bo : selectedRowList) {
								if (b != bo)
									bo.setValue(false);
							}
							// Show API details in the table below for Acct
							// selected.
							showAcctAPIDetails(acctList.get(selectedRowList
									.indexOf(b)));
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return selectedRowList;
	}

	private void showAcctAPIDetails(Account account) {
		System.out.println("In showAcctAPIDetaisl");
		System.out.println("Name: " + account.getName() + "Location: "
				+ account.getAccountLocation() + "Id: "
				+ account.getAccountId());
		ObservableList<VwAccountDetail> selectedAcct = FXCollections.observableArrayList();
		for (VwAccountDetail a : accountData) {
			if (a.getAccountId() == account.getAccountId())
				selectedAcct.add(a);
		}
		accountDetail.setItems(selectedAcct);
		// Populate columns in the details table
		APIColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
				cellData.getValue().getApiTypeName()));
		KeyColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
				cellData.getValue().getSecretKey()));
		EndPointColumn
				.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
						cellData.getValue().getUrl()));

	}
	
	private int getSelectedRow() {
		int selectedRow = 0;
		for (BooleanProperty b: selectedRowList) {
			if (b.getValue()) {
				selectedRow = selectedRowList.indexOf(b);
				System.out.println("selected row is: " + selectedRow);
				return selectedRow;
			}
		}
			return -1;
	}
}
