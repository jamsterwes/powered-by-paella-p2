package eu.poweredbypaella.paellapos.data;

import eu.poweredbypaella.paellapos.DatabaseSetup;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class DatabaseConnection {

    Connection conn;

    // Add/get/remove item(s)
    private PreparedStatement pAddItem;
    private PreparedStatement pGetItem;
    private PreparedStatement pGetItems;
    private PreparedStatement pUpdateItem;
    private PreparedStatement pDeleteItem;

    // Get/set quantity of an item
    private PreparedStatement pGetQuantity;
    private PreparedStatement pSetQuantity;

    // Get next item/receipt/order ID
    private PreparedStatement pGetNextItemID;
    private PreparedStatement pGetNextReceiptID;
    private PreparedStatement pGetNextOrderID;

    // Get receipt/receipt lines
    private PreparedStatement pGetReceipt;
    private PreparedStatement pDeleteReceipt;
    private PreparedStatement pGetAllReceiptLines;
    private PreparedStatement pGetReceiptLines;
    private PreparedStatement pDeleteReceiptLines;
    private PreparedStatement pGetReceipts;

    // Add receipt/receipt lines
    private PreparedStatement pAddReceipt;
    private PreparedStatement pAddReceiptLine;

    // Get order/order lines
    private PreparedStatement pGetOrder;
    private PreparedStatement pGetOrders;
    private PreparedStatement pUpdateOrder;
    private PreparedStatement pUpdateOrderItem;
    private PreparedStatement pGetAllOrderLines;
    private PreparedStatement pGetOrderLines;
    private PreparedStatement pDeleteOrder;
    private PreparedStatement pDeleteOrderItem;
    private PreparedStatement pDeleteOrderLines;

    // Add order/order lines
    private PreparedStatement pAddOrder;
    private PreparedStatement pAddOrderLine;

    // Add/get/remove employee(s)
    private PreparedStatement pAddEmployee;
    private PreparedStatement pGetEmployee;
    private PreparedStatement pGetEmployees;
    private PreparedStatement pDeleteEmployee;

    //Get the date ranges for sales
    private PreparedStatement pGetCustomDayRange;
    private PreparedStatement pGetExcess;
    private PreparedStatement pGetExcess2;
    private PreparedStatement pGetRestock;


    /**
     * @author Rishi Chandnani and Wesley Taylor
     */
    public DatabaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(DatabaseSetup.URL, DatabaseSetup.USERNAME, DatabaseSetup.PASSWORD);

            // Get/add/remove item(s)
            pAddItem = conn.prepareStatement("INSERT INTO items (id, display_name, unit_price, by_weight) VALUES (?, ?, ?, ?)");
            pGetItem = conn.prepareStatement("SELECT display_name, unit_price, by_weight, remaining_stock FROM items WHERE id = ?");
            pGetItems = conn.prepareStatement("SELECT id, display_name, unit_price, by_weight, remaining_stock FROM items");
            pUpdateItem = conn.prepareStatement("UPDATE items SET display_name = ?, unit_price = ?, by_weight = ? WHERE id = ?");
            pDeleteItem = conn.prepareStatement("DELETE FROM items WHERE id = ?");

            // Update quantity of an item
            pGetQuantity = conn.prepareStatement("SELECT remaining_stock FROM items WHERE id = ?");
            pSetQuantity = conn.prepareStatement("UPDATE items SET remaining_stock = ? WHERE id = ?");

            // Get next item/receipt/order ID
            pGetNextItemID = conn.prepareStatement("SELECT id FROM items order by id desc limit 1");
            pGetNextReceiptID = conn.prepareStatement("SELECT id FROM receipts order by id desc limit 1");
            pGetNextOrderID = conn.prepareStatement("SELECT id FROM orders order by id desc limit 1");

            // Get receipt/receipt lines
            pGetReceipt = conn.prepareStatement( "SELECT transaction_date, total, is_cash, employee_id FROM receipts WHERE id = ?");
            pDeleteReceipt = conn.prepareStatement( "DELETE FROM receipts WHERE id = ?");
            pGetReceiptLines = conn.prepareStatement( "SELECT item_id, quantity FROM receipt_lines WHERE receipt_id = ?");
            pGetAllReceiptLines = conn.prepareStatement( "SELECT receipt_id, item_id, quantity FROM receipt_lines");
            pDeleteReceiptLines = conn.prepareStatement( "DELETE FROM receipt_lines WHERE receipt_id = ?");
            pGetReceipts = conn.prepareStatement( "SELECT id, transaction_date, total, is_cash, employee_id FROM receipts");

            // Add receipt/receipt lines
            pAddReceipt = conn.prepareStatement("INSERT INTO receipts (id, transaction_date, total, is_cash, employee_id) VALUES (?, ?, ?, ?, ?)");
            pAddReceiptLine = conn.prepareStatement("INSERT INTO receipt_lines (receipt_id, item_id, quantity) VALUES (?, ?, ?)");

            // Get order/order lines
            pGetOrder = conn.prepareStatement("SELECT cost, delivery_date, received FROM orders WHERE id = ?");
            pGetOrders = conn.prepareStatement("SELECT id, cost, delivery_date, received FROM orders");
            pUpdateOrder = conn.prepareStatement("UPDATE orders SET cost = ?, received = ? WHERE id = ?");
            pUpdateOrderItem = conn.prepareStatement("UPDATE order_lines SET quantity = ? WHERE item_id = ? AND order_id = ?");
            pGetOrderLines = conn.prepareStatement("SELECT item_id, quantity FROM order_lines WHERE order_id = ?");
            pGetAllOrderLines = conn.prepareStatement("SELECT order_id, item_id, quantity FROM order_lines");
            pDeleteOrder = conn.prepareStatement("DELETE from orders WHERE id = ?");
            pDeleteOrderItem = conn.prepareStatement("DELETE from order_lines WHERE item_id = ? AND order_id = ?");
            pDeleteOrderLines = conn.prepareStatement("DELETE from order_lines WHERE order_id = ?");

            // Add order/order lines
            pAddOrder = conn.prepareStatement("INSERT INTO orders (id, cost, delivery_date, received) VALUES (?, ?, ?, ?)");
            pAddOrderLine = conn.prepareStatement("INSERT INTO order_lines (order_id, item_id, quantity) VALUES (?, ?, ?)");

            // Get/add/remove employee(s)
            pAddEmployee = conn.prepareStatement("INSERT INTO employees (employee_name, is_admin) VALUES (?, ?)");
            pGetEmployee = conn.prepareStatement("SELECT employee_name, is_admin FROM employees WHERE id = ?");
            pGetEmployees = conn.prepareStatement("SELECT employee_name, is_admin FROM employees");
            pDeleteEmployee = conn.prepareStatement("DELETE FROM employees WHERE id = ?");

            //Get the date ranges for sales
            pGetCustomDayRange = conn.prepareStatement("SELECT i.display_name, subby.sum, (subby.sum * i.unit_price) as total FROM items i, (select l.item_id, sum(l.quantity) FROM receipt_lines l, receipts r WHERE l.receipt_id = r.id AND r.transaction_date >= ? AND r.transaction_date <= ? GROUP BY l.item_id) subby WHERE i.id = subby.item_id");
            pGetExcess = conn.prepareStatement("SELECT i.id, i.display_name, subby.sum, i.remaining_stock FROM items i, ( select l.item_id, sum(l.quantity) FROM receipt_lines l, receipts r WHERE l.receipt_id = r.id AND r.transaction_date >= ? AND r.transaction_date <= ? group by l.item_id ) subby WHERE i.id = subby.item_id AND subby.sum < (i.remaining_stock * 0.1)");
            pGetExcess2 = conn.prepareStatement("SELECT * FROM items WHERE id not in (select i.id from items i, (select l.item_id, sum(l.quantity) FROM receipt_lines l, receipts r WHERE l.receipt_id = r.id AND r.transaction_date >= ? AND r.transaction_date <= ? group by l.item_id ) subby WHERE i.id = subby.item_id)");
            pGetRestock = conn.prepareStatement("SELECT i.id, i.display_name, subby.sum, i.remaining_stock FROM items i, ( select l.item_id, sum(l.quantity) FROM receipt_lines l, receipts r WHERE l.receipt_id = r.id AND r.transaction_date >= ? AND r.transaction_date <= ? group by l.item_id ) subby WHERE i.id = subby.item_id AND subby.sum > i.remaining_stock");


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Shuts down the connection to the database.
     * 
     * @throws SQLException if closing connection fails
     */
    public void shutdown() throws SQLException {
        conn.close();
    }

    /**
     * Gets the next item ID available in the database.
     * 
     * @return The next available item ID
     * 
     * @throws SQLException if the SQL query fails
     */
    private int getNextItemID() throws SQLException {
        ResultSet result = pGetNextItemID.executeQuery();
        if (result.next()) {
            // Get ID from query, increment
            return result.getInt("id") + 1;
        } else {
            // No receipts in the list, next ID is 1
            return 1;
        }
    }

    /**
     * Gets the next receipt ID available in the database.
     * 
     * @return The next available receipt ID
     * 
     * @throws SQLException if the SQL query fails
     */
    private int getNextReceiptID() throws SQLException {
        ResultSet result = pGetNextReceiptID.executeQuery();
        if (result.next()) {
            // Get ID from query, increment
            return result.getInt("id") + 1;
        } else {
            // No receipts in the list, next ID is 1
            return 1;
        }
    }

    /**
     * Gets the next order ID available in the database.
     * 
     * @return The next available order ID
     * 
     * @throws SQLException if the SQL query fails
     */
    private int getNextOrderID() throws SQLException {
        ResultSet result = pGetNextOrderID.executeQuery();
        if (result.next()) {
            // Get ID from query, increment
            return result.getInt("id") + 1;
        } else {
            // No orders in the list, next ID is 1
            return 1;
        }
    }

    /**
     * Calculates the total on a receipt.
     * 
     * @param receipt The receipt to calculate total of
     * @return The total of the receipt
     * @throws SQLException if an item unit price lookup fails
     */
    public double calcTotal(Receipt receipt) throws SQLException {
        double total = 0;

        for (Integer itemID : receipt.getItems()) {
            total += getItem(itemID).price * receipt.items.get(itemID);
        }

        return total;
    }

    /**
     * Calculates the total on a order.
     * 
     * @param order The order to calculate total of
     * @return The total of the order
     * @throws SQLException if an item unit price lookup fails
     */
    public double calcTotal(Order order) throws SQLException {
        double total = 0;

        for (Integer itemID : order.getItems()) {
            total += getItem(itemID).price * order.items.get(itemID);
        }

        return total;
    }

    /**
     * Adds an item (not including its quantity) into the database.
     * 
     * @param item The item to insert
     * @return The ID of the created item
     * @throws SQLException if the SQL query failed
     */
    public int addItem(Item item) throws SQLException {
        int nextID = getNextItemID();
        pAddItem.setInt(1, nextID);
        pAddItem.setString(2, item.name);
        pAddItem.setDouble(3, item.price);
        pAddItem.setBoolean(4, item.byWeight);
        pAddItem.executeUpdate();
        return nextID;
    }

    /**
     * Gets an item (including its quantity) into the database.
     * 
     * @param id The id of the item to insert
     * @return The item retrieved from the database
     * @throws SQLException if the SQL query failed (or if no item found with that ID)
     */
    public Item getItem(int id) throws SQLException {
        pGetItem.setInt(1, id);
        ResultSet result = pGetItem.executeQuery();
        result.next();
        return new Item(id,
                result.getString("display_name"),
                result.getDouble("unit_price"),
                result.getBoolean("by_weight"),
                result.getDouble("remaining_stock"));
    }

    /**
     * Gets the list of all items in the database.
     * 
     * @return List of all items in the database
     * @throws SQLException if the SQL query failed
     */
    public List<Item> getItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        ResultSet result = pGetItems.executeQuery();
        while (result.next()) {
            items.add(new Item(result.getInt("id"),
                               result.getString("display_name"),
                               result.getDouble("unit_price"),
                               result.getBoolean("by_weight"),
                               result.getDouble("remaining_stock")));
        }
        return items;
    }

    /**
     * Updates an item (not including its quantity) in the database.
     * 
     * @param newItem The new data of the item
     * @throws SQLException if the SQL query failed
     */
    public void updateItem(Item newItem) throws SQLException {
        pUpdateItem.setString(1, newItem.name);
        pUpdateItem.setDouble(2, newItem.price);
        pUpdateItem.setBoolean(3, newItem.byWeight);
        pUpdateItem.setInt(4, newItem.id);
        pUpdateItem.executeUpdate();
    }

    /**
     * Deletes an item from the database.
     * 
     * @param id The id of the item to delete
     * @throws SQLException if the SQL query failed
     */
    public void deleteItem(int id) throws SQLException {
        pDeleteItem.setInt(1, id);
        pDeleteItem.executeUpdate();
    }

    /**
     * Gets the item -quantity from the database.
     *
     * @param id The id of the item to get the quantity of
     * @throws SQLException if the SQL query failed
     */
    public double getQuantity(int id) throws SQLException {
        pGetQuantity.setInt(1, id);
        ResultSet result = pGetQuantity.executeQuery();
        result.next();
        return result.getDouble("remaining_stock");
    }

    /**
     * Sets the item -quantity from the database.
     *
     * @param id The id of the item to set the quantity of
     * @throws SQLException if the SQL query failed
     */
    public void setQuantity(int id, double quantity) throws SQLException {
        pSetQuantity.setDouble(1, quantity);
        pSetQuantity.setInt(2, id);
        pSetQuantity.executeUpdate();
    }

    /**
     * Gets a receipt from the database.
     *
     * @param id The id of the receipt to get
     * @return The receipt retrieved from the database
     * @throws SQLException if the SQL query failed (or if no item found with that ID)
     */
    public Receipt getReceipt(int id) throws SQLException {
        pGetReceipt.setInt(1, id);
        ResultSet result = pGetReceipt.executeQuery();
        result.next();

        // Make receipt
        Receipt receipt = new Receipt(id,
                                      result.getTimestamp("transaction_date"),
                                      result.getDouble("total"),
                                      result.getInt("employee_id"),
                                      result.getBoolean("is_cash"));

        // Fill in receipt lines
        pGetReceiptLines.setInt(1, id);
        result = pGetReceiptLines.executeQuery();
        while (result.next()) {
            receipt.addItem(result.getInt("item_id"), result.getDouble("quantity"));
        }

        return receipt;
    }

    /**
     * Deletes a receipt from the database.
     *
     * @param id The id of the receipt to delete
     * @throws SQLException if the SQL query failed
     */
    public void deleteReceipt(int id) throws SQLException {
        // Fill in cost, received
        pDeleteReceipt.setInt(1, id);
        pDeleteReceipt.executeUpdate();

        // delete the receipt lines
        pDeleteReceiptLines.setInt(1, id);
        pDeleteReceiptLines.executeUpdate();
    }

    /**
     * Gets all receipts from the database.
     *
     * @return The receipts from the database.
     * @throws SQLException if the SQL query failed
     */
    public Collection<Receipt> getReceipts() throws SQLException {
        ResultSet result = pGetReceipts.executeQuery();

        HashMap<Integer, Receipt> receipts = new HashMap<>();
        while (result.next()) {
            // Make receipt
            Receipt receipt = new Receipt(result.getInt("id"),
                                          result.getTimestamp("transaction_date"),
                                          result.getDouble("total"),
                                          result.getInt("employee_id"),
                                          result.getBoolean("is_cash"));
            // Insert receipt into map
            receipts.put(receipt.id, receipt);
        }

        result = pGetAllReceiptLines.executeQuery();
        while (result.next()) {
            // Insert line
            int id = result.getInt("receipt_id");
            receipts.get(id).addItem(result.getInt("item_id"), result.getDouble("quantity"));
        }

        return receipts.values();
    }

    /**
     * Adds a receipt to the database.
     *
     * @param receipt The receipt to be added to the database
     * @return The receipt id of the added receipt in the database
     * @throws SQLException if the SQL query failed
     */
    public int addReceipt(Receipt receipt) throws SQLException {
        int receiptID = getNextReceiptID();

        System.out.printf("Adding receipt ID %d%n", receiptID);

        // Fill out id, transaction_date, total, is_cash, employee_id
        pAddReceipt.setInt(1, receiptID);
        pAddReceipt.setTimestamp(2, receipt.transactionDate);
        pAddReceipt.setDouble(3, receipt.total);
        pAddReceipt.setBoolean(4, receipt.isCash);
        pAddReceipt.setInt(5, receipt.employeeID);

        // Insert receipt into database
        pAddReceipt.executeUpdate();

        // add in the receipt lines
        for (Integer itemID : receipt.getItems()) {
            // Get item quantity
            double quantity = receipt.items.get(itemID);

            // Fill out receipt_id, item_id, quantity
            pAddReceiptLine.setInt(1, receiptID);
            pAddReceiptLine.setInt(2, itemID);
            pAddReceiptLine.setDouble(3, quantity);

            pAddReceiptLine.executeUpdate();
        }

        // Return new receipt ID
        return receiptID;
    }

    /**
     * Gets an order from the database.
     *
     * @param id The id of the order to get from the database.
     * @return The order from the database.
     * @throws SQLException if the SQL query failed (or if no item found with that ID)
     */
    public Order getOrder(int id) throws SQLException {
        pGetOrder.setInt(1, id);
        ResultSet result = pGetOrder.executeQuery();
        result.next();

        // Make order
        Order order = new Order(result.getDouble("cost"),
                result.getTimestamp("delivery_date"),
                result.getBoolean("received"));

        // Fill in order lines
        pGetOrderLines.setInt(1, id);
        result = pGetOrderLines.executeQuery();
        while (result.next()) {
            order.addItem(result.getInt("item_id"), result.getDouble("quantity"));
        }

        return order;
    }

    /**
     * Gets all orders from the database.
     *
     * @return The orders from the database.
     * @throws SQLException if the SQL query failed (or if no item found with that ID)
     */
    public Collection<Order> getOrders() throws SQLException {
        ResultSet result = pGetOrders.executeQuery();

        HashMap<Integer, Order> orders = new HashMap<>();
        while (result.next()) {
            // Make order
            Order order = new Order(result.getInt("id"),
                    result.getDouble("cost"),
                    result.getTimestamp("delivery_date"),
                    result.getBoolean("received"));
            // Insert receipt into map
            orders.put(order.id, order);
        }

        result = pGetAllOrderLines.executeQuery();
        while (result.next()) {
            // Insert line
            int id = result.getInt("order_id");
            orders.get(id).addItem(result.getInt("item_id"), result.getDouble("quantity"));
        }

        return orders.values();
    }

    /**
     * Updates an order in the database.
     *
     * @param id The id of the order to update
     * @param order The new order data.
     * @throws SQLException if the SQL query failed
     */
    public void updateOrderInfo(int id, Order order) throws SQLException {
        // Fill in cost, received
        pUpdateOrder.setDouble(1, order.cost);
        pUpdateOrder.setBoolean(2, order.received);
        pUpdateOrder.setInt(3, id);
        pUpdateOrder.executeUpdate();
    }

    /**
     * Updates an item in an order
     *
     * @param orderId The id of the receipt to update into
     * @param item The new data of the item
     * @throws SQLException if the SQL query failed
     */
    public void updateOrderItem(int orderId, Item item) throws SQLException {
        // Fill in item_id, quantity, item_id, order_id
        pUpdateOrderItem.setDouble(1, item.quantity);
        pUpdateOrderItem.setInt(2, item.id);
        pUpdateOrderItem.setInt(3, orderId);
        pUpdateOrderItem.executeUpdate();
    }

    /**
     * Deletes an order from the database.
     *
     * @param id The id of the order to delete
     * @throws SQLException if the SQL query failed
     */
    public void deleteOrder(int id) throws SQLException {
        // Fill in cost, received
        pDeleteOrder.setInt(1, id);
        pDeleteOrder.executeUpdate();

        // delete the receipt lines
        pDeleteOrderLines.setInt(1, id);
        pDeleteOrderLines.executeUpdate();
    }

    /**
     * Deletes an item from an order.
     *
     * @param orderId The id of the receipt to delete from
     * @param itemId The id of the item to delete
     * @throws SQLException if the SQL query failed
     */
    public void deleteOrderItem(int orderId, int itemId) throws SQLException {
        // Fill in item_id, order_id
        pDeleteOrderItem.setInt(1, itemId);
        pDeleteOrderItem.setInt(2, orderId);
        pDeleteOrderItem.executeUpdate();
    }


    /**
     * Adds an order from the database.
     *
     * @param order The order to add to the database.
     * @return The order id of the added order from the database.
     * @throws SQLException if the SQL query failed
     */
    public int addOrder(Order order) throws SQLException {
        int orderID = getNextOrderID();

        // Fill in id, cost, delivery_date, received
        pAddOrder.setInt(1, orderID);
        pAddOrder.setDouble(2, order.cost);
        pAddOrder.setTimestamp(3, order.deliveryDate);
        pAddOrder.setBoolean(4, order.received);

        pAddOrder.executeUpdate();

        // add in the receipt lines
        for (Integer itemID : order.getItems()) {
            // Get quantity
            double quantity = order.items.get(itemID);

            // Fill out order_id, item_id, quantity
            pAddOrderLine.setInt(1, orderID);
            pAddOrderLine.setInt(2, itemID);
            pAddOrderLine.setDouble(3, quantity);

            pAddOrderLine.executeUpdate();
        }

        // Return new order ID
        return orderID;
    }

    /**
     * Adds an employee to the database.
     *
     * @param employee The employee that needs to be added to the database.
     * @throws SQLException if the SQL query failed
     */
    public void addEmployee(Employee employee) throws SQLException {
        pAddEmployee.setString(1, employee.name);
        pAddEmployee.setBoolean(2, employee.isAdmin);
        pAddEmployee.executeUpdate();
    }

    /**
     * Gets an employee from the database.
     *
     * @param id The id of the employee in the database.
     * @return The employee that was found in the database.
     * @throws SQLException if the SQL query failed
     */
    public Employee getEmployee(int id) throws SQLException {
        pGetEmployee.setInt(1, id);
        ResultSet result = pGetEmployee.executeQuery();
        result.next();
        return new Employee(result.getString("employee_name"),
                result.getBoolean("is_admin"));
    }

    /**
     * Gets all employees from the database.
     *
     * @return All the employees in the database.
     * @throws SQLException if the SQL query failed
     */
    public List<Employee> getEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        ResultSet result = pGetEmployees.executeQuery();
        while (result.next()) {
            employees.add(new Employee(result.getString("name"),
                                       result.getBoolean("is_admin")));
        }
        return employees;
    }

    /**
     * Deletes an employee from the database.
     *
     * @param id The id of the employee in the database.
     * @throws SQLException if the SQL query failed
     */
    public void deleteEmployee(int id) throws SQLException {
        pDeleteEmployee.setInt(1, id);
        pDeleteEmployee.executeUpdate();
    }

    /**
     * Returns a list of sales from the database.
     *
     * @param start The timestamp of the start of the time frame
     * @param finish The timestamp of the end of the time frame
     * @return list of the sales
     * @throws SQLException if the SQL query failed
     */
    public List<SalesInfo> getSalesRange(Timestamp start, Timestamp finish) throws SQLException {
        List<SalesInfo> sales = new ArrayList<>();
        pGetCustomDayRange.setTimestamp(1, start);
        pGetCustomDayRange.setTimestamp(2, finish);
        ResultSet result = pGetCustomDayRange.executeQuery();
        while (result.next()) {
            sales.add(new SalesInfo(result.getString("display_name"),
                    result.getDouble("sum"),
                    result.getDouble("total")));
        }
        return sales;
    }

    /**
     * Returns a list of excess from the database.
     *
     * @param start The timestamp of the start of the time frame
     * @param finish The timestamp of the end of the time frame
     * @return list of the excess
     * @throws SQLException if the SQL query failed
     */
    public List<Item> getExcess(Timestamp start, Timestamp finish) throws SQLException {
        List<Item> itemNames = new ArrayList<>();
        pGetExcess.setTimestamp(1, start);
        pGetExcess.setTimestamp(2, finish);
        ResultSet result = pGetExcess.executeQuery();
        while (result.next()) {
            itemNames.add(new Item(result.getString("display_name")));
        }

        pGetExcess2.setTimestamp(1, start);
        pGetExcess2.setTimestamp(2, finish);
        result = pGetExcess2.executeQuery();
        while (result.next()) {
            itemNames.add(new Item(result.getString("display_name")));
        }

        return itemNames;
    }

    /**
     * Returns a list of restock from the database.
     *
     * @param start The timestamp of the start of the time frame
     * @param finish The timestamp of the end of the time frame
     * @return list of the restock
     * @throws SQLException if the SQL query failed
     */
    public List<Item> getRestock(Timestamp start, Timestamp finish) throws SQLException {
        List<Item> itemNames = new ArrayList<>();
        pGetRestock.setTimestamp(1, start);
        pGetRestock.setTimestamp(2, finish);
        ResultSet result = pGetRestock.executeQuery();
        while (result.next()) {
            itemNames.add(new Item(result.getString("display_name")));
        }
        return itemNames;
    }
}
