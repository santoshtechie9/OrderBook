package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.datastructure.LimitOrderBook;
import com.eventus.bookanalyser.datastructure.OrderTypes;
import com.eventus.bookanalyser.model.LimitOrderEntry;
import com.eventus.bookanalyser.model.OrderBookNotificationEvent;

import java.util.*;

public class BookAnalyzer implements Observer {

    private final LimitOrderBook orderBook;

    public static void main(String[] args) {
        // Using Scanner for Getting Input from User
        Scanner in = new Scanner(System.in);
        String dataLog = "Start";
        //fail fast
        isValidArgument(args);
        BookAnalyzer bookAnalyzer = new BookAnalyzer("ZING", Integer.valueOf(args[0]));
        while (!dataLog.equalsIgnoreCase("exit!")) {
            System.out.print("Input: ");
            dataLog = in.nextLine();
            if (dataLog.isEmpty() || dataLog.isBlank())
                throw new InputMismatchException();
            if (!dataLog.equalsIgnoreCase("exit!"))
                bookAnalyzer.run(dataLog);
        }
    }

    public BookAnalyzer(String instrument, Integer targetSize) {
        this.orderBook = new LimitOrderBook(instrument, targetSize);
        orderBook.addObserver(this);
    }

    private static void isValidArgument(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("Invalid argument; Expected syntax: BookAnalyzer <target-size>");
    }

    public void run(String dataLog) {
        LimitOrderEntry limitOrderEntry = null;
        List<String> dataLogArray = Arrays.asList(dataLog.split(" "));

        //fail fast
        hasValidNumberOfFields(dataLogArray);
        isValidField(dataLogArray);

        if (isAddOrder(dataLogArray))
            limitOrderEntry = creteAddOrderEntry(dataLogArray);
        else if (isRemoveOrder(dataLogArray))
            limitOrderEntry = creteRemoveOrderEntry(dataLogArray);

        processOrder(limitOrderEntry);

    }

    private boolean isRemoveOrder(List<String> dataLogArray) {
        String orderType = dataLogArray.get(1);
        return orderType.equalsIgnoreCase(OrderTypes.R.name());
    }

    private boolean isRemoveOrder(LimitOrderEntry limitOrderEntry) {
        return limitOrderEntry.getOrderType().equalsIgnoreCase(OrderTypes.R.name());
    }

    private boolean isAddOrder(List<String> dataLogArray) {
        String orderType = dataLogArray.get(1);
        return orderType.equalsIgnoreCase(OrderTypes.A.name());
    }

    private boolean isAddOrder(LimitOrderEntry limitOrderEntry) {
        return limitOrderEntry.getOrderType().equalsIgnoreCase(OrderTypes.A.name());
    }

    private void isValidField(List<String> dataLogArray) {
        String orderType = dataLogArray.get(1);
        if (!(orderType.equalsIgnoreCase(OrderTypes.A.name()) || orderType.equalsIgnoreCase(OrderTypes.R.name())))
            throw new IllegalArgumentException(String.format("Invalid orderType: %s", orderType));

        if (orderType.equalsIgnoreCase(OrderTypes.A.name())) {
            hasValidAddOrderArguments(dataLogArray);
        } else {
            hasValidRemoveOrderArguments(dataLogArray);

        }
    }

    private void hasValidRemoveOrderArguments(List<String> dataLogArray) {
        long timestamp = Long.parseLong(dataLogArray.get(0));
        String orderId = dataLogArray.get(2);
        int size = Integer.parseInt(dataLogArray.get(3));
        if (timestamp < 0)
            throw new IllegalArgumentException(String.format("Invalid timestamp: %d", timestamp));
        else if (orderId.isBlank() || orderId.isEmpty())
            throw new IllegalArgumentException(String.format("Invalid orderId: %s", orderId));
        else if (size <= 0)
            throw new IllegalArgumentException(String.format("Invalid size: %d", timestamp));
    }

    private void hasValidAddOrderArguments(List<String> dataLogArray) {
        long timestamp = Long.parseLong(dataLogArray.get(0));
        String orderId = dataLogArray.get(2);
        String side = dataLogArray.get(3);
        double price = Double.parseDouble(dataLogArray.get(4));
        int size = Integer.parseInt(dataLogArray.get(5));
        if (timestamp < 0)
            throw new IllegalArgumentException(String.format("Invalid timestamp: %d", timestamp));
        else if (orderId.isBlank() || orderId.isEmpty())
            throw new IllegalArgumentException(String.format("Invalid orderId: %s", orderId));
        else if (!(side.equalsIgnoreCase(OrderTypes.B.name()) || side.equalsIgnoreCase(OrderTypes.S.name())))
            throw new IllegalArgumentException(String.format("Invalid side: %s", side));
        else if (price <= 0)
            throw new IllegalArgumentException(String.format("Invalid price: %f", price));
        else if (size <= 0)
            throw new IllegalArgumentException(String.format("Invalid size: %d", timestamp));
    }

    public void hasValidNumberOfFields(List<String> dataLogArray) {
        int fieldCount = dataLogArray.size();
        String orderType = dataLogArray.get(1);
        if (orderType.equalsIgnoreCase(OrderTypes.A.name())) {
            if (fieldCount != 6)
                throw new IllegalArgumentException("Invalid argument; Add Order data log should contain 6 fields; space delimited!");
        } else if (orderType.equalsIgnoreCase(OrderTypes.R.name()) && fieldCount != 4) {
            throw new IllegalArgumentException("Invalid row; Reduce Order data log should contain 4 fields; space delimited!");
        }
    }

    private LimitOrderEntry creteAddOrderEntry(List<String> dataLogArray) {
        long timestamp = Long.parseLong(dataLogArray.get(0));
        String orderType = dataLogArray.get(1);
        String orderId = dataLogArray.get(2);
        String side = dataLogArray.get(3);
        double price = Double.parseDouble(dataLogArray.get(4));
        int size = Integer.parseInt(dataLogArray.get(5));
        return new LimitOrderEntry(timestamp, orderType, orderId, side, price, size);
    }

    private LimitOrderEntry creteRemoveOrderEntry(List<String> dataLogArray) {
        long timestamp = Long.parseLong(dataLogArray.get(0));
        String orderType = dataLogArray.get(1);
        String orderId = dataLogArray.get(2);
        int size = Integer.parseInt(dataLogArray.get(3));
        return new LimitOrderEntry(timestamp, orderType, orderId, null, null, size);
    }

    private void processOrder(LimitOrderEntry limitOrderEntry) {
        if (isAddOrder(limitOrderEntry))
            orderBook.addOrder(limitOrderEntry);
        if (isRemoveOrder(limitOrderEntry))
            orderBook.modifyOrder(limitOrderEntry);
    }

    @Override
    public void update(Observable o, Object arg) {
        OrderBookNotificationEvent notifyEvent = (OrderBookNotificationEvent) arg;
        System.out.println(String.format("%d %s %s", notifyEvent.getTimestamp(), notifyEvent.getSide(), notifyEvent.getTotal()));
    }

}
