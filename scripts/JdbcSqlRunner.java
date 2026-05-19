import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcSqlRunner {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: JdbcSqlRunner <sql-file>");
            System.exit(1);
        }

        String url = requiredEnv("SUPABASE_DB_URL");
        String username = requiredEnv("SUPABASE_DB_USERNAME");
        String password = requiredEnv("SUPABASE_DB_PASSWORD");
        String sql = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);

        Class.forName("org.postgresql.Driver");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement()) {
                for (String sqlStatement : splitStatements(sql)) {
                    String trimmed = sqlStatement.trim();
                    if (!trimmed.isEmpty()) {
                        statement.execute(trimmed);
                    }
                }
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }

            connection.commit();
            printCounts(connection);
            printPasswordHashStats(connection);
        }
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " is required.");
        }

        return value;
    }

    private static List<String> splitStatements(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;

        for (int index = 0; index < sql.length(); index++) {
            char currentChar = sql.charAt(index);
            current.append(currentChar);

            if (currentChar == '\'') {
                boolean escapedQuote = inSingleQuote
                        && index + 1 < sql.length()
                        && sql.charAt(index + 1) == '\'';
                if (escapedQuote) {
                    current.append(sql.charAt(++index));
                } else {
                    inSingleQuote = !inSingleQuote;
                }
            } else if (currentChar == ';' && !inSingleQuote) {
                statements.add(current.substring(0, current.length() - 1));
                current.setLength(0);
            }
        }

        String tail = current.toString().trim();
        if (!tail.isEmpty()) {
            statements.add(tail);
        }

        return statements;
    }

    private static void printCounts(Connection connection) throws SQLException {
        String[] tables = {
                "game",
                "user",
                "group",
                "game_user",
                "post",
                "comment",
                "friendship",
                "friendly_match",
                "friendly_match_participation",
                "friendly_match_request",
                "friendly_match_request_participation"
        };

        try (Statement statement = connection.createStatement()) {
            for (String table : tables) {
                try (ResultSet resultSet = statement.executeQuery("select count(*) from \"" + table + "\"")) {
                    if (resultSet.next()) {
                        System.out.println("[count] " + table + "=" + resultSet.getLong(1));
                    }
                }
            }
        }
    }

    private static void printPasswordHashStats(Connection connection) throws SQLException {
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(
                        "select count(*), count(distinct \"login_password\") from \"user\""
                )
        ) {
            if (resultSet.next()) {
                System.out.println("[count] user_password_hash_total=" + resultSet.getLong(1));
                System.out.println("[count] user_password_hash_distinct=" + resultSet.getLong(2));
            }
        }
    }
}
