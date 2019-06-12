package com.config.butler;

import java.io.IOException;
import java.net.*;

public class Local {
    static String loggedInUser, hostName, userPath;

    public Local() {

        loggedInUser = System.getProperty("user.name");

        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        userPath = System.getProperty("user.home");
    }

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String loggedInUser) {
        Local.loggedInUser = loggedInUser;
    }

    public static String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        Local.hostName = hostName;
    }

    public static String getUserPath() {
        return userPath;
    }

    public void setUserPath(String userPath) {
        Local.userPath = userPath;
    }

    public static boolean isNetAvailable() {

        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

}