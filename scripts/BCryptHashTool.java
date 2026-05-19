import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptHashTool {
    public static void main(String[] args) throws Exception {
        int strength = args.length > 0 ? Integer.parseInt(args[0]) : 6;
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(strength);

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8)
                );
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(System.out, StandardCharsets.UTF_8)
                )
        ) {
            String password;
            while ((password = reader.readLine()) != null) {
                writer.write(passwordEncoder.encode(password));
                writer.newLine();
            }
        }
    }
}
