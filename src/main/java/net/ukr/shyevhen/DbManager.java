package net.ukr.shyevhen;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class DbManager {
	private Connection conn;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public DbManager() {
		super();
	}

	public Connection getConn() {
		return conn;
	}

	public void connectToDB() {
		InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties");
		Properties prop = new Properties();
		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		String user = prop.getProperty("db.user");
		String password = prop.getProperty("db.password");
		String url = prop.getProperty("db.url");
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clearDB() {
		try (Statement st = conn.createStatement()) {
			st.execute("DROP TABLE IF EXISTS Orders");
			st.execute("DROP TABLE IF EXISTS Goods");
			st.execute("DROP TABLE IF EXISTS Clients");
			st.execute("CREATE TABLE Clients (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
					+ "full_name VARCHAR(20) NOT NULL,	company VARCHAR(20) DEFAULT NULL,"
					+ "email VARCHAR(30) NOT NULL, phone VARCHAR(13) NOT NULL," + "address VARCHAR(35) NOT NULL)");
			st.execute("CREATE TABLE Goods (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
					+ "title VARCHAR(30) NOT NULL, model VARCHAR(10) NOT NULL,"
					+ "description VARCHAR(255) DEFAULT NULL,	price DEC(15,2) NOT NULL," + "stock INT DEFAULT NULL)");
			st.execute("CREATE TABLE Orders (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
					+ "client_id INT NOT NULL, goods_id INT NOT NULL,	amount INT NOT NULL,"
					+ "total_price DEC(20,2) NOT NULL, date_order DATETIME NOT NULL, complite DATE DEFAULT NULL,"
					+ "FOREIGN KEY(client_id) REFERENCES Clients(id)," + "FOREIGN KEY(goods_id) REFERENCES Goods(id))");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addGoods(String title, String model, String description, BigDecimal price, int stock) {
		try (PreparedStatement ps = conn
				.prepareStatement("INSERT INTO Goods (title,model,description,price,stock) VALUES (?,?,?,?,?)")) {
			ps.setString(1, title);
			ps.setString(2, model);
			ps.setString(3, description);
			ps.setBigDecimal(4, price);
			ps.setInt(5, stock);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void changeGoods(int id, String name, String value) {
		try (PreparedStatement ps = conn.prepareStatement("UPDATE Goods SET " + name + "=? WHERE id = ?");
				PreparedStatement psTwo = conn.prepareStatement("SELECT * FROM Goods WHERE id=?")) {
			ps.setString(1, value);
			ps.setInt(2, id);
			ps.executeUpdate();
			psTwo.setInt(1, id);
			try (ResultSet rs = psTwo.executeQuery()) {
				goodsTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void changeGoods(int id, BigDecimal value) {
		try (PreparedStatement ps = conn.prepareStatement("UPDATE Goods SET price=? WHERE id = ?");
				PreparedStatement psTwo = conn.prepareStatement("SELECT * FROM Goods WHERE id=?")) {
			ps.setBigDecimal(1, value);
			ps.setInt(2, id);
			ps.executeUpdate();
			psTwo.setInt(1, id);
			try (ResultSet rs = psTwo.executeQuery()) {
				goodsTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void changeGoods(int id, int value) {
		try (PreparedStatement ps = conn.prepareStatement("UPDATE Goods SET stock=? WHERE id = ?");
				PreparedStatement psTwo = conn.prepareStatement("SELECT * FROM Goods WHERE id=?")) {
			ps.setInt(1, value);
			ps.setInt(2, id);
			ps.executeUpdate();
			psTwo.setInt(1, id);
			try (ResultSet rs = psTwo.executeQuery()) {
				goodsTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delGoods(int id) {
		try (PreparedStatement psOr = conn.prepareStatement("SELECT id FROM Orders WHERE goods_id = ?");
				PreparedStatement psDel = conn.prepareStatement("DELETE FROM Goods WHERE id = ?")) {
			psOr.setInt(1, id);
			try (ResultSet rs = psOr.executeQuery()) {
				while (rs.next()) {
					delOrder(rs.getInt(1));
				}
			}
			psDel.setInt(1, id);
			psDel.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void goodsList() {
		try (Statement st = conn.createStatement()) {
			try (ResultSet rs = st.executeQuery("SELECT * FROM Goods")) {
				goodsTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getTheGoods(int id) {
		try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Goods WHERE id=?")) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				goodsTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getTheGoods(String name, String value) {
		try {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				if (name.equals("title")) {
					System.out.println("title");
					ps = conn.prepareStatement("SELECT * FROM Goods WHERE title like ?");
				} else if (name.equals("model")) {
					System.out.println("model");
					ps = conn.prepareStatement("SELECT * FROM Goods WHERE model like ?");
				}
				ps.setString(1, "%" + value + "%");
				rs = ps.executeQuery();
				goodsTabl(rs);
			} finally {
				rs.close();
				ps.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getTheGoods(int min, int max) {
		try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Goods WHERE price>=? AND price<=?")) {
			ps.setInt(1, min);
			ps.setInt(2, max);
			try (ResultSet rs = ps.executeQuery()) {
				goodsTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getStockGoods() {
		try (Statement st = conn.createStatement()) {
			try (ResultSet rs = st.executeQuery("SELECT * FROM Goods WHERE stock>=1")) {
				goodsTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void goodsTabl(ResultSet rs) throws SQLException {
		StringBuilder sb = new StringBuilder();
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			sb.append(rsmd.getColumnName(i) + "\t\t|");
		}
		sb.append(System.lineSeparator());
		while (rs.next()) {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (i > 1 && i < 5) {
					sb.append(rs.getString(i) + "\t\t|");
				} else if (i == 1 || i == 6) {
					sb.append(rs.getInt(i) + "\t\t|");
				} else if (i == 5) {
					sb.append(rs.getBigDecimal(i) + "\t\t|");
				}
			}
			sb.append(System.lineSeparator());
		}
		System.out.println(sb.toString());
	}

	public void addClient(String full_name, String company, String email, String phone, String address) {
		try (PreparedStatement ps = conn.prepareStatement(
				"INSERT INTO Clients (full_name, company, email, phone, address) VALUES (?,?,?,?,?)")) {
			ps.setString(1, full_name);
			ps.setString(2, company);
			ps.setString(3, email);
			ps.setString(4, phone);
			ps.setString(5, address);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void changeClient(int id, String name, String value) {
		try (PreparedStatement ps = conn.prepareStatement("UPDATE Clients SET " + name + " = ? WHERE id = ?");
				PreparedStatement psTwo = conn.prepareStatement("SELECT * FROM Clients WHERE id = ?")) {
			ps.setString(1, value);
			ps.setInt(2, id);
			ps.executeUpdate();
			psTwo.setInt(1, id);
			try (ResultSet rs = psTwo.executeQuery()) {
				clientsTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delClient(int id) {
		try (PreparedStatement psOr = conn.prepareStatement("SELECT id FROM Orders WHERE client_id = ?");
				PreparedStatement psDel = conn.prepareStatement("DELETE FROM Clients WHERE id = ?")) {
			psOr.setInt(1, id);
			try (ResultSet rs = psOr.executeQuery()) {
				while (rs.next()) {
					delOrder(rs.getInt(1));
				}
			}
			psDel.setInt(1, id);
			psDel.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clientsList() {
		try (Statement st = conn.createStatement()) {
			try (ResultSet rs = st.executeQuery("SELECT * FROM Clients")) {
				clientsTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchClients(String name, String value) {
		try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Clients WHERE " + name + " LIKE ?");) {
			ps.setString(1, "%" + value + "%");
			ResultSet rs = ps.executeQuery();
			clientsTabl(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchClients(int value) {
		try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Clients WHERE id=?");) {
			ps.setInt(1, value);
			ResultSet rs = ps.executeQuery();
			clientsTabl(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void clientsTabl(ResultSet rs) throws SQLException {
		StringBuilder sb = new StringBuilder();
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			sb.append(rsmd.getColumnName(i) + "\t\t|");
		}
		sb.append(System.lineSeparator());
		while (rs.next()) {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (i > 1) {
					sb.append(rs.getString(i) + "\t\t|");
				} else if (i == 1) {
					sb.append(rs.getInt(i) + "\t\t|");
				}
			}
			sb.append(System.lineSeparator());
		}
		System.out.println(sb.toString());
	}

	public void addOrder(int client_id, int goods_id, int amount) {
		try (PreparedStatement ps = conn.prepareStatement(
				"INSERT INTO Orders (client_id,goods_id,amount,total_price,date_order) " + "VALUES (?, ?, ?, ?, ?)");
				PreparedStatement psTwo = conn.prepareStatement("SELECT price,stock FROM Goods WHERE id = ?");
				PreparedStatement psThree = conn.prepareStatement("UPDATE Goods SET stock = ? WHERE id = ?")) {
			psTwo.setInt(1, goods_id);
			try (ResultSet rs = psTwo.executeQuery()) {
				rs.next();
				int stock = rs.getInt(2);
				if (stock >= amount) {
					ps.setInt(1, client_id);
					ps.setInt(2, goods_id);
					ps.setInt(3, amount);
					BigDecimal totPrice = BigDecimal.valueOf(amount).multiply(rs.getBigDecimal(1));
					ps.setBigDecimal(4, totPrice);
					ps.setString(5, sdf.format(new java.util.Date()));
					ps.executeUpdate();
					psThree.setInt(1, stock - amount);
					psThree.setInt(2, goods_id);
					psThree.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void changeOrder(int id, String name, int value) {
		try (PreparedStatement psOrd = conn.prepareStatement("SELECT goods_id,amount FROM Orders WHERE id = ?")) {
			PreparedStatement ps = null;
			try {
				if (name.equals("amount")) {
					ps = conn.prepareStatement("UPDATE Orders SET amount = ?,total_price = ? WHERE id = ?");
					psOrd.setInt(1, id);
					changeAmount(id, value, psOrd, ps);
				} else if (name.equals("client_id")) {
					ps = conn.prepareStatement("UPDATE Orders SET client_id = ? WHERE id = ?");
					ps.setInt(1, value);
					ps.setInt(2, id);
					ps.executeUpdate();
				}
			} finally {
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void changeAmount(int id, int value, PreparedStatement psOrd, PreparedStatement ps) throws SQLException {
		ResultSet rsOrd = null;
		ResultSet rsGet = null;
		try (PreparedStatement psGet = conn.prepareStatement("SELECT stock,price FROM Goods WHERE id = ?");
				PreparedStatement psUpdate = conn.prepareStatement("UPDATE Goods SET stock = ? WHERE id = ?")) {
			rsOrd = psOrd.executeQuery();
			rsOrd.next();
			int goodsId = rsOrd.getInt(1);
			int oldValue = rsOrd.getInt(2);
			psGet.setInt(1, goodsId);
			rsGet = psGet.executeQuery();
			rsGet.next();
			int stock = rsGet.getInt(1);
			if ((stock + oldValue - value) >= 0) {
				psUpdate.setInt(1, (stock + oldValue - value));
				psUpdate.setInt(2, goodsId);
				psUpdate.executeUpdate();
				ps.setInt(1, value);
				ps.setBigDecimal(2, BigDecimal.valueOf(value).multiply(rsGet.getBigDecimal(2)));
				ps.setInt(3, id);
				ps.executeUpdate();
			}
		} finally {
			rsOrd.close();
			rsGet.close();
		}
	}

	public void delOrder(int id) {
		try (PreparedStatement psGetOr = conn
				.prepareStatement("SELECT goods_id, amount, complite FROM Orders WHERE id = ?");
				PreparedStatement psGetG = conn.prepareStatement("SELECT stock FROM Goods WHERE id = ?");
				PreparedStatement psUpd = conn.prepareStatement("UPDATE Goods SET stock = ? WHERE id = ?");
				PreparedStatement psDel = conn.prepareStatement("DELETE FROM Orders WHERE id = ?")) {
			psGetOr.setInt(1, id);
			try (ResultSet rsOr = psGetOr.executeQuery();) {
				rsOr.next();
				if (rsOr.getString(3) == null) {
					int goodsId = rsOr.getInt(1);
					psGetG.setInt(1, goodsId);
					try (ResultSet rsG = psGetG.executeQuery()) {
						rsG.next();
						psUpd.setInt(1, rsG.getInt(1) + rsOr.getInt(2));
						psUpd.setInt(2, goodsId);
						psUpd.executeUpdate();
					}
				}
			}
			psDel.setInt(1, id);
			psDel.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void compliteOrder(int id) {
		try (PreparedStatement ps = conn.prepareStatement("UPDATE Orders SET complite = ? WHERE id = ?");
				PreparedStatement psCom = conn.prepareStatement("SELECT complite FROM Orders WHERE id = ?")) {
			psCom.setInt(1, id);
			try (ResultSet rs = psCom.executeQuery()) {
				rs.next();
				if (rs.getString(1) == null) {
					ps.setString(1, sdf.format(new java.util.Date()));
					ps.setInt(2, id);
					ps.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void ordersList() {
		try (Statement st = conn.createStatement()) {
			try (ResultSet rs = st.executeQuery("SELECT * FROM Orders")) {
				ordersTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fullOrdersList() {
		try (Statement st = conn.createStatement()) {
			try (ResultSet rs = st.executeQuery("SELECT Orders.id, Orders.client_id, Clients.full_name,"
					+ "Clients.phone,Clients.address, Orders.goods_id,Goods.title,Goods.model,Goods.price, "
					+ "Orders.amount, Orders.total_price, Orders.date_order Orders.complite FROM Orders,Clients,"
					+ "Goods WHERE Orders.client_id=Clients.id AND Orders.goods_id=Goods.id;")) {
				fullOrdersTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchOrders(String name, int client_id) {
		try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Orders WHERE " + name + "=?")) {
			ps.setInt(1, client_id);
			try (ResultSet rs = ps.executeQuery()) {
				ordersTabl(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void ordersTabl(ResultSet rs) throws SQLException {
		StringBuilder sb = new StringBuilder();
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			sb.append(rsmd.getColumnName(i) + "\t\t|");
		}
		sb.append(System.lineSeparator());
		while (rs.next()) {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if ((i >= 1 && i < 5) || i >= 6) {
					sb.append(rs.getString(i) + "\t\t|");
				} else if (i == 5) {
					sb.append(rs.getBigDecimal(i) + "\t\t|");
				}
			}
			sb.append(System.lineSeparator());
		}
		System.out.println(sb.toString());
	}

	private void fullOrdersTabl(ResultSet rs) throws SQLException {
		StringBuilder sb = new StringBuilder();
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			sb.append(rsmd.getColumnName(i) + "\t\t|");
		}
		sb.append(System.lineSeparator());
		while (rs.next()) {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if ((i > 2 && i < 6) || i == 7 || i == 8 || i == 12) {
					sb.append(rs.getString(i) + "\t\t|");
				} else if (i == 1 || i == 2 || i == 6 || i == 10) {
					sb.append(rs.getInt(i) + "\t\t|");
				} else if (i == 9 || i == 11) {
					sb.append(rs.getBigDecimal(i) + "\t\t|");
				}
			}
			sb.append(System.lineSeparator());
		}
		System.out.println(sb.toString());
	}

	public void connClose() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
