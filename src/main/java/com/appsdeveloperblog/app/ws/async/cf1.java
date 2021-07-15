package com.appsdeveloperblog.app.ws.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class cf1 {
    public static void main(String[] args){

        /*CompletableFuture<Integer> cl = CompletableFuture.supplyAsync(cf1::getint1).thenApply(x-> cf1.add2(x));
        CompletableFuture<Integer> cl2 = cl.thenApply(cf1::add2);
        CompletableFuture<Void> cf3 = cl2.thenAccept(System.out::println);

        System.out.println(cf3.isDone());

        sleepnseconds(4);

        System.out.println(cf3.isDone());*/

        CompletableFuture<List<Integer>> f1 = CompletableFuture.supplyAsync(cf1::oddnumbers);
        CompletableFuture<List<Integer>> f2 = CompletableFuture.supplyAsync(cf1::evennumbers);

        CompletableFuture<Stream<Integer>> f3 = f1.thenCombine(f2,(x,y)->{
            System.out.println("anon");
            System.out.println(Thread.currentThread().getName());
           Stream<Integer> l1 = x.stream().map(t->t*2);
           Stream<Integer> l2 = y.stream().map(t->t*2);
           System.out.println("anon exit");
           return Stream.concat(l1,l2).collect(Collectors.toList());
        }).thenApply(x->x.stream().map(t->t*2));

        f3.thenAccept(x->x.forEach(y->System.out.println(y)));

        sleepnseconds(10);

        //CompletableFuture.allOf(f1,f2).thenAccept(x->x*2);

    }

    public static List<Integer > oddnumbers(){
        System.out.println(Thread.currentThread().getName());
        System.out.println("odd enter");
        sleepnseconds(4);
        List<Integer> l = new ArrayList<Integer>();

        l.add(1);

        sleepnseconds(4);

        l.add(3);

        System.out.println("odd exit");

        return l;

    }

    public static List<Integer > evennumbers(){
        System.out.println(Thread.currentThread().getName());
        System.out.println("even enter");
        sleepnseconds(2);
        List<Integer> l = new ArrayList<Integer>();

        l.add(2);

        l.add(4);
        l.add(6);

        System.out.println("even exit");
        return l;

    }



    public static void sleepnseconds(int n){
        try {
            Thread.sleep(n*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  static int getint1(){
        sleepnseconds(3);
        return 2;
    }

    public static int add2(int x){
        return x+2;
    }


}
