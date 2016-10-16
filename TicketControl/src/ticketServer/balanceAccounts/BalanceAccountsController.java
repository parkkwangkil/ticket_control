package ticketServer.balanceAccounts;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class BalanceAccountsController implements Initializable {
	@FXML private Button btnHome;
	@FXML private Button btnRealTimeSales;
	@FXML private Button btnSalesStatistics;
	@FXML private Button btnMealMenu;
	@FXML private Button btnConfiguration;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		btnHome.setOnAction(event->handleBtnHomeAction(event));
		btnRealTimeSales.setOnAction((event)->handleBtnRealTimeSalesAction(event));
		btnSalesStatistics.setOnAction(event->handlebtnSalesStatisticsAction(event));
		btnMealMenu.setOnAction(event->handleBtnMealMenuAction(event));
		btnConfiguration.setOnAction(event->handleBtnConfigurationAction(event));
	}
	
	public void handleBtnHomeAction(ActionEvent event){
		try {
			Parent homeRoot = FXMLLoader.load(getClass().getResource("..\\administratorMain\\AdministratorMain.fxml"));
			Scene scene = new Scene(homeRoot);
			Stage primaryStage = (Stage) btnHome.getScene().getWindow();
			primaryStage.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void handleBtnRealTimeSalesAction(ActionEvent event){
		try {
			Parent realTimeSalesRoot = FXMLLoader.load(getClass().getResource("..\\realTimeSales\\RealTimeSales.fxml"));
			Scene scene = new Scene(realTimeSalesRoot);
			Stage primaryStage = (Stage) btnRealTimeSales.getScene().getWindow();
			primaryStage.setScene(scene);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handlebtnSalesStatisticsAction(ActionEvent event){
		try {
			Parent salesStatisticsRoot = FXMLLoader.load(getClass().getResource("..\\salesStatistics\\SalesStatistics.fxml"));
			Scene scene = new Scene(salesStatisticsRoot);
			Stage primaryStage = (Stage) btnSalesStatistics.getScene().getWindow();
			primaryStage.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void handleBtnMealMenuAction(ActionEvent event){
		try {
			Parent mealMenuRoot = FXMLLoader.load(getClass().getResource("..\\mealMenu\\MealMenu.fxml"));
			Scene scene = new Scene(mealMenuRoot);
			Stage primaryStage = (Stage) btnMealMenu.getScene().getWindow();
			primaryStage.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void handleBtnConfigurationAction(ActionEvent event){
		try {
			Parent configurationRoot = FXMLLoader.load(getClass().getResource("..\\configuration\\Configuration.fxml"));
			Scene scene = new Scene(configurationRoot);
			Stage primaryStage = (Stage) btnConfiguration.getScene().getWindow();
			primaryStage.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
