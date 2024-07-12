package org.koreait;

import org.koreait.articleManager.Container;
import org.koreait.util.DBUtil;
import org.koreait.util.SecSql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
    public void run() {
        System.out.println("==프로그램 시작==");

        while (true) {
            System.out.print("명령어 > ");
            String cmd = Container.getSc().nextLine().trim();
            Connection conn = null;
            try {
                Class.forName("org.mariadb.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String url = "jdbc:mariadb://127.0.0.1:3306/AM_JDBC_2024_07?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul";
            try {
                conn = DriverManager.getConnection(url, "root", "");
                int actionResult = doAction(conn, Container.getSc(), cmd);
                if (actionResult == -1) {
                    System.out.println("==프로그램 종료==");
                    break;
                }
            } catch (SQLException e) {
                System.out.println("에러 1 : " + e);
            } finally {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private int doAction(Connection conn, Scanner sc, String cmd) {
        if (cmd.equals("exit")) {
            return -1;
        }
        if (cmd.equals("member join")) {
            String loginId = null;
            String loginPw = null;
            String loginPwConfirm = null;
            String name = null;

            System.out.println("==회원가입==");
            while (true) {
                System.out.print("로그인 아이디 : ");
                loginId = sc.nextLine().trim();

                if (loginId.length() == 0 || loginId.contains(" ")) {
                    System.out.println("아이디 똑바로 써");
                    continue;
                }

                SecSql sql = new SecSql();

                sql.append("SELECT COUNT(*) > 0");
                sql.append("FROM `member`");
                sql.append("WHERE loginId = ?;", loginId);

                boolean isLoindIdDup = DBUtil.selectRowBooleanValue(conn, sql);

                if (isLoindIdDup) {
                    System.out.println(loginId + "는(은) 이미 사용중");
                    continue;
                }
                break;

            }
            while (true) {
                System.out.print("비밀번호 : ");
                loginPw = sc.nextLine().trim();

                if (loginPw.length() == 0 || loginPw.contains(" ")) {
                    System.out.println("비번 똑바로 입력해");
                    continue;
                }

                boolean loginPwCheck = true;

                while (true) {
                    System.out.print("비밀번호 확인 : ");
                    loginPwConfirm = sc.nextLine().trim();

                    if (loginPwConfirm.length() == 0 || loginPwConfirm.contains(" ")) {
                        System.out.println("비번 확인 똑바로 써");
                        continue;
                    }
                    if (loginPw.equals(loginPwConfirm) == false) {
                        System.out.println("일치하지 않아");
                        loginPwCheck = false;
                    }
                    break;
                }
                if (loginPwCheck) {
                    break;
                }
            }

            while (true) {
                System.out.print("이름 : ");
                name = sc.nextLine();

                if (name.length() == 0 || name.contains(" ")) {
                    System.out.println("이름 똑바로 써");
                    continue;
                }
                break;
            }


            SecSql sql = new SecSql();

            sql.append("INSERT INTO `member`");
            sql.append("SET regDate = NOW(),");
            sql.append("updateDate = NOW(),");
            sql.append("loginId = ?,", loginId);
            sql.append("loginPw= ?,", loginPw);
            sql.append("name = ?;", name);

            int id = DBUtil.insert(conn, sql);

            System.out.println(id + "번 회원이 생성되었습니다");


        } else if (cmd.equals("article write")) {
            System.out.println("==글쓰기==");
            System.out.print("제목 : ");
            String title = sc.nextLine();
            System.out.print("내용 : ");
            String body = sc.nextLine();

            SecSql sql = new SecSql();

            sql.append("INSERT INTO article");
            sql.append("SET regDate = NOW(),");
            sql.append("updateDate = NOW(),");
            sql.append("title = ?,", title);
            sql.append("`body`= ?;", body);

            int id = DBUtil.insert(conn, sql);

            System.out.println(id + "번 글이 생성되었습니다");


        } else if (cmd.equals("article list")) {
            System.out.println("==목록==");

            List<Article> articles = new ArrayList<>();

            SecSql sql = new SecSql();
            sql.append("SELECT *");
            sql.append("FROM article");
            sql.append("ORDER BY id DESC");

            List<Map<String, Object>> articleListMap = DBUtil.selectRows(conn, sql);

            for (Map<String, Object> articleMap : articleListMap) {
                articles.add(new Article(articleMap));
            }

            if (articles.size() == 0) {
                System.out.println("게시글이 없습니다");
                return 0;
            }
            System.out.println("  번호  /   제목  ");
            for (Article article : articles) {
                System.out.printf("  %d     /   %s   \n", article.getId(), article.getTitle());
            }
        } else if (cmd.startsWith("article modify")) {
            int id = 0;
            try {
                id = Integer.parseInt(cmd.split(" ")[2]);
            } catch (Exception e) {
                System.out.println("번호는 정수로 입력해");
                return 0;
            }
            System.out.println("==수정==");
            System.out.print("새 제목 : ");
            String title = sc.nextLine().trim();
            System.out.print("새 내용 : ");
            String body = sc.nextLine().trim();
            PreparedStatement pstmt = null;
            try {
                String sql = "UPDATE article";
                sql += " SET updateDate = NOW()";
                if (title.length() > 0) {
                    sql += " ,title = '" + title + "'";
                }
                if (body.length() > 0) {
                    sql += " ,`body` = '" + body + "'";
                }
                sql += " WHERE id = " + id + ";";
                System.out.println(sql);
                pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("에러 4 : " + e);
            } finally {
                try {
                    if (pstmt != null && !pstmt.isClosed()) {
                        pstmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(id + "번 글이 수정되었습니다.");
        }
        return 0;
    }
}
