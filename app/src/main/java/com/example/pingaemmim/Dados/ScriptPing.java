package com.example.pingaemmim.Dados;

public class ScriptPing {

    public static String getCreateTable(){
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE IF NOT EXISTS PING ( ");
        sql.append("IP VARCHAR (20) NOT NULL DEFAULT(''))");

        return sql.toString();
    }
}
