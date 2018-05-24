package com.github.jojoldu.sqs.annotation.test;

import com.github.jojoldu.sqs.exception.SqsMockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.github.jojoldu.sqs.config.SqsProperties.DEFAULT_PORT;

/**
 * Created by jojoldu@gmail.com on 2018. 5. 21.
 * Blog : http://jojoldu.tistory.com
 * Github : https://github.com/jojoldu
 */

@Slf4j
public class SqsMockUtils {

    public static int getOrCreatePort(String port) {
        if (StringUtils.isEmpty(port)) {
            return DEFAULT_PORT;
        } else if ("random".equals(port)) {
            return findAvailablePort();
        } else {
            log.info(">>>>>>>>>> " +
                    "");
            return Integer.parseInt(port);
        }
    }

    public static int findAvailablePort() {
        for (int port = 10000; port <= 65535; port++) {
            try {
                if(!isRunning(String.valueOf(port))){
                    return port;
                }
            } catch (IOException ex) {
            }
        }

        String message = "Not Found Available port: 10000 ~ 60000";
        log.error(message);
        throw new SqsMockException(message);
    }

    private static boolean isRunning(String port) throws IOException{
        String line;
        StringBuilder pidInfo = new StringBuilder();
        Process p = executeGrepProcessCommand(port);

        try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {

            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }

        } catch (Exception e) {
        }

        return !StringUtils.isEmpty(pidInfo.toString());
    }

    private static Process executeGrepProcessCommand(String port) throws IOException {
        String command = String.format("netstat -nat | grep LISTEN|grep %s", port);
        String[] shell = {"/bin/sh", "-c", command};
        return Runtime.getRuntime().exec(shell);
    }
}