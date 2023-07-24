package com.bitvavo.exchange;

import com.bitvavo.exchange.match.MatchResult;
import com.bitvavo.exchange.model.*;
import com.bitvavo.exchange.processor.MatchEngine;
import com.bitvavo.exchange.utils.MatchEngineUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.*;

public class MatchEngineService {
    private final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
    private MatchEngine engine = new MatchEngine();

    public void receiveAndProcessOrders() {
        
        Scanner fileName = new Scanner(System.in);

        try (BufferedReader br = new BufferedReader(new FileReader(fileName.nextLine()))) {
            String line;
            List<Order> orders = new ArrayList<>();
            List<MatchDetail> matches = new ArrayList<>();
            
            while ((line = br.readLine()) != null) {
                final String COMMA_DELIMITER = ",";
                String[] values = line.split(COMMA_DELIMITER);
                Order order = MatchEngineUtils.createOrder(values[0], MatchEngineUtils.getTypeByChar(values[1]), values[2], values[3]);
                orders.add(order);
            }

            for (Order order : orders) {
                MatchResult mr = engine.processOrder(order.getSequenceId(), order);
                matches.addAll(mr.matchDetails);
            }

            if (matches.isEmpty()) {
                System.out.println("No matches");
                return;
            }

            StringBuilder matchString = new StringBuilder();

            for (MatchDetail matchDetail : matches) {
                matchString.append("trade ")
                        .append(matchDetail.takerOrder().getSequenceId())
                        .append(",").append(matchDetail.makerOrder().getSequenceId())
                        .append(",").append(matchDetail.price().toString()).append(",")
                        .append(matchDetail.quantity().toString())
                        .append(System.lineSeparator());
            }

            System.out.println(matchString);

            List<String> sell = retrieveValuesListFromBookSet(engine.sellBook);
            List<String> buy = retrieveValuesListFromBookSet(engine.buyBook);

            StringBuilder output = new StringBuilder();

            int size = Math.max(sell.size(), buy.size());

            for (int i = 0; i < size; i++) {
                // case both lines have values on sell and buy
                if (i < sell.size() && i < buy.size()) {
                    output.append(buy.get(i)).append(" | ")
                            .append(sell.get(i))
                            .append(System.lineSeparator());
                } // case just sell have values  
                else if (i < sell.size()) {
                    output.append(sell.get(i))
                            .append(" | ")
                            .append(System.lineSeparator());
                }// case just buy have values 
                else {
                    output.append(buy.get(i))
                            .append(" | ")
                            .append(System.lineSeparator());
                }
            }

            System.out.println(output);
            String generateMd5 = MatchEngineUtils.generateMd5(output.toString());
            System.out.println("MD5 generated: " + generateMd5);

        } catch (Exception e) {
            System.out.println("Error while reading file" + fileName.nextLine());
        }
    }

    private List<String> retrieveValuesListFromBookSet(OrderBook orderBook) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<OrderKey, Order> book : orderBook.book.entrySet()) {
            Order order = orderBook.book.get(new OrderKey(book.getKey().sequenceId(), book.getKey().price()));
            String quantityStr = formatter.format(order.getUnfilledQuantity());

            if (orderBook.type == Type.BUY) {
                list.add(quantityStr + " " + order.getPrice());
            } else {
                list.add(order.getPrice() + " " + quantityStr);
            }
        }
        return list;
    }
}
