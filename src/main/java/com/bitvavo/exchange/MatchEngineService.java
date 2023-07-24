package com.bitvavo.exchange;

import com.bitvavo.exchange.match.MatchResult;
import com.bitvavo.exchange.model.MatchDetail;
import com.bitvavo.exchange.model.Order;
import com.bitvavo.exchange.processor.MatchEngine;
import com.bitvavo.exchange.utils.MatchEngineUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MatchEngineService {
    private MatchEngine engine = new MatchEngine();
    
    public void receiveAndProcessOrders(){

        List<Order> orders = new ArrayList<>();
        Scanner fileName = new Scanner(System.in);
        List<MatchDetail> matches = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName.nextLine()))) {
            String line;

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

            if(matches.isEmpty()){
                System.out.println("No matches");
            }

            StringBuilder matchString = new StringBuilder();

            for (MatchDetail matchDetail: matches) {
                matchString.append("trade ")
                        .append(matchDetail.takerOrder().getSequenceId())
                        .append(",").append(matchDetail.makerOrder().getSequenceId())
                        .append(",").append(matchDetail.price().toString()).append(",")
                        .append(matchDetail.quantity().toString())
                        .append(System.lineSeparator());
            }

            String generateMd5 = MatchEngineUtils.generateMd5(matchString.toString());

            System.out.println(generateMd5);

        } catch (Exception e) {
            System.out.println("Error while reading file" + fileName.nextLine());
        }
    }
}
