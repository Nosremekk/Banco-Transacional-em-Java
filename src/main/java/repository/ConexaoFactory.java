package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoFactory {
    private static final String URL = "jdbc:sqlite:banco.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        // O SQLite mantém chaves estrangeiras desligadas por padrão; é fundamental ativá-las:
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void inicializarBanco() {
        String sqlContas = """
            CREATE TABLE IF NOT EXISTS contas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titular TEXT NOT NULL,
                saldo NUMERIC NOT NULL CHECK (saldo >= 0)
            );
        """;

        String sqlTransacoes = """
            CREATE TABLE IF NOT EXISTS transacoes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                conta_origem_id INTEGER NOT NULL,
                conta_destino_id INTEGER NOT NULL,
                valor NUMERIC NOT NULL,
                data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (conta_origem_id) REFERENCES contas(id),
                FOREIGN KEY (conta_destino_id) REFERENCES contas(id)
            );
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlContas);
            stmt.execute(sqlTransacoes);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar o esquema da base de dados: " + e.getMessage(), e);
        }
    }
}