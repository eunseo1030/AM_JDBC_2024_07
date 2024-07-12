package org.koreait.articleManager;


import org.koreait.App;

public class Main {
    public static void main(String[] args) {

        Container.init();

        new App().run();

        Container.outit();

    }
}