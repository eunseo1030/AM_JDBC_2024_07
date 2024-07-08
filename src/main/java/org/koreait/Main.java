package org.koreait;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("== 프로그램 실행 ==");

        int lastId = 0;
        List<Article> articles = new ArrayList<>();

        while (true){

            System.out.print("명령어:");
            String cmd = sc.nextLine().trim();

            if(cmd.length() == 0){
                System.out.println("명령어를 입력해주세요");

            }
            if(cmd.equals("exit")){
                break;
            }
            if(cmd.equals("article write")){
                System.out.println("== 게시글 작성 ==");


                int id = lastId +1;
                System.out.print("제목:");
                String title = sc.nextLine();
                System.out.print("내용:");
                String body = sc.nextLine();


                Article article = new Article(id, title, body);
                articles.add(article);

                System.out.println(id +"번 게시글이 작성되었습니다");
                lastId++;




            }if(cmd.equals("article list")){
                System.out.println("== 게시글 목록==");

                if(articles.size() == 0){
                    System.out.println("등록된 게시글이 없습니다");
                }else {
                    System.out.println("번호   /   제목   /   내용 ");
                    for(int i = articles.size()-1; i >= 0; i--){
                        Article article = articles.get(i);
                        System.out.printf("%d   /   %s    /   %s\n",article.getId(),article.getTitle(),article.getBody());
                    }

                }


            }


        }System.out.println("프로그램 종료되었습니다");
        sc.close();



    }
}
