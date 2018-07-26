package net.ukr.shyevhen;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		DbManager dbm = new DbManager();
		try {
			dbm.connectToDB();
			while (true) {
				System.out.println("Main menu");
				System.out.println("1: clients \r\n2: goods \r\n3: orders \r\n4: delete \r\nexit: press Enter");
				System.out.print("->");
				String num = sc.nextLine();
				if (num.equals("1")) {
					clientsMenu(dbm);
				} else if (num.equals("2")) {
					goodsMenu(dbm);
				} else if (num.equals("3")) {
					ordersMenu(dbm);
				} else if (num.equals("4")) {
					delMenu(dbm);
				} else {
					return;
				}
			}
		} finally {
			dbm.connClose();
		}
	}

	public static void clientsMenu(DbManager dbm) {
		while (true) {
			try {
				System.out.println("Clients menu");
				System.out.println("1: clients list\r\n2: search clients \r\n3: add client "
						+ "\r\n4: change client data \r\n5: client orders \r\nexit: press Enter");
				System.out.print("->");
				String num = sc.nextLine();
				if (num.equals("1")) {
					dbm.clientsList();
				} else if (num.equals("2")) {
					searchClients(dbm);
				} else if (num.equals("3")) {
					addClient(dbm);
				} else if (num.equals("4")) {
					changeClient(dbm);
				} else if (num.equals("5")) {
					System.out.print("Input client id ->");
					dbm.searchOrders("client_id", sc.nextInt());
					sc.nextLine();
				} else {
					return;
				}
			} catch (IllegalStateException | NoSuchElementException | NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	public static void searchClients(DbManager dbm) {
		System.out.println("Search client by");
		System.out.println("1: client id\r\n2: client full_name\r\n3: client company"
				+ "\r\n4: client e-mail \r\n5: client phone \r\n6: client address");
		System.out.print("->");
		String num = sc.nextLine();
		if (num.equals("1")) {
			System.out.print("Input client id ->");
			dbm.searchClients(sc.nextInt());
			sc.nextLine();
		} else if (num.equals("2")) {
			System.out.print("Input client full_name ->");
			dbm.searchClients("full_name", sc.nextLine());
		} else if (num.equals("3")) {
			System.out.print("Input client company ->");
			dbm.searchClients("company", sc.nextLine());
		} else if (num.equals("4")) {
			System.out.print("Input client e-mail ->");
			dbm.searchClients("email", sc.nextLine());
		} else if (num.equals("5")) {
			System.out.print("Input client phone ->");
			dbm.searchClients("phone", sc.nextLine());
		} else if (num.equals("6")) {
			System.out.print("Input client address ->");
			dbm.searchClients("address", sc.nextLine());
		}
	}

	public static void addClient(DbManager dbm) {
		System.out.print("Input client full_name ->");
		String full_name = sc.nextLine();
		System.out.print("Input client company ->");
		String company = sc.nextLine();
		System.out.print("Input client e-mail ->");
		String email = sc.nextLine();
		System.out.print("Input client phone ->");
		String phone = sc.nextLine();
		System.out.print("Input client address ->");
		String address = sc.nextLine();
		dbm.addClient(full_name, company, email, phone, address);
	}

	public static void changeClient(DbManager dbm) throws NumberFormatException {
		System.out.print("Input client id ->");
		int id = Integer.parseInt(sc.nextLine());
		while (true) {
			System.out.println("Change client data");
			System.out.println("1: change full name \r\n2: change company \r\n3: change e-mail "
					+ "\r\n4: change phone \r\n5: change address \r\nexit: press Enter");
			System.out.print("->");
			String num = sc.nextLine();
			if (num.equals("1")) {
				System.out.print("Input client full_name ->");
				dbm.changeClient(id, "full_name", sc.nextLine());
			} else if (num.equals("2")) {
				System.out.print("Input client company ->");
				dbm.changeClient(id, "company", sc.nextLine());
			} else if (num.equals("3")) {
				System.out.print("Input client e-mail ->");
				dbm.changeClient(id, "email", sc.nextLine());
			} else if (num.equals("4")) {
				System.out.print("Input client phone ->");
				dbm.changeClient(id, "phone", sc.nextLine());
			} else if (num.equals("5")) {
				System.out.print("Input client address ->");
				dbm.changeClient(id, "address", sc.nextLine());
			} else {
				return;
			}
		}
	}

	public static void goodsMenu(DbManager dbm) {
		while (true) {
			try {
				System.out.println("Goods menu");
				System.out.println("1: goods list\r\n2: search goods \r\n3: add goods "
						+ "\r\n4: change goods data \r\n5: goods orders \r\nexit: press Enter");
				System.out.print("->");
				String num = sc.nextLine();
				if (num.equals("1")) {
					dbm.goodsList();
				} else if (num.equals("2")) {
					searchGoods(dbm);
				} else if (num.equals("3")) {
					addGoods(dbm);
				} else if (num.equals("4")) {
					changeGoods(dbm);
				} else if (num.equals("5")) {
					System.out.print("Input goods id ->");
					dbm.searchOrders("goods_id", sc.nextInt());
					sc.nextLine();
				} else {
					return;
				}
			} catch (IllegalStateException | NoSuchElementException | NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	public static void searchGoods(DbManager dbm) throws NumberFormatException {
		System.out.println("Search goods by");
		System.out.println("1: goods id \r\n2: goods title \r\n3: goods model \r\n4: price \r\n5: only if available");
		System.out.print("->");
		String num = sc.nextLine();
		if (num.equals("1")) {
			System.out.print("Input client id ->");
			dbm.getTheGoods(sc.nextInt());
			sc.nextLine();
		} else if (num.equals("2")) {
			System.out.print("Input goods title ->");
			dbm.getTheGoods("title", sc.nextLine());
		} else if (num.equals("3")) {
			System.out.print("Input goods model ->");
			dbm.getTheGoods("model", sc.nextLine());
		} else if (num.equals("4")) {
			System.out.print("Input min goods price(integer) ->");
			int min = Integer.parseInt(sc.nextLine());
			System.out.print("Input max goods price(integer) ->");
			dbm.getTheGoods(min, Integer.parseInt(sc.nextLine()));
		} else if (num.equals("5")) {
			dbm.getStockGoods();
		}
	}

	public static void addGoods(DbManager dbm) {
		System.out.print("Input goods title ->");
		String title = sc.nextLine();
		System.out.print("Input goods model ->");
		String model = sc.nextLine();
		System.out.print("Input goods description ->");
		String description = sc.nextLine();
		System.out.print("Input goods price ->");
		BigDecimal price = sc.nextBigDecimal();
		sc.nextLine();
		System.out.print("Input goods stock ->");
		int stock = Integer.parseInt(sc.nextLine());
		dbm.addGoods(title, model, description, price, stock);
	}

	public static void changeGoods(DbManager dbm) throws NumberFormatException {
		System.out.print("Input goods id ->");
		int id = Integer.parseInt(sc.nextLine());
		while (true) {
			System.out.println("Change goods data");
			System.out.println("1: change title \r\n2: change model \r\n3: change description "
					+ "\r\n4: change price \r\n5: change stock \r\nexit: press Enter");
			System.out.print("->");
			String num = sc.nextLine();
			if (num.equals("1")) {
				System.out.print("Input goods title ->");
				dbm.changeGoods(id, "title", sc.nextLine());
			} else if (num.equals("2")) {
				System.out.print("Input goods model ->");
				dbm.changeGoods(id, "model", sc.nextLine());
			} else if (num.equals("3")) {
				System.out.print("Input goods description ->");
				dbm.changeGoods(id, "description", sc.nextLine());
			} else if (num.equals("4")) {
				System.out.print("Input goods price ->");
				dbm.changeGoods(id, sc.nextBigDecimal());
				sc.nextLine();
			} else if (num.equals("5")) {
				System.out.print("Input goods stock ->");
				dbm.changeGoods(id, Integer.parseInt(sc.nextLine()));
			} else {
				return;
			}
		}
	}

	public static void ordersMenu(DbManager dbm) {
		while (true) {
			try {
				System.out.println("Orders menu");
				System.out.println("1: orders list \r\n2: full orders list \r\n3: search orders \r\n4: add order "
						+ "\r\n5: change order \r\n6: complite order \r\nexit: press Enter");
				System.out.print("->");
				String num = sc.nextLine();
				if (num.equals("1")) {
					dbm.ordersList();
				} else if (num.equals("2")) {
					dbm.fullOrdersList();
				} else if (num.equals("3")) {
					searchOrders(dbm);
				} else if (num.equals("4")) {
					addOrders(dbm);
				} else if (num.equals("5")) {
					changeOrder(dbm);
				} else if (num.equals("6")) {
					System.out.print("Input order id ->");
					dbm.compliteOrder(sc.nextInt());
					sc.nextLine();
				} else {
					return;
				}
			} catch (IllegalStateException | NoSuchElementException | NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	public static void searchOrders(DbManager dbm) throws NumberFormatException {
		System.out.println("Search orders by");
		System.out.println("1: order id\r\n2: orders client id\r\n3: orders goods id");
		System.out.print("->");
		String num = sc.nextLine();
		if (num.equals("1")) {
			System.out.print("Input order id ->");
			dbm.searchOrders("id", sc.nextInt());
			sc.nextLine();
		} else if (num.equals("2")) {
			System.out.print("Input orders client id ->");
			dbm.searchOrders("client_id", sc.nextInt());
			sc.nextLine();
		} else if (num.equals("3")) {
			System.out.print("Input orders goods id ->");
			dbm.searchOrders("goods_id", sc.nextInt());
			sc.nextLine();
		}
	}

	public static void addOrders(DbManager dbm) throws NumberFormatException {
		System.out.print("Input client id ->");
		int client = Integer.parseInt(sc.nextLine());
		System.out.print("Input goods id ->");
		int goods = Integer.parseInt(sc.nextLine());
		System.out.print("Input amount ->");
		int amount = Integer.parseInt(sc.nextLine());
		dbm.addOrder(client, goods, amount);
	}

	public static void changeOrder(DbManager dbm) throws NumberFormatException {
		System.out.print("Input order id ->");
		int id = Integer.parseInt(sc.nextLine());
		System.out.println("Change order data");
		System.out.println("1: change client id \r\n2: change amount");
		System.out.print("->");
		String num = sc.nextLine();
		if (num.equals("1")) {
			System.out.print("Input client id ->");
			dbm.changeOrder(id, "client_id", Integer.parseInt(sc.nextLine()));
		} else if (num.equals("2")) {
			System.out.print("Input amount ->");
			dbm.changeOrder(id, "amount", Integer.parseInt(sc.nextLine()));
		}

	}

	public static void delMenu(DbManager dbm) {
		while (true) {
			try {
				System.out.println("Delete menu");
				System.out.println("1: delete client \r\n2: delete goods \r\n3: delete order \r\nexit: press Enter");
				System.out.print("->");
				String num = sc.nextLine();
				if (num.equals("1")) {
					System.out.print("Input client id ->");
					dbm.delClient(sc.nextInt());
					sc.nextLine();
				} else if (num.equals("2")) {
					System.out.print("Input goods id ->");
					dbm.delGoods(sc.nextInt());
					sc.nextLine();
				} else if (num.equals("3")) {
					System.out.print("Input order id ->");
					dbm.delOrder(sc.nextInt());
					sc.nextLine();
				} else {
					return;
				}
			} catch (IllegalStateException | NoSuchElementException | NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

}
