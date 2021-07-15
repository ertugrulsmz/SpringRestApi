package com.appsdeveloperblog.app.ws.async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class opt {

    public static void main(String[] args){
        Optional<String> op = Optional.ofNullable("Ali");

        Integer a1 = op.map(String::toLowerCase).map(String::length)
                .orElse(2);

        System.out.println(a1);

        Optional<String> n = Optional.ofNullable("aa");

        List<String> a11 = new ArrayList<>();
        a11.add("A");
        a11.add("B");

        List<String> ax = a11.stream().map(String::toLowerCase).collect(Collectors.toList());
        System.out.println(ax);

        Stream.of("1","2","3").map(String::toLowerCase).collect(Collectors.toList());

    }

    public static String low(String ars){
        return ars.toLowerCase();
    }
}
